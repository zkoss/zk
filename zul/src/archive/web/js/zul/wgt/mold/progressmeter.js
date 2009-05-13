function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><span id="',
	this.uuid,'$img"', 'class="',zcls,'-img"></span></div>');
}