package util.dto.reception;

import model.entity.authentication.User;
import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ShowUserReceptionsInDto extends AbstractDto{

    private final User user;
    private final String page;


    private ShowUserReceptionsInDto(boolean ok, User user, String page) {
        super(ok);
        this.user = user;
        this.page = page;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<String> getPage() {
        return Optional.ofNullable(page);
    }

    public static ShowUserReceptionsInDtoBuilder getBuilder(){
        return new ShowUserReceptionsInDtoBuilder();
    }

    public static class ShowUserReceptionsInDtoBuilder extends AbstractDtoBuilder{
        private User user;
        private String page;

        public ShowUserReceptionsInDtoBuilder setUser(User user) {
            this.user = user;
            return this;
        }

        public ShowUserReceptionsInDtoBuilder setPage(String page) {
            this.page = page;
            return this;
        }

        @Override
        public ShowUserReceptionsInDto build() {
            return build(true);
        }

        protected ShowUserReceptionsInDto build(boolean buildOk){
            return new ShowUserReceptionsInDto(buildOk, user, page);
        }

    }


}
