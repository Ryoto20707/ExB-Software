import java.awt.*;

public class MinoT extends Tetromino {

    public static Color color = Color.MAGENTA;
    public static int CODE = 5;

    MinoT() {
        super();
        code = CODE;

        block[1][1] = CODE;
        block[2][1] = CODE;
        block[2][2] = CODE;
        block[3][1] = CODE;
    }
}
