/** ApplyVM.java.

	Purpose:
		
	Description:
		
	History:
		5:10:02 PM Nov 13, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zktest.zats.zuti.bean.Person;

/**
 * @author jumperchen
 *
 */
public class ApplyVM {
	private Person person;
	private String templateURI = "../include/apply.zul";
	private String simpleTemplateURI = "../include/basic.zul";
	private String simpleRootTemplateURI = "../include/basicRoot.zul";
	private String simpleNestedTemplateURI = "../include/basicNested.zul";
	private String template = "readonly";
	@Init
	public void init() {
		person = new Person("Foo", "foo.zkoss.com");
	}
	public void setPerson(Person p) {
		person = p;
	}
	public Person getPerson() {
		return person;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplateURI(String uri) {
		templateURI = uri;
	}
	public String getTemplateURI() {
		return templateURI;
	}
	
	public void setSimpleTemplateURI(String uri) {
		simpleTemplateURI = uri;
	}
	public String getSimpleTemplateURI() {
		return simpleTemplateURI;
	}
	
	public void setSimpleNestedTemplateURI(String uri) {
		simpleNestedTemplateURI = uri;
	}
	public String getSimpleNestedTemplateURI() {
		return simpleNestedTemplateURI;
	}
	
	public void setSimpleRootTemplateURI(String uri) {
		simpleRootTemplateURI = uri;
	}
	public String getSimpleRootTemplateURI() {
		return simpleRootTemplateURI;
	}
	@NotifyChange({"person", "templateURI", "template"})
	@Command
	public void editProfile(@BindingParam("profile") Person p) {
		person = p;
		templateURI = "../include/editApply.zul";
		template = "edit";
	}
	@NotifyChange({"person", "templateURI", "template"})
	@Command
	public void updateProfile() {
		templateURI = "../include/apply.zul";
		template = "readonly";
	}

	@NotifyChange({"simpleTemplateURI", "template"})
	@Command
	public void changeTemplate() {
		template = "edit";
		simpleTemplateURI = simpleNestedTemplateURI;
	}

	@NotifyChange({"simpleTemplateURI", "simpleRootTemplateURI", "template"})
	@Command
	public void changeNestedTemplate() {
		template = "edit";
		simpleRootTemplateURI = simpleRootTemplateURI.replace("basicRoot.zul", "basicRoot2.zul");
		simpleTemplateURI = simpleTemplateURI.replace("basic.zul", "basic2.zul");
	}

	@NotifyChange({"simpleTemplateURI"})
	@Command
	public void changeSimpleTemplate() {
		simpleTemplateURI = simpleTemplateURI.replace("basic.zul", "basic2.zul");
	}
}
