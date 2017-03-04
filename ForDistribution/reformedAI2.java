import java.util.*;

/// Sample AI module that picks random moves at each point.
/**
 * This AI performs random actions.  It is meant to illustrate the basics of how to
 * use the AIModule and GameStateModule classes.
 *
 * Since this terminates in under a millisecond, there is no reason to check for
 * the terminate flag.  However, your AI needs to check for the terminate flag.
 *
 * @author Leonid Shamis
 */
public class reformedAI2 extends AIModule
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
                System.out.print("terminate");
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
            if (terminate){
                //System.out.print("terminate");
                return minVal;
            }
            state.unMakeMove();
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
        score += evalTable[j][i];
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
        score += evalTable[j][i];
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
        score += evalTable[j][i];
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
        score += evalTable[j][i];
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
        score += evalTable[j][i];
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


//    private boolean checkBounds(GameStateModule state, int j, int i){
//        return !(i < 0 || j < 0 || i >= state.getHeight() || j >= state.getWidth());
//    }
//
//    private int verticalCheck(GameStateModule state, int j, int i, int player){
//        int count = 0;
//        int counter = 10;
//        int value = 0; // the final value for
//        boolean notSkipped = true;
//        for (; i < state.getHeight(); i++) {
//            if(count >= 2)
//               return value;
//            if (checkBounds(state,j, i)) {
//                if (state.getAt(j,i) == player && notSkipped){
//                    count++;
//                    value += counter;
//                    counter *= 10;
//                }
//                else if (state.getAt(j,i) != 0){
//                    return 0;
//
//                }
//                else {
//                    notSkipped = false;
//                }
//            }
//            else{
//                return 0;
//            }
//        }
//
//        return value;
//    }
//    private int horizontalCheck(GameStateModule state, int j, int i, int player){
//        int count = 0;
//        int counter = 10;
//        int value = 0;
//        boolean notSkipped = true;
//
//        for (; j < state.getWidth(); j++) {
//            System.out.println(count + ": value: "+value);
//            if(count >= 2){
//                System.out.println("count Pr");
//               return value;
//            }
//            if (checkBounds(state,j, i)) {
//                if (state.getAt(j,i) == player && notSkipped){
//                    System.out.println("yes");
//                    count++;
//                    value += counter;
//                    counter *= 10;
//                }
//                else if (state.getAt(j,i) != 0){
//                    System.out.println("oh");
//                    return 0;
//                }
//                else {
//                    System.out.println("shit");
//                    notSkipped = false;
//                }
//            }
//            else{
//                System.out.println("fuck");
//                return 0;
//            }
//        }
//        System.out.println("finished: ");
//        return value;
//
//    }
//    private int DiagonalLeftCheck(GameStateModule state, int j, int i, int player){
//        int count = 0;
//        int counter = 10;
//        int value = 0;
//        boolean notSkipped = true;
//
//        // //System.out.println("FIND: "+ i+" "+ grid[j].length+" "+ j);
//        for (; (j > -1) && (i < state.getHeight()); i++,j--) {
//            System.out.println(i + " " + j);
//            if(count >= 2)
//               return value;
//            if (checkBounds(state,j, i)) {
//                if (state.getAt(j,i) == player && notSkipped){
//                    System.out.println("here: ");
//                    count++;
//                    value += counter;
//                    counter *= 10;
//                }
//                else if (state.getAt(j,i) != 0)
//                    return 0;
//                else {
//                    notSkipped = false;
//                }
//            }
//            else
//                return 0;
//        }
//        return value;
//
//    }
//    private int DiagonalRightCheck(GameStateModule state, int j, int i, int player){
//        int count = 0;
//        int counter = 10;
//        int value = 0;
//        boolean notSkipped = true;
//
//        // //System.out.println("FIND: "+ i+" "+ grid[j].length+" "+ j);
//        for (; j < state.getWidth() && i < state.getHeight(); i++,j++) {
//            if(count >= 2)
//               return value;
//            if (checkBounds(state,j, i)) {
//                if (state.getAt(j,i) == player && notSkipped){
//                    count++;
//                    value += counter;
//                    counter *= 10;
//                }
//                else if (state.getAt(j,i) != 0)
//                    return 0;
//                else {
//                    notSkipped = false;
//                }
//            }
//            else
//                return 0;
//        }
//        return value;
//    }
//
//    private int findNeighbors(GameStateModule state, int j, int i, int player){
//        int value = 0;
//        if(checkBounds(state, j+1,i) && state.getAt(j+1,i) == player){
//            System.out.println("Went Horizontal for player " + player);
//            value += horizontalCheck(state, j+1, i, player);
//        }
//        if(checkBounds(state, i,j+1) && state.getAt(i,j+1) == player){
//            System.out.println("Went vertical for player " + player);
//            value += verticalCheck(state, j, i+1, player);
//        }
//        if(checkBounds(state, j-1,i+1) && state.getAt(j-1,i+1) == player){
//           System.out.println("Went diagonal left for player " + player + " " + i + " " + j);
//            value += DiagonalLeftCheck(state, j-1, i+1, player);
//        }
//        if(checkBounds(state, j+1,i+1) && state.getAt(j+1,i+1) == player){
//           System.out.println("Went diagonal righ for player " + player);
//            value += DiagonalRightCheck(state, j+1, i+1, player);
//        }
//        System.out.println("Should be a value: " + value);
//        return value;
//    }
//
//    private int eval(GameStateModule state){ // evaluation function with DFS on created Matrix
//        if (state.isGameOver())
//            if (state.getWinner() == player)
//                return 10000;
//            else if (state.getWinner() == opponent)
//                return -10000;
//
//        int value1 = 0;
//        int value2 = 0;
//        printState(state);
//
//        for (int i = 0; i < state.getHeight(); i++) {
//            for (int j = 0; j < state.getWidth(); j++) {
//                if (state.getAt(j,i) == 1){
//                    value1 += findNeighbors(state, j, i, 1);
//                }
//                else if (state.getAt(j,i) == 2){
//                    value2 += findNeighbors(state, j, i, 2);
//                }
//            }
//        }
//
//        System.out.println(" eval: " + value1 + " " + value2 + " " + (value1 - value2));
//        return (value1 - value2);
//    }
//
//    private void printState(GameStateModule state) { //Take out later on
//        for(int j = state.getWidth() - 1; j >= 0; j--) {
//            for(int i = 0; i < state.getHeight(); i++) {
//                if(state.getAt(i,j) == 1)
//                    System.out.print("X ");
//                else if(state.getAt(i,j) == 2)
//                    System.out.print("O ");
//                else
//                    System.out.print(". ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//    }
}
