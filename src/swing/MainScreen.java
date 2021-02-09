package swing;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class MainScreen extends JFrame {
    private static final Logger logger = LogManager.getLogger(MainScreen.class);
    static MainScreen mainScreen;
    static JPanel pieChartPanel = new JPanel();
    static JMenuBar menuBar = new JMenuBar();
    static JPanel rightPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel payments = new PaymentsListPanel();
    JButton addButton = new JButton("Add payment");

    void addAttributes(){
        this.setTitle("Expense Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 550);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setJMenuBar(menuBar);
    }

    MainScreen() {
        addAttributes();
        addMenu();
        addPanels();

        refreshPieChart("type");
        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    void addPanels(){
        setRightPanel();
        setLeftPanel();
        setBottomPanel();
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    void setRightPanel(){
        this.add(rightPanel, BorderLayout.EAST);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.add(pieChartPanel);
    }

    void setLeftPanel(){
        this.add(leftPanel, BorderLayout.WEST);
        leftPanel.add(payments);
    }

    void setBottomPanel(){
        this.add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.add(addButton);
        setFilterButtons();

        addButton.addActionListener(e -> new AddPayment());
    }

    void setFilterButtons(){
        JRadioButton shopFilter = createFilterButton("shop");
        JRadioButton typeFilter = createFilterButton("type");

        ButtonGroup filterButtonsGroup = new ButtonGroup();
        filterButtonsGroup.add(shopFilter);
        filterButtonsGroup.add(typeFilter);

        typeFilter.setSelected(true);

        bottomPanel.add(shopFilter);
        bottomPanel.add(typeFilter);
    }

    static void addMenu(){
        menuBar.add(createMenu("Edit", (char) KeyEvent.VK_E)).add(
                createMenuItem("Database", (char) KeyEvent.VK_A, e -> new databaseWindow())
        );

        menuBar.add(createMenu("Help", (char) KeyEvent.VK_H)).add(
                createMenuItem("About", (char) KeyEvent.VK_A, e -> new About()),
                createMenuItem("Exit", (char) KeyEvent.VK_ESCAPE, e -> System.exit(0))
        );
    }

    static JMenu createMenu(String text, char mnemonic){
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);

        return menu;
    }

    static JMenuItem createMenuItem(String text, char mnemonic, ActionListener e){
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(e);

        return menuItem;
    }

    static JRadioButton createFilterButton(String filter){
        JRadioButton radioButton = new JRadioButton(filter);
        radioButton.addActionListener(e ->refreshPieChart(filter));

        return radioButton;
    }

    static void refreshPieChart(String filter){
        pieChartPanel.removeAll();
        pieChartPanel.add(new PieChart(filter));
        pieChartPanel.revalidate();
        pieChartPanel.repaint();
        logger.info("PieChart Refreshed to "+ filter);
    }

    public static void main(String[] args) {
        createSplashScreen();
        mainScreen = new MainScreen();
    }

    static void createSplashScreen(){
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show(2000);
        splashScreen.hide();
    }
}
