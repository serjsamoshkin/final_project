package model.service.reception;

import model.dao.DaoMapper;
import model.dao.GenericDAO;
import model.dao.reception.MasterDAO;
import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import model.service.ServiceMapper;
import model.service.testUtils.TestDataCreator;
import model.service.util.DataCheckerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import persistenceSystem.JDBCDaoController;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;
import util.dto.reception.ShowReceptionInDto;
import util.dto.reception.ShowReceptionOutDto;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ShowReceptionServiceTest {

    private final JDBCDaoController jdbcDaoController = Mockito.mock(JDBCDaoController.class);

    private final DataSource dataSource = Mockito.mock(DataSource.class);
    private final Connection connection = Mockito.mock(Connection.class);

    private final MasterDAO masterDAO = Mockito.spy(new MasterDAO(jdbcDaoController));
    private final ReceptionDAO receptionDAO = Mockito.spy(new ReceptionDAO(jdbcDaoController));

    private ShowReceptionService showReceptionService;
    private DataCheckerService dataChecker ;

    @Before
    public void init() throws Exception{
        DaoMapper.buildMapper(jdbcDaoController);

        ServiceMapper.buildMapper(dataSource);
        Mockito.doReturn(connection).when(dataSource).getConnection();

        Field field = DaoMapper.class.getDeclaredField("daoMap");
        field.setAccessible(true);
        ConcurrentHashMap<Class<? extends GenericDAO>, GenericDAO> daoMap =  new ConcurrentHashMap<>();
        daoMap.put(MasterDAO.class, masterDAO);
        daoMap.put(ReceptionDAO.class, receptionDAO);

        field.set(DaoMapper.getMapper(), daoMap);

        showReceptionService = Mockito.spy(new ShowReceptionService(dataSource));
        dataChecker = Mockito.mock(DataCheckerService.class);
        showReceptionService.dataChecker = dataChecker;

    }

    @Test
    public void testServiceFullDay(){

        int modulator = 60/TimePlanning.getTimeModulator();

        Assert.assertTrue("can't do test for TimeModulator value more then 1 hour (60 minutes)",
                modulator<=1);

        final LocalDate nextDay = LocalDate.now().plusDays(1);
        final User user = TestDataCreator.createTestUser("testUser");
        final Service service = TestDataCreator.createTestService("testService", 2*modulator);
        final Master master = TestDataCreator.createTestMaster("testMaster");
        final Reception reception1 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.startOfDay(nextDay));
        final Reception reception2 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), service.getDuration() + 1));
        final Reception reception3 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.plusDuration(LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()), service.getDuration()));
        Assert.assertFalse("incorrect daily schedule - can't create correct second reception with time plus 3 periods from the start of day",
                LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()).isAfter(TimePlanning.endOfDay(nextDay)));
        Assert.assertFalse("incorrect daily schedule - can't create correct third reception with time plus 5 periods from the start of day",
                LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()).isAfter(TimePlanning.endOfDay(nextDay)));


        // checker's mock
        Mockito.doReturn(Optional.of(nextDay)).when(dataChecker).checkDate(Mockito.any(), Mockito.any());
        Mockito.doReturn(Optional.of(service)).when(dataChecker).checkService(Mockito.any(), Mockito.any());

        // inner dao result mock
        Mockito.doReturn(List.of(master)).when(masterDAO).getMasterListByService(Mockito.any(), Mockito.any());
        Mockito.doReturn(new ArrayList(List.of(reception1, reception2, reception3))).when(receptionDAO).getMastersReceptions(Mockito.any(), Mockito.any(), Mockito.any());


        ShowReceptionOutDto resultDto = showReceptionService.processShowReceptionRequest(ShowReceptionInDto.getBuilder().buildFalse());

        Map<String, Boolean> masterSchedule = resultDto.getMastersSchedule().get(master);
        Assert.assertFalse("No master schedule in result dto", masterSchedule.isEmpty());

        int durationBetween1_2 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
                LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()));
        int durationBetween1_3 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
                LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()));

            Assert.assertTrue("FULL: end of third reception is not end of day",
                    LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()).equals(TimePlanning.endOfDay(nextDay)));

            Assert.assertTrue("FULL: duration between 1 and 2 receptions must be 5", durationBetween1_2 == 5);
            Assert.assertTrue("FULL: duration between 1 and 3 receptions must be 9", durationBetween1_3 == 9);

            Set<String> set1_2 = new HashSet<>();
            for (int i = 0; i < durationBetween1_2; i++) {
                set1_2.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
            }
            Assert.assertTrue("FULL: contains available position between 1 and 2 receptions.",
                    masterSchedule.entrySet().stream().filter(e -> set1_2.contains(e.getKey())).allMatch(Map.Entry::getValue));

            Set<String> set1_3 = new HashSet<>();
            for (int i = 0; i < durationBetween1_3; i++) {
                set1_3.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
            }

            Assert.assertTrue("FULL: contains not one available position between 1 and 3 receptions.",
                    masterSchedule.entrySet().stream().filter(e -> set1_3.contains(e.getKey())).filter(e -> !e.getValue()).count() == 1);

    }

    @Test
    public void testServicePartOfDay(){
        int modulator = 60/TimePlanning.getTimeModulator();

        Assert.assertTrue("can't do test for TimeModulator value more then 1 hour (60 minutes)",
                modulator<=1);

        final LocalDate nextDay = LocalDate.now().plusDays(1);
        final User user = TestDataCreator.createTestUser("testUser");
        final Service service = TestDataCreator.createTestService("testService", modulator);
        final Master master = TestDataCreator.createTestMaster("testMaster");
        final Reception reception1 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.startOfDay(nextDay));
        final Reception reception2 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), service.getDuration() + 1));
        final Reception reception3 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.plusDuration(LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()), service.getDuration()));


        // checker's mock
        Mockito.doReturn(Optional.of(nextDay)).when(dataChecker).checkDate(Mockito.any(), Mockito.any());
        Mockito.doReturn(Optional.of(service)).when(dataChecker).checkService(Mockito.any(), Mockito.any());

        // inner dao result mock
        Mockito.doReturn(List.of(master)).when(masterDAO).getMasterListByService(Mockito.any(), Mockito.any());

        Mockito.doReturn(new ArrayList(List.of(reception1, reception2, reception3))).when(receptionDAO).getMastersReceptions(Mockito.any(), Mockito.any(), Mockito.any());


        ShowReceptionOutDto resultDto = showReceptionService.processShowReceptionRequest(ShowReceptionInDto.getBuilder().buildFalse());

        Map<String, Boolean> masterSchedule = resultDto.getMastersSchedule().get(master);
        Assert.assertFalse("No master schedule in result dto", masterSchedule.isEmpty());

        int durationBetween1_2 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
                LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()));
        int durationBetween1_3 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
                LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()));

        Assert.assertTrue("PARTIALLY: duration between 1 and 2 receptions must be 3", durationBetween1_2 == 3);
        Assert.assertTrue("PARTIALLY: duration between 1 and 3 receptions must be 5", durationBetween1_3 == 5);

        Set<String> set1_2 = new HashSet<>();
        for (int i = 0; i < durationBetween1_2; i++) {
            set1_2.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
        }
        Assert.assertTrue("PARTIALLY: contains available position between 1 and 2 receptions.",
                masterSchedule.entrySet().stream().filter(e -> set1_2.contains(e.getKey())).filter(e -> !e.getValue()).count() == 1);

        Set<String> set1_3 = new HashSet<>();
        for (int i = 0; i < durationBetween1_3; i++) {
            set1_3.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
        }

        Assert.assertTrue("PARTIALLY: contains not one available position between 1 and 3 receptions.",
                masterSchedule.entrySet().stream().filter(e -> set1_3.contains(e.getKey())).filter(e -> !e.getValue()).count() == 2);

        int dailyDuration = TimePlanning.betweenDuration(TimePlanning.startOfDay(nextDay),
                TimePlanning.endOfDay(nextDay));

        Set<String> set3_e = new HashSet<>();
        for (int i = durationBetween1_3 + 1; i <= dailyDuration; i++) {
            set3_e.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
        }

        Assert.assertTrue("PARTIALLY: has lock reception between 3 reception and end of day.",
                masterSchedule.entrySet().stream().filter(e -> set3_e.contains(e.getKey())).noneMatch(Map.Entry::getValue));

    }

    @Test
    public void testServiceEndOfDay(){

        int modulator = 60/TimePlanning.getTimeModulator();

        Assert.assertTrue("can't do test for TimeModulator value more then 1 hour (60 minutes)",
                modulator<=1);

        final LocalDate nextDay = LocalDate.now().plusDays(1);
        final User user = TestDataCreator.createTestUser("testUser");
        final Service service = TestDataCreator.createTestService("testService", 2*modulator);
        final Master master = TestDataCreator.createTestMaster("testMaster");
        final Reception reception1 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.startOfDay(nextDay));
        final Reception reception2 = TestDataCreator.createTestReception(user, master, service,
                TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), service.getDuration() + 1));

        // checker's mock
        Mockito.doReturn(Optional.of(nextDay)).when(dataChecker).checkDate(Mockito.any(), Mockito.any());
        Mockito.doReturn(Optional.of(service)).when(dataChecker).checkService(Mockito.any(), Mockito.any());

        // inner dao result mock
        Mockito.doReturn(List.of(master)).when(masterDAO).getMasterListByService(Mockito.any(), Mockito.any());
        Mockito.doReturn(new ArrayList(List.of(reception1, reception2))).when(receptionDAO).getMastersReceptions(Mockito.any(), Mockito.any(), Mockito.any());

        ShowReceptionOutDto resultDto = showReceptionService.processShowReceptionRequest(ShowReceptionInDto.getBuilder().buildFalse());

        Map<String, Boolean> masterSchedule = resultDto.getMastersSchedule().get(master);
        Assert.assertFalse("No master schedule in result dto", masterSchedule.isEmpty());

        Assert.assertTrue("END_OF_DAY: time at the position before the end of the day is not locked.",
                masterSchedule.entrySet().stream().filter(e -> LocalDateTimeFormatter.toLocalTime(e.getKey())
                        .equals(TimePlanning.minusDuration(TimePlanning.endOfDay(nextDay), 1))).filter(Map.Entry::getValue).count() == 1);


    }

