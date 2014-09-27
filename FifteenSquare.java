/* J Andrew McCormick
 * EECS 1510
 * Dr. Larry Thomas
 * Assignment 2 - 15-Square
 * 
 * This program implements the classic puzzle game of fifteen square.  
 * There is a 4x4 board with fifteen squares numbered from 1 to 15, and
 * 1 blank space.  The idea behind the game is to move each square so that 
 * the numbers 1 to 15 are in order from top left to bottom right.  
 * 
 * The movement of the pieces are handled in the moveSquare() method.
 * Our technique will involve locating the invisible square, storing
 * the locations of the zero square's row and column, then storing the 
 * locations of the row and column of the clicked square.  Our move method will
 * use this information to move the squares appropriately.  Starting a new
 * game simply consists of shuffling the board and setting the move number 
 * to 1.  The shuffle action will consist of finding the invisible square and
 * selecting a random square in either the same row or column (alternating) to
 * generate legitimate moves to shuffle the board.
 * 
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.border.*;
public class FifteenSquare extends JFrame
{	
	//this is to get rid of the yellow triangle that was next to the class
	final static long serialVersionUID = 123123123123123L;
	
	//these variables are loaded and saved every time the program is executed
	//and updated when a game is won and when opened. 
	private int gamesPlayed = 0;	
	private int gamesWon = 0;
	private int fewestMovesForWin = 0;

	//this constant controls the size of the board.  In the future we will
	//change it to a variable and have the user select how big of a play
	//area they wish.
	private final int PLAY_AREA_SIZE = 4;
	
	//this variable chooses the square to be invisible.  It will be the last square
	//in the board.
	private String invisibleNumber = Integer.toString(PLAY_AREA_SIZE * PLAY_AREA_SIZE);
	
	//create an array of reference variables of button objects for both
	//the game board and option buttons.
	private JButton[][] square;
	private JButton[] optionButtons;
	
	//these are the items contained in the option panel.
	private JLabel numberOfMoves;	
	private JButton hint, save, load, exit, help;
	
	//these variables are used in the move method for the purpose of displaying
	//the results of the user click.  The location of the button clicked by the user
	//is also stored in these variables.
	private int clickedRow = 0;
	private int clickedColumn = 0;
	
	//these variables hold the value of the row and column of the invisible square.
	private int invisibleRow = PLAY_AREA_SIZE - 1;
	private int invisibleColumn = PLAY_AREA_SIZE - 1;

	//keeps track of the move number.
	private int moveNumber = 1;

	//this variable creates the image icons that control the various buttons on the frame.
	private ImageIcon buttonIcon, optionIcon, optionIconMouseOver, optionIconMouseClick;

	//this creates the file objects that the data and save games will be loaded into
	private File saveFile = new File("saves/saveFile.dat");
	private File data = new File("saves/data.dat");
	
	//this constructor will create the board for the game, adds the panels with the 
	//game board and option buttons and displays them.
	public FifteenSquare() 
	{
		//These variables control the size of the various areas of the
		//play area.
		final int HEIGHT_OF_SQUARE = 121;
		final int WIDTH_OF_SQUARE = 121;
		final int BUTTON_GAP = 1;
		final int EDGE_GAP = 5;
		final int FRAME_WIDTH = (PLAY_AREA_SIZE * WIDTH_OF_SQUARE) + ((PLAY_AREA_SIZE + 1) * BUTTON_GAP) + 28;
		final int FRAME_HEIGHT = (PLAY_AREA_SIZE * WIDTH_OF_SQUARE) + ((PLAY_AREA_SIZE + 1) * BUTTON_GAP) + 57;
		final int BOARD_PANEL_WIDTH_HEIGHT = (PLAY_AREA_SIZE * WIDTH_OF_SQUARE) + ((PLAY_AREA_SIZE + 1) * BUTTON_GAP);
		final int OPTION_BUTTONS_WIDTH = 160;
		final int OPTION_BUTTONS_HEIGHT = 60;
		final int OPTIONS_PANEL_HEIGHT = (2 * OPTION_BUTTONS_HEIGHT) + (3 * BUTTON_GAP);

		//this will call the method to load all the running data shown in the help file
		loadData();
		gamesPlayed++;
		saveData();
		
		//this creates the font objects that controls the font information within
		//the program
		Font squareFont = new Font("SansSerif", Font.BOLD, 70);
		Font optionFont = new Font("SansSerif", Font.BOLD, 36);
		Font moveNumberFont = new Font("SansSerif", Font.BOLD, 20);
		
		//this sets the title on the frame
		setTitle("Fifteen Square by J Andrew McCormick");
		
		//these are the panels that contain all the buttons
		JPanel boardPanel = new JPanel();
		JPanel optionsPanel = new JPanel();
			
		//this sets the background color of the frame, board panel and option panel to white
		boardPanel.setBackground(Color.WHITE);
		optionsPanel.setBackground(Color.WHITE);
		getContentPane().setBackground(Color.WHITE);
		
		//we will specify all of the locations of the buttons in the 
		//play area, therefore we will set all of the auto layouts
		//to null so it doesn't interfere.
		setLayout(null);
		boardPanel.setLayout(null);
		optionsPanel.setLayout(null);

		//default close operation
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		//these arrays creates all of the references to the buttons.  The
		//objects these buttons will refer to will be created later.
		square = new JButton[PLAY_AREA_SIZE][PLAY_AREA_SIZE];
		optionButtons = new JButton[5];
		
		//this sets the custom icons for the various buttons and button functions
		buttonIcon = new ImageIcon("image/squareButton.png");
		optionIcon = new ImageIcon("image/optionButton.png");
		optionIconMouseOver = new ImageIcon("image/optionButtonMouseOver.png");
		optionIconMouseClick = new ImageIcon("image/optionButtonMouseClick.png");
		
		//This sets the size of the frame and the panels.
		setBounds(1,1,FRAME_WIDTH,FRAME_HEIGHT + OPTIONS_PANEL_HEIGHT + 10);
		boardPanel.setBounds(EDGE_GAP,EDGE_GAP, BOARD_PANEL_WIDTH_HEIGHT, BOARD_PANEL_WIDTH_HEIGHT);
		optionsPanel.setBounds(EDGE_GAP,3 * EDGE_GAP + BOARD_PANEL_WIDTH_HEIGHT,BOARD_PANEL_WIDTH_HEIGHT,OPTIONS_PANEL_HEIGHT);
		
		//this centers the frame on the users screen
		setLocationRelativeTo(null);
		
		//This loop creates each button, gives each button a number, adds
		//it to the panel that contains the board, size and places the buttons
		//in the proper place, sets the font, and adds the action listener to the button.  
		for(int row = 0; row < PLAY_AREA_SIZE; row++)
		{
			for(int column = 0; column < PLAY_AREA_SIZE; column++)
			{	
				square[row][column] = new JButton(PLAY_AREA_SIZE * row + column + 1 + "", buttonIcon);
				boardPanel.add(square[row][column]);
				
				square[row][column].setBounds(((column + 1) * BUTTON_GAP) + WIDTH_OF_SQUARE * column, ((row + 1) * BUTTON_GAP) + HEIGHT_OF_SQUARE * row,WIDTH_OF_SQUARE,HEIGHT_OF_SQUARE);
				square[row][column].addActionListener(new SquareListener());
				square[row][column].setBorder(null);
				square[row][column].setHorizontalTextPosition(JButton.CENTER);
				square[row][column].setVerticalTextPosition(JButton.CENTER);
				square[row][column].setFont(squareFont);
				square[row][column].setFocusable(false);
				square[row][column].setForeground(Color.WHITE);
			}	
		}
		
		//This loop will place and size all of the option buttons.  We calculate
		//the row and the column to place the buttons.
		for(int i = 0; i < 5; i++)
		{
			int row = i / (3);
			int column = i % (3) ;
			
			optionButtons[i] = new JButton(optionIcon);
			optionsPanel.add(optionButtons[i]);
			
			optionButtons[i].setBounds(((column + 1) * BUTTON_GAP) + OPTION_BUTTONS_WIDTH * column, ((row + 1) * BUTTON_GAP) + OPTION_BUTTONS_HEIGHT * row, OPTION_BUTTONS_WIDTH, OPTION_BUTTONS_HEIGHT);		
			optionButtons[i].setVerticalTextPosition(JButton.CENTER);
			optionButtons[i].setHorizontalTextPosition(JButton.CENTER);
			
			optionButtons[i].setForeground(Color.WHITE);
			optionButtons[i].setFocusable(false);
			optionButtons[i].setFont(optionFont);
			optionButtons[i].setBorder(null);
			optionButtons[i].addActionListener(new OptionListener());
			optionButtons[i].addMouseListener(new MouseOverListener());
		}
			
		//this sets the last square in the board to invisible 
		for(int i = 0; i < PLAY_AREA_SIZE; i++)
		{
			for(int j = 0; j < PLAY_AREA_SIZE; j++)
			{
				if(square[i][j].getText().equals(invisibleNumber))
					square[i][j].setVisible(false);
			}
		}

		//this created the option buttons at the bottom of the frame and sets their text
		save = optionButtons[0];
		save.setText("Save");
		help = optionButtons[1];
		help.setText("Help");
		load = optionButtons[2];
		load.setText("Load");
		exit = optionButtons[3];
		exit.setText("Exit");
		hint = optionButtons[4];
		hint.setText("Hint");

		//this effectively disables the hint button, note:  in the inner class we are also
		//not responding to a mouse hover event by setting the range to 4 instead of 5
		hint.setIcon(optionIconMouseOver);
		
		//this will label and display the move counter and set its location
		numberOfMoves = new JLabel("Move: " + moveNumber);
		optionsPanel.add(numberOfMoves);
		numberOfMoves.setBounds(((3 * BUTTON_GAP) + (2 * OPTION_BUTTONS_WIDTH) + 50), ((2 * BUTTON_GAP) + OPTION_BUTTONS_HEIGHT) + 5, 100, 50);
		numberOfMoves.setFont(moveNumberFont);
		
		//don't let the user resize the board
		this.setResizable(false);
		
		//these add the board and the options to the frame
		add(boardPanel);
		add(optionsPanel);

		//this shuffles the squares on the board
		shuffle();
		
		//display the frame
		setVisible(true);
		
		//adds the key listener to the frame so the game can be played with the arrow keys
		addKeyListener(new PlayWithKeyboardListener());
	}
	
	//this inner class is used to handle the user playing with the arrow keys.
	//the way the arrow keys work is that the arrows move a square above,
	//below, left or right of the invisible square into the invisible squares
	//current location.  Example:  If you want to move the square to the left of the 
	//invisible square right, then you press right and the invisible square moves left.
	class PlayWithKeyboardListener implements KeyListener
	{
		public void keyPressed(KeyEvent e)
		{
			findInvisibleSquare();
			
			//if the user pressed the up key, this will emulate the user clicking on the square below
			//the invisible square.  
			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				if(invisibleRow < PLAY_AREA_SIZE - 1)
				{
					clickedColumn = invisibleColumn;
					clickedRow = invisibleRow + 1;
					moveSquare();
					moveNumber++;
					numberOfMoves.setText("Move: " + moveNumber);
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				if(invisibleRow > 0)
				{
					clickedColumn = invisibleColumn;
					clickedRow = invisibleRow - 1;
					moveSquare();
					moveNumber++;
					numberOfMoves.setText("Move: " + moveNumber);
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_LEFT)
			{
				if(invisibleColumn < PLAY_AREA_SIZE - 1)
				{
					clickedRow = invisibleRow;
					clickedColumn = invisibleColumn + 1;
					moveSquare();
					moveNumber++;
					numberOfMoves.setText("Move: " + moveNumber);
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				if(invisibleColumn > 0)
				{
					clickedRow = invisibleRow;
					clickedColumn = invisibleColumn - 1;
					moveSquare();
					moveNumber++;
					numberOfMoves.setText("Move: " + moveNumber);
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_F12)
			{
				if(JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the statistics listed in the help file?", "Reset?", JOptionPane.YES_NO_OPTION) == 0)
				{
					gamesPlayed = 1;
					gamesWon = 0;
					fewestMovesForWin = 999999;
					saveData();
				}
				JOptionPane.showMessageDialog(null, "Reset!");
			}
			
			//after each move, we will check and see if the user won.
			if(isVictorious())
			{

				//if we make it to here, the user has won, if the user clicks yes
				//in the dialog box, then we will shuffle the board and set the move counter to 1.
				//otherwise, we will exit the program
				
				//set these values to be saved
				gamesWon++;
				fewestMovesForWin = moveNumber;
				
				//save the running data in the help file
				saveData();
				
				//here we will ask the user if they would like to play again.  If they do, we will update the program
				//with the total number of games won and we will shuffle the board and set the move number back to one
				if(JOptionPane.showConfirmDialog(null, "Congrats! You have won!  Would you like to play again?", "WINNER!", JOptionPane.YES_NO_OPTION) == 0)
				{
					shuffle();
					gamesPlayed++;
					if(moveNumber < fewestMovesForWin)
						fewestMovesForWin = moveNumber;
					saveData();
					
					moveNumber = 1;
					numberOfMoves.setText("Move: " + moveNumber);
				}
				else
					System.exit(0);	
			}
		}
		
		//unused methods
		public void keyTyped(KeyEvent e){}
		public void keyReleased(KeyEvent e){}
	}
	
	//this class handles the mouse over feature for the option buttons
	//we use i < 4 because we are disabling our hint button
	class MouseOverListener implements MouseListener
	{
		public void mouseEntered(MouseEvent e)
		{
			for(int i = 0; i < PLAY_AREA_SIZE; i++)
				if(e.getSource() == optionButtons[i])
					optionButtons[i].setIcon(optionIconMouseOver);
		}
		public void mouseExited(MouseEvent e)
		{
			for(int i = 0; i < PLAY_AREA_SIZE; i++)
				if(e.getSource() == optionButtons[i])
					optionButtons[i].setIcon(optionIcon);
		}
		public void mouseReleased(MouseEvent e)
		{
			for(int i = 0; i < PLAY_AREA_SIZE; i++)
				if(e.getSource() == optionButtons[i])
					optionButtons[i].setIcon(optionIconMouseOver);
		
		}
		public void mousePressed(MouseEvent e)
		{
			for(int i = 0; i < PLAY_AREA_SIZE; i++)
				if(e.getSource() == optionButtons[i])
					optionButtons[i].setIcon(optionIconMouseClick);
		}
		public void mouseClicked(MouseEvent e){}
	}
	
	//this inner class handles the actions from clicking on the buttons.
	//the save and the load buttons use data streams to save the data to a file
	class OptionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == exit)
			{	
				if(JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit?", JOptionPane.YES_NO_OPTION) == 0)	
					if(JOptionPane.showConfirmDialog(null, "Would you like to save your game?", "Save?", JOptionPane.YES_NO_OPTION) == 0)
					{
						saveGame();
						System.exit(0);
					}	
					else
						System.exit(0);
			}
			else if(e.getSource() == save)
			{
				saveGame();
			}
			else if(e.getSource() == load)
			{
				loadGame();		
			}
			else if(e.getSource() == help)
				JOptionPane.showMessageDialog(null,   "Hello, and welcome to this edition of the classic game 15-Square!\n\n"
													+ "To play this game you click on any square that is in the same row\n"
													+ "or column as the blank space and the numbered squares will move\n"
													+ "in the direction of the blank space.  You can also play using the\n"
													+ "arrow keys.  Press the arrow key that corresponds with the direction\n"
													+ "you wish to move the numbered square relative to the blank space.\n\n"
													+ "Statistics\n"
													+ "Number of Games Played: " + gamesPlayed + "\n"
													+ "Number of Games Won: " + gamesWon + "\n"
													+ "Fewest number of moves used in a win: " + fewestMovesForWin
													+ "\n\n"
													+ "Note:  If you press the 'x' key in the upper-right hand corner of the main screen,\n"
													+ "nothing happens.  Use the exit button provided.  Also, to reset the above statistics\n"
													+ "press F12 from the main screen\n");
		}
	}
	
	//this is the inner class that implements the action listener for the 
	//game board.  When a square is clicked it throws a ActionEvent.  This 
	//class finds the square the threw the ActionEvent, sets the row and column
	//of the clicked square to the variables, and moves the appropriate square using
	//the moveSquare() method.
	class SquareListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for(int i = 0; i < PLAY_AREA_SIZE; i++)
			{
				for(int j = 0; j < PLAY_AREA_SIZE; j++)
				{
					if(e.getSource() == square[i][j])
					{
						//set the class variables with the location of the clicked on square
						clickedRow = i;
						clickedColumn = j;
						
						//if the user makes a move that is legitimate, increment the move number
						if(clickedRow == invisibleRow || clickedColumn == invisibleColumn) moveNumber++;
						
						//every time squares are moves, reprint the text on the move counter
						numberOfMoves.setText("Move: " + moveNumber);
						
						//move the squares, note this will only do something if the user made a 
						//legitimate move, nothing will happen if the user made an invalid move
						moveSquare();
						
						//check to see if the user won
						if(isVictorious())
						{
							//if we make it to here, the user has won, if the user clicks yes
							//in the dialog box, then we will shuffle the board and set the move counter to 1.
							//otherwise, we will exit the program
							gamesWon++;
							if(moveNumber < fewestMovesForWin)
								fewestMovesForWin = moveNumber;
							saveData();
							
							if(JOptionPane.showConfirmDialog(null, "Congrats! You have won!  Would you like to play again?", "WINNER!", JOptionPane.YES_NO_OPTION) == 0)
							{
								gamesPlayed++;
								saveData();
								
								moveNumber = 1;
								numberOfMoves.setText("Move: " + moveNumber);
								
							}
							else
								System.exit(0);	
						}
					}
				}
			}
		}
	}
	
	//this method will start in the upper right hand corner of the game board
	//and will search for the square that is invisible,  it stores the location
	//of this square in class variables
	private void findInvisibleSquare()
	{
		for(invisibleRow = 0; invisibleRow < PLAY_AREA_SIZE; invisibleRow++)
		{
			for(invisibleColumn = 0; invisibleColumn < PLAY_AREA_SIZE; invisibleColumn++)
			{
				if(square[invisibleRow][invisibleColumn].getText().equals(invisibleNumber))
					return;
			}
		}
	}
	
	//this method moves the squares or row of squares as necessary based on where the user clicked.
	private void moveSquare()
	{
		int i = 0;
		
		//step 1:  Find the invisible square.
		findInvisibleSquare();
		
		//step 2:  Set the invisible square to visible
		square[invisibleRow][invisibleColumn].setVisible(true);
		
		//based on where the user clicked, the square either need to move left, right, up, or down
		//these blocks handle all the squares movement.
		
		//left
		if(clickedRow == invisibleRow && clickedColumn > invisibleColumn)
		{	
			for(i = invisibleColumn + 1; i <= clickedColumn; i++)
			{
				square[clickedRow][i - 1].setText(square[clickedRow][i].getText());
				square[clickedRow][i].setText(invisibleNumber);
			}
		}
		//right
		else if(clickedRow == invisibleRow && clickedColumn < invisibleColumn)
		{
			for(i = invisibleColumn - 1; i >= clickedColumn; i--)
			{
				square[clickedRow][i + 1].setText(square[clickedRow][i].getText());
				square[clickedRow][i].setText(invisibleNumber);
			}
		}
		//up
		else if(clickedColumn == invisibleColumn && clickedRow < invisibleRow)
		{
			for(i = invisibleRow - 1; i >= clickedRow; i--)
			{
				square[i + 1][clickedColumn].setText(square[i][clickedColumn].getText());
				square[i][clickedColumn].setText(invisibleNumber);
			}
		}
		//down
		else if(clickedColumn == invisibleColumn && clickedRow > invisibleRow)
		{
			for(i = invisibleRow + 1; i <= clickedRow; i++)
			{
				square[i - 1][clickedColumn].setText(square[i][clickedColumn].getText());
				square[i][clickedColumn].setText(invisibleNumber);
			}
		}
		//find and set the invisible square to invisible
		findInvisibleSquare();
		square[invisibleRow][invisibleColumn].setVisible(false);
	}
	
	//this method will shuffle the board.  It will shuffle it a lot of times.  if the count i 
	// is an even number, it will generate a random column in the same row the invisible square 
	//is located to be "Clicked."  likewise, if the count i is odd it will generate a random
	//row to be "clicked."  After setting the clicked row and column, it will move the pieces.
	private void shuffle()
	{	
		for(int i = 1; i < PLAY_AREA_SIZE * 250; i++) 
		{
			if(i % 2 == 0)
			{
				clickedRow = invisibleRow;
				clickedColumn = invisibleColumn;
				
				//this loop guarantee's that every random move is legitimate
				while(clickedColumn == invisibleColumn)
				{	
					clickedColumn = (int) (Math.random() * PLAY_AREA_SIZE);
				}
			}
			else
			{
				clickedColumn = invisibleColumn;
				clickedRow = invisibleRow;
				
				while(clickedRow == invisibleRow)
				{
					clickedRow = (int) (Math.random() * PLAY_AREA_SIZE);
				}
			}
			moveSquare();
		}
		
		//here we make sure the blank square is in the bottom-right hand corner
		clickedColumn = PLAY_AREA_SIZE - 1;
		moveSquare();
		clickedRow = PLAY_AREA_SIZE - 1;
		moveSquare();
	}
	
	//this method will calculate whether or not the user has won the game.
	//it has an early exit if statement that returns void if the user has not
	//yet reached the victory condition.  
	private boolean isVictorious()
	{
		for(int i = 0; i < PLAY_AREA_SIZE; i++)
		{
			for(int j = 0; j < PLAY_AREA_SIZE; j++)
			{
				//early termination statement
				if(!square[i][j].getText().equals(PLAY_AREA_SIZE * i + j + 1 + ""))
					return false;
			}
		}
		return true;
	}

	//this saves the data that is shown in the help file.
	private void saveData()
	{
		try
		{
			DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(data, false));
			
			dataOut.writeInt(gamesPlayed);
			dataOut.writeInt(gamesWon);
			dataOut.writeInt(fewestMovesForWin);
			dataOut.close();
		}catch(IOException ioe){}
	}
	
	//this holds the data that is loaded every time the program opens.  if
	//there is no file, it creates the file with the most basic of data.
	private void loadData()
	{
		try
		{
			if(data.exists())
			{
				DataInputStream dataIn = new DataInputStream(new FileInputStream(data));
				
				gamesPlayed = dataIn.readInt();
				gamesWon = dataIn.readInt();
				fewestMovesForWin = dataIn.readInt();
				dataIn.close();
			}
			else
			{
				gamesPlayed = 1;
				gamesWon = 0;
				fewestMovesForWin = 999999;
				saveData();
			}
		}catch(IOException ioe){}
	}
	
	//this method is called when the save button is pressed
	private void saveGame()
	{
		if(saveFile.exists())
		{
			if(JOptionPane.showConfirmDialog(null, "Do you want to overwrite your previous save game?", "Overwright?", JOptionPane.YES_NO_OPTION) == 0)
			{
				try
				{	
					DataOutputStream saveOut = new DataOutputStream(new FileOutputStream(saveFile, false));
					
					for(int i = 0; i < PLAY_AREA_SIZE; i++)
					{
						for(int j = 0; j < PLAY_AREA_SIZE; j++)
						{
							saveOut.writeUTF(square[i][j].getText());
						}
					}
					saveOut.writeInt(moveNumber);
					saveOut.close();
					JOptionPane.showMessageDialog(null, "Save complete!");
				}catch(IOException ioe){}
			}
		}
		else
		{
			try
			{	
				DataOutputStream saveOut = new DataOutputStream(new FileOutputStream(saveFile, false));
				
				for(int i = 0; i < PLAY_AREA_SIZE; i++)
				{
					for(int j = 0; j < PLAY_AREA_SIZE; j++)
					{
						saveOut.writeUTF(square[i][j].getText());
					}
				}
				saveOut.writeInt(moveNumber);
				saveOut.close();
				JOptionPane.showMessageDialog(null, "Save complete!");
			}catch(IOException ioe){}
		}
	}
	
	//when you click on the load button, this method will be called and will save the text
	//from each of the buttons and the integer value for the move number to a file.
	private void loadGame()
	{
		try
		{
			if(saveFile.exists())
			{
				DataInputStream saveIn = new DataInputStream(new FileInputStream(saveFile));
				
				findInvisibleSquare();
				square[invisibleRow][invisibleColumn].setVisible(true);
				
				for(int i = 0; i < PLAY_AREA_SIZE; i++)
				{
					for(int j = 0; j < PLAY_AREA_SIZE; j++)
					{						
						square[i][j].setText(saveIn.readUTF());
					}
				}
				findInvisibleSquare();
				square[invisibleRow][invisibleColumn].setVisible(false);
				moveNumber = saveIn.readInt();
				saveIn.close();
				numberOfMoves.setText("Move: " + moveNumber);
				JOptionPane.showMessageDialog(null, "Load complete!");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "No save game exists!");
			}
		}catch(IOException ioe){}
	}
}
