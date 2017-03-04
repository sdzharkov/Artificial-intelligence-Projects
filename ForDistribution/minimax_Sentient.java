import java.util.*;
import java.awt.Point;

public class minimax_Sentient extends AIModule
{
    private int maxPly = 2; //maximum number of moves to play ahead

    private int prevMoveX, prevMoveY;
    private int player;

    //Hold points value
    //We can experiment with these values
    //1 coin of ourPlayer with 3 blank spot  +x
    //2 coin of ourPlayer with 2 blank spot  +x
    //3 coin of ourPlayer with 1 blank spot  +x
    //4 coin of ourPlayer with 0 blank spot  +x
    //Contains the negative value as well
    private static final int[] points = { 0, 10, 100, 500, 100000000 };
    private int[][] pointsCoin = {
        {  3,  4,  5,  7,  7,  5,  4,  3 },
        {  4,  6,  8, 10, 10,  8,  6,  4 },
        {  5,  8, 11, 13, 13, 11,  8,  5 },
        {  7, 10, 13, 16, 16, 13, 10,  7 },
        {  7, 10, 13, 16, 16, 13, 10,  7 },
        {  5,  8, 11, 13, 13, 11,  8,  5 },
        {  4,  6,  8, 10, 10,  8,  6,  4 },
        {  3,  4,  5,  7,  7,  5,  4,  3 } };

    //generates the moves that can be performed from the current state
    //as of right now it generates them in left to right order
    //returns an array of the columns that can be played in
    //each entry contains either a column number to play in that column
    //or a -1 to indicate that the move is invalid
    private int[] generateMoves(final GameStateModule state)
    {
        int i;
        int[] moves = new int[state.getWidth()]; //maybe this should be Integer instead of int?

        for(i = 0; i < state.getWidth(); ++i)
        {
            if(state.canMakeMove(i))
            {
                moves[i] = i; //can play column i
            }
            else
            {
                moves[i] = -1; //mark move as invalid
            }
        }

        return moves;
    }//generateMoves

    //start reading here
    public void getNextMove(final GameStateModule state) //think of this as minimax or alpah beta
    {
        int[] moves = generateMoves(state);//which column to play in
        int max, colToPlay, oldMax;
        max = oldMax = Integer.MIN_VALUE;
        colToPlay = -1;
        int tmpColToPlay = -1;

        //set player
        player = state.getCoins() % 2 + 1;

        int score = initFirstStateScore( state );
        /* System.out.println( "score: " + score ); */
        /* int s2 = cS( state ); */
        /* System.out.println(); */
        /* System.out.println(score); */
        /* System.out.println(); */
        /* System.out.println(s2); */

        while( !terminate )
        {
            max = oldMax = Integer.MIN_VALUE;
            for(int move : moves)
            {
                if(move != -1)//if the move is valid try playing it
                {
                    if(terminate == true)
                    {
                        /* System.out.print( maxPly + " "); */
                        maxPly--;
                        return;
                    }

                    prevMoveX = move;
                    prevMoveY = state.getHeightAt( move );

                    state.makeMove(move);
                    max = MinVal(state, 1, score);

                    /* System.out.print( move + " " ); */
                    /* System.out.println(); */
                    /* System.out.println( "max: " + max + " old Max: " + oldMax ); */

                    if(max > oldMax)//if the new move was better than the old move that is the one we want to play
                    {
                        oldMax = max;
                        tmpColToPlay = move;
                    }//if the new move was better than the old move that is the one we want to play

                    state.unMakeMove();
                }//if the move is valid play it
            }
            maxPly++;
            colToPlay = tmpColToPlay;
            chosenMove = colToPlay; //set our move
        }
    }

