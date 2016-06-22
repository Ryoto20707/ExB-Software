import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ModeSelectPanel extends KeyPanel {
    ModeSelectPanel(final Main main) {
        JButton onePlayer  = new JButton("ひとりであそぶ");
        JButton twoPlayers = new JButton("ふたりであそぶ");
        onePlayer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                main.change(Main.WINDOW_MODE.PLAY);
            }
        });
        twoPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.change(Main.WINDOW_MODE.DOUBLE_PLAY);
            }
        });
        add(onePlayer);
        add(twoPlayers);
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