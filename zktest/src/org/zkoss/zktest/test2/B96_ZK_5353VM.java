package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.Init;

public class B96_ZK_5353VM {
	private Map<String, FieldModel> fields = new HashMap<>();

	@Init
	public void init() {
		fields.put("fieldA", new FieldModel("Field A", "aaa"));
		fields.put("fieldB", new FieldModel("Field B", "bbb change me!"));
	}

	public Map<String, FieldModel> getFields() {
		return fields;
	}

	public static class FieldModel {
		String label;
		String value;

		public FieldModel(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}