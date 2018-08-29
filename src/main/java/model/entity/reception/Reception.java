package model.entity.reception;

import model.entity.authentication.User;
import persistenceSystem.annotations.*;
import persistenceSystem.annotations.EnumType;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

@VersionControl
@TableName(name = "receptions")
public class Reception{

    public static final Reception EMPTY_RECEPTION = new Reception();

    @Id
    @Column(name = "reception_id")
    private int id;
    @Version
    @Column(name = "reception_version")
    private int version;
    @Column(name = "reception_day")
    private java.sql.Date day;
    @Column(name = "reception_time")
    private java.sql.Time time;
    @Column(name = "reception_end_time")
    private java.sql.Time endTime;

    @OneToOne
    @JoinColumn(name = "services_service_id")
    private Service service;

    @OneToOne
    @JoinColumn(name = "masters_master_id")
    private Master master;

    @OneToOne
    @JoinColumn(name = "users_user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "reviews_review_id")
    private Review review;

    @EnumType
    @Column(name = "reception_status")
    private Status status;

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

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
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

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static Reception of(Reception reception){
        Reception copy = new Reception();
        copy.setDay(reception.getDay());
        copy.setEndTime(reception.getEndTime());
        copy.setMaster(reception.getMaster());
        copy.setService(reception.getService());
        copy.setTime(reception.getTime());
        copy.setUser(reception.getUser());
        copy.setId(reception.getId());
        copy.setStatus(reception.getStatus());
        copy.setVersion(reception.getVersion());

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reception)) return false;
        Reception reception = (Reception) o;
        return Objects.equals(getTime(), reception.getTime()) &&
                Objects.equals(getDay(), reception.getDay()) &&
                Objects.equals(getMaster(), reception.getMaster());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDay(), getTime(), getMaster());
    }

    @Override
    public String toString() {
        return "Reception{" +
                "day=" + day +
                ", time=" + time +
                ", endTime=" + endTime +
                ", service=" + service +
                ", master=" + master +
                ", user=" + user +
                '}';
    }

    public enum Status{
        NEW,
        DONE,
        CANCELED,
        ;
    }
}
