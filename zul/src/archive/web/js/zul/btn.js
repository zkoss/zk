/* btn.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  9 16:34:55     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
////
// button //
zkButton = {
	init: function (cmp) {
		if (getZKAttr(cmp, "disd")) return;

		var box = $e(cmp.id + "!box");
		zk.listen(box, "click", zkau.onclick);
		zk.listen(box, "dblclick", zkau.ondblclick);
			//we have to handle here since _onDocDClick won't receive it
		zk.disableSelection(box);
		var btn = $real(cmp);
		zk.listen(btn, "focus", zkButton.onfocus);
		zk.listen(btn, "blur", zkButton.onblur);
		zk.listen(box, "mousedown", zkButton.ondown);
		zk.listen(box, "mouseup", zkButton.onup);
		zk.listen(box, "mouseover", zkButton.onover);
		zk.listen(box, "mouseout", zkButton.onout);
	},
	onover: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			box = $e(cmp.id + "!box");
		zk.addClass(box, getZKAttr(cmp, "zcls") + "-over");
	},
	onout: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			box = $e(cmp.id + "!box");
		if (box != zkButton.down_box)
			zk.rmClass(box, getZKAttr(cmp, "zcls") + "-over");
	},
	onfocus: function (evt) {
		if (!evt) evt = window.event;
		var target = Event.element(evt);
		if (!$tag(Event.element(evt))) return; // Firefox 2 will cause a focus error when resize browser.
		var cmp = $outer(target),
			box = $e(cmp.id + "!box");
		zk.addClass(box, getZKAttr(cmp, "zcls") + "-focus");
		zkau.onfocus(evt);
	},
	onblur: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			box = $e(cmp.id + "!box");
		zk.rmClass(box, getZKAttr(cmp, "zcls") + "-focus");
		zkau.onblur(evt);
	},
	ondown: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)),
			box = $e(cmp.id + "!box"),
			zcls = getZKAttr(cmp, "zcls");
		zk.addClass(box, zcls + "-clk");
		zk.addClass(box, zcls + "-over");
		zk.asyncFocus(cmp.id + "!real", 30);
		zkButton.down_box = box;
		zk.listen(document.body, "mouseup", zkButton.onup);
	},
	onup: function (evt) {
		if (!evt) evt = window.event;
		if (zkButton.down_box) {
			var zcls = getZKAttr($outer(zkButton.down_box), "zcls");
			zk.rmClass(zkButton.down_box, zcls + "-clk");
			zk.rmClass(zkButton.down_box, zcls + "-over");
		}
		zkButton.down_box = null;
		zk.unlisten(document.body, "mouseup", zkButton.onup);
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
		case "visibility":
			val = val == "true";
			if (val) action.show($e(cmp.id + "!box"));
			zk.setVisible(cmp, nm, val);
			if (!val) action.hide($e(cmp.id + "!box"));
			else if (zk.gecko2Only) zk.redraw(cmp); //sometimes, FF2 does re-calc
			return true;
		case "style.height":
		case "style.width":
		case "style":
		case "class":
			zkau.setAttr($e(cmp.id + "!box"), nm, val);
			return true;
		case "tabindex":
			if (zk.gecko || zk.safari)
				zkau.setAttr($real(cmp.id), nm, val);
			else 
				zkau.setAttr($e(cmp.id + "!box"), nm, val);
			return true;
		}
	}
};
if (zk.ie) {
	zkButton.onVisi = zkButton.onSize = function(cmp){
		var box = $e(cmp.id + "!box");
		if (box.style.height && box.offsetHeight) {
			var cellHgh = $int(Element.getStyle(box.rows[0].cells[0], "height"));
			if (cellHgh != box.rows[0].cells[0].offsetHeight) {
				box.rows[1].style.height = box.offsetHeight -
				cellHgh - $int(Element.getStyle(box.rows[2].cells[0], "height")) + "px";
			}
		}
	};
}
zkButtonOS = {
	init: function (cmp) {
		zk.listen(cmp, "click", zkau.onclick);
		zk.listen(cmp, "dblclick", zkau.ondblclick);
			//we have to handle here since _onDocDClick won't receive it
		zk.listen(cmp, "focus", zkau.onfocus);
		zk.listen(cmp, "blur", zkau.onblur);
	}
};
//toolbarbutton
zkTbtn = {
	init: function (cmp) {
		zk.listen(cmp, "click", zkTbtn.onclick);
		
		if (!getZKAttr(cmp, "disd")) {
			zk.listen(cmp, "focus", zkau.onfocus);
			zk.listen(cmp, "blur", zkau.onblur);
		}
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt)); // Bug 2446672
		if (getZKAttr(cmp, "disd")) {
			Event.stop(evt);
			return;
		}
		zkau.onclick(evt, true); //Bug 1878839: we shall always fire onClick
		//No need to call zk.proress() since zkau.onclick sends onClick
	}
};

////
// checkbox and radio //
zkCkbox = {};
zkCkbox.init = function (cmp) {
	cmp = $real(cmp);
	zk.listen(cmp, "click", zkCkbox.onclick);
	zk.listen(cmp, "focus", zkau.onfocus);
	zk.listen(cmp, "blur", zkau.onblur);
};
zkCkbox.setAttr = function (cmp, nm, val) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.setAttr(lbl, nm, zk.getTextStyle(val));
	}
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkCkbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.rmAttr(lbl, nm);
	}
	zkau.rmAttr(cmp, nm);
	return true;
};

/** Handles onclick for checkbox and radio. */
zkCkbox.onclick = function (evt) {
	if (!evt) evt = window.event;
	var real = $real(zk.gecko2Only ? evt.currentTarget : Event.element(evt)),
	//bug #2233787 : this is a bug of firefox 2,
	//                           it need get currentTarget
		newval = real.checked;
	if (newval != real.defaultChecked) { //changed
		// bug #1893575 : we have to clean all of the radio at the same group.
		// in addition we can filter unnecessary onCheck with defaultChecked
		if (real.type == "radio") {
			var nms = document.getElementsByName(real.name);
			for (var i = nms.length; --i >= 0;)
				nms[i].defaultChecked = false;
		}
		real.defaultChecked = newval;
		var uuid = $uuid(real);
		zkau.sendasap({uuid: uuid, cmd: "onCheck", data: [newval]});
	}
};

zkRadio = {};
zkRadio.init = zkCkbox.init;
zkRadio.setAttr = zkCkbox.setAttr;
zkRadio.rmAttr = zkCkbox.rmAttr;

//progressmeter//
zkPMeter = {};
zkPMeter.onSize = zkPMeter.onVisi = function (cmp) {
	var img = $e(cmp.id + "!img");
	if (img) {
		if (zk.ie6Only) img.style.width = ""; //Bug 1899749
		var val = $int(getZKAttr(cmp, "value"));
		img.style.width = Math.round((cmp.clientWidth * val) / 100) + "px";
	}
};
zkPMeter.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if ("z.value" == nm || "style.width" == nm)
		zkPMeter.onSize(cmp);
	return true;
};
