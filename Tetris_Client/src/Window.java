import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class Window extends JFrame implements KeyListener{
    // 画面の大きさ（ピクセル単位）
    public static final int WIDTH  = 400;
    public static final int HEIGHT = 570;
    // 1マスあたりのピクセル数
    public static final int TILE_SIZE = 24;

    // 画面の種類
    private enum WINDOW_MODE {
        TITLE, MODE_SELECT, PLAY, RESULT
    }

    // 現在の画面（他の具体的な画面への参照とする）
    private KeyPanel current;
    // 実際に使用する画面たち
    private KeyPanel title, modeSelect, result;
    private GamePanel gamePanel;


    Window() {
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
    private void change(WINDOW_MODE mode) {
        /**
         * TODO
         * setVisibleをswitch外に出して共通化
         **/
        switch (mode) {
            case TITLE :
                current.setVisible(false);
                current = title;
                current.setVisible(true);
                break;
            case MODE_SELECT:
                current.setVisible(false);
                current = modeSelect;
                current.setVisible(true);
                break;
            case PLAY:
                current.setVisible(false);
                current = gamePanel;
                current.setVisible(true);
                gamePanel.init();
                gamePanel.start(); // ゲームを開始する
                break;
            case RESULT:
                break;
            default:
                break;
        }
    }

    /**
     * 各パネルを初期化する
     */
    private void initPanel() {
        title      = new KeyPanel() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        modeSelect = new KeyPanel() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        gamePanel  = new GamePanel();
        // title
        JButton startButton = new JButton("はじめる");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                change(WINDOW_MODE.MODE_SELECT);
            }
        });
        title.add(startButton);
        // modeSelect
        JButton onePlayer  = new JButton("ひとりであそぶ");
        JButton twoPlayers = new JButton("ふたりであそぶ");
        onePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                change(WINDOW_MODE.PLAY);
            }
        });
        modeSelect.add(onePlayer);
        modeSelect.add(twoPlayers);
        // all
        title.     setSize(WIDTH, HEIGHT);
        modeSelect.setSize(WIDTH, HEIGHT);
        gamePanel. setSize(WIDTH, HEIGHT);
        title.     setVisible(false);
        modeSelect.setVisible(false);
        gamePanel .setVisible(false);
        add(title);
        add(modeSelect);
        add(gamePanel);
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
