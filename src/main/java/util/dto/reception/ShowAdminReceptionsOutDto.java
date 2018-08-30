package util.dto.reception;

import persistenceSystem.criteria.CriteriaBuilder;
import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;
import util.wrappers.ReceptionView;

import java.util.List;
import java.util.Optional;

public class ShowAdminReceptionsOutDto extends AbstractDto{

    private final List<ReceptionView> receptions;
    private final int page;
    private final int pageCount;

    private final String sortingField;
    private final CriteriaBuilder.Order order;

    private ShowAdminReceptionsOutDto(boolean ok, List<ReceptionView> receptions, int page, int pageCount, String sortingField, CriteriaBuilder.Order order) {
        super(ok);
        this.receptions = receptions;
        this.page = page;
        this.pageCount = pageCount;
        this.sortingField = sortingField;
        this.order = order;
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

    public Optional<String> getSortingField() {
        return Optional.ofNullable(sortingField);
    }

    public Optional<CriteriaBuilder.Order> getOrder() {
        return Optional.ofNullable(order);
    }

    public static ShowAdminReceptionsOutDtoBuilder getBuilder(){
        return new ShowAdminReceptionsOutDtoBuilder();
    }

    public static class ShowAdminReceptionsOutDtoBuilder extends AbstractDtoBuilder{
        private List<ReceptionView> receptions;
        private int page;
        private int pageCount;

        private String sortingField;
        private CriteriaBuilder.Order order;


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

        public ShowAdminReceptionsOutDtoBuilder setSortingField(String sortingField) {
            this.sortingField = sortingField;
            return this;
        }

        public ShowAdminReceptionsOutDtoBuilder setOrder(CriteriaBuilder.Order order) {
            this.order = order;
            return this;
        }

        @Override
        public ShowAdminReceptionsOutDto build() {
            return build(true);
        }

        protected ShowAdminReceptionsOutDto build(boolean buildOk){
            return new ShowAdminReceptionsOutDto(buildOk, receptions, page, pageCount, sortingField, order);
        }
    }
}
