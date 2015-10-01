import java.awt.Color;
import java.util.*;

class Computer extends GomokuPlayer {
	
	public Move chooseMove(Color[][] board, Color me) {

		/********************************************
		CONVERTING COLOR BOARD ARRAY INTO INTEGER ARRAY 
		WHERE 	0 = NULL
				1 = ME;
				2 = OPPONENT
		AND ALSO STORING POSSIBLE MOVES INTO ARRAYLIST
		OF STRING OF ROW AND COL SEPARATED BY "-"
		EXAMPLE: ROW = 1 AND COL = 7
				 STRING WILL BE "1-7"
		********************************************/

		int[][] arrboard = new int[8][8];
		ArrayList<String> moves = new ArrayList<>();
		for (int i=0;i<GomokuBoard.ROWS;i++) {
			for (int j=0;j<GomokuBoard.COLS;j++) {
				if (board[i][j] == null) {
					arrboard[i][j] = 0;
					moves.add(i+"-"+j);
				}else if (board[i][j] == me) {
					arrboard[i][j] = 1;
				}else {
					arrboard[i][j] = 2;
				}
			}
		}

		/*
		TO CHECK IF THE BOARD CONTAINS ONLY 4 STONES.
		AND FIND A LOCATION FROM WHERE USER HAVE
		MORE CHANCE OF PLACING 5 STONES TOGETHER
		*/
		if (moves.size() > 60) {
			int finalrow = 0;
			int finalcol = 0;
			int finalscore = -1;
			for (int i=0;i<moves.size();i++) {
				String[] rowcol = moves.get(i).split("-");
				int row = Integer.parseInt(rowcol[0]);
				int col = Integer.parseInt(rowcol[1]);
				int s1 = countLeft(arrboard,row,col); 
				int s2 = countDown(arrboard,row,col);
				int s3 = countUp(arrboard,row,col);
				int s4 = countDown(arrboard,row,col); 
				int s5 = countDup(arrboard,row,col);
				int s6 = countDdown(arrboard,row,col);
				int s7 = countDUp(arrboard,row,col);
				int s8 = countDDown(arrboard,row,col);
				int score = s1+s2+s3+s4+s5+s6+s7+s8;
				if (score > finalscore){
					finalscore = score;
					finalrow = row;
					finalcol = col;
				}

			}
			return new Move(finalrow,finalcol);
		}
		
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		String bestloc = moves.get(0);

		for (int i=0 ; i<moves.size() ; i++){
			String[] rowcol = moves.get(i).split("-");
			int row = Integer.parseInt(rowcol[0]);
			int col = Integer.parseInt(rowcol[1]);
			arrboard[row][col] = 1;

			//TO CHECK IF THE GAME HAS BEEN WON
			String l1 = verticalline(arrboard,col);
			if (l1.contains("11111")) {
				return new Move(row,col);
			}
			String l2 = horizontalline(arrboard,row);
			if (l2.contains("11111")) {
				return new Move(row,col);
			}
			String l3 = diagonalRight(arrboard,row,col);
			if (l3.contains("11111")) {
				return new Move(row,col);
			}
			String l4 = diagonalleft(arrboard,row,col);
			if (l4.contains("11111")) {
				return new Move(row,col);
			}

			int score = alphabeta(arrboard,2,false,alpha,beta);
			arrboard[row][col] = 0;
			if (score > alpha) {
				alpha = score;
				bestloc = row+"-"+col;
			}
		}

		String[] finalmove = bestloc.split("-");
		return new Move(Integer.parseInt(finalmove[0]),Integer.parseInt(finalmove[1]));
	}
	
	private int alphabeta(int[][] board,int depth,boolean minmax,int alpha,int beta){
		if(depth == 0){ //DEPTH OF SEARCH REACHED AND RETURN EVALUATION RESULT
			return evulate(board);
		}else {
			if (minmax) { //MAX
				ArrayList<String> moves = generateMoves(board);

				for (int i=0 ; i<moves.size() ; i++) {
					String[] rowcol = moves.get(i).split("-");
					int row = Integer.parseInt(rowcol[0]);
					int col = Integer.parseInt(rowcol[1]);

					board[row][col] = 1;
					int score = alphabeta(board,depth-1,false,alpha,beta);
					board[row][col] = 0;
					if (score > alpha) {
						alpha = score;
					}
					if (alpha >= beta) {
						return beta;
					}
				}
				return alpha;
			}else { //MIN
				ArrayList<String> moves = generateMoves(board);
				for (int i=0 ; i<moves.size() ; i++) {
					String[] rowcol = moves.get(i).split("-");
					int row = Integer.parseInt(rowcol[0]);
					int col = Integer.parseInt(rowcol[1]);

					board[row][col] = 2;
					int score = alphabeta(board,depth-1,true,alpha,beta);
					board[row][col] = 0;
					if (score < beta) {
						beta = score;
					}
					if (beta <= alpha) {
						return alpha;
					}
				}
				return beta;				
			}
		}
	}
	
