import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    JLabel nextTitle;  // "Next"ラベル
    JLabel holdTitle;  // "Hold"ラベル
    JLabel scoreTitle; // "Score"ラベル
    JLabel scoreDisp;  // 実際のスコアを記入
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
        scoreDisp.setFont(new Font(null, Font.PLAIN, 20));
        scoreDisp.setVisible(true);
        scoreDisp.setBounds(30, 274, 40, 20);
        add(scoreDisp);
    }

    /**
     * スコアを更新、反映
     * @param newScore 新しいスコア
     */
    public void changeScore(int newScore) {
        scoreDisp.setText(Integer.toString(newScore));
        scoreDisp.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
    }
}
