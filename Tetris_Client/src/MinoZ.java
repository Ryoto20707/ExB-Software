import java.awt.*;

public class MinoZ extends Tetromino {

    public static Color color = Color.RED;
    public static int CODE = 6;

    MinoZ(GameField gameField) {
        super(gameField);

        block[2][1] = CODE;
        block[2][2] = CODE;
        block[1][2] = CODE;
        block[1][3] = CODE;
    }
}
