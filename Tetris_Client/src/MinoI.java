import java.awt.*;

public class MinoI extends Tetromino {

    MinoI(GameField gameField) {
        super(gameField);

        block[0][1] = 1;
        block[1][1] = 1;
        block[2][1] = 1;
        block[3][1] = 1;

        color = Color.CYAN;
    }
}
