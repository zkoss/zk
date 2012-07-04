/* B01185CollectionBindingNPE.java

	Purpose:
		
	Description:
		
	History:
		Jun 25, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;


import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01185CollectionBindingNPE {
	List<URL> URL;
	List<Person> person;

	@Init
	public void init(){
		 URL = new ArrayList<URL>();
		 person = new ArrayList<Person>();
	}
	
	@Command @NotifyChange("URL")
	public void addMore(){
    	if(URL != null && URL.size()>0){
    		URL.add(new URL());
    	}else{
    		URL = new ArrayList<URL>();
    		URL.add(new URL());
    	}	
	}
	
    @Command @NotifyChange("URL")
    public void remove(@BindingParam("index") int index){
    	URL.remove(index);
    }
    
	@Command @NotifyChange("person")
	public void addMorePerson(){
    	if(person != null && person.size()>0){
    		person.add(new Person());
    	}else{
    		person = new ArrayList<Person>();
    		person.add(new Person());
    	}	
	}
	
    @Command @NotifyChange("person")
    public void removePerson(@BindingParam("index") int index){
    	person.remove(index);
    }
	
	public List<URL> getURL() {
		return URL;
	}

	public void setURL(List<URL> uRL) {
		URL = uRL;
	}

	public List<Person> getPerson() {
		return person;
	}

	public void setPerson(List<Person> person) {
		this.person = person;
	}
	

	public static class Person {
		private String name;
		private String age;
		public String getName() {
			return name;
		}
		public String getAge() {
			return age;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setAge(String age) {
			this.age = age;
		}
		
	}
	public static class URL {
		private String url;
		private String port;
		
		public String getUrl() {
			return url;
		}
		public String getPort() {
			return port;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setPort(String port) {
			this.port = port;
		}
		
	}
	
}