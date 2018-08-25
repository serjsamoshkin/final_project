package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Reception;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import util.dto.reception.ShowUserReceptionsInDto;
import util.dto.reception.ShowUserReceptionsOutDto;
import util.properties.PaginationPropertiesReader;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ShowUserReceptionsService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ShowUserReceptionsService.class);

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    public ShowUserReceptionsService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowUserReceptionsOutDto processShowUserReceptionRequest(ShowUserReceptionsInDto inDto){

        ShowUserReceptionsOutDto.ShowUserReceptionsOutDtoBuilder builder = ShowUserReceptionsOutDto.getBuilder();

        User user;
        if (inDto.getUser().isPresent()){
            user = inDto.getUser().get();
        }else {
            logger.error("No user parameter passed to processShowUserReceptionRequest");
            return builder.buildFalse();
        }

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
        int pageCount;
        try (Connection connection = getDataSource().getConnection()){
            int receptionCount = dao.getUserReceptionsCount(user, connection);

            int rowsForPage = Integer.valueOf(PaginationPropertiesReader.getInstance()
                    .getPropertyValue("user_reception_count"));

            pageCount = receptionCount / rowsForPage + receptionCount % rowsForPage == 0 ? 1 : 2;
            if (page > pageCount) {
                page = 1;
            }
            receptions = dao.getUserReceptions(user, connection, page);
        }catch (SQLException e){
            throw new PersistException(e);
        }

        builder.setPage(page);
        builder.setPageCount(pageCount);
        builder.setReceptions(receptions.stream().map(ReceptionView::of).collect(Collectors.toList()));

        return builder.build();
    }



}


