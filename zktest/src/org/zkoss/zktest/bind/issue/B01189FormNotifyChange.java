package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;

public class B01189FormNotifyChange {

	boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public String getPropName(){
		return "prop";
	}
	
	Bean bean1 =  new Bean();
	Bean bean2 =  new Bean();
	Bean bean3 =  new Bean();

	public Bean getBean1(){
		return bean1;
	}
	
	public Bean getBean2(){
		return bean2;
	}
	
	public Bean getBean3(){
		return bean3;
	}
	
	public class Bean{
		String prop;

		public String getProp() {
			return prop;
		}

		public void setProp(String prop) {
			this.prop = prop;
		}
	}
	
	
	@Command
	public void save1(){
		
	}
	
	@Command
	public void save2(){
		
	}
	
}

