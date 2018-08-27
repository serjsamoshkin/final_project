package model.service;

import javax.sql.DataSource;

public abstract class AbstractService {
    private final DataSource dataSource;

    protected AbstractService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

}
