package org.yakindu.scr.test;

import org.yakindu.scr.RuntimeService;
import org.yakindu.scr.digitalwatch.DigitalwatchStatemachine;
import org.yakindu.scr.impl.DigitalWatchController;
import org.yakindu.scr.impl.DigitalWatchViewImpl;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DigitalwatchStatemachine sm = new DigitalwatchStatemachine();
		//Note: when you start the homework, you will need to temporarily comment out line sm.setTimer(new TimeService()) in Main.java until you start using time events in your statechart. Once you use at least one time event, you will have to uncomment this line again so that the time events work.
		sm.setTimer(new TimerService());
		DigitalWatchController controller = new DigitalWatchController(sm.getSCILogicUnit());
		DigitalWatchViewImpl view = new DigitalWatchViewImpl(controller, sm.getSCIButtons(), sm);
		sm.getSCIDisplay().setSCIDisplayOperationCallback(view);
		sm.getSCILogicUnit().setSCILogicUnitOperationCallback(controller);

		sm.init();
		sm.enter();
		
		RuntimeService.getInstance().registerStatemachine(sm,1);
		
		
		/*
		 * Good question. I just checked the reference implementation and this is what I understand about this behavior:
- if the watch is in time display mode
- and the user presses and releases the bottom-left button
- if the alarm is not activated, it becomes activated. The fact that the alarm is activated is shown by an alarm sign on the top-right corner of the display.
- if the alarm is not activated, it becomes activated and the alarm symbol on the top-right corner disappears


"If the bottom left button is held for 1.5 seconds or more, the watch goes into alarm editing mode."

Does this one, just like toggle alarm, also work only if we are in time display mode? Or does it work while in some other mode(s) also?
 Marlon Dumas
 Marlon Dumas 2 hours ago Yes, if the bottom left button is held for 1.5 seconds or more, and the watch is in time display mode, the watch goes into alarm editing mode.
If it is in a different mode, then the watch does not go into alarm editing mode.




Talha Mahin Mir
Talha Mahin Mir 6 hours ago
Not sure if something's wrong with the code or I'm doing it wrongly, but for this point 6 when i toggle the alarm using 'LogicUnit.setAlarm()', nothing appears on the screen that indicate alarm state. I confirmed that the operation setAlarm() is actually called by performing chart simulation. Please verify if this problem is with the code or I'm at mistake here. 
 Talha Mahin Mir
Talha Mahin Mir 2 hours ago Never-mind, i was just missing the update call. 






As i understand from the exercise if i activate the chrono, i can go back to time display mode and chrono will keep ticking. 

TimeDisplay will stop ticking when in timeedit mode. Is chrono also effected by this or once chrono is started nothing can interrupt it(except when you cut the power) unless you tell it to stop?


Yes correct, I tried it in the reference implementation:
- If I activate chrono, then go back to time display mode, the chrono keeps ticking. If I go back to chrono mode after 1 minute, the chrono shows the 1 minute has passed.
- If I acticate chrono, then go to time display mode, and then go to time editing mode, and I stay there for about a minute, when I go back to time display mode and then back to chrono, the chrono shows that a bit more than 1 minute has passed.
- same if I go into alarm edit mode.
Yes indeed it seems that nothing can stop the chrono except you moving to the chrono mode and stopping it....






If the alarm is doing its four seconds of blinking, and I push some button while it's blinking, then does the button only interrupt the alarm, or does it interrupt the alarm and also do whatever it usually does?


While the alarm is blinking, pressing on a button (any button) stops the alarm, without triggering any other function. After the alarm is stopped, the watch goes back to exactly the same mode where it was when the alarm started blinking.




		 */
	}

}
