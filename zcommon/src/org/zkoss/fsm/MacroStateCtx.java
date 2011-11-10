/**
 * 
 */
package org.zkoss.fsm;

/**
 * The real power of StateMachine. This extended state allows you to define a
 * sub-StateMachine in a state. The life cycle of the inner state machine is
 * completed determined by the outer one:
 * 1. Upon {@link StateCtx#onLand} is called on the state, 
 * {@link StateMachine#start(Object)} is triggered on the inner machine
 * 2. Upon {@link StateCtx#onReturn} is called on the state, 
 * {@link StateMachine#run(Object)} is triggered on the inner machine
 * 3. Upon {@link StateCtx#onLeave} is called on the state, the inner machine 
 * is terminated.
 * 4. Upon {@link StateCtx#onStop} is called on the state, 
 * {@link StateMachine#onStop(boolean)} is triggered on the inner machine
 * @since 6.0.0
 * @author simonpai
 */
public class MacroStateCtx<E, C, IN, E2, C2> extends StateCtx<E, C, IN> {
	
	// sub machine //
	protected final StateMachine<E2, C2, IN> _submachine;
	
	/**
	 * Construct a macro state, which consists of a state machine by itself.
	 */
	public MacroStateCtx(StateMachine<E2, C2, IN> submachine) {
		super();
		_submachine = submachine;
	}
	
	
	
	// event handler //
	@Override
	protected void onLand(IN input, C inputClass, E origin) {
		_submachine.start(input);
	}
	@Override
	protected void onReturn(IN input, C inputClass) {
		_submachine.run(input);
	}
	@Override
	protected void onLeave(IN input, C inputClass, E destination) {
		_submachine.terminateAt(input);
	}
	@Override
	protected void onStop(boolean endOfInput) {
		_submachine.onStop(endOfInput);
	}
	
}
