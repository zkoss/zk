/* yau.js

{{IS_NOTE
	Purpose:
		
	Description:
		Ext 1.0.1 version.
	History:
		Jun 27, 2007 10:42:13 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
function $childInner(cmp) {
	var id = $uuid(cmp);
	if (id) {
		var n = $e(id + "!inner");
		if (n) return n;
	}
	return null;
};
function $childContent(cmp) {
	var id = $uuid(cmp);
	if (id) {
		var n = $e(id + "!content");
		if (n) return n;
	}
	return null;
};
function $realMetaByType(el, type) {
	el = $parentByType(el, type);
	return el != null ? zkau.getMeta($real(el)): null;
};
function $replaceNestValue(n, v) {
	if (!n || !v) return "";
	var el = document.createElement('span');
	el.innerHTML = n.trim();
	if (el.firstChild  && el.firstChild.innerHTML)el.firstChild.innerHTML = v;
   	return el.innerHTML;
};
zkExt = {};

if (!zkExt._loadcss) { // only load once
	zk.loadCSS('/yuiextz/css/yuiextz.css.dsp');	
	zk.loadCSS('/js/ext/yuiext/resources/css/ext-all.css');	
	zkExt._loadcss = true;
};
zkExt.HIDDEN_AUTO_ID = 1000;
zkExt.data = {};
zkExt.data.Store = {
	ADD: 0,
	ADD_AFTER: 1,
	ADD_BEFORE: 2,
	outer: function (uuid, cmp, html) {	 	
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var ds = gcmp.grid.getDataSource();
		var fields = ds.reader.recordType.prototype.fields;
		fields = this.adjustIndex(fields, gcmp.grid.getColumnModel()); // bug #1787511
		var rn = zkExt.tblCreateElements(html);
		var info = this.getFieldsInfo(fields,rn);
		var Record = Ext.data.Record.create(fields);
		var record = new Record(info.values, rn.id);
		record.node = rn;
		record.cells = info.cells;
		var index  = ds.indexOfId(cmp.id);
		var r = ds.getAt(index);
		delete r.cells; //save memory
		delete r.node; //save memory
		ds.remove(r);
		ds.insert(index,record);		
	},
	add: function (uuid, cmp, html, f) {	 	
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var ds = gcmp.grid.getDataSource();
		var sm = gcmp.grid.getSelectionModel();
		var fields = ds.reader.recordType.prototype.fields;
		fields = this.adjustIndex(fields, gcmp.grid.getColumnModel()); // bug #1787511       
		var rn = zkExt.tblCreateElements(html);
		var info = this.getFieldsInfo(fields,rn);
		var Record = Ext.data.Record.create(fields);
		var record = new Record(info.values, rn.id);
		record.node = rn;
		record.cells = info.cells;
		switch (f) {
			case 1:
				var index  = ds.indexOfId(cmp.id);
				ds.insert(++index,record);	
				break;
			case 2:
				var index  = ds.indexOfId(cmp.id);
				ds.insert(index,record);				
				break;
			default:
				ds.add(record);
		}		
        // gcmp.grid.view.refresh(); // bug#1778553  refresh the click event of row.
	},
	remove: function (uuid, cmp) {
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var ds = gcmp.grid.getDataSource();
		var r =	ds.getById(cmp.id);
		delete r.cells; //save memory
		delete r.node; //save memory
		ds.remove(r);
	},
	removeCell: function (uuid, cmp) {
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var ds = gcmp.grid.getDataSource();
		var cm = gcmp.grid.getColumnModel();
		var r =	ds.getById(cmp.parentNode.id);
		for (var i = 0; i < cm.getColumnCount(); i++) {			
			var n = r.cells[cm.getColumnId(i)];
			if (n && n.id == cmp.id) {
				r.cells[cm.getColumnId(i)] = null;
				r.data[cm.getColumnId(i)] = null;
				break;
			}
		}
		r.commit();
	},
	addCell: function (uuid, cmp, html, f) {
		var rn = zkExt.tblCreateElements(html);
		var tr = cmp.parentNode.id;
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var ds = gcmp.grid.getDataSource();
		var cm = gcmp.grid.getColumnModel();
		var r =	f == 0 ? ds.getById(cmp.id) : ds.getById(tr);
		for (var i = 0; i < cm.getColumnCount(); i++) {
			if (f == 0) {
				r.cells[cm.getColumnId(i)] = rn;
				r.data[cm.getColumnId(i)] = rn.innerHTML;
				break;
			}
			var n = r.cells[cm.getColumnId(i)];			
			if (n && n.id == cmp.id) {
				switch (f) {
					case 1:
						r.cells[cm.getColumnId(i+1)] = rn;
						r.data[cm.getColumnId(i+1)] = rn.innerHTML;
						break;
					case 2:
						r.cells[cm.getColumnId(i-1)] = rn;
						r.data[cm.getColumnId(i-1)] = rn.innerHTML;
						break;
				}
				break;
			}
		}
		r.commit();
	},
	getFieldsInfo: function (fields,rn) {		
		var q = Ext.DomQuery;
		var cells = {};	       
		var values = {};
		for (var j = 0, jlen = fields.length; j < jlen; j++) {
		    var f = fields.items[j];
		    var v = q.selectValue(f.mapping || f.name, rn, f.defaultValue);
		    v = f.convert(v);
		    values[f.name] = v;
		    cells[f.name] = q.selectNode('td:nth('+(j+1)+')',rn);
		}
		return {cells: cells,values: values};
	},
	adjustIndex: function (fields, cm) {
		var clone = fields.clone();
		fields.clear();
		for (var i = 0; i < cm.getColumnCount(); i++) {
			var colId = cm.getColumnId(i);
			var o = clone.item(colId);
			o.mapping = "td:nth(" + (i+1) + ")/@innerHTML";
			fields.add(colId, o);
		}	
		clone.clear();
		clone = null;
		return fields;
	}
};
zkExt.data.HTMLReader = {};

zkExt.grid = {};
zkExt.form = {};
/**
 * Override the Ext.form.DateField object.
 * @param {Object} config
 */
