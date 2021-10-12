package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.validator.AbstractValidator;

public class F00995ValidationMessages {

	String value1 = "ABC";
	
	
	public A getA(){
		return new A();
	}
	
	public D getD(){
		return new D();
	}
	
	public G getG(){
		return new G();
	}
	
	static public class A{
		public B getB(){
			return new B();
		}
	}
	
	static public class B{
		public C getC(){
			return new C();
		}
	}
	
	static public class C{
		
	}
	
	static public class D{
		public E getE(){
			return new E();
		}
	}
	
	static public class E{
		public F getF(){
			return new F();
		}
	}
	
	static public class F{
		
	}
	
	static public class G{
		public H join(Object a,Object d){
			return new H();
		}
	}
	
	static public class H{
		public String getEnd(){
			return "end";
		}
	}
	
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
	public Validator getValidator1(){
		return new AbstractValidator() {
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"abc".equalsIgnoreCase(val)){
					addInvalidMessage(ctx,"key1", "[1] value must equals ignore case 'abc', but is "+val);
					addInvalidMessage(ctx,"key1", "[2] value must equals ignore case 'abc', but is "+val);
					addInvalidMessage(ctx,"key1", "[3] value must equals ignore case 'abc', but is "+val);
				}
			}
		};
	}
}
