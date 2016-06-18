import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Thread{
    public static final int PORT = 8080;
    public static BufferedReader in1;
    public static BufferedReader in2;
    public static PrintWriter out1;
    public static PrintWriter out2;
    private static String name1;
    private static String name2;
    private static ServerSocket s; // フィールドに変更
    private static Socket socket1;  // player1から受け付ける用
    private static Socket socket2;  // player2から受け付ける用

    public BufferedReader sender;
    public String playername;

    // スレッド用コンストラクタ
    Main(BufferedReader sender, String playername) {
        this.sender = sender;
        this.playername = playername;
    }


    public static void main(String[] args) throws IOException {
        s = new ServerSocket(PORT);
        System.out.println("Server起動(port=" + PORT + ")");
        try {
            socket1 = s.accept();
            acceptPlayer1(socket1);
            try {
                socket2 = s.accept();
                acceptPlayer2(socket2);
                //-----------
                Thread th1 = new Thread(new Main(in1, name1));
                Thread th2 = new Thread(new Main(in2, name2));
                th1.start();
                th2.start();
                // ---------
                try {
                    th1.join();
                    th2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // ---------
            }finally {
                socket2.close();
            }
        }finally{
            socket1.close();
        }
        System.out.println("サーバーを終了します");// 終了処理7(サーバー側):サーバーを閉じる
        s.close();
    }

    @Override
    public void run(){
        // これはaccept側かな
    }


    private static void acceptPlayer1(Socket socket1) throws IOException{
        System.out.println("プレイヤー1の参加を待っています...: " + s);
        in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
        out1 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream())),true);
        out1.println("1");// プレイヤー番号を送信
        System.out.println(name1+"が参加しました: "+socket1);
    }

    private static void acceptPlayer2(Socket socket2) throws IOException{
        System.out.println("プレイヤー2の参加を待っています...: " + s);
        acceptPlayer2(socket2);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        out2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream())), true);
        out2.println("2");// プレイヤー番号を送信
        name2 = in2.readLine();// 名前を受信
        System.out.println(name2 + "が参加しました: " + socket2);
    }

    /**
     * Clientから送られてきた文字列に応じて処理を決定する
     * 上のmain()メソッド中で受け付けた時以外に，送られてくると考えられる情報
     * ・fieldInfo  "field:<player>:<fieldInfo>"
     * ・attackInfo  "attack:<player>:<attackinfo>"
     * ・the End of Game (未開発)
     */
    private void doAction(String str){
        switch (str.charAt(0)){
            case 'a':  // fieldInfo
                if(str.charAt(6) == '1')  fallBlock(2, str.substring(8));
                else if (str.charAt(6) == '2') fallBlock(1, str.substring(8));
                break;
            case 'f':  // attackInfo
                if(str.charAt(7) == '1')  renewFieldInfo(2, str.substring(9));
                else if (str.charAt(7) == '2') renewFieldInfo(1, str.substring(9));
                break;
            case 'E':  // end of game
                displayResult();  // 勝敗結果を表示する
                break;

        }
    }

    private void displayResult() {
        // 勝敗結果を表示する
    }

    private void fallBlock(int target, String attackInfo){
        // clientに罰の量を送るだけ，送信用のスレッドに組み込む
    }

    private void renewFieldInfo(int target, String filedInfo) {
        // clientに送るだけ，送信用のスレッドに組み込む
    }

}