    private int MaxVal(final GameStateModule state, int curPly, int parentScore)
    {
        int util = Integer.MIN_VALUE;
        int[] moves;
        if(state.isGameOver()) //terminal test of the psuedo code
        {
            int winner = state.getWinner();

            if(winner == 0)//if the winner is 0 than there was a draw and return no reinforcement
                return 1;
            else
                return Integer.MIN_VALUE + 1;
        }

        //if our time has run out we need to return a utility function
        //right now I'm not sure if it would be better to calculate the utility function each time we reach a state or
        // only if we are told to terminate
        //right now we are calculating only if told to return
        if(curPly == maxPly) //if our time has run out we need to return a utility function
        {
            util = parentScore + computeScore( state );
            return util;
        }

        moves = generateMoves(state);
        int oldPrevMoveX, oldPrevMoveY;
        int score = parentScore + computeScore( state );

        for(int move : moves)
        {
            if(terminate == true)
            {
                break;
            }//if we have run out of time


            if(move != -1)//if the move is valid try playing it
            {
                //save state
                oldPrevMoveX = prevMoveX;
                oldPrevMoveY = prevMoveY;

                prevMoveX = move;
                prevMoveY = state.getHeightAt( move );

                state.makeMove(move);

                util = Math.max(util, MinVal(state, curPly + 1, score));
                /* System.out.println(util + " " + curPly + " " + move); */
                state.unMakeMove();

                //revert back
                prevMoveX = oldPrevMoveX;
                prevMoveY = oldPrevMoveY;
            }//if the move is valid play it
        }//

        return util;
    }//MaxVal

    private int MinVal(final GameStateModule state, int curPly, int parentScore)
    {
        int util = Integer.MAX_VALUE;
        int[] moves;
        if(state.isGameOver()) //terminal test of the psuedo code
        {
            int winner = state.getWinner();

            if(winner == 0)//if the winner is 0 than there was a draw and return no reinforcement
                return 1;
            else
                return Integer.MAX_VALUE;
        }

        //if our time has run out we need to return a utility function
        //right now I'm not sure if it would be better to calculate the utility function each time we reach a state or
        // only if we are told to terminate
        //right now we are calculating only if told to return
        if(curPly == maxPly) //if our time has run out we need to return a utility function
        {
            util = parentScore + computeScore( state );
            return util;
        }

        moves = generateMoves(state);
        int oldPrevMoveX, oldPrevMoveY;
        int score = parentScore + computeScore( state );

        for(int move : moves)
        {

            if(terminate == true)
            {
                break;
            }//if we have run out of time

            if(move != -1)//if the move is valid try playing it
            {
                //save state
                oldPrevMoveX = prevMoveX;
                oldPrevMoveY = prevMoveY;

                prevMoveX = move;
                prevMoveY = state.getHeightAt( move );

                state.makeMove(move);

                util = Math.min(util, MaxVal(state, curPly + 1, score));

                state.unMakeMove();

                //revert back
                prevMoveX = oldPrevMoveX;
                prevMoveY = oldPrevMoveY;
            }//if the move is valid try playing it
        }//for each move play it

        return util;
    }//MaxVal

    /**
     * Compute the score value of a game state.
     */
    private int computeScore( final GameStateModule state )
    {
        int newScore = 0, oldScore = 0;
        int x, y;
        int i, j;
        int width = state.getWidth(), height = state.getHeight();

        /**
         * Compute new score first and then the old score. Return the difference.
         */

        //check row
        //x starts at 0 and move to right
        //y is constant
        newScore += scoreThis( 0, prevMoveY, 1, 0, state );

        //check col
        //x is constant
        //y moves from top to bottom
        newScore += scoreThis( prevMoveX, height - 1, 0, -1, state );

        //check +y diag
        //first grab start x and y value
        x = prevMoveX - prevMoveY;
        y = prevMoveY - prevMoveX;
        if( x < 0 ) x = 0;
        if( y < 0 ) y = 0;

        newScore += scoreThis( x, y, 1, 1, state );

        //check -y diag
        //grab start x annd start y
        for( x = prevMoveX, y = prevMoveY; x > 0 && y < height - 1; --x, ++y ){}

        newScore += scoreThis( x, y, 1, -1, state );

        /* int h = state.getHeightAt( prevMoveX ); */
        /* state.unMakeMove(); */
        /* //check row */
        /* //x starts at 0 and move to right */
        /* //y is constant */
        /* oldScore += scoreThis( 0, prevMoveY, 1, 0, state ); */

        /* //check col */
        /* //x is constant */
        /* //y moves from top to bottom */
        /* oldScore += scoreThis( prevMoveX, height - 1, 0, -1, state ); */

        /* //check +y diag */
        /* //first grab start x and y value */
        /* x = prevMoveX - prevMoveY; */
        /* y = prevMoveY - prevMoveX; */
        /* if( x < 0 ) x = 0; */
        /* if( y < 0 ) y = 0; */

        /* oldScore += scoreThis( x, y, 1, 1, state ); */

        /* //check -y diag */
        /* //grab start x annd start y */
        /* for( x = prevMoveX, y = prevMoveY; x > 0 && y < height - 1; --x, ++y ){} */

        /* oldScore += scoreThis( x, y, 1, -1, state ); */

        /* state.makeMove( prevMoveX ); */
        /* int h2 = state.getHeightAt( prevMoveX ); */
        /* if( h != h2 ) System.exit(1); */
        return newScore - oldScore;
    }

