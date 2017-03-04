import java.lang.Math.*;

public class reformedAlphaS extends AIModule
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
            alphaBeta(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            depth++;
        }
    }

    private void alphaBeta(final GameStateModule state, int depth, int alpha, int beta){
        int numLegalMoves = getMoves(state);
        int[] curMoves = moves;
        int maxMove = -1;

        int maxVal = Integer.MIN_VALUE;
        int temp;

        for (int i = 0; i < numLegalMoves; i++) {
            if (terminate){
                return;
            }
            state.makeMove(curMoves[i]);
            temp = alphaMin(state, depth - 1, alpha, beta);
            if (temp > maxVal){
                maxVal = temp;
                maxMove = curMoves[i];
            }
            state.unMakeMove();
            alpha = Math.max(alpha, maxVal);
            if (beta <= alpha) {
                break;
            }
        }

        chosenMove = maxMove;
    }

    private int alphaMax(final GameStateModule state, int depth, int alpha, int beta){
        if (depth <= 0) {
            return eval(state,depth);
        }

        int numLegalMoves = getMoves(state);

        if (numLegalMoves == 0){
            return eval(state,depth);
        }
        int[] curMoves = moves;
        int maxVal = Integer.MIN_VALUE;

        for (int i = 0; i < numLegalMoves; i++) {
            state.makeMove(curMoves[i]);
            maxVal = Math.max(maxVal, alphaMin(state, depth - 1, alpha, beta));
            state.unMakeMove();
            alpha = Math.max(alpha, maxVal);
            if (beta <= alpha) {
                break;
            }
            if (terminate){
                return maxVal;
            }
        }

        return maxVal;
    }

    private int alphaMin(final GameStateModule state, int depth, int alpha, int beta){

        if (depth <= 0) {
            return eval(state,depth);
        }

        int numLegalMoves = getMoves(state);

        if (numLegalMoves == 0){
            return eval(state,depth);
        }
        int[] curMoves = moves;
        int minVal = Integer.MAX_VALUE;

        for (int i = 0; i < numLegalMoves; i++) {
            state.makeMove(curMoves[i]);
            minVal = Math.min(minVal, alphaMax(state, depth - 1, alpha, beta));
            state.unMakeMove();
            beta = Math.min(beta, minVal);
            if (beta <= alpha) {
                break;
            }
            if (terminate){
                return minVal;
            }
        }

        return minVal;
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

    private int eval(final GameStateModule state, int depth){
        int value = 0;
        if (state.isGameOver()){
            if (state.getWinner() == player)
                return 999999 + depth;
            else if (state.getWinner() == opponent)
                return -999999 - depth;
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
                temp += val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
//        score += evalTable[j][i];
        return score;
    }
    private int goHorizontalLeft(final GameStateModule state, int i, int j, int curPlayer){
        int score = 0;
        int temp = 0;
        int val = 5;
        int p;
        for (int x = 0; i -x >= 0; x++){
            p = state.getAt(i - x, j);
            if (p == curPlayer){
                temp += val * 3;
            }
            if (p == 0) {
                temp += val;
                //val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
//        score += evalTable[j][i];
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
                temp += val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
//        score += evalTable[j][i];
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
                temp += val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
//        score += evalTable[j][i];
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
                temp += val;
                val *= 10;
            }
            if (p == curPlayer % 2 + 1){
                temp = 0;
                break;
            }
            if (x == 2)
                score += temp;
        }
//        score += evalTable[j][i];
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

    private static int[][] evalTable = {
            {  3,  4,  5,  7,  7,  5,  4,  3 },
            {  4,  6,  8, 10, 10,  8,  6,  4 },
            {  5,  8, 11, 13, 13, 11,  8,  5 },
            {  7, 10, 13, 16, 16, 13, 10,  7 },
            {  7, 10, 13, 16, 16, 13, 10,  7 },
            {  5,  8, 11, 13, 13, 11,  8,  5 },
            {  4,  6,  8, 10, 10,  8,  6,  4 },
            {  3,  4,  5,  7,  7,  5,  4,  3 } };

}
