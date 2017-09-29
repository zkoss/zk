package org.zkoss.zktest.test2;


import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.List;

public class B85_ZK_3580VM {
	private Concept selected;

	public Concept getSelected() {
		return selected;
	}

	public void setSelected(Concept selected) {
		this.selected = selected;
	}

	@Init
	public void init() {
		selected = null;
	}

	public interface Concept {
		public String getCode();

		public String getDescription();

		public Concept setCode(String code);

		public Concept setDescription(String description);
	}

	public class BasicConcept implements Concept {
		private String code;
		private String description;

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public Concept setCode(String code) {
			this.code = code;
			return this;
		}

		@Override
		public Concept setDescription(String description) {
			this.description = description;
			return this;
		}
	}
}