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
import java.util.Observable;
import java.util.Observer;

public class Controller implements MouseListener, ActionListener, WindowListener, Observer {
    Observable observable;
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
        
        this.observable = game;
        observable.addObserver(this);
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
    
    public void update(Observable obs, Object arg) {
        if (obs instanceof Game) {
            if (arg == "Won") {
                gameWon();
            }
            else if (arg == "Lost") {
                gameLost();
            }
        }
    }
    
    public void gameWon()
    {
        gui.interruptTimer();
        
        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
        
        //------MESSAGE-----------//
        JLabel message = new JLabel("Congratulations, you won the game!", SwingConstants.CENTER);
                
        //-----STATISTICS-----------//
        JPanel statistics = new JPanel();
        statistics.setLayout(new GridLayout(6,1,0,10));
        
        ArrayList<Time> bTimes = game.getScore().getBestTimes();
        
        if (bTimes.isEmpty() || (bTimes.get(0).getTimeValue() > gui.getTimePassed()))
        {
            statistics.add(new JLabel("    You have the fastest time for this difficulty level!    "));
        }
        
        game.getScore().addTime(gui.getTimePassed(), new Date(System.currentTimeMillis()));
                
        JLabel time = new JLabel("  Time:  " + Integer.toString(gui.getTimePassed()) + " seconds            Date:  " + new Date(System.currentTimeMillis()));
        
        JLabel bestTime = new JLabel();
        
        
        if (bTimes.isEmpty())
        {
            bestTime.setText("  Best Time:  ---                  Date:  ---");
        }
        else
        {
            bestTime.setText("  Best Time:  " + bTimes.get(0).getTimeValue() + " seconds            Date:  " + bTimes.get(0).getDateValue());
        }
        
        JLabel gPlayed = new JLabel("  Games Played:  " + game.getScore().getGamesPlayed());
        JLabel gWon = new JLabel("  Games Won:  " + game.getScore().getGamesWon());
        JLabel gPercentage = new JLabel("  Win Percentage:  " + game.getScore().getWinPercentage() + "%");
        
        statistics.add(time);
        statistics.add(bestTime);
        statistics.add(gPlayed);
        statistics.add(gWon);
        statistics.add(gPercentage);
        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        statistics.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2,10,0));
        
        JButton exit = new JButton("Exit");
        JButton playAgain = new JButton("Play Again");

        
        exit.addActionListener((ActionEvent e) -> {
            dialog.dispose();
            windowClosing(null);
        });        
        playAgain.addActionListener((ActionEvent e) -> {
            dialog.dispose();            
            game.newGame();
        });        
        
        
        buttons.add(exit);
        buttons.add(playAgain);
        
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(message, BorderLayout.NORTH);
        c.add(statistics, BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);
        
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    dialog.dispose();
                    game.newGame();
            }
            }
        );

        dialog.setTitle("Game Won");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);                        
    }
    
    public void gameLost()
    {
        gui.interruptTimer();
        //----------------------------------------------------------------//

        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
        
        //------MESSAGE-----------//
        JLabel message = new JLabel("Sorry, you lost this game. Better luck next time!", SwingConstants.CENTER);
                
        //-----STATISTICS-----------//
        JPanel statistics = new JPanel();
        statistics.setLayout(new GridLayout(5,1,0,10));
        
        JLabel time = new JLabel("  Time:  " + Integer.toString(gui.getTimePassed()) + " seconds");
        
        JLabel bestTime = new JLabel();
        
        ArrayList<Time> bTimes = game.getScore().getBestTimes();
        
        if (bTimes.isEmpty())
        {
            bestTime.setText("                        ");
        }
        else
        {
            bestTime.setText("  Best Time:  " + bTimes.get(0).getTimeValue() + " seconds            Date:  " + bTimes.get(0).getDateValue());
        }
        
        JLabel gPlayed = new JLabel("  Games Played:  " + game.getScore().getGamesPlayed());
        JLabel gWon = new JLabel("  Games Won:  " + game.getScore().getGamesWon());
        JLabel gPercentage = new JLabel("  Win Percentage:  " + game.getScore().getWinPercentage() + "%");
        
        statistics.add(time);
        statistics.add(bestTime);
        statistics.add(gPlayed);
        statistics.add(gWon);
        statistics.add(gPercentage);
        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        statistics.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3,2,0));
        
        JButton exit = new JButton("Exit");
        JButton restart = new JButton("Restart");
        JButton playAgain = new JButton("Play Again");

        
        exit.addActionListener((ActionEvent e) -> {
            dialog.dispose();
            windowClosing(null);
        });        
        restart.addActionListener((ActionEvent e) -> {
            dialog.dispose();            
            game.restartGame();
        });        
        playAgain.addActionListener((ActionEvent e) -> {
            dialog.dispose();            
            game.newGame();
        });        
        
        
        buttons.add(exit);
        buttons.add(restart);
        buttons.add(playAgain);
        
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(message, BorderLayout.NORTH);
        c.add(statistics, BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);
        
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    dialog.dispose();
                    game.newGame();
            }
            }
        );
        
        dialog.setTitle("Game Lost");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);        
    }
    
    //-----------------------------------------------------------------------------//
    //This function is called when clicked on closed button or exit
    @Override
    public void windowClosing(WindowEvent e) 
    {
        if (game.getPlaying())
        {
            ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

            Object[] options = {"Save","Don't Save","Cancel"};

            int quit = JOptionPane.showOptionDialog(null, "What do you want to do with the game in progress?", 
                            "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, question, options, options[2]);

            switch(quit) 
            {
                //save
                case JOptionPane.YES_OPTION:
                    
                    gui.interruptTimer();
                    game.getScore().saveScoreIntoDB();
                    
                    JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.add(new JLabel("Saving.... Please Wait", SwingConstants.CENTER));
                    dialog.add(panel);
                    dialog.setTitle("Saving Game...");
                    dialog.pack();
                    dialog.setLocationRelativeTo(gui);                    
                    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
                       @Override
                       protected Void doInBackground() throws Exception 
                       {
                            game.getBoard().saveBoardState(gui.getTimePassed(), gui.getMines());                
                            return null;
                       }
                       
                       @Override
                       protected void done(){
                           dialog.dispose();                           
                       }                       
                    };
                            
                    worker.execute();
                    dialog.setVisible(true);
                                                            
                    System.exit(0);
                    break;
                
                //dont save                    
                case JOptionPane.NO_OPTION:
                    game.getScore().increaseGamesPlayed();
                    game.getScore().saveScoreIntoDB();
                    System.exit(0);
                    break;
                    
                case JOptionPane.CANCEL_OPTION: break;
            }
        }
        else
            System.exit(0);
    }
    
    //-----------------------------------------------------------------------//

    @Override
    public void actionPerformed(ActionEvent e) {        
        JMenuItem menuItem = (JMenuItem) e.getSource();

        if (menuItem.getName().equals("New Game"))
        {
            if (game.getPlaying())
            {
                ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

                Object[] options = {"Quit and Start a New Game","Restart","Keep Playing"};
                
                int startNew = JOptionPane.showOptionDialog(null, "What do you want to do with the game in progress?", 
                                "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, question, options, options[2]);

                switch(startNew) 
                {
                    case JOptionPane.YES_OPTION:      
                        
                        // Initialize the new game.
                        game.newGame();
                        game.getScore().increaseGamesPlayed();
                        game.getScore().saveScoreIntoDB();
                        break;

                    case JOptionPane.NO_OPTION: 
                        game.getScore().increaseGamesPlayed();   
                        game.getScore().saveScoreIntoDB();
                        game.restartGame();
                        break;
                    
                    case JOptionPane.CANCEL_OPTION: break;
                }
            }
        }
        
        else if (menuItem.getName().equals("Exit"))
        {
            windowClosing(null);
        }
        
        //Statistics
        else
        {
            game.showScore();
        }      
    }
    
    
    //--------------------------------------------------------------------------//
        
    //Mouse Click Listener
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // start timer on first click
        if(!game.getPlaying())
        {
            gui.startTimer();
            game.setPlaying(true);
        }
        
        if (game.getPlaying())
        {
            //Get the button's name
            JButton button = (JButton)e.getSource();

            // Get coordinates (button.getName().equals("x,y")).
            String[] co = button.getName().split(",");

            int x = Integer.parseInt(co[0]);
            int y = Integer.parseInt(co[1]);

            // Get cell information.
            boolean isMine = game.getBoard().getCells()[x][y].getMine();
            int neighbours = game.getBoard().getCells()[x][y].getSurroundingMines();

            // Left Click
            if (SwingUtilities.isLeftMouseButton(e)) 
            {
                if (!game.getBoard().getCells()[x][y].getContent().equals("F"))
                {
                    button.setIcon(null);

                    //Mine is clicked.
                    if(isMine) 
                    {  
                        //red mine
                        button.setIcon(gui.getIconRedMine());
                        button.setBackground(Color.red);
                        game.getBoard().getCells()[x][y].setContent("M");

                        game.gameLost();
                    }
                    else 
                    {
                        // The player has clicked on a number.
                        game.getBoard().getCells()[x][y].setContent(Integer.toString(neighbours));
                        button.setText(Integer.toString(neighbours));
                        gui.setTextColor(button);

                        if( neighbours == 0 ) 
                        {
                            // Show all surrounding cells.
                            button.setBackground(Color.lightGray);
                            button.setText("");
                            game.findSurroundingZeroes(x, y);
                        } 
                        else 
                        {
                            button.setBackground(Color.lightGray);
                        }
                    }
                }
            }
            // Right Click
            else if (SwingUtilities.isRightMouseButton(e)) 
            {
                if(game.getBoard().getCells()[x][y].getContent().equals("F")) 
                {   
                    game.getBoard().getCells()[x][y].setContent("");
                    button.setText("");
                    button.setBackground(new Color(0,110,140));

                    //simple blue

                    button.setIcon(gui.getIconTile());
                    gui.incMines();
                }
                else if (game.getBoard().getCells()[x][y].getContent().equals("")) 
                {
                    game.getBoard().getCells()[x][y].setContent("F");
                    button.setBackground(Color.blue);	

                    button.setIcon(gui.getIconFlag());
                    gui.decMines();
                }
            }

            game.checkGame();
        }
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
