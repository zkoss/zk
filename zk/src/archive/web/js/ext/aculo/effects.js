// script.aculo.us effects.js v1.7.0, Fri Jan 19 19:16:36 CET 2007

// Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
// Contributors:
//  Justin Palmer (http://encytemedia.com/)
//  Mark Pilgrim (http://diveintomark.org/)
//  Martin Bialasinki
// 
// script.aculo.us is freely distributable under the terms of an MIT-style license.
// For details, see the script.aculo.us web site: http://script.aculo.us/ 
//Tom M. Yeh, Potix: prevent it from load twice
if (!window.z_effects_js) {
	z_effects_js = true;

// converts rgb() and #xxx to #xxxxxx format,  
// returns self (or first argument) if not convertable  
String.prototype.parseColor = function() {  
  var color = '#';
  if(this.slice(0,4) == 'rgb(') {  
    var cols = this.slice(4,this.length-1).split(',');  
    var i=0; do { color += parseInt(cols[i]).toColorPart() } while (++i<3);  
  } else {  
    if(this.slice(0,1) == '#') {  
      if(this.length==4) for(var i=1;i<4;i++) color += (this.charAt(i) + this.charAt(i)).toLowerCase();  
      if(this.length==7) color = this.toLowerCase();  
    }  
  }  
  return(color.length==7 ? color : (arguments[0] || this));  
}

/*--------------------------------------------------------------------------*/

/* Tom M. Yeh, Potix: remove unused codes
Element.collectTextNodes = function(element) {  
  return z$A(z$(element).childNodes).collect( function(node) {
    return (node.nodeType==3 ? node.nodeValue : 
      (node.hasChildNodes() ? Element.collectTextNodes(node) : ''));
  }).flatten().join('');
}

Element.collectTextNodesIgnoreClass = function(element, className) {  
  return z$A(z$(element).childNodes).collect( function(node) {
    return (node.nodeType==3 ? node.nodeValue : 
      ((node.hasChildNodes() && !Element.hasClassName(node,className)) ? 
        Element.collectTextNodesIgnoreClass(node, className) : ''));
  }).flatten().join('');
}
Element.setContentZoom = function(element, percent) {
  element = z$(element);  
  element.setStyle({fontSize: (percent/100) + 'em'});   
  if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);
  return element;
}
*/

Element.getOpacity = function(element){
  return $int(z$(element).getStyle('opacity'));
}

Element.setOpacity = function(element, value){
  return z$(element).setStyle({opacity:value});
}

Element.getInlineOpacity = function(element){
  return z$(element).style.opacity || '';
}

Element.forceRerendering = function(element) {
  try {
    element = z$(element);
    var n = document.createTextNode(' ');
    element.appendChild(n);
    element.removeChild(n);
  } catch(e) { }
};

/*--------------------------------------------------------------------------*/
/* Tom M. Yeh, Potix: remove unused codes
Array.prototype.call = function() {
  var args = arguments;
  this.each(function(f){ f.apply(this, args) });
}
*/
/*--------------------------------------------------------------------------*/

var zEffect = {
  _elNotExistErr: {
    name: 'ElementDoesNotExistError',
    message: 'The specified DOM element does not exist, but is required for this effect to operate'
/* Tom M. Yeh, Potix: remove unused codes
  },
  tagifyText: function(element) {
    if(typeof Builder == 'undefined')
      throw("zEffect.tagifyText requires including script.aculo.us' builder.js library");
      
    var tagifyStyle = 'position:relative';
    if(/MSIE/.test(navigator.userAgent) && !window.opera) tagifyStyle += ';zoom:1';
    
    element = z$(element);
    z$A(element.childNodes).each( function(child) {
      if(child.nodeType==3) {
        child.nodeValue.toArray().each( function(character) {
          element.insertBefore(
            Builder.node('span',{style: tagifyStyle},
              character == ' ' ? String.fromCharCode(160) : character), 
              child);
        });
        Element.remove(child);
      }
    });
  },
  multiple: function(element, effect) {
    var elements;
    if(((typeof element == 'object') || 
        (typeof element == 'function')) && 
       (element.length))
      elements = element;
    else
      elements = z$(element).childNodes;
      
    var options = Object.extend({
      speed: 0.1,
      delay: 0.0
    }, arguments[2] || {});
    var masterDelay = options.delay;

    z$A(elements).each( function(element, index) {
      new effect(element, Object.extend(options, { delay: index * options.speed + masterDelay }));
    });
  },
  PAIRS: {
    'slide':  ['SlideDown','SlideUp'],
    'blind':  ['BlindDown','BlindUp'],
    'appear': ['Appear','Fade']
  },
  toggle: function(element, effect) {
    element = z$(element);
    effect = (effect || 'appear').toLowerCase();
    var options = Object.extend({
      queue: { position:'end', scope:(element.id || 'global'), limit: 1 }
    }, arguments[2] || {});
    zEffect[element.visible() ? 
      zEffect.PAIRS[effect][1] : zEffect.PAIRS[effect][0]](element, options);
*/
  }
};

/* Tom M. Yeh, Potix: remove unused codes
var Effect2 = zEffect; // deprecated
*/
/* ------------- transitions ------------- */

zEffect.Transitions = {
/* Tom M. Yeh, Potix: remove unused codes
  linear: zPrototype.K,
*/
  sinoidal: function(pos) {
    return (-Math.cos(pos*Math.PI)/2) + 0.5;
  },
/* Tom M. Yeh, Potix: remove unused codes
  reverse: function(pos) {
    return 1-pos;
  },
*/
  flicker: function(pos) {
    return ((-Math.cos(pos*Math.PI)/4) + 0.75) + Math.random()/4;
  },
/* Tom M. Yeh, Potix: remove unused codes
  wobble: function(pos) {
    return (-Math.cos(pos*Math.PI*(9*pos))/2) + 0.5;
  },
*/
  pulse: function(pos, pulses) { 
    pulses = pulses || 5; 
    return (
      Math.round((pos % (1/pulses)) * pulses) == 0 ? 
            ((pos * pulses * 2) - Math.floor(pos * pulses * 2)) : 
        1 - ((pos * pulses * 2) - Math.floor(pos * pulses * 2))
      );
  },
  none: function(pos) {
    return 0;
  },
  full: function(pos) {
    return 1;
  }
};

/* ------------- core effects ------------- */

zEffect.ScopedQueue = zClass.create();
Object.extend(Object.extend(zEffect.ScopedQueue.prototype, zEnum), {
  initialize: function() {
    this.effects  = [];
    this.interval = null;
  },
  _each: function(iterator) {
    this.effects._each(iterator);
  },
  add: function(effect) {
    var timestamp = new Date().getTime();
    
    var position = (typeof effect.options.queue == 'string') ? 
      effect.options.queue : effect.options.queue.position;
    
    switch(position) {
      case 'front':
        // move unstarted effects after this effect  
        this.effects.findAll(function(e){ return e.state=='idle' }).each( function(e) {
            e.startOn  += effect.finishOn;
            e.finishOn += effect.finishOn;
          });
        break;
      case 'with-last':
        timestamp = this.effects.pluck('startOn').max() || timestamp;
        break;
      case 'end':
        // start effect after last queued effect has finished
        timestamp = this.effects.pluck('finishOn').max() || timestamp;
        break;
    }
    
    effect.startOn  += timestamp;
    effect.finishOn += timestamp;

    if(!effect.options.queue.limit || (this.effects.length < effect.options.queue.limit))
      this.effects.push(effect);
    
    if(!this.interval) 
      this.interval = setInterval(this.loop.bind(this), 15);
  },
  remove: function(effect) {
    this.effects = this.effects.reject(function(e) { return e==effect });
    if(this.effects.length == 0) {
      clearInterval(this.interval);
      this.interval = null;
    }
  },
  loop: function() {
    var timePos = new Date().getTime();
    for(var i=0, len=this.effects.length;i<len;i++) 
      if(this.effects[i]) this.effects[i].loop(timePos);
  }
});

zEffect.Queues = {
  instances: z$H(),
  get: function(queueName) {
    if(typeof queueName != 'string') return queueName;
    
    if(!this.instances[queueName])
      this.instances[queueName] = new zEffect.ScopedQueue();
      
    return this.instances[queueName];
  }
}
zEffect.Queue = zEffect.Queues.get('global');

zEffect.DefaultOptions = {
  transition: zEffect.Transitions.sinoidal,
  duration:   1.0,   // seconds
  fps:        60.0,  // max. 60fps due to zEffect.Queue implementation
  sync:       false, // true for combining
  from:       0.0,
  to:         1.0,
  delay:      0.0,
  queue:      'parallel'
}

zEffect.Base = function() {};
zEffect.Base.prototype = {
  position: null,
  start: function(options) {
    this.options      = Object.extend(Object.extend({},zEffect.DefaultOptions), options || {});
	this.name         = this.options.name || "Base";
    this.currentFrame = 0;
    this.state        = 'idle';
    this.startOn      = this.options.delay*1000;
    this.finishOn     = this.startOn + (this.options.duration*1000);
    this.event('beforeStart');
    if(!this.options.sync)
      zEffect.Queues.get(typeof this.options.queue == 'string' ? 
        'global' : this.options.queue.scope).add(this);
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
      var pos   = (timePos - this.startOn) / (this.finishOn - this.startOn);
      var frame = Math.round(pos * this.options.fps * this.options.duration);
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
      if(this.options.transition) pos = this.options.transition(pos);
      pos *= (this.options.to-this.options.from);
      pos += this.options.from;
      this.position = pos;
      this.event('beforeUpdate');
      if(this.update) this.update(pos);
      this.event('afterUpdate');
    }
  },
  cancel: function() {
    if(!this.options.sync)
      zEffect.Queues.get(typeof this.options.queue == 'string' ? 
        'global' : this.options.queue.scope).remove(this);
    this.state = 'finished';
  },
  event: function(eventName) {
    if(this.options[eventName + 'Internal']) this.options[eventName + 'Internal'](this);
    if(this.options[eventName]) this.options[eventName](this);
  },
  inspect: function() {
    var data = z$H();
    for(property in this)
      if(typeof this[property] != 'function') data[property] = this[property];
    return '#<zEffect:' + data.inspect() + ',options:' + z$H(this.options).inspect() + '>';
  }
}

