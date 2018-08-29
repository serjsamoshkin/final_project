package util.dto.reception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ShowAdminReceptionsInDto extends AbstractDto{

    private final String page;

    private ShowAdminReceptionsInDto(boolean ok, String page) {
        super(ok);
        this.page = page;
    }

    public Optional<String> getPage() {
        return Optional.ofNullable(page);
    }

    public static ShowAdminReceptionsInDtoBuilder getBuilder(){
        return new ShowAdminReceptionsInDtoBuilder();
    }

    public static class ShowAdminReceptionsInDtoBuilder extends AbstractDtoBuilder{
        private String page;


        public ShowAdminReceptionsInDtoBuilder setPage(String page) {
            this.page = page;
            return this;
        }

        @Override
        public ShowAdminReceptionsInDto build() {
            return build(true);
        }

        protected ShowAdminReceptionsInDto build(boolean buildOk){
            return new ShowAdminReceptionsInDto(buildOk, page);
        }

    }


}
