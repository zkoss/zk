/* JQuery.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Dec 4, 2009 10:45:32 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest.zats.ztl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * A simulator of JQuery client side object, which wraps the JQuery client side
 * API.
 *
 * @author jumperchen
 *
 */
public class JQuery extends ClientWidget implements Iterable<JQuery>{

	/**
	 * The script of get jq by UUID
	 */
	private static String JQ = "jq('%1')";

	public JQuery(String uuid) {
		if (isEmpty(uuid))
			throw new NullPointerException("uuid cannot be null!");
		_out = new StringBuffer(JQ.replace("%1", uuid));
	}

	public JQuery(ClientWidget el) {
		_out = new StringBuffer(JQ.replace("'%1'", el.toString()));
	}

	public JQuery(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}

	public JQuery(StringBuffer out) {
		_out = new StringBuffer(out);
	}

	/**
	 * Returns the CSS value from the given name.
	 *
	 * @param name
	 *            CSS name.
	 */
	public String css(String name) {
		return WebDriverTestCase.getEval(_out.toString() + ".css('" + name + "')");
	}

	/**
	 * Returns the attribute value from the given name.
	 *
	 * @param name
	 *            attribute name of the element.
	 */
	public String attr(String name) {
		return WebDriverTestCase.getEval(_out.toString() + ".attr('" + name + "')");
	}

	/**
	 * Returns whether includes the className.
	 *
	 * @param className
	 *            the CSS class name.
	 */
	public boolean hasClass(String className) {
		return Boolean.valueOf((WebDriverTestCase.getEval(_out.toString() + ".hasClass('" + className + "')")));
	}

	/**
	 * check the jquery element match the selector. (We port this from jQuery);
	 *
	 * @param selector
	 *            the JQuery allowed
	 * @return
	 */
	public boolean is(String selector) {
		return Boolean.valueOf((WebDriverTestCase.getEval(_out.toString() + ".is('" + selector + "')")));
	}

	/**
	 * a short cut for visble
	 */
	public boolean isVisible() {
		return is(":visible");
	}

	/**
	 * Finds the element from the given selector.
	 *
	 * @param selector
	 *            the JQuery allowed.
	 */
	public JQuery find(String selector) {
		return new JQuery(_out, ".find('" + selector + "')");
	}

	/**
	 * Returns the first element in JQuery object.
	 */
	public JQuery first() {
		return new JQuery(_out, ".first()");
	}

	/**
	 * Returns the last element in JQuery object.
	 */
	public JQuery last() {
		return new JQuery(_out, ".last()");
	}

	/**
	 * Returns the previous element in JQuery object.
	 */
	public JQuery prev() {
		return new JQuery(_out, ".prev()");
	}

	/**
	 * Returns the next element in JQuery object.
	 */
	public JQuery next() {
		return new JQuery(_out, ".next()");
	}

	/**
	 * Returns the child element in JQuery object.
	 */
	public JQuery children() {
		return new JQuery(_out, ".children()");
	}

	/**
	 * Returns the child element in JQuery object.
	 */
	public JQuery children(String selector) {
		return new JQuery(_out, ".children('" + selector + "')");
	}

	/**
	 * Returns the parent element in JQuery object.
	 */
	public JQuery parent() {
		return new JQuery(_out, ".parent()");
	}

	/**
	 * Returns the parent element in JQuery object.
	 */
	public JQuery parent(String selector) {
		return new JQuery(_out, ".parent('" + selector + "')");
	}
	
	/**
	 * Returns the parents element in JQuery object.
	 */
	public JQuery parents(String selector) {
		return new JQuery(_out, ".parents('" + selector + "')");
	}

	/**
	 * Returns the text content
	 */
	public String text() {
		return WebDriverTestCase.getEval(_out.toString() + ".text()");
	}

	/**
	 * Returns the html content(innerHTML)
	 */
	public String html() {
		return WebDriverTestCase.getEval(_out.toString() + ".html()");
	}

	/**
	 * Returns the current value of the first element in the set of matched
	 * elements.
	 *
	 * @return
	 */
	public String val() {
		return WebDriverTestCase.getEval(_out.toString() + ".val()");
	}

