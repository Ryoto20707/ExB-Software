import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class GameField extends KeyPanel implements Runnable {
    public static final int SINGLE = 1;
    public static final int DOUBLE_SELF = 2;
    public static final int DOUBLE_ENEMY = 3;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 22;
    public static final int COL = HEIGHT + 1;
    public static final int ROW = WIDTH + 2;
    private static int TILE_SIZE = Window.TILE_SIZE;
    private int[][] field = new int[COL][ROW];
    private boolean hold_flag;
    private Tetromino mino, nextMino, hold;
    private String[] attackAlternative = {"NOEFFECT", "SINGLE", "DOUBLE", "TRIPLE", "TETRIS"};
    private int level;// レベル
    private int totalline;// 消したラインの総数
    private int score;// スコアを保持
    private int deletedline;// 消えた列数
    private int nextLines;// 次に送られてくる列数
    private int linehole;// せり上がるブロックの穴の位置
    private int lineholecount;// 同じ場所でせり上がった回数
    private StatPanel statPanel; // 盤面右側のパネル
    private BlockPanel nextPanel, holdPanel; // 次のブロックとホールドのプレビュー
    private NextMinoManager nextMinoManager;
    private CommunicationClient client;

    GameField(StatPanel statPanel, int player, CommunicationClient client) {
        super();
        switch (player) {
            case SINGLE :
                nextMinoManager = new NextMinoManager();
                break;
            case DOUBLE_SELF :
                nextMinoManager = new NextMinoManager(client);
                break;
            default :
                break;
        }
        setLayout(new BorderLayout());
        // 各パネルの紐付け
        this.statPanel = statPanel;
        this.nextPanel = statPanel.nextPanel;
        this.holdPanel = statPanel.holdPanel;
        // 盤面の初期化
        init();
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        mino = nextMinoManager.create(this);
        nextPanel.set(mino);
        nextMino = nextMinoManager.create(this);
        try {
            // カウントダウン
            statPanel.message.setText("3");
            Thread.sleep(1000);
            statPanel.message.setText("2");
            Thread.sleep(1000);
            statPanel.message.setText("1");
            Thread.sleep(1000);
            // Startの表示とゲームの開始は非同期にする
            statPanel.setMessage("Start", 1000);
        }
        catch (InterruptedException e) {

        }
        hold_flag = false;
        nextPanel.set(nextMino);
        while (true) {
            // ブロックを下方向へ移動する
            boolean isFixed = mino.move(Tetromino.DOWN);
            if (isFixed) {  // ブロックが固定されたら
                // 盤面変化の情報を送る
                String filedString = getFieldString();
//                sendFieldInfoToServer(fieldString);    // CommunicationClientのインスタンスに対して適用
                // ブロックがそろった行を消す
                int attackNum = deleteLine();

            // 消した行数に応じて，罰ゲームに関する情報を送る
//            while(sendAttackInfoToServer(attackAlternative[attackNum]) == -1); // CommunicationClientのインスタンスに対して適用
                // スコアを加算する
                getScore(deletedline);
                // 相殺込みで何列送るか計算して送る
                sendLine(sendLineCount(deletedline));
                // ブロックせり上がり処理
                riseLine(nextLines);

                if (isStacked()) {
                    repaint(); // ゲームオーバー後にも更新
                    JOptionPane.showMessageDialog(null, "GameOver!");
                    break;
                }
                // 10ライン消すごとにレベルアップ(上限は10)
                if (totalline / 10 + 1 != level && level < 10) {
                    statPanel.setLevel(++level);
                }
                // 次のブロックをランダムに作成
                mino = nextMino;
                nextMino = nextMinoManager.create(this);
                nextPanel.set(nextMino);
                hold_flag = false;
                // せり上がりが4回を超えたら場所を変更
                if (lineholecount == 4) {
                    lineholecount = 0;
                    linehole = (int) (Math.random() * 10) + 1; // 1から10の乱数
                }
            }

            repaint();

            try {
                Thread.sleep(300-level*25); // レベルが上がると猶予が小さくなる
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ゲーム画面の初期化を行う
     */
    public void init() {
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROW; x++) {
                // 壁をすべて埋める
                if (x == 0 || x == ROW - 1) {
                    field[y][x] = 1;
                } else if (y == COL - 1) {
                    field[y][x] = 1;
                }
                // フィールド内は埋めない
                else {
                    field[y][x] = 0;
                }
            }
        }
        // キーボードが反応するための準備
        setFocusable(true);
        addKeyListener(this);

        // レベルを1にする
        level = 1;
    }

    /**
     * 盤面の状況を（壁の状況も含めて)String化する
     */
    private String getFieldString(){
        // 高速化できる
        StringBuffer ret = new StringBuffer();
        for(int i = 0; i < COL; i++)
            for (int j = 0; j < ROW; j++)
                ret.append(String.valueOf(field[i][j]));
        return ret.toString();
    }


    /**
     * 行を消去
     */
    public int deleteLine() {
        // 何列消えたか数える
        deletedline = 0;
        for (int y = 0; y < COL - 1; y++) {
            int count = 0;
            for (int x = 1; x < ROW - 1; x++) {
                // ブロックがある列の数を数える
                if (field[y][x] > 0)
                    count++;
            }
            // 消去判定
            if (count == ROW - 2) {
                deletedline++;
                for (int x = 1; x < ROW - 1; x++) {
                    field[y][x] = 0;
                }
                // 上段をすべて落下させる
                for (int ty = y; ty > 0; ty--) {
                    for (int tx = 1; tx < ROW - 1; tx++) {
                        field[ty][tx] = field[ty - 1][tx];
                    }
                }
            }
        }
        totalline += deletedline; // 消えた列数を反映
        return deletedline;
    }

    /**
     * 現在の場所にテトロミノを固定する
     *
     * @param pos
     *            座標
     * @param block
     *            形状
     */
    public void fixBlock(Point pos, int[][] block) {
        for (int i = 0; i < Tetromino.ROW; i++) {
            for (int j = 0; j < Tetromino.COL; j++) {
                if (block[i][j] > 1) {
                    if (pos.y + i < 0)
                        continue;
                    // フィールドに埋め込む
                    field[pos.y + i][pos.x + j] = block[i][j];
                }
            }
        }
    }

    /**
     * ホールドを行う ただし一度行ったらそのミノがつくまで次のホールドはできない
     */
    public void hold() {
        /**
         * TODO PositionにSetterを作成
         */
        // フラグが付いてない場合のみホールド実行
        if (!hold_flag) {
            // 座標を初期位置に
            mino.pos.x = 4;
            mino.pos.y = -4;
            /*
             * まだ一度もホールドをしたことがない 新規ブロックを作り次のミノにする
             */
            if (hold == null) {
                // 現在のミノをホールドに送りパネルにセット
                hold = mino;
                holdPanel.set(hold);
                // 次のミノを更新しパネルにセット
                mino = nextMino;
                nextMino = nextMinoManager.create(this);
                nextPanel.set(nextMino);
            }
            /*
             * 一度はした 現在のミノとホールドを入れ替える
             */
            else {
                // スワップ
                Tetromino tmp = mino;
                mino = hold;
                hold = tmp;
                // パネルにセット
                holdPanel.set(hold);
            }
            // フラグをセット。一度地面につくまで変更できない。
            hold_flag = true;
        }
    }

    // スコア計算
    public void getScore(int lines) {
        switch (lines) {
            case 1:
                score += level * 100;
                break;
            case 2:
                score += level * 300;
                break;
            case 3:
                score += level * 500;
                break;
            case 4:
                score += level * 1000;
                break;
        }
        statPanel.changeScore(score);
    }

    // 相手に送る列数を数える
    public int sendLineCount(int lines) {
        switch (lines) {
        case 1:
            return 0;
        case 2:
            return 1;
        case 3:
            return 2;
        case 4:
            return 4;
        default:
            return 0;
        }
    }

    // 相手に送るブロックの列を計算(相殺あり)
    public void sendLine(int send) {
        if (nextLines < send) {
            pushLine(send - nextLines);
            nextLines = 0;
        } else {
            nextLines -= send;
        }
    }

    // 相手にブロックの列を送る処理
    public void pushLine(int push) {

    }

    // ブロックがせり上がる処理
    public void riseLine(int rise) {
        if (rise == 0)
            return;
        // 上段をせり上げる
        for (int ty = 0; ty < COL - rise - 1; ty++) {
            for (int tx = 1; tx < ROW - 1; tx++) {
                field[ty][tx] = field[ty + rise][tx];
            }
        }
        // linehole以外を埋める
        for (int ty = COL - rise - 1; ty < COL - 1; ty++) {
            for (int tx = 1; tx < ROW - 1; tx++) {
                if (tx != linehole)
                    field[ty][tx] = 1;
                else
                    field[ty][tx] = 0;
            }
        }
        // 同じ場所でせり上がった回数を数える
        lineholecount++;
    }

    /**
     * フィールドの描画
     *
     * @param g
     *            Graphics
     */
    public void paintComponent(Graphics g) {
        // 背景色
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Window.WIDTH, Window.HEIGHT);

        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROW; x++) {
                if (field[y][x] > 0) {
                    g.setColor(Tetromino.getColor(field[y][x]));
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    if (field[y][x] > 1) {
                        // 枠線の描画
                        g.setColor(Color.WHITE);
                        g.drawLine( x      * TILE_SIZE,  y      *  TILE_SIZE,
                                   (x + 1) * TILE_SIZE,  y      *  TILE_SIZE);
                        g.drawLine( x      * TILE_SIZE,  y      *  TILE_SIZE,
                                    x      * TILE_SIZE, (y + 1) *  TILE_SIZE);
                        g.drawLine((x + 1) * TILE_SIZE,  y      *  TILE_SIZE,
                                   (x + 1) * TILE_SIZE, (y + 1) *  TILE_SIZE);
                        g.drawLine( x      * TILE_SIZE, (y + 1) *  TILE_SIZE,
                                   (x + 1) * TILE_SIZE, (y + 1) *  TILE_SIZE);
                    }
                }
            }
        }
        // テトロミノを描画
        try {
            mino.drawInPanel(g);
        }
        catch (NullPointerException e) {

        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        // 左
        if (key == KeyEvent.VK_LEFT) {
            mino.move(Tetromino.LEFT);
        }
        // 右
        else if (key == KeyEvent.VK_RIGHT) {
            mino.move(Tetromino.RIGHT);
        }
        // 下
        else if (key == KeyEvent.VK_DOWN) {
            mino.move(Tetromino.DOWN);
        }
        // スペースで回転
        else if (key == KeyEvent.VK_SPACE) {
            mino.turn();
        }
        // 上でハードドロップ
        else if (key == KeyEvent.VK_UP) {
            mino.hardDrop();
        } else if (key == KeyEvent.VK_C) {
            hold();
        }
        // 回転・移動後に再描画
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    /**
     * ブロックを移動できるか調べる
     *
     * @param newPos
     *            ブロックの移動先座標
     * @param block
     *            ブロック
     * @return boolean 移動できたらtrue
     */
    public boolean isMovable(Point newPos, int[][] block) {
        // 各座標において衝突判定
        for (int i = 0; i < Tetromino.ROW; i++) {
            for (int j = 0; j < Tetromino.COL; j++) {
                if (block[i][j] > 0) {
                    // ミノが見えきってない時
                    if (newPos.y + i < 0) {
                        // 横の壁（ただし画面外は描画していない）にめり込む時不可
                        if (newPos.x + j <= 0 || newPos.x + j >= COL - 1) {
                            return false;
                        }
                    }
                    // 移動先に壁や他のミノがある時不可
                    else if (field[newPos.y + i][newPos.x + j] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * ミノが画面に積みきってしまったか調べる（ゲームオーバー判定）
     *
     * @return boolean 上端まできたらtrue
     */
    public boolean isStacked() {
        for (int x = 1; x < WIDTH + 1; x++) {
            if (field[0][x] > 0) {
                return true;
            }
        }
        return false;
    }
}
