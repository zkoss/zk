/* layout.js

{{IS_NOTE
	Purpose:
		
	Description:
		Ext 1.0.1 version.
	History:
		Aug 9, 2007 10:56:42 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
// Customization
zk.load("yuiextz.yau!dsp");

zk.addModuleInit(function () {
Ext.LayoutRegion.prototype.initPanelAsTab = function(panel) {
	var ti = this.tabs.addTab(panel.getEl().id, panel.getTitle(), null,
	this.config.closeOnTab && panel.isClosable());
	if (panel.tabTip !== undefined) {
		ti.setTooltip(panel.tabTip);
	}
	ti.on("activate", function() {
		this.setActivePanel(panel);
		var cmp = zkExt.BorderLayout.getRealPanel(this.activePanel);		
		if (!zkExt.BorderLayout.getLayout(cmp).updating)
		  	zkau.send({uuid: cmp.parentNode.id, cmd: "onSelect", data: [cmp.id]},
				zkau.asapTimeout(cmp.parentNode, "onSelect"));
	}, this);
	if (this.config.closeOnTab) {
		ti.on("beforeclose", function(t, e) {
			e.cancel = true;
		   	this.closeClicked(); // this.remove(panel); by jumperchen
		}, this);
	}
	return ti;
};
Ext.LayoutRegion.prototype.closeClicked = function() {
	if (this.activePanel) {
		// this.remove(this.activePanel); by jumperchen
		var panel = zkExt.BorderLayout.getRealPanel(this.activePanel);
		zkau.send({uuid: panel.id, cmd: "onClose", data: null}, 5);
	}
};
Ext.ContentPanel.prototype.setSize = function(width, height) { // bug #1792952
    if (this.fitToFrame) {
        if (this.fitContainer && this.resizeEl != this.el) {
            this.el.setSize(width, height);
        }
        var size = this.adjustForComponents(width, height);
		this.resizeEl = Ext.get(this.resizeEl.id);
		if (this.autoScroll) {
       		this.resizeEl.setStyle("overflow", "auto");
			this.resizeEl.setStyle("position", "relative");// Bug #1824411
    	}
        this.resizeEl.setSize(this.autoWidth ? "auto" : size.width, this.autoHeight ? "auto" : size.height);
        this.fireEvent('resize', this, size.width, size.height);
    }
};
zkExtBorderLayout = {};
zkExtContentPanel = {};
zkExtNestedPanel = {};
zkExtBorderLayoutRegion = {};
zkExt.BorderLayout = Class.create();
zkExt.BorderLayout.prototype = {
	initialize: function (cmp) {			
		zkExt.LoadMask.init();
		this.id = cmp.id;
		zkau.setMeta(cmp, this);
		this.init();
	},
	init: function () {
		this.regions = {}; 
		this._regions = {};
		this.panels = {"north": [], "south": [], "east": [], "west": [], "center":[]};
		this._panels = {"north": [], "south": [], "east": [], "west": [], "center":[]};
		this.el = null;
		this.validRegions = ["north","south","east","west","center"];
		this.updatedpool = [];
	},
	addPanel:  function(target, panel) {
		this.panels[target].push(panel);
		this._panels[target].push(panel);
	},
	removePanel:  function(target, panel) {
		this.panels[target].remove(panel);
	},
	addRegion: function (target, config) {
		this.regions[target] = config;
		this._regions[target] = config;
	},
	removeRegion: function (target) {
		this.regions[target] = null;
		this.panels[target] = [];
	},
	getRegion: function (target) {
		return this.regions[target];
	},
	getRegions: function () {
		return this.regions;
	},
	getPanels: function (target) {
		return this.panels[target];
	},
	render: function () {
		this.updating = true;
		for(var i = 0; i < this.validRegions.length;i++) {
			var r = this.validRegions[i];
			var rc = this.getRegion(r);
			if (!rc)continue;	
			if (!this.el.isUpdating()) this.el.beginUpdate();
			for(var j = this.getPanels(r).length -1; j >= 0; j--) {
				this.el.add(r, this.getPanels(r)[j]);
				try{
					var l = this.getPanels(r)[j].getLayout();
					if (l)l.endUpdate();
				}catch(e) {}	// ignore the contentpanel.	
				var region = this.el.getRegion(r);				
				if (region.autoScroll) region.bodyEl.setStyle("position", "relative"); //Bug #1819784
				else region.bodyEl.setStyle("position", "");
				region.id = rc.id;
				region.on('expanded', this._expanded, r);	
				region.on('slideshow', this._expanded, r);	
				region.on('collapsed', this._collapsed, r);	
				region.on('slidehide', this._collapsed, r);	
			}		
		}
		var cmp = $e(this.id);
		if ($type(cmp.parentNode) != "ExtNestedPanel") {	
			if (this.el.isUpdating()) this.el.endUpdate();
		} else {
			cmp.parentNode.innerlayout = this.el;
		}
		this._reset();
		this.updating = false;
		
	},
	clean: function () {
		this.el = this.regions = this._regions = this.panels = this._panels = null;
	},
	/**
	 * Re renders the layout when its child is changed. (layout component only)
	 */
	rerender: function () {
		this.updating = true;
		for(var i = 0; i < this.validRegions.length;i++) {
			var r = this.validRegions[i];
			var rc = this._regions[r];
			if (!rc && this._panels[r].length == 0)continue;	
			if (!this.el.isUpdating()) this.el.beginUpdate();
			for(var j = this._panels[r].length -1; j >= 0; j--) {
				if (!this.el.getRegion(r)) {
					this.el.addRegion(r, rc);
				}				
				this.el.add(r, this._panels[r][j]);
				try{
					var l = this._panels[r][j].getLayout();
					if (l)l.endUpdate();
				}catch(e) {}		
				var region = this.el.getRegion(r);
				if (region.autoScroll) region.bodyEl.setStyle("position", "relative"); //Bug #1819784
				else region.bodyEl.setStyle("position", "");
				region.id = rc.id;
				region.on('expanded', this._expanded, r);	
				region.on('slideshow', this._expanded, r);	
				region.on('collapsed', this._collapsed, r);	
				region.on('slidehide', this._collapsed, r);		
			}		
		}
		var cmp = $e(this.id);
		if ($type(cmp.parentNode) != "ExtNestedPanel") {
			if (this.el.isUpdating()) this.el.endUpdate();
		} else {
			cmp.parentNode.innerlayout = this.el;
			var rlayout = zkExt.BorderLayout.getLayout(cmp.parentNode);
			rlayout._put(this.el); // put in the updated pool of root layout.
		}
		while(this._hasNext()) { // clean the updated pool
			var l = this._next();
			if (l.isUpdating()) l.endUpdate();
		}
		this._reset();
		this.updating = false;
	}, // private method
	_reset: function () { 
		this._regions = {};
		this._panels = {"north": [], "south": [], "east": [], "west": [], "center":[]};
	},
	_put: function (layout) {
		this.updatedpool.push(layout);
	},
	_hasNext: function () {
		return this.updatedpool.length > 0;
	},
	_next: function () {
		return this.updatedpool.pop();		
	},
	_panelactivated: function (region,panel) {
		var rid, pid;
		if (panel.layout) {
			pid = $e($uuid(panel.el.id)).parentNode;			
		} else {
			pid = $e($uuid(panel.el.id));			
		}
		rid = pid.parentNode;
		var layout = zkExt.BorderLayout.getLayout(rid);
		//if (!layout._updating) zkau.sendUpdateResult(rid.id, pid.id);
	},
	_expanded: function (region) {
		var uuid = $uuid(region.id);
		zkau.send({uuid: uuid, cmd: "onOpen", data: [true]},
				zkau.asapTimeout($e(uuid), "onOpen"));
	},
	_collapsed: function (region) {
		var uuid = $uuid(region.id);
		zkau.send({uuid: uuid, cmd: "onOpen", data: [false]},
				zkau.asapTimeout($e(uuid), "onOpen"));
	}
};
zkExt.BorderLayout.getLayout = function (cmp, cleanup) {
	var lcmp = getZKAttr(cmp,"lid");
	lcmp = $e(lcmp);
	var layout = zkau.getMeta(lcmp);
	if (layout == null && !cleanup) {
		layout = new zkExt.BorderLayout(lcmp);
	}
	return layout;
};
zkExt.BorderLayout.getRealPanel = function (panel) {
	if (panel.layout)return $e($uuid(panel.el.id)).parentNode;			
	else return $e($uuid(panel.el.id));
};
zkExt.BorderLayout.getInitConfig = function (cmp) {
	var config = {};
	var init = getZKAttr(cmp,"inits");
	if (!init)return config;
	var inits = init.split(",");
	for (var i =0;i < inits.length; i++) {
		var c = getZKAttr(cmp,inits[i]);
		if (inits[i] == "margins" || inits[i] == "cmargins") {
			var ms = c.split(",");
			config[inits[i]] = {top: parseInt(ms[0]), left: parseInt(ms[1]), right: parseInt(ms[2]), bottom: parseInt(ms[3])};
		}else if (c == "true") {
			config[inits[i]] = true;
		}else if (c == "false") {
			config[inits[i]] = false;
		}else {
			config[inits[i]] = c;	
		}		
	}
	return config;
};
zkExtBorderLayout.childchg = function (cmp) {
	var layout = zkau.getMeta(cmp);
	layout.rerender();
};
zkExtBorderLayout.init = function (cmp) {	
	var layout = zkau.getMeta(cmp);
	var np = cmp.parentNode;
	
	// bug #1794652
	var rootlayout;
	while($type(np) == "ExtNestedPanel") { 
		rootlayout = $parentByType(np, "ExtBorderLayout");
		if (!rootlayout)break;
		np = rootlayout.parentNode;
	}
	if (rootlayout) {  	
		var old = $real(cmp);   
		if (old) old.parentNode.removeChild(old);		
		rootlayout.appendChild(old);
	}
	layout.el = new Ext.BorderLayout(cmp.id + "!real", layout.getRegions());
	layout.render();
	layout.el.on('layout', function () {zk.onResize(0, cmp);}, layout.el);
	zk.addInitLater(function () {
			zk.onVisiAt(cmp);
		}, true);
};
zkExtBorderLayout.cleanup = function (cmp) {
	if ($type(cmp.parentNode) == "ExtNestedPanel") {	
		var npc = cmp.parentNode;
		npc.innerlayout = null; // clean related
		var rlayout = zkExt.BorderLayout.getLayout(npc);
		var l = rlayout.el;
		if (l) {
			var rcmp = $e(cmp.id + "!real");
			if (rcmp) { // remove the related nested panel.
				var r = l.getRegion(getZKAttr(npc.parentNode,"pos"));
				var np = r.remove(rcmp.id);
				rlayout.removePanel(r.position, np); // clean related
			}
		}
	} else {
		var l = zkau.getMeta(cmp).el;
		zk.remove(l.el.dom);
	}
	zkau.getMeta(cmp).clean();
};
zkExtContentPanel.init = function (cmp) {
	var layout = zkExt.BorderLayout.getLayout(cmp);
	var panel = new Ext.ContentPanel(cmp.id + "!cave", zkExt.BorderLayout.getInitConfig(cmp));
	panel.tabTip = undefined;
	layout.addPanel(getZKAttr(cmp.parentNode,"pos"),panel);
};
zkExtContentPanel.cleanup = function (cmp) {
	var layout = zkExt.BorderLayout.getLayout(cmp, true);
	var l = layout.el;
	if (l) {
		var r = l.getRegion(getZKAttr(cmp.parentNode,"pos"));
		if (r) { // maybe the region is null;
			var p = r.remove(cmp.id + "!cave");
			layout.removePanel(r.position, p); // clean related
		}
	}
};
zkExtNestedPanel.init = function (cmp) {
	var layout = zkExt.BorderLayout.getLayout(cmp);
	var panel = new Ext.NestedLayoutPanel(cmp.innerlayout, zkExt.BorderLayout.getInitConfig(cmp));
	panel.tabTip = undefined;
	layout.addPanel(getZKAttr(cmp.parentNode,"pos"),panel);
};
zkExtNestedPanel.cleanup = function (cmp) {
	if (cmp.innerlayout) {
		var layout = zkExt.BorderLayout.getLayout(cmp, true);
		var l = layout.el;
		if (l) {
			var r = l.getRegion(getZKAttr(cmp.parentNode,"pos"));
			if (r) { // maybe the region is null;
				var p = r.remove(cmp.innerlayout.id);
				layout.removePanel(r.position, p); // clean related
			}
		}
	}
};
zkExtBorderLayoutRegion.init = function (cmp) {	
	var layout = zkExt.BorderLayout.getLayout(cmp);
	var config = zkExt.BorderLayout.getInitConfig(cmp);
	config.id = cmp.id + "!real"
	layout.addRegion(getZKAttr(cmp,"pos"), config);
};
zkExtBorderLayoutRegion.cleanup = function (cmp) {
	var layout = zkExt.BorderLayout.getLayout(cmp, true);
	var l = layout.el;
	if (l) {
		var r = l.getRegion(getZKAttr(cmp,"pos"));
		r.clearPanels();
		layout.removeRegion(r.position); // clean related
		zk.remove(r.el.dom);
		if (r.collapsible) zk.remove(r.collapsedEl.dom);
		if (r.split) {
			var split = r.getSplitBar();
			if (split) zk.remove(split.el.dom);
		}
		l.regions[r.position] = null;
		l.layout();
	}
};
/** Panel */
zkExtContentPanel.setAttr = function (cmp, name, value) {	
	var l = zkExt.BorderLayout.getLayout(cmp).el;
	var r = l.getRegion(getZKAttr(cmp.parentNode, "pos"));
	var p = r.getPanel(cmp.innerlayout ? cmp.innerlayout.id : cmp.id + "!cave");
	switch (name) {
		case "z.autoScroll" :
			p.autoScroll = value == "true";
			if (value == "true") {
				p.resizeEl.setStyle("overflow", "auto");
				p.resizeEl.setStyle("position", "relative");// Bug #1824411
			} else {
				p.resizeEl.setStyle("overflow", "hidden");				
				p.resizeEl.setStyle("position", "");
			}
			return true;
		case "z.fitContainer" : 
			p.fitContainer = value == "true";
			return true;
		case "z.fitToFrame" : 
			p.fitToFrame = value == "true";
			return true;
		case "z.resizeEl" : 
			p.resizeEl = Ext.get(value, true);
			p.resizeEl.setStyle("overflow", p.autoScroll ? "auto" : "hidden");
			return true;
		case "z.title" : 
			p.setTitle(value);
			return true;
	}
	return false;
};
zkExtNestedPanel.setAttr = zkExtContentPanel.setAttr;