//    public void testServiceWithDuration(Type type){
//
//        int modulator = 60/TimePlanning.getTimeModulator();
//
//        Assert.assertTrue("can't do test for TimeModulator value more then 1 hour (60 minutes)",
//                modulator<=1);
//
//        final LocalDate nextDay = LocalDate.now().plusDays(1);
//        final User user = TestDataCreator.createTestUser("testUser");
//        final Service service = TestDataCreator.createTestService("testService", type.i*modulator);
//        final Master master = TestDataCreator.createTestMaster("testMaster");
//        final Reception reception1 = TestDataCreator.createTestReception(user, master, service,
//                TimePlanning.startOfDay(nextDay));
//        final Reception reception2 = TestDataCreator.createTestReception(user, master, service,
//                TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), service.getDuration() + 1));
//        final Reception reception3 = TestDataCreator.createTestReception(user, master, service,
//                TimePlanning.plusDuration(LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()), service.getDuration()));
//        Assert.assertFalse("incorrect daily schedule - can't create correct second reception with time plus 3 periods from the start of day",
//                LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()).isAfter(TimePlanning.endOfDay(nextDay)));
//        Assert.assertFalse("incorrect daily schedule - can't create correct third reception with time plus 5 periods from the start of day",
//                LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()).isAfter(TimePlanning.endOfDay(nextDay)));
//
//
//        // checker's mock
//        Mockito.doReturn(Optional.of(nextDay)).when(dataChecker).checkDate(Mockito.any(), Mockito.any());
//        Mockito.doReturn(Optional.of(service)).when(dataChecker).checkService(Mockito.any(), Mockito.any());
//
//        // inner dao result mock
//        Mockito.doReturn(List.of(master)).when(masterDAO).getMasterListByService(Mockito.any(), Mockito.any());
//        if (type == Type.END_OF_DAY){
//            Mockito.doReturn(new ArrayList(List.of(reception1, reception2))).when(receptionDAO).getMastersReceptions(Mockito.any(), Mockito.any(), Mockito.any());
//        }else {
//            Mockito.doReturn(new ArrayList(List.of(reception1, reception2, reception3))).when(receptionDAO).getMastersReceptions(Mockito.any(), Mockito.any(), Mockito.any());
//        }
//
//        ShowReceptionOutDto resultDto = showReceptionService.processShowReceptionRequest(ShowReceptionInDto.getBuilder().buildFalse());
//
//        Map<String, Boolean> masterSchedule = resultDto.getMastersSchedule().get(master);
//        Assert.assertFalse("No master schedule in result dto", masterSchedule.isEmpty());
//
//        int durationBetween1_2 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
//                LocalDateTimeFormatter.toLocalTime(reception2.getEndTime()));
//        int durationBetween1_3 = TimePlanning.betweenDuration(LocalDateTimeFormatter.toLocalTime(reception1.getTime()),
//                LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()));
//
//        if (type == Type.FULL) {
//
//            Assert.assertTrue("FULL: end of third reception is not end of day",
//                    LocalDateTimeFormatter.toLocalTime(reception3.getEndTime()).equals(TimePlanning.endOfDay(nextDay)));
//
//            Assert.assertTrue("FULL: duration between 1 and 2 receptions must be 5", durationBetween1_2 == 5);
//            Assert.assertTrue("FULL: duration between 1 and 3 receptions must be 9", durationBetween1_3 == 9);
//
//            Set<String> set1_2 = new HashSet<>();
//            for (int i = 0; i < durationBetween1_2; i++) {
//                set1_2.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
//            }
//            Assert.assertTrue("FULL: contains available position between 1 and 2 receptions.",
//                    masterSchedule.entrySet().stream().filter(e -> set1_2.contains(e.getKey())).allMatch(Map.Entry::getValue));
//
//            Set<String> set1_3 = new HashSet<>();
//            for (int i = 0; i < durationBetween1_3; i++) {
//                set1_3.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
//            }
//
//            Assert.assertTrue("FULL: contains not one available position between 1 and 3 receptions.",
//                    masterSchedule.entrySet().stream().filter(e -> set1_3.contains(e.getKey())).filter(e -> !e.getValue()).count() == 1);
//        }else if (type == Type.PARTIALLY){
//            Assert.assertTrue("PARTIALLY: duration between 1 and 2 receptions must be 3", durationBetween1_2 == 3);
//            Assert.assertTrue("PARTIALLY: duration between 1 and 3 receptions must be 5", durationBetween1_3 == 5);
//
//            Set<String> set1_2 = new HashSet<>();
//            for (int i = 0; i < durationBetween1_2; i++) {
//                set1_2.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
//            }
//            Assert.assertTrue("PARTIALLY: contains available position between 1 and 2 receptions.",
//                    masterSchedule.entrySet().stream().filter(e -> set1_2.contains(e.getKey())).filter(e -> !e.getValue()).count() == 1);
//
//            Set<String> set1_3 = new HashSet<>();
//            for (int i = 0; i < durationBetween1_3; i++) {
//                set1_3.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
//            }
//
//            Assert.assertTrue("PARTIALLY: contains not one available position between 1 and 3 receptions.",
//                    masterSchedule.entrySet().stream().filter(e -> set1_3.contains(e.getKey())).filter(e -> !e.getValue()).count() == 2);
//
//            int dailyDuration = TimePlanning.betweenDuration(TimePlanning.startOfDay(nextDay),
//                    TimePlanning.endOfDay(nextDay));
//
//            Set<String> set3_e = new HashSet<>();
//            for (int i = durationBetween1_3 + 1; i <= dailyDuration; i++) {
//                set3_e.add(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(nextDay), i)));
//            }
//
//            Assert.assertTrue("PARTIALLY: has lock reception between 3 reception and end of day.",
//                    masterSchedule.entrySet().stream().filter(e -> set3_e.contains(e.getKey())).noneMatch(Map.Entry::getValue));
//
//        }else if (type == Type.END_OF_DAY){
//            Assert.assertTrue("END_OF_DAY: time at the position before the end of the day is not locked.",
//                    masterSchedule.entrySet().stream().filter(e -> LocalDateTimeFormatter.toLocalTime(e.getKey())
//                            .equals(TimePlanning.minusDuration(TimePlanning.endOfDay(nextDay), 1))).filter(Map.Entry::getValue).count() == 1);
//        }
//
//    }
//
//    private enum Type{
//        END_OF_DAY(2),
//        FULL(2),
//        PARTIALLY(1);
//
//        private int i;
//
//        Type(int i) {
//            this.i = i;
//        }
//    }
}
