import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIFrame extends JFrame {
    private JLabel[] connection;
    public JButton connect, disconnect;
    public JTextArea IPs;
    public GUIFrame() {
        setTitle("Tetris Server");
        setLayout(new FlowLayout());
        JPanel statusPanel = new JPanel();
        statusPanel.setSize(new Dimension(400, 80));
        statusPanel.setLayout(new FlowLayout());
        JPanel[] status = new JPanel[2];
        JLabel[] playerName = new JLabel[2];
        connection = new JLabel[2];
        for (int i = 0; i < status.length; i++) {
            status[i] = new JPanel();
            status[i].setPreferredSize(new Dimension(200, 90));
            status[i].setLayout(new BoxLayout(status[i], BoxLayout.Y_AXIS));
            playerName[i] = new JLabel(i + 1 + "P");
            playerName[i].setFont(new Font(null, Font.PLAIN, 32));
            playerName[i].setPreferredSize(new Dimension(200, 50));
            playerName[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            playerName[i].setVisible(true);
            status[i].add(playerName[i]);
            connection[i] = new JLabel("未接続");
            connection[i].setFont(new Font(null, Font.PLAIN, 24));
            connection[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            connection[i].setForeground(Color.RED);
            connection[i].setPreferredSize(new Dimension(200, 40));
            connection[i].setVisible(true);
            status[i].add(connection[i]);
            status[i].setVisible(true);
            statusPanel.add(status[i]);
        }
        IPs = new JTextArea();
        IPs.setEditable(false);
        IPs.setVisible(true);
        IPs.setFocusable(true);
        JScrollPane scrollPane = new JScrollPane(IPs);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setVisible(true);
        scrollPane.setFocusable(true);
        connect = new JButton("接続する");
        connect.setPreferredSize(new Dimension(100, 40));
        connect.setVisible(true);
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.open();
            }
        });
        disconnect = new JButton("終了する");
        disconnect.setPreferredSize(new Dimension(100, 40));
        disconnect.setVisible(false);
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        setSize(new Dimension(400, 400));
        add(statusPanel);
        add(scrollPane);
        add(connect);
        add(disconnect);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setFocusable(true);

    }

    public void changeStatus(int id, boolean isConnected) {
        if(isConnected) {
            connection[id].setText("接続済み");
            connection[id].setForeground(Color.GREEN);
        }
        else {
            connection[id].setText("未接続");
            connection[id].setForeground(Color.RED);
        }
    }


}
