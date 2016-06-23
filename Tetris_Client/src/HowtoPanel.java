import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class HowtoPanel extends KeyPanel {
    private Main main;

    HowtoPanel(final Main main) {
        this.main = main;
        JLabel vacantLabel = new JLabel("    ");
        JLabel vacantLabel2 = new JLabel("    ");
        JLabel vacantLabel3 = new JLabel("     ");
        JLabel vacantLabel4 = new JLabel("      ");
        vacantLabel.setFont(new Font(null, Font.BOLD, 18));
        vacantLabel2.setFont(new Font(null, Font.BOLD, 18));
        vacantLabel3.setFont((new Font(null, Font.BOLD, 18)));
        vacantLabel4.setFont((new Font(null, Font.BOLD, 18)));
        setCenter(vacantLabel);
        setCenter(vacantLabel2);
        setCenter(vacantLabel3);
        setCenter(vacantLabel4);;
        add(vacantLabel);
        setBackground(Color.CYAN);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        BorderLayout layout = new BorderLayout();
        add(vacantLabel);

        // 遊び方（ハート）
        JLabel titleLable        =  new JLabel("遊び方");
        titleLable.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        setCenter(titleLable);
        add(titleLable);
        titleLable.setLayout(new GridLayout(100,100,100,100));
        add(vacantLabel2);

        // keybind
        JLabel keyBind       = new JLabel("・ブロックの操作");
        JLabel left          = new JLabel("← : 左へ");
        JLabel right         = new JLabel("→ : 右へ");
        JLabel down          = new JLabel("↓ : 下へ");
        JLabel up            = new JLabel("↑ : 一番下へ");
        JLabel right_rotate = new JLabel("x, space : 右回転");
        JLabel left_rotate   = new JLabel("z : 左回転");
        setCenter(keyBind);
        setCenter(left);
        setCenter(right);
        setCenter(down);
        setCenter(up);
        setCenter(right_rotate);
        setCenter(left_rotate);
        keyBind.setFont(new Font(Font.SERIF, Font.PLAIN, 22));
        left.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        right.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        down.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        up.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        right_rotate.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        left_rotate.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        add(keyBind);
        add(left);
        add(right);
        add(down);
        add(up);
        add(right_rotate);
        add(left_rotate);
        add(vacantLabel3);

        // rule
        JLabel rule = new JLabel("・勝敗決定");
        JLabel rule_exp = new JLabel("ゲームオーバーになった方が負け");
        add(rule);
        add(rule_exp);
        setCenter(rule);
        setCenter(rule_exp);
        rule.setFont(new Font(Font.SERIF, Font.PLAIN, 22));
        rule_exp.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        add(vacantLabel4);

        // 罰ゲームの要素
        JLabel punishment = new JLabel("・相手への攻撃");
        JLabel punishment_exp_1 = new JLabel("プレイヤーが列を消した時，その消した行数と，攻撃者の");
        JLabel punishment_exp_2 = new JLabel("レベルに応じて相手の下にブロックがせり上がる。");
        add(punishment);
        add(punishment_exp_1);
        add(punishment_exp_2);
        setCenter(punishment);
        setCenter(punishment_exp_1);
        setCenter(punishment_exp_2);
        punishment.setFont(new Font(Font.SERIF, Font.PLAIN, 22));
        punishment_exp_1.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        punishment_exp_2.setFont(new Font(Font.SERIF, Font.PLAIN, 15));

        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            main.change(Main.WINDOW_MODE.MODE_SELECT);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void setCenter(JComponent component) {
        component.setAlignmentX(CENTER_ALIGNMENT);
        component.setAlignmentY(CENTER_ALIGNMENT);
    }
}
