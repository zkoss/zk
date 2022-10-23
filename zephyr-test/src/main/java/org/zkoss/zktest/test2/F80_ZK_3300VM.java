package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class F80_ZK_3300VM {
	private boolean checked1 = false;
	private boolean checked2 = false;
	private String desc = "ddd";
	private String download = "123";
	private String href = "./F80-ZK-3300.zul";
	private String hreflang = "en";
	private String media = "print and (resolution:300dpi)";
	private String rel = "nofollow";
	private String target = "_blank";
	private String type = "text/html";

	public F80_ZK_3300VM() {
	}

	public String getDownload() {
		return download;
	}

	public String getHref() {
		return href;
	}

	public String getHreflang() {
		return hreflang;
	}

	public String getMedia() {
		return media;
	}

	public String getRel() {
		return rel;
	}

	public String getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}
	@Command
	@NotifyChange("*")
	public void change() {
		Clients.showNotification("changed");
		download = "321";
		href = "https://www.zkoss.org";
		hreflang = "tw";
		media = "print";
		target = "_self";
	}

	public boolean isChecked2() {
		return checked2;
	}

	public void setChecked2(boolean checked2) {
		this.checked2 = checked2;
	}

	public boolean isChecked1() {
		return checked1;
	}

	public void setChecked1(boolean checked1) {
		this.checked1 = checked1;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
