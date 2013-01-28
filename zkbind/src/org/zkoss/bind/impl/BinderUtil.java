/* BinderUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/9/25 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.zkoss.bind.Binder;
import org.zkoss.util.resource.Location;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class BinderUtil {
	public static void markHandling(Component comp,Binder binder){
		comp.setAttribute(BinderImpl.BINDER, binder);
	}
	
	public static void unmarkHandling(Component comp){
		comp.removeAttribute(BinderImpl.BINDER);
	}
	public static boolean isHandling(Component comp){
		return comp.hasAttribute(BinderImpl.BINDER);
	}
	public static Binder getBinder(Component comp){
		return (Binder)comp.getAttribute(BinderImpl.BINDER);
	}
	public static Binder getBinder(Component comp, boolean recurse){
		return (Binder)comp.getAttribute(BinderImpl.BINDER,recurse);
	}
	
	/**
	 * The context for wrapping a api call for a binder, 
	 * to keep api compatibility and prevent tedious parameter passing of the sugar/debug implementation.
	 * I use List to implement the stack cause I don't need to care the synchronized
	 */
	static ThreadLocal<LiteStack<UtilContext>> _ctxStack = new ThreadLocal<LiteStack<UtilContext>>();
	
	public static UtilContext pushContext(){
		
		LiteStack<UtilContext> stack = _ctxStack.get();
		if(stack==null){
			_ctxStack.set(stack = new LiteStack<UtilContext>(3));//usually 1-2, not sure the performance compare to linkedlist
		}
		UtilContext ctx = new UtilContext();
		stack.push(ctx);
		return ctx;
	}
	public static boolean hasContext(){
		LiteStack<UtilContext> stack = _ctxStack.get();
		if(stack==null){
			return false;
		}
		return !stack.empty();
	}
	public static UtilContext getContext(){
		LiteStack<UtilContext> stack = _ctxStack.get();
		if(stack==null){
			return null;
		}
		return stack.peek();
	}
	
	public static void popContext(){
		LiteStack<UtilContext> stack = _ctxStack.get();
		if(stack!=null){
			stack.pop();
			if(stack.empty()){
				_ctxStack.set(null);
			}
		}else{
			throw new IllegalStateException("nothing to popup");
		}
	}
	
	static public class UtilContext {
		boolean _ignoreAccessCreationWarn;
		Location _location;

		public boolean isIgnoreAccessCreationWarn() {
			return _ignoreAccessCreationWarn;
		}

		public void setIgnoreAccessCreationWarn(boolean ignoreAccessCreationWarn) {
			_ignoreAccessCreationWarn = ignoreAccessCreationWarn;
		}

		public void setCurrentLocation(Location location) {
			_location = location;
		}
		
		public Location getCurrentLocation(){
			return _location;
		}
		
		public String getCurrentLocationMessage(){
			return MiscUtil.formatLocationMessage(null, _location);
		}
	}
	
	static class LiteStack<T> implements Serializable{
		private static final long serialVersionUID = 1L;
		List<T> _stack;
		
		public LiteStack(){
			this(3);
		}
		public LiteStack(int initialCapacity){
			_stack = new ArrayList<T>(initialCapacity);
		}
		
		public void push(T element){
			_stack.add(element);
		}
		
		public int size(){
			return _stack.size();
		}
		
		public boolean empty(){
			return _stack.size()==0;
		}
		
		public T peek(){
			return _stack.get(_stack.size()-1);
		}
		
		public T pop(){
			return _stack.remove(_stack.size()-1);
		}
	}
}
