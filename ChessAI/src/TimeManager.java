
public class TimeManager {

	private long start;
	private long timeLimit;
	// the safety time window we give the searcher to close all its nodes
	private long timeOffset;
	
	private long lastRoundTime;
	
	public TimeManager(long timeOffset)
	{
		this.timeOffset = timeOffset;
		start = System.nanoTime(); // can't think of a better initial value
		timeLimit = 0;
	}
	
	// Starts timing for a search with a given time limit
	void startTiming(long timeLimit)
	{
		this.timeLimit = timeLimit;
		start = System.nanoTime();
	}

	// Ends timing for a search
	void endTiming() 
	{
		lastRoundTime = System.nanoTime() - start;	
	}
	
	// True when the time is up for a search
	boolean timeUp()
	{
		return (timeLimit > 0) && (((System.nanoTime() - start) + timeOffset) > timeLimit);
	}
	
	long getLastRoundTime()
	{
		return lastRoundTime;
	}
}
