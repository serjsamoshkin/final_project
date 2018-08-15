package service.dto;

public abstract class AbstractDtoBuilder {

    private boolean buildOk = true;

    public abstract AbstractDto build();
    protected abstract AbstractDto build(boolean buildOk);

    @SuppressWarnings("unchecked")
    public <T extends AbstractDto> T buildFalse(){
        return (T)build(false);
    }

}
