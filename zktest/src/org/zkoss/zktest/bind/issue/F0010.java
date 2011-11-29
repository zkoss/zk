/* 

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

/**
 * @author Dennis Chen
 * 
 */
public class F0010 extends BindComposer {
	private String value1;
	private String value2;
	private String value0;
	
	Map<String,Converter> converterPool = new HashMap<String,Converter>(); 

	public F0010() {
		value0 = "A";		
		value1 = "B";
		value2 = "C";
		addConverter("converter0", new Converter() {
			public Object coerceToUi(Object val, Component component,
					BindContext ctx) {
				return val + "-toUI-c0";
			}

			public Object coerceToBean(Object val, Component component,
					BindContext ctx) {
				return val + "-toBean-c0";
			}
		});
		converterPool.put("converter1", new Converter() {
			public Object coerceToUi(Object val, Component component,
					BindContext ctx) {
				return val + "-toUI-c1";
			}

			public Object coerceToBean(Object val, Component component,
					BindContext ctx) {
				return val + "-toBean-c1";
			}
		});
	}
	
	public Map<String,Converter> getConverterPool(){
		return converterPool;
	}

	public String getValue0() {
		return value0;
	}

	@NotifyChange
	public void setValue0(String value0) {
		this.value0 = value0;
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public Converter getConverter2() {
		return new Converter() {
			public Object coerceToUi(Object val, Component component,
					BindContext ctx) {
				return val + "-toUI-c2";
			}

			public Object coerceToBean(Object val, Component component,
					BindContext ctx) {
				return val + "-toBean-c2";
			}
		};
	}
	
	
	@NotifyChange("converterPool")
	public void cmd1(){
		converterPool.put("converter1", new Converter() {
			public Object coerceToUi(Object val, Component component,
					BindContext ctx) {
				return val + "-toUI-c3";
			}

			public Object coerceToBean(Object val, Component component,
					BindContext ctx) {
				return val + "-toBean-c3";
			}
		});
	}
	

	public void cmd2(){
		converterPool.put("converter1", new Converter() {
			public Object coerceToUi(Object val, Component component,
					BindContext ctx) {
				return val + "-toUI-c4";
			}

			public Object coerceToBean(Object val, Component component,
					BindContext ctx) {
				return val + "-toBean-c4";
			}
		});
		getBinder().notifyChange(converterPool, "converter1");
	}	

}
