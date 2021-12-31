package minesweeper;

public class Controller {
    private UI gui;
    private Game game;

    public Controller() {
        int mines = 10;
        int r = 9;
        int c = 9;
        Board board = new Board(mines, r, c);
        
        UI.setLook("Nimbus");
        
        this.gui = new UI(board.getRows(), board.getCols(), board.getNumberOfMines());  
        this.game = new Game(gui, board);
        this.gui.setButtonListeners(game);
        
        gui.setVisible(true);
        gui.setIcons();        
        gui.hideAll();
    }
}