zkExt.form.DateField = function (config) {
	Ext.apply(this, config);
    zkExt.form.DateField.superclass.constructor.call(this);
};
Ext.extend(zkExt.form.DateField, Ext.form.DateField,{
	parseDate : function (value) {
        if (!value || value instanceof Date)
            return value;
		if (isNaN(Date.parse(value)))return null;
        var v = zk.parseDate(value,this.format);
		if (!v)v = new Date(value);		
		return v;
    }, 
	formatDate : function (date) {
        return (!date || !(date instanceof Date)) ?
               date : zk.formatDate(date,this.format);
    }
});
/**
 * Override the original ZK client engine.
 */
zkExt.oldCmd1 = {};
zkExt.oldCmd1.addAft = zkau.cmd1.addAft;
zkExt.oldCmd1.addBfr = zkau.cmd1.addBfr;
zkExt.oldCmd1.addChd = zkau.cmd1.addChd;
zkExt.oldCmd1.outer = zkau.cmd1.outer;
zkExt.oldCmd1.setAttr = zkau.cmd1.setAttr;
zkExt.oldCmd1.rm = zkau.cmd1.rm;
zkau.cmd1.setAttr = function (uuid, cmp, dt1, dt2) {
	if (zkExt.isExt(cmp ,"ExtRow")) {
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		zkExt.oldCmd1.setAttr(uuid, cmp, dt1, dt2) ;
		var ds = gcmp.grid.getDataSource();
		var r =	ds.getById(cmp.id);
		r.node = cmp;
		return true;
	} else if (zkExt.isExt(cmp ,"ExtGrid")) {
		if (dt1 == "z.init") {
			// Use cmp to invoke init function of the grid .
			zkExtGrid._renderNow(cmp);
			return true;
		}	
		var gcmp = zkau.getMeta($real(cmp));
		zkExt.oldCmd1.setAttr(uuid, $real(cmp), dt1, dt2) ;
		if (dt1 == "style.width") {	
			gcmp.grid.autoWidth = false;
			gcmp.grid.container.setWidth(dt2);
			gcmp.grid.view.fitColumns();
			gcmp.grid.view.fitColumns();
		} else if (dt1 == "style.height") {	
			gcmp.grid.autoHeight = false;
			gcmp.grid.container.setHeight(dt2);
		} else if (dt1 == "style") {
			gcmp.grid.container.setStyle(dt2);
		} else if (dt1 == "class") {
			gcmp.grid.container.addClass(dt2);
		}	
		return true;
	} else {		
		zkExt.oldCmd1.setAttr(uuid, cmp, dt1, dt2) ;
		return true;
	}
};

