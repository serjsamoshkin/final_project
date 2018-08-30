package util.wrappers;

import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Review;
import model.entity.reception.Service;
import util.datetime.LocalDateTimeFormatter;
import util.functionalInterface.MyFunction;

import java.util.Map;
import java.util.Objects;

public class ReceptionView {

    private final Reception reception;
    private boolean reserved = false;
    private boolean processed = false;

    private Review review;

    private static final ReceptionView empty = new ReceptionView();

    private ReceptionView() {
        reception = Reception.EMPTY_RECEPTION;
    }

    private ReceptionView(Reception reception) {
        this.reception = reception;
        reserved = true;
        if (reception.getStatus() != Reception.Status.NEW){
            processed = true;
        }
    }

    public static ReceptionView of(Reception reception){
        if (reception == null){
            return empty;
        }
        return new ReceptionView(reception);
    }

    public static ReceptionView of(Reception reception, Map<Reception, Review> reviewMap){
        if (reception == null){
            return empty;
        }
        ReceptionView view = new ReceptionView(reception);
        reviewMap.computeIfPresent(reception, (k, v) -> view.review = v);
        return view;
    }

    public static ReceptionView empty(){
        return empty;
    }

    public String getDay() {
        return getOrDefault(() -> LocalDateTimeFormatter.toString(reception.getDay()), "");
    }

    public String getTime() {
        return getOrDefault(() -> LocalDateTimeFormatter.toString(reception.getTime()), "");
    }

    public String getEndTime() {
        return getOrDefault(() -> LocalDateTimeFormatter.toString(reception.getEndTime()), "");
    }

    public Service getService() {
        return getOrDefault(reception::getService, Service.EMPTY_SERVICE);
    }

    public Master getMaster() {
        return getOrDefault(reception::getMaster, Master.EMPTY_MASTER);
    }

    public WrappedUser getUser() {
        return getOrDefault(() -> WrappedUser.of(reception.getUser()), WrappedUser.of());
    }


    public String getId(){
        return getOrDefault(() -> String.valueOf(reception.getId()), "");
    }

    public Review getReview() {
        return review;
    }

    public boolean isHasReview() {
        return getOrDefault(() -> review != null && review.getStatus().equals(Review.Status.DONE), false);
    }

    public String getVersion(){
        return getOrDefault(() -> String.valueOf(reception.getVersion()), "");
    }

    public String getStatus(){
        return getOrDefault(() -> reception.getStatus().toString(), "");
    }

    private <R> R getOrDefault(MyFunction<R> function, R def){
        if (reception == Reception.EMPTY_RECEPTION){
            return def;
        }
        R result = function.apply();
        return result == null ? def : result;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isReserved() {
        return reserved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceptionView)) return false;
        ReceptionView that = (ReceptionView) o;
        return Objects.equals(reception, that.reception);
    }

    @Override
    public int hashCode() {

        return Objects.hash(reception);
    }
}
