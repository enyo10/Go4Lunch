package ch.enyoholali.openclassrooms.go4lunch.utils;

import org.junit.Test;

import ch.enyoholali.openclassrooms.go4lunch.data.DataFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFormatterTest {
    // This class implements and inherit the default methods of DadaFormatter
    private class MyClass implements DataFormatter {

    }
   private MyClass mMyClass= new MyClass();

    @Test
    public void RattingTest(){
        double value =3.9;
        double value2=3.1;

        assertEquals(2,mMyClass.formatRating(value));
        assertEquals(2,mMyClass.formatRating(value2));

        }




   /* @Test
    public void dateReformatYYYYMMJJ_Unit_Test() {
        String originDate = "14/03/2019";         //Original date to reformat
        assertEquals("20190314", DateFormatter.dateFormatYYYYMMJJ(originDate));
    }

    @Test
    public void dateFormat_Unit_Test() {
        String originDate = "2019-03-14T11:38:10-04:00";         //Original date to reformat
        assertEquals("14/03/19", DateFormatter.formatDate(originDate));
    }


    @Test
    public void dateReformat_Unit_Test() {
        String originDate = "20190314";         //Original date to reformat
        assertEquals("14/03/19", DateFormatter.dateReformat(originDate));
    }
*/

}