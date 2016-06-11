import java.awt.*;

public class MinoZ extends Tetromino {

    MinoZ(GameField gameField) {
        super(gameField);

        block[2][1] = 1;
        block[2][2] = 1;
        block[1][2] = 1;
        block[1][3] = 1;

        color = Color.RED;
    }
}
