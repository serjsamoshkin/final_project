package util.dto.reception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ShowReceptionInDto extends AbstractDto {

    private String day;
    private String service;

    private ShowReceptionInDto(boolean ok, String day, String service) {
        super(ok);
        this.day = day;
        this.service = service;
    }

    public Optional<String> getDay() {
        return Optional.ofNullable(day);
    }

    public Optional<String> getService() {
        return Optional.ofNullable(service);
    }

    public static ShowReceptionInDtoBuilder getBuilder(){
        return new ShowReceptionInDtoBuilder();
    }

    public static class ShowReceptionInDtoBuilder extends AbstractDtoBuilder {
        private String day;
        private String service;

        public ShowReceptionInDtoBuilder setDay(String day) {
            this.day = day;
            return this;
        }

        public ShowReceptionInDtoBuilder setService(String service) {
            this.service = service;
            return this;
        }

        @Override
        public ShowReceptionInDto build() {
            return build(true);
        }

        public ShowReceptionInDto build(boolean buildOk){
            return new ShowReceptionInDto(buildOk, day, service);
        }

    }


}
