package util.wrappers;

import model.entity.authentication.Role;
import model.entity.authentication.User;
import model.entity.authentication.UsersRole;
import model.service.ServiceMapper;
import model.service.authentication.RoleService;

import java.util.List;
import java.util.Set;

/**
 * Unmodifiable representation of the {@code User} with additional data
 */
public class WrappedUser extends User {
    private final User userObj;

    private final boolean authenticated;
    private final boolean admin;
    private final boolean master;
    private final boolean user;

    private static volatile WrappedUser defUser;

    private WrappedUser(User userObj) {

        RoleService roleService = ServiceMapper.getMapper().getService(RoleService.class);
        Set<Role> roles = userObj.getRoles();

        this.userObj = userObj;
        user = roles.contains(roleService.getRoleUser());
        admin =  roles.contains(roleService.getRoleAdministrator());
        master = roles.contains(roleService.getRoleMaster());

        authenticated = userObj != User.NOT_AUTHENTICATED;

    }

    public static WrappedUser of(){
        if (defUser == null){
            synchronized (WrappedUser.class){
                if (defUser == null){
                    defUser = new WrappedUser(User.NOT_AUTHENTICATED);
                }
            }
        }
        return defUser;
    }

    public static WrappedUser of(User user){

        if (user instanceof WrappedUser){
            return (WrappedUser) user;
        }

        return new WrappedUser(user);
    }

    public static WrappedUser of(Object user){

        if (user instanceof WrappedUser){
            return new WrappedUser(((WrappedUser) user).getUserObj());
        }else if (user instanceof User) {
            return new WrappedUser((User) user);
        }else {
            throw new IllegalArgumentException("Parameter passed to WrappedUser.of is not instanceof User");
        }
    }

    public static User userOf(Object user){

        if (user instanceof WrappedUser){
            return ((WrappedUser) user).getUserObj();
        } else if (user instanceof User) {
            return (User) user;
        }else {
            throw new IllegalArgumentException("Parameter passed to WrappedUser.userOf is not instanceof User");
        }
    }

    public int getId() {
        return userObj.getId();
    }

    public void setId(int id) {
        throw new IllegalArgumentException("Wrapped user is unmodifiable");
    }

    public String getName() {
        return userObj.getName();
    }

    public void setName(String name) {
        throw new IllegalArgumentException("Wrapped user is unmodifiable");
    }

    public String getEmail() {
        return userObj.getEmail();
    }

    public void setEmail(String email) {
        throw new IllegalArgumentException("Wrapped user is unmodifiable");
    }

    public String getPasswordHash() {
       return userObj.getPasswordHash();
    }

    public void setPasswordHash(String passwordHash) {
        throw new IllegalArgumentException("Wrapped user is unmodifiable");
    }

    public List<UsersRole> getUsersRoles() {
        return userObj.getUsersRoles();
    }

    public void setUsersRoles(List<UsersRole> usersRoles) {
        throw new IllegalArgumentException("Wrapped user is unmodifiable");
    }

    public Set<Role> getRoles() {
        return userObj.getRoles();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isUser() {
        return user;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isMaster() {
        return master;
    }

    public User getUserObj() {
        return userObj;
    }
}