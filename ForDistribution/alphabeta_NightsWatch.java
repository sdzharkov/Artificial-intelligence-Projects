import java.lang.Math.*;

public class alphabeta_NightsWatch extends AIModule
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

        if (numLegalMoves == 0)
            return;

        int maxVal = Integer.MIN_VALUE;
        int temp;

        for (int i = 0; i < numLegalMoves; i++) {
            state.makeMove(curMoves[i]);
            temp = alphaMin(state, depth - 1, alpha, beta);
            //System.out.print("\nMiniMax Value: " + temp + "\n");
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

        if (!terminate)
            chosenMove = maxMove;
    }

    private int alphaMax(final GameStateModule state, int depth, int alpha, int beta){
        if (depth <= 0){
            return eval(state, depth);
        }

        int numLegalMoves = getMoves(state);
        int[] curMoves = moves;

        if (numLegalMoves == 0) {
            return eval(state, depth);
        }
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
        if (depth <= 0){
            return eval(state, depth);
        }

        int numLegalMoves = getMoves(state);
        int[] curMoves = moves;

        if (numLegalMoves == 0) {
            return eval(state, depth);
        }

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
        //System.out.print(evalTable[j][i]);
        //score += evalTable[j][i];

        return score;
    }

}
