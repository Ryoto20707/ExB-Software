import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ModeSelectPanel extends KeyPanel {
    private Main main;
    private int select = 0;
    private EventLabel[] menu = new EventLabel[3];
    private Border margin  = new EmptyBorder(5, 5, 5, 5);
    private Border padding = new EmptyBorder(10, 10, 10, 10);

    ModeSelectPanel(final Main main) {
        this.main = main;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0.9f, 1f, 0.9f));
        menu[0] = new EventLabel("ひとりであそぶ", Main.WINDOW_MODE.PLAY);
        menu[1] = new EventLabel("ふたりであそぶ", Main.WINDOW_MODE.DOUBLE_PLAY);
        menu[2] = new EventLabel("あそびかた",    Main.WINDOW_MODE.HOWTO);
        JLabel title = new JLabel("モード選択");
        JLabel desctiption = new JLabel("Spaceで選択、Enterで決定");
        title.setFont(new Font(null, Font.PLAIN, 40));
        desctiption.setForeground(Color.RED);
        setCenter(title);
        setCenter(desctiption);
        add(Box.createVerticalGlue());
        add(title);
        for (EventLabel aMenu : menu) {
            JLabel label = aMenu.label;
            label.setFont(new Font(null, Font.PLAIN, 20));
            label.setSize(100, 20);
            label.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), padding));
            setCenter(label);
            add(Box.createVerticalGlue());
            add(label);
        }
        add(Box.createVerticalGlue());
        add(desctiption);
        add(Box.createVerticalGlue());
        setBorderFocus();
    }

    private void setCenter(JComponent component) {
        component.setAlignmentX(CENTER_ALIGNMENT);
        component.setAlignmentY(CENTER_ALIGNMENT);
    }

    private void setBorderFocus() {
        for (EventLabel aMenu : menu) {
            aMenu.label.setBorder(new CompoundBorder(margin, padding));
        }
        menu[select].label.setBorder(new CompoundBorder(new LineBorder(Color.GREEN, 5, true), padding));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                select = (select + 1) % menu.length;
                setBorderFocus();
                break;
            case KeyEvent.VK_ENTER:
                main.change(menu[select].mode);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

class EventLabel {
    JLabel label;
    Main.WINDOW_MODE mode;

    EventLabel(String text, Main.WINDOW_MODE mode) {
        this.label = new JLabel(text);
        this.mode = mode;


    }
}