import java.awt.*;

public class MinoO extends Tetromino {

    MinoO(GameField gameField) {
        super(gameField);

        block[1][1] = 1;
        block[1][2] = 1;
        block[2][1] = 1;
        block[2][2] = 1;

        color = Color.YELLOW;
    }
}
