package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ItemValidityChecker;
import ca.cmpt213.a4.client.control.ServerCalls;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/**
 * Creates a dialog box that opens when you click on "Add Item" from the menu.
 * Contains several fields that must be filled properly, and if so, adds a new
 * Consumable to the primary item list.
 */
public class AddConsumableDialog extends JDialog implements ActionListener {
    private JPanel itemFieldsPanel;

    private JComboBox itemTypeList;
    private JTextField nameTextField;
    private JTextField notesTextField;
    private JTextField priceTextField;
    private JTextField measurementTextField;
    private JLabel measurementLabel;
    private DatePicker expiryDatePicker;

    private final int FOOD_ITEM = 1;
    private final int DRINK_ITEM = 2;
    private String[] itemTypeOptions = {"Food", "Drink"};
    private final String CREATE_BUTTON_TEXT = "Create";
    private final String CANCEL_BUTTON_TEXT = "Cancel";

    private final int DIALOG_WIDTH = 500;
    private final int DIALOG_HEIGHT = 350;
    private final int WIDTH_OFFSET = 20;
    private final int HEIGHT_OFFSET = 40;
    private final int INPUT_WIDTH = 250;
    private final int INPUT_HEIGHT = 30;

    public AddConsumableDialog(JFrame frame, String title) {
        super(frame, title);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setModal(true);

        initializeAllTextFields();
        setAllItemFields();
        setConfirmationButtons();

        setVisible(true);
    }

    /**
     * Initialize all the JTextFields for item fields requiring a written input.
     * Includes name, notes, price, and measurement (weight / volume).
     */
    private void initializeAllTextFields() {
        nameTextField = new JTextField();
        notesTextField = new JTextField();
        priceTextField = new JTextField();
        measurementTextField = new JTextField();

        nameTextField.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        notesTextField.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        priceTextField.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        measurementTextField.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
    }

    /**
     * Calling method to set all the fields required to create a new item.
     */
    private void setAllItemFields() {
        itemFieldsPanel = new JPanel();
        itemFieldsPanel.setLayout(new BoxLayout(itemFieldsPanel, BoxLayout.Y_AXIS));

        setItemTypeField();
        setItemField("Name:", nameTextField);
        setItemField("Notes:", notesTextField);
        setItemField("Price:", priceTextField);
        setMeasurementField();
        setItemExpiryDateField();

        itemFieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(itemFieldsPanel);
    }

    /**
     * Set the field for entering item type, between food or drink.
     */
    private void setItemTypeField() {
        JPanel itemTypePanel = new JPanel();
        itemTypePanel.setLayout(new BoxLayout(itemTypePanel, BoxLayout.X_AXIS));
        JLabel itemTypeLabel = new JLabel("Type:");

        itemTypeList = new JComboBox(itemTypeOptions);
        itemTypeList.setSelectedIndex(0);
        itemTypeList.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        itemTypeList.addActionListener(this);

        itemTypePanel.add(itemTypeLabel);
        itemTypePanel.add(Box.createRigidArea(new Dimension(WIDTH_OFFSET, HEIGHT_OFFSET)));
        itemTypePanel.add(itemTypeList);
        itemTypePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        itemFieldsPanel.add(itemTypePanel);
    }

    /**
     * Set individual field for item information requiring a TextField.
     *
     * @param fieldName for the unique information label
     */
    private void setItemField(String fieldName, JTextField textField) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        JLabel fieldLabel = new JLabel(fieldName);

