/* BindEvaluatorXUtil

	Purpose:
		
	Description:
		
	History:
		Oct 19, 2011 5:17:48 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.zk.ui.Component;

/**
 * a internal utility to help doing evaluation.
 * @author dennis
 * @since 6.0.0
 */
public class BindEvaluatorXUtil {

	//eval args, it it is an ExpressionX, than evaluate the value to instance
	public static Map<String, Object> evalArgs(BindEvaluatorX eval,Component comp, Map<String, Object> args) {
		if (args == null) {
			return null;
		}
		final Map<String, Object> result = new LinkedHashMap<String, Object>(args.size()); 
		for(final Iterator<Entry<String, Object>> it = args.entrySet().iterator(); it.hasNext();) {
			final Entry<String, Object> entry = it.next(); 
			final String key = entry.getKey();
			final Object value = entry.getValue();
			//evaluate the arg if it is an ExpressionX
			final Object evalValue = value == null ? null : 
				(value instanceof ExpressionX) ? eval.getValue(null, comp, (ExpressionX)value) : value;
			result.put(key, evalValue);
		}
		return result;
	}
	
	// parse args , if it is a string, than parse it to an ExpressionX
	public static Map<String, Object> parseArgs(BindEvaluatorX eval, Map<String,String[]> args) {
		final Map<String, Object> result = new LinkedHashMap<String, Object>(args.size()); 
		for(final Iterator<Entry<String, String[]>> it = args.entrySet().iterator(); it.hasNext();) {
			final Entry<String, String[]> entry = it.next(); 
			final String key = entry.getKey();
			final String[] value = entry.getValue();
			
			addArg(eval, result, key, value);
		}
		return result;
	}

	
	private static void addArg(BindEvaluatorX eval, Map<String,Object> result, String key, String[] valueScript) {
		Object val = null;
		if(valueScript.length==1){
			val =  eval.parseExpressionX(null, valueScript[0], Object.class);
		}else{
			//TODO support multiple value of a arg
			val = valueScript;
		}
		result.put(key, val);
	}
	
	public static BindEvaluatorX createEvaluator(FunctionMapper fnampper){
		return new BindEvaluatorXImpl(fnampper, org.zkoss.bind.xel.BindXelFactory.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T eval(BindEvaluatorX evalx, Component comp, String expression, Class<T> expectedType){
		ExpressionX expr = evalx.parseExpressionX(null, expression, expectedType);
		Object obj = evalx.getValue(null, comp, expr);
		return (T)obj;
	}
}