zEffect.Parallel = zClass.create();
Object.extend(Object.extend(zEffect.Parallel.prototype, zEffect.Base.prototype), {
  initialize: function(effects) {
    this.effects = effects || [];
    this.start(arguments[1]);
  },
  update: function(position) {
    this.effects.invoke('render', position);
  },
  finish: function(position) {
    this.effects.each( function(effect) {
      effect.render(1.0);
      effect.cancel();
      effect.event('beforeFinish');
      if(effect.finish) effect.finish(position);
      effect.event('afterFinish');
    });
  }
});

zEffect.Event = zClass.create();
Object.extend(Object.extend(zEffect.Event.prototype, zEffect.Base.prototype), {
  initialize: function() {
    var options = Object.extend({
      duration: 0
    }, arguments[0] || {});
    this.start(options);
  },
  update: zPrototype.emptyFunction
});

zEffect.Opacity = zClass.create();
Object.extend(Object.extend(zEffect.Opacity.prototype, zEffect.Base.prototype), {
  initialize: function(element) {
    this.element = z$(element);
    if(!this.element) throw(zEffect._elNotExistErr);
    // make this work on IE on elements without 'layout'
    if(/MSIE/.test(navigator.userAgent) && !window.opera && (!this.element.currentStyle.hasLayout))
      this.element.setStyle({zoom: 1});
    var options = Object.extend({
      from: this.element.getOpacity() || 0.0,
      to:   1.0
    }, arguments[1] || {});
    this.start(options);
  },
  update: function(position) {
    this.element.setOpacity(position);
  }
});

