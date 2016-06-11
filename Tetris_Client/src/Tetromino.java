import java.awt.*;

public abstract class Tetromino{

    // ブロックのサイズ
    public static final int ROW = 4;
    public static final int COL = 4;
    
    static final int TILE_SIZE = Window.TILE_SIZE;

    // 移動方向
    public static final int STAY  = 0;
    public static final int LEFT  = 1;
    public static final int RIGHT = 2;
    public static final int DOWN  = 3;
    protected int[][] block = new int[ROW][COL];

    Point pos;
    GameField field;

    Color color;

    /**
     * テトロミノの初期化
     * ただしこれは抽象クラスなので各形のミノがオーバーライドする
     * @param field 紐付けするフィールド（移動判定などのため）
     */
    Tetromino(GameField field) {
        this.field = field;
        // 全部を0（ブロックなし）で埋める
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                block[i][j] = 0;
            }
        }

        // 中央上から落下を開始する
        pos = new Point(4, -4);
    }


    /**
     * 実際にブロックをパネル内に描画する
     * @param g Graphics
     */
    public void drawInPanel(Graphics g) {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (block[i][j] == 1) {
                    // ブロックの描画
                    g.setColor(color);
                    g.fillRect((pos.x + j) *  TILE_SIZE,
                            (pos.y + i) *  TILE_SIZE,  TILE_SIZE,  TILE_SIZE);
                    // 枠線の描画
                    g.setColor(Color.WHITE);
                    g.drawLine((pos.x + j)     * TILE_SIZE, (pos.y + i)     *  TILE_SIZE,
                               (pos.x + j + 1) * TILE_SIZE, (pos.y + i)     *  TILE_SIZE);
                    g.drawLine((pos.x + j)     * TILE_SIZE, (pos.y + i)     *  TILE_SIZE,
                               (pos.x + j)     * TILE_SIZE, (pos.y + i + 1) *  TILE_SIZE);
                    g.drawLine((pos.x + j + 1) * TILE_SIZE, (pos.y + i)     *  TILE_SIZE,
                               (pos.x + j + 1) * TILE_SIZE, (pos.y + i + 1) *  TILE_SIZE);
                    g.drawLine((pos.x + j)     * TILE_SIZE, (pos.y + i + 1) *  TILE_SIZE,
                               (pos.x + j + 1) * TILE_SIZE, (pos.y + i + 1) *  TILE_SIZE);
                }
            }
        }
    }

    /**
     * dirの方向にブロックを移動
     *
     * @param dir 方向
     */
    public boolean move(int dir) {
        switch (dir) {
            case LEFT :
                Point newPos = new Point(pos.x - 1, pos.y);
                if (field.isMovable(newPos, block)) {
                    // 衝突しなければ位置を更新
                    pos = newPos;
                }
                break;
            case RIGHT :
                newPos = new Point(pos.x + 1, pos.y);
                if (field.isMovable(newPos, block)) {
                    pos = newPos;
                }
                break;
            case DOWN :
                newPos = new Point(pos.x, pos.y + 1);
                if (field.isMovable(newPos, block)) {
                    pos = newPos;
                } else {  // 移動できない＝他のブロックとぶつかる＝固定する
                    // ブロックをフィールドに固定する
                    field.fixBlock(pos, block);
                    // 固定されたらtrueを返す
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * ブロックを回転させる
     */
    public void turn() {
        /*
         * 壁蹴りを考慮するため、その場・右・左について回転判定を行う。
         * 回転可能と分かった時点で回転を実行する。
         */
        int[] moveX    = {0, 1, -1};
        int[] moveMino = {STAY, RIGHT, LEFT};

        for (int index:moveX) {
            Point newPos = new Point(pos.x+moveX[index], pos.y);
            if (field.isMovable(newPos, turnBasis(block))) {
                this.move(moveMino[index]);
                block = turnBasis(block);
                return;
            }
        }
    }

    /**
     * 上記turnで呼び出されるルーチン
     * @param prevBlock 回転前のint[4][4]内形状
     * @return int[][] 回転後の形状
     */
    private static int[][] turnBasis(int[][] prevBlock){
        int[][] turnedBlock = new int[ROW][COL];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                turnedBlock[j][ROW - 1 - i] = prevBlock[i][j];
            }
        }
        return turnedBlock;
    }


}
