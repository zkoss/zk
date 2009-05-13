zul.wgt.Progressmeter = zk.$extends(zul.Widget, {
	//super//
	_value: '',
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-progressmeter";
	},
	_fixImgWidth: function(val) {
		var n=this.getNode();
		var img = this.getSubnode("img");
		if (img) {
			if (zk.ie6Only) img.style.width = ""; //Bug 1899749
				img.style.width = Math.round((n.clientWidth * val) / 100) + "px";
		}
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		this._fixImgWidth(this._value);
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
	},
	setWidth : function (val){
		this.$supers('setWidth', arguments);
		if(this.getNode()) 
			this._fixImgWidth(this._value);
	}
});

//getter setter part...
zk.def(zul.wgt.Progressmeter,{
	value: function (val) {
		if(this.getNode()) 
			this._fixImgWidth(val);
	}
});