    private int scoreThis( int begX, int begY, int dX, int dY, final GameStateModule state )
    {
        LinkedList <Integer> deck = new LinkedList <Integer> ();
        int width = state.getWidth(), height = state.getHeight();
        int score = 0;
        int sameCoin = 0;
        int firstCoinOfSet = -1;
        int i, rind;
        int tmp, currCoin;
        int x, y;

        //cases with columns and -y diagonals
        if( dY < 0 )
        {
            for( x = begX, y = begY; x < width && y >= 0; x += dX, y += dY )
            {
                currCoin = state.getAt( x, y );

                if( currCoin == firstCoinOfSet )
                {
                    deck.addLast( currCoin );
                    sameCoin++;
                }
                else
                {
                    if( currCoin == 0 )
                        deck.addLast( currCoin );
                    else
                    {
                        if( firstCoinOfSet > 0 )
                        {
                            //Also takes care of case when list is initially empty
                            rind = deck.lastIndexOf( firstCoinOfSet ) + 1;
                            for( i = 0; i < rind; ++i )
                                deck.removeFirst();
                        }
                        deck.addLast(currCoin);
                        sameCoin = 1;
                        firstCoinOfSet = currCoin;
                    }
                }
                if( deck.size() >= 4 )
                {
                    if( firstCoinOfSet == player )
                        score += points[ sameCoin ];
                    else
                        score -= points[ sameCoin ];

                    if( deck.removeFirst() == firstCoinOfSet )
                        --sameCoin;
                }
            }
        }
        else
        {
            for( x = begX, y = begY; x < width && y < height; x += dX, y += dY )
            {
                currCoin = state.getAt( x, y );

                if( currCoin == firstCoinOfSet )
                {
                    deck.addLast( currCoin );
                    sameCoin++;
                }
                else
                {
                    if( currCoin == 0 )
                        deck.addLast( currCoin );
                    else
                    {
                        if( firstCoinOfSet > 0 )
                        {
                            //Also takes care of case when list is initially empty
                            rind = deck.lastIndexOf( firstCoinOfSet ) + 1;
                            for( i = 0; i < rind; ++i )
                                deck.removeFirst();
                        }
                        deck.addLast(currCoin);
                        sameCoin = 1;
                        firstCoinOfSet = currCoin;
                    }
                }
                if( deck.size() >= 4 )
                {
                    if( firstCoinOfSet == player )
                        score += points[ sameCoin ];
                    else
                        score -= points[ sameCoin ];

                    if( deck.removeFirst() == firstCoinOfSet )
                        --sameCoin;
                }
            }
        }

        return score;
    }

    private int initFirstStateScore( final GameStateModule state )
    {
        int width = state.getWidth(), height = state.getHeight();
        int minHeight = Integer.MAX_VALUE, maxHeight = Integer.MIN_VALUE;
        int score = 0;
        int x, y;

        //Find lowest and highest height of the blank spot
        for( int i = 0; i < width; ++i )
        {
            if( state.getHeightAt( i ) < minHeight )
                minHeight = i;
            if( state.getHeightAt( i ) > maxHeight )
                maxHeight = i;
        }

        //Generate all rows and compute score
        for( y = minHeight; y <= maxHeight; ++y )
            score += scoreThis( 0, y, 1, 0, state );

        /* System.out.println( "rows: " + score ); */

        //Generate all columns
        for( x = 0; x < width; ++x )
            score += scoreThis( x, height - 1, 0, -1, state );

        //Generate +y diagonals left half
        for( y = height - 4; y >= 0; --y )
            score += scoreThis( 0, y, 1, 1, state );

        int endX = width - 3; //cache the stop parameter

        //Generate +y diagonals right half
        for( x = 1; x < endX; ++x )
            score += scoreThis( x, 0, 1, 1, state );

        //Generate -y diagonals left half
        for( y = 3; y < height; ++y )
            score += scoreThis( 0, y, 1, -1, state );

        //Generate -y diagonals right half
        for( x = 1; x < endX; ++x )
            score += scoreThis( x, height - 1, 1, -1, state );

        return score;
    }
}//minimax_WorldEnderH4G1

