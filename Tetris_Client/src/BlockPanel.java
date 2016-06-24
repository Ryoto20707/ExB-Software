import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class BlockPanel extends JPanel {
    private static int MINI_TILE_SIZE = 14;

    private Tetromino mino;

    public BlockPanel() {
        super();
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (mino != null) {
            for (int i = 0; i < Tetromino.ROW; i++) {
                for (int j = 0; j < Tetromino.COL; j++) {
                    if (mino.block[i][j] != Tetromino.NONE) {
                        // ブロックの描画
                        g.setColor(Tetromino.getColor(mino.block[i][j]));
                        g.fillRect(j * MINI_TILE_SIZE + 10, i *  MINI_TILE_SIZE + 10, MINI_TILE_SIZE, MINI_TILE_SIZE);
                        // 枠線の描画
                        g.setColor(Color.WHITE);
                        g.drawRect(j * MINI_TILE_SIZE + 10, i *  MINI_TILE_SIZE + 10, MINI_TILE_SIZE, MINI_TILE_SIZE);
                    }
                }
            }
        }
    }

    /**
     * パネルにテトロミノを表示する
     * @param tetromino Tetrominoインスタンス
     */
    public void set(Tetromino tetromino) {
        this.mino = tetromino;
        repaint();
    }

    /**
     * パネルにテトロミノを表示する
     * @param blockID TetrominoのID
     */
    public void set(int blockID) {
        this.mino = TetrominoManager.generate(blockID);
        repaint();
    }
}
