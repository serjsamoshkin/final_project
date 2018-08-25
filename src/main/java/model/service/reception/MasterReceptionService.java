package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.MasterDAO;
import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.service.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;
import util.dto.reception.showMasterSchedule.ShowMasterScheduleInDto;
import util.dto.reception.showMasterSchedule.ShowMasterScheduleOutDto;
import util.dto.reception.changeReception.ChangeReceptionInDto;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MasterReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(MasterReceptionService.class);

    public MasterReceptionService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowMasterScheduleOutDto getDailyMasterSchedule(ShowMasterScheduleInDto inDto){

        ShowMasterScheduleOutDto.ShowMasterScheduleOutDtoBuilder builder = ShowMasterScheduleOutDto.getBuilder();

        if (!inDto.getUser().isPresent() || !inDto.getDate().isPresent()){
            logger.error("No value in inDto.getUser()or inDto.getDate() in getDailyMasterSchedule of MasterReceptionService");
            return builder.buildFalse();
        }

        Optional<Master> masterOpt = getMasterByUser(inDto.getUser().get());
        if (!masterOpt.isPresent()){
            logger.error("No master with User id: " + inDto.getUser());
            return ShowMasterScheduleOutDto.getBuilder().buildFalse();
        }

        List<Reception> receptions;
        try (Connection connection = getDataSource().getConnection()){
            receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class)
                    .getMastersReceptions(inDto.getDate().get(), List.of(masterOpt.get()), connection);
        }catch (SQLException e){
            logger.error(e);
            throw new PersistException(e);
        }

        Map<String, ReceptionView> dailyScheme = TimePlanning.getDailyScheme(inDto.getDate().get()).stream()
                .map(LocalDateTimeFormatter::toString)
                .collect(Collectors.toMap(Function.identity(), t ->
                                ReceptionView.of(receptions.stream().filter(r -> LocalDateTimeFormatter.toString(r.getTime())
                                        .equals(t)).findFirst().orElse(null)),
                        MasterReceptionService::throwIllegalStateException,
                        LinkedHashMap::new));

        builder.setSchedule(dailyScheme);

        return builder.build();

    }

    public boolean changeReception(ChangeReceptionInDto inDto){

        // TODO пробросить пользователя до ошибки

        Reception.Status status;
        if (inDto.getStatus().isPresent()){
            try {
                status = Reception.Status.valueOf(inDto.getStatus().get());
            }catch (Exception e){
                logger.error("Incorrect status value in ChangeReceptionInDto in MasterReceptionService. value: " + inDto.getStatus());
                return false;
            }

        }else {
            logger.error("No status value in ChangeReceptionInDto in MasterReceptionService");
            return false;
        }

        Reception reception;
        if (inDto.getId().isPresent()){
            try (Connection connection = getDataSource().getConnection()){
                reception = DaoMapper.getMapper().getDao(ReceptionDAO.class).getByPK(Integer.valueOf(inDto.getId().get()), connection);
            }catch (SQLException e){
                logger.error(e);
                return false;
            }catch (NumberFormatException e){
                logger.error("String to int converting error. Value is: " + inDto.getId().get());
                return false;
            }
        }else {
            logger.error("No id value in ChangeReceptionInDto in MasterReceptionService");
            return false;
        }

        if (!LocalDateTimeFormatter.toLocalDate(reception.getDay()).isEqual(LocalDate.now())){
            logger.error("Tried to change reception in another day. reception: " + reception);
            return false;
        }

        int version;
        if (inDto.getVersion().isPresent()){
            try {
                version = Integer.valueOf(inDto.getVersion().get());
            }catch (NumberFormatException e){
                logger.error("String to int converting error. Value is: " + inDto.getVersion().get());
                return false;
            }

        } else {
            logger.error("No version value in ChangeReceptionInDto in MasterReceptionService");
            return false;
        }


        try (Connection connection = getDataSource().getConnection()) {
            DaoMapper.getMapper().getDao(ReceptionDAO.class).safeUpdate(reception, status, version, connection);
        } catch (SQLException e) {
            logger.error(e);
        } catch (ConcurrentModificationException e) {
            logger.error(e);
            return false;
        }

        return true;
    }

    private Optional<Master> getMasterByUser(User user){

        try (Connection connection = getDataSource().getConnection()){
            return Optional.ofNullable(DaoMapper.getMapper().getDao(MasterDAO.class).getMasterByUserId(user.getId(), connection));
        }catch (SQLException e){
            logger.error(e);
            throw new PersistException(e);
        }

    }

    private static ReceptionView throwIllegalStateException(ReceptionView u, ReceptionView v) {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
    }


}
