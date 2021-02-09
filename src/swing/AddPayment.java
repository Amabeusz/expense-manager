package swing;

import databaseConnection.Database;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Properties;

public class AddPayment extends JDialog {
    static JPanel panel = new JPanel();
    static String[] editableColumns;
    private static final Logger logger = LogManager.getLogger(AddPayment.class);

    AddPayment(){
        editableColumns = getEditableColumnsNames();

        this.setLocationRelativeTo(null);
        this.setContentPane(panel);
        this.setSize(200,350);
        this.setVisible(true);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        panel.removeAll();

        addInputFields();
        addSubmitButton();

        panel.revalidate();
        panel.repaint();
    }

    public static void refresh(){
        panel.removeAll();
        addInputFields();
        addSubmitButton();
        panel.revalidate();
        panel.repaint();
    }

    static private String[] getEditableColumnsNames(){
        String columns = Database.getColumnsNames("payment");
        columns = columns.replace("user_id", " ").replaceFirst("id", " ").replaceAll(" ", "");
        return columns.substring(1, columns.length()-1).split("[|]");
    }

    static void addInputFields(){
        for(String column : getEditableColumnsNames()) {
            Component input = null;

            if (column.equals("type_id") || column.equals("shop_id")) {
                JComboBox comboBox = new JComboBox(Database.getRecords(column.substring(0, column.length() - 3), "name", ""));

                comboBox.addItem("Dodaj");

                column = column.substring(0,column.length()-3);
                String finalColumn = column;
                comboBox.addActionListener(e ->{
                    if(comboBox.getSelectedItem().equals("Dodaj")){
                        new AddPaymentAttribute(finalColumn);
                    }
                });

                input = comboBox;
            } else if (column.equals("date")) {
                UtilDateModel model = new UtilDateModel(new Date());
                JDatePanelImpl datePanel = new JDatePanelImpl(model, new Properties());

                input = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            } else {
                if(column.equals("value")) {
                    NumberFormat longFormat = NumberFormat.getIntegerInstance();
                    longFormat.setGroupingUsed(false);

                    NumberFormatter numberFormatter = new NumberFormatter(longFormat);

                    numberFormatter.setValueClass(Long.class);
                    numberFormatter.setAllowsInvalid(false);

                    input = new JFormattedTextField(numberFormatter);
                } else {
                    input = new JTextField();
                }
            }

            JLabel label = new JLabel(column);
            label.setLabelFor(input);

            panel.add(label);
            panel.add(input);

            panel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
    }

    static void addSubmitButton(){
        JButton button = new JButton("submit");

        button.addActionListener(e ->{
            String columns = "";
            String values = "";
            String label = "";

            for(String column : editableColumns){
                columns += column + ",";
            }

            for(Component c : panel.getComponents()) {

                if (c instanceof JTextField) {
                    String value = ((JTextField) c).getText();
                    values += "'" + value + "',";
                }

                if (c instanceof JDatePickerImpl) {
                    String date = ((JDatePickerImpl) c).getJFormattedTextField().getText();
                    values += "'" + date + "',";
                }

                if (c instanceof JLabel) {
                    JLabel com = (JLabel) c;
                    label = com.getText();
                }

                if (c instanceof JComboBox) {
                    String id = ((JComboBox) c).getSelectedItem().toString();
                    String table = label;
                    String column = "id";
                    String condition = String.format("WHERE name='%s'", id.trim());
                    String value = Database.getRecords(table, column, condition)[0];

                    values += "'" + value + "',";
                }

            }

            String query = String.format("INSERT INTO %s(%s) VALUES (%s)", "payment", columns.substring(0,columns.length()-1), values.substring(0,values.length()-1));
            Database.executeUpdate(query);
            MainScreen.refreshPieChart("type");
            PaymentsListPanel.displayPayments();
            logger.info("Payment added");

        });

        panel.add(button);
    }
}
