import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicationClient extends Thread{
    private String myName, playerID;
    private InetAddress addr;
    public BufferedReader in;
    private PrintWriter out;
    private boolean accepting = true; // Serverからの受付処理を受付中かどうか
    public Socket socket;
    public static final int PORT = 8080;  // Serverのport番号をここにも指定しておく
    private Queue<String> nextMino, general; // 次のミノと一般命令をそれぞれ格納するキュー
    public String enemyField = "";

    public CommunicationClient(String name, InetAddress addr) throws IOException {
        this.myName = name;
        this.addr = addr;
        nextMino = new LinkedList<String>();
        general = new LinkedList<String>();
    }

    /**
     * サーバーに接続し、サーバーからの出力を読み取るスレッドを走らせる。
     */
    public void connect() {
        try {
            // 接続
            this.socket = new Socket(addr, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        }
        catch (IOException e) {

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
                        else if(str.substring(0, 4).equals("next")) { // next:1~7が帰ったらHashMapに入れる
                            nextMino.add(str.substring(5));
                        }
                        else if(str.substring(0, 5).equals("field")) {
                            enemyField = str.substring(6);
                        }
                        else
                            general.add(str);
                    }
                    catch (Exception e) {

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
     * Attackの情報をServerに送信する
     */
    public int sendAttackInfoToServer(String AttackInfo){
//        String "attack:<attackinfo>"として送信
        String sendStr = "attack:" + AttackInfo;
        int ret = sendToServer(sendStr);
        return (ret == 0) ? 0 : -1;
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
     * nameのgetter
     */
    public String getMyName(){
        return this.myName;
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
