package service.dto;

public abstract class AbstractDto {

    private boolean ok;

    protected AbstractDto(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }
}
