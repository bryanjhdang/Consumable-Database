package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ServerCalls;
import ca.cmpt213.a4.client.model.Consumable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Class representing the main menu of the application.
 * Able to view different item lists depending on expiry, and displays
 * the item list in a scrollable panel.
 * Provides the user the ability to add and remove items.
 */
public class ConsumableMenu extends JFrame implements ActionListener {
    private ArrayList<Consumable> currentItemList;

    private JFrame menuFrame = new JFrame();
    private JPanel listOptionsPanel = new JPanel();

    private JPanel itemContainerPanel = new JPanel();
    private JScrollPane itemPane;

    private final String ADD_ITEM_TITLE = "Add Item";

    private final String LIST_ALL = "All";
    private final String LIST_EXPIRED = "Expired";
    private final String LIST_NON_EXPIRED = "Not Expired";
    private final String LIST_EXPIRING_IN_7_DAYS = "Expiring in 7 Days";
    private String currentListStatus;

    private final int FRAME_WIDTH = 600;
    private final int FRAME_HEIGHT = 600;

    public ConsumableMenu() {
        menuFrame.setTitle("My Consumables Tracker");
        menuFrame.getContentPane().setLayout(new BoxLayout(menuFrame.getContentPane(), BoxLayout.Y_AXIS));

        currentListStatus = LIST_ALL;
        currentItemList = ServerCalls.listAll();

        ServerCalls.ping();
        saveDataOnWindowClose();

        setListOptionsButtons();
        setScrollableItemDisplay();
        setAddButton();

        menuFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        menuFrame.setVisible(true);
    }

    /**
     * Set all buttons for viewing the item lists of different expiry status'.
     */
    private void setListOptionsButtons() {
        listOptionsPanel.setLayout(new BoxLayout(listOptionsPanel, BoxLayout.X_AXIS));
        listOptionsPanel.add(Box.createRigidArea(new Dimension(30, 50)));

        setViewAllBtn();
        setViewExpiredBtn();
        setViewNonExpiredBtn();
        setViewExpiredIn7Btn();

        listOptionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        listOptionsPanel.setPreferredSize(new Dimension(400, 100));
        listOptionsPanel.setMaximumSize(new Dimension(400, 100));

        menuFrame.getContentPane().add(listOptionsPanel);
    }

    /**
     * Create button to view all items when clicked.
     */
    private void setViewAllBtn() {
        JButton viewAllBtn = new JButton(LIST_ALL);
        listOptionsPanel.add(viewAllBtn);
        viewAllBtn.addActionListener(this);
    }

    /**
     * Create button to view expired items when clicked.
     */
    private void setViewExpiredBtn() {
        JButton viewExpiredBtn = new JButton(LIST_EXPIRED);
        listOptionsPanel.add(viewExpiredBtn);
        viewExpiredBtn.addActionListener(this);
    }

    /**
     * Create button to view non-expired items when clicked.
     */
    private void setViewNonExpiredBtn() {
        JButton viewNonExpiredBtn = new JButton(LIST_NON_EXPIRED);
        listOptionsPanel.add(viewNonExpiredBtn);
        viewNonExpiredBtn.addActionListener(this);
    }

    /**
     * Create button to view items expiring within 7 days when clicked.
     */
    private void setViewExpiredIn7Btn() {
        JButton viewExpiredIn7Btn = new JButton(LIST_EXPIRING_IN_7_DAYS);
        listOptionsPanel.add(viewExpiredIn7Btn);
        viewExpiredIn7Btn.addActionListener(this);

    }

    /**
     * Create JScrollPane in order to display list of items in a scrollable view.
     */
    private void setScrollableItemDisplay() {
        itemPane = new JScrollPane();
        setItemContainerPanel();

        itemPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemPane.setPreferredSize(new Dimension(700, 600));
        itemPane.setMaximumSize(new Dimension(700, 600));

        itemPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        itemPane.setViewportView(itemContainerPanel);

        menuFrame.getContentPane().add(itemPane);
        itemPane.revalidate();
        itemPane.repaint();
    }

    /**
     * Encompassing panel holding all panels for individual items.
     */
    private void setItemContainerPanel() {
        itemContainerPanel.setLayout(new BoxLayout(itemContainerPanel, BoxLayout.Y_AXIS));
        itemContainerPanel.removeAll();

        // Check if list is empty, display message label if so
        if (currentItemList.size() == 0) {
            JLabel emptyLabel = getEmptyLabel();
            itemContainerPanel.add(emptyLabel);
        }

        // Create a panel for each item in the current item list
        for (int i = 0; i < currentItemList.size(); i++) {
            createItemPanel(i);
        }

        itemContainerPanel.revalidate();
        itemContainerPanel.repaint();
    }

