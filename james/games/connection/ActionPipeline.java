package james.games.connection;

import james.games.connection.action.DoTurn;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Queue;
import java.util.ArrayDeque;
import javax.swing.SwingUtilities;
public class ActionPipeline {
	private PriorityBlockingQueue<CAction> queue = new PriorityBlockingQueue<CAction>();
	private boolean halt = false;
	private boolean change = false;
	private int numVoids = 0;
	private int[] times;
	private int[] endTimes;
	private int curPlayer = 0;
	private int curTime = 0;
	private CBuffer buffer;
	private CBox box;
	private Thread netSide = new Thread() {
		@Override
		public void run() {
			while (!halt) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				while (buffer.hasObjects()) queue.add((CAction)(buffer.getObject().message));
			}
		}
	};
	private Thread boxSide = new Thread() {
		private Queue<CAction> delayed = new ArrayDeque<CAction>();
		@Override
		public void run() {
			while (!halt) {
				boolean added = false;
				change=false;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
				CAction action = queue.peek();
				int top = CAction.VAL_TURN;
				while (action!=null&&action.typeVal()<=top&&action.player()==curPlayer&&action.getStartTime()==curTime) {
					numVoids=0;
					added=true;
					queue.poll();
					if (action.typeVal()!=CAction.VAL_DELAYED) proccess(action);
					else {
						//System.out.println("Hello add");
						delayed.add(action);
					}
					if (action.typeVal()==CAction.VAL_TURN) {
						top=CAction.VAL_MAX;
						times[action.player()] = action.endTime();
					}
					action = queue.peek();
				}
				if (top==CAction.VAL_MAX) {
					if (curPlayer==box.player()) dumpDelayed();
					curPlayer++;
					if (curPlayer>=times.length) {
						curTime++;
						curPlayer=0;
					}
				}
				numVoids++;
				try {
					if ((numVoids<3)&&(endTimes[box.player()]<=curTime||endTimes[box.player()]<=curTime+1&&box.player()<=curPlayer)) SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								if (!change) {
									//System.out.println("Reseting responsiveness: "+(box.player()+1)+" time: "+endTimes[box.player()]+" cur: "+curTime+" pl: "+(curPlayer+1));
									box.setResponsive(true);
								}
							}
					});
				} catch (InterruptedException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
		private void dumpDelayed() {
			//System.out.println("Hello dump "+(box.player()+1));
			while (!delayed.isEmpty()) {
				CAction next = delayed.remove();
				//System.out.println(next);
				proccess(next);
			}
			//System.out.println();
		}
	};
	/**
	 * Method ActionPipleline
	 *
	 *
	 * @param box
	 * @param buffer
	 *
	 */
	public ActionPipeline(CBox box, CBuffer buffer) {
		times=box.getTimes();
		endTimes=times.clone();
		this.box=box;
		this.buffer=buffer;
		boxSide.start();
		netSide.start();
	}

	/**
	 * Method distribute
	 *
	 *
	 * @param action
	 *
	 */
	public void distribute(CAction action) {
		if (action.getClass()==DoTurn.class) {
			//System.out.print("distribute end turn-");
			//System.out.println(action);
			endTimes[box.player()]=action.endTime();
			numVoids=0;
			change=true;
			box.setResponsive(false);
		}
		buffer.sendObject(action,CBuffer.ECHO);
	}
	private boolean occurrsBefore(int player, int time, boolean inc) {
		if (inc) player++;
		if (player>=endTimes.length) {
			player-=endTimes.length;
			time++;
		}
		if (time<curTime) return true;
		if (time>curTime) return false;
		return player<=curPlayer;
	}
	private void proccess(final CAction action) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.out.println("Pipeline put:"+action);
				box.proccessAction2(action);
			}
		});
	}
}
