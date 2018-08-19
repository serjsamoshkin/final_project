package util.dto;

public abstract class AbstractDtoBuilder {

    private boolean buildOk = true;

    public abstract AbstractDto build();
    protected abstract AbstractDto build(boolean buildOk);


    /**
     * Build concrete Dto object of external {@code <T>) type with empty transfer values and
     * with ok == false in. Used to interrupt the creation of Dto object in place where occurs
     * Exception occurred or where invariants were violated.
     * @param <T> concrete Dto type of AbstractDto subclass
     * @return {@code AbstractDto} subclass.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractDto> T buildFalse(){
        return (T)build(false);
    }

}
