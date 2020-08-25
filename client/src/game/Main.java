
package game;

import javax.swing.JOptionPane;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Mus
 */
public class Main extends StateBasedGame{

    public Main(){
        super("Checkers");
    }
    
    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        
        addState(new RequestIP());
        addState(new Play());
        enterState(Module.REQUEST_IP_ID);
        
    }
    
    public void startGame(){
        enterState(Module.PLAY_SCREEN_ID);
    }
    
}
