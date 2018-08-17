package service.dto.reception.ProcessReservation;

import service.dto.AbstractDto;
import service.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ProcessReceptionInDto extends AbstractDto{

    private final String day;
    private final String time;
    private final String service;
    private final String master;

    ProcessReceptionInDto(boolean ok, String day, String time, String service, String master) {
        super(ok);
        this.day = day;
        this.time = time;
        this.service = service;
        this.master = master;
    }

    public Optional<String> getTime() {
        return  Optional.ofNullable(time);
    }

    public Optional<String> getMaster() {
        return  Optional.ofNullable(master);
    }

    public Optional<String> getDay() {
        return Optional.ofNullable(day);
    }

    public Optional<String> getService() {
        return Optional.ofNullable(service);
    }

    public static ProcessReceptionInDtoBuilder getBuilder(){
        return new ProcessReceptionInDtoBuilder();
    }

    public static class ProcessReceptionInDtoBuilder extends AbstractDtoBuilder{
        private String day;
        private String time;
        private String service;
        private String master;


        public ProcessReceptionInDtoBuilder setTime(String time) {
            this.time = time;
            return this;
        }

        public ProcessReceptionInDtoBuilder setMaster(String master) {
            this.master = master;
            return this;
        }

        public ProcessReceptionInDtoBuilder setDay(String day) {
            this.day = day;
            return this;
        }

        public ProcessReceptionInDtoBuilder setService(String service) {
            this.service = service;
            return this;
        }

        @Override
        public ProcessReceptionInDto build() {
            return build(true);
        }

        public ProcessReceptionInDto build(boolean buildOk){
            return new ProcessReceptionInDto(buildOk, day, time, service, master);
        }

    }


}