	//GENERTES POSSIBLE MOVES IN GIVEN BOARD	
	private ArrayList<String> generateMoves(int[][] board){
		ArrayList<String> moves = new ArrayList<>();
		for (int row=0 ; row<GomokuBoard.ROWS; row++) {
			for (int col=0;col<GomokuBoard.COLS; col++) {
				if (board[row][col] == 0) {
					moves.add(row+"-"+col);
				}
			}
		}
		return moves;
	}
	
	//EVALUTES THE STATE OF THE BOARD AND RETURNS SCORE
	private int evulate(int[][] board){
		return horizontal(board)+vertical(board)+leftdigonal(board)+rightdigonal(board);
	}
	
	/********************************************
	RETURNS THE SCORE BASED ON FINDING CERTAIN 
	PATTERN IN THE GIVEN ARGUMENT STRING (INPUT)
	EXAMPLE INPUT = "01100020" WILL RETURN 1
			AS "01100" PATTERN APPEARS IN INPUT
	********************************************/
		
	private int score(String input){					
		if (input.contains("2011100")) {
			return 10;
		}else if (input.contains("1022200")) {
			return -10;
		}else if (input.contains("0011102")) {
			return 10;
		}else if (input.contains("0022201")) {
			return -10;
		}else if (input.contains("011110")) {
			return 1000;
		}else if (input.contains("022220")) {
			return -1000;
		}else if (input.contains("010110")) {
			return 10;
		}else if (input.contains("020220")) {
			return -10;
		}else if (input.contains("011010")) {
			return 10;
		}else if (input.contains("022020")) {
			return -10;
		}else if (input.contains("11111")) {
			return 10000;
		}else if (input.contains("22222")) {
			return -10000;
		}else if (input.contains("11110")) {
			return 100;
		}else if (input.contains("22220")) {
			return -100;
		}else if (input.contains("01111")) {
			return 100;
		}else if (input.contains("02222")) {
			return -100;
		}else if (input.contains("01110")) {
			return 10;
		}else if (input.contains("02220")) {
			return -10;
		}else if (input.contains("11101")) {
			return 100;
		}else if (input.contains("22202")) {
			return -100;
		}else if (input.contains("11011")) {
			return 100;
		}else if (input.contains("22022")) {
			return -100;
		}else if (input.contains("10111")) {
			return 100;
		}else if (input.contains("20222")) {
			return -100;
		}else if (input.contains("01100")){
			return 1;
		}else if (input.contains("02200")){
			return -1;
		}else if (input.contains("11000")){
			return 1;
		}else if (input.contains("22000")){
			return -1;
		}else if (input.contains("00011")){
			return 1;
		}else if (input.contains("00022")){
			return -1;
		}else {
			return 0;
		}
	}
	
	//RETURNS THE SCORE OF ALL THE DIAGONAL LINES FROM LEFT TO RIGHT
	private int rightdigonal(int[][] board){
		int score = 0;
		score = score + helper1(board,0,0);
		score = score + helper1(board,1,0);
		score = score + helper1(board,2,0);
		score = score + helper1(board,3,0);
		score = score + helper1(board,0,1);
		score = score + helper1(board,0,2);
		score = score + helper1(board,0,3);
		return score;
	}
	
	/*
	HELPER METHOD TO GET THE SCORE FOR RIGHT DIAGONAL.
	GENERATES DIAGONAL LINES FROM GIVEN LOCATION (ROW
	AND COL) AND GETS THE SCORE FOR THAT LINE
	*/ 
	private int helper1(int[][] board,int row,int col){
		String line = "";
		row = row - 1;
		col = col - 1;
		for (int i=0 ; i<GomokuBoard.ROWS ; i++) {
			try {
				row = row + 1;
				col = col + 1;
				line = line + board[row][col];
			} catch (Exception e) {break;}
		}
		return score(line);
	}
	
	//RETURNS THE SCORE OF ALL THE DIAGONAL LINES FROM RIGHT TO LEFT
	private int leftdigonal(int[][] board){
		int score = 0;
		score = score + helper2(board,0,7);
		score = score + helper2(board,1,7);
		score = score + helper2(board,2,7);
		score = score + helper2(board,3,7);
		score = score + helper2(board,0,6);
		score = score + helper2(board,0,5);
		score = score + helper2(board,0,4);
		return score;
	}

	/*
	HELPER METHOD TO GET THE SCORE FOR LEFT DIAGONAL.
	GENERATES DIAGONAL LINES FROM GIVEN LOCATION (ROW
	AND COL) AND GETS THE SCORE FOR THAT LINE
	*/
	
