/**
 * 
 */
package org.zkoss.fsm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A Finite State Machine implementation. This state machine is callback based,
 * which differs from the standard FSM from textbook. Easier to use and faster 
 * to develop and debug.
 * @author simonpai
 */
public abstract class StateMachine<E, C, IN> {
	
	protected final Map<E, StateCtx<E, C, IN>> _states = 
		new HashMap<E, StateCtx<E, C, IN>>();
	protected E _current;
	protected boolean _run;
	protected int _step;
	protected boolean _debug = false;
	
	/**
	 * Construct a state machine
	 */
	public StateMachine() {
		init();
		reset();
	}
	
	
	
	// system //
	/**
	 * Set debug mode, where {@link onDebug} is called at certain timing to
	 * assist user develop the state machine
	 */
	public StateMachine<E, C, IN> setDebugMode(boolean mode) {
		_debug = mode;
		return this;
	}
	
	// definition //
	/**
	 * Set the state by token
	 * @return the state
	 */
	public StateCtx<E, C, IN> setState(E token, StateCtx<E, C, IN> state) {
		if(state == null) throw new IllegalArgumentException(
				"State cannot be null. Use removeState() to remove a state.");
		_states.put(token, state.setMaster(this));
		return state;
	}
	
	/**
	 * Remove the state by token.
	 * @return the removed state
	 */
	public StateCtx<E, C, IN> removeState(E token) {
		return _states.remove(token).setMaster(null);
	}
	
	/**
	 * Get the state by token. Automatically creates one if it does not exist.
	 */
	public StateCtx<E, C, IN> getState(E token) {
		return getState(token, true);
	}
	
	/**
	 * Get the state by token. 
	 * @param autoCreate if true, automatically creates one if it does not exist.
	 */
	public StateCtx<E, C, IN> getState(E token, boolean autoCreate) {
		StateCtx<E, C, IN> result = _states.get(token);
		if(result == null && autoCreate) 
			_states.put(token, result = new StateCtx<E, C, IN>().setMaster(this));
		return result;
	}
	
	/**
	 * Called at the constructor of state machine
	 */
	protected void init() {}
	
	/**
	 * Determines the initial state upon meeting the input character and class
	 */
	protected abstract E getLandingState(IN input, C inputClass);
	
	/**
	 * Determines the class for an input character.
	 */
	protected abstract C getClass(IN input);
	
	// event handler //
	/**
	 * This method is called at constructor and when reseting the machine.
	 */
	protected void onReset() {}
	
	/**
	 * This method is called when the machine takes the first character.
	 */
	protected void onStart(IN input, C inputClass, E landing) {}
	
	/**
	 * This method is called before executing a step
	 */
	protected void onBeforeStep(IN input, C inputClass) {} // TODO: include current?
	
	/**
	 * This method is called after executing a step
	 */
	protected void onAfterStep(IN input, C inputClass, E origin, E destination) {} // TODO: change name
	
	/**
	 * This method is called when the machine stops
	 * @param endOfInput true if the machine stops due to end of input
	 */
	protected void onStop(boolean endOfInput) {}
	
	/**
	 * This method is called when the machine rejects an input character
	 */
	protected void onReject(IN input) {
		throw new StateMachineException(_step, _current, input);
	}
	
	/**
	 * This method is call at certain situations when debug mode is on.
	 * @see #setDebugMode(boolean)
	 */
	protected void onDebug(String message){}
	
	
	
	// operation //
	/**
	 * Feed the machine a stream of characters
	 */
	public final void run(Iterator<IN> inputs) { // TODO: iterable
		_run = true;
		while(_run && inputs.hasNext())
			run(inputs.next());
		
		boolean endOfInput = !inputs.hasNext();
		onStop(endOfInput);
		if(_current != null)
			getState(_current).onStop(endOfInput);
		doDebug("");
		doDebug("Stop");
		doDebug("");
	}
	
	/**
	 * Feed the machine a single character
	 */
	public final void run(IN input) {
		
		C inputClass = getClass(input);
		
		doDebug("");
		doDebug("Step " + _step);
		doDebug("* Input: " + input + " (" + inputClass + ")");
		
		onBeforeStep(input, inputClass);
		
		final E origin = _current;
		E destination = null;
		
		if(inputClass == null) {
			doReject(input);
			return;
		}
		if(origin == null){
			destination = getLandingState(input, inputClass); // dest
			if(destination == null) {
				doReject(input);
				return;
			}
			onStart(input, inputClass, destination);
			getState(destination).onLand(input, inputClass, origin);
			
		} else {
			StateCtx<E, C, IN> state = getState(origin);
			
			if(state.isLeaving(input, inputClass)) {
				destination = state.getDestination(input, inputClass); // dest
				if(destination == null) {
					doReject(input);
					return;
				}
				state.onLeave(input, inputClass, destination);
				state.doTransit(input, inputClass);
				getState(destination).onLand(input, inputClass, origin);
				
			} else if(state.isReturning(input, inputClass)) {
				destination = origin; // dest
				state.onReturn(input, inputClass);
				
			} else { // rejected by state
				state.onReject(input, inputClass);
				doReject(input);
				return;
			}
		}
		
		_current = destination;
		
		doDebug("* State: " + origin + " -> " + destination);
		
		onAfterStep(input, inputClass, origin, destination);
		_step++;
	}
	
	// TODO: reduce API
	/**
	 * Starts the machine with a stream of input characters.
	 */
	public final void start(Iterator<IN> inputs) {
		reset();
		run(inputs);
	}
	
	/**
	 * Starts the machine with a single input character.
	 */
	public final void start(IN input) {
		reset();
		run(input);
	}
	
	/**
	 * Terminates the machine.
	 */
	public final void terminate() {
		reset();
	}
	
	
	
	// status query //
	// TODO: enhance, support getStates
	/**
	 * Return the current state
	 */
	public E getCurrentState() {
		return _current;
	}
	
	/**
	 * Return true if the machine is stopped
	 */
	public boolean isTerminated() {
		return !_run && _current == null;
	}
	
	/**
	 * Return true if the machine is suspended
	 */
	public boolean isSuspended() {
		return !_run && _current != null;
	}
	
	
	
	// default internal operation //
	/**
	 * Suspend the machine
	 */
	protected final void suspend() {
		_run = false;
	}
	
	/**
	 * Reject a character
	 */
	protected final void doReject(IN input) {
		_run = false;
		onReject(input);
	}
	
	/**
	 * Send a debug message
	 */
	protected final void doDebug(String message) {
		if(_debug) onDebug(message);
	}
	
	private final void reset() {
		_current = null;
		_run = false;
		_step = 0;
		doDebug("");
		doDebug("Reset");
		onReset();
	}
	
	/*package*/ final void terminateAt(IN input) {
		getState(_current).onLeave(input, null, null);
		reset();
	}
	
	
	
	// exception //
	public static class StateMachineException extends RuntimeException {
		private static final long serialVersionUID = -6580348498729948101L;
		
		private int _step;
		private Object _state;
		private Object _input;
		
		public StateMachineException(int step, Object state, Object input) {
			this(step, state, input, "Rejected at step " + step + 
					" with current state: " + state + ", input: " + input);
		}
		
		public StateMachineException(int step, Object state, Object input, 
				String message) {
			super(message);
			_step = step;
			_state = state;
			_input = input;
		}
		
		public StateMachineException(String message) {
			super(message);
		}
		
		public int getStep() {
			return _step;
		}
		
		public Object getState() {
			return _state;
		}
		
		public Object getInput() {
			return _input;
		}
	}
	

}
