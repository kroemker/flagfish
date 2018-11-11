import java.util.List;
import java.util.ListIterator;

public class Evaluator {

	Board board;
	
	public Evaluator(Board board)
	{
		this.board = board;
	}
	
	public float evaluate(int color)
	{
		float eval = 0;

		int pawnOnFilesMy[] = {0,0,0,0,0,0,0,0,0,0};
		int pawnOnFilesOther[] = {0,0,0,0,0,0,0,0,0,0};

		// count material
		for(Piece p : board.getPieceList(color))
		{
			eval += getPieceValue(p.type);
			if (p.positionalValueTable != null)
				eval += p.positionalValueTable[p.square] * 0.05;
			
			if (p.type == Piece.Type.Pawn)
				pawnOnFilesMy[(p.square & 7)+1]++;
		}
		
		for(Piece p : board.getOtherPieceList(color))
		{
			eval -= getPieceValue(p.type);
			if (p.positionalValueTable != null)
				eval -= p.positionalValueTable[p.square] * 0.05;
			
			if (p.type == Piece.Type.Pawn)
				pawnOnFilesMy[(p.square & 7)]++;
		}

		// count move possibilities
		List<Move> ml = board.generateMoves(color);
		eval += ml.size() * 0.05;
		ml = board.generateMoves(Color.invert(color));
		eval -= ml.size() * 0.05;
		
		// doubled, isolated, passed pawns
		for (int i = 0; i < 8; i++)
		{
			if(pawnOnFilesMy[i+1] > 1)
				eval -= 0.07;
			if(pawnOnFilesMy[i+1] > 0 && pawnOnFilesMy[i] == 0 && pawnOnFilesMy[i+2] == 0)
				eval -= 0.05;
			if(pawnOnFilesMy[i+1] > 0 && pawnOnFilesOther[i] == 0 && pawnOnFilesOther[i+1] == 0 && pawnOnFilesOther[i+2] == 0)
				eval += 0.08;
		}
			
		// king safety
		
		
		return eval;
	}
	
	private float getPieceValue(Piece.Type type)
	{
		switch (type) {
			case Pawn: return 1f;
			case Knight: return 2.8f;
			case King: return 10f;
			case Bishop: return 3.1f;
			case Rook: return 5f;
			case Queen: return 9f;
			default: return 0; //should not reach
		}
	}
	
}
