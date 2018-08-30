package util.dto.reception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ShowAdminReceptionsInDto extends AbstractDto{

    private final String page;
    private final String sortBy;
    private final String direction;

    private ShowAdminReceptionsInDto(boolean ok, String page, String sortBy, String direction) {
        super(ok);
        this.page = page;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public Optional<String> getPage() {
        return Optional.ofNullable(page);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<String> getDirection() {
        return Optional.ofNullable(direction);
    }

    public static ShowAdminReceptionsInDtoBuilder getBuilder(){
        return new ShowAdminReceptionsInDtoBuilder();
    }

    public static class ShowAdminReceptionsInDtoBuilder extends AbstractDtoBuilder{
        private String page;
        private String sortBy;
        private String direction;


        public ShowAdminReceptionsInDtoBuilder setPage(String page) {
            this.page = page;
            return this;
        }

        public ShowAdminReceptionsInDtoBuilder setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public ShowAdminReceptionsInDtoBuilder setDirection(String direction) {
            this.direction = direction;
            return this;
        }

        @Override
        public ShowAdminReceptionsInDto build() {
            return build(true);
        }

        protected ShowAdminReceptionsInDto build(boolean buildOk){
            return new ShowAdminReceptionsInDto(buildOk, page, sortBy, direction);
        }

    }


}
