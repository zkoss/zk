package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.DependsOn;

public class Module2 implements Module {

		String name;
		int x,y;

		public Module2(String name,int x,int y) {
			this.name = name;
			this.x = x;
			this.y = y;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@DependsOn({"x","y"})
		public int getAmount() {
			return x*y;
		}


		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		@Override
		public String getUri() {
			return "modulize-moudle2.zul";
		}
	}