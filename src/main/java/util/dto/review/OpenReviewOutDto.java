package util.dto.review;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;
import util.wrappers.ReceptionView;

import java.util.Map;

public class OpenReviewOutDto extends AbstractDto{

    private final ReceptionView receptionView;
    private final int reviewId;

    private OpenReviewOutDto(boolean ok, ReceptionView receptionView, int reviewId) {
        super(ok);
        this.receptionView = receptionView;
        this.reviewId = reviewId;
    }

    public ReceptionView getReceptionView() {
        return receptionView;
    }

    public int getReviewId() {
        return reviewId;
    }

    public static OpenReviewOutDtoBuilder getBuilder(){
        return new OpenReviewOutDtoBuilder();
    }

    public static class OpenReviewOutDtoBuilder extends AbstractDtoBuilder{

        private ReceptionView receptionView;
        private int reviewId;


        public OpenReviewOutDtoBuilder setReceptionView(ReceptionView receptionView) {
            this.receptionView = receptionView;
            return this;
        }

        public OpenReviewOutDtoBuilder setReviewId(int reviewId) {
            this.reviewId = reviewId;
            return this;
        }

        @Override
        public OpenReviewOutDto build() {
            return build(true);
        }

        protected OpenReviewOutDto build(boolean buildOk){
            return new OpenReviewOutDto(buildOk, receptionView, reviewId);
        }

    }


}
