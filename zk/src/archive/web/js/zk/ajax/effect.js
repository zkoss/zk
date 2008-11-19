/* effect.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 14:45:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us effects.js v1.7.0
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The low level animation effect.
 * You don't use this class. Rather, use {@link zAnima} instead.
 */
zEffect = {
	fade: function(element, opts) {
		element = zDom.$(element);
		var oldOpacity = element.style.opacity || '';
		opts = zk.$default(opts, {to: 0.0});
		if (!opts.from) opts.from = zDom.getOpacity(element) || 1.0;
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) { 
				if(effect.opts.to==0) {
					var e = effect.element;
					zDom.hide(e);
					zDom.setStyle(e, {opacity: oldOpacity}); 
				}
			};
		return new zEffect.Opacity(element,opts);
	},
	appear: function(element, opts) {
		element = zDom.$(element);
		opts = zk.$default(opts, {to: 1.0});
		if (!opts.from)
			opts.from = zDom.getStyle(element, 'display') == 'none' ? 0.0 : zDom.getOpacity(element) || 0.0;
		
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) { 
				zDom.rerender(effect.element);
				// force Safari to render floated elements properly
			};
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				var e = effect.element;
				zDom.setOpacity(e, effect.opts.from);
				zDom.show(e);
			};
		return new zEffect.Opacity(element,opts);
	},

	puff: function(element, opts) {
		element = zDom.$(element);
		var oldStyle = { 
			opacity: element.style.opacity || '', 
			position: zDom.getStyle(element, 'position'),
			top:  element.style.top,
			left: element.style.left,
			width: element.style.width,
			height: element.style.height
		};
		opts = zk.$default(opts, {duration: 1.0});
		if (!opts.beforeSetupInternal)
			opts.beforeSetupInternal = function(effect) {
				zDom.absolutize(effect.effects[0].element)
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.effects[0].element;
				zDom.hide(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zEffect.Parallel(
			[ new zEffect.Scale(element, 200, 
				{sync: true, scaleFromCenter: true, scaleContent: true, restoreAfterFinish: true}),
				new zEffect.Opacity(element, {sync: true, to: 0.0}) ],
			opts);
	},

	blindUp: function(element, opts) {
		element = zDom.$(element);
		zDom.makeClipping(element);
		opts = zk.$default(opts,
			{scaleContent: false, scaleX: false, restoreAfterFinish: true});
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.element;
				zDom.hide(e);
				zDom.undoClipping(e);
			};
		return new zEffect.Scale(element, 0, opts);
	},
	blindDown: function(element, opts) {
		element = zDom.$(element);
		var elementDimensions = zDom.getDimensions(element);
		opts = zk.$default(opts,{
			scaleContent: false,
			scaleX: false,
			scaleFrom: 0,
			scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
			restoreAfterFinish: true});
		if (!opts.afterSetup)
			opts.afterSetup = function(effect) {
				var e = effect.element;
				zDom.makeClipping(e);
				zDom.setStyle(e, {height: '0px'});
				zDom.show(e); 
			};
		if (!opts.afterFinishInternal)
			afterFinishInternal = function(effect) {
				zDom.undoClipping(effect.element);
			};
		return new zEffect.Scale(element, 100, opts);
	},

	switchOff: function(element, opts) {
		element = zDom.$(element);
		var oldOpacity = element.style.opacity || '';
		opts = zk.$default(opts, {
			duration: 0.4, from: 0, transition: zEffect._Tranx.flicker});
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				new zEffect.Scale(effect.element, 1, { 
					duration: 0.3, scaleFromCenter: true,
					scaleX: false, scaleContent: false,
					restoreAfterFinish: true,
					beforeSetup: function(effect) {
						var e = effect.element;
						zDom.makePositioned(e);
						zDom.makeClipping(e);
					},
					afterFinishInternal: function(effect) {
						var e = effect.element;
						zDom.hide(e);
						zDom.undoClipping(e);
						zDom.undoPositioned(e);
						zDom.setStyle(e, {opacity: oldOpacity});
					}
				});
			}
		return new zEffect.Appear(element, opts);
	},

	dropOut: function(element, opts) {
		element = zDom.$(element);
		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};
		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				zDom.makePositioned(effect.effects[0].element);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.effects[0].element;
				zDom.hide(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zEffect.Parallel(
			[new zEffect.Move(element, {x: 0, y: 100, sync: true}), 
			new zEffect.Opacity(element, { sync: true, to: 0.0})], opts);
	},

	slideOut: function(element, anchor, opts) {
		anchor = anchor || 't';
		element = zDom.$(element);

		var movement, s = element.style;
		switch (anchor) {
		case 't':
			movement = {x: 0, y: -zk.parseInt(s.height), sync: true};
			break;
		case 'b':
			movement = {x: 0, y: zk.parseInt(s.height), sync: true};
			break;
		case 'l':
			movement = {x: -zk.parseInt(s.width), y: 0, sync: true};
			break;
		case 'r':
			movement = {x: zk.parseInt(s.width), y: 0, sync: true};
			break;
		}

		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};
		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				zDom.makePositioned(effect.effects[0].element); 
			};
		if (!opts.beforeFinishInternal)
			opts.beforeFinishInternal = function (effect) {
				zDom.hide(effect.effects[0].element);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.effects[0].element;
				zDom.undoPositioned(e)
				zDom.setStyle(e, oldStyle);
			}; 
		return new zEffect.Parallel(
			[new zEffect.Move(element, movement)], opts);
	},
	slideIn: function(element, anchor, opts) {
		anchor = anchor || 't';
		element = zDom.$(element);
		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};

		var movement, s = element.style;
		switch (anchor) {
		case 't':
			var t = zk.parseInt(s.top), h = zk.parseInt(s.height);
			s.top = t - h + "px";
			movement = {x: 0, y: h, sync: true};
			break;
		case 'b':
			var t = zk.parseInt(s.top), h = zk.parseInt(s.height);
			s.top = t + h + "px";
			movement = {x: 0, y: -h, sync: true};
			break;
		case 'l':
			var l = zk.parseInt(s.left), w = zk.parseInt(s.width);
			s.left = l - w + "px";
			movement = {x: w, y: 0, sync: true};
			break;
		case 'r':
			var l = zk.parseInt(s.left), w = zk.parseInt(s.width);
			s.left = l + w + "px";
			movement = {x: -w, y: 0, sync: true};
			break; 
		}

		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				var e = effect.effects[0].element;
				zDom.show(e);
				zDom.makePositioned(e);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.effects[0].element;
				zDom.undoPositioned(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zEffect.Parallel(
			[new zEffect.Move(element, movement)], opts);
	},

	slideDown: function(element, anchor, opts) {
		anchor = anchor || 't';
		element = zDom.$(element);
		zDom.cleanWhitespace(element);

		// SlideDown need to have the content of the element wrapped in a container element with fixed height!
		var orig = {t: zDom.getStyle(element, 'top'), l: zDom.getStyle(element, 'left')},
			isVert = anchor == 't' || anchor == 'b',
			dims = zDom.getDimensions(element);

		opts = zk.$default(opts, {
			scaleContent: false,
			scaleX: !isVert, scaleY: isVert,
			scaleFrom: zk.opera ? 0 : 1,
			scaleMode: {originalHeight: dims.height, originalWidth: dims.width},
			restoreAfterFinish: true
		});
		if (!opts.afterSetup)
			opts.afterSetup = function(effect) {
				var e = effect.element;
				zDom.makePositioned(e);
				switch (anchor) {
				case 't':
					zDom.makeClipping(e);
					zDom.setStyle(e, {height: '0px'});
					zDom.show(e);
					break;
				case 'b':
					orig.ot = dims.top + dims.height;
					zDom.makeClipping(e);
					zDom.setStyle(e, {height: '0px', top: orig.ot + 'px'});
					zDom.show(e);
					break;
				case 'l':
					zDom.makeClipping(e);
					zDom.setStyle(e, {width: '0px'});
					zDom.show(e);
					break;
				case 'r':
					orig.ol = dims.left + dims.width;
					zDom.makeClipping(e);
					zDom.setStyle(e, {width: '0px', left: orig.ol + 'px'});
					zDom.show(e);
					break;
				}
			};
		if (!opts.afterUpdateInternal)
			opts.afterUpdateInternal = function(effect){
				var e = effect.element;
				if (anchor == 'b')
					zDom.setStyle(e, {top: (orig.ot - zk.parseInt(effect.element.style.height)) + 'px'});
				else if (anchor == 'r')
					zDom.setStyle(e, {left: (orig.ol - zk.parseInt(effect.element.style.width)) + 'px'});
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.element;
				zDom.undoClipping(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, {top: orig.t, left: orig.l});
			};
		return new zEffect.Scale(element, 100, opts);
	},
	slideUp: function(element, anchor, opts) {
		anchor = anchor || 't';
		element = zDom.$(element);
		zDom.cleanWhitespace(element);

		var orig = {t: zDom.getStyle(element, 'top'), l: zDom.getStyle(element, 'left')},
			isVert = anchor == 't' || anchor == 'b';

		opts = zk.$default(opts, {
			scaleContent: false, 
			scaleX: !isVert, scaleY: isVert,
			scaleMode: 'box', scaleFrom: 100,
			restoreAfterFinish: true});
		if (!opts.beforeStartInternal)
			opts.beforeStartInternal = function(effect) {
				var e = effect.element;
				zDom.makePositioned(e);
				zDom.makeClipping(e);
				zDom.show(e);
				orig.ot = e.offsetTop;
				orig.oh = e.offsetHeight;
				orig.ol = e.offsetLeft;
				orig.ow = e.offsetWidth;
			};
		if (!opts.afterUpdateInternal)
			opts.afterUpdateInternal = function (effect) {
				var e = effect.element;
				if (anchor == 'b')
					zDom.setStyle(e, {top: (orig.ot + orig.oh - zk.parseInt(effect.element.style.height)) + 'px'});
				else if (anchor == 'r')
					zDom.setStyle(e, {left: (orig.ol + orig.ow - zk.parseInt(effect.element.style.width)) + 'px'});
			};
		if (!opts.beforeFinishInternal)
			opts.beforeFinishInternal = function (effect) {
				zDom.hide(effect.element);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function (effect) {
				var e = effect.element;
				zDom.undoClipping(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, {top: orig.t, left: orig.l});
			};
		return new zEffect.Scale(element, zk.opera ? 0 : 1, opts);
	}
};

