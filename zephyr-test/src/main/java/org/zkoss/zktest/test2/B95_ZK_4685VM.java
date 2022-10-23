/* B95_ZK_4685VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class B95_ZK_4685VM {
	private final FormModel formModel = new FormModel();
	private boolean open = false;
	private boolean content2Open = false;
	public FormModel getFormModel() {
		return this.formModel;
	}
	public boolean isOpen() {
		return this.open;
	}
	public void setOpen(boolean open) {
		this.open = open;
		BindUtils.postNotifyChange(null, null, this, "open");
	}
	public boolean isContent2Open() {
		return this.content2Open;
	}
	public void setContent2Open(boolean open) {
		this.content2Open = open;
		BindUtils.postNotifyChange(null, null, this, "content2Open");
	}
	@Command
	public void doCloseForm() {
		setOpen(false);
	}
	@Command
	public void doOpenFormContent1() {
		getFormModel().createContentModel1();
		BindUtils.postNotifyChange(null, null, this, "formModel");
		setOpen(true);
		setContent2Open(false);
	}

	@Command
	public void doOpenFormContent2() {
		getFormModel().createContentModel2();
		BindUtils.postNotifyChange(null, null, this, "formModel");
		setOpen(true);
		setContent2Open(true);
	}

	@Command
	public void doSaveForm() {
		setOpen(false);
	}
	public static class FormModel {
		private ContentModel contentModel = new ContentModel1();

		public FormModel() {
			super();
		}

		public ContentModel getContentModel() {
			return this.contentModel;
		}

		public void setContentModel(ContentModel model) {
			this.contentModel = model;
		}

		public void createContentModel1() {
			setContentModel(new ContentModel1());
		}

		public void createContentModel2() {
			setContentModel(new ContentModel2());
		}
	}

	public static class ContentModel {

		private String commonProperty = "Common Property";

		public ContentModel() {
			super();
		}

		public String getCommonProperty() {
			return this.commonProperty;
		}

		public void setCommonProperty(String value) {
			this.commonProperty = value;
		}

		public String getFormTemplate() {
			return "content1";
		}
	}

	public static class ContentModel1 extends ContentModel {
		private String model1Property = "Model 1 Property";

		public ContentModel1() {
			super();
		}

		public String getModel1Property() {
			return this.model1Property;
		}

		public void setModel1Property(String value) {
			this.model1Property = value;
		}

		public String getFormTemplate() {
			return "content1";
		}
	}

	public static class ContentModel2 extends ContentModel {
		private String model2Property = "Model 2 Property";

		public ContentModel2() {
			super();
		}
		public String getModel2Property() {
			return this.model2Property;
		}
		public void setModel2Property(String value) {
			this.model2Property = value;
		}
		public String getFormTemplate() {
			return "content2";
		}
	}
}