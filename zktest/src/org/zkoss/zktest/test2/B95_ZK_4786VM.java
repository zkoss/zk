package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class B95_ZK_4786VM {
	private int idx = 0;
	private Bean bean = new Bean(idx++);

	@Command
	public void refresh() {
		bean = new Bean(idx++);
		BindUtils.postNotifyChange(null, null, this, "bean");
	}

	public Bean getBean() {
		return bean;
	}

	public class Bean {
		private final int idx;

		public Bean(int idx) {
			this.idx = idx;
		}

		public int getIdx() {
			return idx;
		}
	}
}
