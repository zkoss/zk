/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.fsm.*;
import org.zkoss.fsm.StateCtx.TransitionListener;
import org.zkoss.zk.ui.select.impl.Attribute.Operator;
import org.zkoss.zk.ui.select.impl.InSeqMachine.SubState;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;
import org.zkoss.zk.ui.select.impl.Token.Type;

/**
 * A parser that parses selector string and generates selector objects.
 * @author simonpai
 */
public class Parser {
	
	private String _source;
	private List<Selector> _selectorSet = new ArrayList<Selector>();
	private Selector _selector;
	
	private InSeqMachine _submachine;
	
	private StateMachine<State, CharClass, Token> _machine = 
		new StateMachine<State, CharClass, Token>(){
		
		@Override
		protected void init() {
			
			getState(State.PRE_SELECTOR)
				.addReturningClasses(CharClass.WHITESPACE)
				.addTransition(CharClass.SELECTOR_LITERAL, State.IN_SELECTOR);
			
			setState(State.IN_SELECTOR,
					new MacroStateCtx<State, CharClass, Token, SubState, Type>(
							_submachine = new InSeqMachine()))
				.addReturningClasses(CharClass.SELECTOR_LITERAL)
				.addTransition(CharClass.WHITESPACE, State.PRE_COMBINATOR)
				.addTransition(CharClass.SELECTOR_SEPARATOR, State.PRE_SELECTOR, 
						new TransitionListener<Token, CharClass>(){
					public void onTransit(Token input, CharClass inputClass) {
						flushCurrentSelector();
					}
				});
			
			getState(State.PRE_COMBINATOR)
				.addTransition(CharClass.COMBINATOR, State.POST_COMBINATOR, 
						new TransitionListener<Token, CharClass>(){
					public void onTransit(Token input, CharClass inputClass) {
						_selector.attachCombinator(getCombinator(input));
					}
				})
				.addTransition(CharClass.SELECTOR_LITERAL, State.IN_SELECTOR)
				.addTransition(CharClass.SELECTOR_SEPARATOR, State.PRE_SELECTOR, 
						new TransitionListener<Token, CharClass>(){
					public void onTransit(Token input, CharClass inputClass) {
						flushCurrentSelector();
					}
				});
			
			getState(State.POST_COMBINATOR)
				.addTransition(CharClass.WHITESPACE, State.PRE_SELECTOR);
			
		}
		
		@Override
		protected CharClass getClass(Token input) {
			switch(input.getType()){
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

		@Override
		protected State getLandingState(Token input, CharClass inputClass) {
			switch(inputClass){
			case WHITESPACE:
				return State.PRE_SELECTOR;
			case SELECTOR_LITERAL:
				return State.IN_SELECTOR;
			}
			return null;
		}
		
		@Override
		protected void onReset() {
			_submachine.setSelector(_selector);
			_submachine.setSource(_source);
		}
		
		@Override
		protected void onStop(boolean endOfInput) {
			// TODO: check state?
		}
		
		private void flushCurrentSelector(){
			// flush current selector
			_selectorSet.add(_selector = new Selector(_selectorSet.size()));
			_submachine.setSelector(_selector);
		}
		
	};
	
	public List<Selector> parse(String source){
		return parse(new Tokenizer().tokenize(source), source);
	}
	
	public List<Selector> parse(List<Token> tokens, String source){
		_source = source;
		_selectorSet.clear();
		_selectorSet.add(_selector = new Selector(0));
		_machine.start(tokens.iterator());
		return _selectorSet;
	}
	
	public void setDebugMode(boolean mode){
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
	private Combinator getCombinator(Token token){
		switch(token.getType()){
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

/*package*/ class InSeqMachine extends StateMachine<InSeqMachine.SubState, Token.Type, Token> {
	
	private Selector _selector;
	private String _source;
	private SimpleSelectorSequence _seq;
	
	public InSeqMachine setSource(String source){
		_source = source;
		return this;
	}
	
	public InSeqMachine setSelector(Selector selector){
		_selector = selector;
		return this;
	}
	
	@Override
	protected Type getClass(Token input) {
		return input.getType();
	}
	
	@Override
	protected SubState getLandingState(Token input, Type inputClass) {
		
		switch(inputClass){
		case IDENTIFIER:
		case UNIVERSAL:
			return SubState.MAIN;
		case NTN_ID:
			return SubState.ID_PRE_VALUE;
		case NTN_CLASS:
			return SubState.CLASS_PRE_VALUE;
		case NTN_PSDOCLS:
			return SubState.PSDOCLS_PRE_NAME;
		case OPEN_BRACKET:
			return SubState.ATTR_PRE_NAME;
		default:
			return null;
		}
	}
	
	@Override
	protected void init() {
		
		setState(SubState.PSDOCLS_PRE_PARAM, new StateCtx<SubState, Type, Token>(){
			@Override
			protected void onLeave(Token input, Type inputClass, SubState dest) {
				// flush pseudo class function parameter
				_seq.attachPseudoClassParameter(input.source(_source));
			}
		});
		
		// ID cycle
		getState(SubState.MAIN)
			.addRoute(Type.NTN_ID, SubState.ID_PRE_VALUE)
			.addRoute(Type.IDENTIFIER, SubState.MAIN, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// flush ID value
					if(_seq.getId() != null) throw new ParseException(
							"Illegal selector syntax: cannot have more than 1" + 
							" ID, failed at index " + input.getBeginIndex());
					_seq.setId(input.source(_source));
				}
			});
		
		// class cycle
		getState(SubState.MAIN)
			.addRoute(Type.NTN_CLASS, SubState.CLASS_PRE_VALUE)
			.addRoute(Type.IDENTIFIER, SubState.MAIN, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// flush class value
					_seq.addClass(input.source(_source));
				}
			});
		
		// TODO: consider quotes
		// pseudo class cycle
		getState(SubState.MAIN)
			.addRoute(Type.NTN_PSDOCLS, SubState.PSDOCLS_PRE_NAME)
			.addRoute(Type.IDENTIFIER, SubState.PSDOCLS_POST_NAME, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// flush pseudo class function name
					_seq.addPseudoClass(input.source(_source));
				}
			})
			.addRoute(Type.OPEN_PAREN, SubState.PSDOCLS_PRE_PARAM)
			.addReturningClasses(Type.MINOR_WHITESPACE)
			.addRoute(Type.IDENTIFIER, SubState.PSDOCLS_POST_PARAM)
			.addReturningClasses(Type.MINOR_WHITESPACE)
			.addTransition(Type.PARAM_SEPARATOR, SubState.PSDOCLS_PRE_PARAM)
			.addRoute(Type.CLOSE_PAREN, SubState.MAIN);
		
		// pseudo class with no parameter
		getState(SubState.PSDOCLS_POST_NAME)
			.addTransition(Type.NTN_ID, SubState.ID_PRE_VALUE)
			.addTransition(Type.NTN_CLASS, SubState.CLASS_PRE_VALUE)
			.addTransition(Type.NTN_PSDOCLS, SubState.PSDOCLS_PRE_NAME)
			.addTransition(Type.OPEN_BRACKET, SubState.ATTR_PRE_NAME);
		
		// attribute cycle
		getState(SubState.MAIN)
			.addRoute(Type.OPEN_BRACKET, SubState.ATTR_PRE_NAME)
			.addRoute(Type.IDENTIFIER, SubState.ATTR_POST_NAME, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// set attribute name
					_seq.addAttribute(input.source(_source));
				}
			})
			.addRoutes(SubState.ATTR_PRE_VALUE, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// set attribute operator
					_seq.attachAttributeOperator(getOperator(inputClass));
				}
			}, Type.OP_EQUAL, Type.OP_BEGIN_WITH, Type.OP_END_WITH, Type.OP_CONTAIN)
			.addRoute(Type.IDENTIFIER, SubState.ATTR_POST_VALUE, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// set attribute value
					_seq.attachAttributeValue(input.source(_source));
				}
			})
			.addRoute(Type.CLOSE_BRACKET, SubState.MAIN);
		
		// attribute value with double quote
		getState(SubState.ATTR_PRE_VALUE)
			.addRoute(Type.DOUBLE_QUOTE, SubState.ATTR_PRE_VALUE_INDQT)
			.addRoute(Type.IDENTIFIER, SubState.ATTR_POST_VALUE_INDQT, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// set attribute value
					_seq.attachAttributeValue(input.source(_source), true);
				}
			})
			.addRoute(Type.DOUBLE_QUOTE, SubState.ATTR_POST_VALUE);
		
		// attribute value with single quote
		getState(SubState.ATTR_PRE_VALUE)
			.addRoute(Type.SINGLE_QUOTE, SubState.ATTR_PRE_VALUE_INSQT)
			.addRoute(Type.IDENTIFIER, SubState.ATTR_POST_VALUE_INSQT, 
					new TransitionListener<Token, Type>(){
				public void onTransit(Token input, Type inputClass) {
					// set attribute value
					_seq.attachAttributeValue(input.source(_source), true);
				}
			})
			.addRoute(Type.SINGLE_QUOTE, SubState.ATTR_POST_VALUE);
		
	}
	
	@Override
	protected void onStart(Token input, Type inputClass, SubState landing){
		_selector.add(_seq = new SimpleSelectorSequence());
		if(inputClass == Type.IDENTIFIER) _seq.setType(input.source(_source));
	}
	
	@Override
	protected void onStop(boolean endOfInput) {
		switch (_current) {
		case MAIN:
		case PSDOCLS_POST_NAME:
		case PSDOCLS_POST_PARAM:
		case ATTR_POST_VALUE:
			break;
		default:
			if (endOfInput) throw new ParseException(
					"Illegal selector syntax: unexpected end of selector string.");
		}
	}
	
	@Override
	protected void onDebug(String message) {
		super.onDebug("\t" + message);
	}
	
	

	// helper //
	private Operator getOperator(Type inputClass){
		switch(inputClass){
		case OP_EQUAL:
			return Operator.EQUAL;
		case OP_BEGIN_WITH:
			return Operator.BEGIN_WITH;
		case OP_END_WITH:
			return Operator.END_WITH;
		case OP_CONTAIN:
			return Operator.CONTAIN;
		default:
			return null;
		}
	}
	
	
	
	// state //
	public enum SubState {
		MAIN, ID_PRE_VALUE, CLASS_PRE_VALUE,
		
		PSDOCLS_PRE_NAME, PSDOCLS_POST_NAME, 
		PSDOCLS_PRE_PARAM, PSDOCLS_POST_PARAM,
		
		ATTR_PRE_NAME, ATTR_POST_NAME, 
		ATTR_PRE_VALUE, ATTR_POST_VALUE,
		ATTR_PRE_VALUE_INSQT, ATTR_POST_VALUE_INSQT,
		ATTR_PRE_VALUE_INDQT, ATTR_POST_VALUE_INDQT;
	}
	
}
