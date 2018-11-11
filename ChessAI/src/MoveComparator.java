import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {

	@Override
	public int compare(Move o1, Move o2) {
		
		if (o1.capture && !o2.capture)
			return -1;
		if(o2.capture && !o1.capture)
			return 1;
		
		return 0;
	}
}
