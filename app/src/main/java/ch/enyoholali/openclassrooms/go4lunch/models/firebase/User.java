package ch.enyoholali.openclassrooms.go4lunch.models.firebase;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String firstname;
    private String lastname;
    private Boolean isMentor;
    private String restaurantId;
    private String restaurantName;
    private String email;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, String urlPicture,String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email=email;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsMentor() { return isMentor; }
    public String getRestaurantId(){
        return restaurantId;
    }
    public String getEmail(){return email;}
    public String getRestaurantName() { return restaurantName; }
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }


    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsMentor(Boolean mentor) { isMentor = mentor; }
    public void setRestaurantId(String restaurantId){this.restaurantId=restaurantId;}

    public void setEmail(String email){this.email=email;}

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }




}
