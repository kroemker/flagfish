import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


public class Logger {

	private PrintStream pgnFile;
	private PrintStream logFile;
	private int moveCount;
	private Board board;
	private String opponentName = "Unknown";
	private int aiRating = 0;
	private int opponentRating = 0;
	private int engineColor = Color.WHITE;
	private String result;
	private String comment;
	
	Stack<Move> moves;
	
	public Logger(Board board, boolean logToFile)
	{
		this.board = board;
		reset();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		
		if(logToFile)
			try 
			{
				logFile = new PrintStream("log_" + dateFormat.format(date) + ".txt", "UTF-8");
			} catch (FileNotFoundException e) 
			{	
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
	}
	
	public void reset()
	{
		this.moveCount = 0;
		this.moves = new Stack<Move>();
	}
	
	public void generatePGN()
	{
		String blackName, whiteName;
		if(engineColor == Color.WHITE)
		{
			blackName = opponentName;
			whiteName = Engine.engineName;
		}
		else
		{
			blackName = Engine.engineName;
			whiteName = opponentName;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		try 
		{
			pgnFile = new PrintStream("match_" + dateFormat.format(date) + ".pgn", "UTF-8");
			pgnFile.println("[Event \"Casual AI Game\"]");
			pgnFile.println("[Site \"AI Home\"]");
			pgnFile.println("[Date \"" + dateFormat.format(date) + "\"]");
			pgnFile.println("[Round \"1\"]");
			pgnFile.println("[White \"" + whiteName + "\"]");
			pgnFile.println("[Black \"" + blackName + "\"]");
			pgnFile.println("[Result \"" + result + "\"]");
			
			board.loadStartPosition();
			for (int i = 0; i < moves.size(); i++) {
				if (((i+1) % 2) == 1)
					pgnFile.print((i/2+1) + ". ");
				pgnFile.print(board.getMoveStringInCurrentPosition(moves.get(i)) + " ");
				board.makeMove(moves.get(i));
			}
			pgnFile.close();
			
		} catch (FileNotFoundException e) 
		{	
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void logCommand(String command)
	{
		logFile.println(command);
	}
	
	public Move getLastMove()
	{
		return moves.peek();
	}
	
	public void removeLastMove()
	{
		moves.pop();
	}
	
	public void updateMoved(MoveInfo mi)
	{
		moves.push(mi.move);
		moveCount++;
		String moveString = board.getMoveStringInCurrentPosition(mi.move);
		logFile.println("----------------------");
		logFile.println("Move #" + moveCount);
		logFile.println("[" + moveString + "]");
		logFile.println("Evaluation/Depth: " + mi.score + "/" + mi.depth);
		logFile.println("Nodes/AB Nodes/Quiesce Nodes: " + mi.allNodes + "/" + mi.alphaBetaNodes + "/" + mi.quiesceNodes);
		logFile.println("Time: " + (mi.time/1000000) + "ms");
		logFile.println();
		board.print(logFile);
		logFile.println();
	}
	
	public void setEngineColor(int color)
	{
		engineColor = color;
	}
	
	public void setOpponentName(String name)
	{
		opponentName = name;
	}
	
	public void setRatings(int air, int oppr)
	{
		aiRating = air;
		opponentRating = oppr;
	}

	public void setResult(String r, String c)
	{
		result = r;
		comment = c;
	}
	
	@Override
	protected void finalize()
	{
		if(logFile != null)
			logFile.close();
	}	
}
