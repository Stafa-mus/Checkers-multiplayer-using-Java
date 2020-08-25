
package checkersmultiplayer;

import board.Board;
import game.Main;
import game.Module;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 * The main class for the checkers multi player game
 * @author user
 */
public class CheckersMultiplayer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CheckersMultiplayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        try {
            AppGameContainer container = new AppGameContainer(new Main());
            container.setDisplayMode(640, 480, false);
            container.setMusicOn(true);
            container.setSoundOn(true);
            container.setShowFPS(false);
            container.setTitle("Checkers Multiplayer");
            container.setUpdateOnlyWhenVisible(false);
            container.start();
        } catch (SlickException ex) {
            Logger.getLogger(CheckersMultiplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
