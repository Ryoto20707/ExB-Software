import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DoubleGameFrame extends JFrame implements KeyListener{
    private GameField myGameField, enemyGameField; // テトリス盤
    private StatPanel myStatPanel, enemyStatPanel; // ステータスパネル

    public DoubleGameFrame() {
        setTitle("Tetris");
        setSize(new Dimension(800, 570));

        myStatPanel    = new StatPanel();
        enemyStatPanel = new StatPanel();
        myGameField    = new GameField(myStatPanel);
        enemyGameField = new GameField(enemyStatPanel);

        myGameField.setLayout(new BorderLayout());
        myGameField.setFocusable(true);
        myGameField.setBounds(0, 0, Window.TILE_SIZE * GameField.ROW, Window.TILE_SIZE * GameField.COL);
        myGameField.setVisible(true);
        add(myGameField, BorderLayout.WEST);
        enemyGameField.setLayout(new BorderLayout());
        enemyGameField.setFocusable(true);
        enemyGameField.setBounds(0, 0, Window.TILE_SIZE * GameField.ROW, Window.TILE_SIZE * GameField.COL);
        enemyGameField.setVisible(true);
        add(enemyGameField, BorderLayout.EAST);
        myStatPanel.setLayout(new BorderLayout());
        myStatPanel.setFocusable(true);
        myStatPanel.setBounds(Window.TILE_SIZE * GameField.ROW, 0, getWidth() - Window.TILE_SIZE * GameField.ROW, getHeight());
        myStatPanel.setVisible(true);
        add(myStatPanel, BorderLayout.EAST);
    }

    public void start() {
        myGameField.start();
    }

    public void init() {
        myGameField.init();
        enemyGameField.init();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        myGameField.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
