import java.awt.*;

public class MinoZ extends Tetromino {

    public static Color color = Color.RED;
    public static int CODE = 6;

    MinoZ() {
        super();
        code = CODE;

        block[1][1] = CODE;
        block[1][2] = CODE;
        block[2][2] = CODE;
        block[2][3] = CODE;
    }
}
