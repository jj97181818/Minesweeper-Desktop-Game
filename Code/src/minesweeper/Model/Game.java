package minesweeper;

import java.io.File;
import java.net.URISyntaxException;
import javafx.util.Pair;
import java.util.Observable;
import java.util.Observer;

public class Game extends Observable
{
    public static String dbPath;
    // "playing" indicates whether a game is running (true) or not (false).
    private boolean playing; 

    private Board board;
    
    private Score score;
    
    private int timePassed;
    private int mines;
              
    public Game()
    {
        // set db path
        String p = "";

        try 
        {
            p = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + "/db.accdb";
        }
        catch (URISyntaxException ex) 
        {
            System.out.println("Error loading database file.");
        }

        dbPath =   "jdbc:ucanaccess://" + p;

        createBoard();
        
        score = new Score();
        score.getScoreFromDB();
                        
        this.playing = false;
        
    }

    
    public Board getBoard() {
        return this.board;
    }
    
    public Score getScore() {
        return this.score;
    }
    
    public boolean getPlaying() {
        return this.playing;
    }
   
    public boolean setPlaying(boolean playing) {
        return this.playing = playing;
    }
    
    public int getTimePassed() {
        return this.timePassed;
    }
    
    public int getMines() {
        return this.mines;
    }
    //-----------------Load Save Game (if any)--------------------------//
    
    public void resumeGame()
    {
        //load board's state
        Pair p = board.loadBoardState();
        this.timePassed = (int)p.getKey();
        this.mines = (int)p.getValue();
        playing = true;
    }
    //------------------------------------------------------------//
        
    public void createBoard()
    {
        // Create a new board        
        int mines = 10;

        int r = 9;
        int c = 9;
                
        this.board = new Board(mines, r, c);        
    }
    

    //---------------------------------------------------------------//
    public void newGame()
    {                
        this.playing = false;      
        createBoard();
    }
    //------------------------------------------------------------------------------//
    
    public void restartGame()
    {
        this.playing = false;
        board.resetBoardState();
    }
        
    //------------------------------------------------------------------------------//    
    public void endGame()
    {
        playing = false;
        score.saveScoreIntoDB();
    }

    
    //-------------------------GAME WON AND GAME LOST ---------------------------------//
    public void gameWon() {
        getScore().increaseCurrentStreak();
        getScore().increaseCurrentWinningStreak();
        getScore().increaseGamesWon();
        getScore().increaseGamesPlayed();
        endGame();
        
        setChanged();
        notifyObservers("Won");
    }
    
    
    public void gameLost()
    {
        score.decreaseCurrentStreak();
        score.increaseCurrentLosingStreak();
        score.increaseGamesPlayed();
        endGame();
        
        setChanged();
        notifyObservers("Lost");
    }
    
    public boolean isFinished()
    {
        boolean isFinished = true;
        String cellSolution;

        Cell cells[][] = board.getCells();
        
        for( int x = 0 ; x < board.getCols() ; x++ ) 
        {
            for( int y = 0 ; y < board.getRows() ; y++ ) 
            {
                // If a game is solved, the content of each Cell should match the value of its surrounding mines
                cellSolution = Integer.toString(cells[x][y].getSurroundingMines());
                
                if(cells[x][y].getMine()) 
                    cellSolution = "F";

                // Compare the player's "answer" to the solution.
                if(!cells[x][y].getContent().equals(cellSolution))
                {
                    //This cell is not solved yet
                    isFinished = false;
                    break;
                }
            }
        }

        return isFinished;
    }

 
    public void leftClick(int x, int y) {
        // Get cell information.
        boolean isMine = board.getCells()[x][y].getMine();
        int neighbours = board.getCells()[x][y].getSurroundingMines();
        
        if (!board.getCells()[x][y].getContent().equals("F"))
        {
            //Mine is clicked.
            if(isMine) 
            {  
                //red mine
                getBoard().getCells()[x][y].setContent("M");

                gameLost();
            }
            else 
            {
                // The player has clicked on a number.
                board.getCells()[x][y].setContent(Integer.toString(neighbours));
                
                if( neighbours == 0 ) 
                {
                    // Show all surrounding cells.
                    findSurroundingZeroes(x, y);
                } 
            }
        }                                     
    }                           
    
    public void rightClick(int x, int y) {
        if(board.getCells()[x][y].getContent().equals("F")) 
        {   
            board.getCells()[x][y].setContent("");
            setChanged();
            notifyObservers("Mines++");
        }
        else if (board.getCells()[x][y].getContent().equals("")) 
        {
            board.getCells()[x][y].setContent("F");
            setChanged();
            notifyObservers("Mines--");
        }
    }
    
    //Check the game to see if its finished or not
    public void checkGame()
    {		
        if(isFinished()) 
        {            
            gameWon();
        }
    }
   
    //----------------------------------------------------------------------/
       
    
    /*
     * If a player clicks on a zero, all surrounding cells ("neighbours") must revealed.
     * This method is recursive: if a neighbour is also a zero, his neighbours must also be revealed.
     */
    public void findSurroundingZeroes(int xCo, int yCo)
    {
        int neighbours;
        
        Cell cells[][] = board.getCells();
        
        // Columns
        for(int x = board.makeValidCoordinateX(xCo - 1) ; x <= board.makeValidCoordinateX(xCo + 1) ; x++) 
        {			
            // Rows
            for(int y = board.makeValidCoordinateY(yCo - 1) ; y <= board.makeValidCoordinateY(yCo + 1) ; y++) 
            {
                // Only unrevealed cells need to be revealed.
                if(cells[x][y].getContent().equals("")) 
                {
                    // Get the neighbours of the current (neighbouring) cell.
                    neighbours = cells[x][y].getSurroundingMines();

                    // Reveal the neighbours of the current (neighbouring) cell
                    cells[x][y].setContent(Integer.toString(neighbours));
           
                    // Is this (neighbouring) cell a "zero" cell itself?
                    if(neighbours == 0)
                    {                        
                        // Yes, give it a special color and recurse!
                        findSurroundingZeroes(x, y);
                    }
                }
            }
        }
    }
}
