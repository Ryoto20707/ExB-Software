import java.awt.*;

public class MinoL extends Tetromino {

    MinoL(GameField gameField) {
        super(gameField);

        block[1][1] = 1;
        block[2][1] = 1;
        block[3][1] = 1;
        block[1][2] = 1;

        color = Color.ORANGE;
    }
}
