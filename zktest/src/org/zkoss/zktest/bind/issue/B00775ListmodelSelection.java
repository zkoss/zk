package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.converter.sys.ListboxModelConverter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

public class B00775ListmodelSelection {
	private List<String> model;
	private Set<String> selected;
	
	public B00775ListmodelSelection() {
		generateModel(10);
	}

	private void generateModel(int size) {
		model = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			model.add(String.valueOf(i));
		}
	}
	
	public Set<String> getSelected() {
		return selected;
	}

	public void setSelected(Set<String> selected) {
		this.selected = selected;
	}

	public List<String> getModel() {
		return model;
	}
	
	@Command
	@NotifyChange("model")
	public void shrinkModel() {
		generateModel(2);
	}
	
	public static class Converter extends ListboxModelConverter {
		private static final long serialVersionUID = 1L;

		@Override
		public Object coerceToUi(Object val, Component comp, BindContext ctx) {
			Object bean = super.coerceToUi(val, comp, ctx);

			if (bean instanceof ListModelList && comp instanceof Listbox) {
				Listbox listbox = (Listbox) comp;
				ListModelList<?> model = (ListModelList<?>) bean;
				if (listbox.isMultiple() && !model.isMultiple()) {
					model.setMultiple(true);
				}
				for (Object child : listbox.getListhead().getChildren()) {
					final Listheader hd = (Listheader) child;
					if ("natural".equals(hd.getSortDirection())) {
						continue;
					} else if ("ascending".equals(hd.getSortDirection())) {
						model.sort(hd.getSortAscending(), true);
					} else if ("descending".equals(hd.getSortDirection())) {
						model.sort(hd.getSortDescending(), true);
					}
				}
			}

			return bean;
		}
	}
}