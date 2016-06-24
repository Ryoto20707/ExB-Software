import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicationClient extends Thread{
    public static final int PORT = 8080;  // Serverのport番号をここにも指定しておく
    private String playerID;
    public BufferedReader in;
    private PrintWriter out;
    public Socket socket;

    // 勝敗フラグ
    public static final int NOT_SETTLED = 0;
    public static final int WIN = 1;
    public static final int LOSE = 2;
    public int result; // 上記3つのいずれかを記録

    private Queue<String> nextMino, general; // 次のミノと一般命令をそれぞれ格納するキュー

    // 受信結果をそれぞれ保持する変数
    public String enemyField;
    public int enemyNext, enemyHold, enemyLevel, enemyScore, attack;

    public boolean connecting; // 通信中フラグ

    public CommunicationClient() {
        // キュー初期化
        nextMino = new LinkedList<String>();
        general = new LinkedList<String>();
    }
    
    public void init() {
        // 各種フラグ、保持変数初期化
        enemyField = "";
        result = NOT_SETTLED;
        enemyNext = -1; enemyHold = -1; enemyLevel = 1; enemyScore = 0; attack = 0;
    }

    /**
     * サーバーに接続し、サーバーからの出力を読み取るスレッドを走らせる。
     */
    public void connect(InetAddress addr) throws IOException{
        init();
        // 接続
        this.socket = new Socket(addr, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        // 接続にあたって適当な文字列を送る
        out.println("connection");
        try {
            // ID(0か1)を受信
            playerID = in.readLine();
            connecting = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 入力待機と動作実行
                    while (connecting) {
                        try {
                            String str = in.readLine();
                            // サーバーが切れた時
                            if (str == null) {
                                JOptionPane.showMessageDialog(null, "サーバーが切断されました。");
                                connecting = false;
                                break;
                            }
                            // 相手のクライアントが切れた時
                            else if (str.equals("disconnected")) {
                                JOptionPane.showMessageDialog(null, "相手の通信が切断されました。");
                                connecting = false;
                                break;
                            }
                            // サーバーから終了命令がきた時
                            else if (str.equals("exit")) {
                                connecting = false;
                                break;
                            }
                            // サーバーの勝敗判定の結果、勝利した時
                            else if(str.length() == 3 && str.equals("win")) {
                                result = WIN;
                            }
                            // サーバーの勝敗判定の結果、敗戦した時
                            else if(str.length() == 4 && str.equals("lose")) {
                                result = LOSE;
                            }
                            // 次のテトロミノIDが通知された時
                            else if(str.length() > 4 && str.substring(0, 4).equals("next")) {
                                nextMino.add(str.substring(5));
                            }
                            // 相手からの攻撃がきた時
                            else if(str.length() > 6 && str.substring(0, 6).equals("attack")) {
                                attack = Integer.parseInt(str.substring(7));
                            }
                            // 相手の次のテトロミノIDが指示された時
                            else if(str.length() > 9 && str.substring(0, 9).equals("enemyNext")) {
                                enemyNext = Integer.parseInt(str.substring(10));
                            }
                            // 相手のHoldが指示された時
                            else if(str.length() > 9 && str.substring(0, 9).equals("enemyHold")) {
                                enemyHold = Integer.parseInt(str.substring(10));
                            }
                            // 相手のレベルが変化した時
                            else if(str.length() > 10 && str.substring(0, 10).equals("enemyLevel")) {
                                enemyLevel = Integer.parseInt(str.substring(11));
                            }
                            // 相手のスコアが通知された時
                            else if(str.length() > 10 && str.substring(0, 10).equals("enemyScore")) {
                                enemyScore = Integer.parseInt(str.substring(11));
                            }
                            // 相手のフィールド文字列が送信された時
                            else if(str.length() > 10 && str.substring(0, 10).equals("enemyField")) {
                                enemyField = str.substring(11);
                            }
                            // それ以外
                            else {
                                general.add(str);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 盤面の情報をServerに送信する
     */
    public void sendFieldInfoToServer(String FieldInfo){
//        String "field:<fieldInfo>"　としてserverに送信
        String sendStr = "field:" + FieldInfo;
        sendToServer(sendStr);
    }

    /**
     * Serverに文字列を送信する
     */
    public int sendToServer(String str) {
        out.println(str);
        return 0;
    }

    /**
     * サーバーから次のテトロミノを取得する
     * @return int 0~6
     */
    public int getMinoCode() {
        // next:0またはnext:1と送信
        sendToServer("next:"+playerID);
        waitForQueue(nextMino);
        // 整数変換して返す
        return Integer.parseInt(nextMino.poll());
    }

    /**
     * Generalキューに何か入るまで待ち、入ったら文字列を返す
     * @return general.poll()
     */
    public String getGeneral() {
        waitForQueue(general);
        return general.poll();
    }

    /**
     * 入力キューに何か入るまで待つ
     * @param queue 対象
     */
    private void waitForQueue(Queue queue) {
        while(true){
            if(!queue.isEmpty()) break;
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
