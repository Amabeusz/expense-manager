package swing;

import databaseConnection.Database;

import javax.swing.*;
import java.awt.*;

public class AddPaymentAttribute extends JDialog {
    JPanel panel = new JPanel();
    JTextField input =  new JTextField();
    String label;

    AddPaymentAttribute(String label){
        this.label = label;

        this.setContentPane(panel);
        this.setSize(200,120);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addLabel();
        addTextInput();
        addSubmitButton();
    }

    void addLabel(){
        panel.add(new JLabel(label));
    }


    void addTextInput(){
        input.setPreferredSize(new Dimension(70,20));

        panel.add(input);
    }

    void addSubmitButton(){
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e ->{
            String query = String.format("INSERT INTO %s(%s) VALUES ('%s')", label, "name", input.getText().trim());
            System.out.println(query);
            Database.executeUpdate(query);

            AddPayment.refresh();
        });
        panel.add(submitButton);
    }



}
