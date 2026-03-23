package project20280.exercises;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WK3Test {
    @Test
    void testConvertToBinary() {
        assertEquals("10111", WK3.BaseConverter.convertToBinary(23));
        assertEquals("111001000000101011000010011101010110110001100010000000000000", WK3.BaseConverter.convertToBinary(1027010000000000000L));
    }
    //How would you extend this to handle different bases?
    // Just replace 2 with a parameter
    //
    // and bases greater than 9?
    // Digits above 9 are represented by letters: 10 -> A, 11 -> B, ... up to 35 -> Z.
    // When converting, we can use num bers '0'–'9' for 0–9, and letters 'A'–'Z' for values >= 10.
}
