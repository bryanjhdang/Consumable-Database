package ca.cmpt.cmpt213.a4.webappserver.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A class used to contain all information about a certain consumable.
 */
public class Consumable implements Comparable<Consumable> {
    protected int itemType;
    protected String name;
    protected String notes;
    protected double price;
    protected double measurement;
    protected LocalDate expiryDate;
    protected String expiryStatus;
    protected long id;

    public Consumable() {
        super();
    }

    public Consumable(int itemType, String name, String notes,
                      double price, double measurement, LocalDate expiryDate, long id) {
        this.itemType = itemType;
        this.name = name;
        this.notes = notes;
        this.price = price;
        this.measurement = measurement;
        this.expiryDate = expiryDate;
        this.id = id;
    }

    /**
     * Determine if the expiry date is before, after, or currently
     * the current date and set the expiryStatus String accordingly.
     */
    protected void findExpiryStatus() {
        long daysBetween = getDaysUntilExpiry();

        // Edit starting message
        final int FOOD_ITEM = 1;
        final int DRINK_ITEM = 2;
        if (itemType == FOOD_ITEM) {
            expiryStatus = "This food item ";
        } else if (itemType == DRINK_ITEM) {
            expiryStatus = "This drink item ";
        }

        if (daysBetween < 0) {
            expiryStatus = expiryStatus + "is expired for " + Math.abs(daysBetween) + " day(s).";
        } else if (daysBetween > 0) {
            expiryStatus = expiryStatus + "will expire in " + daysBetween + " day(s).";
        } else {
            expiryStatus = expiryStatus + "will expire today.";
        }
    }

    /**
     * Constraint double value to only make it go up to two decimals.
     *
     * @param value to alter
     * @return rounded value
     */
    protected double constrainDouble(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getItemTypeAsString() {
        final int FOOD_ITEM = 1;
        if (itemType == FOOD_ITEM) {
            return "Food";
        } else {
            return "Drink";
        }
    }

    public int getItemType() {
        return itemType;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public double getPrice() {
        return price;
    }

    public double getMeasurement() {
        return measurement;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long newId) {
        id = newId;
    }

    /**
     * Calculates the days between expiry date and the current day.
     *
     * @return days between expiry and current day
     */
    private long getDaysUntilExpiry() {
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, expiryDate);
    }

    @Override
    public String toString() {
        findExpiryStatus();

        return
                "Name: " + name + "\n" +
                        "Notes: " + notes + "\n" +
                        "Price: " + constrainDouble(price) + "\n" +
                        "Expiry date: " + expiryDate + "\n" +
                        expiryStatus + "\n";
    }

    @Override
    public int compareTo(Consumable otherConsumable) {
        return Long.compare(getDaysUntilExpiry(), otherConsumable.getDaysUntilExpiry());
    }
}
