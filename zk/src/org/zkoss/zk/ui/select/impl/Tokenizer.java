/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;

import org.zkoss.fsm.*;
import org.zkoss.zk.ui.select.impl.Token.Type;

/**
 * A tokenizer of selector string.
 * @author simonpai
 */
public class Tokenizer {
	
	private final StateMachine<State, CharClass, Character> _machine;
	private ArrayList<Token> _tokens;
	
	public Tokenizer(){
		
		_tokens = null;
		
		_machine = new StateMachine<State, CharClass, Character>(){
			
			private int _anchor;
			private char _prevChar;
			private CharClass _prevClass;
			protected boolean _inDoubleQuote;
			protected boolean _inSingleQuote;
			protected boolean _inParam;
			protected boolean _escaped;
			protected boolean _opEscaped;
			
			@Override
			protected void init() {
				getState(State.MAIN)
					.setReturningAll(true)
					.addMinorTransition('[', State.IN_ATTRIBUTE);
				
				setState(State.IN_ATTRIBUTE, 
						new StateCtx<State, CharClass, Character>(){
					@Override
					protected void onReturn(Character i, CharClass cls) {
						if(cls != CharClass.OTHER) return;
						if(i == '"') 
							_inDoubleQuote = !_inDoubleQuote;
						else if(i == '\'')
							_inSingleQuote = !_inSingleQuote;
					}})
					.setReturningAll(true)
					.addMinorTransition(']', State.MAIN);
				
				// TODO: IN_PARAM
				
			}
			
			@Override
			protected void onReset() {
				_inDoubleQuote = false;
				_inSingleQuote = false;
				_inParam = false;
				_escaped = false;
				_opEscaped = false;
				
				_anchor = 0;
				_prevChar = '!';
				_prevClass = null;
				_tokens = new ArrayList<Token>();
			}
			
			@Override
			protected void afterStep(Character input, CharClass inputClass,
					State origin, State destination) {
				
				doDebug("* OP Escaped: " + _opEscaped);
				
				if(inputClass == CharClass.ESCAPE) return;
				
				boolean isPrefix = 
					origin == State.IN_ATTRIBUTE && 
					inputClass == CharClass.OTHER &&
					(input=='^' || input=='$' || input=='*');
				
				// flush previous identifier/whitespace
				if(inputClass != _prevClass &&_prevClass != null && 
						_prevClass.isMultiple())
					flush(_prevChar, _prevClass, false);
				
				// previous char is ^/$/* but input is not =
				if(origin == State.IN_ATTRIBUTE && _opEscaped && input!='=')
					flush(_prevChar, _prevClass, false);
				
				// flush current
				if(!inputClass.isMultiple() && !isPrefix)
					flush(input, inputClass, true);
				
				// update status
				if(input == '(') _inParam = true;
				else if(input == ')') _inParam = false;
				
				_prevChar = input;
				_prevClass = inputClass;
				_opEscaped = isPrefix;
				
			}
			
			@Override
			protected void onStop(boolean endOfInput) {
				if(!endOfInput) return;
				
				// flush last token if any
				if(_anchor < _step)
					flush(_prevChar, _prevClass, false);
			}
			
			@Override
			protected CharClass getClass(Character c) {
				
				if(_inDoubleQuote && (_escaped || c != '"'))
					return CharClass.LITERAL;
				
				if(_inSingleQuote && (_escaped || c != '\''))
					return CharClass.LITERAL;
				
				// TODO: check this
				if(_inParam && c != ',' && c != ')')
					return Character.isWhitespace(c)? CharClass.OTHER : CharClass.LITERAL;
				
				if(_escaped)
					return Character.isWhitespace(c)? 
							CharClass.WHITESPACE : CharClass.LITERAL;
				
				if(Character.isLetter(c) || Character.isDigit(c) || c == '-' || c == '_')
					return CharClass.LITERAL;
				
				if(Character.isWhitespace(c))
					return CharClass.WHITESPACE;
				
				return c == '\\' ? CharClass.ESCAPE : CharClass.OTHER;
			}
			
			@Override
			protected State getLandingState(Character input,
					CharClass inputClass) {
				
				if(input == '[') return State.IN_ATTRIBUTE;
				if(inputClass == CharClass.ESCAPE) _escaped = true;
				return State.MAIN;
			}
			
			@Override
			protected void onReject(Character input) {
				throw new ParseException(_step, _current, input);
			}

			private void flush(char input, CharClass inputClass, boolean withCurrChar){
				int endIndex = _step + (withCurrChar? 1 : _escaped? -1 : 0);
				_tokens.add(new Token(
						getTokenType(input, inputClass), _anchor, endIndex));
				doDebug("! flush: [" + _anchor + ", " + endIndex + "]");
				_anchor = endIndex;
				
			}
			
			private Type getTokenType(char input, CharClass inputClass){
				
				switch(inputClass){
				case LITERAL:
					return Type.IDENTIFIER;
				case WHITESPACE:
					return Type.WHITESPACE;
				}
				
				switch(input){
				case ',':
					return _inParam ? 
							Type.PARAM_SEPARATOR : Type.SELECTOR_SEPARATOR;
				case '*':
					return Type.UNIVERSAL;
				case '>':
					return Type.CBN_CHILD;
				case '+':
					return Type.CBN_ADJACENT_SIBLING;
				case '~':
					return Type.CBN_GENERAL_SIBLING;
				case '#':
					return Type.NTN_ID;
				case '.':
					return Type.NTN_CLASS;
				case ':':
					return Type.NTN_PSDOCLS;
				case '\'':
					return Type.SINGLE_QUOTE;
				case '"':
					return Type.DOUBLE_QUOTE;
				case '[':
					return Type.OPEN_BRACKET;
				case ']':
					return Type.CLOSE_BRACKET;
				case '(':
					return Type.OPEN_PAREN;
				case ')':
					return Type.CLOSE_PAREN;
				case '=':
					switch(_prevChar){
					case '^':
						return Type.OP_BEGIN_WITH;
					case '$':
						return Type.OP_END_WITH;
					case '*':
						return Type.OP_CONTAIN;
					default:
						return Type.OP_EQUAL;
					}
				default:
					return Character.isWhitespace(input) ? 
							Type.MINOR_WHITESPACE : Type.UNKNOWN_CHAR;
				}
			}
			
		};
	}
	
	public ArrayList<Token> tokenize(String selector){
		_machine.start(new CharSequenceIterator(selector));
		return _tokens;
	}
	
	public void setDebugMode(boolean mode){
		_machine.setDebugMode(mode);
	}
	
	
	
	// state, input class //
	private enum State {
		MAIN, IN_ATTRIBUTE;
	}
	
	private enum CharClass {
		LITERAL(true), WHITESPACE(true), ESCAPE, OTHER;
		
		private boolean _multiple;
		
		CharClass(){
			this(false);
		}
		
		CharClass(boolean multiple){
			_multiple = multiple;
		}
		
		public boolean isMultiple(){
			return _multiple;
		}
	}
	
}
