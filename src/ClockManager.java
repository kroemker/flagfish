
// only used with xboard
public class ClockManager {
	
	public enum Clock
	{
		CONVENTIONAL,
		INCREMENTAL,
		MOVETIME
	}

	Board board;
	Clock clockType;
	int increment = 0; //seconds
	int movesForTimeControl = 0;
	long baseTime = 60;  //seconds
	
	int currentMoveNumber[] = {0,0};
	long clockStartTime[] = {0,0};
	long remainingTime[] = {0,0};
	
	boolean clockStopped = true;
	ClockManager(Board board)
	{
		this.board = board;
	}

	void startClock(int moves, long base, int increment)
	{
		this.clockType = moves == 0 ? Clock.INCREMENTAL : Clock.CONVENTIONAL;
		this.movesForTimeControl = moves;
		this.baseTime = base;
		this.increment = increment;
		this.remainingTime[0] = base * 1000000000L;
		this.remainingTime[1] = base * 1000000000L;
		clockStartTime[board.getColorToMove()] = System.nanoTime();
		clockStopped = false;
	}
		
	void startMoveTimeClock(int movetime)
	{
		this.clockType = Clock.MOVETIME;
		this.baseTime = movetime;
		this.increment = 0;
		this.movesForTimeControl = 0;
		this.remainingTime[0] = movetime * 1000000000L;
		this.remainingTime[1] = movetime * 1000000000L;
		clockStartTime[board.getColorToMove()] = System.nanoTime();
		clockStopped = false;
	}
	
	void stopClock()
	{
		remainingTime[board.getColorToMove()] -= (System.nanoTime()-clockStartTime[board.getColorToMove()]);
		clockStopped = true;
	}
		
	long remainingTimeInMS(int color)
	{
		return remainingTime[color]/1000000;
	}
	
	void updateMove()
	{
		int colorToMove = board.getColorToMove();
		currentMoveNumber[colorToMove]++;
		if (clockType == Clock.CONVENTIONAL || clockType == Clock.INCREMENTAL)
		{
			remainingTime[colorToMove] += increment * 1000000000 - (System.nanoTime()-clockStartTime[colorToMove]);
			// check time control
			if (clockType == Clock.CONVENTIONAL && (currentMoveNumber[colorToMove] % movesForTimeControl == 0))
				remainingTime[colorToMove] += baseTime * 1000000000;
		}
		else if (clockType == Clock.MOVETIME)
		{
			remainingTime[colorToMove] = baseTime * 1000000000L;
		}

		clockStartTime[colorToMove] = System.nanoTime();
	}
}