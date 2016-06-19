import java.awt.*;

public class MinoJ extends Tetromino {

    public static Color color = Color.BLUE;

    MinoJ(GameField gameField) {
        super(gameField);

        block[1][2] = 3;
        block[2][2] = 3;
        block[3][2] = 3;
        block[1][1] = 3;
    }
}
