package ca.cmpt.cmpt213.a4.webappserver.control;

import ca.cmpt.cmpt213.a4.webappserver.model.Consumable;
import ca.cmpt.cmpt213.a4.webappserver.model.ConsumableFactory;
import ca.cmpt.cmpt213.a4.webappserver.model.DrinkItem;
import ca.cmpt.cmpt213.a4.webappserver.model.FoodItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Manages all aspects related to modifying a list of Consumables, such as adding an item,
 * removing an item, and retrieving / returning a list of items.
 */
public class ConsumablesManager {
    private ArrayList<Consumable> itemsList;
    private static ConsumablesManager instance;

    private Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
            new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter jsonWriter,
                                  LocalDate LocalDate) throws IOException {
                    jsonWriter.value(LocalDate.toString());
                }

                @Override
                public LocalDate read(JsonReader jsonReader) throws IOException {
                    return LocalDate.parse(jsonReader.nextString());
                }
            }).create();

    public ConsumablesManager() {
        itemsList = ConsumablesTracker.getListFromFile();
    }

    /**
     * Get a list of all the items in the item list
     *
     * @return a Json String representation of the list
     */
    public String listAll() {
        return myGson.toJson(itemsList);
    }

    /**
     * Add a new food item to the list based on the Json String object provided
     *
     * @param food to add
     * @return a Json String representation of the new list
     */
    public String addFoodItem(@RequestBody String food) {
        Type typeConsumable = new TypeToken<FoodItem>() {
        }.getType();
        FoodItem newFoodItem = myGson.fromJson(food, typeConsumable);

        long nextId = ConsumablesManager.getValidId(itemsList);
        newFoodItem.setId(nextId);

        itemsList.add(newFoodItem);
        return myGson.toJson(itemsList);
    }

    /**
     * Add a new drink item to the list based on the Json String object provided
     *
     * @param drink to add
     * @return a Json String representation of the new list
     */
    public String addDrinkItem(@RequestBody String drink) {
        Type typeConsumable = new TypeToken<DrinkItem>() {
        }.getType();
        DrinkItem newDrinkItem = myGson.fromJson(drink, typeConsumable);

        long nextId = ConsumablesManager.getValidId(itemsList);
        newDrinkItem.setId(nextId);

        itemsList.add(newDrinkItem);
        return myGson.toJson(itemsList);
    }

    /**
     * Remove an item in the item list based on the given item id
     *
     * @param id of the item to remove
     * @return a Json String representation of the new list
     */
    public String removeItem(@PathVariable("id") long id) {
        Consumable itemToRemove = null;

        for (Consumable item : itemsList) {
            if (item.getId() == id) {
                itemToRemove = item;
            }
        }

        if (itemToRemove != null) {
            itemsList.remove(itemToRemove);
        } else {
            throw new IllegalArgumentException();
        }

        return myGson.toJson(itemsList);
    }

    /**
     * Get a list of all expired items
     *
     * @return a Json String representation of the list
     */
    public String listExpired() {
        ArrayList<Consumable> expiredItemList = SortedArrays.sortExpiry(itemsList);
        return myGson.toJson(expiredItemList);
    }

    /**
     * Get a list of all non-expired items
     *
     * @return a Json String representation of the list
     */
    public String listNonExpired() {
        ArrayList<Consumable> nonExpiredItemList = SortedArrays.sortNonExpiry(itemsList);
        return myGson.toJson(nonExpiredItemList);
    }

    /**
     * Get a list of all items expiring within 7 days
     *
     * @return a Json String representation of the list
     */
    public String listExpiringIn7Days() {
        ArrayList<Consumable> expiringIn7DaysList = SortedArrays.sort7DayExpiry(itemsList);
        return myGson.toJson(expiringIn7DaysList);
    }

    /**
     * Save the list of items to a Json file, called upon exit of application
     */
    public void save() {
        ConsumablesTracker.writeToFile(itemsList);
    }

    /**
     * Go through the list of items and returns an id that isn't in use
     */
    public static long getValidId(ArrayList<Consumable> itemsList) {
        long id = 1;
        boolean noDuplicateId = false;

        while (!noDuplicateId) {
            noDuplicateId = true;

            for (Consumable item : itemsList) {
                if (item.getId() == id) {
                    id++;
                    noDuplicateId = false;
                    break;
                }
            }
        }

        return id;
    }
}
