import java.awt.*;

public class MinoS extends Tetromino {

    MinoS(GameField gameField) {
        super(gameField);

        block[1][1] = 1;
        block[1][2] = 1;
        block[2][2] = 1;
        block[2][3] = 1;

        color = Color.GREEN;
    }
}
