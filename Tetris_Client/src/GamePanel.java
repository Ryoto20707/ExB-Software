import java.awt.*;
import java.awt.event.KeyEvent;

public class GamePanel extends KeyPanel{
    private Main main;
    private GameField gameField; // テトリス盤
    private StatPanel statPanel; // ステータスパネル

    public GamePanel(final Main main) {
        this.main = main;
        statPanel = new StatPanel();
        gameField = new GameField(statPanel, GameField.SINGLE, null);
    }

    public void start() {
        gameField.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        gameField.setLayout(new BorderLayout());
        gameField.setFocusable(true);
        gameField.setBounds(0, 0, Main.TILE_SIZE * GameField.ROW, Main.TILE_SIZE * GameField.COL);
        gameField.setVisible(true);
        add(gameField, BorderLayout.WEST);
        statPanel.setLayout(new BorderLayout());
        statPanel.setFocusable(true);
        statPanel.setBounds(Main.TILE_SIZE * GameField.ROW, 0, getWidth() - Main.TILE_SIZE * GameField.ROW, getHeight());
        statPanel.setVisible(true);
        add(statPanel, BorderLayout.EAST);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_Q && gameField.running && gameField.playing) {
            gameField.reset();
            main.change(Main.WINDOW_MODE.MODE_SELECT);
        }
        else if(e.getKeyCode() == KeyEvent.VK_S && !gameField.running) {
            gameField.init();
            gameField.start();
        }
        gameField.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