zEffect.Move = zClass.create();
Object.extend(Object.extend(zEffect.Move.prototype, zEffect.Base.prototype), {
  initialize: function(element) {
    this.element = z$(element);
    if(!this.element) throw(zEffect._elNotExistErr);
    var options = Object.extend({
      x:    0,
      y:    0,
      mode: 'relative'
    }, arguments[1] || {});
    this.start(options);
  },
  setup: function() {
    // Bug in Opera: Opera returns the "real" position of a static element or
    // relative element that does not have top/left explicitly set.
    // ==> Always set top and left for position relative elements in your stylesheets 
    // (to 0 if you do not need them) 
    this.element.makePositioned();
    this.originalLeft = parseFloat(this.element.getStyle('left') || '0');
    this.originalTop  = parseFloat(this.element.getStyle('top')  || '0');
    if(this.options.mode == 'absolute') {
      // absolute movement, so we need to calc deltaX and deltaY
      this.options.x = this.options.x - this.originalLeft;
      this.options.y = this.options.y - this.originalTop;
    }
  },
  update: function(position) {
    this.element.setStyle({
      left: Math.round(this.options.x  * position + this.originalLeft) + 'px',
      top:  Math.round(this.options.y  * position + this.originalTop)  + 'px'
    });
  }
});

// for backwards compatibility
zEffect.MoveBy = function(element, toTop, toLeft) {
  return new zEffect.Move(element, 
    Object.extend({ x: toLeft, y: toTop }, arguments[3] || {}));
};

zEffect.Scale = zClass.create();
Object.extend(Object.extend(zEffect.Scale.prototype, zEffect.Base.prototype), {
  initialize: function(element, percent) {
    this.element = z$(element);
    if(!this.element) throw(zEffect._elNotExistErr);
    var options = Object.extend({
      scaleX: true,
      scaleY: true,
      scaleContent: true,
      scaleFromCenter: false,
      scaleMode: 'box',        // 'box' or 'contents' or {} with provided values
      scaleFrom: 100.0,
      scaleTo:   percent
    }, arguments[2] || {});
    this.start(options);
  },
  setup: function() {
    this.restoreAfterFinish = this.options.restoreAfterFinish || false;
    this.elementPositioning = this.element.getStyle('position');
    
    this.originalStyle = {};
    ['top','left','width','height','fontSize'].each( function(k) {
      this.originalStyle[k] = this.element.style[k];
    }.bind(this));
      
    this.originalTop  = this.element.offsetTop;
    this.originalLeft = this.element.offsetLeft;
    
    var fontSize = this.element.getStyle('font-size') || '100%';
    ['em','px','%','pt'].each( function(fontSizeType) {
      if(fontSize.indexOf(fontSizeType)>0) {
        this.fontSize     = parseFloat(fontSize);
        this.fontSizeType = fontSizeType;
      }
    }.bind(this));
    
    this.factor = (this.options.scaleTo - this.options.scaleFrom)/100;
    
    this.dims = null;
    if(this.options.scaleMode=='box')
      this.dims = [this.element.offsetHeight, this.element.offsetWidth];
    if(/^content/.test(this.options.scaleMode))
      this.dims = [this.element.scrollHeight, this.element.scrollWidth];
    if(!this.dims)
      this.dims = [this.options.scaleMode.originalHeight,
                   this.options.scaleMode.originalWidth];
  },
  update: function(position) {
    var currentScale = (this.options.scaleFrom/100.0) + (this.factor * position);
    if(this.options.scaleContent && this.fontSize)
      this.element.setStyle({fontSize: this.fontSize * currentScale + this.fontSizeType });
    this.setDimensions(this.dims[0] * currentScale, this.dims[1] * currentScale);
  },
  finish: function(position) {
    if(this.restoreAfterFinish) this.element.setStyle(this.originalStyle);
  },
  setDimensions: function(height, width) {
    var d = {};
    if(this.options.scaleX) d.width = Math.round(width) + 'px';
    if(this.options.scaleY) d.height = Math.round(height) + 'px';
    if(this.options.scaleFromCenter) {
      var topd  = (height - this.dims[0])/2;
      var leftd = (width  - this.dims[1])/2;
      if(this.elementPositioning == 'absolute') {
        if(this.options.scaleY) d.top = this.originalTop-topd + 'px';
        if(this.options.scaleX) d.left = this.originalLeft-leftd + 'px';
      } else {
        if(this.options.scaleY) d.top = -topd + 'px';
        if(this.options.scaleX) d.left = -leftd + 'px';
      }
    }
    this.element.setStyle(d);
  }
});