zEffect._Base = zk.$extends(zk.Object, {
	position: null,
	start: function(opts) {
		this.opts = zk.$default(opts, zEffect._defOpts);
		this.name = this.opts.name || "Base";
		this.currentFrame = 0;
		this.state = 'idle';
		this.startOn = this.opts.delay*1000;
		this.finishOn = this.startOn + (this.opts.duration*1000);
		this.event('beforeStart');

		if(!this.opts.sync) zEffect._Queue.add(this);
	},
	loop: function(timePos) {
		if(timePos >= this.startOn) {
			if(timePos >= this.finishOn) {
				this.render(1.0);
				this.cancel();
				this.event('beforeFinish');
				if(this.finish) this.finish(); 
				this.event('afterFinish');
				return;  
			}
			var pos = (timePos - this.startOn) / (this.finishOn - this.startOn);
			var frame = Math.round(pos * this.opts.fps * this.opts.duration);
			if(frame > this.currentFrame) {
				this.render(pos);
				this.currentFrame = frame;
			}
		}
	},
	render: function(pos) {
		if(this.state == 'idle') {
			this.state = 'running';
			this.event('beforeSetup');
			if(this.setup) this.setup();
			this.event('afterSetup');
		}
		if(this.state == 'running') {
			if(this.opts.transition) pos = this.opts.transition(pos);
			pos *= (this.opts.to-this.opts.from);
			pos += this.opts.from;
			this.position = pos;
			this.event('beforeUpdate');
			if(this.update) this.update(pos);
			this.event('afterUpdate');
		}
	},
	cancel: function() {
		if(!this.opts.sync)
			zEffect._Queue.remove(this);
		this.state = 'finished';
	},
	event: function(eventName) {
		if(this.opts[eventName + 'Internal'])
			this.opts[eventName + 'Internal'](this);
		if(this.opts[eventName]) this.opts[eventName](this);
	}
});

