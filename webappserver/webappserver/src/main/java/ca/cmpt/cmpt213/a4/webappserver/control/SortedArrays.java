package ca.cmpt.cmpt213.a4.webappserver.control;

import ca.cmpt.cmpt213.a4.webappserver.model.Consumable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * A class used to return an ArrayList of Consumable's filtered
 * by expiry / non-expiry, extracted by its ArrayList parameter.
 */
public class SortedArrays {

    /**
     * Determine if a consumable is expired
     *
     * @param consumable Object to check expiry
     * @return true if consumable is expired, false otherwise
     */
    private static boolean isExpired(Consumable consumable) {
        LocalDate currentDateTime = LocalDate.now();
        LocalDate expiryDate = consumable.getExpiryDate();

        int compareValue = expiryDate.compareTo(currentDateTime);

        return (compareValue < 0);
    }

    /**
     * Determine if a consumable is expiring in 7 days
     *
     * @param consumable object to check expiry
     * @return true if consumable in 7 days or less, false otherwise
     */
    private static boolean isExpiredWithin7Days(Consumable consumable) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryLocalDate = consumable.getExpiryDate();
        long daysBetween = ChronoUnit.DAYS.between(currentDate, expiryLocalDate);

        return (daysBetween <= 7 && daysBetween >= 0);
    }

    /**
     * Creates ArrayList of expired consumables
     *
     * @param consumableArray ArrayList to be filtered
     * @return ArrayList of all consumables from parameter
     */
    public static ArrayList<Consumable> sortExpiry(ArrayList<Consumable> consumableArray) {
        ArrayList<Consumable> expiredList = new ArrayList<>();

        for (Consumable consumable : consumableArray) {
            if (isExpired(consumable)) {
                expiredList.add(consumable);
            }
        }
        return expiredList;
    }

    /**
     * Creates an ArrayList of non-expired consumables
     *
     * @param consumableArray ArrayList to be filtered
     * @return ArrayList of all non-expired consumables from parameter
     */
    public static ArrayList<Consumable> sortNonExpiry(ArrayList<Consumable> consumableArray) {
        ArrayList<Consumable> nonExpiredList = new ArrayList<>();

        for (Consumable consumable : consumableArray) {
            if (!isExpired(consumable)) {
                nonExpiredList.add(consumable);
            }
        }
        return nonExpiredList;
    }

    /**
     * Creates an ArrayList of consumables expiring in 7 days
     *
     * @param consumableArray ArrayList to be filtered
     * @return ArrayList of all consumables to be expired in 7 days from parameter
     */
    public static ArrayList<Consumable> sort7DayExpiry(ArrayList<Consumable> consumableArray) {
        ArrayList<Consumable> expire7DayList = new ArrayList<>();

        for (Consumable consumable : consumableArray) {
            if (isExpiredWithin7Days(consumable)) {
                expire7DayList.add(consumable);
            }
        }
        return expire7DayList;
    }
}
