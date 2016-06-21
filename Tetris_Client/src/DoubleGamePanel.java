import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;

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
        try {
//           inetAddress = InetAddress.getByAddress(new byte[] {10, 37, (byte)129, 2});
            inetAddress = InetAddress.getByName("LocalHost");
            hostName = inetAddress.getHostName();
            client = new CommunicationClient(hostName, inetAddress);
        }
        catch (IOException e) {

        }

        myStatPanel    = new StatPanel();
        enemyStatPanel = new StatPanel();
        myGameField    = new GameField(myStatPanel, GameField.DOUBLE_SELF, client);
        enemyGameField = new GameField(enemyStatPanel, GameField.DOUBLE_ENEMY, null);

        // 自分の盤面
        myGameField.setLayout(new BorderLayout());
        myGameField.setFocusable(true);
        myGameField.setPreferredSize(new Dimension(Window.TILE_SIZE * GameField.ROW, Window.TILE_SIZE * GameField.COL));
        myGameField.setVisible(true);
        add(myGameField);

        // 自分のステータス
        myStatPanel.setLayout(new BorderLayout());
        myStatPanel.setFocusable(true);
        myStatPanel.setPreferredSize(new Dimension(WIDTH / 2 - Window.TILE_SIZE * GameField.ROW, HEIGHT));
        myStatPanel.setVisible(true);
        add(myStatPanel);

        // 相手のステータス
        enemyStatPanel.setLayout(new BorderLayout());
        enemyStatPanel.setFocusable(true);
        enemyStatPanel.setPreferredSize(new Dimension(WIDTH / 2 - Window.TILE_SIZE * GameField.ROW, HEIGHT));
        enemyStatPanel.setVisible(true);
        add(enemyStatPanel);

        // 相手の盤面
        enemyGameField.setLayout(new BorderLayout());
        enemyGameField.setFocusable(true);
        enemyGameField.setPreferredSize(new Dimension(Window.TILE_SIZE * GameField.ROW, Window.TILE_SIZE * GameField.COL));
        enemyGameField.setVisible(true);
        add(enemyGameField);

        setVisible(true);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.connect();
                while(true) {
                    if(client.getGeneral().equals("start")) {
                        break;
                    }
                }
                myGameField.start();
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
