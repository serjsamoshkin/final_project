package service.dto.reception.ProcessReservation;

import model.entity.model.Master;
import model.entity.model.Service;
import service.dto.AbstractDto;
import service.dto.AbstractDtoBuilder;

import java.util.Map;

public class ProcessReceptionOutDto extends AbstractDto{

    private final Map<Service, Boolean> serviceMap;
    private final Master master;
    private final Service service;
    private final String date;
    private final String time;
    private final boolean reserved;


    public ProcessReceptionOutDto(boolean ok, Map<Service, Boolean> serviceMap, Master master, Service service, String date, String time, boolean reserved) {
        super(ok);
        this.serviceMap = serviceMap;
        this.master = master;
        this.service = service;
        this.date = date;
        this.time = time;
        this.reserved = reserved;
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

        @Override
        public ProcessReceptionOutDto build() {
            return build(true);
        }

        protected ProcessReceptionOutDto build(boolean buildOk){
            return new ProcessReceptionOutDto(buildOk, serviceMap, master, service, date, time, reserved);

        }

    }


}
