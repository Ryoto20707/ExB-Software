import java.util.Random;

public class NextMinoManager {
    public static final int SINGLE = 1;
    public static final int DOUBLE = 2;

    private boolean[] minoflag = new boolean[7];// ミノが偏らないようにするための処理に使う
    private Random rand;
    private int type;
    private CommunicationClient client;

    /**
     * シングルプレイ用のマネージャコンストラクタ
     */
    public NextMinoManager() {
        // 落下ミノ選択用乱数
        rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        type = SINGLE;
    }

    /**
     * ダブルプレイ用のマネージャコンストラクタ
     * @param client 通信のためのクライアント
     */
    public NextMinoManager(CommunicationClient client) {
        this.client = client;
        type = DOUBLE;
    }

    /**
     * 新しいテトロミノをランダムに生成
     *
     * @param gameField
     *            紐付けするフィールド
     * @return 作成されたテトロミノ
     */
    public Tetromino create(GameField gameField) {
        int code;
        if (type == SINGLE) {
            code = createMinoCode();
            return generateMino(code, gameField);
        }
        if (type == DOUBLE) {
            code = client.getMinoCode();
            return generateMino(code, gameField);
        }
        return null;
    }

    /**
     * テトロミノを生成するためのコードをクライアントで発行
      * @return int 0~6
     */
    public int createMinoCode() {
        // ミノが偏らないようにするための処理を追加
        int blockNo = rand.nextInt(7);
        int seedMinoNo = blockNo;// 乱数で得られたミノ
        while (true) {
            if (!minoflag[blockNo]) {
                minoflag[blockNo] = true;
                return blockNo;
            }
            else {
                blockNo = (blockNo + 1) % 7;// 1つずらす
                // 1週したら初期化
                if (seedMinoNo == blockNo) {
                    minoflag = new boolean[7];
                }
            }
        }
    }

    /**
     * コードを元にテトロミノのインスタンスを作成
     * @param blockNo コード
     * @param gameField 紐づけるフィールド
     * @return Tetromino
     */
    public Tetromino generateMino(int blockNo, GameField gameField) {
        switch (blockNo) {
            case 0 :
                return new MinoI(gameField);
            case 1 :
                return new MinoO(gameField);
            case 2 :
                return new MinoZ(gameField);
            case 3 :
                return new MinoL(gameField);
            case 4 :
                return new MinoS(gameField);
            case 5 :
                return new MinoT(gameField);
            case 6 :
                return new MinoJ(gameField);
        }
        return null;
    }
}
