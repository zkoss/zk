zul.wnd.Messagebox = {
	onBind: function (label, minWidth) {
		var win = label.$o(),
			outer = win.$n(),
			msgbox = label.$n().parentElement,
			outerOrigWd = outer.offsetWidth;

		if (minWidth) {
			outer.style.width = jq.px0(Math.min(zk.parseInt(minWidth), jq.innerWidth() - 20));
			msgbox.style.width = jq.px0(zk.parseInt(minWidth) - (outerOrigWd - msgbox.offsetWidth));
		}
		zk(outer).center();
		var top = zk.parseInt(outer.style.top), y = jq.innerY();
		if (y) {
			var y1 = top - y;
			if (y1 > 100) outer.style.top = jq.px0(top - (y1 - 100));
		} else if (top > 100)
			outer.style.top = '100px';
		win.onSize();
	}
};