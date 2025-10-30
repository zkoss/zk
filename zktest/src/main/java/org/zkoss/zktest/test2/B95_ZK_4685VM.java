/* B95_ZK_4685VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.zk.ui.util.Clients;

public class B95_ZK_4685VM {
	private FormModel formModel = new FormModel("default", null);
	private String result = "";

	@Command
	public void displayForm1() {
		BindUtils.postNotifyChange(formModel, "template");
		formModel = new FormModel("form1", new ContentModel1());
		BindUtils.postNotifyChange(this, "formModel");
	}

	@Command
	public void displayForm2() {
		BindUtils.postNotifyChange(formModel, "template");
		formModel = new FormModel("form2", new ContentModel2());
		BindUtils.postNotifyChange(this, "formModel");
	}

	@Command
	public void save(@ContextParam(ContextType.BINDER) BinderCtrl binder,
	                 @BindingParam("myform") Form myform) {
		close(binder, myform);
	}

	@Command
	@NotifyChange("result")
	public void close(@ContextParam(ContextType.BINDER) BinderCtrl binder,
	                  @BindingParam("myform") Form myform) {
		result += binder.getSaveFormFieldNames(myform);
		resetForm();
	}

	@Command
	public void resetForm() {
		formModel = new FormModel("default", null);
		BindUtils.postNotifyChange(formModel, "template");
		BindUtils.postNotifyChange(this, "formModel");
	}


	public FormModel getFormModel() {
		return formModel;
	}

	public String getResult() {
		return result;
	}

	public static class FormModel {
		private String template;
		private ContentModel formContent;

		public FormModel() {
		}

		public FormModel(String template, ContentModel formContent) {
			this.template = template;
			this.formContent = formContent;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public ContentModel getFormContent() {
			return formContent;
		}

		public void setFormContent(ContentModel formContent) {
			this.formContent = formContent;
		}
	}

	public static class ContentModel {
		protected String commonProp;

		public String getCommonProp() {
			return commonProp;
		}

		public void setCommonProp(String commonProp) {
			this.commonProp = commonProp;
		}
	}

	public static class ContentModel1 extends ContentModel {
		private String prop1;

		public String getProp1() {
			return prop1;
		}

		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}
	}

	public static class ContentModel2 extends ContentModel {
		private String prop2;

		public String getProp2() {
			return prop2;
		}

		public void setProp2(String prop2) {
			this.prop2 = prop2;
		}
	}
}