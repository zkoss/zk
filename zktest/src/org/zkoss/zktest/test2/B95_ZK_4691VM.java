package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B95_ZK_4691VM {
	private static String Sub1ZUL = "B95-ZK-4691-1.zul";
	private static String Sub2ZUL = "B95-ZK-4691-2.zul";
	private String src = Sub1ZUL;
	private Long arg = System.currentTimeMillis();
	private Boolean change = true;
	private NavInfo navInfo = new NavInfo();

	public B95_ZK_4691VM() {
		navInfo.setSrc(Sub1ZUL);
		navInfo.setArg(System.currentTimeMillis());
	}

	@Command
//	@NotifyChange({"src", "arg"})
	@NotifyChange("src")
	public void switchSrc() {
		if (src.equals(Sub1ZUL)) {
			src = Sub2ZUL;
		} else {
			src = Sub1ZUL;
		}
//		BindUtils.postNotifyChange(null, null, this, "src");

		if (change) {
			changeArg();
		}

		navInfo.setSrc(src);
		navInfo.setArg(arg);
		BindUtils.postNotifyChange(null, null, this, "navInfo");
	}

	@Command
	public void changeArg() {
		arg = System.currentTimeMillis();
		BindUtils.postNotifyChange(null, null, this, "arg");
	}

	public static class NavInfo {
		private String src;
		private Long arg;

		public String getSrc() {
			return src;
		}

		public void setSrc(String src) {
			this.src = src;
		}

		public Long getArg() {
			return arg;
		}

		public void setArg(Long arg) {
			this.arg = arg;
		}
	}

	public String getSrc() {
		return src;
	}

	public Long getArg() {
		return arg;
	}

	public Boolean getChange() {
		return change;
	}

	public void setChange(Boolean change) {
		this.change = change;
	}

	public NavInfo getNavInfo() {
		return navInfo;
	}
}
