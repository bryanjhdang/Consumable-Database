package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.ConsumableFactory;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Helper class to provide functions that checks validity for an item.
 * (ex: non-empty names, only allowing non-negative numbers).
 */
public class ItemValidityChecker {
    /**
     * Checks is the string is empty or contains only spaces
     */
    private static boolean isEmptyName(String name) {
        if (name.length() == 0) {
            return true;
        }
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all fields are completed and not empty
     *
     * @param name
     * @param price
     * @param measurement
     * @param expiryDate
     * @return true if all fields are filled, false otherwise
     */
    public static boolean allFieldsAreFilled(String name, String price,
                                             String measurement, LocalDate expiryDate) {
        if (isEmptyName(name)) {
            return false;
        }
        if (price.equals("") || measurement.equals("")) {
            return false;
        }
        if (expiryDate == null) {
            return false;
        }
        return true;
    }

    /**
     * Check if String parameters contain only numbers
     */
    public static boolean allNumbersAreValid(String price, String measurement) {
        try {
            Double.parseDouble(price);
            Double.parseDouble(measurement);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Check if values passed in are non-negative
     *
     * @param price       field value to check
     * @param measurement field value to check
     * @return true if values are 0 or greater, false otherwise
     */
    public static boolean allNumbersAreNonNegative(String price, String measurement) {
        double priceNum;
        double measurementNum;
        try {
            priceNum = Double.parseDouble(price);
            measurementNum = Double.parseDouble(measurement);
        } catch (NumberFormatException nfe) {
            return false;
        }

        if (priceNum < 0 || measurementNum < 0) {
            return false;
        }
        return true;
    }
}
