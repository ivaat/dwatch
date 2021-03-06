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
		main_region_masterState,
		main_region_masterState_Watch_displayStates,
		main_region_masterState_Watch_displayStates_IndigLo_LightOff,
		main_region_masterState_Watch_displayStates_IndigLo_LightActive,
		main_region_masterState_Watch_displayStates_IndigLo_LightDelay,
		main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed,
		main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed,
		main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait,
		main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive,
		main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay,
		main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed,
		main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit,
		main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay,
		main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait,
		main_region_masterState_Watch_displayStates_Chrono_ChronoRunning,
		main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting,
		main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate,
		main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown,
		main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden,
		main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate,
		main_region_masterState_Alarm_AlarmOff,
		main_region_masterState_Alarm_AlarmOn,
		main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn,
		main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff,
		$NullState$
	};
	
	private final State[] stateVector = new State[6];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[18];
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
	
	private boolean alarmEditModeActive;
	
	protected void setAlarmEditModeActive(boolean value) {
		alarmEditModeActive = value;
	}
	
	protected boolean getAlarmEditModeActive() {
		return alarmEditModeActive;
	}
	
	private String alarmTime;
	
	protected void setAlarmTime(String value) {
		alarmTime = value;
	}
	
	protected String getAlarmTime() {
		return alarmTime;
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
		for (int i = 0; i < 6; i++) {
			stateVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		setChronoRunning(false);
		
		setTimeEditModeActive(false);
		
		setAlarmEditModeActive(false);
		
		setAlarmTime("");
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
		return stateVector[0] != State.$NullState$||stateVector[1] != State.$NullState$||stateVector[2] != State.$NullState$||stateVector[3] != State.$NullState$||stateVector[4] != State.$NullState$||stateVector[5] != State.$NullState$;
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
		case main_region_masterState:
			return stateVector[0].ordinal() >= State.
					main_region_masterState.ordinal()&& stateVector[0].ordinal() <= State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff.ordinal();
		case main_region_masterState_Watch_displayStates:
			return stateVector[0].ordinal() >= State.
					main_region_masterState_Watch_displayStates.ordinal()&& stateVector[0].ordinal() <= State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate.ordinal();
		case main_region_masterState_Watch_displayStates_IndigLo_LightOff:
			return stateVector[0] == State.main_region_masterState_Watch_displayStates_IndigLo_LightOff;
		case main_region_masterState_Watch_displayStates_IndigLo_LightActive:
			return stateVector[0] == State.main_region_masterState_Watch_displayStates_IndigLo_LightActive;
		case main_region_masterState_Watch_displayStates_IndigLo_LightDelay:
			return stateVector[0] == State.main_region_masterState_Watch_displayStates_IndigLo_LightDelay;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed;
		case main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait:
			return stateVector[1] == State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait;
		case main_region_masterState_Watch_displayStates_Chrono_ChronoRunning:
			return stateVector[2] == State.main_region_masterState_Watch_displayStates_Chrono_ChronoRunning;
		case main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting:
			return stateVector[2] == State.main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting;
		case main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate:
			return stateVector[3] == State.main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown:
			return stateVector[4] == State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden:
			return stateVector[4] == State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate:
			return stateVector[4] == State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate;
		case main_region_masterState_Alarm_AlarmOff:
			return stateVector[5] == State.main_region_masterState_Alarm_AlarmOff;
		case main_region_masterState_Alarm_AlarmOn:
			return stateVector[5].ordinal() >= State.
					main_region_masterState_Alarm_AlarmOn.ordinal()&& stateVector[5].ordinal() <= State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff.ordinal();
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn:
			return stateVector[5] == State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff:
			return stateVector[5] == State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff;
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
	
	private boolean check_main_region_masterState_Watch_displayStates_IndigLo_LightOff_tr0_tr0() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_IndigLo_LightActive_tr0_tr0() {
		return sCIButtons.topRightReleased;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr0_tr0() {
		return timeEvents[0];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr1_tr1() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr1_tr1() {
		return (timeEvents[1]) && (getTimeEditModeActive()==false);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr2_tr2() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr3_tr3() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr1_tr1() {
		return (sCIButtons.bottomRightPressed) && (getChronoRunning()==false);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr2_tr2() {
		return (sCIButtons.bottomRightPressed) && (getChronoRunning()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr3_tr3() {
		return (timeEvents[2]) && (getChronoRunning()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr4_tr4() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr1_tr1() {
		return timeEvents[3];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr1_tr1() {
		return timeEvents[4];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr2_tr2() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr0_tr0() {
		return timeEvents[5];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr1_tr1() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_tr0_tr0() {
		return timeEvents[6];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr0_tr0() {
		return timeEvents[7];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr2_tr2() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr0_tr0() {
		return (sCIButtons.bottomLeftReleased) && (getAlarmEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr1_tr1() {
		return timeEvents[8];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr2_tr2() {
		return (sCIButtons.bottomLeftReleased) && (getTimeEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr0_tr0() {
		return (timeEvents[9]) && (getTimeEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr1_tr1() {
		return (sCIButtons.bottomRightReleased) && (getTimeEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr2_tr2() {
		return (timeEvents[10]) && (getAlarmEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr3_tr3() {
		return (sCIButtons.bottomRightReleased) && (getAlarmEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr0_tr0() {
		return timeEvents[11];
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr1_tr1() {
		return stopChrono;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_tr0_tr0() {
		return startChrono;
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_tr0_tr0() {
		return (timeEvents[12]) && (getTimeEditModeActive()==false);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_tr0_tr0() {
		return (timeEvents[13]) && (getTimeEditModeActive()==true || getAlarmEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_tr0_tr0() {
		return (timeEvents[14]) && (getTimeEditModeActive()==true || getAlarmEditModeActive()==true);
	}
	
	private boolean check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_tr0_tr0() {
		return getTimeEditModeActive()==true || getAlarmEditModeActive()==true;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOff_tr0_tr0() {
		return sCILogicUnit.startAlarm;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_tr0_tr0() {
		return timeEvents[15];
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_tr1_tr1() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_tr2_tr2() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_tr3_tr3() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_tr4_tr4() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_tr0_tr0() {
		return timeEvents[16];
	}
	
	private boolean check_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_tr0_tr0() {
		return timeEvents[17];
	}
	
	private void effect_main_region_masterState_Watch_displayStates_IndigLo_LightOff_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_IndigLo_LightActive_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr3() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		raiseStartChrono();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		raiseStopChrono();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr3() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		sCIDisplay.operationCallback.refreshChronoDisplay();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr4() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		sCILogicUnit.operationCallback.resetChrono();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
		sCILogicUnit.operationCallback.startTimeEditMode();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
		sCILogicUnit.operationCallback.startAlarmEditMode();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
		sCILogicUnit.operationCallback.increaseSelection();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
		sCILogicUnit.operationCallback.increaseSelection();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
		sCILogicUnit.operationCallback.increaseSelection();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
		sCILogicUnit.operationCallback.selectNext();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr2() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr3() {
		exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
		sCILogicUnit.operationCallback.selectNext();
		
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
		sCILogicUnit.operationCallback.increaseChronoByOne();
		
		enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr1() {
		exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
		enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
		setChronoRunning(true);
		
		enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
		sCILogicUnit.operationCallback.increaseTimeByOne();
		
		enterSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
		enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
		enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_default();
	}
	
	private void effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_tr0() {
		exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate();
		enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOff_tr0() {
		exitSequence_main_region_masterState_Alarm_AlarmOff();
		enterSequence_main_region_masterState_Alarm_AlarmOn_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_tr0() {
		exitSequence_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_tr1() {
		exitSequence_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_tr2() {
		exitSequence_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_tr3() {
		exitSequence_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_tr4() {
		exitSequence_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_tr0() {
		exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
		enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_default();
	}
	
	private void effect_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_tr0() {
		exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
		enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_default();
	}
	
	/* Entry action for state 'LightOff'. */
	private void entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightOff() {
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Entry action for state 'LightActive'. */
	private void entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightActive() {
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	/* Entry action for state 'LightDelay'. */
	private void entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightDelay() {
		timer.setTimer(this, 0, 2*1000, false);
	}
	
	/* Entry action for state 'TimeDisplayed'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed() {
		timer.setTimer(this, 1, 1*1000, false);
		
		sCIDisplay.operationCallback.refreshTimeDisplay();
		
		sCIDisplay.operationCallback.refreshDateDisplay();
		
		setTimeEditModeActive(false);
	}
	
	/* Entry action for state 'ChronoDisplayed'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed() {
		timer.setTimer(this, 2, 10, false);
		
		sCIDisplay.operationCallback.refreshChronoDisplay();
	}
	
	/* Entry action for state 'TimeEditingEntryWait'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait() {
		timer.setTimer(this, 3, 1500, false);
	}
	
	/* Entry action for state 'TimeEditingActive'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive() {
		timer.setTimer(this, 4, 5*1000, false);
		
		setTimeEditModeActive(true);
	}
	
	/* Entry action for state 'AlarmInDisplayDelay'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay() {
		timer.setTimer(this, 5, 1500, false);
	}
	
	/* Entry action for state 'AlarmDisplayed'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed() {
		timer.setTimer(this, 6, 1*1000, false);
		
		sCIDisplay.operationCallback.refreshAlarmDisplay();
		
		sCILogicUnit.operationCallback.setAlarm();
		
		setAlarmEditModeActive(false);
	}
	
	/* Entry action for state 'AlarmEdit'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit() {
		timer.setTimer(this, 7, 5*1000, false);
		
		setAlarmEditModeActive(true);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'TimeEditingDelay'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay() {
		timer.setTimer(this, 8, 300, false);
	}
	
	/* Entry action for state 'TimeEditingOutWait'. */
	private void entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait() {
		timer.setTimer(this, 9, 2*1000, false);
		
		timer.setTimer(this, 10, 2*1000, false);
	}
	
	/* Entry action for state 'ChronoRunning'. */
	private void entryAction_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning() {
		timer.setTimer(this, 11, 10, false);
	}
	
	/* Entry action for state 'ChronoWaiting'. */
	private void entryAction_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting() {
		setChronoRunning(false);
	}
	
	/* Entry action for state 'TimeUpdate'. */
	private void entryAction_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate() {
		timer.setTimer(this, 12, 1*1000, false);
	}
	
	/* Entry action for state 'SelectionShown'. */
	private void entryAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown() {
		timer.setTimer(this, 13, 300, false);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'SelectionHidden'. */
	private void entryAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden() {
		timer.setTimer(this, 14, 300, false);
		
		sCIDisplay.operationCallback.hideSelection();
	}
	
	/* Entry action for state 'AlarmOn'. */
	private void entryAction_main_region_masterState_Alarm_AlarmOn() {
		timer.setTimer(this, 15, 4*1000, false);
	}
	
	/* Entry action for state 'AlarmBlinkOn'. */
	private void entryAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn() {
		timer.setTimer(this, 16, 500, false);
		
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	/* Entry action for state 'AlarmBlinkOff'. */
	private void entryAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff() {
		timer.setTimer(this, 17, 500, false);
		
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	/* Exit action for state 'LightDelay'. */
	private void exitAction_main_region_masterState_Watch_displayStates_IndigLo_LightDelay() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'TimeDisplayed'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed() {
		timer.unsetTimer(this, 1);
	}
	
	/* Exit action for state 'ChronoDisplayed'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed() {
		timer.unsetTimer(this, 2);
	}
	
	/* Exit action for state 'TimeEditingEntryWait'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait() {
		timer.unsetTimer(this, 3);
	}
	
	/* Exit action for state 'TimeEditingActive'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive() {
		timer.unsetTimer(this, 4);
	}
	
	/* Exit action for state 'AlarmInDisplayDelay'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay() {
		timer.unsetTimer(this, 5);
	}
	
	/* Exit action for state 'AlarmDisplayed'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed() {
		timer.unsetTimer(this, 6);
	}
	
	/* Exit action for state 'AlarmEdit'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit() {
		timer.unsetTimer(this, 7);
	}
	
	/* Exit action for state 'TimeEditingDelay'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay() {
		timer.unsetTimer(this, 8);
	}
	
	/* Exit action for state 'TimeEditingOutWait'. */
	private void exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait() {
		timer.unsetTimer(this, 9);
		
		timer.unsetTimer(this, 10);
	}
	
	/* Exit action for state 'ChronoRunning'. */
	private void exitAction_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning() {
		timer.unsetTimer(this, 11);
	}
	
	/* Exit action for state 'TimeUpdate'. */
	private void exitAction_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate() {
		timer.unsetTimer(this, 12);
	}
	
	/* Exit action for state 'SelectionShown'. */
	private void exitAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown() {
		timer.unsetTimer(this, 13);
	}
	
	/* Exit action for state 'SelectionHidden'. */
	private void exitAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden() {
		timer.unsetTimer(this, 14);
	}
	
	/* Exit action for state 'AlarmOn'. */
	private void exitAction_main_region_masterState_Alarm_AlarmOn() {
		timer.unsetTimer(this, 15);
	}
	
	/* Exit action for state 'AlarmBlinkOn'. */
	private void exitAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn() {
		timer.unsetTimer(this, 16);
	}
	
	/* Exit action for state 'AlarmBlinkOff'. */
	private void exitAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff() {
		timer.unsetTimer(this, 17);
	}
	
	/* 'default' enter sequence for state masterState */
	private void enterSequence_main_region_masterState_default() {
		enterSequence_main_region_masterState_Watch_default();
		enterSequence_main_region_masterState_Alarm_default();
	}
	
	/* 'default' enter sequence for state displayStates */
	private void enterSequence_main_region_masterState_Watch_displayStates_default() {
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_default();
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_default();
		enterSequence_main_region_masterState_Watch_displayStates_Chrono_default();
		enterSequence_main_region_masterState_Watch_displayStates_TimerUpdater_default();
		enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_default();
	}
	
	/* 'default' enter sequence for state LightOff */
	private void enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff_default() {
		entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_masterState_Watch_displayStates_IndigLo_LightOff;
	}
	
	/* 'default' enter sequence for state LightActive */
	private void enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive_default() {
		entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_masterState_Watch_displayStates_IndigLo_LightActive;
	}
	
	/* 'default' enter sequence for state LightDelay */
	private void enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_default() {
		entryAction_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_masterState_Watch_displayStates_IndigLo_LightDelay;
	}
	
	/* 'default' enter sequence for state TimeDisplayed */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed;
	}
	
	/* 'default' enter sequence for state ChronoDisplayed */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed;
	}
	
	/* 'default' enter sequence for state TimeEditingEntryWait */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait;
	}
	
	/* 'default' enter sequence for state TimeEditingActive */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive;
	}
	
	/* 'default' enter sequence for state AlarmInDisplayDelay */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay;
	}
	
	/* 'default' enter sequence for state AlarmDisplayed */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed;
	}
	
	/* 'default' enter sequence for state AlarmEdit */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit;
	}
	
	/* 'default' enter sequence for state TimeEditingDelay */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay;
	}
	
	/* 'default' enter sequence for state TimeEditingOutWait */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_default() {
		entryAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
		nextStateIndex = 1;
		stateVector[1] = State.main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait;
	}
	
	/* 'default' enter sequence for state ChronoRunning */
	private void enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_default() {
		entryAction_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
		nextStateIndex = 2;
		stateVector[2] = State.main_region_masterState_Watch_displayStates_Chrono_ChronoRunning;
	}
	
	/* 'default' enter sequence for state ChronoWaiting */
	private void enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_default() {
		entryAction_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
		nextStateIndex = 2;
		stateVector[2] = State.main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting;
	}
	
	/* 'default' enter sequence for state TimeUpdate */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_default() {
		entryAction_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
		nextStateIndex = 3;
		stateVector[3] = State.main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate;
	}
	
	/* 'default' enter sequence for state SelectionShown */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_default() {
		entryAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
		nextStateIndex = 4;
		stateVector[4] = State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown;
	}
	
	/* 'default' enter sequence for state SelectionHidden */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_default() {
		entryAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
		nextStateIndex = 4;
		stateVector[4] = State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden;
	}
	
	/* 'default' enter sequence for state Intermediate */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_default() {
		nextStateIndex = 4;
		stateVector[4] = State.main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate;
	}
	
	/* 'default' enter sequence for state AlarmOff */
	private void enterSequence_main_region_masterState_Alarm_AlarmOff_default() {
		nextStateIndex = 5;
		stateVector[5] = State.main_region_masterState_Alarm_AlarmOff;
	}
	
	/* 'default' enter sequence for state AlarmOn */
	private void enterSequence_main_region_masterState_Alarm_AlarmOn_default() {
		entryAction_main_region_masterState_Alarm_AlarmOn();
		enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_default();
	}
	
	/* 'default' enter sequence for state AlarmBlinkOn */
	private void enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_default() {
		entryAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn;
	}
	
	/* 'default' enter sequence for state AlarmBlinkOff */
	private void enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_default() {
		entryAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
		nextStateIndex = 5;
		stateVector[5] = State.main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff;
	}
	
	/* 'default' enter sequence for region main region */
	private void enterSequence_main_region_default() {
		react_main_region__entry_Default();
	}
	
	/* 'default' enter sequence for region Watch */
	private void enterSequence_main_region_masterState_Watch_default() {
		react_main_region_masterState_Watch__entry_Default();
	}
	
	/* 'default' enter sequence for region IndigLo */
	private void enterSequence_main_region_masterState_Watch_displayStates_IndigLo_default() {
		react_main_region_masterState_Watch_displayStates_IndigLo__entry_Default();
	}
	
	/* 'default' enter sequence for region MainDisplay */
	private void enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_default() {
		react_main_region_masterState_Watch_displayStates_MainDisplay__entry_Default();
	}
	
	/* 'default' enter sequence for region Chrono */
	private void enterSequence_main_region_masterState_Watch_displayStates_Chrono_default() {
		react_main_region_masterState_Watch_displayStates_Chrono__entry_Default();
	}
	
	/* 'default' enter sequence for region TimerUpdater */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimerUpdater_default() {
		react_main_region_masterState_Watch_displayStates_TimerUpdater__entry_Default();
	}
	
	/* 'default' enter sequence for region TimeSelectionAnimation */
	private void enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_default() {
		react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation__entry_Default();
	}
	
	/* 'default' enter sequence for region Alarm */
	private void enterSequence_main_region_masterState_Alarm_default() {
		react_main_region_masterState_Alarm__entry_Default();
	}
	
	/* 'default' enter sequence for region AlarmBlinking */
	private void enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_default() {
		react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking__entry_Default();
	}
	
	/* Default exit sequence for state LightOff */
	private void exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state LightActive */
	private void exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state LightDelay */
	private void exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
	}
	
	/* Default exit sequence for state TimeDisplayed */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
	}
	
	/* Default exit sequence for state ChronoDisplayed */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
	}
	
	/* Default exit sequence for state TimeEditingEntryWait */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
	}
	
	/* Default exit sequence for state TimeEditingActive */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
	}
	
	/* Default exit sequence for state AlarmInDisplayDelay */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
	}
	
	/* Default exit sequence for state AlarmDisplayed */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
	}
	
	/* Default exit sequence for state AlarmEdit */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
	}
	
	/* Default exit sequence for state TimeEditingDelay */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
	}
	
	/* Default exit sequence for state TimeEditingOutWait */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait() {
		nextStateIndex = 1;
		stateVector[1] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
	}
	
	/* Default exit sequence for state ChronoRunning */
	private void exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
	}
	
	/* Default exit sequence for state ChronoWaiting */
	private void exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting() {
		nextStateIndex = 2;
		stateVector[2] = State.$NullState$;
	}
	
	/* Default exit sequence for state TimeUpdate */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate() {
		nextStateIndex = 3;
		stateVector[3] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
	}
	
	/* Default exit sequence for state SelectionShown */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
	}
	
	/* Default exit sequence for state SelectionHidden */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
		
		exitAction_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
	}
	
	/* Default exit sequence for state Intermediate */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate() {
		nextStateIndex = 4;
		stateVector[4] = State.$NullState$;
	}
	
	/* Default exit sequence for state AlarmOff */
	private void exitSequence_main_region_masterState_Alarm_AlarmOff() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
	}
	
	/* Default exit sequence for state AlarmOn */
	private void exitSequence_main_region_masterState_Alarm_AlarmOn() {
		exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking();
		exitAction_main_region_masterState_Alarm_AlarmOn();
	}
	
	/* Default exit sequence for state AlarmBlinkOn */
	private void exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
		
		exitAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
	}
	
	/* Default exit sequence for state AlarmBlinkOff */
	private void exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff() {
		nextStateIndex = 5;
		stateVector[5] = State.$NullState$;
		
		exitAction_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
	}
	
	/* Default exit sequence for region main region */
	private void exitSequence_main_region() {
		switch (stateVector[0]) {
		case main_region_masterState_Watch_displayStates_IndigLo_LightOff:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightActive:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightDelay:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_region_masterState_Watch_displayStates_Chrono_ChronoRunning:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
			break;
		case main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate:
			exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
			break;
		default:
			break;
		}
		
		switch (stateVector[4]) {
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate();
			break;
		default:
			break;
		}
		
		switch (stateVector[5]) {
		case main_region_masterState_Alarm_AlarmOff:
			exitSequence_main_region_masterState_Alarm_AlarmOff();
			break;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
			exitAction_main_region_masterState_Alarm_AlarmOn();
			break;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
			exitAction_main_region_masterState_Alarm_AlarmOn();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Watch */
	private void exitSequence_main_region_masterState_Watch() {
		switch (stateVector[0]) {
		case main_region_masterState_Watch_displayStates_IndigLo_LightOff:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightActive:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightDelay:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
			break;
		default:
			break;
		}
		
		switch (stateVector[1]) {
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
			break;
		default:
			break;
		}
		
		switch (stateVector[2]) {
		case main_region_masterState_Watch_displayStates_Chrono_ChronoRunning:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
			break;
		case main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
			break;
		default:
			break;
		}
		
		switch (stateVector[3]) {
		case main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate:
			exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
			break;
		default:
			break;
		}
		
		switch (stateVector[4]) {
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region IndigLo */
	private void exitSequence_main_region_masterState_Watch_displayStates_IndigLo() {
		switch (stateVector[0]) {
		case main_region_masterState_Watch_displayStates_IndigLo_LightOff:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightActive:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
			break;
		case main_region_masterState_Watch_displayStates_IndigLo_LightDelay:
			exitSequence_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region MainDisplay */
	private void exitSequence_main_region_masterState_Watch_displayStates_MainDisplay() {
		switch (stateVector[1]) {
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
			break;
		case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait:
			exitSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Chrono */
	private void exitSequence_main_region_masterState_Watch_displayStates_Chrono() {
		switch (stateVector[2]) {
		case main_region_masterState_Watch_displayStates_Chrono_ChronoRunning:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
			break;
		case main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting:
			exitSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region TimerUpdater */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater() {
		switch (stateVector[3]) {
		case main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate:
			exitSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region TimeSelectionAnimation */
	private void exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation() {
		switch (stateVector[4]) {
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
			break;
		case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate:
			exitSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region Alarm */
	private void exitSequence_main_region_masterState_Alarm() {
		switch (stateVector[5]) {
		case main_region_masterState_Alarm_AlarmOff:
			exitSequence_main_region_masterState_Alarm_AlarmOff();
			break;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
			exitAction_main_region_masterState_Alarm_AlarmOn();
			break;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
			exitAction_main_region_masterState_Alarm_AlarmOn();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region AlarmBlinking */
	private void exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking() {
		switch (stateVector[5]) {
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
			break;
		case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff:
			exitSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
			break;
		default:
			break;
		}
	}
	
	/* The reactions of state LightOff. */
	private void react_main_region_masterState_Watch_displayStates_IndigLo_LightOff() {
		if (check_main_region_masterState_Watch_displayStates_IndigLo_LightOff_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_IndigLo_LightOff_tr0();
		}
	}
	
	/* The reactions of state LightActive. */
	private void react_main_region_masterState_Watch_displayStates_IndigLo_LightActive() {
		if (check_main_region_masterState_Watch_displayStates_IndigLo_LightActive_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_IndigLo_LightActive_tr0();
		}
	}
	
	/* The reactions of state LightDelay. */
	private void react_main_region_masterState_Watch_displayStates_IndigLo_LightDelay() {
		if (check_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_IndigLo_LightDelay_tr1();
			}
		}
	}
	
	/* The reactions of state TimeDisplayed. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr2();
				} else {
					if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr3_tr3()) {
						effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_tr3();
					}
				}
			}
		}
	}
	
	/* The reactions of state ChronoDisplayed. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr2();
				} else {
					if (check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr3_tr3()) {
						effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr3();
					} else {
						if (check_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr4_tr4()) {
							effect_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed_tr4();
						}
					}
				}
			}
		}
	}
	
	/* The reactions of state TimeEditingEntryWait. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait_tr1();
			}
		}
	}
	
	/* The reactions of state TimeEditingActive. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive_tr2();
				}
			}
		}
	}
	
	/* The reactions of state AlarmInDisplayDelay. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay_tr1();
			}
		}
	}
	
	/* The reactions of state AlarmDisplayed. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed_tr0();
		}
	}
	
	/* The reactions of state AlarmEdit. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit_tr2();
				}
			}
		}
	}
	
	/* The reactions of state TimeEditingDelay. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay_tr2();
				}
			}
		}
	}
	
	/* The reactions of state TimeEditingOutWait. */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait() {
		if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr1();
			} else {
				if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr2_tr2()) {
					effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr2();
				} else {
					if (check_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr3_tr3()) {
						effect_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait_tr3();
					}
				}
			}
		}
	}
	
	/* The reactions of state ChronoRunning. */
	private void react_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning() {
		if (check_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr0();
		} else {
			if (check_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr1_tr1()) {
				effect_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning_tr1();
			}
		}
	}
	
	/* The reactions of state ChronoWaiting. */
	private void react_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting() {
		if (check_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_tr0();
		}
	}
	
	/* The reactions of state TimeUpdate. */
	private void react_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate() {
		if (check_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_tr0();
		}
	}
	
	/* The reactions of state SelectionShown. */
	private void react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown() {
		if (check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown_tr0();
		}
	}
	
	/* The reactions of state SelectionHidden. */
	private void react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden() {
		if (check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden_tr0();
		}
	}
	
	/* The reactions of state Intermediate. */
	private void react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate() {
		if (check_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_tr0_tr0()) {
			effect_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_tr0();
		}
	}
	
	/* The reactions of state AlarmOff. */
	private void react_main_region_masterState_Alarm_AlarmOff() {
		if (check_main_region_masterState_Alarm_AlarmOff_tr0_tr0()) {
			effect_main_region_masterState_Alarm_AlarmOff_tr0();
		}
	}
	
	/* The reactions of state AlarmBlinkOn. */
	private void react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn() {
		if (check_main_region_masterState_Alarm_AlarmOn_tr0_tr0()) {
			effect_main_region_masterState_Alarm_AlarmOn_tr0();
		} else {
			if (check_main_region_masterState_Alarm_AlarmOn_tr1_tr1()) {
				effect_main_region_masterState_Alarm_AlarmOn_tr1();
			} else {
				if (check_main_region_masterState_Alarm_AlarmOn_tr2_tr2()) {
					effect_main_region_masterState_Alarm_AlarmOn_tr2();
				} else {
					if (check_main_region_masterState_Alarm_AlarmOn_tr3_tr3()) {
						effect_main_region_masterState_Alarm_AlarmOn_tr3();
					} else {
						if (check_main_region_masterState_Alarm_AlarmOn_tr4_tr4()) {
							effect_main_region_masterState_Alarm_AlarmOn_tr4();
						} else {
							if (check_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_tr0_tr0()) {
								effect_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_tr0();
							}
						}
					}
				}
			}
		}
	}
	
	/* The reactions of state AlarmBlinkOff. */
	private void react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff() {
		if (check_main_region_masterState_Alarm_AlarmOn_tr0_tr0()) {
			effect_main_region_masterState_Alarm_AlarmOn_tr0();
		} else {
			if (check_main_region_masterState_Alarm_AlarmOn_tr1_tr1()) {
				effect_main_region_masterState_Alarm_AlarmOn_tr1();
			} else {
				if (check_main_region_masterState_Alarm_AlarmOn_tr2_tr2()) {
					effect_main_region_masterState_Alarm_AlarmOn_tr2();
				} else {
					if (check_main_region_masterState_Alarm_AlarmOn_tr3_tr3()) {
						effect_main_region_masterState_Alarm_AlarmOn_tr3();
					} else {
						if (check_main_region_masterState_Alarm_AlarmOn_tr4_tr4()) {
							effect_main_region_masterState_Alarm_AlarmOn_tr4();
						} else {
							if (check_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_tr0_tr0()) {
								effect_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff_tr0();
							}
						}
					}
				}
			}
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region__entry_Default() {
		enterSequence_main_region_masterState_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch_displayStates_IndigLo__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_IndigLo_LightOff_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch_displayStates_MainDisplay__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch_displayStates_Chrono__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch_displayStates_TimerUpdater__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation__entry_Default() {
		enterSequence_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Alarm__entry_Default() {
		enterSequence_main_region_masterState_Alarm_AlarmOff_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking__entry_Default() {
		enterSequence_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn_default();
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_region_masterState_Watch_displayStates_IndigLo_LightOff:
				react_main_region_masterState_Watch_displayStates_IndigLo_LightOff();
				break;
			case main_region_masterState_Watch_displayStates_IndigLo_LightActive:
				react_main_region_masterState_Watch_displayStates_IndigLo_LightActive();
				break;
			case main_region_masterState_Watch_displayStates_IndigLo_LightDelay:
				react_main_region_masterState_Watch_displayStates_IndigLo_LightDelay();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed:
				react_main_region_masterState_Watch_displayStates_MainDisplay_TimeDisplayed();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed:
				react_main_region_masterState_Watch_displayStates_MainDisplay_ChronoDisplayed();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait:
				react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingEntryWait();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive:
				react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingActive();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay:
				react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmInDisplayDelay();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed:
				react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmDisplayed();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit:
				react_main_region_masterState_Watch_displayStates_MainDisplay_AlarmEdit();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay:
				react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingDelay();
				break;
			case main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait:
				react_main_region_masterState_Watch_displayStates_MainDisplay_TimeEditingOutWait();
				break;
			case main_region_masterState_Watch_displayStates_Chrono_ChronoRunning:
				react_main_region_masterState_Watch_displayStates_Chrono_ChronoRunning();
				break;
			case main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting:
				react_main_region_masterState_Watch_displayStates_Chrono_ChronoWaiting();
				break;
			case main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate:
				react_main_region_masterState_Watch_displayStates_TimerUpdater_TimeUpdate();
				break;
			case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown:
				react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionShown();
				break;
			case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden:
				react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_SelectionHidden();
				break;
			case main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate:
				react_main_region_masterState_Watch_displayStates_TimeSelectionAnimation_Intermediate();
				break;
			case main_region_masterState_Alarm_AlarmOff:
				react_main_region_masterState_Alarm_AlarmOff();
				break;
			case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn:
				react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOn();
				break;
			case main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff:
				react_main_region_masterState_Alarm_AlarmOn_AlarmBlinking_AlarmBlinkOff();
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}
