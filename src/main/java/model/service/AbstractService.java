package model.service;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

public abstract class AbstractService implements AutoCloseable{
    private final ServletContext context;
    private final DataSource dataSource;

    protected AbstractService(ServletContext context, DataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    protected ServletContext getContext() {
        return context;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() throws Exception {
        // TODO идя в том, чтобы иметь возможность создавать одноразывае сервисы (которые могут выгружаться)
    }
}