	private int helper2(int[][] board,int row,int col){
		String line = "";
		row = row - 1;
		col = col + 1;
		for (int i=0 ; i<GomokuBoard.ROWS ; i++) {
			try {
				row = row + 1;
				col = col - 1;
				line = line + board[row][col];
			} catch (Exception e) {break;}
		}
		return score(line);
	}
	
	//RETURNS THE SCORE OF ALL HORIZONAL LINES OF THE BOARD	
	private int horizontal(int[][] board){
		int score = 0;
		for (int i=0 ; i<GomokuBoard.ROWS; i++) {
			String line = "";
			for (int j=0 ; j<GomokuBoard.COLS; j++) {
				line = line + board[i][j];
			}
			score = score + score(line);
		}
		return score;		
	}
	
	//RETURNS THE SCORE OF ALL THE VERTICAL LINES OF THE BOARD
	private int vertical(int[][] board){
		int score = 0;
		for (int i= 0 ; i<GomokuBoard.ROWS; i++) {
			String line = "";
			for (int j=0 ; j<GomokuBoard.COLS; j++) {
				line = line + board[j][i];
			}
			score = score + score(line);
		}
		return score;
	}

	//RETURNS THE VERTICAL LINE REPRESENTATION FOR GIVEN COL AS STRING
	private String verticalline(int[][] board, int col){
		String line = "";
		for (int i=0;i<GomokuBoard.ROWS ;i++) {
			line = line + board[i][col];
		}
		return line;
	}

	//RETURNS THE HORIZONTAL LINE REPRESENTATION FOR GIVEN ROW AS STRING
	private String horizontalline(int[][] board,int row){
		String line = "";
		for (int i=0;i<GomokuBoard.ROWS;i++) {
			line = line + board[row][i];
		}
		return line;
	}
	
	//RETURNS THE LEFT DIAGONAL LINE REPRESENTATION FOR GIVEN LOCATION AS STRING
	private String diagonalleft(int[][] board,int row,int col){
		String line1 = "";
		String line2 = "";
		for (int i=0; i<8; i++) {
			try {
				line1 = line1 + board[row+i][col+i];
			} catch (Exception e) {
				break;
			}
		}
		
		for (int i=0 ; i<8; i++) {
			try {
				line2 = board[row-i][col-i] + line2;
			} catch (Exception e) {
				break;
			}
		}
		return line1 + line2;
	}

	//RETURNS THE RIGHT DIAGONAL LINE REPRESENTATION FOR GIVEN LOCATION AS STRING
	private String diagonalRight(int[][] board,int row,int col){
		String line1 = "";
		String line2 = "";
		for (int i=0; i<8; i++) {
			try {
				line1 = line1 + board[row-i][col+i];
			} catch (Exception e) {
				break;
			}
		}
		
		for (int i=0 ; i<8; i++) {
			try {
				line2 = board[row+i][col-i] + line2;
			} catch (Exception e) {
				break;
			}
		}
		return line1 + line2;
	}

	/******************************************************
	Methods below are used to count the number of space the
	user has from a given location to place its stone.
	Methods increment the count by 1 if the next location is
	empty and increment the count by 2 if the next location
	is occupied by user's stone. And stops counting if the next
	space is occupied by opponent's stone.
	*****************************************************/

	//Count the number of moves available toward the left
	private int countLeft(int[][] board,int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x][y-i] == 0) {
					count++;
				}else {
					if (board[x][y-i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}
	
	//Count the number of moves available toward the right
	private int countRight(int[][] board,int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x][y+i] == 0) {
					count++;
				}else {
					if (board[x][y+i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}
	
	//Count the number of moves available toward up
	private int countUp(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x-i][y] == 0) {
					count++;
				}else {
					if (board[x-i][y] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}

	//Count the number of moves available toward the down
	private int countDown(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x+i][y] == 0) {
					count++;
				}else {
					if (board[x+i][y] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}

	//Count the number of moves available diagonally down from left to right
	private int countDdown(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x+i][y+i] == 0) {
					count++;
				}else {
					if (board[x+i][y+i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}

	//Count the number of moves available diagonally up from left to right
	private int countDup(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x-i][y-i] == 0) {
					count++;
				}else {
					if (board[x-i][y-i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}
	
	//Count the number of moves available diagonally up from right to left
	private int countDUp(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x+i][y-i] == 0) {
					count++;
				}else {
					if (board[x+i][y-i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}

	//Count the number of moves available diagonally down from right to left
	private int countDDown(int[][] board, int x,int y){
		int count = 0;
		for (int i=1 ; i<5 ; i++) {
			try {
				if (board[x-i][y+i] == 0) {
					count++;
				}else {
					if (board[x-i][y+i] == 1) {
						count = count + 2;
					}else {
						return count;
					}
				}
			} catch (Exception e) {return count;}
		}
		return count;
	}
}