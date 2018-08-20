package model.entity.reception;

import model.entity.authentication.User;

import java.sql.Date;
import java.sql.Time;

public class ImmutableReception {

    private final int id;
    private final java.sql.Date day;
    private final java.sql.Time time;
    private final java.sql.Time endTime;
    private final int version;
    private final Service service;
    private final Master master;
    private final User user;

    private Reception reception;

    private ImmutableReception(Reception reception, int id, Date day, Time time, Time endTime, int version, Service service, Master master, User user) {
        this.reception = reception;

        this.id = id;
        this.day = day;
        this.time = time;
        this.endTime = endTime;
        this.version = version;
        this.service = service;
        this.master = master;
        this.user = user;
    }

    public static ImmutableReception of(Reception reception){

        return new ImmutableReception(reception, reception.getId(), reception.getDay(), reception.getTime(), reception.getEndTime(),
                reception.getVersion(), reception.getService(), reception.getMaster(), reception.getUser());
    }

    public int getId() {
        return id;
    }

    public Date getDay() {
        return day;
    }

    public Time getTime() {
        return time;
    }

    public Time getEndTime() {
        return endTime;
    }

    public int getVersion() {
        return version;
    }

     public Service getService() {
        return service;
    }

    public Master getMaster() {
        return master;
    }

    public User getUser() {
        return user;
    }

    public Reception getReception() {
        if (reception == null){
            Reception reception = new Reception();
            reception.setDay(day);
            reception.setEndTime(endTime);
            reception.setMaster(master);
            reception.setService(service);
            reception.setTime(time);
            reception.setUser(user);
            reception.setId(id);
            reception.setVersion(version);

            return reception;


        }else {
            return reception;
        }
    }

// SETTERS

    public ImmutableReception setId(int id) {
        return new ImmutableReception(null, id, this.getDay(), this.getTime(), this.getEndTime(),
                this.getVersion(), this.getService(), this.getMaster(), this.getUser());
    }

    public ImmutableReception setDay(Date day) {
        return new ImmutableReception(null, this.getId(), day, this.getTime(), this.getEndTime(),
                this.getVersion(), this.getService(), this.getMaster(), this.getUser());
    }

    public ImmutableReception setTime(Time time) {
        return new ImmutableReception(null, this.getId(), this.getDay(), time, this.getEndTime(),
                this.getVersion(), this.getService(), this.getMaster(), this.getUser());
    }

    public ImmutableReception setEndTime(Time endTime) {
        return new ImmutableReception(null, this.getId(), this.getDay(), this.getTime(), endTime,
                this.getVersion(), this.getService(), this.getMaster(), this.getUser());
    }

    public ImmutableReception setVersion(int version) {
        return new ImmutableReception(null, this.getId(), this.getDay(), this.getTime(), this.getEndTime(),
                version, this.getService(), this.getMaster(), this.getUser());
    }

    public ImmutableReception setService(Service service) {
        return new ImmutableReception(null, this.getId(), this.getDay(), this.getTime(), this.getEndTime(),
                this.getVersion(), service, this.getMaster(), this.getUser());
    }

    public ImmutableReception setMaster(Master master) {
        return new ImmutableReception(null, this.getId(), this.getDay(), this.getTime(), this.getEndTime(),
                this.getVersion(), this.getService(), master, this.getUser());
    }

    public ImmutableReception setUser(User user) {
        return new ImmutableReception(null, this.getId(), this.getDay(), this.getTime(), this.getEndTime(),
                this.getVersion(), this.getService(), this.getMaster(), user);
    }
}
