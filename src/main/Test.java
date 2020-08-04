package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
//        String time = "09:59";
//        int hour;
//        int minutes;
//        int seconds;
//
//        System.out.println("Initial string time: " + time);
//        String[] timeArray = time.split(":");
//        hour = Integer.parseInt(timeArray[0]) * 3600;
//        System.out.println("Hour in seconds: " + hour);
//        minutes = Integer.parseInt(timeArray[1]) * 60;
//        System.out.println("Minute in seconds: " + minutes);
//        seconds = hour +  minutes;
//        System.out.println("Time in seconds: " + seconds);
//
//        hour = seconds / 3600;
//        System.out.println("Second in hours: " + hour);
//        minutes = (seconds % 3600) / 60;
//        System.out.println("Second in minutes: " + minutes);
//
//        time = hour + ":" + minutes;
//        System.out.println("Second as time: " + time);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }
}