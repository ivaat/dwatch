package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	protected class SCIButtonsImpl implements SCIButtons {
	
		private boolean topLeftPressed;
		
		public void raiseTopLeftPressed() {
			topLeftPressed = true;
		}
		
		private boolean topLeftReleased;
		
		public void raiseTopLeftReleased() {
			topLeftReleased = true;
		}
		
		private boolean topRightPressed;
		
		public void raiseTopRightPressed() {
			topRightPressed = true;
		}
		
		private boolean topRightReleased;
		
		public void raiseTopRightReleased() {
			topRightReleased = true;
		}
		
		private boolean bottomLeftPressed;
		
		public void raiseBottomLeftPressed() {
			bottomLeftPressed = true;
		}
		
		private boolean bottomLeftReleased;
		
		public void raiseBottomLeftReleased() {
			bottomLeftReleased = true;
		}
		
		private boolean bottomRightPressed;
		
		public void raiseBottomRightPressed() {
			bottomRightPressed = true;
		}
		
		private boolean bottomRightReleased;
		
		public void raiseBottomRightReleased() {
			bottomRightReleased = true;
		}
		
		protected void clearEvents() {
			topLeftPressed = false;
			topLeftReleased = false;
			topRightPressed = false;
			topRightReleased = false;
			bottomLeftPressed = false;
			bottomLeftReleased = false;
			bottomRightPressed = false;
			bottomRightReleased = false;
		}
	}
	
	protected SCIButtonsImpl sCIButtons;
	
	protected class SCIDisplayImpl implements SCIDisplay {
	
		private SCIDisplayOperationCallback operationCallback;
		
		public void setSCIDisplayOperationCallback(
				SCIDisplayOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
	}
	
	protected SCIDisplayImpl sCIDisplay;
	
	protected class SCILogicUnitImpl implements SCILogicUnit {
	
		private SCILogicUnitOperationCallback operationCallback;
		
		public void setSCILogicUnitOperationCallback(
				SCILogicUnitOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
		private boolean startAlarm;
		
		public void raiseStartAlarm() {
			startAlarm = true;
		}
		
		protected void clearEvents() {
			startAlarm = false;
		}
	}
	
	protected SCILogicUnitImpl sCILogicUnit;
	
	private boolean initialized = false;
	
	public enum State {
		main_region_displayStates,
		main_region_displayStates_IndigLo_LightOff,
		main_region_displayStates_IndigLo_LightActive,
		main_region_displayStates_IndigLo_LightDelay,
		main_region_displayStates_MainDisplay_TimeDisplayed,
		main_region_displayStates_MainDisplay_ChronoDisplayed,
		main_region_displayStates_MainDisplay_TimeEditingEntryWait,
		main_region_displayStates_MainDisplay_TimeEditingActive,
		main_region_displayStates_MainDisplay_AlarmInDisplayDelay,
		main_region_displayStates_MainDisplay_AlarmDisplayed,
		main_region_displayStates_MainDisplay_AlarmEdit,
		main_region_displayStates_MainDisplay_AlarmOutDisplayDelay,
		main_region_displayStates_Chrono_ChronoRunning,
		main_region_displayStates_Chrono_ChronoWaiting,
		main_region_displayStates_TimerUpdater_TimeUpdate,
		$NullState$
	};
	
	private final State[] stateVector = new State[4];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[9];
	private boolean startChrono;
	
	private boolean stopChrono;
	
	private boolean chronoRunning;
	
	protected void setChronoRunning(boolean value) {
		chronoRunning = value;
	}
	
	protected boolean getChronoRunning() {
		return chronoRunning;
	}
	
	private boolean timeEditModeActive;
	
	protected void setTimeEditModeActive(boolean value) {
		timeEditModeActive = value;
	}
	
	protected boolean getTimeEditModeActive() {
		return timeEditModeActive;
	}
	
	public DigitalwatchStatemachine() {
		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
	}
	
	public void init() {
		this.initialized = true;
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		for (int i = 0; i < 4; i++) {
			stateVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		setChronoRunning(false);
		
		setTimeEditModeActive(false);
	}
	
	public void enter() {
		if (!initialized) {
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		}
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		enterSequence_main_region_default();
	}
	
	public void exit() {
		exitSequence_main_region();
	}
	
	/**
	 * @see IStatemachine#isActive()
	 */
	public boolean isActive() {
		return stateVector[0] != State.$NullState$||stateVector[1] != State.$NullState$||stateVector[2] != State.$NullState$||stateVector[3] != State.$NullState$;
	}
	
	/** 
	* Always returns 'false' since this state machine can never become final.
	*
	* @see IStatemachine#isFinal()
	*/
	public boolean isFinal() {
		return false;
	}
	/**
	* This method resets the incoming events (time events included).
	*/
	protected void clearEvents() {
		sCIButtons.clearEvents();
		sCILogicUnit.clearEvents();
		startChrono = false;
		stopChrono = false;
		for (int i=0; i<timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}
	
	/**
	* This method resets the outgoing events.
	*/
	protected void clearOutEvents() {
	}
	
	/**
	* Returns true if the given state is currently active otherwise false.
	*/
	public boolean isStateActive(State state) {
	
		switch (state) {
		case main_region_displayStates:
			return stateVector[0].ordinal() >= State.
					main_region_displayStates.ordinal()&& stateVector[0].ordinal() <= State.main_region_displayStates_TimerUpdater_TimeUpdate.ordinal();
		case main_region_displayStates_IndigLo_LightOff:
			return stateVector[0] == State.main_region_displayStates_IndigLo_LightOff;
		case main_region_displayStates_IndigLo_LightActive:
			return stateVector[0] == State.main_region_displayStates_IndigLo_LightActive;
		case main_region_displayStates_IndigLo_LightDelay:
			return stateVector[0] == State.main_region_displayStates_IndigLo_LightDelay;
		case main_region_displayStates_MainDisplay_TimeDisplayed:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_TimeDisplayed;
		case main_region_displayStates_MainDisplay_ChronoDisplayed:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_ChronoDisplayed;
		case main_region_displayStates_MainDisplay_TimeEditingEntryWait:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_TimeEditingEntryWait;
		case main_region_displayStates_MainDisplay_TimeEditingActive:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_TimeEditingActive;
		case main_region_displayStates_MainDisplay_AlarmInDisplayDelay:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_AlarmInDisplayDelay;
		case main_region_displayStates_MainDisplay_AlarmDisplayed:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_AlarmDisplayed;
		case main_region_displayStates_MainDisplay_AlarmEdit:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_AlarmEdit;
		case main_region_displayStates_MainDisplay_AlarmOutDisplayDelay:
			return stateVector[1] == State.main_region_displayStates_MainDisplay_AlarmOutDisplayDelay;
		case main_region_displayStates_Chrono_ChronoRunning:
			return stateVector[2] == State.main_region_displayStates_Chrono_ChronoRunning;
		case main_region_displayStates_Chrono_ChronoWaiting:
			return stateVector[2] == State.main_region_displayStates_Chrono_ChronoWaiting;
		case main_region_displayStates_TimerUpdater_TimeUpdate:
			return stateVector[3] == State.main_region_displayStates_TimerUpdater_TimeUpdate;
		default:
			return false;
		}
	}
	
	/**
	* Set the {@link ITimer} for the state machine. It must be set
	* externally on a timed state machine before a run cycle can be correct
	* executed.
	* 
	* @param timer
	*/
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}
	
	/**
	* Returns the currently used timer.
	* 
	* @return {@link ITimer}
	*/
	public ITimer getTimer() {
		return timer;
	}
	
	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}
	
	public SCIButtons getSCIButtons() {
		return sCIButtons;
	}
	
	public SCIDisplay getSCIDisplay() {
		return sCIDisplay;
	}
	
	public SCILogicUnit getSCILogicUnit() {
		return sCILogicUnit;
	}
	
	private void raiseStartChrono() {
		startChrono = true;
	}
	
	private void raiseStopChrono() {
		stopChrono = true;
	}
	
	private boolean check_main_region_displayStates_IndigLo_LightOff_tr0_tr0() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_displayStates_IndigLo_LightActive_tr0_tr0() {
		return sCIButtons.topRightReleased;
	}
	
	private boolean check_main_region_displayStates_IndigLo_LightDelay_tr0_tr0() {
		return timeEvents[0];
	}
	
	private boolean check_main_region_displayStates_IndigLo_LightDelay_tr1_tr1() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeDisplayed_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeDisplayed_tr1_tr1() {
		return (timeEvents[1]) && (getTimeEditModeActive()==false);
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeDisplayed_tr2_tr2() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeDisplayed_tr3_tr3() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr1_tr1() {
		return (sCIButtons.bottomRightPressed) && (getChronoRunning()==false);
	}
	
	private boolean check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr2_tr2() {
		return (sCIButtons.bottomRightPressed) && (getChronoRunning()==true);
	}
	
	private boolean check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr3_tr3() {
		return (timeEvents[2]) && (getChronoRunning()==true);
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr1_tr1() {
		return timeEvents[3];
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr0_tr0() {
		return timeEvents[4];
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr1_tr1() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmDisplayed_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmEdit_tr0_tr0() {
		return timeEvents[5];
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr1_tr1() {
		return timeEvents[6];
	}
	
	private boolean check_main_region_displayStates_Chrono_ChronoRunning_tr0_tr0() {
		return timeEvents[7];
	}
	
	private boolean check_main_region_displayStates_Chrono_ChronoRunning_tr1_tr1() {
		return stopChrono;
	}
	
	private boolean check_main_region_displayStates_Chrono_ChronoWaiting_tr0_tr0() {
		return startChrono;
	}
	
	private boolean check_main_region_displayStates_TimerUpdater_TimeUpdate_tr0_tr0() {
		return (timeEvents[8]) && (getTimeEditModeActive()==false);
	}
	
	private void effect_main_region_displayStates_IndigLo_LightOff_tr0() {
		exitSequence_main_region_displayStates_IndigLo_LightOff();
		enterSequence_main_region_displayStates_IndigLo_LightActive_default();
	}
	
	private void effect_main_region_displayStates_IndigLo_LightActive_tr0() {
		exitSequence_main_region_displayStates_IndigLo_LightActive();
		enterSequence_main_region_displayStates_IndigLo_LightDelay_default();
	}
	
	private void effect_main_region_displayStates_IndigLo_LightDelay_tr0() {
		exitSequence_main_region_displayStates_IndigLo_LightDelay();
		enterSequence_main_region_displayStates_IndigLo_LightOff_default();
	}
	
	private void effect_main_region_displayStates_IndigLo_LightDelay_tr1() {
		exitSequence_main_region_displayStates_IndigLo_LightDelay();
		enterSequence_main_region_displayStates_IndigLo_LightActive_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr1() {
		exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr2() {
		exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr3() {
		exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr1() {
		exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
		raiseStartChrono();
		
		enterSequence_main_region_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr2() {
		exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
		raiseStopChrono();
		
		enterSequence_main_region_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr3() {
		exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
		sCIDisplay.operationCallback.refreshChronoDisplay();
		
		enterSequence_main_region_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
		enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr1() {
		exitSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
		enterSequence_main_region_displayStates_MainDisplay_TimeEditingActive_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
		enterSequence_main_region_displayStates_MainDisplay_AlarmEdit_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr1() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
		enterSequence_main_region_displayStates_MainDisplay_AlarmDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmDisplayed_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmDisplayed();
		enterSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmEdit_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmEdit();
		enterSequence_main_region_displayStates_MainDisplay_AlarmDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr0() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
		enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr1() {
		exitSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
		enterSequence_main_region_displayStates_MainDisplay_AlarmEdit_default();
	}
	
	private void effect_main_region_displayStates_Chrono_ChronoRunning_tr0() {
		exitSequence_main_region_displayStates_Chrono_ChronoRunning();
		sCILogicUnit.operationCallback.increaseChronoByOne();
		
		enterSequence_main_region_displayStates_Chrono_ChronoRunning_default();
	}
	
	private void effect_main_region_displayStates_Chrono_ChronoRunning_tr1() {
		exitSequence_main_region_displayStates_Chrono_ChronoRunning();
		enterSequence_main_region_displayStates_Chrono_ChronoWaiting_default();
	}
	
	private void effect_main_region_displayStates_Chrono_ChronoWaiting_tr0() {
		exitSequence_main_region_displayStates_Chrono_ChronoWaiting();
		setChronoRunning(true);
		
		enterSequence_main_region_displayStates_Chrono_ChronoRunning_default();
	}
	
	private void effect_main_region_displayStates_TimerUpdater_TimeUpdate_tr0() {
		exitSequence_main_region_displayStates_TimerUpdater_TimeUpdate();
		sCILogicUnit.operationCallback.increaseTimeByOne();
		
		enterSequence_main_region_displayStates_TimerUpdater_TimeUpdate_default();
	}
	
	/* Entry action for state 'LightOff'. */
	private void entryAction_main_region_displayStates_IndigLo_LightOff() {
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Entry action for state 'LightActive'. */
	private void entryAction_main_region_displayStates_IndigLo_LightActive() {
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	/* Entry action for state 'LightDelay'. */
	private void entryAction_main_region_displayStates_IndigLo_LightDelay() {
		timer.setTimer(this, 0, 2*1000, false);
	}
	
	/* Entry action for state 'TimeDisplayed'. */
	private void entryAction_main_region_displayStates_MainDisplay_TimeDisplayed() {
		timer.setTimer(this, 1, 1*1000, false);
		
		sCIDisplay.operationCallback.refreshTimeDisplay();
		
		sCIDisplay.operationCallback.refreshDateDisplay();
	}
	
	/* Entry action for state 'ChronoDisplayed'. */
	private void entryAction_main_region_displayStates_MainDisplay_ChronoDisplayed() {
		timer.setTimer(this, 2, 10, false);
		
		sCIDisplay.operationCallback.refreshChronoDisplay();
	}
	
	/* Entry action for state 'TimeEditingEntryWait'. */
	private void entryAction_main_region_displayStates_MainDisplay_TimeEditingEntryWait() {
		timer.setTimer(this, 3, 2500, false);
	}
	
	/* Entry action for state 'TimeEditingActive'. */
	private void entryAction_main_region_displayStates_MainDisplay_TimeEditingActive() {
		sCILogicUnit.operationCallback.startTimeEditMode();
		
		setTimeEditModeActive(true);
	}
	
	/* Entry action for state 'AlarmInDisplayDelay'. */
	private void entryAction_main_region_displayStates_MainDisplay_AlarmInDisplayDelay() {
		timer.setTimer(this, 4, 1500, false);
	}
	
	/* Entry action for state 'AlarmEdit'. */
	private void entryAction_main_region_displayStates_MainDisplay_AlarmEdit() {
		timer.setTimer(this, 5, 5*1000, false);
	}
	
	/* Entry action for state 'AlarmOutDisplayDelay'. */
	private void entryAction_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay() {
		timer.setTimer(this, 6, 1500, false);
	}
	
	/* Entry action for state 'ChronoRunning'. */
	private void entryAction_main_region_displayStates_Chrono_ChronoRunning() {
		timer.setTimer(this, 7, 10, false);
	}
	
	/* Entry action for state 'ChronoWaiting'. */
	private void entryAction_main_region_displayStates_Chrono_ChronoWaiting() {
		setChronoRunning(false);
	}
	
	/* Entry action for state 'TimeUpdate'. */
	private void entryAction_main_region_displayStates_TimerUpdater_TimeUpdate() {
		timer.setTimer(this, 8, 1*1000, false);
	}
	
	/* Exit action for state 'LightDelay'. */
	private void exitAction_main_region_displayStates_IndigLo_LightDelay() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'TimeDisplayed'. */
	private void exitAction_main_region_displayStates_MainDisplay_TimeDisplayed() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'ChronoDisplayed'. */
	private void exitAction_main_region_displayStates_MainDisplay_ChronoDisplayed() {
		timer.unsetTimer(this, 2);
	}
	
	/* Exit action for state 'TimeEditingEntryWait'. */
	private void exitAction_main_region_displayStates_MainDisplay_TimeEditingEntryWait() {
		timer.unsetTimer(this, 3);
	}
	
	/* Exit action for state 'AlarmInDisplayDelay'. */
	private void exitAction_main_region_displayStates_MainDisplay_AlarmInDisplayDelay() {
		timer.unsetTimer(this, 4);
	}
	
	/* Exit action for state 'AlarmEdit'. */
	private void exitAction_main_region_displayStates_MainDisplay_AlarmEdit() {
		timer.unsetTimer(this, 5);
	}
	
	/* Exit action for state 'AlarmOutDisplayDelay'. */
	private void exitAction_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay() {
		timer.unsetTimer(this, 6);
	}
	
	/* Exit action for state 'ChronoRunning'. */
	private void exitAction_main_region_displayStates_Chrono_ChronoRunning() {
		timer.unsetTimer(this, 7);
	}
	
	/* Exit action for state 'TimeUpdate'. */
	private void exitAction_main_region_displayStates_TimerUpdater_TimeUpdate() {
		timer.unsetTimer(this, 8);
	}
	
	/* 'default' enter sequence for state displayStates */
	private void enterSequence_main_region_displayStates_default() {
		enterSequence_main_region_displayStates_IndigLo_default();
		enterSequence_main_region_displayStates_MainDisplay_default();
		enterSequence_main_region_displayStates_Chrono_default();
		enterSequence_main_region_displayStates_TimerUpdater_default();
	}
	
	/* 'default' enter sequence for state LightOff */
	private void enterSequence_main_region_displayStates_IndigLo_LightOff_default() {
		entryAction_main_region_displayStates_IndigLo_LightOff();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_displayStates_IndigLo_LightOff;
	}
	
	/* 'default' enter sequence for state LightActive */
	private void enterSequence_main_region_displayStates_IndigLo_LightActive_default() {
		entryAction_main_region_displayStates_IndigLo_LightActive();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_displayStates_IndigLo_LightActive;
	}
	
	/* 'default' enter sequence for state LightDelay */
	private void enterSequence_main_region_displayStates_IndigLo_LightDelay_default() {
		entryAction_main_region_displayStates_IndigLo_LightDelay();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_displayStates_IndigLo_LightDelay;
	}
	
	/* 'default' enter sequence for state TimeDisplayed */
	private void enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default() {
		entryAction_main_region_displayStates_MainDisplay_TimeDisplayed();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_TimeDisplayed;
	}
	
	/* 'default' enter sequence for state ChronoDisplayed */
	private void enterSequence_main_region_displayStates_MainDisplay_ChronoDisplayed_default() {
		entryAction_main_region_displayStates_MainDisplay_ChronoDisplayed();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_ChronoDisplayed;
	}
	
	/* 'default' enter sequence for state TimeEditingEntryWait */
	private void enterSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait_default() {
		entryAction_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_TimeEditingEntryWait;
	}
	
	/* 'default' enter sequence for state TimeEditingActive */
	private void enterSequence_main_region_displayStates_MainDisplay_TimeEditingActive_default() {
		entryAction_main_region_displayStates_MainDisplay_TimeEditingActive();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_TimeEditingActive;
	}
	
	/* 'default' enter sequence for state AlarmInDisplayDelay */
	private void enterSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_default() {
		entryAction_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_AlarmInDisplayDelay;
	}
	
	/* 'default' enter sequence for state AlarmDisplayed */
	private void enterSequence_main_region_displayStates_MainDisplay_AlarmDisplayed_default() {
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_AlarmDisplayed;
	}
	
	/* 'default' enter sequence for state AlarmEdit */
	private void enterSequence_main_region_displayStates_MainDisplay_AlarmEdit_default() {
		entryAction_main_region_displayStates_MainDisplay_AlarmEdit();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_AlarmEdit;
	}
	
	/* 'default' enter sequence for state AlarmOutDisplayDelay */
	private void enterSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_default() {
		entryAction_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_displayStates_MainDisplay_AlarmOutDisplayDelay;
	}
	
	/* 'default' enter sequence for state ChronoRunning */
	private void enterSequence_main_region_displayStates_Chrono_ChronoRunning_default() {
		entryAction_main_region_displayStates_Chrono_ChronoRunning();
		nextStateIndex = 2;
		stateVector[2] = State.main_region_displayStates_Chrono_ChronoRunning;
	}
	
	/* 'default' enter sequence for state ChronoWaiting */
	private void enterSequence_main_region_displayStates_Chrono_ChronoWaiting_default() {
		entryAction_main_region_displayStates_Chrono_ChronoWaiting();
		nextStateIndex = 2;
		stateVector[2] = State.main_region_displayStates_Chrono_ChronoWaiting;
	}
	
	/* 'default' enter sequence for state TimeUpdate */
	private void enterSequence_main_region_displayStates_TimerUpdater_TimeUpdate_default() {
		entryAction_main_region_displayStates_TimerUpdater_TimeUpdate();
		nextStateIndex = 3;
		stateVector[3] = State.main_region_displayStates_TimerUpdater_TimeUpdate;
	}
	
	/* 'default' enter sequence for region main region */
	private void enterSequence_main_region_default() {
		react_main_region__entry_Default();
	}
	
	/* 'default' enter sequence for region IndigLo */
	private void enterSequence_main_region_displayStates_IndigLo_default() {
		react_main_region_displayStates_IndigLo__entry_Default();
	}
	
	/* 'default' enter sequence for region MainDisplay */
	private void enterSequence_main_region_displayStates_MainDisplay_default() {
		react_main_region_displayStates_MainDisplay__entry_Default();
	}
	
	/* 'default' enter sequence for region Chrono */
	private void enterSequence_main_region_displayStates_Chrono_default() {
		react_main_region_displayStates_Chrono__entry_Default();
	}
	
	/* 'default' enter sequence for region TimerUpdater */
	private void enterSequence_main_region_displayStates_TimerUpdater_default() {
		react_main_region_displayStates_TimerUpdater__entry_Default();
	}
	
	/* Default exit sequence for state LightOff */
	private void exitSequence_main_region_displayStates_IndigLo_LightOff() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state LightActive */
	private void exitSequence_main_region_displayStates_IndigLo_LightActive() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state LightDelay */
	private void exitSequence_main_region_displayStates_IndigLo_LightDelay() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_displayStates_IndigLo_LightDelay();
	}
	
	/* Default exit sequence for state TimeDisplayed */
	private void exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_TimeDisplayed();
	}
	
	/* Default exit sequence for state ChronoDisplayed */
	private void exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_ChronoDisplayed();
	}
	
	/* Default exit sequence for state TimeEditingEntryWait */
	private void exitSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
	}
	
	/* Default exit sequence for state TimeEditingActive */
	private void exitSequence_main_region_displayStates_MainDisplay_TimeEditingActive() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state AlarmInDisplayDelay */
	private void exitSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
	}
	
	/* Default exit sequence for state AlarmDisplayed */
	private void exitSequence_main_region_displayStates_MainDisplay_AlarmDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
	}
	
	/* Default exit sequence for state AlarmEdit */
	private void exitSequence_main_region_displayStates_MainDisplay_AlarmEdit() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_AlarmEdit();
	}
	
	/* Default exit sequence for state AlarmOutDisplayDelay */
	private void exitSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
	}
	
	/* Default exit sequence for state ChronoRunning */
	private void exitSequence_main_region_displayStates_Chrono_ChronoRunning() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
		
		exitAction_main_region_displayStates_Chrono_ChronoRunning();
	}
	
	/* Default exit sequence for state ChronoWaiting */
	private void exitSequence_main_region_displayStates_Chrono_ChronoWaiting() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state TimeUpdate */
	private void exitSequence_main_region_displayStates_TimerUpdater_TimeUpdate() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
		
		exitAction_main_region_displayStates_TimerUpdater_TimeUpdate();
	}
	
	/* Default exit sequence for region main region */
	private void exitSequence_main_region() {
		switch (stateVector[0]) {
		case main_region_displayStates_IndigLo_LightOff:
			exitSequence_main_region_displayStates_IndigLo_LightOff();
			break;
		case main_region_displayStates_IndigLo_LightActive:
			exitSequence_main_region_displayStates_IndigLo_LightActive();
			break;
		case main_region_displayStates_IndigLo_LightDelay:
			exitSequence_main_region_displayStates_IndigLo_LightDelay();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_region_displayStates_MainDisplay_TimeDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
			break;
		case main_region_displayStates_MainDisplay_ChronoDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
			break;
		case main_region_displayStates_MainDisplay_TimeEditingEntryWait:
			exitSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
			break;
		case main_region_displayStates_MainDisplay_TimeEditingActive:
			exitSequence_main_region_displayStates_MainDisplay_TimeEditingActive();
			break;
		case main_region_displayStates_MainDisplay_AlarmInDisplayDelay:
			exitSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
			break;
		case main_region_displayStates_MainDisplay_AlarmDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_AlarmDisplayed();
			break;
		case main_region_displayStates_MainDisplay_AlarmEdit:
			exitSequence_main_region_displayStates_MainDisplay_AlarmEdit();
			break;
		case main_region_displayStates_MainDisplay_AlarmOutDisplayDelay:
			exitSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_region_displayStates_Chrono_ChronoRunning:
			exitSequence_main_region_displayStates_Chrono_ChronoRunning();
			break;
		case main_region_displayStates_Chrono_ChronoWaiting:
			exitSequence_main_region_displayStates_Chrono_ChronoWaiting();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_region_displayStates_TimerUpdater_TimeUpdate:
			exitSequence_main_region_displayStates_TimerUpdater_TimeUpdate();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region IndigLo */
	private void exitSequence_main_region_displayStates_IndigLo() {
		switch (stateVector[0]) {
		case main_region_displayStates_IndigLo_LightOff:
			exitSequence_main_region_displayStates_IndigLo_LightOff();
			break;
		case main_region_displayStates_IndigLo_LightActive:
			exitSequence_main_region_displayStates_IndigLo_LightActive();
			break;
		case main_region_displayStates_IndigLo_LightDelay:
			exitSequence_main_region_displayStates_IndigLo_LightDelay();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region MainDisplay */
	private void exitSequence_main_region_displayStates_MainDisplay() {
		switch (stateVector[1]) {
		case main_region_displayStates_MainDisplay_TimeDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_TimeDisplayed();
			break;
		case main_region_displayStates_MainDisplay_ChronoDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_ChronoDisplayed();
			break;
		case main_region_displayStates_MainDisplay_TimeEditingEntryWait:
			exitSequence_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
			break;
		case main_region_displayStates_MainDisplay_TimeEditingActive:
			exitSequence_main_region_displayStates_MainDisplay_TimeEditingActive();
			break;
		case main_region_displayStates_MainDisplay_AlarmInDisplayDelay:
			exitSequence_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
			break;
		case main_region_displayStates_MainDisplay_AlarmDisplayed:
			exitSequence_main_region_displayStates_MainDisplay_AlarmDisplayed();
			break;
		case main_region_displayStates_MainDisplay_AlarmEdit:
			exitSequence_main_region_displayStates_MainDisplay_AlarmEdit();
			break;
		case main_region_displayStates_MainDisplay_AlarmOutDisplayDelay:
			exitSequence_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Chrono */
	private void exitSequence_main_region_displayStates_Chrono() {
		switch (stateVector[2]) {
		case main_region_displayStates_Chrono_ChronoRunning:
			exitSequence_main_region_displayStates_Chrono_ChronoRunning();
			break;
		case main_region_displayStates_Chrono_ChronoWaiting:
			exitSequence_main_region_displayStates_Chrono_ChronoWaiting();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region TimerUpdater */
	private void exitSequence_main_region_displayStates_TimerUpdater() {
		switch (stateVector[3]) {
		case main_region_displayStates_TimerUpdater_TimeUpdate:
			exitSequence_main_region_displayStates_TimerUpdater_TimeUpdate();
			break;
		default:
			break;
		}
	}
	
	/* The reactions of state LightOff. */
	private void react_main_region_displayStates_IndigLo_LightOff() {
		if (check_main_region_displayStates_IndigLo_LightOff_tr0_tr0()) {
			effect_main_region_displayStates_IndigLo_LightOff_tr0();
		}
	}
	
	/* The reactions of state LightActive. */
	private void react_main_region_displayStates_IndigLo_LightActive() {
		if (check_main_region_displayStates_IndigLo_LightActive_tr0_tr0()) {
			effect_main_region_displayStates_IndigLo_LightActive_tr0();
		}
	}
	
	/* The reactions of state LightDelay. */
	private void react_main_region_displayStates_IndigLo_LightDelay() {
		if (check_main_region_displayStates_IndigLo_LightDelay_tr0_tr0()) {
			effect_main_region_displayStates_IndigLo_LightDelay_tr0();
		} else {
			if (check_main_region_displayStates_IndigLo_LightDelay_tr1_tr1()) {
				effect_main_region_displayStates_IndigLo_LightDelay_tr1();
			}
		}
	}
	
	/* The reactions of state TimeDisplayed. */
	private void react_main_region_displayStates_MainDisplay_TimeDisplayed() {
		if (check_main_region_displayStates_MainDisplay_TimeDisplayed_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr0();
		} else {
			if (check_main_region_displayStates_MainDisplay_TimeDisplayed_tr1_tr1()) {
				effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr1();
			} else {
				if (check_main_region_displayStates_MainDisplay_TimeDisplayed_tr2_tr2()) {
					effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr2();
				} else {
					if (check_main_region_displayStates_MainDisplay_TimeDisplayed_tr3_tr3()) {
						effect_main_region_displayStates_MainDisplay_TimeDisplayed_tr3();
					}
				}
			}
		}
	}
	
	/* The reactions of state ChronoDisplayed. */
	private void react_main_region_displayStates_MainDisplay_ChronoDisplayed() {
		if (check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr0();
		} else {
			if (check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr1_tr1()) {
				effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr1();
			} else {
				if (check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr2_tr2()) {
					effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr2();
				} else {
					if (check_main_region_displayStates_MainDisplay_ChronoDisplayed_tr3_tr3()) {
						effect_main_region_displayStates_MainDisplay_ChronoDisplayed_tr3();
					}
				}
			}
		}
	}
	
	/* The reactions of state TimeEditingEntryWait. */
	private void react_main_region_displayStates_MainDisplay_TimeEditingEntryWait() {
		if (check_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr0();
		} else {
			if (check_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr1_tr1()) {
				effect_main_region_displayStates_MainDisplay_TimeEditingEntryWait_tr1();
			}
		}
	}
	
	/* The reactions of state TimeEditingActive. */
	private void react_main_region_displayStates_MainDisplay_TimeEditingActive() {
	}
	
	/* The reactions of state AlarmInDisplayDelay. */
	private void react_main_region_displayStates_MainDisplay_AlarmInDisplayDelay() {
		if (check_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr0();
		} else {
			if (check_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr1_tr1()) {
				effect_main_region_displayStates_MainDisplay_AlarmInDisplayDelay_tr1();
			}
		}
	}
	
	/* The reactions of state AlarmDisplayed. */
	private void react_main_region_displayStates_MainDisplay_AlarmDisplayed() {
		if (check_main_region_displayStates_MainDisplay_AlarmDisplayed_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_AlarmDisplayed_tr0();
		}
	}
	
	/* The reactions of state AlarmEdit. */
	private void react_main_region_displayStates_MainDisplay_AlarmEdit() {
		if (check_main_region_displayStates_MainDisplay_AlarmEdit_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_AlarmEdit_tr0();
		}
	}
	
	/* The reactions of state AlarmOutDisplayDelay. */
	private void react_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay() {
		if (check_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr0_tr0()) {
			effect_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr0();
		} else {
			if (check_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr1_tr1()) {
				effect_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay_tr1();
			}
		}
	}
	
	/* The reactions of state ChronoRunning. */
	private void react_main_region_displayStates_Chrono_ChronoRunning() {
		if (check_main_region_displayStates_Chrono_ChronoRunning_tr0_tr0()) {
			effect_main_region_displayStates_Chrono_ChronoRunning_tr0();
		} else {
			if (check_main_region_displayStates_Chrono_ChronoRunning_tr1_tr1()) {
				effect_main_region_displayStates_Chrono_ChronoRunning_tr1();
			}
		}
	}
	
	/* The reactions of state ChronoWaiting. */
	private void react_main_region_displayStates_Chrono_ChronoWaiting() {
		if (check_main_region_displayStates_Chrono_ChronoWaiting_tr0_tr0()) {
			effect_main_region_displayStates_Chrono_ChronoWaiting_tr0();
		}
	}
	
	/* The reactions of state TimeUpdate. */
	private void react_main_region_displayStates_TimerUpdater_TimeUpdate() {
		if (check_main_region_displayStates_TimerUpdater_TimeUpdate_tr0_tr0()) {
			effect_main_region_displayStates_TimerUpdater_TimeUpdate_tr0();
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region__entry_Default() {
		enterSequence_main_region_displayStates_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_displayStates_IndigLo__entry_Default() {
		enterSequence_main_region_displayStates_IndigLo_LightOff_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_displayStates_MainDisplay__entry_Default() {
		enterSequence_main_region_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_displayStates_Chrono__entry_Default() {
		enterSequence_main_region_displayStates_Chrono_ChronoWaiting_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_displayStates_TimerUpdater__entry_Default() {
		enterSequence_main_region_displayStates_TimerUpdater_TimeUpdate_default();
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_region_displayStates_IndigLo_LightOff:
				react_main_region_displayStates_IndigLo_LightOff();
				break;
			case main_region_displayStates_IndigLo_LightActive:
				react_main_region_displayStates_IndigLo_LightActive();
				break;
			case main_region_displayStates_IndigLo_LightDelay:
				react_main_region_displayStates_IndigLo_LightDelay();
				break;
			case main_region_displayStates_MainDisplay_TimeDisplayed:
				react_main_region_displayStates_MainDisplay_TimeDisplayed();
				break;
			case main_region_displayStates_MainDisplay_ChronoDisplayed:
				react_main_region_displayStates_MainDisplay_ChronoDisplayed();
				break;
			case main_region_displayStates_MainDisplay_TimeEditingEntryWait:
				react_main_region_displayStates_MainDisplay_TimeEditingEntryWait();
				break;
			case main_region_displayStates_MainDisplay_TimeEditingActive:
				react_main_region_displayStates_MainDisplay_TimeEditingActive();
				break;
			case main_region_displayStates_MainDisplay_AlarmInDisplayDelay:
				react_main_region_displayStates_MainDisplay_AlarmInDisplayDelay();
				break;
			case main_region_displayStates_MainDisplay_AlarmDisplayed:
				react_main_region_displayStates_MainDisplay_AlarmDisplayed();
				break;
			case main_region_displayStates_MainDisplay_AlarmEdit:
				react_main_region_displayStates_MainDisplay_AlarmEdit();
				break;
			case main_region_displayStates_MainDisplay_AlarmOutDisplayDelay:
				react_main_region_displayStates_MainDisplay_AlarmOutDisplayDelay();
				break;
			case main_region_displayStates_Chrono_ChronoRunning:
				react_main_region_displayStates_Chrono_ChronoRunning();
				break;
			case main_region_displayStates_Chrono_ChronoWaiting:
				react_main_region_displayStates_Chrono_ChronoWaiting();
				break;
			case main_region_displayStates_TimerUpdater_TimeUpdate:
				react_main_region_displayStates_TimerUpdater_TimeUpdate();
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}
