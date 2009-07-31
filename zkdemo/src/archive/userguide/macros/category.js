// category.js

zk.$package('userguide');
zk.load('zul.wgt', function () {
userguide.Categorybar = zk.$extends(zul.wgt.Div, {
	bind_: function () {
		this.$supers('bind_', arguments);
		zk(this.$n()).disableSelection();
		
		zWatch.listen({onSize: this, beforeSize: this});
		this.onChildAdded_();//for updating sum of category width
		this.scrollEvent = false;
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, beforeSize: this});
		this.$supers('unbind_', arguments);
	},
	beforeSize: zk.ie6_ ? function(evt){
			this.$n("body").style.width = "";
			jq(this.$n("body")).removeClass("demo-categorybar-body-scroll");
	}: zk.$void,
	onSize: function(evt){	
		var width = this.$n().offsetWidth;
		//with scorll or not
		if(width < this.childWidth){
			jq(this.$n("left")).addClass("demo-categorybar-left-scroll");
			jq(this.$n("right")).addClass("demo-categorybar-right-scroll");
			jq(this.$n("body")).addClass("demo-categorybar-body-scroll")
								.width((width-40)+"px");
			this._addScollEvent();	
		}else{
			jq(this.$n("left")).removeClass("demo-categorybar-left-scroll");
			jq(this.$n("right")).removeClass("demo-categorybar-right-scroll");
			jq(this.$n("body")).removeClass("demo-categorybar-body-scroll")
								.width(width+"px");
			this.$n("cave").style.marginLeft="0px";
		}
		
	},
	_addScollEvent:function(){
		//update the animiation distance every time it called
		this.$n("cave").distance = jq(this.$n("body")).width() - this.childWidth;
		
		if(this.scrollEvent == false){	//only do at first time
			var cave = this.$n("cave");
			jq(this.$n("left"))
			.mouseover(function(){
				jq(cave).animate({
					marginLeft: "0px"
				},400);
			})
			.mouseout(function(){
				jq(cave).stop(true);
			});
			
			jq(this.$n("right"))
			.mouseover(function(){
				jq(cave).animate({
					marginLeft: cave.distance + "px"
				},400);
			})
			.mouseout(function(){
				jq(cave).stop(true);
			});	
			this.scrollEvent = true;
		}
	},
	onChildAdded_: function (/*child*/) {
		if(this.desktop){
			var childWidth=0;
			jq(this.$n("cave")).children().each(
				function(){
					childWidth += jq(this).width();
			});
			jq(this.$n("cave")).width(childWidth+"px");
			this.childWidth = childWidth;
		}
	},
	onChildRemoved_: function (/*child*/) {
		if(this.desktop){
			var childWidth=0;
			jq(this.$n("cave")).children().each(
				function(){
					childWidth += jq(this).width();
			});
			jq(this.$n("cave")).width(childWidth+"px");
			this.childWidth = childWidth;
		}
	},
	redraw: function (out) {
		var uuid = this.uuid;
		out.push('<div', this.domAttrs_(), '>',
			'<div id="', uuid, '-right"></div>',
			'<div id="', uuid, '-left"></div>',
			'<div id="', uuid, '-body" class="', this.getZclass(), '-body">',
			'<div id="', uuid, '-cave">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('<div class="z-clear"></div></div></div></div>');
	}

});
});

zk.load('zul.wgt', function () {
userguide.Category = zk.$extends(zul.wgt.Button, {
	redraw: function (out) {
		var zcls = this.getZclass();
		out.push('<div', this.domAttrs_(), '>');
			out.push('<img id="', this.uuid, '-img"','src="',this.getImage() ,'" title="',this.getLabel(),'" class="', zcls,'-img"/>');
			
			out.push('<div id="',this.uuid, '-label" class="', zcls,'-text">');
			
			out.push(this.getLabel());
		out.push('</div></div>');
	},
	doMouseOver_: function (evt) {
		jq(this.$n()).addClass("demo-over");
	},
	doMouseOut_: function (evt) {
		if (zk.ie && jq.isAncestor(this.$n(), evt.domEvent.relatedTarget || evt.domEvent.toElement))
			return; //nothing to do
		jq(this.$n()).removeClass("demo-over");
	},
	doClick_: function (evt){
		this.$supers('doClick_', arguments);
		jq(this.$n()).addClass("demo-seld")
						  .siblings().removeClass("demo-seld");
	}
});
});

function onSelect(wgt, deselect) {
	wgt = zk.Widget.$(wgt);
	//port onSelect in zk 3
}
