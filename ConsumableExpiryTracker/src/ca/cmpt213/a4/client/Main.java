package ca.cmpt213.a4.client;

import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.view.ConsumableMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Entry point of main application, creates the main menu
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConsumableMenu();
            }
        });
    }
}
