public class Piece {
	
	public enum Type {
		Pawn,
		Knight,
		King,
		Bishop,
		Rook,
		Queen,
		None
	}
	
	public int color;
	public Type type;
	public int square;
	public boolean sliding;
	public boolean hasMoved;
	public int[] vectorMoves;
	public int[] positionalValueTable;
	public boolean isKingsideRook;
	public boolean isQueensideRook;
	
	public Piece(int color, Type type, int square) {
		this.color = color;
		this.type = type;
		this.square = square;
		this.sliding = isSliding(type);
		this.hasMoved = false;
		this.vectorMoves = getVectorMoves(type, color);
		this.positionalValueTable = getPositionalValueTable(type);
		this.isKingsideRook = false;
		this.isQueensideRook = false;
	}
	
	public Piece(int color, Type type, int square, boolean isKingsideRook, boolean isQueensideRook) {
		this.color = color;
		this.type = type;
		this.square = square;
		this.sliding = isSliding(type);
		this.hasMoved = false;
		this.vectorMoves = getVectorMoves(type, color);
		this.positionalValueTable = getPositionalValueTable(type);
		this.isKingsideRook = isKingsideRook;
		this.isQueensideRook = isQueensideRook;
	}
	
	public String toString()
	{
		String out = "";
		switch (type) {
			case Pawn: out = out + "P"; break;
			case Knight: out = out + "N"; break;
			case King: out = out + "K"; break;
			case Bishop: out = out + "B"; break;
			case Rook: out = out + "R"; break;
			case Queen: out = out + "Q"; break;
			default: return "";
		}
		if (color == Color.WHITE) 
			out += "w"; 
		else
			out+= "b";
		return out;
	}
	
	public void loadVectorTable()
	{
		vectorMoves = getVectorMoves(type, color);
	}
	
	public boolean equals(Piece obj)
	{
		return (square == obj.square && type == obj.type && color == obj.color);
	}
	
	public int typeToInt() {
		switch (type) {
		case Pawn: return 1;
		case Knight: return 2;
		case King: return 3;
		case Bishop: return 4;
		case Rook: return 5;
		case Queen: return 6;
		default: return 0; //should not reach
		}
	}
	
	public static int typeToInt(Piece.Type type) {
		switch (type) {
		case Pawn: return 1;
		case Knight: return 2;
		case King: return 3;
		case Bishop: return 4;
		case Rook: return 5;
		case Queen: return 6;
		default: return 0; //should not reach
		}
	}
	
	public static boolean isSliding(Type type) {
		switch (type) {
			case Pawn: return false;
			case Knight: return false;
			case King: return false;
			case Bishop: return true;
			case Rook: return true;
			case Queen: return true;
			default: return false; //should not reach
		}
	}
	
	private int[] getVectorMoves(Type type, int color) {
		switch (type) {
			case Pawn: 
				if (color == Color.BLACK)
					return LookupTables.BlackPawnVectorTable;
				else
					return LookupTables.WhitePawnVectorTable;
			case Knight: return LookupTables.KnightVectorTable;
			case King: return LookupTables.KingVectorTable;
			case Bishop: return LookupTables.BishopVectorTable;
			case Rook: return LookupTables.RookVectorTable;
			case Queen: return LookupTables.QueenVectorTable;
			default: return null; //should not reach
		}
	}
	
	private int[] getPositionalValueTable(Type type) {
		switch (type) {
			case Pawn: 
				if (color == Color.BLACK)
					return LookupTables.BlackPawnPositionalValueTable;
				else
					return LookupTables.WhitePawnPositionalValueTable;
			case Knight: return LookupTables.KnightPositionalValueTable;
			default: return null;
		}
	}
}
