package ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetails {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private PlaceDetailsResult result;
    @SerializedName("status")
    @Expose
    private String status;

   private String imageUrl;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public PlaceDetailsResult getResult() {
        return result;
    }

    public void setResult(PlaceDetailsResult result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}