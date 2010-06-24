/* Auxbutton.js

	Purpose:
		
	Description:
		
	History:
		Mon Mar 30 13:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An auxiliary button is used for {@link zul.inp.ComboWidget} 
 */
zul.Auxbutton = zk.$extends(zk.Object, {
	/** Constructor
	 * @param zk.Widget wgt the widget that the button belongs to
	 * @param DOMElement btn the button element
	 * @param DOMElement ref the input element
	 */
	$init: function (wgt, btn, ref) {
		this._wgt = wgt;
		this._btn = btn;
		this._ref = ref;

		var $btn = jq(btn);
		$btn.zk.disableSelection();

		if (!wgt.$weave)
			$btn.mouseover(this.proxy(this._domOver))
				.mouseout(this.proxy(this._domOut))
				.mousedown(this.proxy(this._domDown));
	},
	/**
	 * Cleans the button setting, and then you cannot use the object any more.
	 */
	cleanup: function () {
		var $btn = jq(this._btn);

		$btn.zk.enableSelection();

		if (!this._wgt.$weave)
			$btn.unbind('mouseover', this.proxy(this._domOver))
				.unbind('mouseout', this.proxy(this._domOut))
				.unbind('mousedown', this.proxy(this._domDown));
	},
	_domOver: function () {
		var wgt = this._wgt,
			inp = wgt.getInputNode(),
			zcls = wgt.getZclass(),
			inRoundedMold = wgt.inRoundedMold();
		
		if (!wgt.isDisabled() && !zk.dragging) {
		
			if (inRoundedMold && !wgt._buttonVisible) return;
			
			jq(this._btn).addClass(zcls + "-btn-over");
			
			if (inRoundedMold && !jq(inp).hasClass(zcls + '-text-invalid'))
				jq(inp).addClass(zcls + "-inp-over");
		}
	},
	_domOut: function () {
		var wgt = this._wgt,
			zcls = wgt.getZclass();
		if (!wgt.isDisabled() && !zk.dragging) {
			jq(this._btn).removeClass(zcls + "-btn-over");
			jq(wgt.getInputNode()).removeClass(zcls + "-inp-over");
		}
	},
	_domDown: function () {
		var wgt = this._wgt,
			inp = wgt.getInputNode(),
			zcls = wgt.getZclass(),
			inRoundedMold = wgt.inRoundedMold();
			
		if (!wgt.isDisabled() && !zk.dragging) {
			var $Auxbutton = zul.Auxbutton,
				curab = $Auxbutton._curab;
			if (curab) curab._domUp();

			if (inRoundedMold && !wgt._buttonVisible) return;

			jq(this._btn).addClass(zcls + "-btn-clk");
			
			if (inRoundedMold && !wgt.isReadonly() && !jq(inp).hasClass(zcls + '-text-invalid'))
				jq(inp).addClass(zcls + "-inp-clk");			

			jq(document).mouseup(this.proxy(this._domUp));

			$Auxbutton._curab = this;
		}
	},
	_domUp: function () {
		var $Auxbutton = zul.Auxbutton,
			curab = $Auxbutton._curab;
		if (curab) {
			$Auxbutton._curab = null;
			var wgt = curab._wgt,
				zcls = wgt.getZclass();
				
			if (wgt.inRoundedMold() && !wgt._buttonVisible) return;
			
			jq(curab._btn).removeClass(zcls + "-btn-clk");
			jq(wgt.getInputNode()).removeClass(zcls + "-inp-clk");
			
			jq(document).unbind("mouseup", curab.proxy(this._domUp));
		}
	}
});
