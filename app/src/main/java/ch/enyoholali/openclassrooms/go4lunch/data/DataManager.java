package ch.enyoholali.openclassrooms.go4lunch.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

/**
 * This class is a helper class, that help us to convert a list of place details to json string an
 * retrieve back.
 */
public class DataManager {

    /**
     * @param object,
     *       the object to transform.
     * @return String,
     *        the string value to return.
     */
    public  static String objectToJson(Object object){
        Gson json =new Gson();
        return json.toJson(object);
    }

    /**
     *
     * @param json,
     *        A string object to transform to game ArrayList
     * @return ArrayList
     *        The array list of placeDetails.
     */
    public static List<PlaceDetails> jsonToPlaceDetailsList(String json){
        Gson gson=new Gson();
        Type founderListType = new TypeToken<ArrayList<PlaceDetails>>(){}.getType();
        return gson.fromJson(json, founderListType);
    }
}
