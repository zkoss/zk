package org.zkoss.zktest.bind.databinding.el;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zktest.bind.databinding.bean.Person;

public class RuntimeEvalVM {
	private String result = "";
	private boolean checked = false;
	private String selectedStr = null;
	private List selectedList = null;
	private Set selectedSet = null;
	private Map selectedMap = null;
	private String[] selectedArray = null;
	private Bean selectedBean = null;
	private String currentField = "firstName";
	private Person person;
	private Map<String, String> personMap;

	@Init
	public void init(@BindingParam("arg1") String arg1, @ContextParam(ContextType.DESKTOP) Desktop desktop) {
		System.out.println("init: arg1: " + arg1);
		desktop.addListener(new DesktopCleanup() {
			@Override
			public void cleanup(Desktop desktop) throws Exception {
				System.out.println(desktop.getId() + " is destroyed");
			}
		});

		person = new Person();
		person.setFirstName("Dennis");
		person.setLastName("Watson");
		personMap = new HashMap<>();
		personMap.put("firstName", "Dennis");
	}

	@Command
	@NotifyChange("result")
	public void command1() {
		result = "command 1 called";
		checked = !checked;
	}

	@Command
	@NotifyChange("result")
	public void command2() {
		result = "command 2 called";
		checked = !checked;
	}

	@Command
	@NotifyChange({"selectedStr", "selectedList", "selectedSet", "selectedMap", "selectedArray", "selectedBean"})
	public void updateToEmpty() {
		selectedStr = "";
		selectedList = new ArrayList();
		selectedSet = new HashSet();
		selectedMap = new HashMap();
		selectedArray = new String[]{};
		selectedBean = new Bean();
	}

	@Command
	@NotifyChange({"selectedStr", "selectedList", "selectedSet", "selectedMap", "selectedArray", "selected"})
	public void updateToNotEmpty() {
		selectedStr = "1";
		selectedList = new ArrayList();
		selectedList.add("1");
		selectedSet = new HashSet();
		selectedSet.add("1");
		selectedMap = new HashMap();
		selectedMap.put("key", "value");
		selectedArray = new String[]{"1"};
		selectedBean = new Bean(false);
	}


	public String getResult() {
		return result;
	}

	public boolean isChecked() {
		return checked;
	}

	public String getSelectedStr() {
		return selectedStr;
	}

	public List getSelectedList() {
		return selectedList;
	}

	public Set getSelectedSet() {
		return selectedSet;
	}

	public Map getSelectedMap() {
		return selectedMap;
	}

	public String[] getSelectedArray() {
		return selectedArray;
	}


	class Bean {
		private boolean empty;

		public Bean() {
			this.empty = true;
		}

		public Bean(boolean empty) {
			this.empty = empty;
		}

		public boolean isEmpty() {
			return empty;
		}

		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
	}

	public String getCurrentField() {
		return currentField;
	}

	public Person getPerson() {
		return person;
	}

	public Map<String, String> getPersonMap() {
		return personMap;
	}

	public String concat(String s1, String s2) {
		return s1.concat(s2);
	}

	public Bean getSelectedBean() {
		return selectedBean;
	}

}
