import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TitlePanel extends KeyPanel {
    private Main main;
    TitlePanel(final Main main) {
        this.main = main;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0.8f, 0.9f, 1.0f));
              JLabel titleLabel = new JLabel("Tetris");
        final JLabel pressKey   = new JLabel("Press Any Key");
        setCenter(titleLabel);
        setCenter(pressKey);
        titleLabel.setFont(new Font(null, Font.PLAIN, 60));
        pressKey  .setFont(new Font(null, Font.PLAIN, 24));
        titleLabel.setForeground(Color.BLUE);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalGlue());
        add(pressKey);
        add(Box.createVerticalGlue());

        new Timer(800, new ActionListener() {
            private boolean flg = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                flg ^= true;
                pressKey.setText(flg ? "Press Any Key" : " ");
            }
        }).start();

    }

    private void setCenter(JComponent component) {
        component.setAlignmentX(CENTER_ALIGNMENT);
        component.setAlignmentY(CENTER_ALIGNMENT);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        main.change(Main.WINDOW_MODE.MODE_SELECT);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}