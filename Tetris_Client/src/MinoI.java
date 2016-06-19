import java.awt.*;

public class MinoI extends Tetromino {

    public static Color color = Color.CYAN;

    MinoI(GameField gameField) {
        super(gameField);

        block[0][1] = 2;
        block[1][1] = 2;
        block[2][1] = 2;
        block[3][1] = 2;
    }
}
