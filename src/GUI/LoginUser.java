package GUI;

public class LoginUser {

    private String username;
    private String password;
    private Role role;
    private String id;

    public LoginUser(String username, String password, Role role, String id) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getId() { return id; }
    
    @Override
    public String toString() {
        return username + " [" + role + "] (ID: " + id + ")";
    }
}
