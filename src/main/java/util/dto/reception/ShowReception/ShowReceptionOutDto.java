package util.dto.reception.ShowReception;

import model.entity.reception.Master;
import model.entity.reception.Service;
import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Map;

public class ShowReceptionOutDto extends AbstractDto {

    private final Map<Master, Map<String, Boolean>> mastersSchedule;
    private final Map<Service, Boolean> serviceMap;
    private final String reservationDay;
    private final String nextDay;
    private final String previousDay;

    private final int hours;
    private final int minutes;

    public ShowReceptionOutDto(boolean ok, Map<Master, Map<String, Boolean>> mastersSchedule, Map<Service, Boolean> serviceMap, String reservationDay, String nextDay, String previousDay, int hours, int minutes) {
        super(ok);
        this.mastersSchedule = mastersSchedule;
        this.serviceMap = serviceMap;
        this.reservationDay = reservationDay;
        this.nextDay = nextDay;
        this.previousDay = previousDay;
        this.hours = hours;
        this.minutes = minutes;
    }

    public Map<Master, Map<String, Boolean>> getMastersSchedule() {
        return mastersSchedule;
    }

    public Map<Service, Boolean> getServiceMap() {
        return serviceMap;
    }

    public String getReservationDay() {
        return reservationDay;
    }

    public String getNextDay() {
        return nextDay;
    }

    public String getPreviousDay() {
        return previousDay;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public static ShowReceptionOutDtoBuilder getBuilder(){
        return new ShowReceptionOutDtoBuilder();
    }

    public static class ShowReceptionOutDtoBuilder extends AbstractDtoBuilder{
        private Map<Master, Map<String, Boolean>> mastersSchedule;
        private Map<Service, Boolean> serviceMap;
        private String reservationDay;
        private String nextDay;
        private String previousDay;
        private int hours;
        private int minutes;

        public ShowReceptionOutDtoBuilder setMastersSchedule(Map<Master, Map<String, Boolean>> mastersSchedule) {
            this.mastersSchedule = mastersSchedule;
            return this;
        }

        public ShowReceptionOutDtoBuilder setServiceMap(Map<Service, Boolean> serviceMap) {
            this.serviceMap = serviceMap;
            return this;
        }

        public ShowReceptionOutDtoBuilder setReservationDay(String reservationDay) {
            this.reservationDay = reservationDay;
            return this;
        }

        public ShowReceptionOutDtoBuilder setNextDay(String nextDay) {
            this.nextDay = nextDay;
            return this;
        }

        public ShowReceptionOutDtoBuilder setPreviousDay(String previousDay) {
            this.previousDay = previousDay;
            return this;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        @Override
        public ShowReceptionOutDto build() {
            return build(true);
        }

        @Override
        public ShowReceptionOutDto build(boolean buildOk){
            return new ShowReceptionOutDto(buildOk, mastersSchedule, serviceMap, reservationDay, nextDay, previousDay, hours, minutes);
        }
    }
}
