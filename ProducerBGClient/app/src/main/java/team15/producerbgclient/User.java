package team15.producerbgclient;

/**
 * Created by veso on 1/8/2016.
 */
public class User {
    private  String username;
    private  String password;
    private  String email;
    private  String token;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }
}
