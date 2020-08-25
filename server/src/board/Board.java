
package board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A class to represent the board of the game
 * @author Mus
 */
public class Board implements Serializable {
    
    public char[][] board;
    
    public char turn;
    
    public int[][] highlighted;
    
    private static final int UP_RIGHT = 0;
    
    private static final int UP_LEFT = 1;
    
    private static final int DOWN_LEFT = 2;
    
    private static final int DOWN_RIGHT = 3;
    
    public static final char MAN_X = 'x';
    
    public static final char MAN_Y = 'y';
    
    public static final char KING_X = 'X';
    
    public static final char KING_Y = 'Y';
    
    public static final char EMPTY_SPACE = '-';
    
    public static final char UNFILLABLE_SPACE = '$';
    
    public static final char INVALID_POSITION = '@';
    
    public Board(){
        initialize();
    }
    
    public static Board copyBoard(Board board){
        Board tempBoard = new Board();
        tempBoard.setBoard(board.getBoard());
        tempBoard.setTurn(board.getTurn());
        if (board.highlighted != null && board.highlighted.length > 0){
            int length = board.highlighted.length;
            tempBoard.highlighted = new int[length][2];
            for (int i = 0; i < length; i++){
                tempBoard.highlighted[i][0] = board.highlighted[i][0];
                tempBoard.highlighted[i][1] = board.highlighted[i][1];
            }
        }
        return tempBoard;
    }
    
    public void setHighlighted(int[][] highlight){
        highlighted = new int[highlight.length][2];
        for (int i = 0; i < highlight.length; i++){
            for (int j = 0; j < highlight[i].length; j++){
                highlighted[i][j] = highlight[i][j];
            }
        }
    }
    
