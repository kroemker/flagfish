
public class Engine {

	public static final String engineName = "Flagfish";
	
	Logger logger;
	Searcher searcher;
	Board board;
	ClockManager clockManager;
	
	// this 
	private int color; // white or black
	
	private int depthLimit;
	private int[] clock; // ms
	
	public Engine(Board b, Searcher s, Logger l)
	{
		board = b;
		searcher = s;
		logger = l;
		clockManager = new ClockManager(b);
		depthLimit = 0;
		clock = new int[2];
		clock[0] = 60000;
		clock[1] = 60000;
		color = Color.WHITE;
	}
	
	public MoveInfo move()
	{
		//System.out.println("Remaining seconds: " + clockManager.remainingTimeInMS(color)/1000);
		MoveInfo mi = searcher.search(board.colorToMove, depthLimit, clockManager.remainingTimeInMS(color)/40);
		logger.updateMoved(mi);
		board.makeMove(mi.move);
		clockManager.updateMove();
		return mi;
	}
	
	public MoveInfo UCIMove() {
		return null;
	}

	public void startNewGame()
	{
		board.loadStartPosition();
	}
	
	public int getColor() 
	{
		return board.getColorToMove();
	}

	public void setColor(int color) 
	{
		this.color = color;
	}

	public int getDepthLimit() 
	{
		return depthLimit;
	}

	public void setDepthLimit(int depthLimit) 
	{
		this.depthLimit = depthLimit;
	}
	
	public void setClockTime(int color, int time)
	{
		clock[color] = time;
	}

	public int getClockTime(int color)
	{
		return clock[color];
	}
	
	public void setBoard(String FEN)
	{
		board.loadFEN(FEN);
	}
	
	public void setOpponentName(String name)
	{
		logger.setOpponentName(name);
	}
	
	public void setRatings(int aiRating, int opponentRating)
	{
		logger.setRatings(aiRating, opponentRating);
	}
	
	public void setResult(String result, String comment)
	{
		logger.setResult(result, comment);
	}
	
	public ClockManager getClockManager()
	{
		return clockManager;
	}
	
	public void undo()
	{
		board.unmakeMove(logger.getLastMove());
		logger.removeLastMove();
		color = Color.invert(color);
	}
	
	public int checkGameOver()
	{
		if (board.isMate(Color.WHITE))
		{
			logger.generatePGN();
			logger.reset();
			return Color.WHITE;
		}
		else if (board.isMate(Color.BLACK))
		{
			logger.generatePGN();
			logger.reset();
			return Color.BLACK;
		}
		return Color.NONE;
	}
	
	public void logCommand(String command)
	{
		logger.logCommand(command);
	}
	
	public boolean engineToMove()
	{
		return color == board.getColorToMove();
	}
	
	public String inputMove(String input)
	{
		Move m;
		
		if (input.length() < 4 || !Character.isLetter(input.charAt(0))|| !Character.isLetter(input.charAt(2)) || !Character.isDigit(input.charAt(1)) || !Character.isDigit(input.charAt(3)))
		{
			return ("Error (illegal move format): " + input);
		}
		
		int k = getFileByChar(input.charAt(0));
		int q = Integer.parseInt(String.valueOf(input.charAt(1))) - 1;
		int src = k + q * 16;
		if (k == 8 || q < 0 || q > 7)
		{
			return ("Error (illegal move format): " + input);
		}
		
		k = getFileByChar(input.charAt(2));
		q = Integer.parseInt(String.valueOf(input.charAt(3))) - 1;
		int dest = k + q * 16;
		if (k == 8 || q < 0 || q > 7)		
		{
			return ("Error (illegal move format): " + input);
		}
		
		Piece.Type promoType = Piece.Type.None;
		if (input.length() == 5)
			promoType = getPieceTypeFromChar(input.charAt(4));

		int colorToPlay = board.getColorToMove();
		Piece king = board.getPiece(getKing(colorToPlay));
		
		if (input.equals("e1g1") && king != null && king.type == Piece.Type.King)
			m = new Move(colorToPlay, Move.CastlingMove.Kingside, board.getPiece(getKing(colorToPlay)), board.getPiece(getRook(colorToPlay,true)), 
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		else if(input.equals("e8g8") && king != null && king.type == Piece.Type.King)
			m = new Move(colorToPlay, Move.CastlingMove.Kingside, board.getPiece(getKing(colorToPlay)), board.getPiece(getRook(colorToPlay,true)),  
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		else if(input.equals("e1c1") && king != null && king.type == Piece.Type.King)
			m = new Move(colorToPlay, Move.CastlingMove.Queenside, board.getPiece(getKing(colorToPlay)), board.getPiece(getRook(colorToPlay,false)), 
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		else if(input.equals("e8c8") && king != null && king.type == Piece.Type.King)
			m = new Move(colorToPlay, Move.CastlingMove.Queenside, board.getPiece(getKing(colorToPlay)), board.getPiece(getRook(colorToPlay,false)),  
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		else if (promoType != Piece.Type.None)
			m = new Move(colorToPlay, src, dest, board.getPiece(src), board.getPiece(dest),  
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay), promoType);
		else if (board.getPiece(src).type == Piece.Type.Pawn && dest == board.getEnpassantSquare())
			m = new Move(colorToPlay, src, dest, board.getPiece(src), board.getEnpassantPiece(), 
					board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		else
			m = new Move(colorToPlay, src, dest, board.getPiece(src), board.getPiece(dest), 
				board.getEnpassantSquare(), board.getEnpassantPiece(), board.getCastlingRights(colorToPlay));
		
		if(!board.isLegalMove(m))
		{
			return ("Illegal move: " + input);
		}

		logger.updateMoved(new MoveInfo(m, 0, 0, 0, 0, 0));
		board.makeMove(m);
		clockManager.updateMove();
		return null;
	}

	
	private int getFileByChar(char c)
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
		return 8;
	}
	
	private int getKing(int color)
	{
		if (color == Color.WHITE)
			return 4;
		else
			return 116;
	}
	
	private int getRook(int color, boolean kingside)
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
	
	private Piece.Type getPieceTypeFromChar(char c)
	{
		switch(c)
		{
		case 'q':
			return Piece.Type.Queen;
		case 'r':
			return Piece.Type.Rook;
		case 'n':
			return Piece.Type.Knight;
		case 'b':
			return Piece.Type.Bishop;
		default:
			return Piece.Type.None;
		}
	}

}
