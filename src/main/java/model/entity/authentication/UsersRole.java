package model.entity.authentication;

import persistenceSystem.annotations.*;

import java.util.Objects;

/**
 * Contains pairs of {@code user} Roles.
 * Has DAO implementation in class {@code UserRoleDAO}.
 */
@TableName(name = "users_roles")
public class UsersRole {

    @Id
    @Column(name = "user_role_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "users_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "roles_role_id")
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersRole usersRole = (UsersRole) o;
        return Objects.equals(getUser(), usersRole.getUser()) &&
                Objects.equals(getRole(), usersRole.getRole());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUser(), getRole());
    }

    @Override
    public String toString() {
        return "user=" + user +
                ", role=" + role;
    }
}
