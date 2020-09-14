import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Board {

	//piece lists
	LinkedList<Piece> whitePieces;
	LinkedList<Piece> blackPieces;
	
	ArrayList<LinkedList<Piece>> pieceListHolder;
	
	//0x88 board
	Piece[] board;
	
	int enpassantSquare;
	Piece enpassantPiece;
		
	CastlingRights whiteCastlingRights;
	CastlingRights blackCastlingRights;
	
	AttackMap whiteAttackMap;
	AttackMap blackAttackMap;
	
	Piece[] kings;
	CastlingRights[] castlingRightsHolder = {whiteCastlingRights, blackCastlingRights};
	AttackMap[] attackMapHolder = {whiteAttackMap, blackAttackMap};
	
	int colorToMove;
	
	public Board() {
		whitePieces = new LinkedList<Piece>();
		blackPieces = new LinkedList<Piece>();
		
		pieceListHolder = new ArrayList<LinkedList<Piece>>();
		pieceListHolder.add(whitePieces);
		pieceListHolder.add(blackPieces);
		
		board = new Piece[128];
		for(int i = 0; i < 128; i++)
			board[i] = null;
		
		kings = new Piece[2];
		
		enpassantSquare = 128;
		enpassantPiece = null;
		
		whiteAttackMap = new AttackMap();
		blackAttackMap = new AttackMap();
		attackMapHolder[0] = whiteAttackMap;
		attackMapHolder[1] = blackAttackMap;
		
		whiteCastlingRights = new CastlingRights();
		blackCastlingRights = new CastlingRights();
		castlingRightsHolder[0] = whiteCastlingRights;
		castlingRightsHolder[1] = blackCastlingRights;
		
		colorToMove = Color.WHITE;
	}
	
	public void loadStartPosition() {
		whitePieces.clear();
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Rook, 0, false, true));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Knight, 1));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Bishop, 2));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Queen, 3));
		
		Piece whiteKing = new Piece(Color.WHITE, Piece.Type.King, 4);
		kings[0] = whiteKing;
		whitePieces.add(whiteKing);
		
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Bishop, 5));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Knight, 6));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Rook, 7, true, false));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 16));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 17));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 18));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 19));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 20));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 21));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 22));
		whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, 23));

		blackPieces.clear();
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Rook, 112, false, true));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Knight, 113));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Bishop, 114));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Queen, 115));
		
		Piece blackKing = new Piece(Color.BLACK, Piece.Type.King, 116);
		kings[1] = blackKing;
		blackPieces.add(blackKing);
		
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Bishop, 117));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Knight, 118));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Rook, 119, true, false));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 96));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 97));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 98));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 99));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 100));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 101));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 102));
		blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, 103));
		
		refillBoardByPieceList();
		
		enpassantSquare = 128;
		enpassantPiece = null;
		
		whiteAttackMap = new AttackMap();
		blackAttackMap = new AttackMap();
		attackMapHolder[0] = whiteAttackMap;
		attackMapHolder[1] = blackAttackMap;
		
		whiteCastlingRights = new CastlingRights();
		blackCastlingRights = new CastlingRights();
		castlingRightsHolder[0] = whiteCastlingRights;
		castlingRightsHolder[1] = blackCastlingRights;
		
		colorToMove = Color.WHITE;
	}
	
	public void loadFEN(String fen)
	{
		int len = fen.length();
		int rank = 7;
		int file = 0;
		boolean piece = false;

		whitePieces.clear();
		blackPieces.clear();

		int i = 0;
		for(i = 0; i < len; i++)
		{
			char c = fen.charAt(i);
			if (Character.isDigit(c)) 
			{
				file += Integer.parseInt(String.valueOf(c));
			}
			else
			{	
				piece = false;
				switch (c)
				{
				// white pieces
				case 'R':
					if (rank*16+file == 0)
						whitePieces.add(new Piece(Color.WHITE, Piece.Type.Rook, rank*16+file, false, true));
					else if (rank*16+file == 7)
						whitePieces.add(new Piece(Color.WHITE, Piece.Type.Rook, rank*16+file, true, false));
					
					piece = true;
					break;
				case 'P':
					whitePieces.add(new Piece(Color.WHITE, Piece.Type.Pawn, rank*16+file));
					piece = true;
					break;
				case 'N':
					whitePieces.add(new Piece(Color.WHITE, Piece.Type.Knight, rank*16+file));
					piece = true;
					break;
				case 'K':
					Piece whiteKing = new Piece(Color.WHITE, Piece.Type.King, rank*16+file);
					kings[0] = whiteKing;
					whitePieces.add(whiteKing);
					piece = true;
					break;
				case 'Q':
					whitePieces.add(new Piece(Color.WHITE, Piece.Type.Queen, rank*16+file));
					piece = true;
					break;
				case 'B':
					whitePieces.add(new Piece(Color.WHITE, Piece.Type.Bishop, rank*16+file));
					piece = true;
					break;
				
				// black pieces
				case 'r':
					if (rank*16+file == 112)
						blackPieces.add(new Piece(Color.BLACK, Piece.Type.Rook, rank*16+file, false, true));
					else if (rank*16+file == 119)
						blackPieces.add(new Piece(Color.BLACK, Piece.Type.Rook, rank*16+file, true, false));
					
					piece = true;
					break;
				case 'p':
					blackPieces.add(new Piece(Color.BLACK, Piece.Type.Pawn, rank*16+file));
					piece = true;
					break;
				case 'n':
					blackPieces.add(new Piece(Color.BLACK, Piece.Type.Knight, rank*16+file));
					piece = true;
					break;
				case 'k':
					Piece blackKing = new Piece(Color.BLACK, Piece.Type.King, rank*16+file);
					kings[1] = blackKing;
					blackPieces.add(blackKing);
					piece = true;
					break;
				case 'q':
					blackPieces.add(new Piece(Color.BLACK, Piece.Type.Queen, rank*16+file));
					piece = true;
					break;
				case 'b':
					blackPieces.add(new Piece(Color.BLACK, Piece.Type.Bishop, rank*16+file));
					piece = true;
					break;
				}
				if (piece)
					file++;
			}
			//new rank
			if (c == '/')
			{
				rank--;
				file = 0;
			}
			if(c == ' ')
				break;
		}
		
		i++;
		if (i < len)
		{
			if (fen.charAt(i) == 'w') colorToMove = Color.WHITE;
			else if(fen.charAt(i) == 'b') colorToMove = Color.BLACK;
		}
		i++;
		i++;
		while ((i < len) && (fen.charAt(i) != ' '))
		{
			if(fen.charAt(i) == 'K') whiteCastlingRights.canKingside = true;
			if(fen.charAt(i) == 'Q') whiteCastlingRights.canQueenside = true;
			if(fen.charAt(i) == 'k') blackCastlingRights.canKingside = true;
			if(fen.charAt(i) == 'q') blackCastlingRights.canQueenside = true;
			i++;
		}
		i++;

		int enpassantFile = -1;
		int enpassantRank = -1;
		
		switch (fen.charAt(i))
		{
		case 'a': enpassantFile = 0; break;
		case 'b': enpassantFile = 1; break;
		case 'c': enpassantFile = 2; break;
		case 'd': enpassantFile = 3; break;
		case 'e': enpassantFile = 4; break;
		case 'f': enpassantFile = 5; break;
		case 'g': enpassantFile = 6; break;
		case 'h': enpassantFile = 7; break;
		case '-': break;
		}
		i++;
		i++;
		
		if (enpassantFile >= 0)
		{
			enpassantRank = (int) fen.charAt(i);
		}
		
		refillBoardByPieceList();
		
		if (enpassantFile >= 0)
		{
			enpassantSquare = enpassantFile + enpassantRank * 16;
			if (enpassantRank == 3)
				enpassantPiece = board[enpassantFile + enpassantRank * 16 + 16];
			else	
				enpassantPiece = board[enpassantFile + enpassantRank * 16 - 16];
		}
	}
	
	//slow, dont use in move generation
	void refillBoardByPieceList() {
		for(int i = 0; i < 128; i++)
		{
			board[i] = null;
			ListIterator<Piece> it = whitePieces.listIterator();
			while (it.hasNext())
			{
				Piece n = it.next();
				if (n.square == i)
					board[i] = n;
			}
			it = blackPieces.listIterator();
			while (it.hasNext())
			{
				Piece n = it.next();
				if (n.square == i)
					board[i] = n;
			}
		}
	}
	
	public int getColorToMove()
	{
		return colorToMove;
	}
	
	public void setColorToMove(int c)
	{
		colorToMove = c;
	}
	
	public Piece getPiece(int pos) {
		return board[pos];
	}
	
	public Piece getKing(int color) {
		return kings[color];
	}
	
	public int getEnpassantSquare() {
		return enpassantSquare;
	}
	
	public Piece getEnpassantPiece() {
		return enpassantPiece;
	}
	
	public CastlingRights getCastlingRights(int color) {
		return castlingRightsHolder[color];
	}
	
	public CastlingRights getOtherCastlingRights(int color) {
		return castlingRightsHolder[Color.invert(color)];
	}
	
	public LinkedList<Piece> getPieceList(int color) {
		return pieceListHolder.get(color);
	}
	
	public LinkedList<Piece> getOtherPieceList(int color) {
		return pieceListHolder.get(Color.invert(color));
	}
	
	public AttackMap getAttackMap(int color) {
		return attackMapHolder[color];
	}
	
	public boolean isMate(int color) {
		if(!inCheck(color)) return false;
		
		List<Move> moves = generateMoves(color);
		ListIterator<Move> it = moves.listIterator();
		
		while(it.hasNext())
		{
			Move m = it.next();
			makeMove(m);
			
			if(!inCheck(color))
			{
				unmakeMove(m);
				return false;
			}
			unmakeMove(m);
		}
		return true;
	}
	
	public boolean inCheck(int color) {
		return attacks(getKing(color).square, Color.invert(color));
	}
	
	public boolean sufficientMaterial() {
		int bknights = 0, bbishops = 0;
		int wknights = 0, wbishops = 0;
		for(Piece p : whitePieces)
			if(p.type == Piece.Type.Pawn || p.type == Piece.Type.Rook || p.type == Piece.Type.Queen)
				return true;
			else if(p.type == Piece.Type.Bishop)
				wbishops++;
			else if(p.type == Piece.Type.Knight)
				wknights++;
		for(Piece p : blackPieces)
			if(p.type == Piece.Type.Pawn || p.type == Piece.Type.Rook || p.type == Piece.Type.Queen)
				return true;
			else if(p.type == Piece.Type.Bishop)
				bbishops++;
			else if(p.type == Piece.Type.Knight)
				bknights++;
		if(wbishops > 1 || bbishops > 1 || (wbishops > 0 && wknights > 0) || (bbishops > 0 && bknights > 0))
			return true;
		return false;
	}
	
	public boolean isLegalMove(Move m) {
		List<Move> moves = generateMoves(m.color);
		return moves.contains(m);
	}
	
	public boolean attacks(int square, int color) {
		LinkedList<Piece> pieces = getPieceList(color);
		ListIterator<Piece> it = pieces.listIterator();
		
		while (it.hasNext())
		{
			Piece current = it.next();
			
			for(int j = 0; j < current.vectorMoves.length; j++)
			{
				int dest = current.square;
				do {
					dest += current.vectorMoves[j];
						
					// make sure we don't move out of the board
					if ((dest & 0x88) != 0) break;
					
					// special rules for pawns
					if (current.type == Piece.Type.Pawn)
					{			
						//capture move
						dest = current.square + current.vectorMoves[j] - 1; //left for white, right for black
						if (dest == square)
							return true;
						
						dest = current.square + current.vectorMoves[j] + 1; //right for white, left for black
						if (dest == square)
							return true;
						
						break;
					}
					
					// if dest is our target square we can return true
					if (dest == square) return true;
					
				} while (current.sliding && (board[dest] == null));	
			}
		}
		return false;
	}
	
	public List<Move> generateCaptures(int color) {
		List<Move> moves = new ArrayList<Move>();
		
		CastlingRights cr = getCastlingRights(color);
		
		AttackMap am = getAttackMap(color);
		am.clearMap();
		
		LinkedList<Piece> pieces = getPieceList(color);
		ListIterator<Piece> it = pieces.listIterator();
		
		while (it.hasNext())
		{
			Piece current = it.next();
			
			for(int j = 0; j < current.vectorMoves.length; j++)
			{
				int dest = current.square;
				do {
					dest += current.vectorMoves[j];

					// make sure we don't move out of the board
					if ((dest & 0x88) != 0) break;
										
					// special rules for pawns
					if (current.type == Piece.Type.Pawn)
					{			
						//capture move
						dest = current.square + current.vectorMoves[j] - 1; //left for white, right for black
						if ((dest & 0x88) == 0) {
							
							am.setAttacked(dest);
							if ((board[dest] != null) && (board[dest].color != color))
							{
								//check promotion
								if (dest >> 4 == 0 || dest >> 4 == 7)
								{
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Queen));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Knight));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Rook));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Bishop));
								}
								else
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
							}
							else if (dest == enpassantSquare)
								moves.add(new Move(color, current.square, dest, current, enpassantPiece, enpassantSquare, enpassantPiece, cr));
						}
						
						dest = current.square + current.vectorMoves[j] + 1; //right for white, left for black
						if ((dest & 0x88) == 0) {
							
							am.setAttacked(dest);
							if ((board[dest] != null) && (board[dest].color != color))
							{
								//check promotion
								if (dest >> 4 == 0 || dest >> 4 == 7)
								{
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Queen));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Knight));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Rook));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Bishop));
								}
								else
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
							}
							else if (dest == enpassantSquare)
								moves.add(new Move(color, current.square, dest, current, enpassantPiece, enpassantSquare, enpassantPiece, cr));
						}
						break;
					}
					
					am.setAttacked(dest);
					
					// look if capture a piece
					if ((board[dest] != null) && (board[dest].color != color))
						moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
					
				} while (current.sliding && (board[dest] == null));	
			}
		}
		return moves;
	}
	
	public List<Move> generateMoves(int color) {
		List<Move> moves = new ArrayList<Move>();
		
		CastlingRights cr = getCastlingRights(color);
		
		AttackMap am = getAttackMap(color);
		am.clearMap();
		
		boolean incheck = inCheck(color);
		
		LinkedList<Piece> pieces = getPieceList(color);
		ListIterator<Piece> it = pieces.listIterator();
		
		while (it.hasNext())
		{
			Piece current = it.next();
			// castling
			if (current.type == Piece.Type.King && !incheck)
			{
				int dest = current.square + 2;
				if ((cr.canKingside) && (board[dest-1] == null) 
						&& (board[dest] == null) 
						&& !attacks(dest, Color.invert(color)) && !attacks(dest-1, Color.invert(color)))
				{
					moves.add(new Move(color, Move.CastlingMove.Kingside, current, board[dest+1], enpassantSquare, enpassantPiece, cr));
				}
				dest = current.square - 2;
				if ((cr.canQueenside) && (board[dest-1] == null) 
						&& (board[dest] == null) && (board[dest+1] == null) 
						&& !attacks(dest, Color.invert(color)) && !attacks(dest+1, Color.invert(color)))
				{
					moves.add(new Move(color, Move.CastlingMove.Queenside, current, board[dest-2], enpassantSquare, enpassantPiece, cr));
				}
			}
			
			
			for(int j = 0; j < current.vectorMoves.length; j++)
			{
				int dest = current.square;
				do {
					dest += current.vectorMoves[j];
				
					// make sure we don't move out of the board
					if ((dest & 0x88) != 0) break;
					
					// special rules for pawns
					if (current.type == Piece.Type.Pawn)
					{
						//single move
						if (board[dest] == null)
						{
							//check promotion
							if (dest >> 4 == 0 || dest >> 4 == 7)
							{
								moves.add(new Move(color, current.square, dest, current, null, enpassantSquare, enpassantPiece, cr, Piece.Type.Queen));
								moves.add(new Move(color, current.square, dest, current, null, enpassantSquare, enpassantPiece, cr, Piece.Type.Knight));
								moves.add(new Move(color, current.square, dest, current, null, enpassantSquare, enpassantPiece, cr, Piece.Type.Rook));
								moves.add(new Move(color, current.square, dest, current, null, enpassantSquare, enpassantPiece, cr, Piece.Type.Bishop));
							}
							else
								moves.add(new Move(color, current.square, dest, current, null, enpassantSquare, enpassantPiece, cr));
						
							//double move
							if ((((current.square >> 4) == 1) && (color == Color.WHITE)) || 
								(((current.square >> 4) == 6) && (color == Color.BLACK)))
							{
								dest += current.vectorMoves[j];
								if ((dest & 0x88) == 0 && board[dest] == null)
									moves.add(new Move(color, current.square, dest, current, null, 
											dest-current.vectorMoves[j], current, enpassantSquare, enpassantPiece, cr));
							}
						}
						
						//capture move
						dest = current.square + current.vectorMoves[j] - 1; //left for white, right for black
						if ((dest & 0x88) == 0) {
							am.setAttacked(dest);
														
							if ((board[dest] != null) && (board[dest].color != color))
							{
								//check promotion
								if (dest >> 4 == 0 || dest >> 4 == 7)
								{
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Queen));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Knight));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Rook));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Bishop));
								}
								else
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
							}
							else if (dest == enpassantSquare && enpassantPiece.color != color)
							{
								moves.add(new Move(color, current.square, dest, current, enpassantPiece, enpassantSquare, enpassantPiece, cr));
							}
						}
						
						dest = current.square + current.vectorMoves[j] + 1; //right for white, left for black
						if ((dest & 0x88) == 0) {							
							am.setAttacked(dest);
							
							if ((board[dest] != null) && (board[dest].color != color))
							{
								//check promotion
								if (dest >> 4 == 0 || dest >> 4 == 7)
								{
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Queen));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Knight));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Rook));
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr, Piece.Type.Bishop));
								}
								else
									moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
							}
							else if (dest == enpassantSquare && enpassantPiece.color != color)
							{
								moves.add(new Move(color, current.square, dest, current, enpassantPiece, enpassantSquare, enpassantPiece, cr));
							}
						}
						break;
					}
					
					am.setAttacked(dest);
					// look if we are about to capture our own piece
					if ((board[dest] != null) && (board[dest].color == color)) break;
					
					moves.add(new Move(color, current.square, dest, current, board[dest], enpassantSquare, enpassantPiece, cr));
					
				} while (current.sliding && (board[dest] == null));	
			}
		}  
		return moves;
	}
	
	public void makeMove(Move move) {		
		//update en passant
		enpassantSquare = move.enpassantSquare;
		enpassantPiece = move.enpassantPiece;
		
		//update castling and return!
		if (move.castlingMove == Move.CastlingMove.Kingside)
		{
			board[move.movingPiece.square] = null;
			board[move.capturedPiece.square] = null;
			move.movingPiece.square += 2;
			move.capturedPiece.square -= 2;
			board[move.movingPiece.square] = move.movingPiece;
			board[move.capturedPiece.square] = move.capturedPiece;
			getCastlingRights(move.color).unsetAll();
			colorToMove = Color.invert(colorToMove);
			return;
		}
		else if (move.castlingMove == Move.CastlingMove.Queenside)
		{
			board[move.movingPiece.square] = null;
			board[move.capturedPiece.square] = null;
			move.movingPiece.square -= 2;
			move.capturedPiece.square += 3;
			board[move.movingPiece.square] = move.movingPiece;
			board[move.capturedPiece.square] = move.capturedPiece;
			getCastlingRights(move.color).unsetAll();
			colorToMove = Color.invert(colorToMove);
			return;
		}
		
		//update piece list 
		if (move.capturedPiece != null)
		{
			getOtherPieceList(move.color).remove(move.capturedPiece);
			board[move.capturedPiece.square] = null; //needed for enpassant capture
			
			// in case we captured a rook, change other(!) side's castling rights
			if (move.capturedPiece.isKingsideRook)
				getOtherCastlingRights(move.color).canKingside = false;
			else if (move.capturedPiece.isQueensideRook)
				getOtherCastlingRights(move.color).canQueenside = false;
		}
		
		move.movingPiece.square = move.destination;
		
		//check promotion
		if(move.promotionType != Piece.Type.None)
		{
			move.movingPiece.type = move.promotionType;
			move.movingPiece.sliding = Piece.isSliding(move.promotionType);
			move.movingPiece.loadVectorTable();
		}
			
		//unset castling rights
		if (move.movingPiece.type == Piece.Type.King)
			getCastlingRights(move.color).unsetAll();
		else if (move.movingPiece.isKingsideRook)
			getCastlingRights(move.color).canKingside = false;
		else if (move.movingPiece.isQueensideRook)
			getCastlingRights(move.color).canQueenside = false;
		
		//update board
		board[move.destination] = move.movingPiece;
		board[move.source] = null;
		
		colorToMove = Color.invert(colorToMove);
	}
	
	public void unmakeMove(Move move) {
		//en passant
		enpassantSquare = move.oldEnpassantSquare;
		enpassantPiece = move.oldEnpassantPiece;
		
		//castling rights
		getCastlingRights(move.color).copy(move.oldCastlingRights);
		
		//update castling
		if (move.castlingMove == Move.CastlingMove.Kingside)
		{
			board[move.movingPiece.square] = null;
			board[move.capturedPiece.square] = null;
			move.movingPiece.square -= 2;
			move.capturedPiece.square += 2;
			board[move.movingPiece.square] = move.movingPiece;
			board[move.capturedPiece.square] = move.capturedPiece;
			colorToMove = Color.invert(colorToMove);
			return;
		}
		else if (move.castlingMove == Move.CastlingMove.Queenside)
		{
			board[move.movingPiece.square] = null;
			board[move.capturedPiece.square] = null;
			move.movingPiece.square += 2;
			move.capturedPiece.square -= 3;
			board[move.movingPiece.square] = move.movingPiece;
			board[move.capturedPiece.square] = move.capturedPiece;
			colorToMove = Color.invert(colorToMove);
			return;
		}
		

		board[move.destination] = null; // needed for enpassant capture
		//update piece list
		if (move.capturedPiece != null)
		{
			getOtherPieceList(move.color).add(move.capturedPiece);
			board[move.capturedPiece.square] = move.capturedPiece;
		}
			
		move.movingPiece.square = move.source;
		
		if(move.promotionType != Piece.Type.None)
		{
			move.movingPiece.type = Piece.Type.Pawn;
			move.movingPiece.sliding = false;
			move.movingPiece.loadVectorTable();
		}
		//update board
		board[move.source] = move.movingPiece;

		colorToMove = Color.invert(colorToMove);
	}
	
	public int hash()
	{
		int hash = 0;
		for(int i = 0; i < 128; i++)
		{
			Piece p = board[i];
			if (p == null) continue;
			
			hash ^= p.color << (i%32);
			hash ^= p.typeToInt() << (i%32);
		}
		return hash;
	}
	
	public void print(PrintStream out)
	{
		int pos = 112;
		while(pos >= 0)
		{
			for(int i = 0; i < 8; i++)
			{
				if (board[pos+i] != null)
					out.print(board[pos+i].toString());
				else
					out.print("--");
				out.print(" ");
			}
			out.println();
			pos -= 16;
		}
	}
	
	public String getMoveStringInCurrentPosition(Move m)
	{
		if (m.castlingMove == Move.CastlingMove.Kingside)
			return "O-O";
		else if (m.castlingMove == Move.CastlingMove.Queenside)
			return "O-O-O";
		
		String ret = getPieceChar(m.movingPiece.type);
		List<Move> moves = generateMoves(m.color);
		
		if (m.movingPiece.type == Piece.Type.Pawn && m.capturedPiece != null)
			ret = ret.concat(String.format("%1$c",getFileBySquare(m.source)));
		else if (m.movingPiece.type != Piece.Type.Pawn)
		{
			boolean needRank = false;
			boolean needFile = false;
			for(Move n : moves)
			{
				if(n.movingPiece.type != m.movingPiece.type || n.destination != m.destination || n.equals(m)) continue;
				if(getFileBySquare(n.source) == getFileBySquare(m.source))
					needRank = true;
				if(getRankBySquare(n.source) == getRankBySquare(m.source))
					needFile = true;
				if (!needRank || !needFile)
					needFile = true;
			}
			if (needFile)
				ret = ret.concat(String.format("%1$c",getFileBySquare(m.source)));
			if (needRank)
				ret = ret.concat(String.format("%1$c",getRankBySquare(m.source)));
		}
		
		if (m.capturedPiece != null)
			ret = ret.concat("x");
		ret = ret.concat(String.format("%1$c%2$c", getFileBySquare(m.destination), getRankBySquare(m.destination)));
			
		if (m.promotionType != Piece.Type.None)
			ret = ret.concat("=").concat(getPieceChar(m.promotionType));
		return ret;
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
	
	private String getPieceChar(Piece.Type pieceType)
	{
		switch(pieceType){
		case Pawn:
			return "";
		case Bishop:
			return "B";
		case Knight:
			return "N";
		case Rook:
			return "R";
		case Queen:
			return "Q";
		case King:
			return "K";
		case None:
			return "E";
		default:
			return "E";
		}
	}
}
