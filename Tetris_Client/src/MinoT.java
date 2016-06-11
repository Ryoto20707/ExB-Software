import java.awt.*;

public class MinoT extends Tetromino {

    MinoT(GameField gameField) {
        super(gameField);

        block[1][1] = 1;
        block[2][1] = 1;
        block[2][2] = 1;
        block[3][1] = 1;

        color = Color.MAGENTA;
    }
}
