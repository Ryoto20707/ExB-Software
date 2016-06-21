import java.awt.*;

public class MinoJ extends Tetromino {

    public static Color color = Color.BLUE;
    public static int CODE = 1;

    MinoJ(GameField gameField) {
        super(gameField);

        block[1][2] = CODE;
        block[2][2] = CODE;
        block[3][2] = CODE;
        block[1][1] = CODE;
    }
}
