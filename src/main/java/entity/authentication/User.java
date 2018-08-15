package entity.authentication;
import myPersistenceSystem.annotations.*;

import java.util.*;

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

    //TODO При создании нового пользователя список пуст, нужно переделать запись таблиц вторичных ключей через внутренний сеттер.
    @OneToMany(mappedBy = "user")
    private List<UsersRole> usersRoles;

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

        // TODO загрушка пока не сделаю механизм актуализации по вторичной связе.
        if (usersRoles == null) {
            usersRoles = new ArrayList<>();
        }

        Set<Role> roles = new HashSet<>();
        for (UsersRole usersRole :
                usersRoles) {
            roles.add(usersRole.getRole());
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