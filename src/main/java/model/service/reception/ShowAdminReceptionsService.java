package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.dao.reception.ReviewDAO;
import model.entity.reception.Reception;
import model.entity.reception.Review;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import util.dto.reception.ShowAdminReceptionsInDto;
import util.dto.reception.ShowAdminReceptionsOutDto;
import util.dto.reception.ShowUserReceptionsOutDto;
import util.properties.PaginationPropertiesReader;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowAdminReceptionsService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ShowAdminReceptionsService.class);

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    public ShowAdminReceptionsService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowAdminReceptionsOutDto processShowReceptionRequest(ShowAdminReceptionsInDto inDto){

        ShowAdminReceptionsOutDto.ShowAdminReceptionsOutDtoBuilder builder = ShowAdminReceptionsOutDto.getBuilder();

        int page = 1;
        if (inDto.getPage().isPresent()){
            try {
                page = Integer.valueOf(inDto.getPage().get());
            }catch (NumberFormatException e){
                logger.error(e);
                return builder.buildFalse();
            }

        }

        ReceptionDAO dao = DaoMapper.getMapper().getDao(ReceptionDAO.class);

        List<Reception> receptions;
        Map<Reception, Review> receptionReviewMap;
        int pageCount;
        try (Connection connection = getDataSource().getConnection()){
            int receptionCount = dao.getReceptionsCount(connection);

            int rowsForPage = Integer.valueOf(PaginationPropertiesReader.getInstance()
                    .getPropertyValue("admin_reception_count"));

            pageCount = receptionCount / rowsForPage + receptionCount % rowsForPage == 0 || receptionCount < rowsForPage ? 1 : 2;
            if (page > pageCount) {
                page = 1;
            }
            receptions = dao.getReceptionsWithLimit(connection, page);
            receptionReviewMap = DaoMapper.getMapper().getDao(ReviewDAO.class).
                    getReviewsByReceptions(receptions, connection).stream().
                    collect(Collectors.toMap(Review::getReception, Function.identity()));

        }catch (SQLException e){
            throw new PersistException(e);
        }

        builder.setPage(page);
        builder.setPageCount(pageCount);
        builder.setReceptions(receptions.stream().map(reception -> ReceptionView.of(reception, receptionReviewMap))
                .collect(Collectors.toList()));

        return builder.build();
    }



}


