/* InSeqMachine.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 12:06:37 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.select.impl;

import org.zkoss.fsm.StateCtx;
import org.zkoss.fsm.StateMachine;

/*package*/ class InSeqMachine extends StateMachine<InSeqMachine.SubState, Token.Type, Token> {

	private Selector _selector;
	private String _source;
	private SimpleSelectorSequence _seq;

	public InSeqMachine setSource(String source) {
		_source = source;
		return this;
	}

	public InSeqMachine setSelector(Selector selector) {
		_selector = selector;
		return this;
	}

	protected Token.Type getClass(Token input) {
		return input.getType();
	}

	protected SubState getLandingState(Token input, Token.Type inputClass) {

		switch (inputClass) {
		case IDENTIFIER:
		case UNIVERSAL:
			return SubState.MAIN;
		case NTN_ID:
			return SubState.ID_PRE_VALUE;
		case NTN_CLASS:
			return SubState.CLASS_PRE_VALUE;
		case NTN_PSDOCLS:
			return SubState.PSDOCLS_PRE_NAME;
		case NTN_PSDOELEM:
			return SubState.PSDOELEM_PRE_NAME;
		case OPEN_BRACKET:
			return SubState.ATTR_PRE_NAME;
		default:
			return null;
		}
	}

	protected void init() {

		setState(SubState.PSDOCLS_PRE_PARAM, new StateCtx<SubState, Token.Type, Token>() {

			protected void onLeave(Token input, Token.Type inputClass, SubState dest) {
				// flush pseudo class function parameter
				_seq.attachPseudoClassParameter(input.source(_source));
			}
		});

		// ID cycle
		getState(SubState.MAIN).addRoute(Token.Type.NTN_ID, SubState.ID_PRE_VALUE).addRoute(Token.Type.IDENTIFIER, SubState.MAIN,
				new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// flush ID value
						if (_seq.getId() != null)
							throw new ParseException("Illegal selector syntax: cannot have more than 1"
									+ " ID, failed at index " + input.getBeginIndex());
						_seq.setId(input.source(_source));
					}
				});

		// class cycle
		getState(SubState.MAIN).addRoute(Token.Type.NTN_CLASS, SubState.CLASS_PRE_VALUE).addRoute(Token.Type.IDENTIFIER,
				SubState.MAIN, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// flush class value
						_seq.addClass(input.source(_source));
					}
				});

		// TODO: consider quotes
		// pseudo class cycle
		getState(SubState.MAIN).addRoute(Token.Type.NTN_PSDOCLS, SubState.PSDOCLS_PRE_NAME)
				.addRoute(Token.Type.IDENTIFIER, SubState.PSDOCLS_POST_NAME, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// flush pseudo class function name
						_seq.addPseudoClass(input.source(_source));
					}
				}).addRoute(Token.Type.OPEN_PAREN, SubState.PSDOCLS_PRE_PARAM).addReturningClasses(Token.Type.MINOR_WHITESPACE)
				.addRoute(Token.Type.IDENTIFIER, SubState.PSDOCLS_POST_PARAM).addReturningClasses(Token.Type.MINOR_WHITESPACE)
				.addTransition(Token.Type.PARAM_SEPARATOR, SubState.PSDOCLS_PRE_PARAM)
				.addRoute(Token.Type.CLOSE_PAREN, SubState.MAIN);

		// pseudo class with no parameter
		getState(SubState.PSDOCLS_POST_NAME).addTransition(Token.Type.NTN_ID, SubState.ID_PRE_VALUE)
				.addTransition(Token.Type.NTN_CLASS, SubState.CLASS_PRE_VALUE)
				.addTransition(Token.Type.NTN_PSDOCLS, SubState.PSDOCLS_PRE_NAME)
				.addTransition(Token.Type.NTN_PSDOELEM, SubState.PSDOELEM_PRE_NAME)
				.addTransition(Token.Type.OPEN_BRACKET, SubState.ATTR_PRE_NAME);

		// ZK-2944: pseudo element TODO very similar behavior as class
		getState(SubState.MAIN).addRoute(Token.Type.NTN_PSDOELEM, SubState.PSDOELEM_PRE_NAME).addRoute(Token.Type.IDENTIFIER,
				SubState.MAIN, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// flush pseudo class function name
						_seq.addPseudoElement(input.source(_source));
					}
				});

		// attribute cycle
		getState(SubState.MAIN).addRoute(Token.Type.OPEN_BRACKET, SubState.ATTR_PRE_NAME)
				.addRoute(Token.Type.IDENTIFIER, SubState.ATTR_POST_NAME, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// set attribute name
						_seq.addAttribute(input.source(_source));
					}
				}).addRoutes(SubState.ATTR_PRE_VALUE, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// set attribute operator
						_seq.attachAttributeOperator(getOperator(inputClass));
					}
				}, Token.Type.OP_EQUAL, Token.Type.OP_BEGIN_WITH, Token.Type.OP_END_WITH, Token.Type.OP_CONTAIN)
				.addRoute(Token.Type.IDENTIFIER, SubState.ATTR_POST_VALUE, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// set attribute value
						_seq.attachAttributeValue(input.source(_source));
					}
				}).addRoute(Token.Type.CLOSE_BRACKET, SubState.MAIN);

		// attribute value with double quote
		getState(SubState.ATTR_PRE_VALUE).addRoute(Token.Type.DOUBLE_QUOTE, SubState.ATTR_PRE_VALUE_INDQT)
				.addRoute(Token.Type.IDENTIFIER, SubState.ATTR_POST_VALUE_INDQT, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// set attribute value
						_seq.attachAttributeValue(input.source(_source), true);
					}
				}).addRoute(Token.Type.DOUBLE_QUOTE, SubState.ATTR_POST_VALUE);

		// attribute value with single quote
		getState(SubState.ATTR_PRE_VALUE).addRoute(Token.Type.SINGLE_QUOTE, SubState.ATTR_PRE_VALUE_INSQT)
				.addRoute(Token.Type.IDENTIFIER, SubState.ATTR_POST_VALUE_INSQT, new StateCtx.TransitionListener<Token, Token.Type>() {
					public void onTransit(Token input, Token.Type inputClass) {
						// set attribute value
						_seq.attachAttributeValue(input.source(_source), true);
					}
				}).addRoute(Token.Type.SINGLE_QUOTE, SubState.ATTR_POST_VALUE);

	}

	protected void onStart(Token input, Token.Type inputClass, SubState landing) {
		_selector.add(_seq = new SimpleSelectorSequence());
		if (inputClass == Token.Type.IDENTIFIER)
			_seq.setType(input.source(_source));
	}

	protected void onStop(boolean endOfInput) {
		switch (_current) {
		case MAIN:
		case PSDOCLS_POST_NAME:
		case PSDOCLS_POST_PARAM:
		case ATTR_POST_VALUE:
			break;
		default:
			if (endOfInput)
				throw new ParseException("Illegal selector syntax: unexpected end of selector string.");
		}
	}

	protected void onDebug(String message) {
		super.onDebug("\t" + message);
	}

	// helper //
	private Attribute.Operator getOperator(Token.Type inputClass) {
		switch (inputClass) {
		case OP_EQUAL:
			return Attribute.Operator.EQUAL;
		case OP_BEGIN_WITH:
			return Attribute.Operator.BEGIN_WITH;
		case OP_END_WITH:
			return Attribute.Operator.END_WITH;
		case OP_CONTAIN:
			return Attribute.Operator.CONTAIN;
		default:
			return null;
		}
	}

	// state //
	public enum SubState {
		MAIN, ID_PRE_VALUE, CLASS_PRE_VALUE,

		PSDOCLS_PRE_NAME, PSDOCLS_POST_NAME, PSDOCLS_PRE_PARAM, PSDOCLS_POST_PARAM,

		PSDOELEM_PRE_NAME,

		ATTR_PRE_NAME, ATTR_POST_NAME, ATTR_PRE_VALUE, ATTR_POST_VALUE, ATTR_PRE_VALUE_INSQT, ATTR_POST_VALUE_INSQT, ATTR_PRE_VALUE_INDQT, ATTR_POST_VALUE_INDQT;
	}
}
