import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class DoubleGamePanel extends KeyPanel {
    private GameField myGameField, enemyGameField; // テトリス盤
    private StatPanel myStatPanel, enemyStatPanel; // ステータスパネル
    private CommunicationClient client;
    private Main main;
    private JFrame dialog;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 570;

    public DoubleGamePanel(final Main main) {
        this.main = main;
        setSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // BoxLayoutで横から詰める
        client         = new CommunicationClient();
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

        init();
        setVisible(true);
        setFocusable(true);
        addKeyListener(this);
    }

    private void init() {
        client.init();
        myGameField.init();
        myStatPanel.init();
        enemyStatPanel.init();
        enemyGameField.init();

    }

    public void start() {
        final InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(JOptionPane.showInputDialog("サーバーのマシン名またはIPを入力してください。"));
        }
        catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "宛先が不正です。");
            main.change(Main.WINDOW_MODE.MODE_SELECT);
            return;
        }

        dialog = new JFrame("Connecting");
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(200, 70));
        dialog.add(dialogPanel);
        dialog.setFocusable(true);
        dialog.setSize(new Dimension(300, 160));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel dialogLabel = new JLabel(Arrays.toString(inetAddress.getAddress()) + "に接続中...");
        dialogLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogLabel.setVisible(true);
        dialogPanel.add(dialogLabel, BorderLayout.CENTER);
        JButton dialogCancel = new JButton("キャンセル");
        dialogCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.socket.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                close();
            }
        });
        dialogCancel.setVisible(true);
        dialogPanel.add(dialogCancel, BorderLayout.PAGE_END);
        dialogPanel.setVisible(true);
        dialog.pack();
        dialog.setVisible(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(inetAddress);
                }
                catch(IOException e) {
                    JOptionPane.showMessageDialog(null, "サーバーが見つかりませんでした。");
                    dialog.dispose();
                    main.change(Main.WINDOW_MODE.MODE_SELECT);
                    return;
                }
                while(!client.connecting) {
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dispose();
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
                        enemyGameField.traceEnemy.start();
                    }
                }).start();
                final Timer clientWatcher = new Timer(1000, null);
                clientWatcher.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (client.attack != 0) {
                            myGameField.nextLines += client.attack;
                            myStatPanel.setMessage("Atk " + client.attack + "lines!", 2000);
                            myStatPanel.setMessageBackground(Color.RED, 2000);
                            client.attack = 0;
                        }
                        if(!client.connecting) {
                            main.change(Main.WINDOW_MODE.MODE_SELECT);
                            finishGame();
                            init();
                            clientWatcher.stop();
                        }
                        if(client.result == CommunicationClient.LOSE) {
                            myStatPanel.message.setText("LOSE");
                            enemyStatPanel.message.setText("<html>Press Q<br>to Quit<html>");
                            finishGame();
                        }
                        if(client.result == CommunicationClient.WIN) {
                            myStatPanel.message.setText("WIN");
                            enemyStatPanel.message.setText("<html>Press Q<br>to Quit<html>");
                            finishGame();
                        }
                    }
                });
                clientWatcher.start();
            }
        }).start();
    }

    private void close() {
        main.change(Main.WINDOW_MODE.MODE_SELECT);
    }

    private void finishGame() {
        myGameField.playing = false;
        enemyGameField.traceEnemy.stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_Q && !myGameField.running && !myGameField.playing) {
            client.sendToServer("exit");
            init();
            main.change(Main.WINDOW_MODE.MODE_SELECT);
        }
        myGameField.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
