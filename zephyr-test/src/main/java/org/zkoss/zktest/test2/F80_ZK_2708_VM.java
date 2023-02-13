package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.NotifyChange;

public class F80_ZK_2708_VM {
	private int _ncols = 12;
	private String _spacing = "20/60";

	public int getNcols() {
		return _ncols;
	}

	@NotifyChange("ncols")
	public void setNcols(int ncols) {
		_ncols = ncols;
	}

	public String getSpacing() {
		return _spacing;
	}

	@NotifyChange("spacing")
	public void setSpacing(String spacing) {
		_spacing = spacing;
	}
}
