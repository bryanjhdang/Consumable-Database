package ca.cmpt.cmpt213.a4.webappserver.model;

import java.time.LocalDate;

/**
 * Class used to store information about a drink consumable item.
 * Stores volume in millilitres as a measurement.
 */
public class DrinkItem extends Consumable {
    public DrinkItem(int itemType, String name, String notes,
                     double price, double volume, LocalDate expiryDate, long id) {
        super(itemType, name, notes, price, volume, expiryDate, id);
    }

    @Override
    public String toString() {
        findExpiryStatus();

        return
                "Name: " + name + "\n" +
                        "Notes: " + notes + "\n" +
                        "Price: " + constrainDouble(price) + "\n" +
                        "Volume: " + constrainDouble(measurement) + "\n" +
                        "Expiry date: " + expiryDate + "\n" +
                        expiryStatus + "\n";
    }
}
