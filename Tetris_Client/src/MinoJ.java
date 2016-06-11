import java.awt.*;

public class MinoJ extends Tetromino {

    MinoJ(GameField gameField) {
        super(gameField);

        block[1][2] = 1;
        block[2][2] = 1;
        block[3][2] = 1;
        block[1][1] = 1;

        color = Color.BLUE;
    }
}
