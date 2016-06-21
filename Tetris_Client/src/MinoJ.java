import java.awt.*;

public class MinoJ extends Tetromino {

    public static Color color = Color.BLUE;
    public static int CODE = 1;

    MinoJ() {
        super();
        code = CODE;

        block[1][2] = CODE;
        block[2][2] = CODE;
        block[3][2] = CODE;
        block[3][1] = CODE;
    }
}
