package model.service.review;

import model.dao.DaoMapper;
import model.dao.reception.ReviewDAO;
import model.entity.reception.Reception;
import model.entity.reception.Review;
import model.service.AbstractService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.RowNotUniqueException;
import util.dto.review.OpenReviewOutDto;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReviewService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ReviewService.class);

    public ReviewService(DataSource dataSource) {
        super(dataSource);
    }

    public boolean createReview(Reception reception, Connection connection) {

        Review review = new Review();
        review.setReception(reception);
        review.setToken(DigestUtils.md5Hex(reception.getId() + "DFd33sd!"));
        review.setStatus(Review.Status.NEW);
        try {
            DaoMapper.getMapper().getDao(ReviewDAO.class).save(review, connection);
        } catch (RowNotUniqueException e) {
            logger.error(e);
            return false;
        }

        return true;
    }

    public OpenReviewOutDto getReviewByToken(String token) {

        OpenReviewOutDto.OpenReviewOutDtoBuilder builder = OpenReviewOutDto.getBuilder();

        try (Connection connection = getDataSource().getConnection()) {
            Optional<Review> reviewOpt = DaoMapper.getMapper().getDao(ReviewDAO.class).getReviewByToken(token, connection);
            if (reviewOpt.isPresent()) {
                builder.setReviewId(reviewOpt.get().getId());
                builder.setReceptionView(ReceptionView.of(reviewOpt.get().getReception()));
            } else {
                logger.error("No review with token: " + token);
                return builder.buildFalse();
            }

        } catch (SQLException e) {
            logger.error(e);
            return builder.buildFalse();
        }

        return builder.build();
    }

    public boolean setReviewCommentById(int id, String text) {

        ReviewDAO dao = DaoMapper.getMapper().getDao(ReviewDAO.class);
        try (Connection connection = getDataSource().getConnection()) {
            try {
                connection.setAutoCommit(false);
                Optional<Review> reviewOpt = dao.getReviewWithLock(id, connection);
                if (reviewOpt.isPresent()) {
                    Review review = Review.copyOf(reviewOpt.get());
                    if (review.getStatus() == Review.Status.DONE) {
                        connection.rollback();
                        return false;
                    } else {
                        review.setStatus(Review.Status.DONE);
                        review.setText(text);
                    }
                    dao.update(review, connection);
                } else {
                    logger.error("no review with id: " + id);
                    return false;
                }
                connection.commit();
            } catch (SQLException e) {
                logger.error(e);
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }

        return true;
    }

    public void sendReviews() {

        ReviewDAO dao = DaoMapper.getMapper().getDao(ReviewDAO.class);

        try (Connection connection = getDataSource().getConnection()) {
            try {
                connection.setAutoCommit(false);
                List<Review> reviews = dao.getUnsentReviews(connection);
                // TODO просто заглушка
                reviews.forEach(r -> dao.changeStatus(r, Review.Status.SENT, connection));
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.error(e);
        }

    }


}
