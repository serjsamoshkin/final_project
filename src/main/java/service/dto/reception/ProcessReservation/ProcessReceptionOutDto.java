package service.dto.reception.ProcessReservation;

import entity.model.Master;
import entity.model.Service;
import service.dto.AbstractDto;
import service.dto.AbstractDtoBuilder;

import java.util.Date;
import java.util.Map;

public class ProcessReceptionOutDto extends AbstractDto{

    private final Map<Master, Map<String, Boolean>> mastersSchedule;
    private final  Map<Service, Boolean> serviceMap;
    private final  Date reservationDay;
    private final  String reservationDayTxt;
    private final  String nextDay;
    private final  String previousDay;

    public ProcessReceptionOutDto(boolean ok, Map<Master, Map<String, Boolean>> mastersSchedule, Map<Service, Boolean> serviceMap, Date reservationDay, String reservationDayTxt, String nextDay, String previousDay) {
        super(ok);
        this.mastersSchedule = mastersSchedule;
        this.serviceMap = serviceMap;
        this.reservationDay = reservationDay;
        this.reservationDayTxt = reservationDayTxt;
        this.nextDay = nextDay;
        this.previousDay = previousDay;
    }

    public Map<Master, Map<String, Boolean>> getMastersSchedule() {
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


    public static ShowReceptionOutDtoBuilder getBuilder(){
        return new ShowReceptionOutDtoBuilder();
    }

    public static class ShowReceptionOutDtoBuilder extends AbstractDtoBuilder{
        private  Map<Master, Map<String, Boolean>> mastersSchedule;
        private  Map<Service, Boolean> serviceMap;
        private  Date reservationDay;
        private  String reservationDayTxt;
        private  String nextDay;
        private  String previousDay;

        public void setMastersSchedule(Map<Master, Map<String, Boolean>> mastersSchedule) {
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

        @Override
        public ProcessReceptionOutDto build() {
            return build(true);
        }

        public ProcessReceptionOutDto build(boolean buildOk){
            return new ProcessReceptionOutDto(buildOk, mastersSchedule, serviceMap, reservationDay, reservationDayTxt, nextDay, previousDay);

        }

    }


}