zEffect.Highlight = zClass.create();
Object.extend(Object.extend(zEffect.Highlight.prototype, zEffect.Base.prototype), {
  initialize: function(element) {
    this.element = z$(element);
    if(!this.element) throw(zEffect._elNotExistErr);
    var options = Object.extend({ startcolor: '#ffff99' }, arguments[1] || {});
    this.start(options);
  },
  setup: function() {
    // Prevent executing on elements not in the layout flow
    if(this.element.getStyle('display')=='none') { this.cancel(); return; }
    // Disable background image during the effect
    this.oldStyle = {};
    if (!this.options.keepBackgroundImage) {
      this.oldStyle.backgroundImage = this.element.getStyle('background-image');
      this.element.setStyle({backgroundImage: 'none'});
    }
    if(!this.options.endcolor)
      this.options.endcolor = this.element.getStyle('background-color').parseColor('#ffffff');
    if(!this.options.restorecolor)
      this.options.restorecolor = this.element.getStyle('background-color');
    // init color calculations
    this._base  = z$R(0,2).map(function(i){ return parseInt(this.options.startcolor.slice(i*2+1,i*2+3),16) }.bind(this));
    this._delta = z$R(0,2).map(function(i){ return parseInt(this.options.endcolor.slice(i*2+1,i*2+3),16)-this._base[i] }.bind(this));
  },
  update: function(position) {
    this.element.setStyle({backgroundColor: z$R(0,2).inject('#',function(m,v,i){
      return m+(Math.round(this._base[i]+(this._delta[i]*position)).toColorPart()); }.bind(this)) });
  },
  finish: function() {
    this.element.setStyle(Object.extend(this.oldStyle, {
      backgroundColor: this.options.restorecolor
    }));
  }
});

zEffect.ScrollTo = zClass.create();
Object.extend(Object.extend(zEffect.ScrollTo.prototype, zEffect.Base.prototype), {
  initialize: function(element) {
    this.element = z$(element);
    this.start(arguments[1] || {});
  },
  setup: function() {
    zPos.prepare();
    var offsets = zPos.cumulativeOffset(this.element);
    if(this.options.offset) offsets[1] += this.options.offset;
    var max = window.innerHeight ? 
      window.height - window.innerHeight :
      document.body.scrollHeight - 
        (document.documentElement.clientHeight ? 
          document.documentElement.clientHeight : document.body.clientHeight);
    this.scrollStart = zPos.deltaY;
    this.delta = (offsets[1] > max ? max : offsets[1]) - this.scrollStart;
  },
  update: function(position) {
    zPos.prepare();
    window.scrollTo(zPos.deltaX, 
      this.scrollStart + (position*this.delta));
  }
});

/* ------------- combination effects ------------- */

zEffect.Fade = function(element) {
  element = z$(element);
  var oldOpacity = element.getInlineOpacity();
  var options = Object.extend({
  from: element.getOpacity() || 1.0,
  to:   0.0,
  afterFinishInternal: function(effect) { 
    if(effect.options.to!=0) return;
    effect.element.hide().setStyle({opacity: oldOpacity}); 
  }}, arguments[1] || {});
  return new zEffect.Opacity(element,options);
}

zEffect.Appear = function(element) {
  element = z$(element);
  var options = Object.extend({
  from: (element.getStyle('display') == 'none' ? 0.0 : element.getOpacity() || 0.0),
  to:   1.0,
  // force Safari to render floated elements properly
  afterFinishInternal: function(effect) {
    effect.element.forceRerendering();
  },
  beforeSetup: function(effect) {
    effect.element.setOpacity(effect.options.from).show(); 
  }}, arguments[1] || {});
  return new zEffect.Opacity(element,options);
}

zEffect.Puff = function(element) {
  element = z$(element);
  var oldStyle = { 
    opacity: element.getInlineOpacity(), 
    position: element.getStyle('position'),
    top:  element.style.top,
    left: element.style.left,
    width: element.style.width,
    height: element.style.height
  };
  return new zEffect.Parallel(
   [ new zEffect.Scale(element, 200, 
      { sync: true, scaleFromCenter: true, scaleContent: true, restoreAfterFinish: true }), 
     new zEffect.Opacity(element, { sync: true, to: 0.0 } ) ], 
     Object.extend({ duration: 1.0, 
      beforeSetupInternal: function(effect) {
        zPos.absolutize(effect.effects[0].element)
      },
      afterFinishInternal: function(effect) {
         effect.effects[0].element.hide().setStyle(oldStyle); }
     }, arguments[1] || {})
   );
}

zEffect.BlindUp = function(element) {
  element = z$(element);
  element.makeClipping();
  return new zEffect.Scale(element, 0,
    Object.extend({ scaleContent: false, 
      scaleX: false, 
      restoreAfterFinish: true,
      afterFinishInternal: function(effect) {
        effect.element.hide().undoClipping();
      } 
    }, arguments[1] || {})
  );
}

