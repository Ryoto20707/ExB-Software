import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class CommunicationClient extends Thread{
    private String myName;
    private InetAddress addr;
    private BufferedReader in;
    private PrintWriter out;
    private boolean accepting = true; // Serverからの受付処理を受付中かどうか
    public Socket socket;
    public static final int PORT = 8080;  // Serverのport番号をここにも指定しておく

    public CommunicationClient(String name, InetAddress addr) throws IOException {
        this.myName = name;
        this.addr = addr;
        this.socket = new Socket(addr, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        // Serverからの情報を受け付けるThread
        CommunicationClient ccAccept = new CommunicationClient(this.myName, this.addr);  // これじゃだめなきがする
        ccAccept.start();
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
//        String "field:<player>:<fieldInfo>"　としてserverに送信
        String sendStr = "field:" + addr + ":" + FieldInfo;
        sendToServer(sendStr);
    }
    /**
     * Attackの情報をServerに送信する
     */
    public int sendAttackInfoToServer(String AttackInfo){
//        String "attack:<player>:<attackinfo>"として送信
        String sendStr = "attack:" + addr + ":" + AttackInfo;
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

    @Override
    public void run(){
        // ここにこのrun()が走っているThreadのユーザに罰ゲーム処理と，相手のボード状況の変化をさせる
        while(accepting){
            try{
                //　Serverから送られてきた文字列に応じて，このスレッドを走らせている
                //  Clientに処理をする。（Serverの構築がきてから）
                String str = in.readLine();
            }catch(IOException e){
                // ----
            }
        }

    }
}
