package model.entity.reception;

import model.entity.authentication.User;
import persistenceSystem.annotations.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@TableName(name = "masters")
public class Master implements Comparable<Master>{

    public static final Master EMPTY_MASTER = new Master();
    static {
        EMPTY_MASTER.name = "Master not found";
    }

    @Id
    @Column(name = "master_id")
    private int id;
    @Column(name = "master_name")
    private String name;
    @Column(name = "master_surname")
    private String surname;
    @Column(name = "master_name_ru")
    private String nameRu;
    @Column(name = "master_surname_ru")
    private String surnameRu;

    @OneToOne
    @JoinColumn(name = "users_user_id")
    private User user;

    @OneToMany(mappedBy = "master") // имя поля дочернего класса
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getSurnameRu() {
        return surnameRu;
    }

    public void setSurnameRu(String surnameRu) {
        this.surnameRu = surnameRu;
    }

    public Set<Service> getMasterServices() {
        return masterServices.stream().map(MastersService::getService)
                .collect(Collectors.toSet());
    }

    public void setMasterServices(List<MastersService> masterServices) {
        this.masterServices = masterServices;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(Master o) {
        if (o==null){
            throw new NullPointerException();
        }
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Master)) return false;
        Master master = (Master) o;
        return Objects.equals(getName(), master.getName()) &&
                Objects.equals(getSurname(), master.getSurname());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getSurname());
    }

    @Override
    public String toString() {
        return "Master{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
