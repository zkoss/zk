(function () {
zul.ContainerWidget = zk.$extends(zul.Widget, {
	_contentNode: null,
	bind_: function () {
		this.$supers(zul.ContainerWidget, 'bind_', arguments);
		
		//B70-ZK-2069: some widget need fire onScroll event, which has characteristic of container
		if (jq(this).data('scrollable'))
			this.bindScroll_();
	},
	bindScroll_: function () {
		_contentNode = jq(this.getContentNode());
		if (_contentNode) {
			var wgt = this;
			_contentNode.bind('scroll', function () {
				zWatch.fireDown('onScroll', wgt);
			});
		}
	},
	getContentNode: function () {
		return this.getCaveNode();
	},
	unbind_: function () {
		if (_contentNode)
			_contentNode.unbind('onScroll');
		this.$supers(zul.ContainerWidget, 'unbind_', arguments);
	}
});
})();