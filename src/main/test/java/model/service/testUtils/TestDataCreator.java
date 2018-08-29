package model.service.testUtils;

import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class TestDataCreator {

    private static Random rnd = new Random();

    public static Reception createTestReception(User user, Master master, Service service, LocalTime startTime){

        LocalDate now = LocalDate.now();

        Reception reception = new Reception();

        reception.setId(rnd.nextInt());
        reception.setVersion(0);
        reception.setUser(user);
        reception.setMaster(master);
        reception.setService(service);
        reception.setDay(LocalDateTimeFormatter.toSqlDate(now));
        reception.setTime(LocalDateTimeFormatter.toSqlTime(startTime));
        reception.setEndTime(LocalDateTimeFormatter.toSqlTime(TimePlanning.plusDuration(startTime, service.getDuration())));
        reception.setStatus(Reception.Status.NEW);

        return reception;
    }

    public static User createTestUser(String name){

        User user = new User();
        user.setId(rnd.nextInt());
        user.setEmail("test@te.te");
        user.setName(name);

        return user;
    }

    public static Service createTestService(String name, int duration){

        Service service = new Service();
        service.setId(rnd.nextInt());
        service.setName(name);
        service.setNameRu(name);
        service.setDuration(duration);

        return service;
    }

    public static Master createTestMaster(String name){

        Master master = new Master();
        master.setId(rnd.nextInt());
        master.setName(name);
        master.setNameRu(name);
        master.setSurname(name);
        master.setSurnameRu(name);

        return master;
    }

}
