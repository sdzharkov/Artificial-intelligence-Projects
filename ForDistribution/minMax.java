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
public class minMax extends AIModule
{

    private int[] moves;
    private int player;
    private int opponent;

    @Override
    public void getNextMove(final GameStateModule state){
        int depth = 1;
        moves = new int[state.getWidth()];
        final GameStateModule game = state.copy();
        player = state.getActivePlayer();
        opponent = player % 2 + 1;

        System.out.print(player + " " + opponent);
        while (!terminate){
            System.out.print("\n\ndepth: " + depth + " chosen:" + chosenMove + "\n");
            miniMaxDecision(game, depth);
            depth++;
        }
    }

    public void miniMaxDecision(final GameStateModule state, int depth)
    {
        int numLegalMoves = getMoves(state);
        int[] curMoves = moves;
        //System.out.print(curMoves[0]);
        int maxVal = Integer.MIN_VALUE;
        int maxMove = -1;
        int temp;

        for (int i = 0; i < numLegalMoves; i++)
        {
            if (terminate){
                System.out.print("terminate");
                return;
            }
            final GameStateModule game = state.copy();
            game.makeMove(curMoves[i]);
            //state.makeMove(curMoves[i]);
            System.out.print("\n\nDepth: " + depth + ", AI Move column: " + curMoves[i] + "\n");
            temp = min(game, depth - 1);
            System.out.print("\nMiniMax Value: " + temp + "\n");
            if (temp > maxVal)
            {
                maxVal = temp;
                maxMove = curMoves[i];
            }
            //state.unMakeMove();
        }

        chosenMove = maxMove;

    }

    private int min(final GameStateModule state, int depth)
    {
        System.out.print("In min\n");
        if (depth <= 0) {
            System.out.print("Depth less than zero\n");
            return eval(state);
        }

        else
        {
            int numLegalMoves = getMoves(state);
            if (state.isGameOver())
                if (numLegalMoves > 0)
                    System.out.print("legal moves but game over");
            if (numLegalMoves == 0){
                if (!state.isGameOver())
                    System.out.print("no moves but not game over");
                System.out.print("no legal moves, eval");
                System.out.print(eval(state));
                return eval(state);
            }
            int[] curMoves = moves;
            int minVal = Integer.MAX_VALUE;
            //int minMove = -1;
            int temp;
            for (int i = 0; i < numLegalMoves; i++)
            {
                if (terminate){
                    System.out.print("terminate");
                    return minVal;
                }
                final GameStateModule game = state.copy();
                game.makeMove(curMoves[i]);
                //if (game.isGameOver())
                //	return eval(game);
                //state.makeMove(curMoves[i]);
                System.out.print("Min move: " + curMoves[i] + "\n");
                temp = max(game, depth - 1);
                if (temp < minVal) //&& temp != Integer.MIN_VALUE)
                {
                    minVal = temp;
                    //maxMove = curMoves[i];
                }
                //state.unMakeMove();
            }
            System.out.print("Min actually returning minVal\n");
            return minVal;
        }
    }

