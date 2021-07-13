package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

public class B96_ZK_4892VM {
	Item item;

	private Map<String, String> infoMap = new HashMap<>();

	public B96_ZK_4892VM() {
		item = new Item("A");
		item.setInfo("data");
		infoMap.put("data", "info");
	}

	public Item getItem() {
		return item;
	}

	public Option getOption() {
		return item.getOption();
	}

	public Map<String, String> getInfoMap() {
		return infoMap;
	}

	public Item getAttribute(String attr) {
		return item;
	}

	public Converter getConverter1() {
		return new Converter() {


			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return ((Item) val).getName();
			}


			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}

		};
	}

	public Converter getConverter2() {
		return new Converter() {
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return ((Option) val).getName();
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
		};
	}

	@Command
	public void cmd1() {
		item.setName("B");
		item.setInfo("cmd1");
		infoMap.put("cmd1", "info - cmd1");
		BindUtils.postNotifyChange(null, null, item, ".");
		BindUtils.postNotifyChange(null, null, infoMap, ".");
	}

	@Command
	public void printItemName() {
		Clients.log(item.getName());
	}

	@Command
	public void cmd2() {
		item.getOption().setName("B-option");
		item.setInfo("cmd2");
		infoMap.put("cmd2", "info - cmd2");
		BindUtils.postNotifyChange(null, null, item.option, ".");
		BindUtils.postNotifyChange(null, null, infoMap, ".");
	}

	@Command
	public void cmd3() {
		item.setInfo("cmd3");
		infoMap.put("cmd3", "info - cmd3");
		BindUtils.postNotifyChange(null, null, infoMap, ".");
	}

	@Immutable
	static public class Item {
		String name;
		Option option;
		String info;

		public Item(String name) {
			this.name = name;
			option = new Option(name + "-option");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Option getOption() {
			return option;
		}

		public void setOption(Option option) {
			this.option = option;
		}

		public String getCurrentInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}
	}

	static public class Option {
		String name;

		public Option(String name) {
			this.name = name;

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
