package model.entity.reception;

import persistenceSystem.annotations.Column;
import persistenceSystem.annotations.Id;
import persistenceSystem.annotations.OneToMany;
import persistenceSystem.annotations.TableName;

import javax.sql.rowset.serial.SerialBlob;
import java.util.List;
import java.util.Objects;


@TableName(name = "services")
public class Service implements Comparable<Service>{

    public static final Service EMPTY_SERVICE = new Service();
    static {
        EMPTY_SERVICE.name = "Service not found";
        EMPTY_SERVICE.setId(-1);
    }

    @Id
    @Column(name = "service_id")
    private int id;
    @Column(name = "service_name")
    private String name;
    @Column(name = "service_description")
    private String description;
    @Column(name = "service_duration")
    private int duration;

    // TODO LAZY
    @Column(name = "service_pic")
    SerialBlob picture;

    @OneToMany(mappedBy = "service") // имя поля дочернего класса
    private List<MastersService> masterServices;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public SerialBlob getPicture() {
        return picture;
    }

    public void setPicture(SerialBlob picture) {
        this.picture = picture;
    }

    public List<MastersService> getMasterServices() {
        return masterServices;
    }

    public void setMasterServices(List<MastersService> masterServices) {
        this.masterServices = masterServices;
    }

    @Override
    public int compareTo(Service o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return Objects.equals(getName(), service.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                '}';
    }
}
