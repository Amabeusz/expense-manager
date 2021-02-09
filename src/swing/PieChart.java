package swing;

import databaseConnection.Database;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PieChart extends JPanel {
    static JFreeChart chart;
    static String filter;

    public PieChart(String filter) {
        this.filter = filter;
        this.add(createPieChart());
    }

    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset( );
        String query = String.format("SELECT p.value, %s.name FROM payment p, %s WHERE p.%s_id = %s.id", filter, filter, filter, filter);
        try {
            System.out.println(query);
            ResultSet rs = Database.conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String payment = "";
                for (String s : new String[]{"name", "value"}) {
                    payment += " " + rs.getObject(s);
                }
                String[] p = payment.trim().split(" ");
                dataset.setValue(p[0], Double.valueOf(p[1]));
            }
        }catch (SQLException e) {}

        return dataset;
    }

    public static JPanel createPieChart() {
        chart = createChart(createDataset() );
        ChartPanel chartPanel = new ChartPanel( chart );

        return chartPanel;
    }

    private static JFreeChart createChart(PieDataset dataset ) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Expenses",
                dataset,
                true,
                true,
                false);

        return chart;
    }
}
