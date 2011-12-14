zul.wnd.Messagebox = {
	onBind: function (label, minWidth) {
		var node = label.$n(),
			tr = node.parentNode.parentNode.parentNode,
			width = node.offsetWidth + tr.cells[0].offsetWidth,
			win = label.$o(),
			cave = win.$n('cave'),
			outer = win.$n();
		width += zk(cave).padBorderWidth()
			+ zk((cave = cave.parentNode)).padBorderWidth()
			+ zk((cave = cave.parentNode)).padBorderWidth()
			+ zk((cave = cave.parentNode)).padBorderWidth();

		outer.style.width = jq.px0(Math.min(Math.max(width, zk.parseInt(minWidth)), jq.innerWidth() - 20));
		zk(outer).center();
		var top = zk.parseInt(outer.style.top), y = jq.innerY();
		if (y) {
			var y1 = top - y;
			if (y1 > 100) outer.style.top = jq.px0(top - (y1 - 100));
		} else if (top > 100)
			outer.style.top = "100px";
		win.onSize();
	}
};