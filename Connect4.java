//J Andrew McCormick
//EECS 1510
//Dr. Thomas
//2/27/2014
//Project 1:  Connect4
//This program implements the classic game of connect 4.  Typically
//two players play on a 6x7 board.  Player 1 will 'place' pieces of
//letter A and player 2 of letter B.  The first person to connect 4
//like lettered pieces in a row wins.

import java.util.Scanner;

public class Connect4
{
	//The main method of this program handles gathering all of the 
	//user input and method calls.
	public static void main(String[] args)
	{		
		//these are boolean control variables to control various loops in the game
		boolean continuePlaying = true;
		boolean isInputValid = false;
		boolean isWinner = false;
		
		//these constants are the constants that control basic aspects of the game 
		//Note, if there are more than 999 columns, the footer printed
		//at the bottom of the game board will not line up perfectly
		final int ROWS = 20;
		final int COLUMNS = 20;
		final int CONNECT_HOW_MANY = 4;
		final int NUMBER_OF_PLAYERS = 7;
		final int STARTING_CHAR = 64;
		
		//the char that corresponds to the players playing piece
		char playerChar;
		
		//these variables keep track of what turn it is and who the current player is
		int turnCount = 1;
		int player = 0;
		
		//this will decide when it is necessary to start checking for the victory condition
		int startCheckingVictoryCondition = (CONNECT_HOW_MANY * NUMBER_OF_PLAYERS) - 1;
		
		//these are the variables allocated for storing the row and column of
		//the last piece placed
		int columnNumber = 0;
		int rowNumber = 0;
		
		//this declares and initializes the game board
		char[][] gameBoard = new char[ROWS][COLUMNS];
		
		//scanner to get users column selection
		Scanner input= new Scanner(System.in); 
		
		//game header
		System.out.println("Welcome to Connect " + CONNECT_HOW_MANY + " for " + NUMBER_OF_PLAYERS + " players!");
		
		//loop that controls input and method calls
		do
		{
			//these variables get reset every time a new game starts
			continuePlaying = true;
			isInputValid = false;
			isWinner = false;
			
			//decide which player is current player
			player = ((turnCount + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS) + 1;
			
			//set the player's playing piece
			playerChar = (char) (player + STARTING_CHAR);
			
			//start the game by printing the empty game board
			printGameBoard(gameBoard);
			
			//this loop handles all the user input received by the program and
			//calls the appropriate methods in their necessary order.  Our valid inputs 
			//are from 1 to the width of the board (7 for default).  There is also a sentinel
			//value of -1 added so the user can terminate the program early if they so desire.
			//Having this sentinel value causes some of the calculations to be a little bit 
			//confusing.  We assign the value the user enters directly to the columnNumber 
			//variable.  In order for this value to correspond with our gameBoard array, 
			//it needs to have values from 0 to 6 (instead of 1 to 7).  To remedy this, we
			//subtract 1 from the input value.  Because of this, the sentinel value internally
			//has a value of -2.  
			do
			{	
				//we need to set the variable every time the loop executes
				isInputValid = false;
		
				//ask the player for the column number they wish for
				System.out.println("Player " + player + ", please select a column from 1 to " + COLUMNS);
				System.out.print("followed by the enter key (-1 to end game early): ");
				
				//This loop makes sure the user enters an integer as their input
				do
				{
					//set columnNumber to a dummy number -1
					columnNumber = -1;

					//if the next value the user inputs is an integer
					if(input.hasNextInt()) 
					{
						//if the user inputs 0, which is invalid, it will cause an infinite loop
						//to prevent this we will change it to -3 to remove any opportunity of 
						//infinite loop
						if((columnNumber = (input.nextInt() - 1)) == -1)
							columnNumber = -3;
					}//end if
					else
					{	
						input.next();
						System.out.println("You entered an invalid value!");
						System.out.println("Player " + player + ", please select a column from 1 to " + COLUMNS);
						System.out.print("followed by the enter key (-1 to end game early): ");
					}//end else
				}while(columnNumber == -1);
				
				//this closes the program if the user chooses to exit early.  Note the user enters -1 but
				//because of the input calculation that is done, it becomes a -2.
				if(columnNumber == -2) System.exit(0);
			
				//this statement makes sure the user chose a column within the valid range
				if(columnNumber < 0 || columnNumber > COLUMNS - 1) 
				{
					System.out.println("You selected a column that does not exist!");
					continue;
				}//end if
				
				//this sends the user input to be placed in the board and returns the row
				//number that the piece ended up in, it returns a -1 if the user 
				//selects a full column
				rowNumber = placePiece(playerChar, columnNumber, gameBoard);
				
				//if the user placed a piece in a full column, it returned -1 
				if(rowNumber == -1) 
				{
					System.out.println("You selected a column that is full!");
					continue;
				}//end if
				//if the user entered a valid column then we no longer need to execute this loop
				else isInputValid = true; 
			
			}while(!isInputValid);//do until the input is valid
			
			//start checking the victory condition at the first chance somebody could win
			if(turnCount >= startCheckingVictoryCondition) 
				isWinner = isVictoryConditionMet(CONNECT_HOW_MANY, ROWS, COLUMNS, rowNumber, columnNumber, playerChar, gameBoard);
			
			//check for a winner
			if(isWinner) 
			{
				//print the game board with the final piece placed
				printGameBoard(gameBoard);
				
				//congratulate player and ask if they want to play again
				System.out.println("Congratulations player " + player + ", you have won!");
				
				//if the user wants to play again, restart loop, restart turnCount
				//and reset array to zero's
				System.out.println("Would you like to play again? (1 followed by enter for yes,");
				System.out.print("any other key followed by enter to quit): ");

				if(input.next().equals("1")) 
				{
					//keep the loop going
					continuePlaying = true;	
					
					//when the loop starts over again, because of the soon to come
					//increment, turnCount will reset to 1;
					turnCount = 0;
					
					//this loop will reset all values in gameBoard to zero
					for(int i = 0; i < gameBoard.length; i++)
					{
						for(int j = 0; j < gameBoard[i].length; j++)
						{
							gameBoard[i][j] = 0;
						}//end for
					}//end for
				}//end if
				else continuePlaying = false;
			}//end if
			
			//Tie checker
			if(turnCount == ROWS * COLUMNS)
			{
				printGameBoard(gameBoard);
				
				System.out.println("The game is a tie game.");
				System.out.print("Would you like to play again? (1 followed by enter for yes,");
				System.out.print("any other key followed by enter to quit): ");
				
				//if the user wants to play again, restart loop, restart turnCount
				//and reset array to zero's
				if(input.next().equals("1")) 
				{
					//keep the loop going
					continuePlaying = true;	
					
					//when the loop starts over again, because of the soon to come
					//increment, turnCount will reset to 1;
					turnCount = 0;
					
					//this loop will reset all values in gameBoard to zero
					for(int i = 0; i < gameBoard.length; i++)
					{
						for(int j = 0; j < gameBoard[i].length; j++)
						{
							gameBoard[i][j] = 0;
						}//end for
					}//end for
				}//end if
				else continuePlaying = false;
			}//end if
			//increment turn count
			turnCount++;
			
		}while(continuePlaying);//keep playing as long as the player wants to
		
		input.close();
	}//end main

	//This method places the appropriate players piece in the column they selected
	// and returns the row number.  If they placed a piece in a full column it will return a -1.
	public static int placePiece(char playerChar, int columnNum, char[][] gameBoard)
	{
		//this variable stores the current row that is being checked.
		int row;
		
		//first we will start with the bottom row
		for(row = (gameBoard.length - 1); row >=  0; row--)
		{
			//place piece in first empty row
			if(gameBoard[row][columnNum] == 0)
			{
				gameBoard[row][columnNum] = playerChar;
				return row;
			}//end if
		}//end for
		
		//if column was full
		return -1;
	}//end placePiece
	
	//This method checks to see if the player that dropped the most recent piece wins
	public static boolean isVictoryConditionMet(final int CONNECT_HOW_MANY, final int ROWS, final int COLUMNS, int rowNum, int columnNum, char playerChar, char[][] gameBoard)
	{
		//this count will keep track of how many pieces are in-a-row and will start at 1
		//because the location of the last dropped piece is included and is the 
		//starting location.
		int count = 1;
	  
		//these boolean variables control whether a particular if statement needs to continue or not
		boolean continueLeftOrUp = true;		
		boolean continueRightOrDown = true;
			
		//this for loop checks for a vertical victory.  Note that if both of the 
		//boolean control variables are set to false then the for loop
		//will execute empty.
		for(int i = 1; i <= CONNECT_HOW_MANY - 1; i++)
		{
			//make sure that in checking the columns, that we
			//don't cause an array out of bounds error
			if(columnNum - i < 0) continueLeftOrUp = false;

			//this if statement checks to the left of the last dropped piece
			//note that the if statement will not execute if it will cause an 
			//out of bounds error and will not continue to execute if the last
			//checked location was not the same color as the last dropped piece.
			if(continueLeftOrUp)
			{
				if(gameBoard[rowNum][columnNum - i] == playerChar) count++;
				else continueLeftOrUp = false;
			}//end if
			
			//out of bounds check
			if(columnNum + i > COLUMNS - 1) continueRightOrDown = false;
			
			//check right
			if(continueRightOrDown)
			{
				if(gameBoard[rowNum][columnNum + i] == playerChar) count++;
				else continueRightOrDown = false;
			}//end if 
		}//end for
		
		//if there is a victory, return true, if not, reset boolean variables and count and continue  
		if(count >= CONNECT_HOW_MANY) return true;
		else 
		{
			count = 1;
			continueLeftOrUp = true;
			continueRightOrDown = true;
		}
		
		//vertical check, we never need to check above the last piece placed because under no
		//circumstances will there be pieces above the last piece place
		for(int i = 1; i <= CONNECT_HOW_MANY - 1; i++)
		{
			//out of bounds check
			if(rowNum + i > ROWS - 1) continueRightOrDown = false;
			
			//check down
			if(continueRightOrDown)
			{
				if(gameBoard[rowNum + i][columnNum] == playerChar) count++;
				else continueRightOrDown = false;
			}
		}
		
		//victory check
		if(count >= CONNECT_HOW_MANY) return true;
		else 
		{
			count = 1;
			continueLeftOrUp = true;
			continueRightOrDown = true;
		}
		
		//diagonal backslash
		for(int i = 1; i <= CONNECT_HOW_MANY - 1; i++)
		{
			//out of bounds check
			if(rowNum - i < 0 || columnNum - i < 0) continueLeftOrUp = false;
			
			//up and left
			if(continueLeftOrUp)
			{
				if(gameBoard[rowNum - i][columnNum - i] == playerChar) count++;
				else continueLeftOrUp = false;
			}
			
			//out of bounds check
			if(rowNum + i > ROWS - 1 || columnNum + i > COLUMNS - 1) continueRightOrDown = false;
			
			//down and right
			if(continueRightOrDown)
			{
				if(gameBoard[rowNum + i][columnNum + i] == playerChar) count++;
				else continueRightOrDown = false;
			}
		}
		
		//victory check
		if(count >= CONNECT_HOW_MANY) return true;
		else 
		{
			count = 1;
			continueLeftOrUp = true;
			continueRightOrDown = true;
		}
		
		//diagonal front slash
		for(int i = 1; i <= CONNECT_HOW_MANY - 1; i++)
		{
			//out of bounds check
			if(rowNum - i < 0 || columnNum + i > COLUMNS - 1) continueLeftOrUp = false;
		
			//up and right
			if(continueLeftOrUp)
			{
				if(gameBoard[rowNum - i][columnNum + i] == playerChar) count++;
				else continueLeftOrUp = false;
			}
			
			//out of bounds check
			if(rowNum + i > ROWS - 1 || columnNum - i < 0) continueRightOrDown = false;
			
			//down and left
			if(continueRightOrDown)
			{
				if(gameBoard[rowNum + i][columnNum - i] == playerChar) count++;
				else continueRightOrDown = false;
			}
		}
		
		//victory check
		if(count >= CONNECT_HOW_MANY) return true;
		
		//if victory has not been achieved, return false
		return false;
	}//end isVictoryConditionMet
	
	//This method will print the game board after every play with
	//A for player 1's piece and B for player 2's piece, so on and so on. 
	public static void printGameBoard(char[][] gameBoard)
	{
		//these char variables control the various aspects of the printed game board
		char verticalLine = '|';
		char space = ' ';
		char dash = '-';
		
		//start with the first row
		for(int i = 0; i < gameBoard.length; i++)
		{
			//print a vertical line at the beginning of the board
			System.out.print(verticalLine);
			
			//print a character followed by a vertical line or a space
			//followed by a vertical line in every column based on what is in 
			//the gameBoard array
			for(int j = 0; j < gameBoard[i].length; j++)
			{
				//if the game board is more than 10 spaces wide, the footer at the bottom of the 
				//board will be off.  to remedy this, we will create a special case where
				//the game board is double digits in width.  Since the default value is less than 
				//double digits then we will do the less than comparison first and completely skip the 
				//greater than comparison
				if(gameBoard[0].length <= 10)
				{	
					if(gameBoard[i][j] == 0) System.out.print(space + "" + verticalLine);
					else System.out.print(gameBoard[i][j] + "" + verticalLine);
					continue;
				}//end if
				
				if(gameBoard[0].length > 10)
				{
					if(gameBoard[i][j] == 0) System.out.print(space + "" + space + space + verticalLine);
					else System.out.print(space + "" + gameBoard[i][j] + space  + verticalLine);	
				}//end if
			}//end for
			System.out.println();
		}//end for
		
		//this will print the footer labeling each column from 1 to last column
		//taking into account a board with double and triple digits wide
		System.out.print(dash);
		for(int i = 1; i <= gameBoard[0].length; i++)
		{
			if(gameBoard[0].length > 10)
			{
				if(i < 10)
				{	
					System.out.print(dash + "" + i + dash + dash);
				}
				if(i >= 10 && i < 100)
				{	
					System.out.print(i + "" + dash + dash);
				}
				if(i >= 100)
				{	
					System.out.print(i + "" + dash);
				}
			}//end if
			else System.out.print(i + "" + dash);
		}//end for
		System.out.println();
	}//end printGameBoard
}//end class
