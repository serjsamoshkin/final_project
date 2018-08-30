package model.entity.authentication;
import persistenceSystem.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for presenting authenticated users in application.
 * Has DAO implementation in class {@code UserDAO}.
 * Used to implement Authentication, contains List<UsersRole> usersRoles
 * for implementation of Authorization.
 * Used as Bean object in web
 */
@TableName(name = "users")
public class User {

    public static final User NOT_AUTHENTICATED = new User();
    static {
        NOT_AUTHENTICATED.name = "guest";
        NOT_AUTHENTICATED.email = "";
        NOT_AUTHENTICATED.passwordHash = "";
        NOT_AUTHENTICATED.usersRoles = new ArrayList<>();
    }

    @Id
    @Column(name = "user_id")
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "user_email")
    private String email;
    @Column(name = "user_password_hash")
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<UsersRole> usersRoles = new ArrayList<>();

    private volatile Set<Role> roles;

    public User() {
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<UsersRole> getUsersRoles() {
        return usersRoles;
    }

    public void setUsersRoles(List<UsersRole> usersRoles) {
        this.usersRoles = usersRoles;
    }

    public Set<Role> getRoles() {
        if (roles == null){
            synchronized (this){
                if (roles == null) {
                    roles = usersRoles.stream().map(UsersRole::getRole).collect(Collectors.toSet());
                }
            }
        }
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return email;
    }
}
