import java.awt.*;

public class MinoZ extends Tetromino {

    public static Color color = Color.RED;

    MinoZ(GameField gameField) {
        super(gameField);

        block[2][1] = 8;
        block[2][2] = 8;
        block[1][2] = 8;
        block[1][3] = 8;
    }
}
