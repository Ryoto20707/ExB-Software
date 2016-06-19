import java.awt.*;

public class MinoL extends Tetromino {

    public static Color color = Color.ORANGE;

    MinoL(GameField gameField) {
        super(gameField);

        block[1][1] = 4;
        block[2][1] = 4;
        block[3][1] = 4;
        block[1][2] = 4;
    }
}