zEffect.BlindDown = function(element) {
  element = z$(element);
  var elementDimensions = element.getDimensions();
  return new zEffect.Scale(element, 100, Object.extend({ 
    scaleContent: false, 
    scaleX: false,
    scaleFrom: 0,
    scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
    restoreAfterFinish: true,
    afterSetup: function(effect) {
      effect.element.makeClipping().setStyle({height: '0px'}).show(); 
    },  
    afterFinishInternal: function(effect) {
      effect.element.undoClipping();
    }
  }, arguments[1] || {}));
}

zEffect.SwitchOff = function(element) {
  element = z$(element);
  var oldOpacity = element.getInlineOpacity();
  return new zEffect.Appear(element, Object.extend({
    duration: 0.4,
    from: 0,
    transition: zEffect.Transitions.flicker,
    afterFinishInternal: function(effect) {
      new zEffect.Scale(effect.element, 1, { 
        duration: 0.3, scaleFromCenter: true,
        scaleX: false, scaleContent: false, restoreAfterFinish: true,
        beforeSetup: function(effect) { 
          effect.element.makePositioned().makeClipping();
        },
        afterFinishInternal: function(effect) {
          effect.element.hide().undoClipping().undoPositioned().setStyle({opacity: oldOpacity});
        }
      })
    }
  }, arguments[1] || {}));
}

zEffect.DropOut = function(element) {
  element = z$(element);
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left'),
    opacity: element.getInlineOpacity() };
  return new zEffect.Parallel(
    [ new zEffect.Move(element, {x: 0, y: 100, sync: true }), 
      new zEffect.Opacity(element, { sync: true, to: 0.0 }) ],
    Object.extend(
      { duration: 0.5,
        beforeSetup: function(effect) {
          effect.effects[0].element.makePositioned(); 
        },
        afterFinishInternal: function(effect) {
          effect.effects[0].element.hide().undoPositioned().setStyle(oldStyle);
        } 
      }, arguments[1] || {}));
}
zEffect.SlideOut = function(element, anchor) {
  anchor = anchor || 't';
  element = z$(element);
  var movement, s = element.style;
  switch (anchor) {
  	case 't':
		movement = {x: 0, y: -$int(s.height), sync: true};
		break;
	case 'b':
		movement = {x: 0, y: $int(s.height), sync: true};
		break;
	case 'l':
		movement = {x: -$int(s.width), y: 0, sync: true};
		break;
	case 'r':
		movement = {x: $int(s.width), y: 0, sync: true};
		break;
  }
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left'),
    opacity: element.getInlineOpacity() };
  return new zEffect.Parallel(
    [ new zEffect.Move(element, movement)],
    Object.extend(
      { duration: 0.5,
        beforeSetup: function(effect) {
          effect.effects[0].element.makePositioned(); 
        },
		beforeFinishInternal: function (effect) {
			effect.effects[0].element.hide();
		},
        afterFinishInternal: function(effect) {
          effect.effects[0].element.undoPositioned().setStyle(oldStyle);
        } 
      }, arguments[2] || {}));
}
zEffect.SlideIn = function(element, anchor) {
  anchor = anchor || 't';
  element = z$(element);
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left'),
    opacity: element.getInlineOpacity() };

   var movement, s = element.style;
  switch (anchor) {
  	case 't':
		var t = $int(s.top), h = $int(s.height);
		s.top = t - h + "px";
		movement = {x: 0, y: h, sync: true};
		break;
	case 'b':
		var t = $int(s.top), h = $int(s.height);
		s.top = t + h + "px";
		movement = {x: 0, y: -h, sync: true};
		break;
	case 'l':
		var l = $int(s.left), w = $int(s.width);
		s.left = l - w + "px";
		movement = {x: w, y: 0, sync: true};
		break;
	case 'r':
		var l = $int(s.left), w = $int(s.width);
		s.left = l + w + "px";
		movement = {x: -w, y: 0, sync: true};
		break; 
  }
  return new zEffect.Parallel(
    [ new zEffect.Move(element, movement)],
    Object.extend(
      { duration: 0.5,
        beforeSetup: function(effect) {
		  effect.effects[0].element.show();
          effect.effects[0].element.makePositioned();
        },
        afterFinishInternal: function(effect) {
          effect.effects[0].element.undoPositioned().setStyle(oldStyle);
        } 
      }, arguments[2] || {}));
}
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Shake = function(element) {
  element = z$(element);
  var oldStyle = {
    top: element.getStyle('top'),
    left: element.getStyle('left') };
    return new zEffect.Move(element, 
      { x:  20, y: 0, duration: 0.05, afterFinishInternal: function(effect) {
    new zEffect.Move(effect.element,
      { x: -40, y: 0, duration: 0.1,  afterFinishInternal: function(effect) {
    new zEffect.Move(effect.element,
      { x:  40, y: 0, duration: 0.1,  afterFinishInternal: function(effect) {
    new zEffect.Move(effect.element,
      { x: -40, y: 0, duration: 0.1,  afterFinishInternal: function(effect) {
    new zEffect.Move(effect.element,
      { x:  40, y: 0, duration: 0.1,  afterFinishInternal: function(effect) {
    new zEffect.Move(effect.element,
      { x: -20, y: 0, duration: 0.05, afterFinishInternal: function(effect) {
        effect.element.undoPositioned().setStyle(oldStyle);
  }}) }}) }}) }}) }}) }});
}
*/
zEffect.SlideDown = function(element, anchor) {
  if (typeof anchor == 'object') { // backward compatible
  	arguments[2] = anchor;
	anchor = 't';
  }
  anchor = anchor || 't';
  element = z$(element).cleanWhitespace();
  // SlideDown need to have the content of the element wrapped in a container element with fixed height!
  var orig = {
	  	t: element.getStyle('top'),
	  	l: element.getStyle('left')
	  },
	  isVert = anchor == 't' || anchor == 'b';
  var dims = element.getDimensions();
  return new zEffect.Scale(element, 100, Object.extend({ 
    scaleContent: false, 
    scaleX: !isVert,
    scaleY: isVert,
    scaleFrom: window.opera ? 0 : 1,
    scaleMode: {originalHeight: dims.height, originalWidth: dims.width},
    restoreAfterFinish: true,
    afterSetup: function(effect) {
      effect.element.makePositioned();
	  switch (anchor) {
	  	case 't':
      		effect.element.makeClipping().setStyle({height: '0px'}).show();
			break;
	  	case 'b':
			orig.ot = dims.top + dims.height;
      		effect.element.makeClipping().setStyle({
				height: '0px', top: orig.ot + 'px'}).show();
			break;
	  	case 'l':
      		effect.element.makeClipping().setStyle({width: '0px'}).show();
			break;
	  	case 'r':
			orig.ol = dims.left + dims.width;
      		effect.element.makeClipping().setStyle({
				width: '0px', left: orig.ol + 'px'}).show();
			break;
	  }
    },
	afterUpdateInternal: function(effect){
		if (anchor == 'b') {
			effect.element.setStyle({top:
	        	(orig.ot - $int(effect.element.style.height)) + 'px' });
		} else if (anchor == 'r') {
	    	effect.element.setStyle({left:
	        	(orig.ol - $int(effect.element.style.width)) + 'px' });
		} 
	},
    afterFinishInternal: function(effect) {
      effect.element.undoClipping().undoPositioned();
      effect.element.undoPositioned().setStyle({
	  	top: orig.t, left: orig.l});
	 }
    }, arguments[2] || {})
  );
}

