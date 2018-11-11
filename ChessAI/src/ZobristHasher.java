import java.util.Random;

public class ZobristHasher 
{
	private Board board;
	
	private long zobristTable[];
	
	private long zobristHash;
	
	private int tableSize = 2 *  64 * 6 + 16 + 8 + 1; //2 players * 64 squares * 6 different pieces + 16 castling right permutations + 8 files of the enpassant square + 1 black to move

	private int castlingRightIndex;
	
	public ZobristHasher(Board b)
	{
		board = b;
		zobristHash = 0;
		
		Random sr = new Random();
				
		zobristTable = new long[tableSize];
		
		for(int i = 0; i < tableSize; i++)
			zobristTable[i] = sr.nextLong();
		        
		hashNew();
	}
	
	public void hashNew()
	{
		zobristHash = 0;
        for(int i = 0;  i < 8; i++)
            for(int j = 0;  j < 8; j++)
            {
            	Piece p = board.getPiece(i + 16 * j);
            	if (p != null)
            	{
            		int offset = p.color * 64 * 6; // offset in the table for black/white pieces
            		int square = j * 8 + i;
            		int type = p.typeToInt() - 1;
            		zobristHash ^= zobristTable[offset + square + type * 64];
            	}
            }
        
        // castling rights
        int crwk = board.getCastlingRights(Color.WHITE).canKingside ? 1 : 0;        
        int crwq = board.getCastlingRights(Color.WHITE).canQueenside ? 1 : 0;
        int crbk = board.getCastlingRights(Color.BLACK).canKingside ? 1 : 0;
        int crbq = board.getCastlingRights(Color.BLACK).canQueenside ? 1 : 0;
        castlingRightIndex =  crwk << 3 + crwq << 2 + crbk << 1 + crbq;
        zobristHash ^= zobristTable[2 * 64 * 6 + castlingRightIndex];
        
        // enpassant file
        if (board.getEnpassantSquare() < 128)
        	zobristHash ^= zobristTable[2 * 64 * 6 + 16 + board.getEnpassantSquare() & 7];
        
        // black to move
        if (board.getColorToMove() == Color.BLACK)
        	zobristHash ^= zobristTable[tableSize-1];
	}
	
	// update hash by a made move
	// has to be called AFTER a move was applied to the board!
	public void updateHash(Move m)
	{
		int offset = m.color * 64 * 6;
		if (m.castlingMove == Move.CastlingMove.None)
		{
	    	int dsquare = m.destination >> 1 + m.destination & 7;
			int ssquare = m.source >> 1 + m.source & 7;
	    	int type = m.movingPiece.typeToInt() - 1;
			zobristHash ^= zobristTable[offset + ssquare + type * 64]; // remove piece from src
			
			if (m.promotionType == Piece.Type.None)
				zobristHash ^= zobristTable[offset + dsquare + type * 64]; // place piece on dest
			else
			{
		    	type = Piece.typeToInt(m.promotionType);
		    	zobristHash ^= zobristTable[offset + dsquare + type * 64]; // place promo piece on dest
			}

			if (m.capture)
			{
				offset = m.capturedPiece.color * 64 * 6;
		    	type = m.capturedPiece.typeToInt() - 1;
		    	dsquare = m.capturedPiece.square >> 1 + m.capturedPiece.square & 7; // need this in case of enpassant capture
				zobristHash ^= zobristTable[offset + dsquare + type * 64]; // remove captured piece from dst
			}
		}
		else
		{
			int kingdsquare = m.movingPiece.square >> 1 + m.movingPiece.square & 7;
        	int rookdsquare = m.capturedPiece.square >> 1 + m.capturedPiece.square & 7;
			int kingssquare = m.kingSrc >> 1 + m.kingSrc & 7;
			int rookssquare = m.rookSrc >> 1 + m.rookSrc & 7;
			// put pieces
        	zobristHash ^= zobristTable[offset + kingdsquare + 3 * 64]; // king is 3
        	zobristHash ^= zobristTable[offset + rookdsquare + 5 * 64]; // rook is 5
			// remove pieces
        	zobristHash ^= zobristTable[offset + kingssquare + 3 * 64]; // king is 3
        	zobristHash ^= zobristTable[offset + rookssquare + 5 * 64]; // rook is 5
		}
		
		zobristHash ^= zobristTable[2 * 64 * 6 + castlingRightIndex]; // undo old castling rights
        int crwk = board.getCastlingRights(Color.WHITE).canKingside ? 1 : 0;        
        int crwq = board.getCastlingRights(Color.WHITE).canQueenside ? 1 : 0;
        int crbk = board.getCastlingRights(Color.BLACK).canKingside ? 1 : 0;
        int crbq = board.getCastlingRights(Color.BLACK).canQueenside ? 1 : 0;
        castlingRightIndex =  crwk << 3 + crwq << 2 + crbk << 1 + crbq;
        zobristHash ^= zobristTable[2 * 64 * 6 + castlingRightIndex];
		
		// enpassant square
		if (m.enpassantSquare < 128)
        	zobristHash ^= zobristTable[2 * 64 * 6 + 16 + m.enpassantSquare & 7];
		if (m.oldEnpassantSquare < 128)
        	zobristHash ^= zobristTable[2 * 64 * 6 + 16 + m.oldEnpassantSquare & 7];
		
		// switch side to move
		zobristHash ^= zobristTable[tableSize-1]; 	
	}
		
	public long getZobristHash()
	{
		return zobristHash;
	}
}
