package entity.authentication;

import myPersistenceSystem.annotations.*;

import java.util.List;
import java.util.Objects;

/**
 * Class for presenting user {@code roles} in application.
 * Has DAO implementation in class {@code RoleDAO}.
 */
@TableName(name = "roles")
public class Role {

    public static final Role EMPTY_ROLE = new Role();
    static {
        EMPTY_ROLE.name = "Empty";
    }


    @Id
    @Column(name = "role_id")
    private int id;
    @Column(name = "role_name")
    private String name;

    @OneToMany(mappedBy = "role") // имя поля дочернего класса
    private List<UsersRole> usersRoles;

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

    public List<UsersRole> getUsersRoles() {
        return usersRoles;
    }

    public void setUsersRoles(List<UsersRole> usersRoles) {
        this.usersRoles = usersRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(getName(), role.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return  name;
    }
}
