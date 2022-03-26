package ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceDetails implements Comparable<PlaceDetails> {

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

   // Default constructor.
   public PlaceDetails(){

   }

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

    @Override
    public int compareTo(PlaceDetails o) {
        return this.getResult().getRating().compareTo(o.getResult().getRating());
    }
}