package ca.cmpt.cmpt213.a4.webappserver.control;

import ca.cmpt.cmpt213.a4.webappserver.model.Consumable;
import ca.cmpt.cmpt213.a4.webappserver.model.ConsumableFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A class to read from and write to a json file
 */
public class ConsumablesTracker {
    private static Gson myGson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class,
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

    public ConsumablesTracker() {
    }

    /**
     * Use gson to read data from a json file into Consumable ArrayList
     */
    // https://attacomsian.com/blog/gson-read-json-file
    public static ArrayList<Consumable> getListFromFile() {
        ArrayList<Consumable> consumablesList = new ArrayList<>();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(".\\itemsList.json"));
            Type typeConsumable = new TypeToken<ArrayList<Consumable>>() {
            }.getType();
            ConsumableFactory factory = new ConsumableFactory();

            ArrayList<Consumable> tempItemList;
            tempItemList = myGson.fromJson(reader, typeConsumable);

            // Retrieve data from list to turn into appropriate subclass
            if (tempItemList != null) {
                for (int i = 0; i < tempItemList.size(); i++) {
                    Consumable item = tempItemList.get(i);
                    int itemType = item.getItemType();
                    String name = item.getName();
                    String notes = item.getNotes();
                    double price = item.getPrice();
                    double measurement = item.getMeasurement();
                    LocalDate expiryDate = item.getExpiryDate();
                    long id = item.getId();

                    Consumable subclassItem = factory
                            .getInstance(itemType, name, notes, price, measurement, expiryDate, id);
                    consumablesList.add(subclassItem);
                }
            }

            reader.close();
        } catch (IOException ioe) {
            System.out.println("Unable to read in consumable items from file.");
        }

        return consumablesList;
    }

    /**
     * Use gson to write to a json file from Consumable ArrayList
     */
    // https://attacomsian.com/blog/gson-write-json-file
    public static void writeToFile(ArrayList<Consumable> consumablesList) {
        try {
            Writer writer = new FileWriter(".\\itemsList.json");
            myGson.toJson(consumablesList, writer);
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Unable to save consumable items to file.");
        }
    }
}