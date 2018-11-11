
public class CastlingRights {

	public boolean canQueenside;
	public boolean canKingside;
	
	public CastlingRights() {
		canQueenside = true;
		canKingside = true;
	}
	
	public CastlingRights(CastlingRights n) {
		canKingside = n.canKingside;
		canQueenside = n.canQueenside;
	}
	
	public void unsetAll() {
		canQueenside = false;
		canKingside = false;
	}
	
	public void copy(CastlingRights n) {
		canKingside = n.canKingside;
		canQueenside = n.canQueenside;
	}
}
