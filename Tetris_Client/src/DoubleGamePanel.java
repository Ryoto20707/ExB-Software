import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DoubleGamePanel extends KeyPanel {
    private GameField myGameField, enemyGameField; // テトリス盤
    private StatPanel myStatPanel, enemyStatPanel; // ステータスパネル
    private CommunicationClient client;
    private InetAddress inetAddress;
    private String hostName;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 570;

    public DoubleGamePanel() {
        setSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // BoxLayoutで横から詰める
        client = new CommunicationClient();

        myStatPanel    = new StatPanel();
        enemyStatPanel = new StatPanel();
        myGameField    = new GameField(myStatPanel, GameField.DOUBLE_SELF, client);
        enemyGameField = new GameField(enemyStatPanel, GameField.DOUBLE_ENEMY, client);

        // 自分の盤面
        myGameField.setLayout(new BorderLayout());
        myGameField.setFocusable(true);
        myGameField.setPreferredSize(new Dimension(Main.TILE_SIZE * GameField.ROW, Main.TILE_SIZE * GameField.COL));
        myGameField.setVisible(true);
        add(myGameField);

        // 自分のステータス
        myStatPanel.setLayout(new BorderLayout());
        myStatPanel.setFocusable(true);
        myStatPanel.setPreferredSize(new Dimension(WIDTH / 2 - Main.TILE_SIZE * GameField.ROW, HEIGHT));
        myStatPanel.setVisible(true);
        add(myStatPanel);

        // 相手のステータス
        enemyStatPanel.setLayout(new BorderLayout());
        enemyStatPanel.setFocusable(true);
        enemyStatPanel.setPreferredSize(new Dimension(WIDTH / 2 - Main.TILE_SIZE * GameField.ROW, HEIGHT));
        enemyStatPanel.setVisible(true);
        add(enemyStatPanel);

        // 相手の盤面
        enemyGameField.setLayout(new BorderLayout());
        enemyGameField.setFocusable(true);
        enemyGameField.setPreferredSize(new Dimension(Main.TILE_SIZE * GameField.ROW, Main.TILE_SIZE * GameField.COL));
        enemyGameField.setVisible(true);
        add(enemyGameField);

        setVisible(true);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() {
        try {
            inetAddress = InetAddress.getByName(JOptionPane.showInputDialog("サーバーのIPを入力してください。"));
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.connect(inetAddress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                myStatPanel.message.setText("通信待機中");
                while(true) {
                    if(client.getGeneral().equals("start")) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                myGameField.start();
                enemyGameField.startEnemy();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (client.attack != 0) {
                        myGameField.nextLines += client.attack;
                        myStatPanel.setMessage("Atk " + client.attack + "lines!", 1000);
                        client.attack = 0;
                    }
                    if(!client.connecting) {
                        main.change(Main.WINDOW_MODE.MODE_SELECT);
                    }
                    if(client.result == CommunicationClient.WIN) {
                        myStatPanel.message.setText("WIN");
                        enemyStatPanel.message.setText("LOSE");
                    }
                    if(client.result == CommunicationClient.LOSE) {
                        myStatPanel.message.setText("LOSE");
                        enemyStatPanel.message.setText("WIN");
                    }
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
