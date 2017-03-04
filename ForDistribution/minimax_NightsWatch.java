import java.util.*;


public class minimax_NightsWatch extends AIModule
{
	private int[] moves;
	private int player;
	private int opponent;

	@Override
	public void getNextMove(final GameStateModule state){
		int depth = 1;
		moves = new int[state.getWidth()];
		player = state.getActivePlayer();
		opponent = player % 2 + 1;

		while (!terminate){
			miniMaxDecision(state, depth);
			depth++;
		}
	}

	public void miniMaxDecision(final GameStateModule state, int depth)
	{
		int numLegalMoves = getMoves(state);
		int[] curMoves = moves;
		int maxVal = Integer.MIN_VALUE;
		int maxMove = -1;
		int temp;

		for (int i = 0; i < numLegalMoves; i++)
		{
			if (terminate){
				return;
			}
			state.makeMove(curMoves[i]);
			temp = min(state, depth - 1);
			if (temp > maxVal)
			{
				maxVal = temp;
				maxMove = curMoves[i];
			}
			state.unMakeMove();
		}
		if (!terminate)
			chosenMove = maxMove;
	}

	private int min(final GameStateModule state, int depth)
	{
		if (depth <= 0) {
			return eval(state, depth);
		}

		int numLegalMoves = getMoves(state);

		if (numLegalMoves == 0){
			return eval(state, depth);
		}
		int[] curMoves = moves;
		int minVal = Integer.MAX_VALUE;
		int temp;
		for (int i = 0; i < numLegalMoves; i++)
		{
			state.makeMove(curMoves[i]);
			temp = max(state, depth - 1);
			if (temp < minVal)
			{
				minVal = temp;
			}
			state.unMakeMove();
			if (terminate){
				return minVal;
			}
		}

		return minVal;
	}

	private int max(final GameStateModule state, int depth)
	{
		if (depth <= 0) {
			return eval(state, depth);
		}	
		int numLegalMoves;
		numLegalMoves = getMoves(state);

		if (numLegalMoves == 0){
			return eval(state, depth);
		}

		int[] curMoves = moves;
		int maxVal = Integer.MIN_VALUE;
		int temp;
		for (int i = 0; i < numLegalMoves; i++)
		{
			state.makeMove(curMoves[i]);
			temp = min(state, depth - 1);
			if (temp > maxVal)
			{
				maxVal = temp;
			}
			state.unMakeMove();
			if (terminate){
				return maxVal;
			}
		}

		return maxVal;	
	}

	private int getMoves(final GameStateModule state)
	{
		int numLegalMoves = 0;
		moves = new int[state.getWidth()];
		for(int i = 0; i < state.getWidth(); ++i)
			if(state.canMakeMove(i))
				moves[numLegalMoves++] = i;
		return numLegalMoves;

	}

	private int eval(final GameStateModule state, int depth){
		int value = 0;
		if (state.isGameOver()){
			if (state.getWinner() == player)
				return 1000 + depth;
			else if (state.getWinner() == opponent)
				return -1000 + depth;
			else
				return 0;
		}

		for (int j = 0; j < state.getHeight(); j++)
			for (int i = 0; i < state.getWidth(); i++){
				int p = state.getAt(i,j);
				if (p == player){
					value += getScore(state, i, j, p);
				}

				if (p == opponent){
					value -= getScore(state, i, j, p);
				}
			}

		return value;
	}

	private int getScore(final GameStateModule state, int i, int j, int curPlayer){
		int score = 0;
		int temp;
		int p;
		temp = 0;
		for (int x = 0; x + i < state.getWidth() && x < 4; x++){
			p = state.getAt(i + x, j);
			if (p == curPlayer)
				temp += 35;
			if (p == curPlayer % 2 + 1){
				temp = 0;
				break;
			}
			if (x == 1)
				score += 2;
			if (x == 2)
				score += 8;
			if (x == 3)
				score += temp;
		}
		temp = 0;
		for (int x = 0; x + i < state.getWidth() && x + j < state.getHeight() && x < 4; x++){
			p = state.getAt(i + x, j + x);
			if (p == curPlayer)
				temp += 35;
			if (p == curPlayer % 2 + 1){
				temp = 0;
				break;
			}
			if (x == 1)
				score += 2;
			if (x == 2)
				score += 8;
			if (x == 3)
				score += temp;
		}
		temp = 0;
		for (int x = 0; x + j < state.getHeight() && x < 4; x++){
			p = state.getAt(i, j + x);
			if (p == curPlayer)
				temp += 35;
			if (p == curPlayer % 2 + 1){
				temp = 0;
				break;
			}
			if (x == 1)
				score += 2;
			if (x == 2)
				score += 8;
			if (x == 3)
				score += temp;
		}
		temp = 0;
		for (int x = 0; i - x >= 0 && x + j < state.getHeight() && x < 4; x++){
			p = state.getAt(i - x, j + x);
			if (p == curPlayer)
				temp += 35;
			if (p == curPlayer % 2 + 1){
				temp = 0;
				break;
			}
			if (x == 1)
				score += 2;
			if (x == 2)
				score += 8;
			if (x == 3)
				score += temp;
		}
		temp = 0;
		for (int x = 0; i - x >= 0 && x < 4; x++){
			p = state.getAt(i - x, j);
			if (p == curPlayer)
				temp += 35;
			if (p == curPlayer % 2 + 1){
				temp = 0;
				break;
			}
			if (x == 1)
				score += 2;
			if (x == 2)
				score += 8;
			if (x == 3)
				score += temp;
		}
		return score;
	}

	private static int[][] evalTable =
		{{3, 4, 5,  9,  5,  4, 3},
		 {4, 6, 8,  10, 8,  6, 4},
		 {5, 8, 11, 13, 11, 8, 5},
		 {5, 8, 11, 13, 11, 8, 5},
 		 {4, 6, 8,  10, 8,  6, 4},
		 {3, 4, 5,  9,  5,  4, 3}};

}
