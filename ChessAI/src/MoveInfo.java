
public class MoveInfo {

	public Move move;
	public float score;
	public int alphaBetaNodes;
	public int quiesceNodes;
	public int allNodes;
	public int depth;
	public long time;
	public boolean aborted;
	
	public MoveInfo()
	{
		move = null;
		aborted = true;
		this.score = 0;
		this.alphaBetaNodes = 0;
		this.quiesceNodes = 0;
		this.allNodes = 0;
		this.depth = 0;
		this.time = 0;
	}
	
	public MoveInfo(Move move, float score, int alphaBetaNodes, int quiesceNodes, int depth, long time)
	{
		this.move = move;
		this.score = score;
		this.alphaBetaNodes = alphaBetaNodes;
		this.quiesceNodes = quiesceNodes;
		this.allNodes = alphaBetaNodes + quiesceNodes;
		this.depth = depth;
		this.time = time;
		this.aborted = false;
	}
	
}