zEffect.Parallel = zk.$extends(zEffect._Base, {
	$init: function(effects, opts) {
		this._effects = effects || [];
		this.start(opts);
	},
	update: function(position) {
		this._effects.invoke('render', position);
	},
	finish: function(position) {
		for (var j = 0, effs = this._effects, len = effs.length; j < len;) {
			var ef = effs[j++];
			ef.render(1.0);
			ef.cancel();
			ef.event('beforeFinish');
			if(ef.finish) ef.finish(position);
			ef.event('afterFinish');
		}
	}
});

zEffect.Opacity = zk.$extends(zEffect._Base, {
	$init: function(element, opts) {
		var e = this.element = zDom.$(element);
		// make this work on IE on elements without 'layout'
		if(zk.ie && (!e.currentStyle.hasLayout))
			zDom.setStyle(e, {zoom: 1});
		opts = zk.$default(opts, {to: 1.0});
		if (!opts.from) opts.from = zDom.getOpacity(e) || 0.0,
		this.start(opts);
	},
	update: function(position) {
		zDom.setOpacity(this.element, position);
	}
});

zEffect.Move = zk.$extends(zEffect._Base, {
	$init: function(element, opts) {
		this.element = zDom.$(element);
		opts = zk.$default(opts, {x: 0, y: 0, mode: 'relative'});
		this.start(opts);
	},
	setup: function() {
	// Bug in Opera: Opera returns the "real" position of a static element or
	// relative element that does not have top/left explicitly set.
	// ==> Always set top and left for position relative elements in your stylesheets 
	// (to 0 if you do not need them) 
		var e = this.element;
		zDom.makePositioned(e);
		this.originalLeft = parseFloat(zDom.getStyle(e, 'left') || '0');
		this.originalTop = parseFloat(zDom.getStyle(e, 'top')  || '0');
		if(this.opts.mode == 'absolute') {
			// absolute movement, so we need to calc deltaX and deltaY
			this.opts.x -= this.originalLeft;
			this.opts.y -= this.originalTop;
		}
	},
	update: function(position) {
		zDom.setStyle(this.element, {
			left: Math.round(this.opts.x  * position + this.originalLeft) + 'px',
			top:  Math.round(this.opts.y  * position + this.originalTop)  + 'px'
		});
	}
});

