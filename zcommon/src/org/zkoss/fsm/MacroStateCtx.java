/**
 * 
 */
package org.zkoss.fsm;

/**
 * The real power of StateMachine. This extended State allows you to define a
 * sub-StateMachine in a State.
 * @author simonpai
 */
public class MacroStateCtx<E, C, IN, E2, C2> extends StateCtx<E, C, IN> {
	
	// sub machine //
	protected StateMachine<E2, C2, IN> _submachine;
	
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
