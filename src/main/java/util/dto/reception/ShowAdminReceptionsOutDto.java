package util.dto.reception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;
import util.wrappers.ReceptionView;

import java.util.List;

public class ShowAdminReceptionsOutDto extends AbstractDto{

    private final List<ReceptionView> receptions;
    private final int page;
    private final int pageCount;

    private ShowAdminReceptionsOutDto(boolean ok, List<ReceptionView> receptions, int page, int pageCount) {
        super(ok);
        this.receptions = receptions;
        this.page = page;
        this.pageCount = pageCount;
    }

    public List<ReceptionView> getReceptions() {
        return receptions;
    }

    public int getPage() {
        return page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public static ShowAdminReceptionsOutDtoBuilder getBuilder(){
        return new ShowAdminReceptionsOutDtoBuilder();
    }

    public static class ShowAdminReceptionsOutDtoBuilder extends AbstractDtoBuilder{
        private List<ReceptionView> receptions;
        private int page;
        private int pageCount;


        public ShowAdminReceptionsOutDtoBuilder setReceptions(List<ReceptionView> receptions) {
            this.receptions = receptions;
            return this;

        }

        public ShowAdminReceptionsOutDtoBuilder setPage(int page) {
            this.page = page;
            return this;
        }

        public ShowAdminReceptionsOutDtoBuilder setPageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        @Override
        public ShowAdminReceptionsOutDto build() {
            return build(true);
        }

        protected ShowAdminReceptionsOutDto build(boolean buildOk){
            return new ShowAdminReceptionsOutDto(buildOk, receptions, page, pageCount);
        }
    }
}
