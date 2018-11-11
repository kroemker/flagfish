import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Searcher {

	public static int INFINITY = 10000000;
	public static int MATE = 10000;
	
	Board board;
	Evaluator evaluator;
	int alphaBetaNodes;
	int quiesceNodes;
	MoveComparator comparator;
	TimeManager timeManager;
	
	public Searcher(Board board, Evaluator evaluator, MoveComparator comparator, TimeManager timeManager)
	{
		this.board = board;
		this.evaluator = evaluator;
		this.comparator = comparator;
		this.timeManager = timeManager;
		alphaBetaNodes = 0;
		quiesceNodes = 0;
	}
		
	public MoveInfo search(int color, int depthLimit, long timeLimit)
	{
		timeLimit = timeLimit * 1000000L;
		long start = System.nanoTime();
		MoveInfo currentBestMove = null;
		MoveInfo lastBestMove = null;
		
		int depth = 1;
		while(true)
		{
			currentBestMove = searchRoot(color, depth, timeLimit);
			
			if (depth == depthLimit) break; // if depthLimit == 0 this will never be triggered
					
			depth++;
			
			if(currentBestMove.aborted) {
				//System.out.println("Current search was aborted, returning lastBestMove: " + lastBestMove.move.toString());
				break;
			}
			lastBestMove = currentBestMove;
		
			// look if it is worth it to even try another iteration
			long remaining = timeLimit - (System.nanoTime() - start);
			if (remaining < timeManager.getLastRoundTime() * 3) //FIXME: fix the 3
			{
				//System.out.println("Stopped search, remaining time: " + (remaining/1000000) + " ms and last round time " + (timeManager.getLastRoundTime()/1000000) + " ms");
				break;
			}
		}
		
		if (currentBestMove.aborted)
			return lastBestMove;
		else
			return currentBestMove;
	}
	
	private MoveInfo searchRoot(int color, int depthLimit, long timeLimit)
	{
		timeManager.startTiming(timeLimit);
		
		float score;
		Move bestMove = null;
		
		List<Move> moves = board.generateMoves(color);
		Collections.sort(moves, comparator);
		ListIterator<Move> it = moves.listIterator();
		
		float alpha = -INFINITY;
		float beta = +INFINITY;
		
		while(it.hasNext())
		{
			Move m = it.next();
	
			board.makeMove(m);
			
			// dont consider invalid moves
			if (board.inCheck(color))
			{
				board.unmakeMove(m);
				continue;
			}

			/*
			System.out.println("ROOT");
			board.print(System.out);
			System.out.println(moves.toString());
			System.out.println(m.toString());
			System.out.println();
			*/
			
			score = -alphaBeta(Color.invert(color), -beta, -alpha, depthLimit-1);
			
			board.unmakeMove(m);

			if (timeManager.timeUp())
				return new MoveInfo(); //return aborted move
			
			if(score > alpha)
			{
				bestMove = m;
				alpha = score;
			}
		}
		
		timeManager.endTiming();
		
		return new MoveInfo(bestMove, alpha, alphaBetaNodes, quiesceNodes, depthLimit, timeManager.getLastRoundTime());
	}
	
	
	
	private float alphaBeta(int color, float alpha, float beta, int depth)
	{
		if (depth == 0) 
		{
			return quiesce(color, alpha, beta);
		}
		
		alphaBetaNodes++;
		
		if(!board.sufficientMaterial())
			return 0;
		
		boolean hasLegalMove = false;
		float score = 0;
		List<Move> moves = board.generateMoves(color);
		
		Collections.sort(moves, comparator);
		ListIterator<Move> it = moves.listIterator();
		
		while(it.hasNext())
		{
			Move m = it.next();
			
			int hash = board.hash();			
			board.makeMove(m);

			// dont consider invalid moves
			if (board.inCheck(color))
			{
				board.unmakeMove(m);
				continue;
			}
			hasLegalMove = true;
			/*
			System.out.println("AB-NODE");
			board.print(System.out);
			System.out.println(moves.toString());
			System.out.println(m.toString());
			System.out.println();
			*/
			score = -alphaBeta(Color.invert(color), -beta, -alpha, depth-1);
			
			board.unmakeMove(m);
						
			if (hash != board.hash())
			{
				board.print(System.err);
				System.out.println(m.toString() + " with hash: " + board.hash());
				System.out.println();
				System.out.flush();
				assert false;
			}
			
			// check timeout
			if (timeManager.timeUp())
				return 0;
			
			if(score >= beta)
				return beta;
			if(score > alpha)
				alpha = score;
		}
		if (!hasLegalMove)
		{
			if (board.inCheck(color))
				return -MATE; //mate
			else
				return 0; //stalemate
		}
		
		return alpha;
	}
	
	private float quiesce(int color, float alpha, float beta)
	{
		quiesceNodes++;
		float standPattern = evaluator.evaluate(color);
		if (standPattern >= beta)
			return beta;
		if (alpha < standPattern)
			alpha = standPattern;
		
		float score;
		List<Move> moves = board.generateCaptures(color);
		Collections.sort(moves, comparator);
		ListIterator<Move> it = moves.listIterator();
		while(it.hasNext())
		{
			Move m = it.next();
			
			// if we capture a king its very good for "color"
			if(m.capturedPiece.type == Piece.Type.King)
				return MATE;
			
			int hash = board.hash();

			board.makeMove(m);

			// dont consider invalid moves
			if (board.inCheck(color))
			{
				board.unmakeMove(m);
				continue;
			}
			/*
			System.out.println("QUIESCE-NODE");
			board.print(System.out);
			System.out.println(moves.toString());
			System.out.println(m.toString());
			System.out.println();
			*/
			score = -quiesce(Color.invert(color), -beta, -alpha);
			
			board.unmakeMove(m);
			
			if (hash != board.hash())
			{
				board.print(System.out);
				System.out.println(m.toString() + " hash: " + board.hash());
				System.out.println();
				System.out.flush();
				assert false;
			}
			
			// check timeout
			if (timeManager.timeUp())
				return 0;
			
			if (score >= beta)
				return beta;
			if (score > alpha)
				alpha = score;
		}
		return alpha;
	}
}
