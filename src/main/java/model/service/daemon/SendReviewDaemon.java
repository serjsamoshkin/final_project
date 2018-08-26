package model.service.daemon;

import model.dao.reception.ReviewDAO;
import model.service.ServiceMapper;
import model.service.review.ReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class SendReviewDaemon implements Runnable{

    private final Logger logger = LogManager.getLogger(SendReviewDaemon.class);

    @Override
    public void run() {

        while (true){

            ServiceMapper.getMapper().getService(ReviewService.class).sendReviews();

            try {
                TimeUnit.MINUTES.sleep(5);
            }catch (InterruptedException e){
                logger.error(e);
                break;
            }

        }

    }
}
