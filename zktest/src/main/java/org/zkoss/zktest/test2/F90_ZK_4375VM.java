/* F90_ZK_4375VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 19 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkmax.zul.DefaultStepModel;
import org.zkoss.zkmax.zul.Step;
import org.zkoss.zkmax.zul.StepModel;
import org.zkoss.zul.ListModel;

/**
 * @author jameschu
 */
public class F90_ZK_4375VM {
	private StepModel<TestItem> model;
	private boolean _wrappedLabels;

	@Init
	public void init() {
		model = new DefaultStepModel<>();
		model.add(new TestItem("Step 1", "description 1", "content 1", false, false));
		model.add(new TestItem("Step 2", "description 2", "content 2", false, false));
		model.add(new TestItem("Step 3", "description 3", "content 3", false, false));
		model.setActiveIndex(0);
	}

	@Command
	@NotifyChange("wrappedLabels")
	public void changeWrappedLabels() {
		_wrappedLabels = !_wrappedLabels;
	}

	@Command
	@NotifyChange("model")
	public void changeAllTitle() {
		ListModel<TestItem> steps = model.getSteps();
		for (int i = 0; i < steps.getSize(); i++) {
			TestItem item = steps.getElementAt(i);
			item.setTitle("new " + item.getTitle());
		}
	}

	@Command
	public void onComplete(@BindingParam("step") Step step) {
		model.getSteps().getElementAt(step.getIndex()).setComplete(step.isComplete());
	}

	@Command
	public void back() {
		model.back();
	}

	@Command
	public void next() {
		model.next();
	}

	public StepModel<TestItem> getModel() {
		return model;
	}

	public boolean isWrappedLabels() {
		return _wrappedLabels;
	}

	public class TestItem {
		private String title;
		private String description;
		private String content;
		private boolean complete;
		private boolean error;

		public TestItem(String title, String description, String content, boolean complete, boolean error) {
			this.title = title;
			this.description = description;
			this.content = content;
			this.complete = complete;
			this.error = error;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public boolean isComplete() {
			return complete;
		}

		public void setComplete(boolean complete) {
			this.complete = complete;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}
	}
}
