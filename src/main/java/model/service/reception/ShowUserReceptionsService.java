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

    public List<ReceptionView> processShowUserReceptionRequest(User user){

        ReceptionDAO dao = DaoMapper.getMapper().getDao(ReceptionDAO.class);

        List<Reception> receptions;
        try (Connection connection = getDataSource().getConnection()){
            receptions = dao.getUserReceptions(user, connection);
            dao.getUserReceptionsCount(user, connection);

        }catch (SQLException e){
            throw new PersistException(e);
        }


        return receptions.stream().map(ReceptionView::of).collect(Collectors.toList());
    }



}


