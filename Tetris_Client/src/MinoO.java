import java.awt.*;

public class MinoO extends Tetromino {

    public static Color color = Color.YELLOW;
    public int CODE = 3;

    MinoO() {
        super();
        code = CODE;

        block[1][1] = CODE;
        block[1][2] = CODE;
        block[2][1] = CODE;
        block[2][2] = CODE;
    }
}
