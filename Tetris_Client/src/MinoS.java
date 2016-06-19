import java.awt.*;

public class MinoS extends Tetromino {

    public static Color color = Color.GREEN;

    MinoS(GameField gameField) {
        super(gameField);

        block[1][1] = 6;
        block[1][2] = 6;
        block[2][2] = 6;
        block[2][3] = 6;
    }
}
