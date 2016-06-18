import javax.swing.*;
import java.awt.*;

public class StatPanel extends JPanel {
    JLabel scoreTitle; // "Score"ラベル
    JLabel scoreDisp;  // 実際のスコアを記入

    StatPanel() {
        super();
        // scoreTitleの生成
        scoreTitle = new JLabel("Score");
        scoreTitle.setFocusable(true);
        scoreTitle.setVisible(true);
        scoreTitle.setBounds(10, 60, 40, 12);
        add(scoreTitle);
        scoreDisp = new JLabel("0");
        scoreDisp.setFocusable(true);
        scoreDisp.setVisible(true);
        scoreDisp.setBounds(14, 78, 40, 12);
        add(scoreDisp);
    }

    /**
     * スコアを更新、反映
     * @param newScore 新しいスコア
     */
    public void changeScore(int newScore) {
        scoreDisp.setText(new Integer(newScore).toString());
        scoreDisp.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        
    }
}
