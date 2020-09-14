import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Communicator {

	static final int MODE_GO = 0;
	static final int MODE_FORCE = 1;
	
	enum Protocol {
		XBoard,
		UCI,
		Unknown
	}
	
	BufferedReader reader;
	
	Engine engine;
	
	public Communicator(Engine e)
	{
		reader = new BufferedReader(new InputStreamReader(System.in));
		engine = e;
	}
	
	public void communicate() throws IOException
	{
		boolean quit = false;
		int mode = MODE_FORCE;
		int protocolVersion = 1;
		boolean editMode = false;
		boolean analyzeMode = false;
		boolean ponder = false;
		boolean postThinkingOutput = false;
		
		MoveInfo bestMove = null;
		Protocol protocol = Protocol.Unknown;
		boolean think = false;
		while(!quit)
		{
			if (protocol == Protocol.XBoard && mode == MODE_GO && engine.engineToMove())
			{
				MoveInfo mi = engine.move();
				send("move " + mi.move.toString());
				// in case someone got mated update mode
				mode = sendMateResult(engine.checkGameOver(), mode);
			} else if (protocol == Protocol.UCI && think) {
				bestMove = engine.move();
				send("bestmove " + bestMove.move.toString());
				think = false;
			}
			
			String command = reader.readLine();
			engine.logCommand("Server: " + command);
			
			String[] parts = command.split(" ");
			switch (parts[0])
			{
			case "xboard":
				protocol = Protocol.XBoard;
				continue;
			case "uci":
				protocol = Protocol.UCI;
				send("id name " + Engine.engineName);
				send("id author Leo Krömker");
				send("uciok");
				continue;
			}
			
			if (protocol == Protocol.UCI)
			{
				switch (parts[0]) 
				{
				case "isready":
					send("readyok");
					break;
				case "ucinewgame":
					engine.setDepthLimit(0);
					engine.startNewGame();
					break;
				case "position":
					int index = 2;
					if (parts[1].equals("fen")) {
						engine.setBoard(parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + " " + parts[7]);
						index = 8;
					} else if (parts[1].equals("startpos"))
						engine.setBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
					
					if (parts.length > index && parts[index].equals("moves"))
						for(index = index + 1; index < parts.length; index++)
							engine.inputMove(parts[index]);
					
					break;
				case "go":
					boolean infinite = false;
					for (int i = 0; i < parts.length; i++)
					{
						if (parts[i].equals("depth")) 
						{
							engine.setDepthLimit(Integer.parseInt(parts[i+1]));
							i++;
						}
						else if (parts[i].equals("movetime"))
						{
							engine.getClockManager().startMoveTimeClock(Integer.parseInt(parts[i+1]) / 1000);
							i++;
						}
						else if (parts[i].equals("wtime")) 
						{
							engine.setClockTime(Color.WHITE, Integer.parseInt(parts[i+1]) / 1000);
							i++;
						}
						else if (parts[i].equals("btime")) 
						{
							engine.setClockTime(Color.BLACK, Integer.parseInt(parts[i+1]) / 1000);
							i++;
						}
						else if (parts[i].equals("infinite")) 
						{
							infinite = true;
						}
					}
					think = true;
					break;
				case "stop":
					think = false;
					if(bestMove != null)
						send("bestMove " + bestMove.move.toString());
					break;
				case "quit":
					quit = true;
					break;
				}
			}
			else if (protocol == Protocol.XBoard)
			{
				switch (parts[0]) 
				{
				case "protover":
					protocolVersion = Integer.parseInt(parts[1]);
					send("feature ping=1 setboard=1 playother=0 usermove=1 time=0 draw=1 colors=1 analyze=0 myname=\"" + Engine.engineName + "\"");
				case "computer":
				case "accepted":
				case "rejected":
					break;
				case "variant":
					//variants not supported
					break;
				case "new":
					//mode = MODE_GO;
					engine.setDepthLimit(0);
					engine.startNewGame();
					break;
				case "quit":
					quit = true;
					break;
				case "random":
					// random not supported	
					break;
				case "playother":
					break;
				case "force":
					mode = MODE_FORCE;
					engine.getClockManager().stopClock();
					break;
				case "go":
					mode = MODE_GO;
					engine.setColor(engine.board.getColorToMove());
					break;
				case "white":
					engine.setColor(Color.WHITE);
					break;
				case "black":
					engine.setColor(Color.BLACK);
					break;
				case "level":
					String[] baseParts = parts[2].split(":");
					if (baseParts.length == 2)
						engine.getClockManager().startClock(Integer.parseInt(parts[1]), Integer.parseInt(baseParts[0])*60 + Integer.parseInt(baseParts[1]), Integer.parseInt(parts[3]));
					else
						engine.getClockManager().startClock(Integer.parseInt(parts[1]), Integer.parseInt(baseParts[0])*60, Integer.parseInt(parts[3]));
					break;
				case "st":
					engine.getClockManager().startMoveTimeClock((int)Float.parseFloat(parts[1]));
					break;
				case "sd":
					engine.setDepthLimit(Integer.parseInt(parts[1]));
					break;
				case "nps":
					break;
				case "time":
					break;
				case "otim":
					break;
				case "?":
					break;
				case "ping":
					send("pong " + parts[1]);
					break;
				case "draw":
					break;
				case "result":
					engine.setResult(parts[1], parts[2]);
					break;
				case "setboard":
					engine.setBoard(command.substring(9));
					break;
				case "edit":
					editMode = true;
				case "hint":
					break;
				case "bk":
					//return no book in yet
					break;
				case "undo":
					engine.undo();
					break;
				case "remove":
					engine.undo();
					engine.undo();
					break;
				case "hard":
					ponder = true;
					break;
				case "easy":
					ponder = false;
					break;
				case "post":
					postThinkingOutput = true;
					break;
				case "nopost":
					postThinkingOutput = false;
					break;
				case "analyze":
					analyzeMode = true;
					send("Error: (unknown command): analyze");
					break;
				case "name":
					engine.setOpponentName(parts[1]);
					break;
				case "rating":
					engine.setRatings(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
					break;
				case "usermove":
				default:
					String err = engine.inputMove(parts[1]);
					if (err != null)
						send(err);
					else
						// in case someone got mated update mode
						mode = sendMateResult(engine.checkGameOver(), mode);
					break;
				}
			}
		}
	}

	int sendMateResult(int color, int currentMode)
	{
		if (color == Color.WHITE)
		{
			send("0-1 {Black mates}");
			return MODE_FORCE;
		}
		else if (color == Color.BLACK)
		{
			send("1-0 {White mates}");
			return MODE_FORCE;
		}
		return currentMode;
	}
	
	void send(String s)
	{
		engine.logCommand("Engine: " + s);
		System.out.println(s);
	}
}
