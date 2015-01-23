package org.zkoss.zktest.bind.el;

import java.math.BigDecimal;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zel.ELProcessor;

public class F80_ZK_2596 {
	
	private String value;
	
	public F80_ZK_2596() {
		value = "value";
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Command @NotifyChange("value")
	public void click(@BindingParam("key")String key) {
		value = key;
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
