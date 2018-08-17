package service.dto.reception.ShowReception;

import entity.model.Master;
import entity.model.Service;
import service.dto.AbstractDto;
import service.dto.AbstractDtoBuilder;

import java.util.Date;
import java.util.Map;

public class ShowReceptionOutDto extends AbstractDto {

    private final Map<Master, Map<String, Boolean>> mastersSchedule;
    private final Map<Service, Boolean> serviceMap;
    private final String reservationDay;
    private final String nextDay;
    private final String previousDay;

    ShowReceptionOutDto(boolean ok, Map<Master, Map<String, Boolean>> mastersSchedule, Map<Service, Boolean> serviceMap, String reservationDay, String nextDay, String previousDay) {
        super(ok);
        this.mastersSchedule = mastersSchedule;
        this.serviceMap = serviceMap;
        this.reservationDay = reservationDay;
        this.nextDay = nextDay;
        this.previousDay = previousDay;
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

    public static ShowReceptionOutDtoBuilder getBuilder(){
        return new ShowReceptionOutDtoBuilder();
    }

    public static class ShowReceptionOutDtoBuilder extends AbstractDtoBuilder{
        private  Map<Master, Map<String, Boolean>> mastersSchedule;
        private  Map<Service, Boolean> serviceMap;
        private  String reservationDay;
        private  String nextDay;
        private  String previousDay;

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

        @Override
        public ShowReceptionOutDto build() {
            return build(true);
        }

        @Override
        public ShowReceptionOutDto build(boolean buildOk){
            return new ShowReceptionOutDto(buildOk, mastersSchedule, serviceMap, reservationDay, nextDay, previousDay);
        }
    }
}
