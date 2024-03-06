package org.seokkalae.musicjan.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Validator {
    public static boolean isValidUrl(String value) {
        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
