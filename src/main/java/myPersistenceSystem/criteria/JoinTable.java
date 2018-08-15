package myPersistenceSystem.criteria;

public abstract class JoinTable<L, R> implements Criteria {
    private final Class<L> leftClazz;
    private final Class<R> rightClazz;

    private final String leftFieldName;
    private final String rightFieldName;

    public JoinTable(Class<L> leftClazz, String leftFieldName, Class<R> rightClazz, String rightFieldName) {
        this.leftClazz = leftClazz;
        this.rightClazz = rightClazz;
        this.leftFieldName = leftFieldName;
        this.rightFieldName = rightFieldName;
    }

    public Class<L> getLeftClazz() {
        return leftClazz;
    }

    public Class<R> getRightClazz() {
        return rightClazz;
    }

    public String getLeftFieldName() {
        return leftFieldName;
    }

    public String getRightFieldName() {
        return rightFieldName;
    }
}
