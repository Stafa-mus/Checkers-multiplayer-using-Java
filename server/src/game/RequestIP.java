/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author user
 */
public class RequestIP extends BasicGameState{

    private String ipAddress = "";
    
    @Override
    public int getID() {
        return Module.REQUEST_IP_ID;
    }

    TrueTypeFont font;
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        Font f = new Font("calibri", 35, 35);
        font = new TrueTypeFont(f, true);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(Module.BACKGROUND_COLOR);
        g.setFont(font);
        int[] pos = {60, 150};
        g.setColor(Color.blue);
        g.drawString("Type game server IP address: ", pos[0], pos[1]);
        g.setColor(Color.red);
        g.drawString("IP: " + ipAddress, pos[0] + 50, pos[1] + 50);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game){
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Input input = container.getInput();
        List<Integer> numericKeys = new ArrayList<Integer>();
        numericKeys.add(Input.KEY_0);
        numericKeys.add(Input.KEY_1);
        numericKeys.add(Input.KEY_2);
        numericKeys.add(Input.KEY_3);
        numericKeys.add(Input.KEY_4);
        numericKeys.add(Input.KEY_5);
        numericKeys.add(Input.KEY_6);
        numericKeys.add(Input.KEY_7);
        numericKeys.add(Input.KEY_8);
        numericKeys.add(Input.KEY_9);
        numericKeys.add(Input.KEY_PERIOD);
        if (input.isKeyPressed(Input.KEY_BACK)){
            if (ipAddress.length() > 0){
                ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
            }
        }
        for (int i: numericKeys){
            if (input.isKeyPressed(i)){
                if (Input.getKeyName(i).equalsIgnoreCase("PERIOD")){
                    ipAddress += ".";
                }
                else{
                    ipAddress += Input.getKeyName(i);
                }
                
            }
        }
        if (input.isKeyPressed(Input.KEY_ENTER)){
            Module.SERVER_IP = ipAddress;
            //System.out.println("Server ip after hitting Enter: " + Module.SERVER_IP);
            game.addState(new Play());
            Module.SERVER_IP = ipAddress;
            Play.ipAddress = ipAddress;
            Play.intiateThings();
            game.enterState(Module.PLAY_SCREEN_ID);
        }
    }
    
}