zkau.cmd1.rm = function (uuid, cmp) {   
	 if (zkExt.isExt(cmp ,"ExtRow")) {
		if (cmp) {
			zk.cleanupAt(cmp);
			cmp = $childExterior(cmp);
			zkExt.data.Store.remove(uuid, cmp);
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
		return true;
	}  else if (zkExt.isExt(cmp ,"ExtCell")) {
		if (cmp) {
			zk.cleanupAt(cmp);
			cmp = $childExterior(cmp);
			zkExt.data.Store.removeCell(uuid, $childExterior(cmp));
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
		return true;
	} else {		
		zkExt.oldCmd1.rm(uuid, cmp);
		return true;
	}
};
zkau.cmd1.outer = function (uuid, cmp, html) {
	if (zkExt.isExt(cmp ,"ExtRow")) {
		zk.cleanupAt(cmp);
		var from = cmp.previousSibling, from2 = cmp.parentNode,
			to = cmp.nextSibling;
		zkExt.data.Store.outer(uuid, cmp, html);
		if (from) zkau._initSibs(from, to, true);
		else zkau._initChildren(from2, to);
		if (zkau.valid) zkau.valid.fixerrboxes();
		return true;
	} else if (zkExt.isExt(cmp ,"ExtCol")) {
		zk.cleanupAt(cmp);
		var from = cmp.previousSibling, from2 = cmp.parentNode,
			to = cmp.nextSibling;					
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var cm = gcmp.grid.getColumnModel();
		var rn = zkExt.tblCreateElements(html);
		for (var i = 0; i < cm.getColumnCount(); i++) {
			if (cm.getColumnId(i) == cmp.id) {
				cm.setColumnHeader(i,rn.innerHTML);
				break;	
			}
		}		
		if (from) zkau._initSibs(from, to, true);
		else zkau._initChildren(from2, to);
		if (zkau.valid) zkau.valid.fixerrboxes();
		return true;
	} else if (zkExt.isExt(cmp ,"ExtCell")) {
		zk.cleanupAt(cmp);
		var from = cmp.previousSibling, from2 = cmp.parentNode,
			to = cmp.nextSibling;
		var cmpE = $childExterior(cmp);
		if ($type(cmpE) == "ExtCell") {
			var grid = $realMetaByType(cmp,"ExtGrid").grid;			
			var ds = grid.getDataSource();
			var cm = grid.getColumnModel();
			var r =	ds.getById(cmpE.parentNode.id);
			for (var i = 0; i < cm.getColumnCount(); i++) {
				var field = cm.getColumnId(i);
				var n = r.cells[field];			
				if (n.id == cmpE.id) {
					r.data[field] = html;
					break;
				}
			}
		}
		zk.setOuterHTML(cmp, html);
		if (from) zkau._initSibs(from, to, true);
		else zkau._initChildren(from2, to);
		if (zkau.valid) zkau.valid.fixerrboxes();
		return true;	
	} else {		
		zkExt.oldCmd1.outer(uuid, cmp, html);
		if (cmp) {//Bug #1803948
			var parent = $e($uuid(cmp.parentNode.id)); // bug #1792952
			if (parent && $type(parent) == "ExtContentPanel") {
				zkExt.BorderLayout.getLayout(parent).el.layout();
			}	
		}
		return true;
	}
};
zkau.cmd1.addAft = function (uuid, cmp, html) {
	if (zkExt.isExt(cmp ,"ExtRow")) {
		var n = $childExterior(cmp);
		var to = n.nextSibling;
		zkExt.data.Store.add(uuid, n, html, zkExt.data.Store.ADD_AFTER);
		zkau._initSibs(n, to, true);
		return true;
	} else if (zkExt.isExt(cmp ,"ExtCell")) {		
		var n = $childExterior(cmp);
		var to = n.nextSibling;
		zkExt.data.Store.addCell(uuid, n, html, zkExt.data.Store.ADD_AFTER);
		zkau._initSibs(n, to, true);
		return true;
	} else {		
		zkExt.oldCmd1.addAft(uuid, cmp, html);
		return true;		
	}
};
zkau.cmd1.addBfr = function (uuid, cmp, html) {
	if (zkExt.isExt(cmp ,"ExtRow")) {
		var n = $childExterior(cmp);
		var to = n.previousSibling;
		zkExt.data.Store.add(uuid, n, html, zkExt.data.Store.ADD_BEFORE);
		zkau._initSibs(n, to, false);
		return true;
	} else if (zkExt.isExt(cmp ,"ExtCell")) {
		var n = $childExterior(cmp);
		var to = n.previousSibling;		
		zkExt.data.Store.addCell(uuid, n, html, zkExt.data.Store.ADD_BEFORE);
		zkau._initSibs(n, to, false);
		return true;
	} else {		
		zkExt.oldCmd1.addBfr(uuid, cmp, html);
		return true;
	}
};
zkau.cmd1.addChd = function (uuid, cmp, html) {
	if (zkExt.isExt(cmp, "ExtRows")) {
		cmp = $real(cmp); //go into the real tag (e.g., tabpanel)
		var lc = cmp.lastChild;
		zkExt.data.Store.add(uuid, cmp, html, zkExt.data.Store.ADD);
		if (lc) zkau._initSibs(lc, null, true);
		else zkau._initChildren(cmp);
		return true;
	} else if (zkExt.isExt(cmp, "ExtRow")) {
		cmp = $real(cmp); //go into the real tag (e.g., tabpanel)
		var lc = cmp.lastChild;
		zkExt.data.Store.addCell(uuid, cmp, html, zkExt.data.Store.ADD);
		if (lc) zkau._initSibs(lc, null, true);
		else zkau._initChildren(cmp);
		return true;
		
	} else {		
		zkExt.oldCmd1.addChd(uuid, cmp, html);
		return true;
	}
};
zkExt.tblCreateElements = function (html) {
	var level;
	html = html.trim(); //If not trim, Opera will create TextNode!
	var tag = zk.tagOfHtml(html)
	switch (tag) {
	case "TABLE":
		level = 0;
		break;
	case "TR":
		level = 2;
		html = '<table>' + html + '</table>';
		break;
	case "TH": case "TD":
		level = 3;
		html = '<table><tr>' + html + '</tr></table>';
		break;
	case "COL":
		level = 2;
		html = '<table><colgroup>'+html+'</colgroup></table>';
		break;
	default://case "THEAD": case "TBODY": case "TFOOT": case "CAPTION": case "COLGROUP":
		level = 1;
		html = '<table>' + html + '</table>';
		break;
	}

	//get the correct node
	var el = document.createElement('DIV');
	el.innerHTML = html;
	while (--level >= 0) {
		el = el.firstChild;		
		if ($tag(el) == "TBODY")el = el.firstChild;
	}
	
	return el;
};
zkExt.isExt = function (cmp, type) {
	if (!cmp)return false;
	var s = $type($real(cmp)) || "";
	if (!type) {
		if (s.startsWith("Ext"))return true;
		s = $type($childExterior(cmp)) || "";	
		return s.startsWith("Ext");
	}
	if (s == type)return true;
	s = $type($childExterior(cmp)) || "";
	return s == type;
};

zkExt.data.HTMLReader = function (meta, recordType) {
	zkExt.data.HTMLReader.superclass.constructor.call(this, meta, recordType);
};
Ext.extend(zkExt.data.HTMLReader, Ext.data.DataReader, {
    read : function (response) {
        var doc = response.responseXML;
        if (!doc) {
            throw {message: "HTMLReader.read: HTML Document not available"};
        }
        return this.readRecords(doc);
    },
    
    readRecords : function (doc) {
        this.xmlData = doc;
        var root = doc.documentElement || doc;
    	var q = Ext.DomQuery;
    	var recordType = this.recordType, fields = recordType.prototype.fields;
    	var sid = this.meta.id;
    	var totalRecords = 0, success = true;
    	if (this.meta.totalRecords) {
    	    totalRecords = q.selectNumber(this.meta.totalRecords, root, 0);
    	}
        
        if (this.meta.success) {
            var sv = q.selectValue(this.meta.success, root, true);
            success = sv !== false && sv !== 'false';
    	}
    	var records = [];
    	var ns = q.select(this.meta.record, root);
        for (var i = 0, len = ns.length; i < len; i++) {
        	var cells = {};
	        var n = ns[i];
	        var values = {};
	        var id = sid ? n.getAttribute(sid) : undefined;
	        for (var j = 0, jlen = fields.length; j < jlen; j++) {
	            var f = fields.items[j];
                var v = q.selectValue(f.mapping || f.name, n, f.defaultValue);
	            v = f.convert(v);
	            values[f.name] = v;
	            // add the info of each child of row
	            cells[f.name] = q.selectNode('td:nth('+(j+1)+')',n);
	        }
	        var record = new recordType(values, id);
	        record.node = n;
	        record.cells = cells;
	        records[records.length] = record;
	    }

	    return {
	        success : success,
	        records : records,
	        totalRecords : totalRecords || records.length
	    };
    }
});
zkExt.LoadMask = {    
        init : function () {     	  	
				this.clean();
        		var n = $e("loading-mask");
				if (!n) {				
	        		this.createNode();  
				}		
				var loading = Ext.get('loading');
				var mask = Ext.get('loading-mask');
				mask.setOpacity(.8);
				mask.shift({
					xy:loading.getXY(),
					width:loading.getWidth(),
					height:loading.getHeight(), 
					remove:true,
					duration:1,
					opacity:0.3,
					easing:'bounceOut',
					callback : function () {						
						loading.fadeOut({duration:.2,remove:true});
						zkExt.LoadMask.clean(); // bug # 1773597
					}
				});
        },
        createNode : function () {
			var n = document.createElement("DIV");
			document.body.appendChild(n);  	    
		 	    var html = '<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">&#160;</div>'
		 	    +'<div id="loading"><div class="loading-indicator"><img src="'+zk.getUpdateURI('/web/js/ext/yuiext/resources/images/default/grid/loading.gif')
		 	    +'" style="width:16px;height:16px;" align="absmiddle">&#160;Loading...</div></div>';  
			zk._setOuterHTML(n, html);
		},
		clean : function () {
			var n = $e("loading-mask");
			if (n) n.parentNode.removeChild(n);
			var n = $e("loading");
			if (n) n.parentNode.removeChild(n);
		}        
};

