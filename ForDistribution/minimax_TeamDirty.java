import java.util.*;
import java.awt.Point;

public class minimax_TeamDirty extends AIModule{
	private int our_player;
	public ArrayList<Point> player1Ats = new ArrayList<Point>();
	public ArrayList<Point> player2Ats = new ArrayList<Point>();


	@Override
	public void getNextMove(final GameStateModule state){
		int i = 1;
		final GameStateModule game = state.copy();
		this.our_player = game.getActivePlayer();
		while (!terminate){
			//System.out.print("\n\ndepth: " + i + "\n\n");
			interactiveDeepeningMiniMaxDecision(game, i);
			i++;
			i++;
		}
	}

	public int makeMove(final GameStateModule game, int column){ 
	// store the move to a arraylist, reducing complexity of evaluation Function (also making things easier to deal with)
		int row = game.getHeightAt(column) - 1;
		if (game.getActivePlayer() == 1){
			this.player1Ats.add(new Point(row,column));
		}else{
			this.player2Ats.add(new Point(row,column));
		}
		////System.out.print("\ncolumn: " + column + " row: " + row);
		game.makeMove(column);
		return row;
	}
	public void unmakeMove(final GameStateModule game, int column, int row){
		game.unMakeMove();
		if (game.getActivePlayer() == 1){
			this.player1Ats.remove(new Point(row,column));
		}else{
			this.player2Ats.remove(new Point(row,column));
		}
	}

	public void interactiveDeepeningMiniMaxDecision(final GameStateModule game, int depth){
		int result_value = -1*Integer.MAX_VALUE;
		int result_value_aux;
		int column = 0;
		final int[] moves = new int[game.getWidth()];
		int numLegalMoves = 0;

	// Fill in what moves are legal.
		for(int i = 0; i < game.getWidth(); ++i)
			if(game.canMakeMove(i))
				moves[numLegalMoves++] = i;

	// For each possible action, returns the maximum value given depth
			for (int i = 0; i < numLegalMoves && !terminate; i++){
				int row = makeMove(game,moves[i]);
				result_value_aux = minValue(game, depth - 1, moves[i]);
	    if (result_value_aux >= result_value){// update result if the new result is greater than the previous calculated max
	    	column = moves[i];
	    	result_value = result_value_aux;
	    }
	    unmakeMove(game, moves[i],row);
	}

	chosenMove = (terminate)?chosenMove:column;
}

public int minValue(final GameStateModule game, int depth, int column){
	boolean gameIsOver = game.isGameOver();
	if (depth <= 0 || gameIsOver) return evaluationFunction(game, gameIsOver, column); 
	// cutoff test

	int v = Integer.MAX_VALUE;
	final int[] moves = new int[game.getWidth()];
	int numLegalMoves = 0;
	int v_aux;

	// Fill in what moves are legal.
	for(int i = 0; i < game.getWidth(); ++i)
		if(game.canMakeMove(i))
			moves[numLegalMoves++] = i;

		for (int i = 0; i < numLegalMoves && !terminate; i++){
			int row = makeMove(game,moves[i]);
			v_aux = maxValue(game, depth - 1, moves[i]);
			if (v_aux <= v){
				v = v_aux;
			}
			unmakeMove(game, moves[i],row);
		}

		if(terminate){
			//System.out.print("\n TERMINATE \n");
			return Integer.MAX_VALUE;
		}else{
			return v;
		}
	}

	public int maxValue(final GameStateModule game, int depth, int column){
		boolean gameIsOver = game.isGameOver();
		if (depth <= 0 || gameIsOver) return evaluationFunction(game, gameIsOver, column); 
	// terminal test (a function of the depth)

		int v = -1*Integer.MAX_VALUE;
		int[] moves = new int[game.getWidth()];
		int numLegalMoves = 0;
		int v_aux;

	// Fill in what moves are legal.
		for(int i = 0; i < game.getWidth(); ++i)
			if(game.canMakeMove(i))
				moves[numLegalMoves++] = i;

			for (int i = 0; i < numLegalMoves && !terminate; i++){
				int row = makeMove(game,moves[i]);
				v_aux = minValue(game, depth - 1, moves[i]);
				if (v_aux > v){
					v = v_aux;
				}
				unmakeMove(game, moves[i],row);
			}
			if(terminate){
				//System.out.print("\n TERMINATE \n");
				return Integer.MAX_VALUE;
			}else{
				return v;
			}
		}

		public int evaluationFunction(final GameStateModule game, boolean gameIsOver, int column) {
			if(!gameIsOver) {
	    Iterator[] itr = {this.player1Ats.iterator(), this.player2Ats.iterator()};// 
	    int width = game.getWidth(), height = game.getHeight();
	    int x, y;
	    int[] score = {0,0}, weights = {1,2,3,4,3,2,1};


	    for (int p = 0; p < 2; p++) {// iterate for each player
		while(itr[p].hasNext()) {// computes the score for each player's board points. 
		Point element = (Point)itr[p].next();
		x = (int)element.getX();
		y = (int)element.getY();
		for(int dx = -1; dx <= 1; dx++) {
			for(int dy = -1; dy <= 1; dy++) {
				if(x + dx < 0 || x + dx >= width || y + dy < 0 || y + dy >= height)
				// adjacent point out of bounds
					continue;
				if(game.getAt(x+dx,y+dy) == p+1) {
				// adjacent point belongs to the player being evaluated
					if(game.getAt(x-dx, y-dy) == p+1) {
				    // (x+dx,y+dy) make three in a row with (x,y);
						score[p] += 2*weights[column]; 
				    // it must be weighted in accordance with column, since it is known
				    // that who controls the middle has more advantage
					}else {
				    score[p] += 1; // two in a row is better than nothing.
				}
			}else{
				// the adjancent point or is empty or it belongs to another player, hence without importance
				continue;
			}
		}
	}
}
}
////System.out.print("\nscore[0]: " + score[0] + " score[1]: " + score[1]);
if(our_player == 1){
	return score[0] - score[1];
}else{
	return score[1] - score[0];
}
}else {
	    // returns +infinity if  winner==ourPlayer; -infinity otherwise;
	if(game.getWinner() == our_player){
		return Integer.MAX_VALUE;
	}else{
		return -1*Integer.MAX_VALUE;
	}
}
}
}