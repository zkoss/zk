/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.fsm.MacroStateCtx;
import org.zkoss.fsm.StateCtx.TransitionListener;
import org.zkoss.fsm.StateMachine;
import org.zkoss.fsm.StateMachine.StateMachineException;
import org.zkoss.zk.ui.select.impl.InSeqMachine.SubState;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;
import org.zkoss.zk.ui.select.impl.Token.Type;

/**
 * A parser that parses selector string and generates selector objects.
 * @since 6.0.0
 * @author simonpai
 */
public class Parser {

	private String _source;
	private List<Selector> _selectorSet = new LinkedList<Selector>(); //to preserve order
	private Selector _selector;

	private InSeqMachine _submachine;

	private StateMachine<State, CharClass, Token> _machine = new StateMachine<State, CharClass, Token>() {

		protected void init() {

			getState(State.PRE_SELECTOR).addReturningClasses(CharClass.WHITESPACE)
					.addTransition(CharClass.SELECTOR_LITERAL, State.IN_SELECTOR);

			setState(State.IN_SELECTOR,
					new MacroStateCtx<State, CharClass, Token, SubState, Type>(_submachine = new InSeqMachine()))
							.addReturningClasses(CharClass.SELECTOR_LITERAL)
							.addTransition(CharClass.WHITESPACE, State.PRE_COMBINATOR)
							.addTransition(CharClass.SELECTOR_SEPARATOR, State.PRE_SELECTOR,
									new TransitionListener<Token, CharClass>() {
				public void onTransit(Token input, CharClass inputClass) {
					flushCurrentSelector();
				}
			});

			getState(State.PRE_COMBINATOR).addTransition(CharClass.COMBINATOR, State.POST_COMBINATOR,
					new TransitionListener<Token, CharClass>() {
				public void onTransit(Token input, CharClass inputClass) {
					_selector.attachCombinator(getCombinator(input));
				}
			}).addTransition(CharClass.SELECTOR_LITERAL, State.IN_SELECTOR).addTransition(CharClass.SELECTOR_SEPARATOR,
					State.PRE_SELECTOR, new TransitionListener<Token, CharClass>() {
				public void onTransit(Token input, CharClass inputClass) {
					flushCurrentSelector();
				}
			});

			getState(State.POST_COMBINATOR).addTransition(CharClass.WHITESPACE, State.PRE_SELECTOR);

		}

		protected CharClass getClass(Token input) {
			switch (input.getType()) {
			case WHITESPACE:
				return CharClass.WHITESPACE;

			case CBN_CHILD:
			case CBN_ADJACENT_SIBLING:
			case CBN_GENERAL_SIBLING:
				return CharClass.COMBINATOR;

			case SELECTOR_SEPARATOR:
				return CharClass.SELECTOR_SEPARATOR;

			default:
				return CharClass.SELECTOR_LITERAL;
			}
		}

		protected State getLandingState(Token input, CharClass inputClass) {
			switch (inputClass) {
			case WHITESPACE:
				return State.PRE_SELECTOR;
			case SELECTOR_LITERAL:
				return State.IN_SELECTOR;
			}
			return null;
		}

		protected void onReset() {
			_submachine.setSelector(_selector);
			_submachine.setSource(_source);
		}

		protected void onStop(boolean endOfInput) {
			// TODO: check state?
		}

		private void flushCurrentSelector() {
			// flush current selector
			_selectorSet.add(_selector = new Selector(_selectorSet.size()));
			_submachine.setSelector(_selector);
		}

	};

	public List<Selector> parse(String source) {
		try {
			return parse(new Tokenizer().tokenize(source), source);
		} catch (StateMachineException e) {
			throw new ParseException("Illegal selector string: " + source);
		}
	}

	public List<Selector> parse(List<Token> tokens, String source) {
		_source = source;
		_selectorSet.clear();
		_selectorSet.add(_selector = new Selector(0));
		_machine.start(tokens.iterator());
		return _selectorSet;
	}

	public void setDebugMode(boolean mode) {
		_machine.setDebugMode(mode);
		_submachine.setDebugMode(mode);
	}

	// state, char class //
	private enum State {
		PRE_SELECTOR, IN_SELECTOR, PRE_COMBINATOR, POST_COMBINATOR;
	}

	private enum CharClass {
		SELECTOR_LITERAL, WHITESPACE, COMBINATOR, SELECTOR_SEPARATOR;
	}

	// helper //
	private Combinator getCombinator(Token token) {
		switch (token.getType()) {
		case CBN_CHILD:
			return Combinator.CHILD;
		case CBN_ADJACENT_SIBLING:
			return Combinator.ADJACENT_SIBLING;
		case CBN_GENERAL_SIBLING:
			return Combinator.GENERAL_SIBLING;
		}
		throw new IllegalStateException();
	}

}
