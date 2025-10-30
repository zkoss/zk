package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B80_ZK_3562VM {

	private Level1 level1;
	
	@Init
	public void init() {
		level1 = new Level1();
		Level2 level2 = new Level2();
		Level3 level3 = new Level3();
		level1.setLevel2(level2);
		level2.setLevel3(level3);
		level3.setValue("the actual value");
	}
	
	@Command
	public void save() {
		
	}
	
	public Level1 getLevel1() {
		return level1;
	}

	public static class Level1 {
		private Level2 level2;

		public Level2 getLevel2() {
			return level2;
		}

		public void setLevel2(Level2 level2) {
			this.level2 = level2;
		}
	}

	public static class Level2 {
		private Level3 level3;
		
		public Level3 getLevel3() {
			return level3;
		}
		
		public void setLevel3(Level3 level3) {
			this.level3 = level3;
		}
	}
	
	public static class Level3 {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
}