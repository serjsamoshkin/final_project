package model.entity.reception;

import persistenceSystem.annotations.*;

import java.util.Objects;

@TableName(name = "reviews")
public class Review {

    @Id
    @Column(name = "review_id")
    private int id;

    @Column(name = "review_text")
    private String text;

    @OneToOne
    @JoinColumn(name = "receptions_reception_id")
    private Reception reception;

    @Column(name = "review_token")
    private String token;

    @EnumType
    @Column(name = "review_status")
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Reception getReception() {
        return reception;
    }

    public void setReception(Reception reception) {
        this.reception = reception;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static Review copyOf(Review review){
        Review copy = new Review();
        copy.setId(review.getId());
        copy.setStatus(review.getStatus());
        copy.setReception(review.getReception());
        copy.setText(review.getText());
        copy.setToken(review.getToken());

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return Objects.equals(getReception(), review.getReception());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getReception());
    }

    public enum Status{
        NEW,
        SENT,
        DONE,
        ;
    }
}
