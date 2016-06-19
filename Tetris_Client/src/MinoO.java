import java.awt.*;

public class MinoO extends Tetromino {

    public static Color color = Color.YELLOW;

    MinoO(GameField gameField) {
        super(gameField);

        block[1][1] = 5;
        block[1][2] = 5;
        block[2][1] = 5;
        block[2][2] = 5;
    }
}
