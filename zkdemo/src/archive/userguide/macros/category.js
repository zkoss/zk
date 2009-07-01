// category.js

zk.$package('userguide');
zPkg.load('zul.wgt', null, function () {
userguide.Categorybar = zk.$extends(zul.wgt.Div, {
	bind_: function () {
		this.$supers('bind_', arguments);
		zk(this.getNode()).disableSelection();
		
		zWatch.listen({onSize: this, onShow: this});
		this.onChildAdded_();//for updating sum of category width
		this.scrollEvent = false;
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	},
	onSize: function(evt){	
		var width = jq(this.getNode()).width();
		//with scorll or not
		if(width < this.childWidth){
			jq(this.getSubnode("left")).addClass("demo-categorybar-left-scroll");
			jq(this.getSubnode("right")).addClass("demo-categorybar-right-scroll");
			jq(this.getSubnode("body")).addClass("demo-categorybar-body-scroll")
									   .width((width-40)+"px");
			this._addScollEvent();	
		}else{
			jq(this.getSubnode("left")).removeClass("demo-categorybar-left-scroll");
			jq(this.getSubnode("right")).removeClass("demo-categorybar-right-scroll");
			jq(this.getSubnode("body")).removeClass("demo-categorybar-body-scroll")
									   .width(width+"px");
			this.getSubnode("cave").style.marginLeft="0px";
		}
		
	},
	_addScollEvent:function(){
		//update the animiation distance every time it called
		this.getSubnode("cave").distance = jq(this.getSubnode("body")).width() - this.childWidth;
		
		if(this.scrollEvent == false){	//only do at first time
			var cave = this.getSubnode("cave");
			jq(this.getSubnode("left"))
			.mouseover(function(){
				jq(cave).animate({
					marginLeft: cave.distance+"px"
				},1000);
			})
			.mouseout(function(){
				jq(cave).stop(true);
			});
			
			jq(this.getSubnode("right"))
			.mouseover(function(){
				jq(cave).animate({
					marginLeft: "0px"
				},1000);
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
			jq(this.getSubnode("cave")).children().each(
				function(){
					childWidth += jq(this).width();
			});
			jq(this.getSubnode("cave")).width(childWidth+"px");
			this.childWidth = childWidth;
		}
	},
	onChildRemoved_: function (/*child*/) {
		if(this.desktop){
			var childWidth=0;
			jq(this.getSubnode("cave")).children().each(
				function(){
					childWidth += jq(this).width();
			});
			jq(this.getSubnode("cave")).width(childWidth+"px");
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

zPkg.load('zul.wgt', null, function () {
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
		jq(this.getNode()).addClass("demo-over");
	},
	doMouseOut_: function (evt) {
		jq(this.getNode()).removeClass("demo-over");
	},
	doClick_: function (evt){
		this.$supers('doClick_', arguments);
		jq(this.getNode()).addClass("demo-seld")
						  .siblings().removeClass("demo-seld");
	}
});
});

function onSelect(wgt, deselect) {
	wgt = zk.Widget.$(wgt);
	//port onSelect in zk 3
}
