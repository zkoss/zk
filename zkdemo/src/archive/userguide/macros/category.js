/* JS for userguide's category bar */
// Fix IE6 Graphics
if (zk.ie6Only) {
	zk.addBeforeInit(function () {
    	//a small transparent image used as a placeholder
		BLANK_GIF = zk.getUpdateURI('/web/img/spacer.gif');
    	ALPHA_IMAGE_LOADER = "DXImageTransform.Microsoft.AlphaImageLoader";
    	PNG_FILTER = "progid:" + ALPHA_IMAGE_LOADER + "(src='%1',sizingMethod='%2')";
    	
    	// these file name should be fixed
    	FILE_NAME = "24x24.png|32x32.png|48x48.png|128x128.png|ButtonBlue.png|ButtonGray.png|small.png|small-sel.png"
    				  + "|normal.png|normal-sel.png|large.png|large-sel.png";
    	
    	//regular expression version of the above
    	PNG = new RegExp(String(FILE_NAME).replace(/([\/()[\]{}*+-.,^$?\\])/g, "\\$1"), "i");
    	filtered = [];
    
    	printing = false;
    	zk.listen(window, "beforeprint", function() {
    		printing = true;
    		for (var i = 0; i < filtered.length; i++) removeFilter(filtered[i]);
    	});
    	zk.listen(window, "afterprint", function() {
    		for (var i = 0; i < filtered.length; i++) addFilter(filtered[i]);
    		printing = false;
    	});
    	zk.addInit(function(){fixImage4IE6();});
	});

	function fixImage(element) {
		// we have to preserve width and height
		var image = new Image(element.width, element.height);
		image.onload = function() {
			element.width = image.width;
			element.height = image.height;
			image = null;
		};
		image.src = element.src;
		// store the original url (we'll put it back when it's printed)
		element.pngSrc = element.src;
		// add the AlphaImageLoader thingy
		addFilter(element);
	};
	
	//apply a filter
	function addFilter(element, sizingMethod) {
		var filter = element.filters[ALPHA_IMAGE_LOADER];
		if (filter) {
			filter.src = element.src;
			filter.enabled = true;
		} else {
			element.runtimeStyle.filter = format(PNG_FILTER, element.src, sizingMethod || "scale");
			filtered.push(element);
		}
		//remove the real image
		element.src = BLANK_GIF;
	};
	function format(string) {
		// Replace %n with arguments[n].
		// e.g. format("%1 %2%3 %2a %1%3", "she", "se", "lls");
		// ==> "she sells sea shells"
		// Only %1 - %9 supported.
		var args = arguments;
		var _FORMAT = new RegExp("%([1-" + arguments.length + "])", "g");
		return String(string).replace(_FORMAT, function(match, index) {
			return index < args.length ? args[index] : match;
			});
	};
	function removeFilter(element) {
		element.src = element.pngSrc;
		element.filters[ALPHA_IMAGE_LOADER].enabled = false;
	};
	function fixImage4IE6() {
		var images = document.getElementsByTagName("img");
		for (var len = images.length; --len >= 0; ) {
			if (PNG.test(images[len].src)) {
				fixImage(images[len]);
				zk.listen(images[len], "propertychange", function() {
			   	if (!printing && event.propertyName == "src" &&
			   			event.srcElement.src.indexOf(BLANK_GIF) == -1 &&
			   			PNG.test(event.srcElement.src)) fixImage(event.srcElement);
			 	});
			}
		}
	};
}

