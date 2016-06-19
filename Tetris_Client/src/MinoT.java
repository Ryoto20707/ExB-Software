import java.awt.*;

public class MinoT extends Tetromino {

    public static Color color = Color.MAGENTA;

    MinoT(GameField gameField) {
        super(gameField);

        block[1][1] = 7;
        block[2][1] = 7;
        block[2][2] = 7;
        block[3][1] = 7;
    }
}
