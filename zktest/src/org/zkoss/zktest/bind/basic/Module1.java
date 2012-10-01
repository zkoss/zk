package org.zkoss.zktest.bind.basic;
public class Module1 implements Module {

		String name;
		int amount = 33;

		public Module1(String name,int amount) {
			this.name = name;
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		@Override
		public String getUri() {
			return "modulize-moudle1.zul";
		}
	}