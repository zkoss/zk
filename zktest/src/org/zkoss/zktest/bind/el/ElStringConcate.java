package org.zkoss.zktest.bind.el;

import java.math.BigDecimal;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zel.ELProcessor;

public class ElStringConcate {
	
	private String value;
	
	public ElStringConcate() {
		value = "string concate";
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Command
	public void click(@BindingParam("key")String key) {
		System.out.println("yes we can!! " + key);
	}

	public static void main(String[] args) {
		ELProcessor elProc = new ELProcessor();
		
		System.out.println(elProc.eval("xxx.xxx"));
		System.out.println(elProc.eval("var = 'hi'; var.xxx"));
		elProc.defineBean("foo", new BigDecimal("123"));
		elProc.defineBean("bar", "blahblah");
		//System.out.println(elProc.eval("Math.sqrt(16)"));
		//System.out.println(elProc.eval("incr = x -> x+1; incr(10)"));
		
		String expression = "bar += 'hi' += foo";
		String ret1 = (String) elProc.getValue(expression, String.class);
		//System.out.println(ret1);
	}
}
