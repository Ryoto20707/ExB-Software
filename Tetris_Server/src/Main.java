import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

public class Main implements Runnable {
    public static final int PORT = 8080;
    public static BufferedReader[] in;
    public static PrintWriter[] out;
    public static ServerSocket s; // フィールドに変更
    private static Socket[] sockets;
    private static boolean[] connected = {false, false};
    private static Thread[] th;

    public BufferedReader sender;
    public int id;
    private static Random rand = new Random(System.currentTimeMillis());
    private static boolean[] minoflag = new boolean[7];// ミノが偏らないようにするための処理に使う
    private static final HashMap<Integer, Integer> minoMap = new HashMap<Integer, Integer>();
    private static int[] mapCount = {0, 0};
    private int before = -1;
    private static int winnner;

    private static GUIFrame gui;

    // スレッド用コンストラクタ
    Main(BufferedReader sender, int id) {
        this.sender = sender;
        this.id = id;
    }

    /**
     * TODO
     * 切断が二回
     * IPとマシン名表示
     */
    public static void main(String[] args) {
        gui = new GUIFrame();
        in = new BufferedReader[2];
        out = new PrintWriter[2];
        sockets = new Socket[2];
        th = new Thread[2];
    }

    public static void open() {
        try {
            s = new ServerSocket(PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        gui.connect.setVisible(false);
        gui.disconnect.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                while(true) {
                    try {
                        String str = stdin.readLine();
                        if (str.equals("exit")) {
                            s.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
                System.out.println("ネットワークインターフェイス未取得");
            }

        } catch (SocketException e) {
            System.out.println("ネットワークインターフェイス取得エラー");
            e.printStackTrace();
        }
        for (int i = 0; i <= 1; i++) {
            final int player = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        winnner = -1;
                        acceptPlayer(player);
                        while (true) {
                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (connected[enemy(player)]) {
                                sendTo(player, "start");
                                try {
                                    th[enemy(player)].join();
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
                            else if(!connected[player]) {
                                break;
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void run(){
        // 入力を検知し、動作を実行するループ
        while(connected[id]) {
            try {
                String read;
                try {
                    read = sender.readLine();
                    // 切断されたら終了
                    if(read == null || read.equals("exit")) {
                        sendTo(id, "exit");
                        disconnect(id);
                        break;
                    }
                }
                catch (SocketException e) {
                    return;
                }
                doAction(read, id);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            sockets[id].close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disconnect(final int playerID) {
//        System.out.println((playerID + 1) + "Pが切断されました");
        gui.changeStatus(playerID, false);
        try {
            sockets[playerID].close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        connected[playerID] = false;
        mapCount[playerID] = 0;
    }

    private static void acceptPlayer(final int playerID) {
        try {
            System.out.println((playerID+1)+"Pの参加を待っています...");
                sockets[playerID] = s.accept();
            in[playerID] = new BufferedReader(new InputStreamReader(sockets[playerID].getInputStream()));
            out[playerID] = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sockets[playerID].getOutputStream())),true);
            in[playerID].readLine();
            out[playerID].println(playerID);// プレイヤー番号を送信
            th[playerID] = new Thread(new Main(in[playerID], playerID));
//            System.out.println((playerID+1)+"Pが参加しました");
            gui.changeStatus(playerID, true);
            connected[playerID] = true;
            th[playerID].start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clientから送られてきた文字列に応じて処理を決定する
     * 上のmain()メソッド中で受け付けた時以外に，送られてくると考えられる情報
     * ・fieldInfo  "field:<fieldInfo>"
     * ・attackInfo "attack:<attackinfo>"
     * ・nextMino   "next:<player>"
     * ・the End of Game (未開発)
     */
    private void doAction(String str, int playerID){
        switch (str.charAt(0)){
            case 'a':  // attackInfo
                sendTo(enemy(playerID), "attack:" + str.substring(7));
                break;
            case 'f':  // fieldInfo
                sendTo(enemy(playerID), "enemyField:" + str.substring(6));
                break;
            case 'n': // nextMino
                int minoCode = createMinoCode(playerID);
                sendTo(playerID, "next:" + minoCode);
                if(before != -1) sendTo(enemy(playerID), "enemyNext:" + before);
                before = minoCode;
                break;
            case 'h':
                sendTo(enemy(playerID), "enemyHold:" + str.substring(5));
                break;
            case 'l':
                sendTo(enemy(playerID), "enemyLevel:" + str.substring(6));
                break;
            case 's':
                sendTo(enemy(playerID), "enemyScore:" + str.substring(6));
                break;
            case 'g':
                synchronized(this) {
                    if(winnner != playerID) {
                        setWinnner(enemy(playerID));
                    }
                }
                break;
        }
    }

    private static void setWinnner(int player) {
        sendTo(player, "win");
        sendTo(enemy(player), "lose");
        winnner = player;
    }

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
