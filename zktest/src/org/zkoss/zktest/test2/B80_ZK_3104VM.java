package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.json.JSONAware;
import org.zkoss.zk.ui.util.Clients;

@ToServerCommand("dataChange")
public class B80_ZK_3104VM {

	private List<String> origList = new ArrayList<String>(Arrays.asList("\"Peter\""));
	private List<String> emptyList = new ArrayList<String>();
	
	private Pojo origPojo = new Pojo();
	private PojoJ origPojoJ = new PojoJ();
	
	@Command
	public void testEmptyList(@BindingParam("list") List<String> list) {
		list.add("\"Monty\"");
		Clients.log(String.valueOf(list.toString().equals(emptyList.toString())));
	}

	@Command
	public void testList(@BindingParam("list") List<String> list) {
		list.add("\"Monty\"");
		Clients.log(String.valueOf(list.toString().equals(origList.toString())));
	}

	@Command
	public void testPojo(@BindingParam("pojo") Pojo pojo) {
		Clients.log(String.valueOf(pojo.toString().equals(origPojo.toString())));
	}

	@Command
	public void testPojoJ(@BindingParam("pojo") PojoJ pojoJ) {
		Clients.log(String.valueOf(pojoJ.toString().equals(pojoJ.toString())));
		Clients.log(String.valueOf(pojoJ.toJSONString().equals(pojoJ.toJSONString())));
	}

	@Command
	public void dataChange(@BindingParam("data") B80_ZK_3104Object data) {
		Clients.log(String.valueOf(data.getType().equals("myData")));
	}

	public List<String> getOrigList() {
		return origList;
	}
	
	public List<String> getEmptyList() {
		return emptyList;
	}

	public Pojo getOrigPojo() {
		return origPojo;
	}

	public PojoJ getOrigPojoJ() {
		return origPojoJ;
	}


	public class Pojo {
		private String name;

		public Pojo() {
		}
		public Pojo(String type) {
			this.type = type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setName(String name) {
			this.name = name;
		}

		private String type = "withoutName";
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		@Override
		public String toString() {
			return getName() + " " + getType();
		}
	}

	public class PojoJ implements JSONAware {
		private String name;
		private String type = "withoutName";
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		@Override
		public String toString() {
			return "toString : " + getName() + " " + getType();
		}

		@Override
		public String toJSONString() {
			return "toJSONString : " + getName() + " " + getType();
		}
	}
}
