/**
 * 
 */
package org.zkoss.fsm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A context for a State in a StateMachine, holding returning and transition
 * information.
 * @author simonpai
 */
public class StateCtx<E, C, IN> {
	
	protected StateMachine<E, C, IN> _machine;
	
	// local properties //
	protected boolean _returnAll = false;
	protected final Set<C> _returners = new HashSet<C>();
	protected final Set<IN> _minorReturners = new HashSet<IN>();
	protected final Map<C, E> _transitions = new HashMap<C, E>();
	protected final Map<C, TransitionListener<IN, C>> _transitionListeners = 
		new HashMap<C, TransitionListener<IN, C>>();
	protected final Map<IN, E> _minorTransitions = new HashMap<IN, E>();
	protected final Map<IN, TransitionListener<IN, C>> _minorTransitionListeners = 
		new HashMap<IN, TransitionListener<IN, C>>();
	
	/**
	 * Creates a new state context
	 */
	public StateCtx() {
		init();
	}
	
	/**
	 * Sets the owner state machine.
	 */
	protected StateCtx<E, C, IN> setMaster(StateMachine<E, C, IN> master) {
		_machine = master;
		return this;
	}
	
	
	
	// definition //
	/**
	 * Initialization method called at the constructor
	 */
	protected void init(){}
	
	// returning //
	/**
	 * Add an input class for returning to this state
	 * @return this state
	 */
	public StateCtx<E, C, IN> addReturningClasses(C ... inputClasses) {
		for(C c : inputClasses) _returners.add(c);
		return this;
	}
	
	/**
	 * Add multiple input classes for returning to this state
	 * @return this state
	 */
	public StateCtx<E, C, IN> addReturningClasses(Collection<C> collection) {
		_returners.addAll(collection);
		return this;
	}
	
	/**
	 * Add input characters for returning to this state
	 * @return this state
	 */
	public StateCtx<E, C, IN> addReturningInputs(IN ... inputs) {
		for(IN i : inputs) _minorReturners.add(i);
		return this;
	}
	
	/**
	 * Add input characters for returning to this state
	 * @return this state
	 */
	public StateCtx<E, C, IN> addReturningInputs(Collection<IN> collection) {
		_minorReturners.addAll(collection);
		return this;
	}
	
	/**
	 * Set whether returns to this state upon meeting unspecified characters 
	 * @return this state
	 */
	public StateCtx<E, C, IN> setReturningAll(boolean returnAll) {
		_returnAll = returnAll;
		return this;
	}
	
	// transition //
	/**
	 * Add a transition rule
	 * @return this state
	 */
	public StateCtx<E, C, IN> addTransition(C inputClass, E destination) {
		return addTransition(inputClass, destination, null);
	}
	
	/**
	 * Add a transition rule with a callback
	 * @return this state
	 */
	public StateCtx<E, C, IN> addTransition(C inputClass, E destination, 
			TransitionListener<IN, C> callback) {
		_transitions.put(inputClass, destination);
		if(callback == null)
			_transitionListeners.remove(inputClass);
		else
			_transitionListeners.put(inputClass, callback);
		return this;
	}
	
	/**
	 * Add multiple transitions
	 * @return this state
	 */
	public StateCtx<E, C, IN> addTransitions(E destination, C ... inputClasses) {
		return addTransitions(destination, null, inputClasses);
	}
	
	/**
	 * Add multiple transitions with a callback
	 * @return this state
	 */
	public StateCtx<E, C, IN> addTransitions(E destination, 
			TransitionListener<IN, C> callback, C ... inputClasses) {
		for(C c : inputClasses)
			addTransition(c, destination, callback);
		return this;
	}
	
	// route //
	/**
	 * Add a transition. Same as {@link #addTransition(Object, Object)}, 
	 * and only differs in return value.
	 * @return destination state.
	 */
	public StateCtx<E, C, IN> addRoute(C inputClass, E destination) {
		return addRoute(inputClass, destination, null);
	}
	
	/**
	 * Add a transition with callback. 
	 * Same as {@link #addTransition(Object, Object, TransitionListener)}, 
	 * and only differs in return value.
	 * @return destination state.
	 */
	public StateCtx<E, C, IN> addRoute(C inputClass, E destination, 
			TransitionListener<IN, C> callback) {
		addTransition(inputClass, destination, callback);
		return _machine.getState(destination);
	}
	
	/**
	 * Add multiple transitions. 
	 * Same as {@link #addTransitions(Object, Object...)}, 
	 * and only differs in return value.
	 * @return destination state.
	 */
	public StateCtx<E, C, IN> addRoutes(E destination, C ... inputClasses) {
		return addRoutes(destination, null, inputClasses);
	}
	
	/**
	 * Add multiple transitions with a callback. 
	 * Same as {@link #addTransitions(Object, TransitionListener, Object...)}, 
	 * and only differs in return value.
	 * @return destination state.
	 */
	public StateCtx<E, C, IN> addRoutes(E destination, 
			TransitionListener<IN, C> callback, C ... inputClasses) {
		addTransitions(destination, callback, inputClasses);
		return _machine.getState(destination);
	}
	
