//import java.util.*;

public class reformedAI extends AIModule
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
            //System.out.print("\n\ndepth: " + depth + " chosen:" + chosenMove + "\n");
            miniMaxDecision(state, depth);
            depth++;
        }

        //System.out.print("\nChosen Move:" + chosenMove + "\n");

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
                //System.out.print("terminate");
                return;
            }
            state.makeMove(curMoves[i]);
            //System.out.print("\n\nDepth: " + depth + ", AI Move column: " + curMoves[i] + "\n");
            temp = min(state, depth - 1);
            //System.out.print("\nMiniMax Value: " + temp + "\n");
            if (temp > maxVal)
            {
                maxVal = temp;
                maxMove = curMoves[i];
            }
            state.unMakeMove();
        }

        chosenMove = maxMove;
    }

    private int min(final GameStateModule state, int depth)
    {
        if (depth <= 0) {
            return eval(state);
        }

        int numLegalMoves = getMoves(state);

        if (numLegalMoves == 0){
            return eval(state);
        }
        int[] curMoves = moves;
        int minVal = Integer.MAX_VALUE;
        int temp;
        for (int i = 0; i < numLegalMoves; i++)
        {
            // if (terminate){
            // 	System.out.print("terminate");
            // 	return minVal;
            // }
            state.makeMove(curMoves[i]);
            temp = max(state, depth - 1);
//            if (depth == 1)
//                System.out.print("Min: " + temp + '\n');
            if (temp < minVal) //&& temp != Integer.MIN_VALUE)
            {
                minVal = temp;
            }
//            if (terminate){
//                //System.out.print("terminate");
//                return minVal;
//            }
            state.unMakeMove();
            if (terminate){
                //System.out.print("terminate");
                return minVal;
            }
        }

        return minVal;
    }

    private int max(final GameStateModule state, int depth)
    {
        if (depth <= 0) {
            return eval(state);
        }
        int numLegalMoves = getMoves(state);

        if (numLegalMoves == 0){
            return eval(state);
        }

        //System.out.print("Before Loop, numLegalMoves:" + numLegalMoves + (numLegalMoves == 0) + '\n');
        int[] curMoves = moves;
        int maxVal = Integer.MIN_VALUE;
        int temp;
        for (int i = 0; i < numLegalMoves; i++)
        {
            // if (terminate){
            // 	//System.out.print("terminate");
            // 	return maxVal;
            // }
            state.makeMove(curMoves[i]);
            temp = min(state, depth - 1);
//            if (depth == 1)
//                System.out.print("Max: " + temp + '\n');
            if (temp > maxVal) //&& temp != Integer.MAX_VALUE)
            {
                maxVal = temp;
            }
            if (terminate){
                //System.out.print("terminate");
                return maxVal;
            }
            state.unMakeMove();
        }

        return maxVal;
    }

    private int getMoves(final GameStateModule state)
    {
        // Fill in what moves are legal.
        int numLegalMoves = 0;
        moves = new int[state.getWidth()];
        for(int i = 0; i < state.getWidth(); ++i)
            if(state.canMakeMove(i))
                moves[numLegalMoves++] = i;
        return numLegalMoves;

    }

    private int eval(final GameStateModule state){
        int value = 0;
        if (state.isGameOver()){
            if (state.getWinner() == player)
                return 999999;
            else if (state.getWinner() == opponent)
                return -999999;
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
    private int goHorizontalRight(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 10;
        int p;
        for (int x = 0; x + i < state.getWidth() && x < 3; x++){
            p = state.getAt(i + x, j);
            if (p == curPlayer) {
                temp += evalTable[j][i]*val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
        //score += evalTable[j][i];
        return score;
    }
    private int goHorizontalLeft(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 10;
        int p;
        for (int x = 0; i -x >= 0; x++){
            p = state.getAt(i - x, j);
            if (p == curPlayer) {
                temp += evalTable[j][i]*val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
        //score += evalTable[j][i];
        return score;
    }
    private int goDiagonalRight(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 10;
        int p;
        for (int x = 0; x + i < state.getWidth() && x + j < state.getHeight() && x < 3; x++){
            p = state.getAt(i + x, j + x);
            if (p == curPlayer) {
                temp += evalTable[j][i]*val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
        //score += evalTable[j][i];
        return score;
    }
    private int goDiagonalLeft(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 10;
        int p;
        for (int x = 0; i - x >= 0 && x + j < state.getHeight() && x < 3; x++){
            p = state.getAt(i - x, j + x);
            if (p == curPlayer) {
                temp += evalTable[j][i]*val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
        //score += evalTable[j][i];
        return score;
    }

    private int goVertical(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 10;
        int p;
        for (int x = 0; x + j < state.getHeight() && x < 3; x++){
            p = state.getAt(i, j + x);
            if (p == curPlayer) {
                temp += evalTable[j][i]*val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
        //score += evalTable[j][i];
        return score;
    }
    private boolean checkBounds(GameStateModule state, int i, int j){
        return !(i < 0 || j < 0 || i >= state.getWidth() || j >= state.getHeight());
    }

    private int getScore(final GameStateModule state, int i, int j, int curPlayer){
        int value = 0;
        if(checkBounds(state, i-1, j)){
            value += goHorizontalLeft(state, i-1, j, curPlayer);
        }
        if(checkBounds(state, i+1, j)){
            value += goHorizontalRight(state, i+1, j, curPlayer);
        }
        if(checkBounds(state, i-1, j+1)){
            value += goDiagonalLeft(state, i-1, j+1, curPlayer);
        }
        if(checkBounds(state, i+1, j+1)){
            value += goDiagonalRight(state, i+1, j+1, curPlayer);
        }
        if(checkBounds(state, i, j+1)){
            value += goVertical(state, i, j+1, curPlayer);
        }

        return value;
    }

    private static int[][] evalTable =
            {{3, 4, 5,  9,  5,  4, 3},
            {4, 6, 8,  10, 8,  6, 4},
            {5, 8, 11, 13, 11, 8, 5},
            {5, 8, 11, 13, 11, 8, 5},
            {4, 6, 8,  10, 8,  6, 4},
            {3, 4, 5,  9,  5,  4, 3}};

    // public int eval(final GameStateModule game) {
    // 	int utilityValue = 69;
    // 	if (game.isGameOver()){
    // 		if (game.getWinner() == player)
    // 			return 100;
    // 		if (game.getWinner() == opponent)
    // 			return 40;
    // 	}

    // 	for (int i = 0; i < 6; i++)
    // 		for (int j = 0; j < 7; j++)
    // 			if (game.getAt(j,i) == player)
    // 				utilityValue += evalTable[i][j];
    // 			else if (game.getAt(j,i) == opponent)
    // 				utilityValue -= evalTable[i][j];
    // 	return  utilityValue;

    // }
}
