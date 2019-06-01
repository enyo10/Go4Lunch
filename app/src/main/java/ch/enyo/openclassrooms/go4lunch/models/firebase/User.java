package ch.enyo.openclassrooms.go4lunch.models.firebase;

import android.support.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private Boolean isMentor;
    private String restaurantId;
    private String restaurantName;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isMentor = false;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsMentor() { return isMentor; }
    public String getRestaurantId(){
        return restaurantId;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsMentor(Boolean mentor) { isMentor = mentor; }
    public void setRestaurantId(String restaurantId){this.restaurantId=restaurantId;}
    public String getRestaurantName() { return restaurantName; }


}
