package com.example.truyenapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {
    public static String formatDate(String dateString, String inputFormat, String outputFormat) {
        SimpleDateFormat dateInput = new SimpleDateFormat(inputFormat);
        SimpleDateFormat dateOutput = new SimpleDateFormat(outputFormat);

        try {
            Date date = dateInput.parse(dateString);
            return dateOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static double roundNumber(double number) {
        return Math.round(number * 10.0) / 10.0;
    }
}
