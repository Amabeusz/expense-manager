package swing;

import databaseConnection.Database;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class PaymentsListPanel extends JPanel {
    JPanel datePanel = new JPanel();
    JLabel header = new JLabel("Payments");
    static JPanel payments = new JPanel();
    static JDatePickerImpl dateFrom = datePicker();
    static JDatePickerImpl dateTo = datePicker();

    PaymentsListPanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addDatePanel();
        addHeader();
        this.add(payments);
        displayPayments();
    }

    void addDatePanel(){
        datePanel.setLayout(new FlowLayout());
        datePanel.add(dateFrom);
        datePanel.add(dateTo);

        this.add(datePanel);
    }

    static JDatePickerImpl datePicker(){
        UtilDateModel model = new UtilDateModel(new Date());
        JDatePanelImpl datePanelImpl = new JDatePanelImpl(model, new Properties());

        datePanelImpl.setPreferredSize(new Dimension(180,180));
        datePanelImpl.addActionListener(e -> displayPayments());

        return new JDatePickerImpl(datePanelImpl, new DateLabelFormatter());
    }

    void addHeader(){
        header.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(header);
    }

    public static void displayPayments(){
        try {
            payments.removeAll();

            JTable table = new JTable(buildTableModel(Database.getPayments(dateFrom.getJFormattedTextField().getText(), dateTo.getJFormattedTextField().getText())));
            JScrollPane tablePane = new JScrollPane(table);

            payments.add(tablePane);

            payments.revalidate();
            payments.repaint();
        } catch(SQLException e) { e.printStackTrace();}


    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }
}
