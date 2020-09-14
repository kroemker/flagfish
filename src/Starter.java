import java.util.Scanner;

public class Starter {

	public static void main(String[] args)
	{
		Board board = new Board();
		Evaluator evaluator = new Evaluator(board);
		TimeManager timeManager = new TimeManager(10000000); // 10 ms time offset
		Searcher searcher = new Searcher(board, evaluator, new MoveComparator(), timeManager);
		Logger logger = new Logger(board, true);
		Engine engine = new Engine(board, searcher, logger);
		Communicator communicator = new Communicator(engine);
		
		boolean normalRun = true;
		
		if (normalRun)
			try {
				communicator.communicate();
			} catch (Exception e) {
			}
		else
		{
			board.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
			board.print(System.out);
			System.out.println(board.inCheck(Color.BLACK));
			
			NeuralNetwork n = new NeuralNetwork(10, 5, 1, 5);
			n.initializeNetwork();
			float[] input = {1.0f,0.0f,1.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f};
			float[] output =  n.calculate(input);
		
			for(int i = 0; i < output.length; i++)
				System.out.print(output[i] + " ");
			
			//board.loadStartPosition();
			//board.loadFEN("rnbqkbnr/pp3Qpp/2p5/8/3P4/3pP3/PPP2PPP/RNB1K1NR w KQkq -");
			
			//board.print(System.out);
			//System.out.println(board.generateMoves(Color.BLACK).toString());
			
			//int counter = 1;
			//int color = Color.WHITE;
	
			/*
			while(counter < 60 && !board.isMate(color))
			{
				long t = System.nanoTime();
				MoveInfo mi = searcher.search(color, 0, 4); // 4 second time limit
				t = System.nanoTime() - t;
	
				System.out.println(counter + ". " + board.getMoveStringInCurrentPosition(mi.move) + ": " + mi.score + " , " + (t/1000000) + " ms; " + mi.allNodes + " / " + mi.depth);
	
				logger.printMoveInfo(mi.move);
				board.makeMove(mi.move);
				
				counter++;
				
				color = Color.invert(color);
			}
			
			if (counter < 60)
				System.out.println(Color.toString(Color.invert(color)) + " won!");
			else
				System.out.println("30 move limit reached!");
	
			Move m;
			Scanner sc = new Scanner(System.in);
			
			MoveInfo mi = searcher.search(Color.invert(color), 0, 5);
			System.out.println(counter + ". " + board.getMoveStringInCurrentPosition(mi.move) + " with score: " + mi.score);
			logger.printMoveInfo(mi.move);
			board.makeMove(mi.move);
			
			while((m = getMoveInput(sc, color, board)) != null)
			{
				logger.printMoveInfo(m);
				board.makeMove(m);
				
				counter++;
				
				long t = System.nanoTime();
				mi = searcher.search(Color.invert(color), 0, 5);
				t = System.nanoTime() - t;
				
				System.out.println(counter + ". " + board.getMoveStringInCurrentPosition(mi.move) + ": " + mi.score + " , " + (t/1000000) + " ms; " + mi.allNodes + " / " + mi.depth);
	
				logger.printMoveInfo(mi.move);
				board.makeMove(mi.move);
				
				//board.print();
			}
			*/
		}
	}
	
	public static Move getMoveInput(Scanner sc, int color, Board board)
	{
		String input = sc.nextLine();
		
		if (input.compareTo("O-O-O") == 0) 
			return new Move(color, Move.CastlingMove.Queenside, board.getPiece(getKing(color)), board.getPiece(getRook(color,false)),
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(color));
		if (input.compareTo("O-O") == 0) 
			return new Move(color, Move.CastlingMove.Kingside, board.getPiece(getKing(color)), board.getPiece(getRook(color,true)),
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(color));
		if (input.compareTo("gg") == 0)
			return null;
		
		int k = getFileByChar(input.charAt(0));
		int q = Integer.parseInt(String.valueOf(input.charAt(1))) - 1;
		int src = k + q * 16;
		int dest = getFileByChar(input.charAt(2)) + (Integer.valueOf(String.valueOf(input.charAt(3)))-1) * 16;
		
		return new Move(color, src, dest, board.getPiece(src), board.getPiece(dest), board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(color));
	}
	
	public static int getFileByChar(char c)
	{
		switch (c)
		{
		case 'a': return 0;
		case 'b': return 1;
		case 'c': return 2;
		case 'd': return 3;
		case 'e': return 4;
		case 'f': return 5;
		case 'g': return 6;
		case 'h': return 7;
		}
		return 0;
	}
	
	public static int getKing(int color)
	{
		if (color == Color.WHITE)
			return 4;
		else
			return 116;
	}
	
	public static int getRook(int color, boolean kingside)
	{
		if (color == Color.WHITE)
			if (kingside)
				return 7;
			else
				return 0;
		else
			if (kingside)
				return 119;
			else
				return 112;
	}
	
}
