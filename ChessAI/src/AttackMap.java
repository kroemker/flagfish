import java.io.PrintStream;


public class AttackMap {

	int[] map; // has also 0x88 board format, so that there is no need for coordinate conversion
	int version;
	boolean valid;
	
	public AttackMap() {
		map = new int[128];
		for(int i = 0; i < 128; i++)
			map[i] = 0;
		version = 0;
		valid = false;
	}
	
	public void setValid() {
		valid = true;
	}
	
	public void setInvalid() {
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void clearMap() {
		version++;
		if (version == Integer.MAX_VALUE)
		{
			for(int i = 0; i < 128; i++)
				map[i] = 0;
			version = 0;
		}
	}
	
	public void setAttacked(int square) {
		map[square] = version;
	}
	
	public boolean isAttacked(int square) {
		return map[square] == version;
	}
	
	public void print(PrintStream out) {
		int pos = 112;
		while(pos >= 0)
		{
			for(int i = 0; i < 8; i++)
			{
				out.print(map[pos+i]);
				out.print("\t");
			}
			out.println();
			pos -= 16;
		}
	}
}
