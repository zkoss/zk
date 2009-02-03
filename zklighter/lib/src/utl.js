_z='zul.utl';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.utl.Script = zk.$extends(zk.Widget, {
	/** Returns the content of JavaScript codes to execute.
	 */
	getContent: function () {
		return this._content;
	},
	/** Sets the content of JavaScript code snippet to execute.
	 * <p>Note: the codes won't be evaluated until it is attached the
	 * DOM tree. If it is attached, the JavaScript code sippet is executed
	 * immediately. In additions, it executes only once.
	 * <p>When the JavaScript code snipped is executed, you can access
	 * this widget by use of <code>this</code>. In other words,
	 * it was executed in the context of a member method of this widget.
	 * @param cnt the content. It could be a String instance, or
	 * a Function instance.
	 */
	setContent: function (cnt) {
		this._content = cnt;
		if (cnt) {
			this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
			if (this.desktop) //check parent since no this.getNode()
				this._exec();
		} else
			this._fn = null;
	},
	/** Returns the src of the JavaScript file.
	 */
	getSrc: function () {
		return this._src;
	},
	/** Sets the src (URI) of the JavaScript file.
	 */
	setSrc: function (src) {
		this._src = src;
		if (src) {
			this._srcrun = false;
			if (this.desktop)
				this._exec();
		}
	},

	/** Returns the charset. */
	getCharset: function () {
		return this._charset;
	},
	/** Sets the charset. */
	setCharset: function (charset) {
		this._charset = charset;
	},

	_exec: function () {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		this.packages = null; //only once
		pkgs = pkgs.split(',');
		for (var j = 0, l = pkgs.length; j < l;)
			zPkg.load(pkgs[j++].trim());

		if (zPkg.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	},
	_exec0: function () {
		var wgt = this, fn = this._fn;
		if (fn) {
			this._fn = null; //run only once
			zk.afterMount(function () {fn.apply(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			zDom.appendScript(this._src, this._charset);
		}
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this._exec();
	}
});

(_zkwg=_zkpk.Script).prototype.className='zul.utl.Script';
zul.utl.Timer = zk.$extends(zk.Widget, {
	_running: true,
	_delay: 0,

	isRepeats: function () {
		return this._repeats;
	},
	setRepeats: function (repeats) {
		if (this._repeats != repeats) {
			this._repeats = repeats;
			if (this.desktop) this._sync();
		}
	},
	getDelay: function () {
		return this._delay;
	},
	setDelay: function (delay) {
		if (this._delay != delay) {
			this._delay = delay;
			if (this.desktop) this._sync();
		}
	},
	isRunning: function () {
		return this._running;
	},
	setRunning: function (running) {
		if (this._running != running) {
			this._running = running;
			if (this.desktop) this._sync();
		}
	},
	play: function () {
		this.setRunning(true);
	},
	stop: function () {
		this.setRunning(false);
	},

	_sync: function () {
		this._stop();
		this._play();
	},
	_play: function () {
		if (this._running) {
			var fn = this.proxy(this._tmfn);
			if (this._repeats)
				this._iid = setInterval(fn, this._delay);
			else
				this._tid = setTimeout(fn, this._delay);
		}
	},
	_stop: function () {
		var id = this._iid;
		if (id) {
			this._iid = null;
			clearInterval(id)
		}
		id = this._tid;
		if (id) {
			this._tid = null;
			clearTimeout(id);
		}
	},
	_tmfn: function () {
		this.fire('onTimer', null, {ignorable: true});
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._running) this._play();
	},
	unbind_: function () {
		this._stop();
		this.$supers('unbind_', arguments);
	}
});

(_zkwg=_zkpk.Timer).prototype.className='zul.utl.Timer';
zul.utl.Style = zk.$extends(zk.Widget, {
	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			this._content = null;
			if (this.desktop) this._updLink();
		}
	},
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!zk.bootstrapping)
			throw "Content cannot be changed after bootstrapping";
		this._content = content;
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._gened) this._gened = false; //<style> gened
		else this._updLink();
	},
	unbind_: function () {
		zDom.remove(this._getLink());
		this.$supers('unbind_', arguments);
	},
	_updLink: function () {
		var head = this._getHead(),
			ln = this._getLink(head),
			n = this.getNode();
		if (n) n.innerHTML = '';
		if (ln) ln.href = this._src;
		else {
			ln = document.createElement("LINK");
			ln.id = this.uuid;
			ln.rel = "stylesheet";
			ln.type = "text/css";
			ln.href = this._src;
			head.appendChild(ln);
		}
	},
	_getHead: function () {
		return head = document.getElementsByTagName("HEAD")[0];
	},
	_getLink: function (head) {
		head = head || this._getHead();
		for (var lns = head.getElementsByTagName("LINK"), j = lns.length,
		uuid = this.uuid; --j >= 0;)
			if (lns[j].id == uuid)
				return lns[j];
	},
	redraw: function (out) {
		//IE: unable to look back LINK or STYLE with ID
		if (zk.bootstrapping && this._content) {
			out.push('<span style="display:none" id="',
				this.uuid, '"><style type="text/css">\n',
				this._content, '\n</style></span>\n');
			this._gened = true;
		}
	}
});
(_zkwg=_zkpk.Style).prototype.className='zul.utl.Style';
}finally{zPkg.end(_z);}}