    private int max(final GameStateModule state, int depth)
    {
        System.out.print("In max\n");
        if (depth <= 0) {
            System.out.print("max, depth less than 0 \n");
            System.out.print("Evaluated value due to depth = 0: " + eval(state) + "\n");
            return eval(state);
        }
        else
        {
            int numLegalMoves = getMoves(state);
            if (state.isGameOver())
                if (numLegalMoves > 0)
                    System.out.print("legal moves but game over");

            if (numLegalMoves == 0){
                if (!state.isGameOver())
                    System.out.print("no moves but not game over");
                System.out.print("max, no legal moves so eval");
                return eval(state);
            }
            int[] curMoves = moves;
            int maxVal = Integer.MIN_VALUE;
            //int minMove = -1;
            int temp;
            for (int i = 0; i < numLegalMoves; i++)
            {
                if (terminate){
                    System.out.print("terminate");
                    return maxVal;
                }
                final GameStateModule game = state.copy();
                game.makeMove(curMoves[i]);
                //state.makeMove(curMoves[i]);
                temp = min(game, depth - 1);
                if (temp > maxVal) //&& temp != Integer.MAX_VALUE)
                {
                    maxVal = temp;
                    //maxMove = curMoves[i];
                }
                //state.unMakeMove();
            }

            return maxVal;
        }
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

    private boolean checkBounds(int[][] grid, int i, int j){
        if (i < 0 || j < 0 || j >= grid.length || i >= grid[j].length)
            return false;
        return true;
    }

    private int verticalCheck(int[][] grid, int i, int j, int player){
        int count = 0;
        int counter = 10;
        int value = 0; // the final value for
        for (; i < grid[j].length; i++) {
//            if(count > 3){
//                return value;
//            }
            if (checkBounds(grid,i, j) && grid[j][i] == player){
                count++;
                value += counter;
                counter *= 10;
            }
            else{
                return value;
            }
        }
        return value;

    }
    private int horizontalCheck(int[][] grid, int i, int j, int player){
        int count = 0;
        int counter = 10;
        int value = 0;
        for (; j < grid.length; j++) {
            if (checkBounds(grid, i,j) && grid[j][i] == player){
                // System.out.println(checkBounds(grid, i, j) + " " + (grid[j][i] == player) + " " + grid[j][i]);
                count++;
                value += counter;
                counter *= 10;
            }
            else{
                //System.out.println("else: " + value);
                return value;
            }
        }
        //  System.out.println("finished: " + value);
        return value;

    }
    private int DiagonalLeftCheck(int[][] grid, int i, int j, int player){
        int count = 0;
        int counter = 10;
        int value = 0;
        // System.out.println("FIND: "+ i+" "+ grid[j].length+" "+ j);
        for (; (j > -1) && (i < grid[j].length); i++,j--) {
            if (checkBounds(grid, i,j) && grid[j][i] == player){
                count++;
                value += counter;
                counter *= 10;
            }
            else{
                return value;
            }
        }
        return value;

    }
    private int DiagonalRightCheck(int[][] grid, int i, int j, int player){
        int count = 0;
        int counter = 10;
        int value = 0;
        // System.out.println("FIND: "+ i+" "+ grid[j].length+" "+ j);
        for (; j < grid.length && i < grid[j].length; i++,j++) {
            if (checkBounds(grid, i,j) && grid[j][i] == player){
                count++;
                value += counter;
                counter *= 10;
            }
            else{
                return value;
            }
        }
        return value;
    }

    private int findNeighbors(int[][] grid, int i, int j, int player){
        int value = 0;
        if(checkBounds(grid, i,j+1) && grid[j+1][i] == player){
            System.out.println("Went Horizontal for player " + player);
            value += horizontalCheck(grid, i, j+1, player);
        }
        if(checkBounds(grid, i+1,j) && grid[j][i+1] == player){
            System.out.println("Went vertical for player " + player);
            value += verticalCheck(grid, i+1, j, player);
        }
        if(checkBounds(grid, i+1,j-1) && grid[j-1][i+1] == player){
            System.out.println("Went diagonal left for player " + player);
            value += DiagonalLeftCheck(grid, i+1, j-1, player);
        }
        if(checkBounds(grid, i+1,j+1) && grid[j+1][i+1] == player){
            System.out.println("Went diagonal righ for player " + player);
            value += DiagonalRightCheck(grid, i+1, j+1, player);
        }
        System.out.println("Should be a value: " + value);
        return value;
    }

    private int[][] fillMatrix(GameStateModule state){ // function to recreate a matrix based on a state
        int[][] mat = new int[state.getWidth()][state.getHeight()];
        for (int i = 0; i < state.getHeight(); i++) {
            for (int j = 0; j < state.getWidth(); j++) {
                mat[j][i] = state.getAt(j, i);
//                if(mat[j][i] == 1)
//                    System.out.print("X ");
//                else if(mat[j][i] == 2)
//                    System.out.print("O ");
//                else
//                    System.out.print(". ");
            }
//            System.out.println();

        }
//        System.out.println();
        //System.out.println();
        return mat;
    }

    private int eval(GameStateModule state){ // evaluation function with DFS on created Matrix
        if (state.isGameOver()){
            if (state.getWinner() == player)
                return 10000;
            if (state.getWinner() == opponent)
                return -10000;
        }
        int value1 = 0;
        int value2 = 0;
        int[][] newMatrix = fillMatrix(state);
        //printBoard(state);
        printMatrix(newMatrix);

        for (int i = 0; i < state.getHeight(); i++) {
            for (int j = 0; j < state.getWidth(); j++) {
                if (newMatrix[j][i] == 1){
                    value1 += findNeighbors(newMatrix, i, j, 1);
                }
                else if (newMatrix[j][i] == 2){
                    value2 += findNeighbors(newMatrix, i, j, 2);
                }
            }
        }

        System.out.println(" eval: " + value1 + " " + value2 + " " + (value1 - value2));
        return (value1 - value2);
    }

    private void printMatrix(int[][] mat) { //Take out later on
        for(int j = mat[0].length - 1; j >= 0; j--) {
            for(int i = 0; i < mat.length; i++) {
                if(mat[i][j] == 1)
                    System.out.print("X ");
                else if(mat[i][j] == 2)
                    System.out.print("O ");
                else
                    System.out.print(". ");


            }
            System.out.println();
        }
        System.out.println();
    }
}
