// script.aculo.us dragdrop.js v1.7.0, Fri Jan 19 19:16:36 CET 2007

// Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005, 2006 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)
// 
// script.aculo.us is freely distributable under the terms of an MIT-style license.
// For details, see the script.aculo.us web site: http://script.aculo.us/

//Tom M. Yeh, Potix: prevent it from load twice
if (!window.z_dragdrop_js) {
	z_dragdrop_js = true;

/* Tom M. Yeh, Potix: remove unused codes
if(typeof zEffect == 'undefined')
  throw("dragdrop.js requires including script.aculo.us' effects.js library");

var zDroppables = {
  drops: [],

  remove: function(element) {
    this.drops = this.drops.reject(function(d) { return d.element==z$(element) });
  },

  add: function(element) {
    element = z$(element);
    var options = Object.extend({
      greedy:     true,
      hoverclass: null,
      tree:       false
    }, arguments[1] || {});

    // cache containers
    if(options.containment) {
      options._containers = [];
      var containment = options.containment;
      if((typeof containment == 'object') && 
        (containment.constructor == Array)) {
        containment.each( function(c) { options._containers.push(z$(c)) });
      } else {
        options._containers.push(z$(containment));
      }
    }
    
    if(options.accept) options.accept = [options.accept].flatten();

    Element.makePositioned(element); // fix IE
    options.element = element;

    this.drops.push(options);
  },
  
  findDeepestChild: function(drops) {
    deepest = drops[0];
      
    for (i = 1; i < drops.length; ++i)
      if (Element.isParent(drops[i].element, deepest.element))
        deepest = drops[i];
    
    return deepest;
  },

  isContained: function(element, drop) {
    var containmentNode;
    if(drop.tree) {
      containmentNode = element.treeNode; 
    } else {
      containmentNode = element.parentNode;
    }
    return drop._containers.detect(function(c) { return containmentNode == c });
  },
  
  isAffected: function(point, element, drop) {
    return (
      (drop.element!=element) &&
      ((!drop._containers) ||
        this.isContained(element, drop)) &&
      ((!drop.accept) ||
        (Element.classNames(element).detect( 
          function(v) { return drop.accept.include(v) } ) )) &&
      zPos.within(drop.element, point[0], point[1]) );
  },

  deactivate: function(drop) {
    if(drop.hoverclass)
      Element.removeClassName(drop.element, drop.hoverclass);
    this.last_active = null;
  },

  activate: function(drop) {
    if(drop.hoverclass)
      Element.addClassName(drop.element, drop.hoverclass);
    this.last_active = drop;
  },

  show: function(point, element) {
    if(!this.drops.length) return;
    var affected = [];
    
    if(this.last_active) this.deactivate(this.last_active);
    this.drops.each( function(drop) {
      if(zDroppables.isAffected(point, element, drop))
        affected.push(drop);
    });
        
    if(affected.length>0) {
      drop = zDroppables.findDeepestChild(affected);
      zPos.within(drop.element, point[0], point[1]);
      if(drop.onHover)
        drop.onHover(element, drop.element, zPos.overlap(drop.overlap, drop.element));
      
      zDroppables.activate(drop);
    }
  },

  fire: function(event, element) {
    if(!this.last_active) return;
    zPos.prepare();

    if (this.isAffected([Event.pointerX(event), Event.pointerY(event)], element, this.last_active))
      if (this.last_active.onDrop) 
        this.last_active.onDrop(element, this.last_active.element, event);
  },

  reset: function() {
    if(this.last_active)
      this.deactivate(this.last_active);
  }
}
*/
var zDraggables = {
  drags: [],
  observers: [],
  
  register: function(draggable) {
    if(this.drags.length == 0) {
      this.eventMouseUp   = this.endDrag.bindAsEventListener(this);
      this.eventMouseMove = this.updateDrag.bindAsEventListener(this);
      this.eventKeypress  = this.keyPress.bindAsEventListener(this);
      
      Event.observe(document, "mouseup", this.eventMouseUp);
      Event.observe(document, "mousemove", this.eventMouseMove);
      Event.observe(document, "keypress", this.eventKeypress);
    }
    this.drags.push(draggable);
  },
  
  unregister: function(draggable) {
    this.drags = this.drags.reject(function(d) { return d==draggable });
    if(this.drags.length == 0) {
      Event.stopObserving(document, "mouseup", this.eventMouseUp);
      Event.stopObserving(document, "mousemove", this.eventMouseMove);
      Event.stopObserving(document, "keypress", this.eventKeypress);
    }
  },
  
  activate: function(draggable) {
    if(zk.opera || draggable.options.delay) { 
      this._timeout = setTimeout(function() { 
        zDraggables._timeout = null; 
        window.focus(); 
        zDraggables.activeDraggable = draggable; 
      }.bind(this), draggable.options.delay); 
    } else {
      window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
      this.activeDraggable = draggable;
    }
  },
  
  deactivate: function() {
    this.activeDraggable = null;
  },
  
  updateDrag: function(event) {
    if(!this.activeDraggable) return;
    var pointer = [Event.pointerX(event), Event.pointerY(event)];
    // Mozilla-based browsers fire successive mousemove events with
    // the same coordinates, prevent needless redrawing (moz bug?)
    if(this._lastPointer && (this._lastPointer.inspect() == pointer.inspect())) return;
    this._lastPointer = pointer;
    
    this.activeDraggable.updateDrag(event, pointer);
  },
  
  endDrag: function(event) {
    if(this._timeout) { 
      clearTimeout(this._timeout); 
      this._timeout = null; 
    }
    if(!this.activeDraggable) return;
    this._lastPointer = null;
    this.activeDraggable.endDrag(event);
    this.activeDraggable = null;
  },
  
  keyPress: function(event) {
    if(this.activeDraggable)
      this.activeDraggable.keyPress(event);
  },
  
  addObserver: function(observer) {
    this.observers.push(observer);
    this._cacheObserverCallbacks();
  },
  
  removeObserver: function(element) {  // element instead of observer fixes mem leaks
    this.observers = this.observers.reject( function(o) { return o.element==element });
    this._cacheObserverCallbacks();
  },
  
  notify: function(eventName, draggable, event) {  // 'onStart', 'onEnd', 'onDrag'
    if(this[eventName+'Count'] > 0)
      this.observers.each( function(o) {
        if(o[eventName]) o[eventName](eventName, draggable, event);
      });
    if(draggable.options[eventName]) draggable.options[eventName](draggable, event);
  },
  
  _cacheObserverCallbacks: function() {
    ['onStart','onEnd','onDrag'].each( function(eventName) {
      zDraggables[eventName+'Count'] = zDraggables.observers.select(
        function(o) { return o[eventName]; }
      ).length;
    });
  }
}

/*--------------------------------------------------------------------------*/

var zDraggable = zClass.create();
zDraggable._dragging    = {};

zDraggable.prototype = {
  initialize: function(element) {
 var zdd = zk.ie && arguments[1] && arguments[1].z_dragdrop; //Tom M. Yeh, Potix: Bug 1538506
    var defaults = {
      handle: false,
      reverteffect: function(element, top_offset, left_offset) {
if (top_offset || left_offset) {
var orgpos = element.style.position; //Tom M. Yeh, Potix: Bug 1538506
        var dur = Math.sqrt(Math.abs(top_offset^2)+Math.abs(left_offset^2))*0.02;
        new zEffect.Move(element, { x: -left_offset, y: -top_offset, duration: dur,
          queue: {scope:'_draggable', position:'end'},
          afterFinish: function () {element.style.position = orgpos;}
        });
}
      },
      endeffect: function(element) {
        var toOpacity = typeof element._opacity == 'number' ? element._opacity : 1.0;
        new zEffect.Opacity(element, {duration:0.2, from:0.7, to:toOpacity, 
          queue: {scope:'_draggable', position:'end'},
          afterFinish: function(){ 
            zDraggable._dragging[element] = false 
          }
        }); 
      },
      zindex: 1000,
      revert: false,
      scroll: false,
      scrollSensitivity: 20,
      scrollSpeed: 15,
      snap: false,  // false, or xy or [x,y] or function(x,y){ return [x,y] }
      delay: 0,
	  stackup: false
    };
    
    if(!arguments[1] || typeof arguments[1].endeffect == 'undefined')
      Object.extend(defaults, {
        starteffect: function(element) {
          element._opacity = Element.getOpacity(element);
          zDraggable._dragging[element] = true;
          new zEffect.Opacity(element, {duration:0.2, from:element._opacity, to:0.7}); 
        }
      });
    
    var options = Object.extend(defaults, arguments[1] || {});

    this.element = z$(element);
    
    if(options.handle && (typeof options.handle == 'string'))
      this.handle = this.element.down('.'+options.handle, 0);
    
    if(!this.handle) this.handle = z$(options.handle);
    if(!this.handle) this.handle = this.element;
    
    if(options.scroll && !options.scroll.scrollTo && !options.scroll.outerHTML) {
      options.scroll = z$(options.scroll);
      this._isScrollChild = Element.childOf(this.element, options.scroll);
    }

if (zk.opera || !options.z_dragdrop) //Tom M. Yeh, Potix: Bug 1534426, 1535787 (ie: no makePositioned), 1647114 (opera: makePositioned required)
    Element.makePositioned(this.element); // fix IE    

    this.delta    = this.currentDelta();
    this.options  = options;
    this.dragging = false;   

    this.eventMouseDown = this.initDrag.bindAsEventListener(this);
    Event.observe(this.handle, "mousedown", this.eventMouseDown);
    
    zDraggables.register(this);
  },
  
  destroy: function() {
    Event.stopObserving(this.handle, "mousedown", this.eventMouseDown);
    zDraggables.unregister(this);
  },
  
  currentDelta: function() {
    return([
      $int(Element.getStyle(this.element,'left')),
      $int(Element.getStyle(this.element,'top'))]);
  },
  
  initDrag: function(event) {
    if(typeof zDraggable._dragging[this.element] != 'undefined' &&
      zDraggable._dragging[this.element]) return;
    if(Event.isLeftClick(event)) {    
      // abort on form elements, fixes a Firefox issue
      var src = Event.element(event);
      if((tag_name = src.tagName.toUpperCase()) && (
        tag_name=='INPUT' ||
        tag_name=='SELECT' ||
        tag_name=='OPTION' ||
        tag_name=='BUTTON' ||
        tag_name=='TEXTAREA')) return;
        
//Tom M. Yeh, Potix: skip popup/dropdown (of combobox and others)
for (var n = src; n && n != this.element; n = n.parentNode)
	if (Element.getStyle(n, 'position') == 'absolute')
		return;

      var pointer = [Event.pointerX(event), Event.pointerY(event)];
//Tom M. Yeh, Potix: give the element a chance to ignore dragging
if (this.options.ignoredrag && this.options.ignoredrag(this.element, pointer, event))
	return;
//Tom M. Yeh, Potix: disable selection
//zk.disableSelection(document.body); // Bug #1820433
      var pos     = zPos.cumulativeOffset(this.element);
      this.offset = [0,1].map( function(i) { return (pointer[i] - pos[i]) });
      
      zDraggables.activate(this);
//Jumper Chen, Potix: Bug #1845026
//We need to ensure that the onBlur event is fired before the onSelect event for consistent among four browsers. 
	  if (zkau.currentFocus && Event.element(event) != zkau.currentFocus 
	  	&& typeof zkau.currentFocus.blur == "function") zkau.currentFocus.blur();
      Event.stop(event);
	  zkau.closeFloatsOnFocus(src); // Bug 2562880
//Tom M. Yeh, Potix: mousedown is eaten above
zkau.autoZIndex(src, false, true);
    }
  },
  
  startDrag: function(event) {
//Tom M. Yeh, Potix: disable selection
zk.disableSelection(document.body); // Bug #1820433
  	if (this.options.stackup) { // Bug #1911280
		this.domstackup = document.createElement("DIV");
		document.body.appendChild(this.domstackup);
		zk.setOuterHTML(this.domstackup, '<div class="z-dd-stackup" id="zk_dd_stackup"></div>');
		this.domstackup = $e("zk_dd_stackup");
		if (zk.gecko) this.domstackup.style.MozUserSelect = "none";
		this.domstackup.style.width = zk.pageWidth() + "px";
		this.domstackup.style.height = zk.pageHeight() + "px";
	}
    this.dragging = true;
    
    if(this.options.ghosting) {
//Tom M. Yeh, Potix: ghosting is controllable
var ghosting = true;
if (typeof this.options.ghosting == 'function') ghosting = this.options.ghosting(this, true, event);
if (ghosting) {
      this._clone = this.element.cloneNode(true);
this.z_orgpos = this.element.style.position; //Tom M. Yeh, Potix: Bug 1514789
if (this.z_orgpos != 'absolute')
      zPos.absolutize(this.element);
      this.element.parentNode.insertBefore(this._clone, this.element);
}
    }
    
    if(this.options.zindex) { //Tom M. Yeh, Poitx: after ghosting
      this.originalZ = $int(Element.getStyle(this.element,'z-index'));
      this.element.style.zIndex = this.options.zindex;
    }
    
    if(this.options.scroll) {
      if (this.options.scroll == window) {
        var where = this._getWindowScroll(this.options.scroll);
        this.originalScrollLeft = where.left;
        this.originalScrollTop = where.top;
      } else {
        this.originalScrollLeft = this.options.scroll.scrollLeft;
        this.originalScrollTop = this.options.scroll.scrollTop;
      }
    }
    
    zDraggables.notify('onStart', this, event);
    if(this.options.starteffect) this.options.starteffect(this.element, this.handle);
  },
  
  updateDrag: function(event, pointer) {
    if(!this.dragging) this.startDrag(event);
    zPos.prepare();
/* Tom M. Yeh, Potix: remove unused codes
    zDroppables.show(pointer, this.element);
*/
    zDraggables.notify('onDrag', this, event);
    this.draw(pointer, event);
    if(this.options.change) this.options.change(this, pointer, event); //Tom M Yeh, Potix: add pointer
    
    if(this.options.scroll) {
      this.stopScrolling();
      
      var p;
      if (this.options.scroll == window) {
        with(this._getWindowScroll(this.options.scroll)) { p = [ left, top, left+width, top+height ]; }
      } else {
        p = zPos.page(this.options.scroll);
        p[0] += this.options.scroll.scrollLeft + zPos.deltaX;
        p[1] += this.options.scroll.scrollTop + zPos.deltaY;
        p.push(p[0]+this.options.scroll.offsetWidth);
        p.push(p[1]+this.options.scroll.offsetHeight);
      }
      var speed = [0,0];
      if(pointer[0] < (p[0]+this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[0]+this.options.scrollSensitivity);
      if(pointer[1] < (p[1]+this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[1]+this.options.scrollSensitivity);
      if(pointer[0] > (p[2]-this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[2]-this.options.scrollSensitivity);
      if(pointer[1] > (p[3]-this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[3]-this.options.scrollSensitivity);
      this.startScrolling(speed);
    }
    
    // fix AppleWebKit rendering
    if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);
	
    Event.stop(event);
  },
  
  finishDrag: function(event, success) {
    this.dragging = false;
	if (this.domstackup) zk.remove(this.domstackup);
	delete this.domstackup;
//Tom M. Yeh, Potix: enable selection back and clear selection if any
zk.enableSelection(document.body);
setTimeout("zk.clearSelection()", 0);
    if(this.options.ghosting) {
//Tom M. Yeh: Potix: ghosting is controllable
var ghosting = true;
if (typeof this.options.ghosting == 'function') ghosting = this.options.ghosting(this, false);
if (ghosting) {
if (this.z_orgpos != "absolute") { //Tom M. Yeh, Potix: Bug 1514789
      zPos.relativize(this.element);
this.element.style.position = this.z_orgpos;
}
      Element.remove(this._clone);
      this._clone = null;
}
    }

/* Tom M. Yeh, Potix: remove unused codes
    if(success) zDroppables.fire(event, this.element);
*/
    zDraggables.notify('onEnd', this, event);

	var pointer = [Event.pointerX(event), Event.pointerY(event)]; //Tom M. Yeh, Potix: add pointer
    var revert = this.options.revert;
    if(revert && typeof revert == 'function') revert = revert(this.element, pointer, event); //Tom M. Yeh, Potix: add pointer
    
    var d = this.currentDelta();
    if(revert && this.options.reverteffect) {
      this.options.reverteffect(this.element, 
        d[1]-this.delta[1], d[0]-this.delta[0]);
    } else {
      this.delta = d;
    }

    if(this.options.zindex)
      this.element.style.zIndex = this.originalZ;

    if(this.options.endeffect) 
      this.options.endeffect(this.element, event); //Tom M. Yeh, Potix: add event
      	
    zDraggables.deactivate(this);
/* Tom M. Yeh, Potix: remove unused codes
    zDroppables.reset();
*/
  },
  
  keyPress: function(event) {
    if(Event.keyCode(event)!=27/*Tom M. Yeh, Potix:Event.KEY_ESC*/) return;
    this.finishDrag(event, false);
    Event.stop(event);
  },
  
  endDrag: function(event) {
    if(!this.dragging) return;
    this.stopScrolling();
    this.finishDrag(event, true);
    Event.stop(event);
  },
  
  draw: function(point, event) {
    var pos = zPos.cumulativeOffset(this.element);
    if(this.options.ghosting) {
      var r   = zPos.realOffset(this.element);
      pos[0] += r[0] - zPos.deltaX; pos[1] += r[1] - zPos.deltaY;
    }
    
    var d = this.currentDelta();
    pos[0] -= d[0]; pos[1] -= d[1];
    
    if(this.options.scroll && (this.options.scroll != window && this._isScrollChild)) {
      pos[0] -= this.options.scroll.scrollLeft-this.originalScrollLeft;
      pos[1] -= this.options.scroll.scrollTop-this.originalScrollTop;
    }
    
    var p = [0,1].map(function(i){ 
      return (point[i]-pos[i]-this.offset[i]) 
    }.bind(this));
    
    if(this.options.snap) {
      if(typeof this.options.snap == 'function') {
        p = this.options.snap(p[0],p[1],this);
      } else {
      if(this.options.snap instanceof Array) {
        p = p.map( function(v, i) {
          return Math.round(v/this.options.snap[i])*this.options.snap[i] }.bind(this))
      } else {
        p = p.map( function(v) {
          return Math.round(v/this.options.snap)*this.options.snap }.bind(this))
      }
    }}
    
//Tom M. Yeh, Potix: resolve scrolling offset when DIV is used
if (this.z_scrl) {
	p[0] -= this.z_scrl[0]; p[1] -= this.z_scrl[1];
}

    var style = this.element.style;
//Tom M. Yeh, Potix: support function constraint
if (typeof this.options.draw == 'function') {
	this.options.draw(this, p, event);
} else if (typeof this.options.constraint == 'function') {
	var np = this.options.constraint(this, p, event); //return null or [newx, newy]
	if (np) p = np;
	style.left = p[0] + "px";
	style.top  = p[1] + "px";
} else {
    if((!this.options.constraint) || (this.options.constraint=='horizontal'))
      style.left = p[0] + "px";
    if((!this.options.constraint) || (this.options.constraint=='vertical'))
      style.top  = p[1] + "px";
} //Tom M. Yeh, Potix
    
    if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering
  },
  
  stopScrolling: function() {
    if(this.scrollInterval) {
      clearInterval(this.scrollInterval);
      this.scrollInterval = null;
      zDraggables._lastScrollPointer = null;
    }
  },
  
  startScrolling: function(speed) {
    if(!(speed[0] || speed[1])) return;
    this.scrollSpeed = [speed[0]*this.options.scrollSpeed,speed[1]*this.options.scrollSpeed];
    this.lastScrolled = new Date();
    this.scrollInterval = setInterval(this.scroll.bind(this), 10);
  },
  
  scroll: function() {
    var current = new Date();
    var delta = current - this.lastScrolled;
    this.lastScrolled = current;
    if(this.options.scroll == window) {
      with (this._getWindowScroll(this.options.scroll)) {
        if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
          var d = delta / 1000;
          this.options.scroll.scrollTo( left + d*this.scrollSpeed[0], top + d*this.scrollSpeed[1] );
        }
      }
    } else {
      this.options.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
      this.options.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
    }
    
    zPos.prepare();
/* Tom M. Yeh, Potix: remove unused codes
    zDroppables.show(zDraggables._lastPointer, this.element);
*/
    zDraggables.notify('onDrag', this);
    if (this._isScrollChild) {
      zDraggables._lastScrollPointer = zDraggables._lastScrollPointer || z$A(zDraggables._lastPointer);
      zDraggables._lastScrollPointer[0] += this.scrollSpeed[0] * delta / 1000;
      zDraggables._lastScrollPointer[1] += this.scrollSpeed[1] * delta / 1000;
      if (zDraggables._lastScrollPointer[0] < 0)
        zDraggables._lastScrollPointer[0] = 0;
      if (zDraggables._lastScrollPointer[1] < 0)
        zDraggables._lastScrollPointer[1] = 0;
      this.draw(zDraggables._lastScrollPointer);
    }
    
    if(this.options.change) this.options.change(this);
  },
  
  _getWindowScroll: function(w) {
    var T, L, W, H;
    with (w.document) {
      if (w.document.documentElement && documentElement.scrollTop) {
        T = documentElement.scrollTop;
        L = documentElement.scrollLeft;
      } else if (w.document.body) {
        T = body.scrollTop;
        L = body.scrollLeft;
      }
      if (w.innerWidth) {
        W = w.innerWidth;
        H = w.innerHeight;
      } else if (w.document.documentElement && documentElement.clientWidth) {
        W = documentElement.clientWidth;
        H = documentElement.clientHeight;
      } else {
        W = body.offsetWidth;
        H = body.offsetHeight
      }
    }
    return { top: T, left: L, width: W, height: H };
  }
}

/*--------------------------------------------------------------------------*/

/* Tom M. Yeh, Potix: remove zSortable
var zSortableObserver = zClass.create();
zSortableObserver.prototype = {
...
}
var zSortable = {
...
}
*/

/* Tom M. Yeh, Potix: remove unused codes
// Returns true if child is contained within element
Element.isParent = function(child, element) {
  if (!child.parentNode || child == element) return false;
  if (child.parentNode == element) return true;
  return Element.isParent(child.parentNode, element);
}

Element.findChildren = function(element, only, recursive, tagName) {    
  if(!element.hasChildNodes()) return null;
  tagName = tagName.toUpperCase();
  if(only) only = [only].flatten();
  var elements = [];
  z$A(element.childNodes).each( function(e) {
    if(e.tagName && e.tagName.toUpperCase()==tagName &&
      (!only || (Element.classNames(e).detect(function(v) { return only.include(v) }))))
        elements.push(e);
    if(recursive) {
      var grandchildren = Element.findChildren(e, only, recursive, tagName);
      if(grandchildren) elements.push(grandchildren);
    }
  });

  return (elements.length>0 ? elements.flatten() : []);
}

Element.offsetSize = function (element, type) {
//Tom M. Yeh, Potix: safari bug
if (type == 'vertical' || type == 'height')
  return zk.offsetHeight(element);
else
  return zk.offsetWidth(element);
  //return element['offset' + ((type=='vertical' || type=='height') ? 'Height' : 'Width')];
}
*/

//backward compatible (3.0 or earlier)
if (!window.Draggables) Draggables = zDraggables;
if (!window.Draggable) Draggable = zDraggable;

} //Tom M. Yeh, Potix: prevent it from load twice
