package service.dto.reception;

import java.util.Optional;

public class ShowReceptionInDto {

    private final String day;
    private final  String service;

    ShowReceptionInDto(String day, String service) {
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

    public static class ShowReceptionInDtoBuilder {
        private String day;
        private  String service;


        public ShowReceptionInDtoBuilder setDay(String day) {
            this.day = day;
            return this;
        }

        public ShowReceptionInDtoBuilder setService(String service) {
            this.service = service;
            return this;
        }

        public ShowReceptionInDto build(){
            return new ShowReceptionInDto(day, service);
        }

    }


}
