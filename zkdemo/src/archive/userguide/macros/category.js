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
	beforeSize: zk.ie6_ ? function () {
		jq(this.$n("body")).css("width", "").removeClass("demo-categorybar-body-scroll");
	}: zk.$void,
	onSize: function(){
		var width = this.$n().offsetWidth;
		//with scorll or not
		if(width < this.childWidth){
			jq(this.$n("left")).addClass("demo-categorybar-left-scroll");
			jq(this.$n("right")).addClass("demo-categorybar-right-scroll");
			jq(this.$n("body")).addClass("demo-categorybar-body-scroll")
								.width(jq.px0(width- zk(this.$n('body')).sumStyles('lr',jq.margins)));
			this._addScollEvent();
		}else{
			jq(this.$n("left")).removeClass("demo-categorybar-left-scroll");
			jq(this.$n("right")).removeClass("demo-categorybar-right-scroll");
			jq(this.$n("body")).removeClass("demo-categorybar-body-scroll")
								.width(jq.px0(width));
			this.$n("cave").style.marginLeft="0px";
		}

	},
	_addScollEvent:function(){
		//update the animiation distance every time it called
		this.$n("cave").distance = jq(this.$n("body")).width() - this.childWidth;

		if(this.scrollEvent == false){	//only do at first time
			var cave = this.$n("cave"),
				$cave = jq(cave),
				stop = function () {
					$cave.stop(true);
				};
			jq(this.$n("left")).mouseover(function () {
				$cave.animate({
					marginLeft: "0px"
				}, Math.abs(zk.parseInt($cave.css('marginLeft'))/80) * 400);
			}).mouseout(stop);
			jq(this.$n("right")).mouseover(function () {
				$cave.animate({
					marginLeft: cave.distance + "px"
				}, Math.abs((cave.distance - zk.parseInt($cave.css('marginLeft')))/80) * 400);
			}).mouseout(stop);
			this.scrollEvent = true;
		}
	},
	onChildAdded_: _zkf = function (/*child*/) {
		if(this.desktop){
			var childWidth=0, cave = this.$n("cave"), $cave = jq(cave);
			$cave.children().each(function() {
				childWidth += jq(this).width();
			});
			$cave.width(jq.px0(childWidth));
			this.childWidth = childWidth;
		}
	},
	onChildRemoved_: _zkf,
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
		out.push('<div', this.domAttrs_(), '>',
				'<img id="', this.uuid, '-img"','src="',this.getImage() ,
				'" title="',this.getLabel(),'" class="', zcls,'-img"/>',
				'<div id="',this.uuid, '-label" class="', zcls,'-text">',
				this.getLabel(),'</div></div>');
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
