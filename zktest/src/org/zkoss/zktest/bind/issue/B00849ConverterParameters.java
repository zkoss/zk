package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class B00849ConverterParameters {

	@Wire
	Label l11;
	@Wire
	Label l12;
	@Wire
	Label l21;
	@Wire
	Label l22;
	@Wire
	Label l31;
	@Wire
	Label l32;
	
	@Init
	public void init(@ContextParam(ContextType.VIEW) Component component){
		Selectors.wireComponents(component, this, false);
	}
	
	public class Order {
		private String name = "default name";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private Order order = new Order();

	public Order getOrder() {
		return order;
	}

	public Converter getMyConverter1() {
		return new Converter() {

			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val;
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				String value1 = (String) ctx.getConverterArg("key1");
				String value2 = (String) ctx.getConverterArg("key2");
				if(value1==null){
					l11.setValue("1.toBean:"+value1);
				}
				if(value2==null){
					l12.setValue("1.toBean:"+value2);
				}
				return value1+":"+value2;
			}
		};
		
	}
	public Converter getMyConverter2() {
		return new Converter() {

			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val;
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				String value1 = (String) ctx.getConverterArg("key1");
				String value2 = (String) ctx.getConverterArg("key2");
				if(value1==null){
					l21.setValue("2.toBean:"+value1);
				}
				if(value2==null){
					l22.setValue("2.toBean:"+value2);
				}
				return value1+":"+value2;
			}
		};
		
	}
	public Converter getMyConverter3() {
		return new Converter() {

			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val;
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				String value1 = (String) ctx.getConverterArg("key1");
				String value2 = (String) ctx.getConverterArg("key2");
				if(value1==null){
					l31.setValue("3.toBean:"+value1);
				}
				if(value2==null){
					l32.setValue("3.toBean:"+value2);
				}
				return value1+":"+value2;
			}
		};
		
	}

	// -----------command -----------------
	@Command
	public void compute() {
	}

}
