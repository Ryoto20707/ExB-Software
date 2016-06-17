import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class GameField extends KeyPanel implements Runnable{
    private static final int WIDTH  = 10;
    private static final int HEIGHT = 22;
    private static final int COL = HEIGHT+1;
    private static final int ROW = WIDTH+2;
    private int[][] field = new int[COL][ROW];
    private boolean hold_flag;
    private Tetromino mino, nextMino, hold;
    private boolean[] minoflag = new boolean[7];// ミノが偏らないようにするための処理に使う
    private Random rand;
    private int score;// スコアを保持
    private int deletedline;// 消えた列数
    private int nextLines;// 次に送られてくる列数
    private int linehole;// せり上がるブロックの穴の位置
    private int lineholecount;// 同じ場所でせり上がった回数

    GameField() {
        super();
        init();
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        hold_flag = false;
        mino = createMino(this);
        nextMino = createMino(this);
        linehole = (int) (Math.random() * 10) + 1;
        while (true) {
            // ブロックを下方向へ移動する
            boolean isFixed = mino.move(Tetromino.DOWN);
            if (isFixed) { // ブロックが固定されたら
                if (isStacked()) {
                    JOptionPane.showMessageDialog(null, "GameOver!");
                }
                // 次のブロックをランダムに作成
                mino = nextMino;
                nextMino = createMino(this);
                hold_flag = false;
            }
            // ブロックがそろった行を消す
            deleteLine();
            repaint();

            try {
                Thread.sleep(200);
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
                }
                else if (y == COL - 1) {
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

        // 落下ミノ選択用乱数
        rand = new Random();
        rand.setSeed(System.currentTimeMillis());
    }

    /**
     * 新しいテトロミノをランダムに生成
     *
     * @param gameField
     *            紐付けするフィールド
     * @return 作成されたテトロミノ
     */
    private Tetromino createMino(GameField gameField) {
        // ミノが偏らないようにするための処理を追加
        int blockNo = rand.nextInt(7);
        int seedMinoNo = blockNo;// 乱数で得られたミノ
        while (true) {
            if (!minoflag[blockNo]) {
                minoflag[blockNo] = true;
        switch (blockNo) {
            case 0:
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
            } else {
                blockNo = (blockNo + 1) % 7;// 1つずらす
                // 1週したら初期化
                if (seedMinoNo == blockNo) {
                    minoflag = new boolean[7];
                }
                continue;
            }
        return null;
    }
    }

    /**
     * 行を消去
     */
    public void deleteLine() {
        // 何列消えたか数える
        deletedline = 0;
        for (int y = 0; y < COL - 1; y++) {
            int count = 0;
            for (int x = 1; x < ROW - 1; x++) {
                // ブロックがある列の数を数える
                if (field[y][x] == 1)
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
    }

    /**
     * 現在の場所にテトロミノを固定する
     * @param pos 座標
     * @param block 形状
     */
    public void fixBlock(Point pos, int[][] block) {
        for (int i = 0; i < Tetromino.ROW; i++) {
            for (int j = 0; j < Tetromino.COL; j++) {
                if (block[i][j] == 1) {
                    if (pos.y + i < 0) continue;
                    // フィールドに埋め込む
                    field[pos.y + i][pos.x + j] = 1;
                }
            }
        }
    }

    /**
     * ホールドを行う
     * ただし一度行ったらそのミノがつくまで次のホールドはできない
     */
    public void hold() {
        /**
         * TODO
         * PositionにSetterを作成
         */
        // フラグが付いてない場合のみホールド実行
        if(!hold_flag) {
            // 座標を初期位置に
            mino.pos.x = 4;
            mino.pos.y = -4;
            /*
             * まだ一度もホールドをしたことがない
             * 新規ブロックを作り次のミノにする
             */
            if(hold == null) {
                hold = mino;
                mino = nextMino;
                nextMino = createMino(this);
            }
            /*
             * 一度はした
             * 現在のミノとホールドを入れ替える
             */
            else {
                Tetromino tmp = mino;
                mino = hold;
                hold = tmp;
            }
            // フラグをセット。一度地面につくまで変更できない。
            hold_flag = true;
        }
    }

    // スコア計算
    public void getScore(int lines) {
        switch (lines) {
        case 1:
            score += 100;
            break;
        case 2:
            score += 300;
            break;
        case 3:
            score += 500;
            break;
        case 4:
            score += 1000;
            break;
        }
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

        // 枠線
        g.setColor(Color.LIGHT_GRAY);
        for (int y = 0; y < COL; y++) {
            for (int x = 0; x < ROW; x++) {
                if (field[y][x] == 1) {
                    g.fillRect(x * Window.TILE_SIZE, y *  Window.TILE_SIZE,  Window.TILE_SIZE,
                            Window.TILE_SIZE);
                }
            }
        }
        // テトロミノを描画
        mino.drawInPanel(g);
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
        }
        else if (key == KeyEvent.VK_C) {
            hold();
        }
        // 回転・移動後に再描画
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    /**
     * ブロックを移動できるか調べる
     * @param newPos ブロックの移動先座標
     * @param block ブロック
     * @return boolean 移動できたらtrue
     */
    public boolean isMovable(Point newPos, int[][] block) {
        // 各座標において衝突判定
        for (int i=0; i<Tetromino.ROW; i++) {
            for (int j=0; j<Tetromino.COL; j++) {
                if (block[i][j] == 1) {
                    // ミノが見えきってない時
                    if (newPos.y + i < 0) {
                        // 横の壁（ただし画面外は描画していない）にめり込む時不可
                        if (newPos.x + j <= 0 || newPos.x+j >= COL-1) {
                            return false;
                        }
                    }
                    // 移動先に壁や他のミノがある時不可
                    else if (field[newPos.y+i][newPos.x+j] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * ミノが画面に積みきってしまったか調べる（ゲームオーバー判定）
     * @return boolean 上端まできたらtrue
     */
    public boolean isStacked() {
        for (int x = 1; x < WIDTH + 1; x++) {
            if (field[0][x] == 1) {
                return true;
            }
        }
        return false;
    }
}