/** BorderLayoutRegion */
zkExtBorderLayoutRegion.setAttr = function (cmp, name, value) {
	var l = zkExt.BorderLayout.getLayout(cmp).el;
	var r = l.getRegion(getZKAttr(cmp, "pos"));
	switch (name) {
		case "z.cmargins" :
			var cm = value.split(",");
			r.cmargins = {top: parseInt(cm[0]), left: parseInt(cm[1]), right: parseInt(cm[2]), bottom: parseInt(cm[3])};
			l.layout();
			return true;
		case "z.margins" :
			var cm = value.split(",");
			r.margins = {top: parseInt(cm[0]), left: parseInt(cm[1]), right: parseInt(cm[2]), bottom: parseInt(cm[3])};
			l.layout();
			return true;
		case "z.showPanel" :
			var p = $e(value);
			value = p.innerlayout ? p.innerlayout.id : p.id + "!cave";
			r.showPanel(value);
			return true;
		case "z.resizeTo" :
			r.resizeTo(parseInt(value));
			return true;	
		case "z.animate" :
			r.config.animate = value == "true";
			return true;	
		case "z.autoHide" :
			if (value == "true")	r.initAutoHide();
			else r.clearAutoHide();
			return true;
		case "z.autoScroll" :
			r.autoScroll = value == "true";
			r.bodyEl.setStyle("overflow", value == "true" ? "auto" : "hidden");
			if (r.tabs) r.tabs.bodyEl.setStyle("overflow", value == "true" ? "auto" : "hidden");
			return true;	
		case "z.collapsed" :
			if (value == "true") r.collapse();
			else r.expand();
			return true;	
		case "z.collapsedTitle" :
			r.setCollapsedTitle(value);
			return true;	
		case "z.hideTabs" :
			if (r.tabs) r.tabs.stripWrap.setDisplayed(value == "false");
			return true;	
		case "z.hideWhenEmpty" :
			if (value == "true") {
				r.validateVisibility();
				r.on("paneladded", r.validateVisibility, r);
	       		r.on("panelremoved", r.validateVisibility, r);
			} else {
				r.un("paneladded", r.validateVisibility, r);
	       		r.un("panelremoved", r.validateVisibility, r);
			}
			return true;
		case "z.minTabWidth" :
			if (r.tabs) {
				r.tabs.minTabWidth = parseInt(value);
				r.autoSizeTabs();
			}
			return true;
		case "z.maxTabWidth" :
			if (r.tabs) {
				r.tabs.maxTabWidth = parseInt(value);
				r.autoSizeTabs();
			}
			return true;	
		case "z.preferredTabWidth" :
			if (r.tabs) {
				r.tabs.preferredTabWidth = parseInt(value);
				r.autoSizeTabs();
			}
			return true;	
		case "z.resizeTabs" :
			if (r.tabs) {
				r.tabs.resizeTabs = value == "true";
				r.autoSizeTabs();
			}
			return true;
		case "z.title" :
			r.config.title = false; // avoid its judgement.
			r.updateTitle(value);
			return true;	
		case "z.visible" :
			r[value]();
			return true;	
		case "z.maxSize" :
			if (r.split)	r.config.maxSize = r.split.maxSize = parseInt(value);
			return true;
		case "z.minSize" :
			if (r.split)	r.config.minSize = r.split.minSize = parseInt(value);
			return true;
		case "z.useShim" :
			if (r.split)	r.config.useShim = r.split.useShim = value == "true";
			return true;			
		case "z.unhidePanel" : 
			var p = $e(value);
			value = p.innerlayout ? p.innerlayout.id : p.id + "!cave";
			r.unhidePanel(value);
			return true;
		case "z.hidePanel" :
			var p = $e(value);
			value = p.innerlayout ? p.innerlayout.id : p.id + "!cave";
			r.hidePanel(value);
			return true;			
	}
	return false;
};
});