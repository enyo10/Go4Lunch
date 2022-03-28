package ch.enyo.holali.go4lunch;

import org.junit.Test;

import static org.junit.Assert.*;

import ch.enyoholali.openclassrooms.go4lunch.BuildConfig;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void apikey_isCorrect(){
        String apikey =  BuildConfig.APIKEY;

       String APIKEY = "c527aa8fadcb58f1cccef75e3a64a3ae";
       assertEquals(apikey, APIKEY);
    }
}