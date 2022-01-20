package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.ConsumableFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that provides static functions to call the appropriate curl command, related
 * to handling an item list, such as adding items, removing items, and retrieving lists.
 */
public class ServerCalls {
    private static ArrayList<Consumable> tempItemList = new ArrayList<>();
    static Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
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

    /**
     * Returns a "ping" message in terminal on start-up
     */
    public static void ping() {
        voidGetMappingCall("ping");
    }

    /**
     * Returns a list of all items
     */
    public static ArrayList<Consumable> listAll() {
        return getList("listAll");
    }

    /**
     * Returns a list of expired items
     */
    public static ArrayList<Consumable> listExpired() {
        return getList("listExpired");
    }

    /**
     * Returns a list of non-expired items
     */
    public static ArrayList<Consumable> listNonExpired() {
        return getList("listNonExpired");
    }

    /**
     * Returns a list of items expiring in 7 days
     */
    public static ArrayList<Consumable> listExpiringIn7Days() {
        return getList("listExpiringIn7Days");
    }

    /**
     * Returns the corresponding list (all, expired, non-expired, 7 day expiry)
     *
     * @param listType corresponding to the type of list that the user wants
     * @return an ArrayList of Consumables with the proper items
     */
    private static ArrayList<Consumable> getList(String listType) {
        String serializedString = "";
        try {
            String command = "curl -H \"Content-Type: application/json\" -X GET localhost:8080/" + listType;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readIn;
            while ((readIn = reader.readLine()) != null) {
                serializedString = readIn;
            }
            Type itemArrayType = new TypeToken<ArrayList<Consumable>>() {
            }.getType();
            tempItemList = myGson.fromJson(serializedString, itemArrayType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return separateList(tempItemList);
    }

    /**
     * Provides an item as a String to add to the main item list
     *
     * @param itemAsJsonString to add to the curl command
     * @param itemType         of food or drink
     * @return a new ArrayList of Consumables containing the new item
     */
    public static ArrayList<Consumable> addItem(String itemAsJsonString, int itemType) {
        String serializedString = "";
        try {
            String itemTypeAsStr = convertItemTypeToStr(itemType);
            String command = "curl -H \"Content-Type: application/json\" -X POST -d " + itemAsJsonString
                    + " localhost:8080/addItem/" + itemTypeAsStr;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readIn;
            while ((readIn = reader.readLine()) != null) {
                serializedString = readIn;
            }
            Type itemArrayType = new TypeToken<ArrayList<Consumable>>() {
            }.getType();
            tempItemList = myGson.fromJson(serializedString, itemArrayType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return separateList(tempItemList);
    }

    /**
     * Removes an item from the list of Consumables
     *
     * @param item to remove
     * @return an ArrayList of Consumables without the item
     */
    public static ArrayList<Consumable> removeItem(Consumable item) {
        String serializedString = "";
        long itemId = item.getId();

        try {
            String command = "curl -H \"Content-Type: application/json\" -X POST localhost:8080/removeItem/"
                    + itemId;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readIn;
            while ((readIn = reader.readLine()) != null) {
                serializedString = readIn;
            }
            Type itemArrayType = new TypeToken<ArrayList<Consumable>>() {
            }.getType();
            tempItemList = myGson.fromJson(serializedString, itemArrayType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return separateList(tempItemList);
    }

    /**
     * Called upon exiting in order to save data to a Json file
     */
    public static void exit() {
        voidGetMappingCall("exit");
    }

    /**
     * Takes in a String parameter to provide the correct get request
     *
     * @param curlCommand to call
     */
    private static void voidGetMappingCall(String curlCommand) {
        try {
            String command = "curl -H \"Content-Type: application/json\" -X GET localhost:8080/" + curlCommand;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readIn = null;
            while ((readIn = reader.readLine()) != null) {
                System.out.println(readIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in several parameters to create a String representation of a Consumable
     *
     * @return a Json String Consumable object
     */
    public static String convertParametersToJsonString(int itemType, String name, String notes,
                                                       double price, double measurement, LocalDate expiryDate) {
        String jsonItem = "\"{";
        jsonItem = jsonItem.concat("\\\"itemType\\\":" + itemType + ",");
        jsonItem = jsonItem.concat("\\\"name\\\":\\\"" + name + "\\\",");
        jsonItem = jsonItem.concat("\\\"notes\\\":\\\"" + notes + "\\\",");
        jsonItem = jsonItem.concat("\\\"price\\\":" + constrainDouble(price) + ",");
        jsonItem = jsonItem.concat("\\\"measurement\\\":" + constrainDouble(measurement) + ",");
        jsonItem = jsonItem.concat("\\\"expiryDate\\\":" + expiryDate);
        jsonItem = jsonItem.concat("}\"");
        return jsonItem;
    }

    /**
     * Constrains a double to two decimal places
     *
     * @param value to constrain
     * @return constrained double
     */
    private static double constrainDouble(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Converts itemType to its corresponding String
     *
     * @param itemType to convert
     * @return "food" if 1, "drink" if 2
     */
    private static String convertItemTypeToStr(int itemType) {
        final int FOOD = 1;
        if (itemType == FOOD) {
            return "food";
        } else {
            return "drink";
        }
    }

    /**
     * Takes a list of Consumables and then separates it into FoodItem's and DrinkItem's
     *
     * @param itemList to separate
     * @return a list of distinct objects
     */
    private static ArrayList<Consumable> separateList(ArrayList<Consumable> itemList) {
        ArrayList<Consumable> tempList = new ArrayList<>();

        for (Consumable item : itemList) {
            int itemType = item.getItemType();
            String name = item.getName();
            String notes = item.getNotes();
            double price = item.getPrice();
            double measurement = item.getMeasurement();
            LocalDate expiryDate = item.getExpiryDate();
            long id = item.getId();

            Consumable newItem = ConsumableFactory.getInstance
                    (itemType, name, notes, price, measurement, expiryDate, id);
            tempList.add(newItem);
        }

        Collections.sort(tempList);
        return tempList;
    }

}
