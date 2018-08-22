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
import util.dto.reception.ShowMasterSchedule.ShowMasterScheduleInDto;
import util.dto.reception.ShowMasterSchedule.ShowMasterScheduleOutDto;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MasterReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(MasterReceptionService.class);

    public MasterReceptionService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowMasterScheduleOutDto getDailyMasterSchedule(ShowMasterScheduleInDto inDto){

        ShowMasterScheduleOutDto.ShowMasterScheduleOutDtoBuilder builder = ShowMasterScheduleOutDto.getBuilder();

        Optional<Master> masterOpt = getMasterByUser(inDto.getUser());
        if (!masterOpt.isPresent()){
            logger.error("No master with User id: " + inDto.getUser());
            return ShowMasterScheduleOutDto.getBuilder().buildFalse();
        }

        List<Reception> receptions;
        try (Connection connection = getDataSource().getConnection()){
            receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class)
                    .getMastersReceptions(inDto.getDate(), List.of(masterOpt.get()), connection);
        }catch (SQLException e){
            logger.error(e);
            throw new PersistException(e);
        }

        Map<String, ReceptionView> dailyScheme = TimePlanning.getDailyScheme(inDto.getDate()).stream()
                .map(LocalDateTimeFormatter::toString)
                .collect(Collectors.toMap(Function.identity(), t ->
                                ReceptionView.of(receptions.stream().filter(r -> LocalDateTimeFormatter.toString(r.getTime())
                                        .equals(t)).findFirst().orElse(null)),
                        MasterReceptionService::throwIllegalStateException,
                        LinkedHashMap::new));

        builder.setSchedule(dailyScheme);

        return builder.build();

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
