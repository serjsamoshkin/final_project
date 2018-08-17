package entity.model;

import entity.authentication.User;
import myPersistenceSystem.annotations.*;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Objects;


@TableName(name = "receptions")
public class Reception{

    public static final Reception EMPTY_RECEPTION = new Reception();

    @Id
    @Column(name = "reception_id")
    private int id;
    @Column(name = "reception_day")
    private java.sql.Date day;
    @Column(name = "reception_time")
    private java.sql.Time time;

    @OneToOne
    @JoinColumn(name = "services_service_id")
    private Service service;

    @OneToOne
    @JoinColumn(name = "masters_master_id")
    private Master master;

    @OneToOne
    @JoinColumn(name = "users_user_id")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reception)) return false;
        Reception reception = (Reception) o;
        return Objects.equals(getTime(), reception.getTime()) &&
                Objects.equals(getDay(), reception.getDay()) &&
                Objects.equals(getService(), reception.getService()) &&
                Objects.equals(getMaster(), reception.getMaster());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDay(), getTime(), getService(), getMaster());
    }

    @Override
    public String toString() {
        return "Reception{" +
                "day=" + day +
                ", time=" + time +
                ", service=" + service +
                ", master=" + master +
                ", user=" + user +
                '}';
    }
}
