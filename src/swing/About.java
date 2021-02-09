package swing;

import javax.swing.*;

public class About extends JFrame {

    About(){
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setSize(40, 55);
        this.setLocationRelativeTo(null);

        addJLabel();
    }

    void addJLabel(){
        JLabel label = new JLabel("Amabeusz");
        this.add(label);
        this.setVisible(true);
    }
}
