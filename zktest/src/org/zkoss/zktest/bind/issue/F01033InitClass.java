package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;

public class F01033InitClass{

	static public class Case1{

		static public class A{
			String value1;
			@Init
			public void initA(@BindingParam("val1")String value1){
				this.value1 = value1;
			}
			public String getValue1() {
				return value1;
			}
			public void setValue1(String value1) {
				this.value1 = value1;
			}
		}
		
		static public class B extends A{
				
		}
	
		static public class C extends B{
			String value2;
			@Init(superclass=true)
			public void initC(@BindingParam("val2")String value2){
				this.value2 = value2;
			}
			public String getValue2() {
				return value2;
			}
			public void setValue2(String value2) {
				this.value2 = value2;
			}
		}
		
		@Init(superclass=true)
		static public class D extends C{
			
		}
	}
	static public class Case2{

		static public class A{
			String value1;
			@Init
			public void initA(@BindingParam("val1")String value1){
				this.value1 = value1;
			}
			public String getValue1() {
				return value1;
			}
			public void setValue1(String value1) {
				this.value1 = value1;
			}
		}
		@Init(superclass=true)
		static public class B extends A{
				
		}
	
		static public class C extends B{
			String value2;
			@Init(superclass=true)
			public void initC(@BindingParam("val2")String value2){
				this.value2 = value2;
			}
			public String getValue2() {
				return value2;
			}
			public void setValue2(String value2) {
				this.value2 = value2;
			}
		}
		
		@Init(superclass=true)
		static public class D extends C{
			
		}
	}
}
