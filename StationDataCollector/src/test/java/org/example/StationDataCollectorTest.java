package org.example;

import org.example.database.Charge;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StationDataCollectorTest {

    @Test
    public void shouldReturnCountKwh(){
        //Tests if the stationDataCollector adds-up the kwhs right and sends the right message with mock data (should still work if database is changed)
        // Arrange
        StationCollector stationCollector = new StationCollector();

        // Überladen der getChargeList-Methode, um die gewünschten Daten zurückzugeben
        StationCollector mockedStationCollector = new StationCollector() {
            @Override
            public List<Charge> getChargeList(String customerId, String db_url) {
                return List.of(
                        new Charge(1, 1.0, 1),
                        new Charge(1, 1.0, 1),
                        new Charge(1, 1.0, 1),
                        new Charge(1, 1.0, 1)
                );
            }
        };

        // Verwendung des überladenen StationCollector-Objekts
        StationCollector stationCollector1 = mockedStationCollector;

        // Act
        String finalmessage = stationCollector1.getCustomerData("1;1;localhost:30011");

        // Assert
        assertEquals("1;4.0", finalmessage);
    }

    @Test
    public void shouldReturnErrorMessage(){
        //Tests if when a wrong message is sent the right error is returned
        //Arrange
        StationCollector stationCollector = new StationCollector();

        //Act
        String finalmessage = stationCollector.getCustomerData("Test-Test");

        //Assert
        assertEquals("Error: Wrong messaging format", finalmessage);
    }

    @Test
    public void shouldReturnfinalMessage(){
        //Test if right message is returned (if their are changes in the database this test wont work anymore)
        //Arrange
        StationCollector stationCollector1 = new StationCollector();

        //Act
        String finalmessage = stationCollector1.getCustomerData("1;1;localhost:30011");

        //Assert
        assertEquals("1;71.1", finalmessage);
    }

    @Test
    public void shouldReturnEmptyList(){
        //Test if a wrong DatabaseUrl or customerID is sent if a empty List is returnted
        List<Charge> charges = new LinkedList<>();

        //Arrange
        StationCollector stationCollector = new StationCollector();

        //Act
        List<Charge> charges2 = stationCollector.getChargeList("1", "Test");

        //Assert
        assertEquals(charges,charges2);
    }

    @Test
    public void shouldReturnCharges(){
        //Test if the right data is gathered from database (if database is changed the test wont work anymore)
        //Arrange
        StationCollector stationCollector = new StationCollector();
        List<Charge> charges = new LinkedList<>();
        charges.add(new Charge(2, 10.8, 1));
        charges.add(new Charge(4, 10.6, 1));
        charges.add(new Charge(11, 49.7, 1));

        //Act
        List<Charge> charges2 = stationCollector.getChargeList("1", "localhost:30011");


        //Assert
        assertEquals(charges.get(2).getKwh(),charges2.get(2).getKwh());
    }
}
