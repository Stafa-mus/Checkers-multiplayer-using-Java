/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;



/**
 * The server class
 * @author user
 */

import board.Board;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import game.StartGame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
    
    Server server;
    boolean client1Registered = false;
    public static final int MAIN_PORT = 12345;
    
    private class NewConnection extends Connection{
        
    }
    
    public void start() throws IOException{
        Board board = new Board();
        char firstTurn = Board.MAN_X;
        server = new Server(){
            @Override
            protected Connection newConnection(){
                System.out.println("New connection");
                NewConnection conn = new NewConnection();
                Registration r = new Registration();
                r.board = board;
                r.turn = firstTurn;
                //conn.sendTCP(r);
                return conn;
            }
        };
        Kryo kryo = server.getKryo();
        kryo.register(Board.class);
        kryo.register(server.Registration.class);
        kryo.register(char[][].class);
        kryo.register(char[].class);
        kryo.register(int[][].class);
        kryo.register(server.GameMessage.class);
        kryo.register(game.StartGame.class);
        server.start();
        server.bind(MAIN_PORT);
        server.addListener(new Listener(){
            
            @Override
            public void disconnected(Connection connection){
                if (server.getConnections().length <= 0){
                    System.exit(0);
                }
            }
            
            @Override
            public void received(Connection connection, Object object){
                //System.out.println("Received something: ");
                if (object instanceof GameMessage){
                    System.out.println("Turns out this something is a board");
                    for (Connection conn: server.getConnections()){
                        conn.sendTCP(object);
                    }
                }
                else if (object instanceof Registration){
                    System.out.println("A client is trying to register with the server...");
                    Registration r = new Registration();
                    r.board = board;
                    if (!client1Registered){
                        r.turn = firstTurn;
                        client1Registered = true;
                    }
                    else{
                        r.turn = Board.MAN_Y;
                    }
                    connection.sendTCP(r);
                    if (server.getConnections().length >= 2){
                        StartGame st = new StartGame();
                        st.startGame = "START";
                        server.sendToAllTCP(st);
                        System.out.println("Sent start game message");
                    }
                }
                else{
                    //System.out.println("An unknown error occured.");
                    //System.out.println(object.getClass());
                }
            }
        });
    }
    
    public static void main(String[] args){
       GameServer s = new GameServer();
        try {
            s.start();
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
