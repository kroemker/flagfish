
public class Color {

	public static int WHITE = 0;
	public static int BLACK = 1;
	public static int NONE = 2;
	
	static int invert(int color)
	{
		return color ^ 1;
	}
	
	static boolean isWhite(int color)
	{
		return color == WHITE;
	}
	
	static boolean isBlack(int color)
	{
		return color == BLACK;
	}
	
	static String toString(int color)
	{
		if (color == WHITE)
			return "White";
		else if (color == BLACK)
			return "Black";
		else 
			return "Unknown";
	}
}
