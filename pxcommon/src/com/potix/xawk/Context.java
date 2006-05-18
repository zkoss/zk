/* Context.java

{{IS_NOTE
	$Id: Context.java,v 1.2 2006/02/27 03:42:07 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Sep  2 21:04:19  2002, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.xawk;

import java.util.List;
import java.util.LinkedList;

import bsh.Interpreter;
import bsh.EvalError;

import com.potix.lang.Classes;
import com.potix.idom.Element;

/**
 * A context when an element is being processing.
 * The beanshell script could use it to get relevant information.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:07 $
 * @see Xawk
 */
final public class Context {
	/** The path of the element being processed, e.g., "/a/b".
	 */
	public final String path;
	/** The element being processed.
	 */
	public final Element element;
	/** The name of the element being processed, i.e., element.getName().
	 */
	public String name;
	/** The text of the element being processed, i.e., element.getText(true).
	 */
	public String text;
	/** The parent context, or null if it is the root context.
	 * The parent context is the context of the parent element.
	 */
	public final Context parent;
	/** The BeanShell interpreter that interprets the script.
	 */
	public final Interpreter interpreter;

	/** the stack.
	 */
	private final List _stack;

	/** Constructs a root context.
	 * @param e the element being parsing
	 */
	Context(Element e, Interpreter interpreter) {
		this.element = e;
		this.name = e.getName();
		this.path = '/' + this.name;
		this.text = e.getText(true);
		this.parent = null;
		this.interpreter = interpreter;
		this._stack = new LinkedList();
	}
	/** Creates a non-root context.
	 * @param e the element being parsing
	 * @param parent the parent context; never null
	 */
	Context(Element e, Context parent) {
		this.element = e;
		this.name = e.getName();
		this.path = parent.path + '/' + this.name;
		this.text = e.getText(true);
		this.parent = parent;
		this.interpreter = parent.interpreter;
		this._stack = parent._stack;
	}

	/** Returns the attribute value of the element.
	 * A shortcut of element.getAttributeValue(attrName).
	 * @see com.potix.idom.Element#getAttributeValue(String)
	 */
	public final String getAttributeValue(String attrName) {
		return this.element.getAttributeValue(attrName);
	}
	/** Binds a value to a BeanShell variable.
	 * Note: the value could be accessed directly in the script.
	 *
	 * <p>Typical use: <code>the.set(the.name, the.text)</code>.
	 * Then, you could access the text in another following script
	 * (particularly, in parent's script).
	 *
	 * <p>XML tags usually contains '-', so this method automatically
	 * removes '-' and capitalizes the succeeding character for the name
	 * parameter (by use of {@link Classes#correctFieldName}.
	 * Example, 'field-name' becomes 'fieldName'.
	 *
	 *<pre><code>&lt;rule&gt;
	 *	&lt;pattern&gt;/parent/child;/pattern&gt;
	 *	&lt;end&gt;the.set(the.name, the.text);&lt;/end&gt;
	 *	&lt;-- creates a BeanShell variable called child --&gt;
	 *&lt;/rule&gt;
	 *&lt;rule&gt;
	 *	&lt;pattern&gt;/parent&lt;/pattern&gt;
	 *	&lt;end&gt;print(child));&lt;/end&gt;
	 *&lt;/rule&gt;</code></pre>
	 *
	 * @exception bsh.EvalError if failed to bind
	 */
	public final void set(String name, Object value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds a boolean to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, boolean value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds an integer to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, int value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds a long to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, long value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds a short to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, short value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds a byte to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, byte value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Binds a char to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final void set(String name, char value) throws EvalError {
		interpreter.set(Classes.correctFieldName(name), value);
	}
	/** Returns the object bound to a BeanSheel variable.
	 * @see #set(String, Object)
	 */
	public final Object get(String name) throws EvalError {
		return interpreter.get(Classes.correctFieldName(name));
	}

	/** Pushes a value to the stack. Note: there is only one stack for
	 * all contexts of the same xawk instance.
	 * <p>A common use of the stack is to implement nested if.
	 * See potools's InstallerBean.
	 */
	public final void push(Object val) {
		_stack.add(0, val);
	}
	/** Pushes a boolean to the satck.
	 */
	public final void push(boolean v) {
		push(new Boolean(v));
	}
	/** Pushes an integer to the satck.
	 */
	public final void push(int v) {
		push(new Integer(v));
	}
	/** Pushes a long to the satck.
	 */
	public final void push(long v) {
		push(new Long(v));
	}
	/** Pushes a short to the satck.
	 */
	public final void push(short v) {
		push(new Short(v));
	}
	/** Pushes a byte to the satck.
	 */
	public final void push(byte v) {
		push(new Byte(v));
	}
	/** Pushes a char to the satck.
	 */
	public final void push(char v) {
		push(new Character(v));
	}

	/** Pops a value off the stack.
	 */
	public final Object pop() {
		return _stack.remove(0);
	}
	/** Peeks the value on top of the stack, or null if nothing in the stack.
	 */
	public final Object peek() {
		return _stack.isEmpty() ? null: _stack.get(0);
	}
}
