package org.example;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CollectionDispatcherTest {

    @Test
    public void ShouldReturnLength3(){
        //Checks if 3 stations are returned (if a station is added or removed from the database the test does not work anymore)
        //Arrange
        CollectionDispatcher collectionDispatcher = new CollectionDispatcher();

        //Act
        int length = collectionDispatcher.manageMessages("1");

        //Assert
        assertEquals(3,length);

    }
}
