
zk.afterLoad('zkmax.barcode', function () {
	zkmax.barcode.Barcode.registerLibrary(function mybarcode(wgt) {
		var canvas = wgt.$n(); //canvas
		//The section can let user to define library.
		PDF417.init(wgt.getValue());
		var barcode = PDF417.getBarcodeArray();
		canvas.height = parseFloat(wgt.getHeight().replace('px', ''));
		var bh = Math.ceil(canvas.height/ barcode.num_rows),
			bw = bh;
		canvas.width = bw * barcode.num_cols;
		for (var ctx = canvas.getContext("2d"), y = 0, r = 0; r < barcode.num_rows; ++r) {
			for (var x = 0, c = 0; c < barcode.num_cols; ++c)
				1 == barcode.bcode[r][c] && ctx.fillRect(x, y, bw, bh), x += bw;
				y += bh
		};
	}, 'PDF417', ['pdf417']);
});