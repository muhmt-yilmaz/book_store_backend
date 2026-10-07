package com.project.book_store.utils;

import java.time.LocalTime;

public class TimeControl {

    public static boolean check(LocalTime start, LocalTime stop){
        return start.isAfter(stop) || start.equals(stop);
    }
}
