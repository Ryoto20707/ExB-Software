import java.awt.*;
import java.awt.event.KeyEvent;

public class GamePanel extends KeyPanel{
    private GameField gameField;
    private StatPanel statPanel;

    public GamePanel() {
        gameField = new GameField(statPanel);
        statPanel = new StatPanel();
    }

    public void start() {
        gameField.start();
    }

    public void init() {
        gameField.init();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        gameField.setLayout(new BorderLayout());
        gameField.setFocusable(false);
        gameField.setBounds(0, 0, Window.TILE_SIZE * GameField.ROW, Window.TILE_SIZE * GameField.COL);
        gameField.setVisible(true);
        add(gameField, BorderLayout.WEST);
        statPanel.setLayout(new BorderLayout());
        statPanel.setFocusable(false);
        statPanel.setBounds(Window.TILE_SIZE * GameField.ROW, 0, getWidth() - Window.TILE_SIZE * GameField.ROW, getHeight());
        statPanel.setVisible(true);
        add(statPanel, BorderLayout.EAST);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        gameField.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
