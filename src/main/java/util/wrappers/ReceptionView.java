package util.wrappers;

import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import util.datetime.LocalDateTimeFormatter;

import java.util.Objects;

public class ReceptionView {

    private final Reception reception;
    private boolean reserved = false;
    private boolean processed = false;

    private static final ReceptionView empty = new ReceptionView();

    private ReceptionView() {
        reception = Reception.EMPTY_RECEPTION;
    }

    private ReceptionView(Reception reception) {
        this.reception = reception;
        reserved = true;
        if (reception.getStatus() != Reception.Status.NEW){
            processed = true;
        }
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

    public String getDay() {
        if (reception == null){
            return "";
        }
        return LocalDateTimeFormatter.toString(reception.getDay());
    }

    public String getTime() {
        if (reception == null){
            return "";
        }
        return LocalDateTimeFormatter.toString(reception.getTime());
    }

    public String getEndTime() {
        if (reception == null){
            return "";
        }
        return LocalDateTimeFormatter.toString(reception.getEndTime());
    }

    public Service getService() {
        if (reception == null){
            return Service.EMPTY_SERVICE;
        }
        return reception.getService();
    }

    public Master getMaster() {
        if (reception == null){
            return Master.EMPTY_MASTER;
        }
        return reception.getMaster();
    }

    public String getUser() {
        if (reception == null){
            return "";
        }
        return reception.getUser().getName();
    }

    public String getId(){
        if (reception == null){
            return "";
        }
        return String.valueOf(reception.getId());
    }

    public String getVersion(){
        if (reception == null){
            return "";
        }
        return String.valueOf(reception.getVersion());
    }

    public String getStatus(){
        if (reception == null){
            return "";
        }
        return reception.getStatus().toString();
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isReserved() {
        return reserved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceptionView)) return false;
        ReceptionView that = (ReceptionView) o;
        return Objects.equals(reception, that.reception);
    }

    @Override
    public int hashCode() {

        return Objects.hash(reception);
    }
}
