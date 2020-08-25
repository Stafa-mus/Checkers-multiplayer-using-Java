
package board;

/**
 * A class to represent a move in the game.
 * @author Mus
 */
public class Move {
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    /**
     * Constructor for a move using arrays
     * @param start array containing x1, y1
     * @param stop array containing x2, y2
     */
    public Move(int[] start, int [] stop){
        x1 = start[0];
        y1 = start[1];
        x2 = stop[0];
        y2 = stop[1];
    }
    
    public String toString(){
        String string = "";
        string += x1 + ", " + y1 + " to " + x2 + ", " + y2;
        return string;
    }
    
    public Move(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public int[] getStart(){
        int [] start = {x1, y1};
        return start;
    }
    
    public int[] getStop(){
        int[] stop = {x2, y2};
        return stop;
    }
    
    public int getX1(){
        return x1;
    }
    
    public int getX2(){
        return x2;
    }
    
    public int getY1(){
        return y1;
    }
    
    public int getY2(){
        return y2;
    }
}
