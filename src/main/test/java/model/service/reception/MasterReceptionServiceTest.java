package model.service.reception;

import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;
import util.dto.reception.ShowMasterScheduleInDto;
import util.dto.reception.ShowMasterScheduleOutDto;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MasterReceptionServiceTest {

    @Test
    public void testService() throws Exception{

        final DataSource dataSource = Mockito.mock(DataSource.class);
        final Connection connection = Mockito.mock(Connection.class);
        final ReceptionDAO dao = Mockito.mock(ReceptionDAO.class);

        final MasterReceptionService service = Mockito.spy(new MasterReceptionService(dataSource));

        final LocalDate now = LocalDate.now();
        final Master master = new Master();
        final User user = new User();

        final Reception reception = new Reception();

        reception.setId(0);
        reception.setVersion(0);
        reception.setUser(user);
        reception.setMaster(master);
        reception.setTime(LocalDateTimeFormatter.toSqlTime(TimePlanning.startOfDay(now)));
        reception.setDay(LocalDateTimeFormatter.toSqlDate(now));
        reception.setEndTime(LocalDateTimeFormatter.toSqlTime(TimePlanning.plusDuration(TimePlanning.startOfDay(now), 1)));
        reception.setStatus(Reception.Status.NEW);

        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        Mockito.doReturn(Optional.of(master)).when(service).getMasterByUser(Mockito.any(User.class));
        Mockito.doReturn(List.of(reception)).when(service).getMastersReceptions(Mockito.any(LocalDate.class), Mockito.any(Master.class), Mockito.any(Connection.class));

        Mockito.when(dao.getMastersReceptions(now, List.of(master), connection)).thenReturn(List.of());

        ShowMasterScheduleOutDto dto = service.getDailyMasterSchedule(ShowMasterScheduleInDto.getBuilder().setDate(LocalDate.now()).setUser(new User()).build());

        Map<String, ReceptionView> schedule = dto.getSchedule();

        Assert.assertFalse("Empty schedule",
                schedule.isEmpty());
        Assert.assertTrue("reception is not on first position",
                schedule.values().stream().findFirst().get().equals(ReceptionView.of(reception)));

        Assert.assertTrue("size of schedule doesn't match with intervals in day schedule",
                schedule.size() == TimePlanning.betweenDuration(TimePlanning.startOfDay(now), TimePlanning.endOfDay(now)));
        Assert.assertTrue("reception occurs several times",
                schedule.values().stream().filter(v -> v.equals(ReceptionView.of(reception))).count() == 1);


    }

}
