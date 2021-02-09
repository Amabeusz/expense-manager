package swing;

import databaseConnection.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class databaseWindow extends JFrame{
    private JPanel panel;
    private JPanel resultsPanel;
    private JPanel checkBoxes;
    private JComboBox tablesListComboBox;
    private JSpinner idSpinner;
    private JPanel comboBoxPanel;
    private JButton deleteButton;
    private JPanel databaseOptions;
    private JPanel addOptions;
    private JPanel filterBar;
    private JButton selectAll;
    private JPanel deletePanel;
    private JPanel topBarPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel lastOpertionLabel;
    private ArrayList<String> selectedColumns;

    databaseWindow(){
        selectedColumns = new ArrayList<>();

        this.setContentPane(panel);
        this.setSize(700,300);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        resultsPanel.setLayout(new BorderLayout());

        createComboBox();
        createCheckBoxes();
        createAddRecordInputs(Database.getColumnsNames(String.valueOf(tablesListComboBox.getSelectedItem())).split("[|]"));
        refreshPanel();

        tablesListComboBox.addActionListener (e -> {
            selectedColumns.clear();
            resultsPanel.removeAll();
            createCheckBoxes();
            createAddRecordInputs(Database.getColumnsNames(String.valueOf(tablesListComboBox.getSelectedItem())).split("[|]"));
            refreshPanel();
        });

        deleteButton.addActionListener(e -> {
            String query = String.format("DELETE FROM %s WHERE id=%s", tablesListComboBox.getSelectedItem().toString(), idSpinner.getValue());
            Database.executeUpdate(query);
            lastOpertionLabel.setText(query);
            fillTable();
        });

        selectAll.addActionListener(e -> {
            for(Component c : checkBoxes.getComponents()) {
                Checkbox check = (Checkbox) c;
                check.setState(!check.getState());
                if(check.getState()) {
                    selectedColumns.add(check.getLabel());
                } else {
                    selectedColumns.remove(check.getLabel());
                }
            }
            fillTable();
            lastOpertionLabel.setText("SELECT");
        });

        if (Database.getConnectionStatus()) {
            statusLabel.setText("connection up");
            statusLabel.setIcon(new ImageIcon("img/green_dot.png"));
        } else {
            statusLabel.setText("connection down");
            statusLabel.setIcon(new ImageIcon("img/red_dot.png"));
            Database.connectDatabase();
        }
    }

    void createComboBox(){
        tablesListComboBox = new JComboBox(Database.getTablesNames());
        comboBoxPanel.add(tablesListComboBox);
    }

    void createCheckBoxes(){
        checkBoxes.removeAll();

        for(String s : Database.getColumnsNames(String.valueOf(tablesListComboBox.getSelectedItem())).split("[|]")){
            checkBoxes.add(new Checkbox(s.trim()));
        }

        addListener();
    }

    //checkboxes listener
    void addListener(){
        for(Component c : checkBoxes.getComponents()){
            Checkbox check = (Checkbox) c;
            check.addItemListener(e -> {
                if(check.getState()) {
                    selectedColumns.add(check.getLabel());
                } else {
                    selectedColumns.remove(check.getLabel());
                }
                lastOpertionLabel.setText("SELECT " + tablesListComboBox.getSelectedItem());
                fillTable();
            });
        }
    }

    void createAddRecordInputs(String[] columns){
        addOptions.removeAll();

        for(String s : columns){
            if(!s.equals("      id")) {
                JTextArea inputValue = new JTextArea(s.trim());
                inputValue.setPreferredSize(new Dimension(50, 20));

                inputValue.addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent e) {
                        inputValue.setText("");
                    }

                    public void focusLost(FocusEvent e) {
                        if (inputValue.getText().equals("")) {
                            inputValue.setText(s);
                        }
                        System.out.println(inputValue.getText());
                    }
                });

                addOptions.add(inputValue);
            }
        }

        JButton addButton = new JButton("add");
        addButton.addActionListener(e ->{
            String query = "INSERT INTO " + tablesListComboBox.getSelectedItem().toString() + " (";

            for(String s : columns)
                if(!s.trim().equals("id"))
                    query += s.trim() + ", ";

            query = query.substring(0, query.length()-2) + ") VALUES (";

            Component[] c = addOptions.getComponents();

            for(int i=0; i<c.length; i++) {
                if (c[i] instanceof JTextArea) {
                    String input = ((JTextArea) c[i]).getText();
                    query += "'" + input + "', ";
                }
            }
            query = query.substring(0, query.length()-2) + ")";

            System.out.println(query);

            try {
                Database.conn.createStatement().executeUpdate(query);
            }catch (SQLException ex){ex.printStackTrace();}

            lastOpertionLabel.setText("ADD RECORD");
            fillTable();
        });
        addOptions.add(addButton);
    }

   void fillTable() {
       resultsPanel.removeAll();

       try {
           String query = "select * from " + tablesListComboBox.getSelectedItem().toString();
           ResultSet rs = Database.conn.createStatement().executeQuery(query);
           JTable table = new JTable(buildTableModel(rs));

           JScrollPane tablePane = new JScrollPane(table);
           resultsPanel.add(tablePane, BorderLayout.CENTER);

       }catch (SQLException e){}
       refreshPanel();
   }

    public DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        Vector<String> columnNames = new Vector<>(selectedColumns);

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (String s : selectedColumns) {
                vector.add(rs.getObject(s));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames){
            @Override
            public String getColumnName(int column) {
                return selectedColumns.get(column);
            }
        };
    }

    void refreshPanel(){
        panel.revalidate();
        panel.repaint();
    }
}