zEffect.SlideUp = function(element, anchor) {
  if (typeof anchor == 'object') { // backward compatible
  	arguments[2] = anchor;
	anchor = 't';
  }
  anchor = anchor || 't';
  element = z$(element).cleanWhitespace();
    var orig = {
	  	t: element.getStyle('top'),
	  	l: element.getStyle('left')
	  },
	  isVert = anchor == 't' || anchor == 'b';
  return new zEffect.Scale(element, window.opera ? 0 : 1,
   Object.extend({ scaleContent: false, 
    scaleX: !isVert,
    scaleY: isVert,
    scaleMode: 'box',
    scaleFrom: 100,
    restoreAfterFinish: true,
    beforeStartInternal: function(effect) {
      effect.element.makePositioned();
      effect.element.makeClipping().show();
	  orig.ot = effect.element.offsetTop;
	  orig.oh = effect.element.offsetHeight;
	  orig.ol = effect.element.offsetLeft;
	  orig.ow = effect.element.offsetWidth;
    },
	afterUpdateInternal: function(effect){
		if (anchor == 'b') {
			effect.element.setStyle({top:
	        	(orig.ot + orig.oh - $int(effect.element.style.height)) + 'px' });
		} else if (anchor == 'r') {
	    	effect.element.setStyle({left:
	        	(orig.ol + orig.ow - $int(effect.element.style.width)) + 'px' });
		} 
	},
	beforeFinishInternal: function (effect) {
		effect.element.hide();
	},
    afterFinishInternal: function(effect) {
      effect.element.undoClipping().undoPositioned().setStyle({
	  	top: orig.t, left: orig.l});
    }
   }, arguments[2] || {})
  );
}

