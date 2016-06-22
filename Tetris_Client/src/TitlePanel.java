import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TitlePanel extends KeyPanel {
    private Main main;
    TitlePanel(final Main main) {
        this.main = main;
        JButton startButton = new JButton("はじめる");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.change(Main.WINDOW_MODE.MODE_SELECT);
            }
        });
        add(startButton);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}