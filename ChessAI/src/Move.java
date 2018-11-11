
public class Move {

	public enum CastlingMove {
		Kingside,
		Queenside,
		None
	}
	
	public int color;
	public int source;
	public int destination;
	public Piece movingPiece;
	public Piece capturedPiece; // is also the rook on castling
	public boolean capture;
	
	public int enpassantSquare; // the enpassant square that is created
	public Piece enpassantPiece; // the piece that created the square	
	public int oldEnpassantSquare;
	public Piece oldEnpassantPiece;
	
	public CastlingMove castlingMove;
	public CastlingRights oldCastlingRights;
	public int rookSrc; // used only for zobrist hashing to fastly determine the origin square of the castling move pieces
	public int kingSrc; // ..
	
	public Piece.Type promotionType;
	
	//castling move
	public Move(int color, CastlingMove castlingMove, Piece king, Piece rook, 
			int oldEnpassantSquare, Piece oldEnpassantPiece, CastlingRights oldCastlingRights)
	{
		this.color = color;
		this.castlingMove = castlingMove;
		this.source = 128;
		this.destination = 128;
		this.movingPiece = king;
		this.capturedPiece = rook;
		this.rookSrc = rook.square;
		this.kingSrc = king.square;
		this.enpassantSquare = 128;
		this.enpassantPiece = null;
		this.oldEnpassantSquare = oldEnpassantSquare;
		this.oldEnpassantPiece = oldEnpassantPiece;
		this.oldCastlingRights = new CastlingRights(oldCastlingRights);
		this.promotionType = Piece.Type.None;
	}
	
	//"normal" move
	public Move(int color, int source, int destination, Piece movingPiece, Piece capturedPiece, 
			int oldEnpassantSquare, Piece oldEnpassantPiece, CastlingRights oldCastlingRights) {
		this.color = color;
		this.source = source;
		this.destination = destination;
		this.movingPiece = movingPiece;
		this.capturedPiece = capturedPiece;
		this.enpassantSquare = 128;
		this.enpassantPiece = null;
		this.oldEnpassantSquare = oldEnpassantSquare;
		this.oldEnpassantPiece = oldEnpassantPiece;
		this.castlingMove = CastlingMove.None;
		this.oldCastlingRights = new CastlingRights(oldCastlingRights);
		this.promotionType = Piece.Type.None;
		this.capture = capturedPiece != null;
	}
	
	//promotion move
	public Move(int color, int source, int destination, Piece movingPiece, Piece capturedPiece, 
			int oldEnpassantSquare, Piece oldEnpassantPiece, CastlingRights oldCastlingRights, Piece.Type promotionType) {
		this.color = color;
		this.source = source;
		this.destination = destination;
		this.movingPiece = movingPiece;
		this.capturedPiece = capturedPiece;
		this.enpassantSquare = 128;
		this.enpassantPiece = null;
		this.oldEnpassantSquare = oldEnpassantSquare;
		this.oldEnpassantPiece = oldEnpassantPiece;
		this.castlingMove = CastlingMove.None;
		this.oldCastlingRights = new CastlingRights(oldCastlingRights);
		this.promotionType = promotionType;
		this.capture = capturedPiece != null;
	}
	
	//move that changes the en passant field
	public Move(int color, int source, int destination, Piece movingPiece, Piece capturedPiece,
			int enpassantSquare, Piece enpassantPiece, int oldEnpassantSquare, Piece oldEnpassantPiece, CastlingRights oldCastlingRights) {
		this.color = color;
		this.source = source;
		this.destination = destination;
		this.movingPiece = movingPiece;
		this.capturedPiece = capturedPiece;
		this.enpassantSquare = enpassantSquare;
		this.enpassantPiece = enpassantPiece;
		this.oldEnpassantSquare = oldEnpassantSquare;
		this.oldEnpassantPiece = oldEnpassantPiece;
		this.castlingMove = CastlingMove.None;
		this.oldCastlingRights = new CastlingRights(oldCastlingRights);
		this.promotionType = Piece.Type.None;
		this.capture = capturedPiece != null;
	}
	
	// this returns coordinate notation, for standard notation use Board.getMoveStringInCurrentPosition(Move)
	public String toString()
	{
		if (castlingMove == CastlingMove.Kingside && color == Color.BLACK)
			return "e8g8";
		else if (castlingMove == CastlingMove.Kingside && color == Color.WHITE)
			return "e1g1";
		else if (castlingMove == CastlingMove.Queenside && color == Color.BLACK)
			return "e8c8";
		else if (castlingMove == CastlingMove.Queenside && color == Color.WHITE)
			return "e1c1";
		else if (promotionType != Piece.Type.None)
			return String.format("%1$c%2$c%3$c%4$c%5$c", getFileBySquare(source), getRankBySquare(source), getFileBySquare(destination), getRankBySquare(destination), getCharOfPiece(promotionType));
		else
			return String.format("%1$c%2$c%3$c%4$c", getFileBySquare(source), getRankBySquare(source), getFileBySquare(destination), getRankBySquare(destination));
	}
	
	public boolean equals(Object m)
	{
		if (!(m instanceof Move)) return false;
		Move move = (Move)m;
		return (color == move.color && source == move.source && destination == move.destination);
	}
	
	private char getFileBySquare(int sq)
	{
		sq = sq & 7;
		switch (sq) {
		case 0: return 'a';
		case 1: return 'b';
		case 2: return 'c';
		case 3: return 'd';
		case 4: return 'e';
		case 5: return 'f';
		case 6: return 'g';
		case 7: return 'h';
		default: return 'X';
		}
	}
	
	private char getRankBySquare(int sq)
	{
		sq = sq >> 4;
		switch (sq) {
		case 0: return '1';
		case 1: return '2';
		case 2: return '3';
		case 3: return '4';
		case 4: return '5';
		case 5: return '6';
		case 6: return '7';
		case 7: return '8';
		default: return 'X';
		}
	}

	private char getCharOfPiece(Piece.Type type)
	{
		switch (type) {
		case Queen: return 'q';
		case Rook: return 'r';
		case Knight: return 'n';
		case Bishop: return 'b';
		default: return 'x';
		}
	}
}
