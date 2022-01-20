package ca.cmpt213.a4.client.model;

import java.time.LocalDate;

/**
 * Class used to store information about a food consumable item.
 * Stores weight in grams as a measurement.
 */
public class FoodItem extends Consumable {
    public FoodItem(int itemType, String name, String notes,
                    double price, double weight, LocalDate expiryDate, long id) {
        super(itemType, name, notes, price, weight, expiryDate, id);
    }

    @Override
    public String toString() {
        findExpiryStatus();

        return
                "Name: " + name + "\n" +
                        "Notes: " + notes + "\n" +
                        "Price: " + constrainDouble(price) + "\n" +
                        "Weight: " + constrainDouble(measurement) + "\n" +
                        "Expiry date: " + expiryDate + "\n" +
                        expiryStatus + "\n";
    }
}
