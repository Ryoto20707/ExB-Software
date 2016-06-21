import java.awt.*;

public class MinoL extends Tetromino {

    public static Color color = Color.ORANGE;
    public static int CODE = 2;

    MinoL(GameField gameField) {
        super(gameField);

        block[1][1] = CODE;
        block[2][1] = CODE;
        block[3][1] = CODE;
        block[1][2] = CODE;
    }
}