zEffect.Scale = zk.$extends(zEffect._Base, {
	$init: function(element, percent, opts) {
		this.element = zDom.$(element);
		opts = zk.$default(opts, {
			scaleX: true,
			scaleY: true,
			scaleContent: true,
			scaleFromCenter: false,
			scaleMode: 'box', // 'box' or 'contents' or {} with provided values
			scaleFrom: 100.0,
			scaleTo: percent
			});
		this.start(opts);
	},
	setup: function() {
		var el = this.element;
		this.restoreAfterFinish = this.opts.restoreAfterFinish || false;
		this.elementPositioning = zDom.getStyle(el, 'position');
		
		this.originalStyle = {};
		for (var j = 0, styles=['top','left','width','height','fontSize'],
		len = styles.length; j < len;) {
			var s = styles[j++]
			this.originalStyle[s] = el.style[s];
		}

		this.originalTop = el.offsetTop;
		this.originalLeft = el.offsetLeft;

		var fontSize = zDom.getStyle(el, 'font-size') || '100%';
		for (var j = 0, types=['em','px','%','pt'],
		len = types.length; j < len;) {
			var t = types[j++];
			if(fontSize.indexOf(t) > 0) {
				this.fontSize = parseFloat(fontSize);
				this.fontSizeType = t;
			}
		}

		this.factor = (this.opts.scaleTo - this.opts.scaleFrom)/100;

		this.dims = null;
		if(this.opts.scaleMode=='box')
			this.dims = [el.offsetHeight, el.offsetWidth];
		if(/^content/.test(this.opts.scaleMode))
			this.dims = [el.scrollHeight, el.scrollWidth];
		if(!this.dims)
			this.dims = [this.opts.scaleMode.originalHeight,
				this.opts.scaleMode.originalWidth];
	},
	update: function(position) {
		var currentScale = (this.opts.scaleFrom/100.0) + (this.factor * position);
		if(this.opts.scaleContent && this.fontSize)
			zDom.setStyle(this.element, {fontSize: this.fontSize * currentScale + this.fontSizeType});
		this.setDimensions(this.dims[0] * currentScale, this.dims[1] * currentScale);
	},
	finish: function(position) {
		if(this.restoreAfterFinish)
			zDom.setStyle(this.element, this.originalStyle);
	},
	setDimensions: function(height, width) {
		var d = {};
		if(this.opts.scaleX) d.width = Math.round(width) + 'px';
		if(this.opts.scaleY) d.height = Math.round(height) + 'px';
		if(this.opts.scaleFromCenter) {
			var topd = (height - this.dims[0])/2;
			var leftd = (width  - this.dims[1])/2;
			if(this.elementPositioning == 'absolute') {
				if(this.opts.scaleY) d.top = this.originalTop-topd + 'px';
				if(this.opts.scaleX) d.left = this.originalLeft-leftd + 'px';
			} else {
				if(this.opts.scaleY) d.top = -topd + 'px';
				if(this.opts.scaleX) d.left = -leftd + 'px';
			}
		}
		zDom.setStyle(this.element, d);
	}
});

