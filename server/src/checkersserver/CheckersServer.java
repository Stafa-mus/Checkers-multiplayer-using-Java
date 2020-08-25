/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkersserver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.GameServer;

/**
 *
 * @author user
 */
public class CheckersServer {

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
            java.util.logging.Logger.getLogger(CheckersServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        ServerFrame frame = new ServerFrame();
        frame.setVisible(true);
    }
    
}
