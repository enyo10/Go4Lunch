package ch.enyoholali.openclassrooms.go4lunch.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.common.Period;

public interface DataFormatter {

    /**
     * @param fullName String
     *
     * <p>
     * Split last name and first name if exists
     */
    default String formatFullName(String fullName) {
        String[] firstName = fullName.split(" ");
        return firstName[0];
    }

    /**
     * This method to format ratting.
     * @param rating double
     * Convert 5 stars rating into 3
     */
    default float formatRating(double rating) {
        //DecimalFormat df = new DecimalFormat("#.#");
        rating = Math.round(rating * 3/5);
        //rating = Double.parseDouble(df.format(rating));
        return (float) rating;
    }

    /**
     * This method to format the address
     * @param address String
     * Take the first element in an address array (exclude zip code)
     */
    default String formatAddress(String address) {
        String[] newAddress = address.split(",");
        return newAddress[0];
    }

    /**
     * This method to
     * @param listPeriod List
     * @param openNow    boolean
     * Give opening time of a restaurant from place Api info
     */
    default String formatOpeningTime(boolean openNow, List<Period> listPeriod) {
        if (!openNow) {
            return "Closed";
        } else {
            if (listPeriod.size() == 7 || listPeriod.size() == 0) {
                return "Open 24/7";
            } else if (listPeriod.size() <= 6) {
                return "Open until " + listPeriod.get(dayOfWeek()).getClose().getTime() + "pm";
            }
        }
        return "Closed Soon";
    }

    /**
     * @param weekDayArray List
     * Give opening time of a restaurant from place Api info
     */
    default String formatWeekDayText(List<String> weekDayArray) {
        StringBuilder builder = new StringBuilder();
        if (weekDayArray.size() >=2) {
            String[] ot = weekDayArray.get(dayOfWeek()).split(" ");
            for (int i = 1, otLength = ot.length; i < otLength; i++) {
                String anOt = ot[i];
                builder.append(anOt).append(" ");
            }
        } else {
            builder.append("Closed");
        }

        return builder.toString().toLowerCase() ;
    }

    /**
     * Give the current day digit of the week
     */
    default Integer dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        //Day number of week (1 = Monday, ..., 7 = Sunday) != Place api 0 = Monday
        return calendar.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * @param s string
     * change marker id into integer id
     */
    // Change marker id into integer id
    default int convertStringIdIntoInteger(String s) {
        String tmp = s.substring(1);
        return Integer.valueOf(tmp) - 1;
    }

    /**
     * @param s String
     * Simply convert a string into an integer
     */
    default Integer convertStringIntoInteger(String s) {
        return Integer.valueOf(s);
    }

    /**
     * This method to compute distance between two restaurant.
     * @param deviceLat     device latitude
     * @param restaurantLat restaurant latitude
     * @param deviceLng     device longitude
     * @param restaurantLng restaurant longitude
     * compute the distance between a place and the device thanks the gps (note: accuracy less performable than metrics api)
     */
    // Find the computeDistance between two coordinates
    default String computeDistance(double deviceLat, double restaurantLat, double deviceLng, double restaurantLng) {

        final int R = 6371; // Radius of earth
        double latDistance = Math.toRadians(restaurantLat - deviceLat);
        double lonDistance = Math.toRadians(restaurantLng - deviceLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(deviceLat)) * Math.cos(Math.toRadians(restaurantLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0.0 - 0.0;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        //Rounded
        return Math.round(Math.sqrt(distance)) + "m";
    }

    /**
     * This method to format the date.
     * @param date date object
     * <p>
     * format timestamp into european date pattern
     */
    default String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

}