        fieldPanel.add(fieldLabel);
        fieldPanel.add(Box.createRigidArea(new Dimension(WIDTH_OFFSET, HEIGHT_OFFSET)));
        fieldPanel.add(textField);
        fieldPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        itemFieldsPanel.add(fieldPanel);
    }

    /**
     * Set field for volume or weight depending on if item type is a
     * drink or food, respectively.
     */
    private void setMeasurementField() {
        JPanel measurementPanel = new JPanel();
        measurementPanel.setLayout(new BoxLayout(measurementPanel, BoxLayout.X_AXIS));
        measurementLabel = new JLabel("Weight:");

        measurementPanel.add(measurementLabel);
        measurementPanel.add(Box.createRigidArea(new Dimension(WIDTH_OFFSET, HEIGHT_OFFSET)));
        measurementPanel.add(measurementTextField);
        measurementPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        itemFieldsPanel.add(measurementPanel);
    }

    /**
     * Set the field for entering expiry date.
     * Implemented using https://github.com/LGoodDatePicker/LGoodDatePicker
     */
    private void setItemExpiryDateField() {
        JPanel expiryPanel = new JPanel();
        expiryPanel.setLayout(new BoxLayout(expiryPanel, BoxLayout.X_AXIS));
        JLabel expiryLabel = new JLabel("Expiry date:");

        DatePickerSettings settings = new DatePickerSettings();
        settings.setAllowKeyboardEditing(false);
        settings.setFormatForDatesCommonEra("yyyy/MM/dd");
        settings.setFormatForDatesBeforeCommonEra("uuuu/MM/dd");
        expiryDatePicker = new DatePicker(settings);
        expiryDatePicker.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));

        expiryPanel.add(expiryLabel);
        expiryPanel.add(Box.createRigidArea(new Dimension(WIDTH_OFFSET, HEIGHT_OFFSET)));
        expiryPanel.add(expiryDatePicker);
        expiryPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        itemFieldsPanel.add(expiryPanel);
    }

    /**
     * Set confirmation buttons to check if the user would like to create or
     * cancel their item.
     */
    private void setConfirmationButtons() {
        JPanel buttonPanel = new JPanel();

        JButton createButton = new JButton(CREATE_BUTTON_TEXT);
        createButton.addActionListener(this);

        JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelButton.addActionListener(this);

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel);
    }

    /**
     * Takes in valid fields from user input and converts it into a Consumable,
     * adding it to the list of items.
     */
    private void addItemToList() {
        String itemTypeStr = itemTypeList.getSelectedItem().toString();
        int itemType = 0;
        final int IDX_OFFSET = 1;
        if (itemTypeStr.equals(itemTypeOptions[FOOD_ITEM - IDX_OFFSET])) {
            itemType = FOOD_ITEM;
        } else if (itemTypeStr.equals(itemTypeOptions[DRINK_ITEM - IDX_OFFSET])) {
            itemType = DRINK_ITEM;
        }

        String name = nameTextField.getText();
        String notes = notesTextField.getText();
        double price = Double.parseDouble(priceTextField.getText());
        double measurement = Double.parseDouble(measurementTextField.getText());
        LocalDate expiryDate = expiryDatePicker.getDate();

        String itemAsJsonStr = ServerCalls.convertParametersToJsonString
                (itemType, name, notes, price, measurement, expiryDate);
        ServerCalls.addItem(itemAsJsonStr, itemType);
    }

    /**
     * Checks all required fields and if the specified input is valid.
     * This includes empty fields, empty names, and negative numbers.
     */
    private boolean allInputsAreValid() {
        String name = nameTextField.getText();
        String price = priceTextField.getText();
        String measurement = measurementTextField.getText();
        LocalDate expiryDate = expiryDatePicker.getDate();

        // Check if any fields are empty
        if (!ItemValidityChecker.allFieldsAreFilled(name, price, measurement, expiryDate)) {
            JOptionPane.showMessageDialog(null,
                    "Some fields are missing!",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        // Check if numbers fields are numbers
        if (!ItemValidityChecker.allNumbersAreValid(price, measurement)) {
            JOptionPane.showMessageDialog(null,
                    "Please input numbers for price and weight / volume!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if number fields are non-negative
        if (!ItemValidityChecker.allNumbersAreNonNegative(price, measurement)) {
            JOptionPane.showMessageDialog(null,
                    "Values for price and weight / volume must be non-negative!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Detect button clicked to either create a new item, or cancel item selection.
     *
     * @param buttonClicked to check which button was pressed
     */
    private void handleButtonClicked(String buttonClicked) {
        if (buttonClicked.equals(CREATE_BUTTON_TEXT)) {
            if (allInputsAreValid()) {
                addItemToList();
                dispose();
            }
        }

        if (buttonClicked.equals(CANCEL_BUTTON_TEXT)) {
            dispose();
        }
    }


    /**
     * Detect which item type is currently selected and edit the field
     * for measurement accordingly.
     * If item type is food, display weight
     * If item type is drink, display volume
     *
     * @param itemTypeSelected
     */
    private void handleComboBoxInput(String itemTypeSelected) {
        if (itemTypeSelected.equals("Food")) {
            measurementLabel.setText("Weight:");
        } else {
            measurementLabel.setText("Volume:");
        }
        measurementLabel.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof JButton) {
            String buttonClicked = e.getActionCommand();
            handleButtonClicked(buttonClicked);
        }

        if (source instanceof JComboBox) {
            JComboBox comboBox = (JComboBox) e.getSource();
            String itemTypeSelected = (String) comboBox.getSelectedItem();
            handleComboBoxInput(itemTypeSelected);
        }
    }
}
