package minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.border.TitledBorder;
import minesweeper.Time;

public class Controller implements MouseListener, ActionListener, WindowListener {
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
        this.gui.setButtonListeners(this);
        
        gui.setVisible(true);
        gui.setIcons();        
        gui.hideAll();
        resumeGameOrNot();
    }
 
    public void resumeGameOrNot()
    {
        if(game.getBoard().checkBoardStateExist())
        {
            ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

            int option = JOptionPane.showOptionDialog(null, "Do you want to continue your saved game?", 
                            "Saved Game Found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, question,null,null);

            switch(option) 
            {
                case JOptionPane.YES_OPTION:      
      
                    game.resumeGame();
                    
                    //set button's images
                    setButtonImages();
                    
                    //load timer's value                                        
                    gui.setTimePassed(game.getTimePassed());

                    //load mines value
                    gui.setMines(game.getMines());
                    
                    gui.startTimer();
                    
                    break;

                case JOptionPane.NO_OPTION:
                    game.getBoard().deleteBoardState();
                    break;
                    
                case JOptionPane.CLOSED_OPTION:
                    game.getBoard().deleteBoardState();
                    break;
            }
        }
    }
    
    public void setButtonImages()
    {
        Cell cells[][] = game.getBoard().getCells();
        JButton buttons[][] = gui.getButtons();
        
        for( int y=0 ; y<game.getBoard().getRows() ; y++ ) 
        {
            for( int x=0 ; x<game.getBoard().getCols() ; x++ ) 
            {
                buttons[x][y].setIcon(null);
                
                if (cells[x][y].getContent().equals(""))
                {
                    buttons[x][y].setIcon(gui.getIconTile());
                }
                else if (cells[x][y].getContent().equals("F"))
                {
                    buttons[x][y].setIcon(gui.getIconFlag());
                    buttons[x][y].setBackground(Color.blue);	                    
                }
                else if (cells[x][y].getContent().equals("0"))
                {
                    buttons[x][y].setBackground(Color.lightGray);
                }
                else
                {
                    buttons[x][y].setBackground(Color.lightGray);                    
                    buttons[x][y].setText(cells[x][y].getContent());
                    gui.setTextColor(buttons[x][y]);                                        
                }
            }
        }
    }
    //-----------------------------------------------------------------------------//
    //This function is called when clicked on closed button or exit
    @Override
    public void windowClosing(WindowEvent e) 
    {
        game.windowClosing(e);
    }
    
    //-----------------------------------------------------------------------//

    @Override
    public void actionPerformed(ActionEvent e) {        
        game.actionPerformed(e);   
    }
    
    
    //--------------------------------------------------------------------------//
        
    //Mouse Click Listener
    @Override
    public void mouseClicked(MouseEvent e)
    {
        game.mouseClicked(e);
    }

    //-------------------------RELATED TO SCORES----------------------//


    
    //---------------------EMPTY FUNCTIONS-------------------------------//
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }    

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