// Bug in opera makes the TD containing this element expand for a instance after finish 
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Squish = function(element) {
  return new zEffect.Scale(element, window.opera ? 1 : 0, { 
    restoreAfterFinish: true,
    beforeSetup: function(effect) {
      effect.element.makeClipping(); 
    },  
    afterFinishInternal: function(effect) {
      effect.element.hide().undoClipping(); 
    }
  });
}
*/
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Grow = function(element) {
  element = z$(element);
  var options = Object.extend({
    direction: 'center',
    moveTransition: zEffect.Transitions.sinoidal,
    scaleTransition: zEffect.Transitions.sinoidal,
    opacityTransition: zEffect.Transitions.full
  }, arguments[1] || {});
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    height: element.style.height,
    width: element.style.width,
    opacity: element.getInlineOpacity() };

  var dims = element.getDimensions();    
  var initialMoveX, initialMoveY;
  var moveX, moveY;
  
  switch (options.direction) {
    case 'top-left':
      initialMoveX = initialMoveY = moveX = moveY = 0; 
      break;
    case 'top-right':
      initialMoveX = dims.width;
      initialMoveY = moveY = 0;
      moveX = -dims.width;
      break;
    case 'bottom-left':
      initialMoveX = moveX = 0;
      initialMoveY = dims.height;
      moveY = -dims.height;
      break;
    case 'bottom-right':
      initialMoveX = dims.width;
      initialMoveY = dims.height;
      moveX = -dims.width;
      moveY = -dims.height;
      break;
    case 'center':
      initialMoveX = dims.width / 2;
      initialMoveY = dims.height / 2;
      moveX = -dims.width / 2;
      moveY = -dims.height / 2;
      break;
  }
  
  return new zEffect.Move(element, {
    x: initialMoveX,
    y: initialMoveY,
    duration: 0.01, 
    beforeSetup: function(effect) {
      effect.element.hide().makeClipping().makePositioned();
    },
    afterFinishInternal: function(effect) {
      new zEffect.Parallel(
        [ new zEffect.Opacity(effect.element, { sync: true, to: 1.0, from: 0.0, transition: options.opacityTransition }),
          new zEffect.Move(effect.element, { x: moveX, y: moveY, sync: true, transition: options.moveTransition }),
          new zEffect.Scale(effect.element, 100, {
            scaleMode: { originalHeight: dims.height, originalWidth: dims.width }, 
            sync: true, scaleFrom: window.opera ? 1 : 0, transition: options.scaleTransition, restoreAfterFinish: true})
        ], Object.extend({
             beforeSetup: function(effect) {
               effect.effects[0].element.setStyle({height: '0px'}).show(); 
             },
             afterFinishInternal: function(effect) {
               effect.effects[0].element.undoClipping().undoPositioned().setStyle(oldStyle); 
             }
           }, options)
      )
    }
  });
}
*/
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Shrink = function(element) {
  element = z$(element);
  var options = Object.extend({
    direction: 'center',
    moveTransition: zEffect.Transitions.sinoidal,
    scaleTransition: zEffect.Transitions.sinoidal,
    opacityTransition: zEffect.Transitions.none
  }, arguments[1] || {});
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    height: element.style.height,
    width: element.style.width,
    opacity: element.getInlineOpacity() };

  var dims = element.getDimensions();
  var moveX, moveY;
  
  switch (options.direction) {
    case 'top-left':
      moveX = moveY = 0;
      break;
    case 'top-right':
      moveX = dims.width;
      moveY = 0;
      break;
    case 'bottom-left':
      moveX = 0;
      moveY = dims.height;
      break;
    case 'bottom-right':
      moveX = dims.width;
      moveY = dims.height;
      break;
    case 'center':  
      moveX = dims.width / 2;
      moveY = dims.height / 2;
      break;
  }
  
  return new zEffect.Parallel(
    [ new zEffect.Opacity(element, { sync: true, to: 0.0, from: 1.0, transition: options.opacityTransition }),
      new zEffect.Scale(element, window.opera ? 1 : 0, { sync: true, transition: options.scaleTransition, restoreAfterFinish: true}),
      new zEffect.Move(element, { x: moveX, y: moveY, sync: true, transition: options.moveTransition })
    ], Object.extend({            
         beforeStartInternal: function(effect) {
           effect.effects[0].element.makePositioned().makeClipping(); 
         },
         afterFinishInternal: function(effect) {
           effect.effects[0].element.hide().undoClipping().undoPositioned().setStyle(oldStyle); }
       }, options)
  );
}
*/
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Pulsate = function(element) {
  element = z$(element);
  var options    = arguments[1] || {};
  var oldOpacity = element.getInlineOpacity();
  var transition = options.transition || zEffect.Transitions.sinoidal;
  var reverser   = function(pos){ return transition(1-zEffect.Transitions.pulse(pos, options.pulses)) };
  reverser.bind(transition);
  return new zEffect.Opacity(element, 
    Object.extend(Object.extend({  duration: 2.0, from: 0,
      afterFinishInternal: function(effect) { effect.element.setStyle({opacity: oldOpacity}); }
    }, options), {transition: reverser}));
}

