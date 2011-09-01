/* Submit.js

	Purpose:
		
	Description:
		
	History:
		Wed Aug 24 16:18:18 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
zul.fud.Submit = zk.$extends(zul.wgt.Button, {
	submit: function () {
		var f = this.$f('fileupload'),
			self = this;
		function t() {
			if (zul.Upload.isFinish(f)) {
				self.$o().submit();
				clearInterval(self._tmp);
				self._tmp = undefined;
				return true;
			}
		}
		if (t()) return;
		self._tmp = setInterval(t, 800);
		this.setDisabled(true);
		this.nextSibling.setDisabled(true);
		if (zk.ie)
			this.$f('btns').rerender();
	},
	revert: function () {	
		clearInterval(this._tmp);
		this._tmp = undefined;
		this.setDisabled(false);
		this.nextSibling.setDisabled(false);
		if (zk.ie)
			this.$f('btns').rerender();
	}
});