	/**
	 * Returns the current computed height for the first element.
	 */
	public int height() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".height()"));
	}

	/**
	 * Returns the current computed width for the first element.
	 */
	public int width() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".width()"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding but not border.
	 */
	public int innerHeight() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".innerHeight()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding but not border.
	 */
	public int innerWidth() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".innerWidth()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding and border.
	 */
	public int outerWidth() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".outerWidth()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding and border, it will including margin, if true
	 *
	 * @param boolean includeMargin
	 */
	public int outerWidth(boolean includeMargin) {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(
				_out.toString() + ".outerWidth(" + includeMargin + ")"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding and border.
	 */
	public int outerHeight() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".outerHeight()"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding and border, it will including margin, if true
	 *
	 * @param boolean includeMargin
	 */
	public int outerHeight(boolean includeMargin) {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(
				_out.toString() + ".outerHeight(" + includeMargin + ")"));
	}

	/**
	 * Returns the length of the array from the jQuery object.
	 */
	public int length() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".length"));
	}

	/**
	 * Switches to the ZK object.
	 */
	public ZK zk() {
		return new ZK(_out, ".zk");
	}

	
	public By toBy() {
		String id = attr("id");
		if (!isEmpty(id))
			return By.id(id);
		return By.className(attr("className"));
	}
	
	/**
	 * Returns the scrollbar width.
	 */
	public static int scrollbarWidth() {
		return Integer.parseInt(WebDriverTestCase.getEval("jq.scrollbarWidth()"));
	}

	/**
	 * Returns whether the widget exists or not.
	 */
	public boolean exists() {
		return Boolean.valueOf(WebDriverTestCase.getEval("!!" + _out.toString() + "[0]"));
	}

	/**
	 * Returns the current computed offsetLeft for the first element
	 */
	public int offsetLeft() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".offset().left"));
	}

	/**
	 * Returns the current computed offsetTop for the first element
	 */
	public int offsetTop() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".offset().top"));
	}

	/**
	 * Returns the current computed positionLeft (the offsetLeft relative to the
	 * parent) for the first element
	 *
	 * @return
	 */
	public int positionLeft() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".position().left"));
	}

	/**
	 * Returns the current computed positionTop (the offsetTop relative to the
	 * parent) for the first element
	 *
	 * @return
	 */
	public int positionTop() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".position().top"));
	}
	/**
	 * getter for scrollTop
	 * if multiple result , will receive first value.
	 * @return
	 */
	public int scrollTop(){
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".scrollTop()"));
	}
	
	/**
	 * setter for scrollTop 
	 * @param value
	 */
	public void scrollTop(int value){
		WebDriverTestCase.eval(_out.toString() + ".scrollTop(\""+value+"\")");
	}

	/**
	 * getter for scrollLeft
	 * if multiple result , will receive first value.
	 * @return
	 */
	public int scrollLeft(){
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".scrollLeft()"));
	}
	
	/**
	 * setter for scrollLeft 
	 * @param value
	 * @return
	 */
	public void scrollLeft(int value){
		WebDriverTestCase.eval(_out.toString() + ".scrollLeft(\""+value+"\")");
	}
	
	/**
	 * Note:This not a jQuery base method.
	 * just a short cut ,in javascript's world equals get(0).scrollHeight
	 * @return
	 */
	public int scrollHeight(){
		return WebDriverTestCase.parseInt(get(0).get("scrollHeight"));
	}
	
	/**
	 * Note:This not a jQuery base method.
	 * just a short cut ,in javascript's world equals get(0).scrollWidth
	 * @return
	 */
	public int scrollWidth(){
		return WebDriverTestCase.parseInt(get(0).get("scrollWidth"));
	}
	
	/**
	 * proxy for jQuery get method
	 * @param index
	 * @return Element  the dom element
	 */
	public Element get(int index){
		return new Element(_out.toString()+"[" + index + "]");
	}
	
	public Element toElement() {
		return get(0);
	}
	
	/**
	 * proxy for jQuery eq method
	 * @param index
	 * @return
	 */
	public JQuery eq(int index){
		return new JQuery(_out,".eq(" + index + ")");
	}

	public void remove() {
		WebDriverTestCase.eval(_out.toString() + ".remove()");
	}
	@Override
	public Iterator<JQuery> iterator() {
		return new JQueryIerator(this);
	}
	
	/**
	 * translate to widget. (a shortcut for  new Widget($obj) );
	 * @return
	 */
	public Widget toWidget(){
		return new Widget(this);
	}
	/**
	 * I use private class to prevent more complexly code in util.
	 * No body should know how it works , 
	 * just know that it return the JQuery object in order.
	 * In fact , this is useful I think. 
	 * @author Tony
	 *
	 */
	private class JQueryIerator implements Iterator<JQuery>{
		private JQuery _context;
		private int _count;
		private int _index = 0 ;
		public JQueryIerator(JQuery context){
			_context = context;
			_count = _context.length();
		}
		@Override
		public boolean hasNext() {
			return _index != _count;
		}
		@Override
		public JQuery next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			JQuery result = _context.eq(_index);
			++ _index ;
			return result;
		}
		/**
		 * why we don't support remove in this time?
		 * Because we don't really got a jQuery instance in this time.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
