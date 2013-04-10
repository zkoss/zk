package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class PerformanceVM {

	String base1 = "/bind/basic/performanceInner1.zul";
	String base2 = "/bind/basic/performanceInner2.zul";

	int count = 0;

	String src1 = base1;
	String src2 = base2;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSrc1() {
		return src1;
	}

	public void setSrc1(String src1) {
		this.src1 = src1;
	}

	public String getSrc2() {
		return src2;
	}

	public void setSrc2(String src2) {
		this.src2 = src2;
	}

	@Command
	@NotifyChange({ "count", "src1", "src2" })
	public void switchCase() {
		count++;
		src1 = base1 + "?ts=" + count;
		src2 = base2 + "?ts=" + count;
	}

}
