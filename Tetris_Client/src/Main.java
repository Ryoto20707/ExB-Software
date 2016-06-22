import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JFrame implements KeyListener {
    // 画面の大きさ（ピクセル単位）
    public static final int WIDTH  = 400;
    public static final int HEIGHT = 570;
    // 1マスあたりのピクセル数
    public static final int TILE_SIZE = 24;

    // 画面の種類
    protected enum WINDOW_MODE {
        TITLE, MODE_SELECT, PLAY, DOUBLE_PLAY, RESULT
    }

    // 現在の画面（他の具体的な画面への参照とする）
    private KeyPanel current;
    // 実際に使用する画面たち
    private KeyPanel title, modeSelect, result;
    private GamePanel gamePanel;
    private DoubleGamePanel doubleGamePanel;

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        setTitle("Tetris");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initPanel();
        current = title;
        current.setVisible(true);
        setVisible(true);
        setFocusable(true);
        addKeyListener(this);
    }

    /**
     * ウィンドウを切り替える
     * @param mode WINDOW_MODEで与えた定数のいずれかが入る
     */
    protected void change(WINDOW_MODE mode) {
        current.setVisible(false);
        switch (mode) {
            case TITLE :
                current = title;
                break;
            case MODE_SELECT:
                current = modeSelect;
                break;
            case PLAY:
                current = gamePanel;
                gamePanel.start(); // ゲームを開始する
                break;
            case DOUBLE_PLAY:
                setSize(DoubleGamePanel.WIDTH, DoubleGamePanel.HEIGHT);
                current = doubleGamePanel;
                doubleGamePanel.start(); // ゲームを開始する
                break;
            case RESULT:
                break;
            default:
                break;
        }
        current.setVisible(true);
    }

    /**
     * 各パネルを初期化する
     */
    private void initPanel() {
        title           = new TitlePanel(this);
        modeSelect      = new ModeSelectPanel(this);
        gamePanel       = new GamePanel();
        doubleGamePanel = new DoubleGamePanel();
        title.          setSize(WIDTH, HEIGHT);
        modeSelect.     setSize(WIDTH, HEIGHT);
        gamePanel.      setSize(WIDTH, HEIGHT);
        doubleGamePanel.setSize(DoubleGamePanel.WIDTH, HEIGHT);
        title.          setVisible(false);
        modeSelect.     setVisible(false);
        gamePanel.      setVisible(false);
        doubleGamePanel.setVisible(false);
        add(title);
        add(modeSelect);
        add(gamePanel);
        add(doubleGamePanel);
    }

    /*
     * 各種キーイベントを現在の画面に伝播させる
     */
    @Override
    public void keyPressed(KeyEvent e) {
        current.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
