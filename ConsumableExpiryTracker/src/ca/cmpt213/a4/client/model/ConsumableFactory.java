package ca.cmpt213.a4.client.model;

import java.time.LocalDate;

/**
 * Creates a Factory to generate a subclass depending on the parameter.
 * Can generate either a food or drink consumable.
 */
public class ConsumableFactory {

    /**
     * "getInstance" method used to generate different consumable items
     *
     * @return Consumable subclass
     */
    public static Consumable getInstance(int itemType, String name, String notes, double price,
                                         double measurement, LocalDate expiryDate, long id) {
        final int FOOD_ITEM = 1;
        final int DRINK_ITEM = 2;

        if (itemType == FOOD_ITEM) {
            return new FoodItem(itemType, name, notes, price, measurement, expiryDate, id);
        } else if (itemType == DRINK_ITEM) {
            return new DrinkItem(itemType, name, notes, price, measurement, expiryDate, id);
        }

        return null;
    }
}
