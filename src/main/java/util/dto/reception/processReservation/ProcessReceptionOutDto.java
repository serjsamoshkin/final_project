package util.dto.reception.processReservation;

import model.entity.reception.Master;
import model.entity.reception.Service;
import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Map;

public class ProcessReceptionOutDto extends AbstractDto{

    private final Map<Service, Boolean> serviceMap;
    private final Master master;
    private final Service service;
    private final String date;
    private final String time;
    private final boolean reserved;

    private final int hours;
    private final int minutes;
    private final String endTime;

    private ProcessReceptionOutDto(boolean ok, Map<Service, Boolean> serviceMap, Master master, Service service, String date, String time, boolean reserved, int hours, int minutes, String endTime) {
        super(ok);
        this.serviceMap = serviceMap;
        this.master = master;
        this.service = service;
        this.date = date;
        this.time = time;
        this.reserved = reserved;
        this.hours = hours;
        this.minutes = minutes;
        this.endTime = endTime;
    }

    public Map<Service, Boolean> getServiceMap() {
        return serviceMap;
    }

    public Master getMaster() {
        return master;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Service getService() {
        return service;
    }

    public boolean isReserved() {
        return reserved;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public String getEndTime() {
        return endTime;
    }

    public static ShowReceptionOutDtoBuilder getBuilder(){
        return new ShowReceptionOutDtoBuilder();
    }

    public static class ShowReceptionOutDtoBuilder extends AbstractDtoBuilder{
        private Map<Service, Boolean> serviceMap;
        private Master master;
        private Service service;
        private String date;
        private String time;
        private boolean reserved;

        private int hours;
        private int minutes;
        private String endTime;

        public ShowReceptionOutDtoBuilder setServiceMap(Map<Service, Boolean> serviceMap) {
            this.serviceMap = serviceMap;
            return this;
        }

        public ShowReceptionOutDtoBuilder setMaster(Master master) {
            this.master = master;
            return this;
        }

        public ShowReceptionOutDtoBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public ShowReceptionOutDtoBuilder setTime(String time) {
            this.time = time;
            return this;
        }

        public ShowReceptionOutDtoBuilder setReserved(boolean reserved) {
            this.reserved = reserved;
            return this;
        }

        public ShowReceptionOutDtoBuilder setService(Service service) {
            this.service = service;
            return this;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        @Override
        public ProcessReceptionOutDto build() {
            return build(true);
        }

        protected ProcessReceptionOutDto build(boolean buildOk){
            return new ProcessReceptionOutDto(buildOk, serviceMap, master, service, date, time, reserved, hours, minutes, endTime);
        }
    }
}