zEffect.ScrollTo = zk.$extends(zEffect._Base, {
	$init: function(element, opts) {
		this.element = zDom.$(element);
		this.start(opts || {});
	},
	setup: function() {
		var innerY = zDom.innerY(),
			offsets = zDom.cmOffset(this.element);
		if(this.opts.offset) offsets[1] += this.opts.offset;
		var max = window.innerHeight ? 
			window.height - window.innerHeight :
			document.body.scrollHeight - 
			(document.documentElement.clientHeight ? 
				document.documentElement.clientHeight : document.body.clientHeight);
		this.scrollStart = innerY;
		this.delta = (offsets[1] > max ? max : offsets[1]) - this.scrollStart;
	},
	update: function(position) {
		window.scrollTo(zDom.innerX(), this.scrollStart + (position*this.delta));
	}
});

zEffect._Tranx = {
	sinoidal: function(pos) {
		return (-Math.cos(pos*Math.PI)/2) + 0.5;
	},
	flicker: function(pos) {
		return ((-Math.cos(pos*Math.PI)/4) + 0.75) + Math.random()/4;
	},
	pulse: function(pos, pulses) { 
		pulses = pulses || 5; 
		return Math.round((pos % (1/pulses)) * pulses) == 0 ? 
			((pos * pulses * 2) - Math.floor(pos * pulses * 2)) : 
			1 - ((pos * pulses * 2) - Math.floor(pos * pulses * 2));
	},
	none: function(pos) {
		return 0;
	},
	full: function(pos) {
		return 1;
	}
};
zEffect._Queue = {
	_effects: [],
	_interval: null,

	add: function(effect) {
		var timestamp = zUtl.now(),
			position = typeof effect.opts.queue == 'string' ? 
				effect.opts.queue : effect.opts.queue.position,
			effque = zEffect._Queue,
			effs = effque._effects;

		switch(position) {
		case 'front':
			// move unstarted effects after this effect  
			for (var j = 0, len = effs.length; j < len;) {
				var ef = effs[j++];
				if (ef.state == 'idle') {
					e.startOn  += effect.finishOn;
					e.finishOn += effect.finishOn;
				}
			}
			break;
		case 'with-last':
			for (var j = 0, len = effs.length; j < len;) {
				var v = effs[j++].startOn;
				if (v > timestamp) timestamp = v;
			}
			break;
		case 'end':
			// start effect after last queued effect has finished
			for (var j = 0, len = effs.length; j < len;) {
				var v = effs[j++].finishOn;
				if (v > timestamp) timestamp = v;
			}
			break;
		}

		effect.startOn  += timestamp;
		effect.finishOn += timestamp;

		if(!effect.opts.queue.limit || (effs.length < effect.opts.queue.limit))
			effs.push(effect);

		if(!effque._interval) 
			effque._interval = setInterval(effque.loop, 15);
	},
	remove: function(effect) {
		var effque = zEffect._Queue, effs = effque._effects;
		effs.$remove(effect);
		if(!effs.length) {
			clearInterval(effque._interval);
			effque._interval = null;
		}
	},
	loop: function() {
		var timePos = zUtl.now(), effs = zEffect._Queue._effects;
		for(var i=0, len=effs.length; i<len; i++) 
			if(effs[i])
				effs[i].loop(timePos);
	}
};

zEffect._defOpts = {
  transition: zEffect._Tranx.sinoidal,
  duration:   1.0,   // seconds
  fps:        60.0,  // max. 60fps due to zEffect.Queue implementation
  sync:       false, // true for combining
  from:       0.0,
  to:         1.0,
  delay:      0.0,
  queue:      'parallel'
};
