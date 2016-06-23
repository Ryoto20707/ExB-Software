import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    JLabel nextTitle;  // "Next"ラベル
    JLabel holdTitle;  // "Hold"ラベル
    JLabel scoreTitle; // "Score"ラベル
    JLabel scoreDisp;  // 実際のスコアを記入
    JLabel message;    // メッセージ
    JLabel level;      // "Level "+level;
    BlockPanel nextPanel; // 次のミノのプレビュー
    BlockPanel holdPanel; // ホールドのプレビュー

    StatPanel() {
        super();
        // "Next"のラベル
        nextTitle = new JLabel("Next");
        nextTitle.setVisible(true);
        nextTitle.setBounds(10, 30, 40, 12);
        add(nextTitle);
        // 次のミノのプレビュー
        nextPanel = new BlockPanel();
        nextPanel.setVisible(true);
        nextPanel.setBounds(10, 44, 80, 80);
        add(nextPanel);
        // "Hold"のレベル
        holdTitle = new JLabel("Hold");
        holdTitle.setVisible(true);
        holdTitle.setBounds(10, 140, 40, 12);
        add(holdTitle);
        // ホールドされたミノのプレビュー
        holdPanel = new BlockPanel();
        holdPanel.setVisible(true);
        holdPanel.setBounds(10, 156, 80, 80);
        add(holdPanel);
        // "Score"のラベル
        scoreTitle = new JLabel("Score");
        scoreTitle.setVisible(true);
        scoreTitle.setBounds(10, 260, 40, 12);
        add(scoreTitle);
        // スコアのラベル、初期値0
        scoreDisp = new JLabel("0");
        scoreDisp.setVisible(true);
        scoreDisp.setBounds(20, 278, 80, 20);
        scoreDisp.setBackground(Color.LIGHT_GRAY);
        scoreDisp.setOpaque(true);
        add(scoreDisp);
        // メッセージ
        message = new JLabel();
        message.setVisible(true);
        message.setBounds(20, 320, 80, 60);
        message.setBackground(Color.LIGHT_GRAY);
        message.setOpaque(true);
        add(message);
        // レベル
        level = new JLabel("Level 1");
        level.setVisible(true);
        level.setBounds(30, 400, 80, 12);
        add(level);
    }

    /**
     * スコアを更新、反映
     * @param newScore 新しいスコア
     */
    public void changeScore(int newScore) {
        scoreDisp.setText(Integer.toString(newScore));
        scoreDisp.repaint();
    }

    public void setLevel(int newLevel) {
        level.setText("Level "+newLevel);
        level.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * メッセージを指定時間表示する。
     * 別スレッドで実行することで本来の操作を止めない。
     * @param msg 表示メッセージ
     * @param time 表示時間
     */
    public void setMessage(String msg, final int time) {
        message.setText(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    message.setText("");
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * メッセージパネルの色を一定時間変える
     * 別スレッドで実行することで本来の操作を止めない。
     * @param color 色
     * @param time 表示時間
     */
    public void setMessageBackground(Color color, final int time) {
        message.setBackground(color);
        new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Thread.sleep(time);
                   message.setBackground(Color.LIGHT_GRAY);
               }
               catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        }).start();
    }
}
