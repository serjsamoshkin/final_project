package entity.model;

import myPersistenceSystem.annotations.*;

import java.util.Objects;

@TableName(name = "masters_services")
public class MastersService {

    @Id
    @Column(name = "masters_services_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "masters_master_id")
    private Master master;
    @ManyToOne
    @JoinColumn(name = "services_service_id")
    private Service service;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MastersService)) return false;
        MastersService that = (MastersService) o;
        return getId() == that.getId() &&
                Objects.equals(getMaster(), that.getMaster()) &&
                Objects.equals(getService(), that.getService());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getMaster(), getService());
    }

    @Override
    public String toString() {
        return "MastersService{" +
                "master=" + master +
                ", service=" + service +
                '}';
    }
}
