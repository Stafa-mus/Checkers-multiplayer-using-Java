
package game;

import board.Board;
import java.awt.Font;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;


/**
 *
 * @author Mus
 */
public class Module {
    public static final Color BACKGROUND_COLOR = Color.lightGray;
    public static final Color PLAYER_X_MAN_COLOR = Color.red;
    public static final Color PLAYER_Y_MAN_COLOR = Color.green;
    public static final Color PLAYER_X_KING_COLOR = Color.red;
    public static final Color PLAYER_Y_KING_COLOR = Color.gray;
    public static final Color UNFILLABLE_SLOT_COLOR = Color.pink;
    public static final Color EMPTY_SPACE_COLOR = Color.yellow;
    public static final Color LINE_COLOR = Color.green;
    public static final Color HIGHLIGHT_COLOR = Color.blue;
    public static final Color CLOCK_COLOR = Color.green;
    
    
    public static final int PLAY_SCREEN_ID = 0;
    public static final int REQUEST_IP_ID = 1;
    
    
    private static final Font font_1 = new Font("Verdana", 20, 20);
    public static final TrueTypeFont FONT_1 = new TrueTypeFont(font_1, true);
    
    public static volatile Board BOARD;
    
    public static final int PIECE_SQUARE = 30;
    public static final int MARGIN_X = 170;
    public static final int MARGIN_Y = 90;
    public static String SERVER_IP;
    public static int MAX_MOVE_TIME = 20; 
}
