package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B00632 {

	String value1;
	String value2;
	Binder binder;
	
	public B00632(){
		value1 = "A";
		value2 = "B";
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
		this.value2 = "by-"+value1;
		getBinder().notifyChange(this, "value2");
		
	}
	
	public Binder getBinder(){
		if(binder==null){
			binder = new MyBinder();
		}
		return binder;
	}
	
	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	@Command 
	public void cmd1(){
		
	}
	@Command 
	public void cmd2(){
		
	}
	@Command 
	public void cmd3(){
		
	}
	
	public class MyBinder extends AnnotateBinder{
		public String getName(){
			return "XYZ";
		}
	}
}
