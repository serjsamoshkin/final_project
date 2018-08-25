package util.dto.reception.showUserReceptions;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;
import util.wrappers.ReceptionView;

import java.util.List;
import java.util.Map;

public class ShowUserReceptionsOutDto extends AbstractDto{

    private final List<ReceptionView> receptions;
    private final int page;
    private final int pageCount;

    private ShowUserReceptionsOutDto(boolean ok, List<ReceptionView> receptions, int page, int pageCount) {
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

    public static ShowUserReceptionsOutDtoBuilder getBuilder(){
        return new ShowUserReceptionsOutDtoBuilder();
    }

    public static class ShowUserReceptionsOutDtoBuilder extends AbstractDtoBuilder{
        private List<ReceptionView> receptions;
        private int page;
        private int pageCount;


        public ShowUserReceptionsOutDtoBuilder setReceptions(List<ReceptionView> receptions) {
            this.receptions = receptions;
            return this;

        }

        public ShowUserReceptionsOutDtoBuilder setPage(int page) {
            this.page = page;
            return this;
        }

        public ShowUserReceptionsOutDtoBuilder setPageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        @Override
        public ShowUserReceptionsOutDto build() {
            return build(true);
        }

        protected ShowUserReceptionsOutDto build(boolean buildOk){
            return new ShowUserReceptionsOutDto(buildOk, receptions, page, pageCount);
        }
    }
}