function onSelect(cmp, deselect) {
	var parent = cmp.parentNode;
	for (var cn = zk.childNodes(parent, zkCategoryBar._isLegalType), len = cn.length; --len >= 0;) {
		if (!deselect && cn[len] == cmp)
			zk.addClass(cn[len], "demo-seld");
		else
			zk.rmClass(cn[len], "demo-seld");
	}
	if (!deselect) zkCategoryBar.onScrollTo(cmp);
}
zkCategoryBar = {
	init: function (cmp) {
		zk.disableSelection(cmp);
		zk.listen($e(cmp, "right"), "mouseover", this.onClickArrow);
		zk.listen($e(cmp, "right"), "mouseout", this.stop);
		zk.listen($e(cmp, "left"), "mouseover", this.onClickArrow);
		zk.listen($e(cmp, "left"), "mouseout", this.stop);
	},
	onSize: function (cmp) {
		var body = $e(cmp, "body");
		this._forceStyle(cmp, "");
		this._forceStyle(body, "");
		if (zk.ie6Only) {
			this._forceStyle(cmp, zk.revisedSize(cmp, 
					$int(cmp.parentNode.parentNode.style.width) - zk.previousSibling(cmp, "DIV").offsetWidth) + "px");
		} else {
			this._forceStyle(cmp, zk.revisedSize(cmp, cmp.offsetWidth) + "px");	
		}
		this._forceStyle(body, zk.revisedSize(body, cmp.offsetWidth) + "px");
		this._checkScrolling(cmp, body);
	},
	_forceStyle: function (cmp, value) {
		cmp.style.width = zk.ie6Only ? "0px" : "";
		cmp.style.width = value;
	},
	_isLegalType: function (n) {return (n.id);},
	_checkScrolling: function (cmp, body) {
		var cave = $e(cmp, "cave"),
		 	chd = zk.childNodes(cave, zkCategoryBar._isLegalType),
		 	headwidth = body.offsetWidth,
		 	chdwidth = 0;
		chd.forEach(function(n) { chdwidth += $int(n.offsetWidth) + 2;});
		
		if (cmp._scrolling) {
			if (chdwidth <= (headwidth)) {
				cmp._scrolling = false;
				this._fixButton(cmp);
				body.scrollLeft = 0;
			} else {
				var size = body.offsetWidth - $e(cmp, "right").offsetWidth
				- $e(cmp, "left").offsetWidth - 4;
				if (size < 0) size = 0;
				body.style.width = size + "px";
			}
		} else {
			if (chdwidth > (headwidth - 10)) {
				cmp._scrolling = true;
				this._fixButton(cmp);
				var size = body.offsetWidth - $e(cmp, "right").offsetWidth
					- $e(cmp, "left").offsetWidth - 4;
				if (size < 0) size = 0;
				body.style.width = size + "px";
				cave.style.width = "5000px";
			}
		}
	},
	_fixButton : function(cmp) {
		var zcls = getZKAttr(cmp, "zcls");
		zk[cmp._scrolling ? "addClass" : "rmClass"]($e(cmp, "body"), zcls + "-body-scroll");
		zk[cmp._scrolling ? "addClass" : "rmClass"]($e(cmp, "right"), zcls + "-right-scroll");
		zk[cmp._scrolling ? "addClass" : "rmClass"]($e(cmp, "left"), zcls + "-left-scroll");
	},
	onScrollTo: function (cmp) {
		var parent = $outer(cmp.parentNode),
			body = $e(parent, "body"),
			osl = cmp.offsetLeft,
			tosw = cmp.offsetWidth,
			scl = body.scrollLeft,
			hosw = body.offsetWidth;
		if (osl < scl) {
			this.scroll(parent, scl - osl + 2, false, false, true);
		} else if (osl + tosw > scl + hosw) {
			this.scroll(parent, osl + tosw - scl - hosw + 2, false, true, true);			
		}
	},
	onClickArrow: function (evt) {
		var btn = zkau.evtel(evt),
			cmp = $outer(btn),
			body = $e(cmp, "body"),
			chd = zk.childNodes($e(cmp, "cave"), zkCategoryBar._isLegalType),
			scl = body.scrollLeft;
		if (!chd.length) return;
		
		if (btn.id.endsWith("!right")) {
			var hosw = body.offsetWidth;
			for (var i = 0, count = chd.length; i < count; i++) {
				if (chd[i].offsetLeft + chd[i].offsetWidth > scl + hosw) {
					move = chd[i].offsetLeft + chd[i].offsetWidth - scl - hosw + 2;
					if (move == 0 || move == null || isNaN(move))
						return;
					zkCategoryBar.scroll(cmp, move, btn, true);
					return;
				};
			};
		} else { //Scroll to next left tab
			if (scl == chd[0].offsetLeft) {
				clearInterval(zkCategoryBar.run);
				return;
			}
			for (var i = 0, count = chd.length; i < count; i++) {
				if (chd[i].offsetLeft > scl) {
					if (zk.previousSibling(chd[i], "DIV") == null)  return;
					move = scl - zk.previousSibling(chd[i], "DIV").offsetLeft + 2;
					if (isNaN(move)) return;
					zkCategoryBar.scroll(cmp, move, btn);
					return;
				};
			};
			move = scl - chd[chd.length-1].offsetLeft + 2;
			if (isNaN(move)) return;
			zkCategoryBar.scroll(cmp, move, btn);
		}
	},
	stop: function () {
		clearInterval(zkCategoryBar.run);
	},
	scroll: function(cmp, move, btn, isRight, stopPropagate) {
		if (move <= 0) return;
		var step, body = $e(cmp, "body");
		step = move <= 60 ? 5 : eval(5 * ($int(move / 60) + 1));
		clearInterval(zkCategoryBar.run);
		zkCategoryBar.run = setInterval(function() {
			if (move == 0) {
				if (!stopPropagate)
					zkCategoryBar.onClickArrow(btn);
				else clearInterval(zkCategoryBar.run);
				return;
			} else {
				move < step ? zkCategoryBar.goscroll(body, isRight, move) :
						zkCategoryBar.goscroll(body, isRight, step);
				move = move - step;
				move = move < 0 ? 0 : move;
			}
		}, 20);
	},
	goscroll: function(body, isRight, step) {
		body.scrollLeft = isRight ? (body.scrollLeft + step) : (body.scrollLeft - step);
		body.scrollLeft = (body.scrollLeft <= 0 ? 0 : body.scrollLeft);
	}
};
