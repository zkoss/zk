wgt4852.TestWidget = zk.$extends(zul.wgt.Span, {
	$define: {
		prop1: _myFunc = function() {
			console.log("something");
		},
		prop2: _myFunc,
		prop3: myFunc /* forgot '_' */
	}
}, {});