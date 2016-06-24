import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

public class Main implements Runnable {
    public static final int PORT = 8080;
    public static BufferedReader[]  in = new BufferedReader[2];
    public static PrintWriter[]    out = new PrintWriter[2];
    public static ServerSocket s; // サーバーソケット
    private static Socket[] sockets    = new Socket[2]; // クライアント接続ソケット
    private static Thread[] th         = new Thread[2]; // クライアント接続用スレッド
    private static boolean[] connected = {false, false}; // 1P, 2Pが接続されているか
    private static boolean[] minoflag  = new boolean[7]; // ミノが偏らないようにするための処理に使う
    private static final HashMap<Integer, Integer> minoMap = new HashMap<Integer, Integer>(); // 落下ミノ共通化のためのハッシュマップ
    private static Random rand = new Random(System.currentTimeMillis()); // テトロミノ発生乱数
    private static int[] mapCount = {0, 0}; // minoMapのプレイヤーごとのインデックス
    private static int winnner;  // 勝者ID。0or1or-1(初期)
    private static GUIFrame gui; // 表示GUI

    // スレッド用
    public BufferedReader reader; // in[player]が渡される。
    public int id;                // 接続しているプレイヤー(0or1)
    private int before = -1;      // 直前に発生させたテトロミノID


    // スレッド用コンストラクタ
    Main(BufferedReader reader, int id) {
        this.reader = reader;
        this.id = id;
    }

    public static void main(String[] args) {
        // GUI表示
        gui = new GUIFrame();
    }