	// minor transition //
	/**
	 * Add a transition for a character.
	 * @return this state
	 */
	public StateCtx<E, C, IN> addMinorTransition(IN input, E destination) {
		return addMinorTransition(input, destination, null);
	}
	
	/**
	 * Add a transition for a character with a callback
	 * @return this state
	 */
	public StateCtx<E, C, IN> addMinorTransition(IN input, E destination, 
			TransitionListener<IN, C> callback) {
		_minorTransitions.put(input, destination);
		if(callback == null)
			_minorTransitionListeners.remove(input);
		else
			_minorTransitionListeners.put(input, callback);
		return this;
	}
	
	/**
	 * Add multiple transitions for characters
	 * @return this state
	 */
	public StateCtx<E, C, IN> addMinorTransitions(E destination, IN ... inputs) {
		return addMinorTransitions(destination, null, inputs);
	}
	
	/**
	 * Add multiple transitions for characters with a callback
	 * @return this state
	 */
	public StateCtx<E, C, IN> addMinorTransitions(E destination, 
			TransitionListener<IN, C> callback, IN ... inputs) {
		for(IN i : inputs)
			addMinorTransition(i, destination, callback);
		return this;
	}
	
	// minor route //
	/**
	 * Add a transition for a character. 
	 * Same as {@link #addMinorRoute(Object, Object)}, 
	 * and only differs in return value.
	 * @return destination state
	 */
	public StateCtx<E, C, IN> addMinorRoute(IN input, E destination) {
		return addMinorRoute(input, destination, null);
	}
	
	/**
	 * Add a transition for a character with callback. 
	 * Same as {@link #addMinorTransition(Object, Object, TransitionListener)}, 
	 * and only differs in return value.
	 * @return destination state
	 */
	public StateCtx<E, C, IN> addMinorRoute(IN input, E destination, 
			TransitionListener<IN, C> callback) {
		addMinorTransition(input, destination, callback);
		return _machine.getState(destination);
	}
	
	/**
	 * Add multiple transitions for characters. 
	 * Same as {@link #addMinorTransitions(Object, Object...)}, 
	 * and only differs in return value.
	 * @return destination state
	 */
	public StateCtx<E, C, IN> addMinorRoutes(E destination, IN ... inputs) {
		return addMinorRoutes(destination, null, inputs);
	}
	
	/**
	 * Add multiple transitions for characters with a callback. 
	 * Same as {@link #addMinorTransitions(Object, TransitionListener, Object...)}, 
	 * and only differs in return value.
	 * @return destination state
	 */
	public StateCtx<E, C, IN> addMinorRoutes(E destination, 
			TransitionListener<IN, C> callback, IN ... inputs) {
		addMinorTransitions(destination, callback, inputs);
		return _machine.getState(destination);
	}
	
	
	
	// query //
	/**
	 * Returns true if this state returns to itself by default, unless meeting
	 * specified characters or classes.
	 */
	public boolean isReturningAll(){
		return _returnAll;
	}
	
	/**
	 * Returns true if this state returns to itself upon meeting the character
	 * or the class.
	 */
	public boolean isReturning(IN input, C inputClass) {
		return _returnAll || _returners.contains(inputClass) || 
			_minorReturners.contains(input);
	}
	
	/**
	 * Returns true if the machine is leaving this state upon meeting the 
	 * character or the class.
	 */
	public boolean isLeaving(IN input, C inputClass) {
		return _transitions.containsKey(inputClass) || 
			_minorTransitions.containsKey(input);
	}
	
	/**
	 * Returns the destination state upon meeting the character or the class.
	 */
	public E getDestination(IN input, C inputClass) {
		E result = _minorTransitions.get(input);
		return (result != null)? result : _transitions.get(inputClass);
	}
	
	
	
	// event handler //
	/**
	 * This method is called when the machine enters this state
	 */
	protected void onLand(IN input, C inputClass, E origin) {}
	
	/**
	 * This method is called when the machine returns to the same state
	 */
	protected void onReturn(IN input, C inputClass) {}
	
	/**
	 * This method is called when the machine rejects the input on this state
	 */
	protected void onReject(IN input, C inputClass) {}
	
	/**
	 * This method is called when the machine leaves this state
	 */
	protected void onLeave(IN input, C inputClass, E destination) {}
	
	/**
	 * This method is called when the machine stops on this state
	 */
	protected void onStop(boolean endOfInput) {}
	
	public interface TransitionListener<IN, C> {
		public void onTransit(IN input, C inputClass);
	}
	
	
	
	// default operation //
	/*package*/ void doTransit(IN input, C inputClass) {
		TransitionListener<IN, C> c = _transitionListeners.get(inputClass);
		if(c != null) c.onTransit(input, inputClass);
		c = _minorTransitionListeners.get(input);
		if(c != null) c.onTransit(input, inputClass);
	}
}