    public void setBoard(char[][] board){
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                this.board[i][j] = board[i][j];
            }
        }
    }
    
    private boolean canCapture(Move move){
        int[] start = move.getStart();
        int[] stop = move.getStop();
        int[] temp = {4, 3};
        if (isManX(start) || isKingX(start)){
            if (isManY(getBottomLeft(start)) || isKingY(getBottomLeft(start))){
                if (isEmpty(stop) && Arrays.equals(stop, getBottomLeft(getBottomLeft(start)))){
                    return true;
                }
            }
            if (isManY(getBottomRight(start)) || isKingY(getBottomRight(start))){
                if (isEmpty(stop) && Arrays.equals(stop, getBottomRight(getBottomRight(start)))){
                    //System.out.println("Returning true: isManX, isManY, bottom right");
                    return true;
                }
            }
        }
        else if (isManY(start) || isKingY(start)){
            if (isManX(getTopLeft(start)) || isKingX(getTopLeft(start))){
                if (isEmpty(stop) && Arrays.equals(stop, getTopLeft(getTopLeft(start)))){
                    
                    //System.out.println("Returning true: isManY, isManYX top left");
                    return true;
                }
            }
            if (isManX(getTopRight(start)) || isKingX(getTopRight(start))){
                if (isEmpty(stop) && Arrays.equals(stop, getTopRight(getTopRight(start)))){
                    return true;
                }
            }
        }
        if (isKing(start)){
            //System.out.println("Start is king");
            int direction = getMoveDirection(move);
            //System.out.println("The move: " + move);
            int[][] diagonal = getBottomLeftDiagonal(move.getStart(), move.getStop());
            switch (direction){
                case Board.DOWN_LEFT:
                    //System.out.println("Moving down left");
                    diagonal = getBottomLeftDiagonal(move.getStart(), move.getStop());
                    break;
                case Board.DOWN_RIGHT:
                    //System.out.println("Moving down right");
                    diagonal = getBottomRightDiagonal(move.getStart(), move.getStop());
                    break;
                case Board.UP_LEFT:
                    //System.out.println("Moving up left");
                    diagonal = getTopLeftDiagonal(move.getStart(), move.getStop());
                    break;
                case Board.UP_RIGHT:
                    //System.out.println("Moving up right");
                    diagonal = getTopRightDiagonal(move.getStart(), move.getStop());
                    break;
                default:
                    break;
            }
            //System.out.println("Printing diagonal");
            for (int i = 0; i < diagonal.length; i++){
                //System.out.println(diagonal[i][0] + "" + diagonal[i][1]);
            }
            if (isEmptyDiagonal(diagonal)){
                int[] pos = {1, 1};
                switch (direction){
                    case Board.UP_LEFT:
                        pos = getBottomRight(move.getStop());
                        break;
                    case Board.DOWN_LEFT:
                        pos = getTopRight(move.getStop());
                        break;
                    case Board.DOWN_RIGHT:
                        pos = getTopLeft(move.getStop());
                        break;
                    case Board.UP_RIGHT:
                        pos = getBottomLeft(move.getStop());
                        break;
                }
                boolean found = false;
                for (int i = 0; i < diagonal.length; i++){
                    if (Arrays.equals(pos, diagonal[i])){
                        found = true;
                    }
                }
                if (!found){
                    return false;
                }
                if (!isEmpty(move.getStop())){
                    return false;
                }
                if (isKingX(move.getStart())){
                    if (isManY(pos) || isKingY(pos)){
                        //System.out.println("Can capture");
                        //System.out.println("pos" + pos[0] + pos[1] );
                        //System.out.println("move" + move);
                        return true;
                    }
                }
                else if (isKingY(move.getStart())){
                    if (isManX(pos) || isKingX(pos)){
                        //System.out.println("Can capture");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    private int[] getBottomLeft(int[] position){
        int[] p = {position[0], position[1]};
        p[0] += 1;
        p[1] -= 1;
        return p;
    }
    
    private int[] getBottomRight(int[] position){
        int[] p = {position[0], position[1]};
        p[0] += 1;
        p[1] += 1;
        return p;
    }
    
    private int[] getTopLeft(int[] position){
        int[] p = {position[0], position[1]};
        p[0] -= 1;
        p[1] -= 1;
        return p;
    }
    
    private int[] getTopRight(int[] position){
        int[] p = {position[0], position[1]};
        p[0] -= 1;
        p[1] += 1;
        return p;
    }
    
    private boolean isEmpty(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.EMPTY_SPACE);
    }
    
    private int getMoveDirection(Move move){
        int[] start = move.getStart();
        int[] stop = move.getStop();
        int direction;
        if (start[0] > stop[0]){
            if (start[1] > stop[1]){
                direction = Board.UP_LEFT;
            }
            else{
                direction = Board.UP_RIGHT;
            }
        }
        else{
            if (start[1] > stop[1]){
                direction = Board.DOWN_LEFT;
            }
            else{
                direction = Board.DOWN_RIGHT;
            }
        }
        return direction;
    }
    
    public boolean isManX(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.MAN_X);
    }
    
    public boolean isManY(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.MAN_Y);
    }
    
    public boolean isKingX(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.KING_X);
    }
    
    public boolean isKingY(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.KING_Y);
    }
    
    private boolean isKing(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        return (board[position[0]][position[1]] == Board.KING_X || 
                board[position[0]][position[1]] == Board.KING_Y);
    }
    
    private int[][] getTopLeftDiagonal(int [] start, int[] stop){
        int[] tempArray;
        List<int[]> diagonalList = new ArrayList<>();
        int i = start[0]; int j = start[1];
        for (; i >= stop[0]; i--){
                tempArray = new int[2];
                tempArray[0] = i;
                tempArray[1] = j;
                diagonalList.add(tempArray);
                j--;
                if (j <= stop[1]){
                    break;
                }
        }
        
        int size = diagonalList.size();
        int[][] returnArray = new int[size][2];
        for (i = 0; i < size; i++){
            returnArray[i] = diagonalList.get(i);
        }
        return returnArray;
    }
    
    private int[][] getTopRightDiagonal(int [] start, int[] stop){
        int[] tempArray;
        List<int[]> diagonalList = new ArrayList<>();
        int i = start[0]; int j = start[1];
        for (; i >= stop[0]; i--){
                tempArray = new int[2];
                tempArray[0] = i;
                tempArray[1] = j;
                diagonalList.add(tempArray);
                j++;
                if (j >= stop[1]){
                    break;
                }
        }
        
        int size = diagonalList.size();
        int[][] returnArray = new int[size][2];
        for (i = 0; i < size; i++){
            returnArray[i] = diagonalList.get(i);
        }
        int temp[] = {3, 2};
        return returnArray;
    }
    
    private int[][] getBottomLeftDiagonal(int [] start, int[] stop){
        int[] tempArray;
        List<int[]> diagonalList = new ArrayList<>();
        int i = start[0]; int j = start[1];
        for (; i <= stop[0]; i++){
                tempArray = new int[2];
                tempArray[0] = i;
                tempArray[1] = j;
                diagonalList.add(tempArray);
                j--;
                if (j <= stop[1]){
                    break;
                }
        }
        
        int size = diagonalList.size();
        int[][] returnArray = new int[size][2];
        for (i = 0; i < size; i++){
            returnArray[i] = diagonalList.get(i);
        }
        return returnArray;
    }
    
    private int[][] getBottomRightDiagonal(int [] start, int[] stop){
        int[] tempArray;
        List<int[]> diagonalList = new ArrayList<>();
        int i = start[0]; int j = start[1];
        for (; i <= stop[0]; i++){
                tempArray = new int[2];
                tempArray[0] = i;
                tempArray[1] = j;
                diagonalList.add(tempArray);
                j++;
                if (j >= stop[1]){
                    break;
                }
        }
        
        int size = diagonalList.size();
        int[][] returnArray = new int[size][2];
        for (i = 0; i < size; i++){
            returnArray[i] = diagonalList.get(i);
        }
        int temp[] = {3, 2};
        if (Arrays.equals(start, temp)){
            for (i = 0; i < returnArray.length; i++){
            }
        }
        return returnArray;
    }
    
    private boolean isEmptyDiagonal(int[][] diagonal){
        if (diagonal.length <= 1){
            return false;
        }
        Move move = new Move(diagonal[1], diagonal[diagonal.length - 1]);
        int direction = getMoveDirection(move);
        boolean isEmpty = true;
        int a, b;
        switch (direction){
            case Board.UP_RIGHT:
                a = move.getX1();
                b = move.getY1();
                for (; a >= move.getX2(); a--){
                    int[] pos = {a, b};
                    if (!isEmpty(pos)){
                        isEmpty = false;
                    }
                    b++;
                    if (b >= move.getY2()){
                        break;
                    }
                }
                break;
            case Board.UP_LEFT:
                a = move.getX1();
                b = move.getY1();
                for (; a > move.getX2(); a--){
                    int[] pos = {a, b};
                    if (!isEmpty(pos)){
                        isEmpty = false;
                    }
                    b--;
                    if (b < move.getY2()){
                        break;
                    }
                }
                break;
            case Board.DOWN_LEFT:
                a = move.getX1();
                b = move.getY1();
                for (; a <= move.getX2(); a++){
                    int[] pos = {a, b};
                    if (!isEmpty(pos)){
                        isEmpty = false;
                    }
                    b--;
                    if (b <= move.getY2()){
                        break;
                    }
                }
                break;
            case Board.DOWN_RIGHT:
                a = move.getX1();
                b = move.getY1();
                for (; a < move.getX2(); a++){
                        int[] pos = {a, b};
                        if (!isEmpty(pos)){
                            isEmpty = false;
                        }
                        b++;
                        if (b > move.getY2()){
                            break;
                        }
                }
                break;
            default:
                break;
        }
        return isEmpty;
    }
    
    private boolean setEmpty(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        else{
            board[position[0]][position[1]] = Board.EMPTY_SPACE;
            return true;
        }
    }
    
    public boolean moveIsCapture(Move move){
        int[] start = move.getStart();
        int[] stop = move.getStop();
        Move[] capturingMoves = capturingMoves(start);
        for (Move m: capturingMoves){
            if (Arrays.equals(m.getStart() ,start) && 
                    Arrays.equals(m.getStop(), stop)){
                return true;
            }
        }
        return false;
    }
    
    
    private boolean isValidPosition(int[] position){
        if (position[0] < 0 || position[0] > 9 || 
                position[1] < 0 || position[1] > 9){
            return false;
        }
        else{
            return true;
        }
    }
    
    public void changeTurn(){
        if (turn == Board.MAN_X){
            turn = Board.MAN_Y;
        }
        else{
            turn = Board.MAN_X;
        }
    }
    
    public char getTurn(){
        return turn;
    }
    
    private char getPieceAt(int[] position){
        if (isValidPosition(position)){
            return board[position[0]][position[1]];
        }
        else{
            return Board.INVALID_POSITION;
        }
    }
    
    public boolean canMove(int[] position){
        Move[] nonCap = nonCapturingMoves(position);
        Move[] cap = capturingMoves(position);
        if (cap.length > 0 || nonCap.length > 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean play(Move move){
        if (canCapture(move)){
            makeMove(move);
            return true;
        }
        else{
            Move[] moves = nonCapturingMoves(move.getStart());
            for (Move m: moves){
                if (m.equals(move)){
                    makeMove(move);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
        String string = "  0 1 2 3 4 5 6 7 8 9\n";
        for (int i = 0; i < 10; i++){
            string += i + " ";
            for (int j = 0; j < 10; j++){
                string += String.valueOf(board[i][j]) + " ";
            }
            string += "\n";
        }
        return string;
    }
    
    public Move[] nonCapturingMoves(int[] position){
        List<Move> moves = new ArrayList<Move>();
        if (isManX(position)){
            if (isEmpty(getBottomLeft(position))){
                moves.add(new Move(position, getBottomLeft(position)));
            }
            if (isEmpty(getBottomRight(position))){
                moves.add(new Move(position, getBottomRight(position)));
            }
        }
        else if (isManY(position)){
            if (isEmpty(getTopRight(position))){
                moves.add(new Move(position, getTopRight(position)));
            }
            if (isEmpty(getTopLeft(position))){
                moves.add(new Move(position, getTopLeft(position)));
            }
        }
        else if (isKing(position)){
            if (isEmpty(getTopRight(position))){
                moves.add(new Move(position, getTopRight(position)));
            }
            if (isEmpty(getTopLeft(position))){
                moves.add(new Move(position, getTopLeft(position)));
            }
            if (isEmpty(getBottomRight(position))){
                moves.add(new Move(position, getBottomRight(position)));
            }
            if (isEmpty(getBottomLeft(position))){
                moves.add(new Move(position, getBottomLeft(position)));
            }
        }
        Move[] theMoves = new Move[moves.size()];
        for (int i = 0; i < moves.size(); i++){
            theMoves[i] = moves.get(i);
        }
        return theMoves;
    }
    
    public void makeMove(Move move){
        int direction = getMoveDirection(move);
        boolean capture = canCapture(move);
        int[] start = move.getStart();
        int[] stop = move.getStop();
        if (!capture){
            board[stop[0]][stop[1]] = board[start[0]][start[1]];
            board[start[0]][start[1]] = Board.EMPTY_SPACE;
        }
        else{
            int [] pos;
            switch (direction){
                case Board.UP_RIGHT:
                    board[stop[0]][stop[1]] = board[start[0]][start[1]];
                    board[start[0]][start[1]] = Board.EMPTY_SPACE;
                    pos = getBottomLeft(stop);
                    board[pos[0]][pos[1]] = Board.EMPTY_SPACE;
                    break;
                case Board.UP_LEFT:
                    board[stop[0]][stop[1]] = board[start[0]][start[1]];
                    board[start[0]][start[1]] = Board.EMPTY_SPACE;
                    pos = getBottomRight(stop);
                    board[pos[0]][pos[1]] = Board.EMPTY_SPACE;
                    break;
                case Board.DOWN_LEFT:
                    board[stop[0]][stop[1]] = board[start[0]][start[1]];
                    board[start[0]][start[1]] = Board.EMPTY_SPACE;
                    pos = getTopRight(stop);
                    board[pos[0]][pos[1]] = Board.EMPTY_SPACE;
                    break;
                case Board.DOWN_RIGHT:
                    board[stop[0]][stop[1]] = board[start[0]][start[1]];
                    board[start[0]][start[1]] = Board.EMPTY_SPACE;
                    pos = getTopLeft(stop);
                    board[pos[0]][pos[1]] = Board.EMPTY_SPACE;
                    break;
                default:
                    break;
            }
        }
        for (int i = 1; i < 10; i+= 2){
            int[] temp = {0, i};
            if (isManY(temp)){
                board[0][i] = Board.KING_Y;
            }
        }
        for (int i = 0; i < 10; i+= 2){
            int[] temp = {9, i};
            if (isManX(temp)){
                board[9][i] = Board.KING_X;
            }
        }
    }
    
    public void setPiece(int[] position, char piece){
        board[position[0]][position[1]] = piece;
    }
    
    public void setTurn(char turn){
        this.turn = turn;
    }
    
    public Move[] capturingMoves(int[] position){
        List<Move> moves = new ArrayList<>();
        int i = 0; 
        int j = 1;
        for (; i < 10; i++){
            for (j = 0; j < 10; j++){
                //int temp[] = {i, j};
                if (board[i][j] == Board.UNFILLABLE_SPACE ){
                    continue;
                }
                if (canCapture(new Move(position[0], position[1], i, j))){
                    moves.add(new Move(position[0], position[1], i, j));
                    if (j > 10) break;
                }
            }
        }
        Move[] theMoves = new Move[moves.size()];
        for (i = 0; i < moves.size(); i++){
            theMoves[i] = moves.get(i);
        }
        return theMoves;
    }
    
    public void test(){
        
    }
    
    public char[][] getBoard(){
        return board;
    }
    
    private void initialize(){
        Random random = new Random();
        int t = random.nextInt(2);
        if (t == 1){
            turn = Board.MAN_X;
        }
        else{
            turn = Board.MAN_Y;
        }
        board = new char[10][10];
        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 10; j++){
                board[i][j] = Board.UNFILLABLE_SPACE;
            }
        }
        for (int i = 0; i < 4; i++){
            if (! (i % 2 == 0)){
                for (int j = 0; j < 10; j+= 2){
                    board[i][j] = Board.MAN_X;
                }
            }
            else{
                for (int j = 1; j < 10; j+= 2){
                    board[i][j] = Board.MAN_X;
                }
            }
        }
        for (int i = 4; i < 6; i++){
            if (i % 2 == 0){
                for (int j = 1; j < 10; j += 2){
                    board[i][j] = Board.EMPTY_SPACE;
                }
            }
            else{
                for (int j = 0; j < 10; j += 2){
                    board[i][j] = Board.EMPTY_SPACE;
                }
            }
        }
        for (int i = 6; i < 10; i++){
            if ((i % 2 == 0)){
                for (int j = 1; j < 10; j+= 2){
                    board[i][j] = Board.MAN_Y;
                }
            }
            else{
                for (int j = 0; j < 10; j+= 2){
                    board[i][j] = Board.MAN_Y;
                }
            }
        }
    }
}
