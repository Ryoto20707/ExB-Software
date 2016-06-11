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
    private Tetromino mino, nextMino;
    private Random rand;

    GameField() {
        super();
        init();
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        mino = createMino(this);
        while (true) {
            // ブロックを下方向へ移動する
            boolean isFixed = mino.move(Tetromino.DOWN);
            if (isFixed) {  // ブロックが固定されたら
                if (isStacked()) {
                    JOptionPane.showMessageDialog(null, "GameOver!");
                }
                // 次のブロックをランダムに作成
                nextMino = createMino(this);
                mino = nextMino;
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

    // 新しいテトロミノを生成
    private Tetromino createMino(GameField gameField) {
        int blockNo = rand.nextInt(7);
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
        return null;
    }

    /**
     * 行を消去
     */
    public void deleteLine() {
        for (int y = 0; y < COL - 1; y++) {
            int count = 0;
            for (int x = 1; x < ROW - 1; x++) {
                // ブロックがある列の数を数える
                if (field[y][x] == 1)
                    count++;
            }
            // 消去判定
            if (count == ROW - 2) {
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
     * フィールドの描画
     * @param g Graphics
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
        /**
         * TODO
         * 上でhard_drop(一瞬で落とす)
         */
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
