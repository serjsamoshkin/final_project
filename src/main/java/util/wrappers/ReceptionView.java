package util.wrappers;

import model.entity.reception.Reception;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Objects;

public class ReceptionView {

    private final Reception reception;
    private boolean reserved = false;

    private static final ReceptionView empty = new ReceptionView();

    private ReceptionView() {
        reception = Reception.EMPTY_RECEPTION;
    }

    private ReceptionView(Reception reception) {
        this.reception = reception;
        reserved = true;
    }

    public static ReceptionView of(Reception reception){
        if (reception == null){
            return empty;
        }
        return new ReceptionView(reception);
    }

    public static ReceptionView empty(){
        return empty;
    }

    // TODO мультиязык
    public Date getDay() {
        return reception.getDay();
    }

    public Time getTime() {
        return reception.getTime();
    }

    public Time getEndTime() {
        return reception.getEndTime();
    }

    public String getService() {
        return reception.getService().getName();
    }

    public String getMaster() {
        return reception.getMaster().getName();
    }

    public String getUser() {
        return reception.getUser().getName();
    }


    public boolean isReserved() {
        return reserved;
    }
}
