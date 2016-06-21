import java.awt.*;

public class MinoI extends Tetromino {

    public static Color color = Color.CYAN;
    public static int CODE = 0;

    MinoI() {
        super();
        code = CODE;

        block[0][1] = CODE;
        block[1][1] = CODE;
        block[2][1] = CODE;
        block[3][1] = CODE;
    }
}
