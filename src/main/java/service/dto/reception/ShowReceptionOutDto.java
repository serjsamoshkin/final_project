package service.dto.reception;

import entity.model.Master;
import entity.model.Service;

import java.util.Date;
import java.util.Map;

public class ShowReceptionOutDto {

    private final Map<Master, Map<Date, Boolean>> mastersSchedule;
    private final  Map<Service, Boolean> serviceMap;
    private final  Date reservationDay;
    private final  String reservationDayTxt;
    private final  String nextDay;
    private final  String previousDay;

    private boolean ok = false;


    ShowReceptionOutDto(Map<Master, Map<Date, Boolean>> mastersSchedule, Map<Service, Boolean> serviceMap, Date reservationDay, String reservationDayTxt, String nextDay, String previousDay) {
        this.mastersSchedule = mastersSchedule;
        this.serviceMap = serviceMap;
        this.reservationDay = reservationDay;
        this.reservationDayTxt = reservationDayTxt;
        this.nextDay = nextDay;
        this.previousDay = previousDay;
    }

    public Map<Master, Map<Date, Boolean>> getMastersSchedule() {
        return mastersSchedule;
    }

    public Map<Service, Boolean> getServiceMap() {
        return serviceMap;
    }

    public Date getReservationDay() {
        return reservationDay;
    }

    public String getReservationDayTxt() {
        return reservationDayTxt;
    }

    public String getNextDay() {
        return nextDay;
    }

    public String getPreviousDay() {
        return previousDay;
    }

    public boolean isOk() {
        return ok;
    }

    public static ShowReceptionOutDtoBuilder getBuilder(){
        return new ShowReceptionOutDtoBuilder();
    }

    public static class ShowReceptionOutDtoBuilder {
        private  Map<Master, Map<Date, Boolean>> mastersSchedule;
        private  Map<Service, Boolean> serviceMap;
        private  Date reservationDay;
        private  String reservationDayTxt;
        private  String nextDay;
        private  String previousDay;

        public void setMastersSchedule(Map<Master, Map<Date, Boolean>> mastersSchedule) {
            this.mastersSchedule = mastersSchedule;
        }

        public ShowReceptionOutDtoBuilder setServiceMap(Map<Service, Boolean> serviceMap) {
            this.serviceMap = serviceMap;
            return this;
        }

        public ShowReceptionOutDtoBuilder setReservationDay(Date reservationDay) {
            this.reservationDay = reservationDay;
            return this;
        }

        public void setReservationDayTxt(String reservationDayTxt) {
            this.reservationDayTxt = reservationDayTxt;
        }

        public ShowReceptionOutDtoBuilder setNextDay(String nextDay) {
            this.nextDay = nextDay;
            return this;
        }

        public ShowReceptionOutDtoBuilder setPreviousDay(String previousDay) {
            this.previousDay = previousDay;
            return this;
        }


        public ShowReceptionOutDto buildFalse(){
            ShowReceptionOutDto dto = new ShowReceptionOutDto(null, null, null, null, null, null);
            dto.ok = false;
            return dto;
        }

        public ShowReceptionOutDto build(){
            ShowReceptionOutDto dto = new ShowReceptionOutDto(mastersSchedule, serviceMap, reservationDay, reservationDayTxt, nextDay, previousDay);
            dto.ok = true;
            return dto;
        }

    }


}
