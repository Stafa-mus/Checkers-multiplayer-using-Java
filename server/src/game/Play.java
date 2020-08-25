
package game;

import board.Board;
import board.Move;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import server.GameMessage;
import server.GameServer;
import server.Registration;

/**
 *
 * @author Mus
 */
public class Play extends BasicGameState {

    public static String ipAddress;
    
    public static void intiateThings(){
        System.out.println("initiating things");
        client = new Client();
        System.out.println("init called!");
        Kryo kryo = client.getKryo();
        kryo.register(Board.class);
        kryo.register(server.Registration.class);
        kryo.register(char[][].class);
        kryo.register(char[].class);
        kryo.register(int[][].class);
        kryo.register(server.GameMessage.class);
        kryo.register(game.StartGame.class);
        client.start();
        try {
            client.connect(5000, ipAddress, GameServer.MAIN_PORT);
        } 
        catch (java.net.SocketTimeoutException x){
            System.out.println("Cannot connect to server!");
        }
        catch (Exception ex) {
            Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "We could not connect you to the "
                    + "server at " + Module.SERVER_IP, "Cannot connect", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        client.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object){
                if (object instanceof GameMessage){
                    GameMessage message = (GameMessage)object;
                    Module.BOARD = message.board;
                    Module.BOARD.setTurn(message.turn);
                }
                else if (object instanceof server.Registration){
                    server.Registration r = (server.Registration)object;
                    Module.BOARD = r.board;
                    myTurn = r.turn;
                }
                else if (object instanceof StartGame){
                    opponentConnected = true;
                }
            }
        });
        Registration r = new Registration();
        r.turn = 'a';
        client.sendTCP(r);
    }
    
    private static boolean opponentConnected = false;
    
    /**
     * Who's turn is it?
     */
    String currentTurn = "";
    /**
     * Write time up message on the screen
     */
    String timesUp = "";
    
    /**
     * Keep track of the highlighted fields that will be sent to the server
     */
    List<int[]> highlighted = new ArrayList<>();
    
    /**
     * Prevent a variable from being accessed while another thread is modifying it
     */
    ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
    * The possible moves the player can make
    */
    List<Move> possibleMoves = new ArrayList<Move>();
    
    /**
     * The cell that the mouse is over
     */
    List<int[]> mouseOver = new ArrayList<>();
    
    /**
     * What character represents my turn?
     */
    private static char myTurn;
    
    /**
     * How much time do I have left to make a move?
     */
    private int timeLeft = 0; //10 seconds
    
    /**
     * The position containing the piece that can move.
     */
    int[] firstPosition = null;
    /**
     * The position that is the destination of the piece selected
     */
    int[] secondPosition = null;
    /**
     * How much time has elapsed since we last updated the frame on the screen?
     */
    long elapsedTime = 0;
    /**
     * A method to get the ID of this screen
     * @return An ID representing this 'screen' of game play, as opposed to the pause screen or game over screen
     */
    @Override
    public int getID() {
        return Module.PLAY_SCREEN_ID;
    }

    /**
     * The user's connection to the server
     */
    private static Client client;
    
    /**
     * Called when this screen is first created
     * @param container the game container
     * @param state the state that contains the container
     * @throws SlickException if some error occurred, and the state could not be initialized
     */
    @Override
    public void init(GameContainer container, StateBasedGame state) throws SlickException {
        
    }

    
    /**
     * Send the game state to the server
     */
    private void sendState(){
        GameMessage message = new GameMessage();
        message.board = Module.BOARD;
        message.turn = Module.BOARD.getTurn();
        client.sendTCP(message);
        
    }
    
    /**
     * Convert a row and column to a screen coordinate. Used for example to highlight a cell
     * @param row the row 
     * @param column the column
     * @return an array containing the top left corner ...why am I documenting this code?
     */
    private int[] toScreen(int row, int column){
        int[] r = new int[2];
        r[1] = Module.PIECE_SQUARE * row + Module.MARGIN_Y;
        r[0] = Module.PIECE_SQUARE * column + Module.MARGIN_X;
        return r;
    }
    
    private int[] fromScreen(int x, int y){
        int[] r = new int[2];
        //y = (int)(y / Module.MARGIN_Y) * Module.MARGIN_Y;
        y = (int)((y - Module.MARGIN_Y) / Module.PIECE_SQUARE);
        x = (int)((x - Module.MARGIN_X) / Module.PIECE_SQUARE);
        r[0] = (int)(y / Module.PIECE_SQUARE) - 3;
        r[1] = (int)(x / Module.PIECE_SQUARE) - 5;
        r[0] = y;
        r[1] = x;
        return r;
    }
    
    private void drawBoard(Graphics g){
        char[][] board = Module.BOARD.getBoard();
        g.setColor(Module.LINE_COLOR);
        for (int i = Module.MARGIN_Y; i <= Module.MARGIN_Y + Module.PIECE_SQUARE * 10;
                i+=Module.PIECE_SQUARE){
            g.drawLine(Module.MARGIN_X, i, Module.MARGIN_X + Module.PIECE_SQUARE * 10, i);
        }
        for (int j = Module.MARGIN_X; j <= Module.MARGIN_X + Module.PIECE_SQUARE * 10; 
                j += Module.PIECE_SQUARE){
            g.drawLine(j, Module.MARGIN_Y, j, Module.MARGIN_Y + Module.PIECE_SQUARE * 10);
        }
        g.setColor(Module.UNFILLABLE_SLOT_COLOR);
        for (int i = Module.MARGIN_X; i < Module.MARGIN_X + Module.PIECE_SQUARE * 10; i += Module.PIECE_SQUARE){
            for (int j = Module.MARGIN_Y; j < Module.MARGIN_Y + Module.PIECE_SQUARE * 10; j += Module.PIECE_SQUARE){
                g.fillRect(i, j, Module.PIECE_SQUARE, Module.PIECE_SQUARE);
            }
        }
        Color drawColor  = null;
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                int[] coord = new int[2];
                char piece = board[i][j];
                Circle circle = null;
                int radius = Module.PIECE_SQUARE / 2;
                switch (piece){
                    case Board.MAN_X:
                        drawColor = Module.PLAYER_X_MAN_COLOR;
                        g.setColor(Module.EMPTY_SPACE_COLOR);
                        coord = toScreen(i, j);
                        g.fillRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
                        circle = new Circle(coord[0] + radius, coord[0] + radius, radius);
                        g.setColor(drawColor);
                        g.fillOval(coord[0] + 3, coord[1] + 3, Module.PIECE_SQUARE - 5, Module.PIECE_SQUARE - 5);
                        break;
                    case Board.MAN_Y:
                        drawColor = Module.PLAYER_Y_MAN_COLOR;
                        g.setColor(Module.EMPTY_SPACE_COLOR);
                        coord = toScreen(i, j);
                        g.fillRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
                        g.setColor(drawColor);
                        g.fillOval(coord[0] + 3, coord[1] + 3, Module.PIECE_SQUARE - 5, Module.PIECE_SQUARE - 5);
                        break;
                    case Board.EMPTY_SPACE:
                        drawColor = Module.EMPTY_SPACE_COLOR;
                        g.setColor(Module.EMPTY_SPACE_COLOR);
                        coord = toScreen(i, j);
                        g.setColor(drawColor);
                        g.fillRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
                        break;
                    case Board.UNFILLABLE_SPACE:
                        drawColor = Module.UNFILLABLE_SLOT_COLOR;
                        break;
                    case Board.KING_X:
                        drawColor = Module.PLAYER_X_KING_COLOR;
                        g.setColor(Module.EMPTY_SPACE_COLOR);
                        coord = toScreen(i, j);
                        g.fillRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
                        g.setColor(drawColor);
                        g.fillOval(coord[0] + 3, coord[1] + 3, Module.PIECE_SQUARE - 5, Module.PIECE_SQUARE - 5);
                        break;
                    case Board.KING_Y:
                        drawColor = Module.PLAYER_Y_KING_COLOR;
                        g.setColor(Module.EMPTY_SPACE_COLOR);
                        coord = toScreen(i, j);
                        g.fillRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
                        g.setColor(drawColor);
                        g.fillOval(coord[0] + 3, coord[1] + 3, Module.PIECE_SQUARE - 5, Module.PIECE_SQUARE - 5);
                        break;
                }
                
            }
        }
        g.setColor(Module.HIGHLIGHT_COLOR);
        g.setLineWidth(2);
        for (int[] i: highlighted){
            int[] coord = toScreen(i[0], i[1]);
            g.drawRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
        }
        for (int[] i: mouseOver){
            int[] coord = toScreen(i[0], i[1]);
            g.drawRect(coord[0], coord[1], Module.PIECE_SQUARE, Module.PIECE_SQUARE);
        }
    }
    
    private void drawOpponentNotConnected(Graphics g, GameContainer container){
        Font font = new Font("calibri", 25, 25);
        TrueTypeFont theFont = new TrueTypeFont(font, true);
        g.setFont(theFont);
        int[] pos = new int[2];
        pos[0] = 50;
        pos[1] = 350;
        g.setColor(Color.red);
        g.drawString("Opponent not yet connected or wrong IP address", pos[0], pos[1]);
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame state, Graphics g) throws SlickException {
        g.setBackground(Module.BACKGROUND_COLOR);
        g.setColor(Color.red);
        if (!opponentConnected){
            drawOpponentNotConnected(g, container);
            return;
        }
        if (Module.BOARD != null){
            drawBoard(g);
            drawClock(g, container);
            //if (Module.BOARD.getTurn() == myTurn){
            int[] pos = {Module.MARGIN_X, 400};
            Color color;
            if (myTurn == Board.MAN_X){
                color = Module.PLAYER_X_MAN_COLOR;
            }
            else{
                color = Module.PLAYER_Y_MAN_COLOR;
            }
            drawString(currentTurn, color, pos, g);
            if (myTurn == Board.MAN_X){
                color = Module.PLAYER_Y_MAN_COLOR;
            }
            else{
                color = Module.PLAYER_X_MAN_COLOR;
            }
            pos[1] += 20;
            drawString(timesUp, color, pos, g);
            //}
        }
        else{
            
        }
    }

    private void drawClock(Graphics g, GameContainer container){
        g.setBackground(Module.BACKGROUND_COLOR);
        int[] centerPoint = {container.getWidth()/2, (Module.MARGIN_Y / 2)+ 5};
        int clockRadius = (Module.MARGIN_Y / 2) - 7;
        //System.out.println("Center: " + centerPoint[0] + centerPoint[1]);
        //System.out.println("Radius: " + clockRadius);
        g.drawOval(centerPoint[0] - clockRadius, centerPoint[1] - clockRadius, clockRadius*2, clockRadius*2);
        int[] tip = {centerPoint[0], centerPoint[1] - clockRadius + 5};
        double[]x = {tip[0]};
        double[]y = {tip[1]};
        double angle  = 360 / Module.MAX_MOVE_TIME * timeLeft;
        angle = Math.toRadians(angle);
        rotatePoints(x, y, centerPoint[0], centerPoint[1], angle);
        g.drawLine(centerPoint[0], centerPoint[1], (int)x[0], (int)y[0]);
    }
    
    void rotatePoints(
        double[] x,     //X coords to rotate - replaced on return
        double[] y,     //Y coords to rotate - replaced on return
        double cx,      //X coordinate of center of rotation
        double cy,      //Y coordinate of center of rotation
        double angle)   //Angle of rotation (radians, counterclockwise)
{
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double temp;
    for( int n=0; n<x.length; n++ ){
        temp = ((x[n]-cx)*cos - (y[n]-cy)*sin) + cx;  
        y[n] = ((x[n]-cx)*sin + (y[n]-cy)*cos) + cy;
        x[n] = temp;
    }
    return;
}
    
    private void drawString(String string, Color color, int[] position, Graphics g){
        g.setFont(Module.FONT_1);
        g.setColor(color);
        g.drawString(string, position[0], position[1]);
    }
    
    private boolean isValidClick(int[] click){
        if (click[0] < 0 || click[0] > 9){
            return false;
        }
        if (click[1] < 0 || click[1] > 9){
            return false;
        }
        if (myTurn == Board.MAN_X){
            if (Module.BOARD.isManX(click) || Module.BOARD.isKingX(click)){
                return true;
            }
            else{
                return false;
            }
        }
        else if (myTurn == Board.MAN_Y){
            if (Module.BOARD.isManY(click) || Module.BOARD.isKingY(click)){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
    
    private boolean isValidFirstClick(int[] position){
        boolean isValid = isCellClicked(position);
        if (isValid){
            if (Module.BOARD.canMove(position)){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    private boolean isCellClicked(int[] position){
        if (position[0] < 0 || position[0] > 9 || position[1] < 0 || position[1] > 9){
            return false;
        }
        else{
            return true;
        }
    }
    
    
    
    @Override
    public void update(GameContainer container, StateBasedGame state, int delta) throws SlickException {
        if (!opponentConnected){
            return;
        }
        if (myTurn == Module.BOARD.getTurn() && Module.BOARD != null){
            if (elapsedTime >= 1000){
            elapsedTime= 0;
            timeLeft += 1;
            }
            else{
                elapsedTime += delta;
            }
            if (timeLeft >= Module.MAX_MOVE_TIME){
                timeLeft = 0;
                timesUp = "Time's out!";
                Module.BOARD.changeTurn();
                timeLeft = 0;
                sendState();
            }
        }
        
        if ( Module.BOARD != null){
            if (myTurn == Module.BOARD.getTurn()){
                currentTurn = "It's your turn";
            }
            else{
                currentTurn = "Wait for opponent...";
            }
        }
        Input userInput = container.getInput();
        int mouseX = userInput.getMouseX();
        int mouseY = userInput.getMouseY();
        int[] highl = fromScreen(mouseX, mouseY);
        mouseOver.clear();
        if (isCellClicked(highl)){
            mouseOver.add(highl);
        }
        if (Module.BOARD != null && myTurn == Module.BOARD.getTurn()){
            timesUp = "";
        }
        if (userInput.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            int[] selectedCoord = fromScreen(mouseX, mouseY);
            if (!(Module.BOARD.getTurn() == myTurn)){
                System.out.println("It is not your turn");
                currentTurn = "Ola, it's not your turn yet";
            }
            else{
                timesUp = "";
            }
            if (isCellClicked(selectedCoord) && Module.BOARD.getTurn() == myTurn){
                if (firstPosition == null && isValidClick(selectedCoord)){
                    highlighted.clear();
                    if (Module.BOARD.canMove(selectedCoord)){
                        firstPosition = Arrays.copyOf(selectedCoord, selectedCoord.length);
                        highlighted.clear();
                        highlighted.add(firstPosition);
                        possibleMoves.clear();
                        Move[] c = Module.BOARD.capturingMoves(firstPosition);
                        for (Move m: c){
                            possibleMoves.add(m);
                            
                        }
                        c = Module.BOARD.nonCapturingMoves(firstPosition);
                        for (Move m: c){
                            possibleMoves.add(m);
                        }
                    }
                    else{
                        System.out.println("Please click a piece that can move.");
                    }
                }
                else if (firstPosition != null){
                    secondPosition = Arrays.copyOf(selectedCoord, selectedCoord.length);
                    Move tempMove = new Move(firstPosition, secondPosition);
                    boolean isCapture = false;
                    boolean isValidMove = false;
                    for (Move m: possibleMoves){
                        if (Arrays.equals(m.getStart(), tempMove.getStart()) && 
                                Arrays.equals(m.getStop(), tempMove.getStop())){
                            isValidMove = true;
                        }
                    }
                    if (!isValidMove){
                        secondPosition = null;
                        firstPosition = null;
                        possibleMoves.clear();
                        highlighted.clear();
                    }
                    else{
                        Move[] cap = Module.BOARD.capturingMoves(tempMove.getStart());
                        for (Move m: cap){
                            if (Arrays.equals(m.getStart(), tempMove.getStart()) && 
                                    Arrays.equals(m.getStop(), tempMove.getStop())){
                                isCapture = true;
                                System.out.println("Move is capture.");
                                break;
                            }
                        }
                        try{
                            lock.writeLock().lock();
                            Module.BOARD.makeMove(tempMove);
                        }
                        finally{
                            lock.writeLock().unlock();
                        }
                        highlighted.clear();
                        
                        if (isCapture){
                            cap = Module.BOARD.capturingMoves(tempMove.getStop());
                            if (cap.length > 0){
                                for (Move mov: cap){
                                    possibleMoves.add(mov);
                                }
                                firstPosition = Arrays.copyOf(secondPosition, secondPosition.length);
                                highlighted.clear();
                                highlighted.add(firstPosition);
                                secondPosition = null;
                                sendState();
                            }
                            else{
                                firstPosition = null;
                                secondPosition = null;
                                possibleMoves.clear();
                                Module.BOARD.changeTurn();
                                timeLeft = 0;
                                sendState();
                            }
                        }
                        else{
                            firstPosition = null;
                            secondPosition = null;
                            Module.BOARD.changeTurn();
                            timeLeft = 0;
                            sendState();
                        }
                    }
                }
            }
            else{
                System.out.println("Point clicked is not a valid cell.");
            }
        }
    }
    
}