    // 入力を検知し、動作を実行するループ
    @Override
    public void run(){
        while(connected[id]) {
            try {
                String read;
                try {
                    read = reader.readLine();
                    /*
                     * nullはクライアントが切断した時
                     * "exit"はクライアントから終了命令がきた時
                     */
                    if(read == null || read.equals("exit")) {
                        // 切断処理
                        sendTo(id, "exit");
                        disconnect(id);
                        break;
                    }
                }
                catch (SocketException e) {
                    return;
                }
                doAction(read, id); // 入力に対して処理を実行
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 切断後にソケットを閉じる
        try {
            sockets[id].close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * サーバー開設
     */
    public static void open() {
        // ソケットを開く
        try {
            s = new ServerSocket(PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // GUIボタンを「接続する」から「終了する」に切り替える
        gui.connect.setVisible(false);
        gui.disconnect.setVisible(true);
        // テキストエリアにネットワークアドレスをすべて表示
        try {
            System.out.println(s);
            // ネットワークインターフェイスを取得
            Enumeration testMIP = NetworkInterface.getNetworkInterfaces();
            // 取得できた場合、処理を行う
            if (null != testMIP) {
                // ネットワークインターフェイスの全件を処理する
                while (testMIP.hasMoreElements()) {
                    // ネットワークインターフェイスを１件取得
                    NetworkInterface testNI = (NetworkInterface) testMIP.nextElement();
                    // ネットワークインターフェイスからInetAddressを取得
                    Enumeration testInA = testNI.getInetAddresses();
                    // InetAddressの全件を処理する
                    while (testInA.hasMoreElements()) {
                        // InetAddressを１件取得
                        InetAddress testIP = (InetAddress)testInA.nextElement();
                        // 取得した情報をログに出力
                        gui.IPs.append("Address: " + testIP.getHostAddress()+"\n");	// IPアドレス
                    }
                }
            } else {
                gui.IPs.append("ネットワークインターフェイス未取得");
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }

        // プレイヤーとの接続に関するスレッドを2つ(人数分)立てる
        for (int i = 0; i <= 1; i++) {
            final int player = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        // 勝者初期化
                        winnner = -1;
                        // 接続を受け付ける
                        acceptPlayer(player);
                        while (true) {
                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 自分が接続したのち相手が接続したらゲーム開始
                            if (connected[enemy(player)]) {
                                sendTo(player, "start"); // 開始の合図を送る
                                try {
                                    // 相手の終了(切断)を待つ
                                    th[enemy(player)].join();
                                    /*
                                     * 上の終了待ちでは
                                     * 1. 相手から切断した
                                     * 2. 自分が切断したせいで相手も切断された
                                     * の2通りが該当する。以下は1.に関する処理を行う
                                     */
                                    if(connected[player]) {
                                        sendTo(player, "disconnected");
                                        disconnect(player);
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            // 自分が切断していたら終了
                            else if(!connected[player]) {
                                break;
                            }
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * クライアントソケットを開設する
     * @param playerID クライアントのID(0or1)
     */
    private static void acceptPlayer(final int playerID) {
        try {
            System.out.println((playerID+1)+"Pの参加を待っています...");
            sockets[playerID] = s.accept();
            in[playerID] = new BufferedReader(new InputStreamReader(sockets[playerID].getInputStream()));
            out[playerID] = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sockets[playerID].getOutputStream())),true);
            in[playerID].readLine();
            out[playerID].println(playerID);// プレイヤー番号を送信
            th[playerID] = new Thread(new Main(in[playerID], playerID));
            gui.changeStatus(playerID, true);
            connected[playerID] = true;
            th[playerID].start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * クライアント接続ソケットを閉じる
     * @param playerID 閉じるクライアントのID(0or1)
     */
    private static void disconnect(final int playerID) {
        gui.changeStatus(playerID, false); // GUIの「接続済み」を「未接続」に変える
        // ソケットを閉じる
        try {
            sockets[playerID].close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // フラグとカウントの初期化
        connected[playerID] = false;
        mapCount[playerID] = 0;
    }

    /**
     * Clientから送られてきた文字列に応じて処理を決定する
     * 上のmain()メソッド中で受け付けた時以外に，送られてくると考えられる情報
     * ・妨害攻撃         "attack:lines"
     * ・盤面情報         "field:fieldString"
     * ・ホールドの通知    "hold:minoID"
     * ・次のミノの要求    "next"
     * ・レベルの通知      "level:levelInt"
     * ・スコアの通知      "score:scoreInt"
     * ・ゲームオーバー通知 "gameOver"
     */
    private void doAction(String str, int playerID){
        switch (str.charAt(0)){
            case 'a': // attack
                sendTo(enemy(playerID), "attack:" + str.substring(7));
                break;
            case 'f': // field
                sendTo(enemy(playerID), "enemyField:" + str.substring(6));
                break;
            case 'n': // next
                int minoCode = createMinoCode(playerID);
                sendTo(playerID, "next:" + minoCode);
                if(before != -1) sendTo(enemy(playerID), "enemyNext:" + before);
                before = minoCode;
                break;
            case 'h': // hold
                sendTo(enemy(playerID), "enemyHold:" + str.substring(5));
                break;
            case 'l': // level
                sendTo(enemy(playerID), "enemyLevel:" + str.substring(6));
                break;
            case 's': // score
                sendTo(enemy(playerID), "enemyScore:" + str.substring(6));
                break;
            case 'g': // gameOver
                // ほぼ同時にgameOverになったら、先に受信した方を敗者にする
                synchronized(this) {
                    if(winnner != playerID) {
                        setWinnner(enemy(playerID));
                    }
                }
                break;
        }
    }

    /**
     * winnerのsetterと、プレイヤーへの通知を担う
     * @param player 勝者ID(0or1)
     */
    private static void setWinnner(int player) {
        // 勝者と敗者の通知
        sendTo(player, "win");
        sendTo(enemy(player), "lose");
        // setter
        winnner = player;
    }

    /**
     * クライアントに文字列を送信する
     * @param target ID(0or1)
     * @param str String
     */
    private static void sendTo(int target, String str) {
        out[target].println(str);
    }

    /**
     * テトロミノのコード(0~6)を生成、プレイヤー間で違うものが出ないようにサーバーで統一する
     * @param playerID 0または1
     * @return int 0~6
     */
    private synchronized int createMinoCode(int playerID) {
        /*
         * HashMap<Integer, Integer>に<key, code>の対応で入れる。
         * 先に新しいコードを要求したプレイヤーには新しいコードを生成し、HashMapに入れると同時に返す。
         * 後にコードを要求したプレイヤーにはHashMapから対応するkeyのコードを返し、HashMapから削除する。
         * このためHashMapを排他制御する必要があるので、synchronizedメソッドとしている。
         *
         * ここでいうkeyはmapCount[]でインデックスが保持されている。
         */
        // 後から要求したプレイヤー
        if(mapCount[playerID] == Math.min(mapCount[0], mapCount[1]) && mapCount[0] != mapCount[1]) {
            int code = minoMap.get(mapCount[playerID]);
            minoMap.remove(mapCount[playerID]++);
            return code;
        }
        // 先に要求したプレイヤー
        int blockNo = rand.nextInt(7);
        int seedMinoNo = blockNo;// 乱数で得られたミノ
        while (true) {
            if (!minoflag[blockNo]) {
                minoMap.put(mapCount[playerID]++, blockNo);
                minoflag[blockNo] = true;
                return blockNo;
            } else {
                blockNo = (blockNo + 1) % 7;// 1つずらす
                // 1週したら初期化
                if (seedMinoNo == blockNo) {
                    minoflag = new boolean[7];
                }
            }
        }
    }

    /**
     * 自分のIDを元に敵のIDを返す
     * @param playerID 0or1
     * @return int 0or1or-1(error)
     */
    private static int enemy(int playerID) {
        if(playerID == 0) return 1;
        if(playerID == 1) return 0;
        return -1;
    }
}
