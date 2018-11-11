
public class ClockManager {

	public enum Clock
	{
		CONVENTIONAL,
		INCREMENTAL,
		MOVETIME
	}
	
	Clock clockType;
	int increment = 0; //seconds
	int movesForTimeControl = 0;
	long baseTime = 60;  //seconds
	
	int colorToMove = Color.WHITE;
	
	int currentMoveNumber[] = {0,0};
	long clockStartTime[] = {0,0};
	long remainingTime[] = {0,0};
	
	boolean clockStopped = true;
	
	void startClock(int moves, long base, int increment)
	{
		this.clockType = Clock.CONVENTIONAL;
		this.movesForTimeControl = moves;
		this.baseTime = base;
		this.increment = increment;
		this.remainingTime[0] = base * 1000000000L;
		this.remainingTime[1] = base * 1000000000L;
		clockStartTime[Color.WHITE] = System.nanoTime();
		colorToMove = Color.WHITE;
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
		clockStartTime[Color.WHITE] = System.nanoTime();
		colorToMove = Color.WHITE;
		clockStopped = false;
	}
	
	void stopClock()
	{
		remainingTime[colorToMove] -= (System.nanoTime()-clockStartTime[colorToMove]);
		clockStopped = true;
	}
		
	long remainingTimeInMS(int color)
	{
		return remainingTime[color]/1000000;
	}
	
	void updateMove()
	{
		currentMoveNumber[colorToMove]++;
		if (clockType == Clock.CONVENTIONAL)
		{
			// update remaining time
			remainingTime[colorToMove] += increment * 1000000000 - (System.nanoTime()-clockStartTime[colorToMove]);
			// check time control
			if (movesForTimeControl > 0 && (currentMoveNumber[colorToMove] % movesForTimeControl) == 0)
				remainingTime[colorToMove] += baseTime * 1000000000;
		}
		else if (clockType == Clock.MOVETIME)
		{
			remainingTime[colorToMove] = baseTime * 1000000000L;
		}

		colorToMove = Color.invert(colorToMove);
		clockStartTime[colorToMove] = System.nanoTime();
	}
}