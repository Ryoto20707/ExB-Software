import java.awt.*;

public class MinoS extends Tetromino {

    public static Color color = Color.GREEN;
    public static int CODE = 4;

    MinoS(GameField gameField) {
        super(gameField);

        block[1][1] = CODE;
        block[1][2] = CODE;
        block[2][2] = CODE;
        block[2][3] = CODE;
    }
}
