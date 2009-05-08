/* Listcell.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listcell = zk.$extends(zul.LabelImageWidget, {
	getListbox: function () {
		var p = this.parent;
		return p ? p.parent: null;
	},

	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listcell" : this._zclass;
	},
	getTextNode_: function () {
		return zDom.firstChild(this.getNode(), "DIV");
	},
	domContent_: function () {
		var s1 = this.$supers('domContent_', arguments),
			s2 = this._colHtmlPre();
		return s1 ? s2 ? s1 + '&nbsp;' + s2: s1: s2;
	},
	_colHtmlPre: function () {
		var s = '',
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this) {
			//TODO
			/*if (item instanceof Listgroup) {
				sb.append("<img src=\"")
				.append(getDesktop().getExecution().encodeURL("~./img/spacer.gif"))
				.append("\" class=\"").append(item.getZclass()+"-img ")
				.append(item.getZclass()).append(((Listgroup) item).isOpen() ? "-img-open" : "-img-close")
				.append("\" align=\"absmiddle\"/>");
			}*/
			if (box.isCheckmark()) {
				var item = this.parent;
				var chkable = item.isCheckable();
				s += '<input type="' + (box.isMultiple() ? 'checkbox': 'radio')
					+ '" id="' + item.uuid + '$cm"';
				if (!chkable || item.isDisabled())
					s += ' disabled="disabled"';
				if (item.isSelected())
					s += ' checked="checked"';
				if (!box.isMultiple()) 
					s += ' name="' + box.uuid + '"';
				if (!chkable)
					s += ' style="visibility:hidden"';
				s += '/>';
			}
		}
		return s;
	}
});
