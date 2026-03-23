package project20280.exercises;

import java.util.Arrays;

public class Wk1 {

    public static void q1() {
        int[] my_array = {25, 14, 56, 15, 36, 56, 77, 18, 29, 49};

        System.out.println(my_array);

        double sum = 0;
        for(int i = 0; i<= my_array.length-1; i++) sum += my_array[i];
        System.out.println(sum/my_array.length);
        double average = Arrays.stream(my_array).average().orElse(0);
        System.out.println(average);
        //double average = ...;
    }
    public static void main(String [] args) {
        q1();
    }
}

