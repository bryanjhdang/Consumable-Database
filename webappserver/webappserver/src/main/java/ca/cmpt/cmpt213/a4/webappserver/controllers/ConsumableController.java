package ca.cmpt.cmpt213.a4.webappserver.controllers;

import ca.cmpt.cmpt213.a4.webappserver.control.ConsumablesManager;
import ca.cmpt.cmpt213.a4.webappserver.control.ConsumablesTracker;
import ca.cmpt.cmpt213.a4.webappserver.control.SortedArrays;
import ca.cmpt.cmpt213.a4.webappserver.model.Consumable;
import ca.cmpt.cmpt213.a4.webappserver.model.DrinkItem;
import ca.cmpt.cmpt213.a4.webappserver.model.FoodItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
public class ConsumableController {
    private ConsumablesManager consumablesManager = new ConsumablesManager();

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

    @GetMapping("/ping")
    public String getPing() {
        return "System is up!";
    }

    @GetMapping("/listAll")
    public String listAllItems() {
        return consumablesManager.listAll();
    }

    @PostMapping("/addItem/food")
    @ResponseStatus(HttpStatus.CREATED)
    public String addFoodItem(@RequestBody String food) {
        return consumablesManager.addFoodItem(food);
    }

    @PostMapping("/addItem/drink")
    @ResponseStatus(HttpStatus.CREATED)
    public String addDrinkItem(@RequestBody String drink) {
        return consumablesManager.addDrinkItem(drink);
    }

    @PostMapping("/removeItem/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String removeItem(@PathVariable("id") long id) {
        return consumablesManager.removeItem(id);
    }

    @GetMapping("/listExpired")
    public String listExpiredItems() {
        return consumablesManager.listExpired();
    }

    @GetMapping("/listNonExpired")
    public String listNonExpiredItems() {
        return consumablesManager.listNonExpired();
    }

    @GetMapping("/listExpiringIn7Days")
    public String listExpiringIn7DaysItems() {
        return consumablesManager.listExpiringIn7Days();
    }

    @GetMapping("/exit")
    public void exitProgram() {
        consumablesManager.save();
    }
}