zEffect.Fold = function(element) {
  element = z$(element);
  var oldStyle = {
    top: element.style.top,
    left: element.style.left,
    width: element.style.width,
    height: element.style.height };
  element.makeClipping();
  return new zEffect.Scale(element, 5, Object.extend({   
    scaleContent: false,
    scaleX: false,
    afterFinishInternal: function(effect) {
    new zEffect.Scale(element, 1, { 
      scaleContent: false, 
      scaleY: false,
      afterFinishInternal: function(effect) {
        effect.element.hide().undoClipping().setStyle(oldStyle);
      } });
  }}, arguments[1] || {}));
};
*/
/* Tom M. Yeh, Potix: remove unused codes
zEffect.Morph = zClass.create();
Object.extend(Object.extend(zEffect.Morph.prototype, zEffect.Base.prototype), {
  initialize: function(element) {
    this.element = z$(element);
    if(!this.element) throw(zEffect._elNotExistErr);
    var options = Object.extend({
      style: {}
    }, arguments[1] || {});
    if (typeof options.style == 'string') {
      if(options.style.indexOf(':') == -1) {
        var cssText = '', selector = '.' + options.style;
        z$A(document.styleSheets).reverse().each(function(styleSheet) {
          if (styleSheet.cssRules) cssRules = styleSheet.cssRules;
          else if (styleSheet.rules) cssRules = styleSheet.rules;
          z$A(cssRules).reverse().each(function(rule) {
            if (selector == rule.selectorText) {
              cssText = rule.style.cssText;
              throw z$break;
            }
          });
          if (cssText) throw z$break;
        });
        this.style = cssText.parseStyle();
        options.afterFinishInternal = function(effect){
          effect.element.addClassName(effect.options.style);
          effect.transforms.each(function(transform) {
            if(transform.style != 'opacity')
              effect.element.style[transform.style.camelize()] = '';
          });
        }
      } else this.style = options.style.parseStyle();
    } else this.style = z$H(options.style)
    this.start(options);
  },
  setup: function(){
    function parseColor(color){
      if(!color || ['rgba(0, 0, 0, 0)','transparent'].include(color)) color = '#ffffff';
      color = color.parseColor();
      return z$R(0,2).map(function(i){
        return parseInt( color.slice(i*2+1,i*2+3), 16 ) 
      });
    }
    this.transforms = this.style.map(function(pair){
      var property = pair[0].underscore().dasherize(), value = pair[1], unit = null;

      if(value.parseColor('#zzzzzz') != '#zzzzzz') {
        value = value.parseColor();
        unit  = 'color';
      } else if(property == 'opacity') {
        value = parseFloat(value);
        if(/MSIE/.test(navigator.userAgent) && !window.opera && (!this.element.currentStyle.hasLayout))
          this.element.setStyle({zoom: 1});
      } else if(Element.CSS_LENGTH.test(value)) 
        var components = value.match(/^([\+\-]?[0-9\.]+)(.*)$/),
          value = parseFloat(components[1]), unit = (components.length == 3) ? components[2] : null;

      var originalValue = this.element.getStyle(property);
      return z$H({ 
        style: property, 
        originalValue: unit=='color' ? parseColor(originalValue) : parseFloat(originalValue || 0), 
        targetValue: unit=='color' ? parseColor(value) : value,
        unit: unit
      });
    }.bind(this)).reject(function(transform){
      return (
        (transform.originalValue == transform.targetValue) ||
        (
          transform.unit != 'color' &&
          (isNaN(transform.originalValue) || isNaN(transform.targetValue))
        )
      )
    });
  },
  update: function(position) {
    var style = z$H(), value = null;
    this.transforms.each(function(transform){
      value = transform.unit=='color' ?
        z$R(0,2).inject('#',function(m,v,i){
          return m+(Math.round(transform.originalValue[i]+
            (transform.targetValue[i] - transform.originalValue[i])*position)).toColorPart() }) : 
        transform.originalValue + Math.round(
          ((transform.targetValue - transform.originalValue) * position) * 1000)/1000 + transform.unit;
      style[transform.style] = value;
    });
    this.element.setStyle(style);
  }
});

zEffect.Transform = zClass.create();
Object.extend(zEffect.Transform.prototype, {
  initialize: function(tracks){
    this.tracks  = [];
    this.options = arguments[1] || {};
    this.addTracks(tracks);
  },
  addTracks: function(tracks){
    tracks.each(function(track){
      var data = z$H(track).values().first();
      this.tracks.push(z$H({
        ids:     z$H(track).keys().first(),
        effect:  zEffect.Morph,
        options: { style: data }
      }));
    }.bind(this));
    return this;
  },
  play: function(){
    return new zEffect.Parallel(
      this.tracks.map(function(track){
        var elements = [z$(track.ids) || $z$(track.ids)].flatten();
        return elements.map(function(e){ return new track.effect(e, Object.extend({ sync:true }, track.options)) });
      }).flatten(),
      this.options
    );
  }
});
*/
Element.CSS_PROPERTIES = z$w(
  'backgroundColor backgroundPosition borderBottomColor borderBottomStyle ' + 
  'borderBottomWidth borderLeftColor borderLeftStyle borderLeftWidth ' +
  'borderRightColor borderRightStyle borderRightWidth borderSpacing ' +
  'borderTopColor borderTopStyle borderTopWidth bottom clip color ' +
  'fontSize fontWeight height left letterSpacing lineHeight ' +
  'marginBottom marginLeft marginRight marginTop markerOffset maxHeight '+
  'maxWidth minHeight minWidth opacity outlineColor outlineOffset ' +
  'outlineWidth paddingBottom paddingLeft paddingRight paddingTop ' +
  'right textIndent top width wordSpacing zIndex');
  
Element.CSS_LENGTH = /^(([\+\-]?[0-9\.]+)(em|ex|px|in|cm|mm|pt|pc|\%))|0$/;

String.prototype.parseStyle = function(){
  var element = Element.extend(document.createElement('div'));
  element.innerHTML = '<div style="' + this + '"></div>';
  var style = element.down().style, styleRules = z$H();
  
  Element.CSS_PROPERTIES.each(function(property){
    if(style[property]) styleRules[property] = style[property]; 
  });
  if(/MSIE/.test(navigator.userAgent) && !window.opera && this.indexOf('opacity') > -1) {
    styleRules.opacity = this.match(/opacity:\s*((?:0|1)?(?:\.\d*)?)/)[1];
  }
  return styleRules;
};

/* Tom M. Yeh, Potix: remove unused codes
Element.morph = function(element, style) {
  new zEffect.Morph(element, Object.extend({ style: style }, arguments[2] || {}));
  return element;
};
*/
['setOpacity','getOpacity','getInlineOpacity','forceRerendering']
 .each( 
/* Tom M. Yeh, Potix: remove unused codes
['setOpacity','getOpacity','getInlineOpacity','forceRerendering','setContentZoom',
 'collectTextNodes','collectTextNodesIgnoreClass','morph'].each( 
*/
  function(f) { Element.Methods[f] = Element[f]; }
);

/* Tom M. Yeh, Potix: remove unused codes
Element.Methods.visualEffect = function(element, effect, options) {
  s = effect.gsub(/_/, '-').camelize();
  effect_class = s.charAt(0).toUpperCase() + s.substring(1);
  new zEffect[effect_class](element, options);
  return z$(element);
};
*/
Element.addMethods();

//backward compatible (3.0 or earlier)
if (!window.Effect) Effect = zEffect;

} //Tom M. Yeh, Potix: prevent it from load twice
