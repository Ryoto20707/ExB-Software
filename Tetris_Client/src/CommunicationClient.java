import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicationClient extends Thread{
    private String playerID;
    private InetAddress addr;
    public BufferedReader in;
    private PrintWriter out;
    private boolean accepting = true; // Serverからの受付処理を受付中かどうか
    public Socket socket;
    public static final int PORT = 8080;  // Serverのport番号をここにも指定しておく
    private Queue<String> nextMino, general; // 次のミノと一般命令をそれぞれ格納するキュー
    public String enemyField = "";
    public int enemyNext = -1, enemyHold = -1, enemyLevel = 1, enemyScore = 0, attack = 0;
    public static final int NOT_SETTLED = 0;
    public static final int WIN = 1;
    public static final int LOSE = 2;
    public int result = NOT_SETTLED;

    public CommunicationClient() {
        nextMino = new LinkedList<String>();
        general = new LinkedList<String>();
    }

    /**
     * サーバーに接続し、サーバーからの出力を読み取るスレッドを走らせる。
     */
    public void connect(InetAddress addr) {
        this.addr = addr;
        try {
            // 接続
            this.socket = new Socket(addr, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 接続にあたって適当な文字列を送る
                out.println("connection");
                try {
                    // ID(0か1)を受信
                    playerID = in.readLine();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                // 入力待機と動作実行
                while (true) {
                    try {
                        String str = in.readLine();
                        if (str == null) {
                            JOptionPane.showMessageDialog(null, "サーバーが切断されました。");
                            break;
                        }
                        else if (str.equals("exit"))// 終了処理1:exitが入力されるかサーバーが切断される
                            break;
                        else if(str.length() == 3 && str.equals("win")) {
                            result = WIN;
                        }
                        else if(str.length() == 4 && str.equals("lose")) {
                            result = LOSE;
                        }
                        else if(str.length() > 4 && str.substring(0, 4).equals("next")) { // next:1~7が帰ったらHashMapに入れる
                            nextMino.add(str.substring(5));
                        }
                        else if(str.length() > 6 && str.substring(0, 6).equals("attack")) {
                            attack = Integer.parseInt(str.substring(7));
                        }
                        else if(str.length() > 9 && str.substring(0, 9).equals("enemyNext")) {
                            enemyNext = Integer.parseInt(str.substring(10));
                        }
                        else if(str.length() > 9 && str.substring(0, 9).equals("enemyHold")) {
                            enemyHold = Integer.parseInt(str.substring(10));
                        }
                        else if(str.length() > 10 && str.substring(0, 10).equals("enemyLevel")) {
                            enemyLevel = Integer.parseInt(str.substring(11));
                        }
                        else if(str.length() > 10 && str.substring(0, 10).equals("enemyScore")) {
                            enemyScore = Integer.parseInt(str.substring(11));
                        }
                        else if(str.length() > 10 && str.substring(0, 10).equals("enemyField")) {
                            enemyField = str.substring(11);
                        }
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
    /**
     * ユーザの情報をServerに送信する
     */
    public void sendUsersAddrToServer(){
        sendToServer(addr.toString());
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
     * インスタンスのInetAddressを取得する(getter)
     */
    public InetAddress getAddr(){
        return this.addr;
    }

    /**
     * クライアントでのサーバからの通信受付処理を停止する
     */
    public void stopAcceptFromServer(){
        accepting = false;
    }

    /**
     * サーバーから次のテトロミノを取得する
     * @return int 0~6
     */
    public int getMinoCode() {
        // next:0またはnext:1と送信
        sendToServer("next:"+playerID);
        while(true) {
            if(!nextMino.isEmpty()) break;
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {

            }
        }
        // 整数変換して返す
        return Integer.parseInt(nextMino.poll());
    }

    public String getGeneral() {
        while(true){
            if(!general.isEmpty()) break;
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {

            }
        }
        return general.poll();
    }
}
