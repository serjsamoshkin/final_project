package util.dto.reception;

import model.entity.authentication.User;
import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.time.LocalDate;
import java.util.Optional;

public class ShowMasterScheduleInDto extends AbstractDto{

    private final User user;
    private final LocalDate date;


    private ShowMasterScheduleInDto(boolean ok, User user, LocalDate date) {
        super(ok);
        this.user = user;
        this.date = date;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(date);
    }

    public static ShowMasterScheduleInDtoBuilder getBuilder(){
        return new ShowMasterScheduleInDtoBuilder();
    }

    public static class ShowMasterScheduleInDtoBuilder extends AbstractDtoBuilder{
        private User user;
        private LocalDate date;

        public ShowMasterScheduleInDtoBuilder setUser(User user) {
            this.user = user;
            return this;
        }

        public ShowMasterScheduleInDtoBuilder setDate(LocalDate date) {
            this.date = date;
            return this;
        }

        @Override
        public ShowMasterScheduleInDto build() {
            return build(true);
        }

        protected ShowMasterScheduleInDto build(boolean buildOk){
            return new ShowMasterScheduleInDto(buildOk, user, date);
        }

    }


}
