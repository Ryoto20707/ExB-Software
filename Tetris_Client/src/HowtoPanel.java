import javax.swing.*;
import java.awt.event.KeyEvent;

public class HowtoPanel extends KeyPanel {
    private Main main;

    HowtoPanel(final Main main) {
        this.main = main;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            main.change(Main.WINDOW_MODE.MODE_SELECT);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