    /**
     * Create a singular panel for one item in the item list.
     *
     * @param index corresponding to the item in the item list to create a panel
     */
    private void createItemPanel(int index) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setPreferredSize(new Dimension(550, 120));
        itemPanel.setMaximumSize(new Dimension(550, 120));
        itemPanel.add(Box.createRigidArea(new Dimension(100, 50)));

        // Add text for item
        String rawItemInfo = currentItemList.get(index).toString();
        String itemInfo = reformatItemInfo(rawItemInfo);
        JLabel itemInfoLabel = new JLabel(itemInfo);
        itemPanel.add(itemInfoLabel);

        // Add remove button for item
        setRemoveButton(itemPanel, index);

        // Add to overarching item display panel
        String itemType = currentItemList.get(index).getItemTypeAsString();
        itemPanel.setBorder(BorderFactory.createTitledBorder("Item " + (index + 1) + " - " + itemType));
        itemContainerPanel.add(itemPanel);
    }

    /**
     * Helper method to return a specific empty list label, depending on the current
     * item list being viewed.
     *
     * @return a JLabel to display the empty status
     */
    private JLabel getEmptyLabel() {
        switch (currentListStatus) {
            case LIST_EXPIRED -> {
                return new JLabel("No expired items to show.");
            }
            case LIST_NON_EXPIRED -> {
                return new JLabel("No non-expired items to show.");
            }
            case LIST_EXPIRING_IN_7_DAYS -> {
                return new JLabel("No items expiring in 7 days to show.");
            }
            default -> {
                return new JLabel("No items to show.");
            }
        }
    }

    /**
     * Format toString() String of Consumable class to a more readable format.
     * Obtained from: https://stackoverflow.com/questions/21321854/jpanel-to-display-large-amount-of-text
     *
     * @param itemInfo to reformat
     * @return reformatted String
     */
    private String reformatItemInfo(String itemInfo) {
        String reformattedItemInfo = itemInfo.replace("\n", "<br>");
        reformattedItemInfo = "<html><font size='3'>" + reformattedItemInfo + "</font></html>";
        return reformattedItemInfo;
    }

    /**
     * Creates a remove button that, when clicked, removes the item from the list
     * and refreshes the UI display accordingly.
     *
     * @param itemPanel for the singular panel to add a button to
     * @param index     for the item in the current list to be removed
     */
    private void setRemoveButton(JPanel itemPanel, int index) {
        JButton removeButton = new JButton("Remove");
        itemPanel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Consumable tempItem = currentItemList.get(index);
                currentItemList = ServerCalls.removeItem(tempItem);
                setItemContainerPanel();
            }
        });

    }

    /**
     * Create a button that, when clicked, pops up a dialog prompting to add a new item.
     */
    private void setAddButton() {
        JButton addItemButton = new JButton("Add Item");
        menuFrame.add(addItemButton);

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddConsumableDialog(menuFrame, ADD_ITEM_TITLE);
                updateList(currentListStatus);
                setItemContainerPanel();
            }
        });
    }

    /**
     * Detects if the window 'X' button is pressed. If so, save item list data into a json file.
     */
    private void saveDataOnWindowClose() {
        menuFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        menuFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerCalls.exit();
                menuFrame.dispose();
            }
        });
    }

    /**
     * List all items in the item list, regardless of expiry status.
     */
    private void listAllItems() {
        currentItemList = ServerCalls.listAll();
    }

    /**
     * List only expired items in the item list.
     */
    private void listExpiredItems() {
        currentItemList = ServerCalls.listExpired();
    }

    /**
     * List only non-expired items in the item list.
     */
    private void listNonExpiredItems() {
        currentItemList = ServerCalls.listNonExpired();
    }

    /**
     * List only items that are expiring within 7 days or less (but not yet expired)
     */
    private void listItemsExpiringIn7Days() {
        currentItemList = ServerCalls.listExpiringIn7Days();
    }

    /**
     * Updates the current list that is being shown on the screen,
     * depending on the expiry status the viewer has chosen.
     *
     * @param currentListStatus String to determine which list to show
     */
    private void updateList(String currentListStatus) {
        switch (currentListStatus) {
            case LIST_ALL -> listAllItems();
            case LIST_EXPIRED -> listExpiredItems();
            case LIST_NON_EXPIRED -> listNonExpiredItems();
            case LIST_EXPIRING_IN_7_DAYS -> listItemsExpiringIn7Days();
            default -> System.out.println("Something went wrong with viewing another list!");
        }
        setItemContainerPanel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String listChosen = e.getActionCommand();
        currentListStatus = listChosen;
        updateList(listChosen);
    }
}
