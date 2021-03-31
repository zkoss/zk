/* inputdevicecapabilities-polyfill.js - https://github.com/WICG/InputDeviceCapabilities
 *  
 * Uses a (not perfectly accurate) heuristic to  implement 
 * UIEvent.sourceCapabilities and InputDeviceCapabilities.firesTouchEvents.
 * Assumptions:
 *   - If sourceCapabilities is consulted on an event, it will be first read within
 *     one second of the original event being dispatched.  We could, instead,
 *     determine the sourceCapabilities as soon as any UIEvent is dispatched (eg.
 *     by hooking addEventListener) but that woudln't work for legacy onevent
 *     style handlers.
 *   - Touch and non-touch input devices aren't both being used within one
 *     second of eachother.  Eg. if you tap the screen then quickly move the
 *     mouse, we may incorrectly attribute the mouse movement to the touch
 *     device. 
 *     
 *  Browser support:
 *   - Verified working on:
 *     - Chrome 43 (Windows, Linux and Android)
 *     - IE 11 (Windows)
 *     - Firefox 38 (Linux)
 *     - Safari 8 (Mac and iOS)
 *   - Event constructors aren't supported by IE at all.
 *   - IE on Windows phone isn't supported properly (https://github.com/WICG/InputDeviceCapabilities/issues/13)
 *   - Won't work in IE prior to version 9 (due to lack of Object.defineProperty)
 */

(function(global) {
  'use strict';
  
  if ('InputDeviceCapabilities' in global|| 'sourceCapabilities' in UIEvent.prototype)
    return;
  
  function InputDeviceCapabilities(inputDeviceCapabilitiesInit) {
      Object.defineProperty(this, '__firesTouchEvents', {
        value: (inputDeviceCapabilitiesInit && 'firesTouchEvents' in inputDeviceCapabilitiesInit) ? 
          inputDeviceCapabilitiesInit.firesTouchEvents : false,
        writable: false,
        enumerable: false
      });
  };
  // Put the attributes prototype as getter functions to match the IDL. 
  InputDeviceCapabilities.prototype = {
    get firesTouchEvents() {
      return this.__firesTouchEvents;
    }
  }; 
  global.InputDeviceCapabilities = InputDeviceCapabilities;

  var touchDevice = new InputDeviceCapabilities({firesTouchEvents:true});
  var nonTouchDevice = new InputDeviceCapabilities({firesTouchEvents:false});
    
  // Keep track of the last time we saw a touch event.  Note that if you don't
  // already have touch handlers on your document, this can have unfortunate
  // consequences for scrolling performance.  See https://plus.google.com/+RickByers/posts/cmzrtyBYPQc.
  var lastTouchTime;
  function touchHandler(event) {
    lastTouchTime = Date.now();
  };
  document.addEventListener('touchstart', touchHandler, true);
  document.addEventListener('touchmove', touchHandler, true);
  document.addEventListener('touchend', touchHandler, true);
  document.addEventListener('touchcancel', touchHandler, true);

  var specifiedSourceCapabilitiesName = '__inputDeviceCapabilitiesPolyfill_specifiedSourceCapabilities';

  // A few UIEvents aren't really input events and so should always have a null
  // source capabilities.  Arguably we should have a list of opt-in event types instead,
  // but that probably depends on ultimately how we want to specify this behavior.
  var eventTypesWithNoSourceCapabilities = ['resize', 'error', 'load', 'unload', 'abort'];
  
  // We assume that any UI event that occurs within this many ms from a touch
  // event is caused by a touch device.  This needs to be a little longer than
  // the maximum tap delay on various browsers (350ms in Safari) while remaining
  // as short as possible to reduce the risk of confusing other input that happens
  // to come shortly after touch input.  
  var touchTimeConstant = 1000;
  
  Object.defineProperty(UIEvent.prototype, 'sourceCapabilities', {
    get: function() {
      // Handle script-generated events and events which have already had their
      // sourceCapabilities read.
      if (specifiedSourceCapabilitiesName in this)
        return this[specifiedSourceCapabilitiesName];

      // Handle non-input events.
      if (eventTypesWithNoSourceCapabilities.indexOf(this.type) >= 0)
        return null;
      
      // touch events may not be supported by this browser at all (eg. IE desktop).
      if (!('TouchEvent' in global))
        return nonTouchDevice;
      
      // Touch events are always generated from devices that fire touch events.
      if (this instanceof TouchEvent)
        return touchDevice;
      
      // Pointer events are special - they may come before a touch event.
      if ('PointerEvent' in global && this instanceof PointerEvent) {
        if (this.pointerType == "touch")
          return touchDevice;
        return nonTouchDevice;
      }

      // Otherwise use recent touch events to decide if this event is likely due
      // to a touch device or not.
      var sourceCapabilities = Date.now() < lastTouchTime + touchTimeConstant ? touchDevice : nonTouchDevice;
      
      // Cache the value to ensure it can't change over the life of the event.
      Object.defineProperty(this, specifiedSourceCapabilitiesName, {
        value: sourceCapabilities,
        writable: false
      });
      
      return sourceCapabilities;
    },
    configurable: true,
    enumerable: true
  });
  
  // Add support for supplying a sourceCapabilities from JS in all UIEvent constructors.
  function augmentEventConstructor(constructorName) {
    if (!(constructorName in global))
      return;

    // IE doesn't support event constructors at all.
    // In Safari typeof constructor is 'object' while it's 'function' in other browsers.
    if (!('length' in global[constructorName]) || global[constructorName].length < 1)
      return;

    var origCtor = global[constructorName];
    global[constructorName] = function(type, initDict) {
      var sourceCapabilities = (initDict && initDict.sourceCapabilities) ? initDict.sourceCapabilities : null;
      // Need to explicitly remove sourceCapabilities from the dictionary as it would cause
      // a type error in blink when InputDeviceCapabilities support is disabled.
      if (initDict)
        delete initDict.sourceCapabilities;
      var evt = new origCtor(type, initDict);
      // Stash the sourceCapabilities value away for use by the UIEvent.sourceCapabilities
      // getter.  We could instead shadow the property on this instance, but 
      // that would be subtly different than the specified API.
      Object.defineProperty(evt, specifiedSourceCapabilitiesName, {
        value: sourceCapabilities,
        writable: false
      });
      return evt;
    }
    global[constructorName].prototype = origCtor.prototype;
  };
  
  // Note that SVGZoomEvent desn't yet have constructors defined.
  var uiEventConstructors = ['UIEvent', 'MouseEvent', 'TouchEvent', 'InputEvent', 'CompositionEvent', 'FocusEvent', 'KeyboardEvent', 'WheelEvent', 'PointerEvent'];
  for (var i = 0; i < uiEventConstructors.length; i++)
    augmentEventConstructor(uiEventConstructors[i]);

  // Ensure events created with document.createEvent always get a null sourceCapabilities
  var origCreateEvent = Document.prototype.createEvent;
  Document.prototype.createEvent = function(type) {
    var evt = origCreateEvent.call(this, type);
    if (evt instanceof UIEvent) {
      Object.defineProperty(evt, specifiedSourceCapabilitiesName, {
        value: null,
        writable: false
      });
      return evt;
    }
  };
})(this);
