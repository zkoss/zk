/* grid.js

{{IS_NOTE
	Purpose:
		
	Description:
		Ext 1.0.1 version.
	History:
		Jun 22, 2007 4:29:13 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("yuiextz.yau!dsp");
zk.addModuleInit(function () {
zkExtGrid = {};
zkExtGridView = {};
zkExtCols = {};
zkExtCol = {};
zkExtCol.setAttr = function (cmp, name, value) {
	if (name == "z.sort") {
		var gcmp = $realMetaByType(cmp,"ExtGrid");		
		var ds = gcmp.grid.dataSource;
		var f = ds.fields.get(cmp.id);
		var dir = "";
        switch (value) {
			case "ascending": dir = "ASC"; break;
			case "descending": dir = "DESC"; break;
		}
        ds.sortToggle[f.name] = dir;
        ds.sortInfo = {field: f.name, direction: dir};
        gcmp.grid.view.updateHeaderSortState();   
		return true;
	} else if (name == "z.lock") {
		var gcmp = $realMetaByType(cmp,"ExtGrid");
		var cm	= gcmp.grid.colModel;
		if (value == "true") {	
			cm.setLocked(cm.getIndexById(cmp.id),true);
		} else {
			cm.setLocked(cm.getIndexById(cmp.id),false);
		}
		return true;
	} else if (name == "z.asc" || name == "z.dsc") {
		var gcmp = $realMetaByType(cmp,"ExtGrid");		
		gcmp.grid.colModel.getColumnById(cmp.id).sortable = value == "true" ? true : false;		
	}
	return false;
};
	
zkExt.grid.Grid = zkExtGrid;
zkExt.grid.GridView = zkExtGridView;
zkExt.grid.ColumnModel = {
	defaultRenderer: function (v) {	
		if (!v)return "";		
		var el = document.createElement('span');
		el.innerHTML = v.trim();
		if (el.firstChild  && el.firstChild.innerHTML) return el.firstChild.innerHTML;
       	return "";
	},
	booleanRenderer: function (v) {
		var s = zkExt.grid.ColumnModel.defaultRenderer(v);
        return $replaceNestValue(v, (s.toLowerCase() == "true" || s.toLowerCase() == "yes") ? 'Yes' : 'No');   
    }	
};
zkExt.grid.RowSelectionModel = {
	selectAll: function (cmp, value, notify) {		
		var grid = zkau.getMeta($real(cmp)).grid;
		if (grid.selModel.locked) return;
        grid.selModel.selections.clear();
        for (var i = 0, len = grid.dataSource.getCount(); i < len; i++) {
            grid.selModel.selectRow(i, true, false, true);
        }
	},
	select: function (cmp, value, notify) {
		var grid = zkau.getMeta($real(cmp)).grid;
		var ds = grid.dataSource;
		var index = value == "zk_n_a" ? -1 : ds.indexOfId(value);
		if (index < 0) {
			grid.selModel.clearSelections();
		} else {
			grid.selModel.selectRow(index, false, false, true);
		}
	},
	selectRows: function (cmp, value, notify) {	
		var grid = zkau.getMeta($real(cmp)).grid;	
		var ds = grid.dataSource;
		grid.selModel.clearSelections();
		for (var j = 0;;) {
			var k = value.indexOf(',', j);
			var s = (k >= 0 ? value.substring(j, k): value.substring(j)).trim();
			var index = ds.indexOfId(s);
			if (index >= 0)grid.selModel.selectRow(index, true, false, true); 
			if (k < 0) break;
			j = k + 1;
		}
	}
};
zkExtGrid._ignoreDefaultCls = function (cls) {
	if (!cls || cls.trim() == "")return cls;
	var clss = cls.split(" ");
	cls = "";
	for (var i = 0, ic; ic = clss[i]; i++) {
		if (!ic.startsWith("x-grid-")) {
			cls += ic + " ";
		}
	}
	return cls;
};
zkExtGrid._splitAttrs = function (n) {
	if (!n)return{style : "", cls : "",  attrs : ""};
	if (zk.ie) { // IE attributes has more than 80 attributes.
		var attrs = ["align", "width", "height", "title", "accessKey", "rowSpan", "colSpan", "z.type", "z.rows"
			, "z.trackMouseOver", "z.multiple", "z.onSelect", "z.onColumnLockChange", "z.onColumnMoved", "z.sort"
			, "z.lock", "z.selId", "z.gridType", "z.editortype", "z.format", "z.loaded", "z.model", "z.asc" ,"z.dsc"];
		var s = "", attr = "";
		if (n.attributes) {
			if (s = n.getAttribute("style")) {
				if (s.cssText.trim() != "")s = 'style="' + s.cssText + '"';
				else s = "";
			} else s = "";		
			for (var i = 0, a; a = attrs[i]; i++) {
				var v = n.getAttribute(a);
				if (v)attr += ' ' + a + '="' + v + '"';
			}
		}
		return {style : s, cls : zkExtGrid._ignoreDefaultCls(n.className || ""),  attrs : attr};
	}
	var s = "", c = "", attr = "";
	var attrs = n.attributes || [];
	for (var i = 0, ai; ai = attrs[i]; i++) {
		if (ai.name.toLowerCase() == "id") continue;
		else if (ai.name.toLowerCase() == "style" && ai.specified)s = ai.name + '="' +ai.value+ '" ';
		else if (ai.name.toLowerCase() == "class" && ai.specified)c = ai.value;
		else if (ai.specified)attr += ai.name + '="' +ai.value+ '" ';
	}
	return {
		style : s, cls : zkExtGrid._ignoreDefaultCls(c),  attrs : attr
	};
};
zkExtGrid.init = function (cmp) {		
    zkExt.LoadMask.init();
	cmp = $e(cmp);
	var grid = getZKAttr(cmp, "gridType") == "tableGrid" ?
		new zkExtGrid._tableGrid(cmp,zkExtGrid._config(cmp)) : 
		new zkExtGrid._editorGrid(cmp,zkExtGrid._config(cmp)) ;	 
	grid.container.id = cmp.id;
	grid.container.dom.id = cmp.id;
	grid.node = cmp; // store the first HTML dom of grid component.
	grid.view = new zkExtGridView(); // reorganize the data structure of HTML
	cmp.grid = grid;
	zkau.setMeta($real(cmp),cmp);// store the relation of cmp and grid 
	grid.colModel.on('columnmoved', zkExtGrid.onColumnMoved, grid.colModel);
	grid.colModel.on('columnlockchange', zkExtCols.onColumnLockChange, grid.colModel);
	grid.colModel.cmp = cmp;
	grid.selModel.on('rowselect', zkExtGrid.onSelect);
	grid.on('afteredit', zkExtGrid.onAfteredit, grid);
	grid.on('bodyscroll', function () {zkExtGrid._render(cmp, zk.gecko ? 200: 60);}, grid);
	grid.on('columnresize', function (index, resize) {$e(grid.colModel.getColumnId(index)).style.width = resize + "px";}, grid);
	if (getZKAttr(cmp, "dragdrop") == "true") {
		zkExtGrid.initDrop(grid);
	}
	grid.render();	
	zkExt.grid.RowSelectionModel.select(cmp, getZKAttr(cmp, "selId"));
	
	//init sort function
	grid.dataSource.sort = function (fieldName, dir) {	
		if (dir == "ASC")dir = "descending";
		if (dir == "DESC")dir = "ascending";
		zkau.send({uuid: fieldName, cmd: "onSort", data: [dir]}, 10);	             
	}
	cmp._beforeUnload = function () {
		zkExtGrid.cleanup(cmp); // save memory bug #1787605
	};
	zk.addBeforeUnload(cmp._beforeUnload);
};	
/** Renders listitems that become visible by scrolling.	 */
zkExtGrid._render = function (cmp ,timeout) {
	setTimeout("zkExtGrid._renderNow('"+cmp.id+"')", timeout);
};
zkExtGrid._renderNow = function (cmp) {
	cmp = $real(cmp);
	var gcmp = zkau.getMeta(cmp);	
	if (getZKAttr(cmp, "model") != "true") return;

	var ds = gcmp.grid.getDataSource();
	if (ds.getCount()<=0) return; //no row at all

	//Note: we have to calculate from top to bottom because each row's
	//height might diff (due to different content)
	var data = "";
	var body = cmp.childNodes[1];
	var min = body.scrollTop, max = min + body.offsetHeight;
	for (var j = 0; j < ds.getCount(); ++j) {
		var r = $e(ds.getAt(j).id);
		if ($visible(r)) {
			var top = zk.offsetTop(r);
			if (top + zk.offsetHeight(r) < min) continue;
			if (top >= max) break;
			if (getZKAttr(r, "loaded") != "true")
				data += "," + r.id;
		}
	}
	if (data) {
		data = data.substring(1);
		var uuid = $uuid(cmp);
		zkau.send({uuid: uuid , cmd: "onRender", data: [data]}, 0);
	}
};
zkExtGrid.initDrop = function (grid) {
	var dd = new Ext.dd.DropZone(grid.container, {
		ddGroup : 'ROWDD',
		notifyDrop : function (target, e, data) {
			if (data.grid) {
				var gcmp = zkau.getMeta($real(this.id));	
				var t = Ext.lib.Event.getTarget(e);
	        	var index = gcmp ? gcmp.grid.view.findRowIndex(t) : 
					target.view.findRowIndex(t);	
				if (index === false)return;
				var dropped = gcmp ? gcmp.grid.dataSource.getAt(index) : 
					target.grid.dataSource.getAt(index);
				if (getZKAttr(dropped.node, "drop") == "true") {					
					var dragged = data.grid.dataSource.getAt(data.rowIndex);
					if (dropped.id == dragged.id)return;
					var keys = "";
					if (e.altKey) keys += 'a';
					if (e.ctrlKey) keys += 'c';
					if (e.shiftKey) keys += 's';
					zkau.send({uuid: dropped.id , cmd: "onDrop", data: [dragged.id, e.xy[0], e.xy[1], keys]});				
				}
			}
			return false;
		},	    
	    notifyOver : function (target, e, data) {
			if (!data.grid)return this.dropAllowed; // avoid the moved header 
			var t = Ext.lib.Event.getTarget(e);
			var gcmp = zkau.getMeta($real(this.id));	
        	var index = gcmp ? gcmp.grid.view.findRowIndex(t) : 
				target.view.findRowIndex(t);	
			if (gcmp != null && gcmp.grid == data.grid && index == data.rowIndex)
				return this.dropNotAllowed;
			if (index !== false) {
				var dropped = gcmp ? gcmp.grid.dataSource.getAt(index) : 
				target.grid.dataSource.getAt(index);
				if (getZKAttr(dropped.node, "drop") == "true")
					return this.dropAllowed;				
			}
	        return this.dropNotAllowed ;
    	}
	});
	
};
zkExtGrid.onAfteredit = function (e) {
	var cell = e.record.get(e.field);
	var cmp = $e(e.field);
	var el = document.createElement('span');
		el.innerHTML = cell.trim();
	var uuid = el.firstChild.id;
	$e(uuid).innerHTML = e.value;
	e.record.commit();
	
	var etype = getZKAttr(cmp, "editortype");
	if (etype == "date") {
		var format = getZKAttr(cmp, "format");
		e.value = zk.formatDate(new Date(e.value),format);
	}
	zkau.send({uuid: uuid, cmd: "onChange", data: [e.value]},
		zkau.asapTimeout(uuid, "onChange", 30));
};
zkExtGrid._config = function (cmp) {
	// By default, configure the configuration of Grid
	var wd = cmp.style.width;
	var autoWidth = false;
	if (!wd || wd == "auto") {
		cmp.style.width = cmp.clientWidth + "px";	
		autoWidth = true;
	}
	var ht = cmp.style.height;
	var autoHeight = false;
	if (!ht || ht == "auto") {
		cmp.style.height = cmp.clientHeight + "px";		
		autoHeight = true;
	}
	var multiple = false;
	if (getZKAttr(cmp, "multiple") == "true") {
		multiple = true;
	}	
	var trackMouseOver = false;
	if (getZKAttr(cmp, "trackMouseOver") == "true") {
		trackMouseOver = true;
	}
	var enableColumnMove = false;
	if (getZKAttr(cmp, "columnmove") == "true") {
		enableColumnMove = true;
	}
	var enableColumnHide = false;
	if (getZKAttr(cmp, "columnhide") == "true") {
		enableColumnHide = true;
	}
	var enableDragDrop = false;
	if (getZKAttr(cmp, "dragdrop") == "true") {
		enableDragDrop = true;
	}
	var enableColLock = false;
	if (getZKAttr(cmp, "columnlock") == "true") {
		enableColLock = true;
	}
	return {
		'width': wd,
		'height': ht,
		'autoWidth': autoWidth,
		'autoHeight': autoHeight,
		'multiple' : multiple,
		'trackMouseOver' : trackMouseOver,
		'enableColumnMove' : enableColumnMove,
		'enableColumnHide' : enableColumnHide,
		'enableDragDrop' : enableDragDrop,
		'enableColLock': enableColLock
	};
};
zkExtCols.onColumnLockChange = function (cm, colIndex, locked) {
	var cmp = $e(cm.columns.id);
	var data = "";
	var rows = cm.getColumnCount();
		
	for (var j = 0; j < cm.getColumnCount(); ++j) {
		if (cm.isLocked(j)) {
			data += ","+cm.getColumnId(j);
		}
	}
	if (data) data = data.substring(1);
	var uuid = $uuid(cmp);
	zkau.send({uuid: uuid, cmd: "onColumnLockChange", data: [data]},
		zkau.asapTimeout(uuid, "onColumnLockChange", 100));
	
};
zkExtGrid.onColumnMoved = function (cm, oldIndex, newIndex) {
	var cmp = cm.cmp;
		zkau.send({uuid: cmp.id, cmd: "onColumnMoved",
				data: [cm.getDataIndex(newIndex),oldIndex,newIndex]},
    	 	zkau.asapTimeout(cmp, "onColumnMoved", 10));
};
zkExtGrid.onSelect = function (sm, rowIndex) {
	var cmp = sm.grid.node;
		var data = "";
		var rows = sm.getSelections();			
		for (var j = 0, row; row = rows[j]; ++j) {
			data += ","+row.id;
		}
		if (data) data = data.substring(1);
		var uuid = $uuid(cmp);
		zkau.send({uuid: uuid, cmd: "onSelect", data: [data]},
			zkau.asapTimeout(cmp, "onSelect", 100));
};
zkExtGrid.setAttr = function (cmp, name, value) {
	if (name == "z.trackMouseOver") {
		var grid = zkau.getMeta($real(cmp)).grid;
		if (value == "true") {
			grid.on("mouseover", grid.view.onRowOver, grid.view);
	        grid.on("mouseout", grid.view.onRowOut, grid.view);
		}else {
		  	grid.un("mouseover", grid.view.onRowOver, grid.view);
	        grid.un("mouseout", grid.view.onRowOut, grid.view);
		}
		return true;
	} else if (name == "z.multiple") {	
		var grid = zkau.getMeta($real(cmp)).grid;
		grid.selModel.singleSelect = value == "true" ? false : true;
		return true;
	} else if (name == "select") {
		zkExt.grid.RowSelectionModel.select(cmp, value);
		return true;		
	} else if (name == "selectAll") {
		zkExt.grid.RowSelectionModel.selectAll(cmp, value);
		return true;
	} else if (name == "chgSel") {
		zkExt.grid.RowSelectionModel.selectRows(cmp, value);
		return true;
	} else if (name == "z.selId") {
		zkExt.grid.RowSelectionModel.select(cmp, value);
		return true;
	}
	return false;
};
zkExtGrid.cleanup = function (cmp) {
	var gcmp = zkau.getMeta($real(cmp));
	if (gcmp) {
		// clean the relationship. bug #1787605
		if (gcmp.grid.colModel.cmp) delete gcmp.grid.colModel.cmp;
		if (gcmp.grid.node) delete gcmp.grid.node;
		if (gcmp.grid.colModel.columns) delete gcmp.grid.colModel.columns;
		var cm = gcmp.grid.colModel;
		for (var i = 0; i < cm.getColumnCount(); i++) {
			delete cm.config[i].node;
		}
		gcmp.grid.destroy();		
		gcmp.grid = null;
	}
	zkau.cleanupMeta($real(cmp));
	zk.rmBeforeUnload(cmp._beforeUnload);
};
/**
 * Create a table grid.
 * @param {Object} cmp
 * @param {Object} config
 */
zkExtGrid._tableGrid = function (cmp, config) {
    config = config || {};
    var cf = config.fields || [], ch = config.columns || [];
    var table = Ext.get(cmp);
    var ct = table.insertSibling();
    var fields = [], cols = [];
    var headers = table.query("thead th");
	var columns = table.query("thead tr")[0];
	var sizable = getZKAttr(columns,"sizable") == "true" ? true : false;
	for (var i = 0, h; h = headers[i]; i++) {
		var text = h.innerHTML;
		var name = h.id;
        fields.push(Ext.applyIf(cf[i] || {}, {
            name: name,
            mapping: 'td:nth('+(i+1)+')/@innerHTML',
			type: 'string'
        }));
		var asc = getZKAttr($e(name), "asc") == "true" ? true : false;		
		var dsc = getZKAttr($e(name), "dsc") == "true" ? true : false;		
        var sortable = asc && dsc;		
        var locked = getZKAttr($e(name), "lock") == "true" ? true : false;
		var width = h.offsetWidth;
		if (h.style.width) {
			if (h.style.width.indexOf("px")>-1)
			 	width = parseInt(h.style.width);
		}	  
		cols.push(Ext.applyIf(ch[i] || {}, {
			'header': text,
			'dataIndex': name,
			'id': name,
			'width': width,
			'tooltip': h.title,
			'node': h,
			'sortable': sortable,
			'locked':locked,
			'resizable': sizable
        }));
	}
    var ds  = new Ext.data.Store({
        reader: new zkExt.data.HTMLReader({
        	id:'id',
            record:'tbody tr'
        }, fields)
    });
    var cm = new Ext.grid.ColumnModel(cols);
    // add the info of zk columns
 	cm.columns = columns;
    if (config.width || config.height)
    	ct.setSize(config.width || 'auto', config.height || 'auto');    
    if (config.remove !== false)table.remove();    
	ds.loadData(table.dom);
    zkExtGrid._tableGrid.superclass.constructor.call(this, ct,
        Ext.applyIf(config, {
            'ds': ds,
            'cm': cm,
            'sm': new zkExtGrid._RowSelectionModel({singleSelect: !config.multiple})
        }
    ));
};
/**
 * Create a editable grid.
 * @param {Object} cmp
 * @param {Object} config
 */
zkExtGrid._editorGrid = function (cmp, config) {
    config = config || {};
    var cf = config.fields || [], ch = config.columns || [];
    var table = Ext.get(cmp);
    var ct = table.insertSibling();
    var fields = [], cols = [];
    var headers = table.query("thead th");
	var columns = table.query("thead tr")[0];
	var sizable = getZKAttr(columns,"sizable") == "true" ? true : false;
	for (var i = 0, h; h = headers[i]; i++) {
		var text = h.innerHTML;
		var name = h.id;
		var etype = getZKAttr(h, "editortype");
		var editor = null, dateFormat = null, renderer = null;
		switch (etype) {
			case "text":
				editor = new Ext.grid.GridEditor(new Ext.form.TextField({
               		allowBlank: false
				}));
				break;
			case "combo":
				var combo =  getZKAttr(h, "combo");
				combo = zkau.getByZid(h, combo); 
				editor = new Ext.grid.GridEditor(new Ext.form.ComboBox({
	               typeAhead: true,
	               triggerAction: 'all',
	               transform: combo,
	               lazyRender:true
	            }));
				break;
			case "number":
				editor = new Ext.grid.GridEditor(new Ext.form.NumberField({
					allowBlank: false
				}));
				break;
			case "date":
				dateFormat = getZKAttr(h, "format") || "yyyy/MM/dd";
				var df = dateFormat;
				editor = new Ext.grid.GridEditor(new zkExt.form.DateField({
	                format: df
	            }));
				renderer = function (v) {					
					var s = zkExt.grid.ColumnModel.defaultRenderer(v);
				    return $replaceNestValue(v, s ? zk.formatDate(new Date(s),df) : "");
				}	
				break;
			case "check":
				editor = new Ext.grid.GridEditor(new Ext.form.Checkbox());
				renderer = zkExt.grid.ColumnModel.booleanRenderer;
				break;
		}
        fields.push(Ext.applyIf(cf[i] || {}, {
            name: name,
            mapping: 'td:nth('+(i+1)+')/@innerHTML',
			dateFormat: dateFormat,
			type: "string"
        }));
		
		var asc = getZKAttr($e(name), "asc") == "true" ? true : false;		
		var dsc = getZKAttr($e(name), "dsc") == "true" ? true : false;		
        var sortable = asc && dsc;	
		var width = h.offsetWidth;
		if (h.style.width) {
			if (h.style.width.indexOf("px")>-1)
			 	width = parseInt(h.style.width);
		}
		cols.push(Ext.applyIf(ch[i] || {}, {
			'header': text,
			'dataIndex': name,
			'id': name,
			'width': width,
			'tooltip': h.title,
			'node': h,
			'sortable': sortable,
			'editor': editor,
			'renderer': renderer,
			'resizable': sizable
        }));
	}

    var ds  = new Ext.data.Store({
        reader: new zkExt.data.HTMLReader({
        	id:'id',
            record:'tbody tr'
        }, fields)
    });

    var cm = new Ext.grid.ColumnModel(cols);
    // add the info of zk columns
 	cm.columns = columns;
	
    if (config.width || config.height) {
    	ct.setSize(config.width || 'auto', config.height || 'auto');
    }
    if (config.remove !== false) {
        table.remove();
    }
	ds.loadData(table.dom);
    zkExtGrid._editorGrid.superclass.constructor.call(this, ct,
        Ext.applyIf(config, {
            'ds': ds,
            'cm': cm,
            'sm': new zkExtGrid._RowSelectionModel({singleSelect: !config.multiple})
        }
    ));
}; 
zkExtGridView = function (config) {
	Ext.apply(this, config);
    zkExtGridView.superclass.constructor.call(this);
};

/**
 * Customize the original RowSelectionModel.
 * @param {Object} config
 */
zkExtGrid._RowSelectionModel = function (config) {
	Ext.apply(this, config);
    zkExtGrid._RowSelectionModel.superclass.constructor.call(this);
};
/**
 * Override the selectRow function to provide one more parameter for disabling an event.(suppressEvent) 
 */
Ext.extend(zkExtGrid._RowSelectionModel, Ext.grid.RowSelectionModel,{
	 selectRow : function (index, keepExisting, preventViewNotify, suppressEvent) {
        if (this.locked || (index < 0 || index >= this.grid.dataSource.getCount())) return;
        if (this.fireEvent("beforerowselect", this, index, keepExisting) !== false) {
            if (!keepExisting || this.singleSelect) {
                this.clearSelections();
            }
            var r = this.grid.dataSource.getAt(index);
            this.selections.add(r);
            this.last = this.lastActive = index;
            if (!preventViewNotify) {
                this.grid.getView().onRowSelect(index);
            }
			if (!suppressEvent) {
	            this.fireEvent("rowselect", this, index, r);
	            this.fireEvent("selectionchange", this);
			}
        }
    }
});
Ext.extend(zkExtGrid._tableGrid, Ext.grid.Grid);
Ext.extend(zkExtGrid._editorGrid, Ext.grid.EditorGrid, {
	    startEditing : function (row, col) {
        this.stopEditing();
        if (this.colModel.isCellEditable(col, row)) {
            this.view.focusCell(row, col);
            var r = this.dataSource.getAt(row);
            var field = this.colModel.getDataIndex(col);
            var e = {
                grid: this,
                record: r,
                field: field,
                value: r.data[field],
                row: row,
                column: col,
                cancel:false
            };
            if (this.fireEvent("beforeedit", e) !== false && !e.cancel) {
                this.editing = true; // flag for buffering of orphan key strokes
                (function () { // complex but required for focus issues in safari, ie and opera
                    var ed = this.colModel.getCellEditor(col, row);
                    ed.row = row;
                    ed.col = col;
                    ed.record = r;
                    ed.on("complete", this.onEditComplete, this, {single: true});
                    ed.on("specialkey", this.selModel.onEditorKey, this.selModel);
                    this.activeEditor = ed;
                    var v = zkExt.grid.ColumnModel.defaultRenderer(r.data[field]);
                    ed.startEdit(this.view.getCell(row, col), v);
                }).defer(50, this);
            }
        }
    }
});
Ext.extend(zkExtGridView, Ext.grid.GridView,{
	init: function (grid) {
		Ext.grid.GridView.superclass.init.call(this, grid);

		this.bind(grid.dataSource, grid.colModel);

	    grid.on("headerclick", this.handleHeaderClick, this);

        if (grid.trackMouseOver) {
            grid.on("mouseover", this.onRowOver, this);
	        grid.on("mouseout", this.onRowOut, this);
	    }
	    grid.cancelTextSelection = function () {};
		this.gridId = grid.id;
		var tpls = this.templates || {};
		var attrs = zkExtGrid._splitAttrs(grid.node); 
	    tpls.master = new Ext.Template(
	       '<div class="x-grid" hidefocus="true">',
	          '<div id="' + grid.container.id + '!topbar" class="x-grid-topbar"></div>',
	          '<div id="' + grid.container.id + '!scroller" class="x-grid-scroller"><div></div></div>',
	          '<div id="' + grid.container.id + '!locked" class="x-grid-locked">',
	              '<div class="x-grid-header">{lockedHeader}</div>',
	              '<div class="x-grid-body">{lockedBody}</div>',
	          "</div>",
	          '<div id="' + grid.container.id + '!real"  class="x-grid-viewport ' + attrs.cls + '"' + attrs.attrs + ' ' + attrs.style + ' >',
	              '<div class="x-grid-header">{header}</div>',
	              '<div class="x-grid-body">{body}</div>',
	          "</div>",
	          '<div id="' + grid.container.id + '!bottombar" class="x-grid-bottombar"></div>',
	          '<a href="#" class="x-grid-focus" tabIndex="-1"></a>',
	          '<div id="' + grid.container.id + '!proxy" class="x-grid-resize-proxy">&#160;</div>',
	       "</div>"
	    );
	    tpls.master.disableformats = true;

	    tpls.header = new Ext.Template(
	       '<table border="0" cellspacing="0" cellpadding="0">',
	       '<tbody><tr id="{uuid}" class="x-grid-hd-row {cls}" {attrs} z.type="yuiextz.grid.ExtCols">{cells}</tr></tbody>',
	       "</table>{splits}"
	    );
	    tpls.header.disableformats = true;
		
		tpls.header.compile();

		tpls.hcell = new Ext.Template(
		   '<td id="{uuid}" class="x-grid-hd x-grid-td-{id} {cellId} {cls}" {attrs}><div id="{uuid}!inner" title="{title}" class="x-grid-hd-inner x-grid-hd-{id}">',
		   '<div id="{uuid}!content" class="x-grid-hd-text" unselectable="on">{value}<img class="x-grid-sort-icon" src="', Ext.BLANK_IMAGE_URL, '" /></div>',
		   "</div></td>"
		);
	    tpls.hcell.disableFormats = true;
		tpls.hcell.compile();
	
	    tpls.hsplit = new Ext.Template('<div class="x-grid-split {splitId} x-grid-split-{id}" style="{style}" unselectable="on">&#160;</div>');
	    tpls.hsplit.disableFormats = true;		
		tpls.hsplit.compile();


	    tpls.body = new Ext.Template(
	       '<table border="0" cellspacing="0" cellpadding="0">',
	       '<tbody id="' + getZKAttr(grid.node,"rows") + '" z.type="yuiextz.grid.ExtRows">{rows}</tbody>',
	       "</table>"
	    );
	    tpls.body.disableFormats = true;
		tpls.body.compile();

 		tpls.lockedBody = new Ext.Template(
	       '<table border="0" cellspacing="0" cellpadding="0">',
	       '<tbody id="' + getZKAttr(grid.node,"rows") + '!locked" z.type="yuiextz.grid.ExtRows">{rows}</tbody>',
	       "</table>"
	    );
	    tpls.lockedBody.disableFormats = true;
		tpls.lockedBody.compile();

	    tpls.row = new Ext.Template('<tr id="{uuid}" class="x-grid-row {cls} {alt}" z.type="yuiextz.grid.ExtRow" {attrs}>{cells}</tr>');
	    tpls.row.disableFormats = true;
		tpls.row.compile();

	    tpls.cell = new Ext.Template(
	        '<td id="{uuid}!chdextr" class="x-grid-col x-grid-td-{id} {cellId} {css} {cls}" z.type="yuiextz.grid.ExtCell" {attrs} tabIndex="0">',
	        '<div id="{uuid}!inner" class="x-grid-col-{id} x-grid-cell-inner"><div id="{uuid}!content" class="x-grid-cell-text" unselectable="on" {attr}>{value}</div></div>',
	        "</td>"
	    );
        tpls.cell.disableFormats = true;
		tpls.cell.compile();

		this.templates = tpls;
	},
	
    renderHeaders : function () {
	    var cm = this.cm;
        var ct = this.templates.hcell, ht = this.templates.header, st = this.templates.hsplit;
        var cb = [], lb = [], sb = [], lsb = [], p = {};
        for (var i = 0, len = cm.getColumnCount(); i < len; i++) {        
            var attrs = zkExtGrid._splitAttrs(cm.config[i].node);
            p.attrs = attrs.attrs + " " + attrs.style;
            p.cls = attrs.cls;
            p.cellId = "x-grid-hd-0-" + i;
            p.splitId = "x-grid-csplit-0-" + i;
            p.id = p.uuid = cm.getColumnId(i);
            p.title = cm.getColumnTooltip(i) || "";
            p.value = cm.getColumnHeader(i) || "";
            p.style = (this.grid.enableColumnResize === false || !cm.isResizable(i) || cm.isFixed(i)) ? 'cursor:default' : '';
            if (!cm.isLocked(i)) {
                cb[cb.length] = ct.apply(p);
                sb[sb.length] = st.apply(p);
            }else{
                lb[lb.length] = ct.apply(p);
                lsb[lsb.length] = st.apply(p);
            }
        }
        
        var attrs = zkExtGrid._splitAttrs(cm.columns); 
        
        return [ht.apply({uuid:cm.columns.id+"!locked", cells: lb.join(""), splits:lsb.join(""), cls: attrs.cls, attrs: attrs.attrs+" "+attrs.style}),
                ht.apply({uuid:cm.columns.id, cells: cb.join(""), splits:sb.join(""), cls: attrs.cls, attrs: attrs.attrs+" "+attrs.style})];
	},
	doRender : Ext.isGecko ?
            function (cs, rs, ds, startRow, colCount, stripe) {
                var ts = this.templates, ct = ts.cell, rt = ts.row;
                // buffers
                var buf = "", lbuf = "", cb, lcb, c, p = {}, rp = {}, r;
                for (var j = 0, len = rs.length; j < len; j++) {
                    r = rs[j], cb = "", lcb = "", rowIndex = (j+startRow);
                    for (var i = 0; i < colCount; i++) {
	                        c = cs[i];
	                        p.cellId = "x-grid-cell-" + rowIndex + "-" + i;
	                        p.id = c.id;
	                        var attrs;
	                        if (r.cells[c.name]) {
	                        	p.uuid =  r.cells[c.name].id;
	                        	attrs = zkExtGrid._splitAttrs(r.cells[c.name]);                    
		                       	p.uuid = $uuid(p.uuid);	                         
		                        p.cls = attrs.cls;
		                        p.attrs = attrs.attrs + " " + attrs.style;
		                        p.css = p.attr = "";
		                        if ($childContent(p.uuid)) {
		                        	r.cells[c.name].innerHTML = $childContent(p.uuid).innerHTML;
		                        	r.data[c.name] = $childContent(p.uuid).innerHTML;
		                        }
		                        p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
		                        if (p.value == undefined || p.value === "") p.value = "&#160;";
		                        if (r.dirty && typeof r.modified[c.name] !== 'undefined') {
		                            p.css += p.css ? ' x-grid-dirty-cell' : 'x-grid-dirty-cell';
		                        }
		                        var markup = ct.apply(p);
		                        if (!c.locked) {
		                            cb+= markup;
		                        }else{
		                            lcb+= markup;
		                        }
							}
                    }
                    var alt = [];
                    if (stripe && ((rowIndex+1) % 2 == 0)) {
                        alt[0] = "x-grid-row-alt";
                    }
                    if (r.dirty) {
                        alt[1] = " x-grid-dirty-row";
                    }
                    var attrs = zkExtGrid._splitAttrs(r.node); 
                    rp.attrs = attrs.attrs + " " + attrs.style;
                    rp.cls = attrs.cls;
                    rp.cells = lcb;
                    if (this.getRowClass) {
                        alt[2] = this.getRowClass(r, rowIndex);
                    }
                    rp.alt = alt.join(" ");
                    rp.uuid = rs[j].id+"!locked";
                    lbuf+= rt.apply(rp);
                    rp.uuid = rs[j].id;
                    rp.cells = cb;
                    buf+=  rt.apply(rp);
                }
                return [lbuf, buf];
            } :
            function (cs, rs, ds, startRow, colCount, stripe) {
            
                var ts = this.templates, ct = ts.cell, rt = ts.row;
                // buffers
                var buf = [], lbuf = [], cb, lcb, c, p = {}, rp = {}, r;
                for (var j = 0, len = rs.length; j < len; j++) {
                    r = rs[j], cb = [], lcb = [], rowIndex = (j+startRow);
                    for (var i = 0; i < colCount; i++) {
	                        c = cs[i];
	                     	var attrs;
	                        if (r.cells[c.name]) {
	                        	p.uuid =  r.cells[c.name].id;
	                        	attrs = zkExtGrid._splitAttrs(r.cells[c.name]);	                      
		                        p.cls = attrs.cls;
		                        p.attrs = attrs.attrs + " " + attrs.style;
		                        p.cellId = "x-grid-cell-" + rowIndex + "-" + i;
		                        p.id = c.id;
	                        	p.uuid = $uuid(p.uuid);
	                        	if ($childContent(p.uuid)) {
		                        	r.cells[c.name].innerHTML = $childContent(p.uuid).innerHTML;
		                        	r.data[c.name] = $childContent(p.uuid).innerHTML;
		                        }
		                        p.css = p.attr = "";
		                        p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
		                        if (p.value == undefined || p.value === "") p.value = "&#160;";
		                        if (r.dirty && typeof r.modified[c.name] !== 'undefined') {
		                            p.css += p.css ? ' x-grid-dirty-cell' : 'x-grid-dirty-cell';
		                        }
		                        var markup = ct.apply(p);
		                        if (!c.locked) {
		                            cb[cb.length] = markup;
		                        }else{
		                            lcb[lcb.length] = markup;
		                        }
							}
                    }
                    var alt = [];
                    if (stripe && ((rowIndex+1) % 2 == 0)) {
                        alt[0] = "x-grid-row-alt";
                    }
                    if (r.dirty) {
                        alt[1] = " x-grid-dirty-row";
                    }
                    var attrs = zkExtGrid._splitAttrs(r.node); 
                    rp.attrs = attrs.attrs + " " + attrs.style;
                    rp.cls = attrs.cls;
                    rp.cells = lcb;
                    if (this.getRowClass) {
                        alt[2] = this.getRowClass(r, rowIndex);
                    }
                    rp.alt = alt.join(" ");
                    rp.cells = lcb.join("");
                    rp.uuid = rs[j].id+"!locked";
                    lbuf[lbuf.length] = rt.apply(rp);
                    rp.cells = cb.join("");
                    rp.uuid = rs[j].id;
                    buf[buf.length] =  rt.apply(rp);
                }
                return [lbuf.join(""), buf.join("")];
            },

    renderBody : function () {
        var markup = this.renderRows();
        var bt = this.templates.body;
        var lbt = this.templates.lockedBody;
        return [lbt.apply({rows: markup[0]}), bt.apply({rows: markup[1]})];
    },

    render : function () {

        var cm = this.cm;
        var colCount = cm.getColumnCount();

        if (this.grid.monitorWindowResize === true) {
            Ext.EventManager.onWindowResize(this.onWindowResize, this, true);
        }
        var header = this.renderHeaders();
        var body = this.templates.body.apply({rows:""});
        var lockedBody = this.templates.lockedBody.apply({rows:""});
        var html = this.templates.master.apply({
            lockedBody: lockedBody,
            body: body,
            lockedHeader: header[0],
            header: header[1]
        });

        this.updateColumns();

        this.grid.container.dom.innerHTML = html;

        this.initElements();

        this.scroller.on("scroll", this.handleScroll, this);
        this.lockedBody.on("mousewheel", this.handleWheel, this);
        this.mainBody.on("mousewheel", this.handleWheel, this);

        this.mainHd.on("mouseover", this.handleHdOver, this);
        this.mainHd.on("mouseout", this.handleHdOut, this);
        this.mainHd.on("dblclick", this.handleSplitDblClick, this,
                {delegate: "."+this.splitClass});

        this.lockedHd.on("mouseover", this.handleHdOver, this);
        this.lockedHd.on("mouseout", this.handleHdOut, this);
        this.lockedHd.on("dblclick", this.handleSplitDblClick, this,
                {delegate: "."+this.splitClass});

        if (this.grid.enableColumnResize !== false && Ext.grid.SplitDragZone) {
            new Ext.grid.SplitDragZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
        }

        this.updateSplitters();

        if (this.grid.enableColumnMove && Ext.grid.HeaderDragZone) {
            new Ext.grid.HeaderDragZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
            new Ext.grid.HeaderDropZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
        }

        if (this.grid.enableCtxMenu !== false && Ext.menu.Menu) {
            this.hmenu = new Ext.menu.Menu({id: this.grid.id + "-hctx"});
            this.hmenu.add(
                {id:"asc", text: this.sortAscText, cls: "xg-hmenu-sort-asc"},
                {id:"desc", text: this.sortDescText, cls: "xg-hmenu-sort-desc"}
            );
            if (this.grid.enableColLock !== false) {
                this.hmenu.add('-',
                    {id:"lock", text: this.lockText, cls: "xg-hmenu-lock"},
                    {id:"unlock", text: this.unlockText, cls: "xg-hmenu-unlock"}
                );
            }
            if (this.grid.enableColumnHide !== false) {

                this.colMenu = new Ext.menu.Menu({id:this.grid.id + "-hcols-menu"});
                this.colMenu.on("beforeshow", this.beforeColMenuShow, this);
                this.colMenu.on("itemclick", this.handleHdMenuClick, this);

                this.hmenu.add('-',
                    {id:"columns", text: this.columnsText, menu: this.colMenu}
                );
            }
            this.hmenu.on("itemclick", this.handleHdMenuClick, this);

            this.grid.on("headercontextmenu", this.handleHdCtx, this);
        }

        if ((this.grid.enableDragDrop || this.grid.enableDrag) && Ext.grid.GridDragZone) {
            this.dd = new Ext.grid.GridDragZone(this.grid, {
                ddGroup : this.grid.ddGroup || 'ROWDD',   
				handleMouseDown : function (e) {
				    if (this.dragging) {
				        return;
				    }
				    if (Ext.QuickTips) {
				        Ext.QuickTips.disable();
				    }
				    var data = this.getDragData(e);
					if (!data) return false;
					var recode = data.grid.dataSource.getAt(data.rowIndex);
					if (getZKAttr(recode.node, "drag") != "true") {
						return false;
					}
				    if (data && this.onBeforeDrag(data, e) !== false) {
				        this.dragData = data;
				        this.proxy.stop();
				        Ext.dd.DragSource.superclass.handleMouseDown.apply(this, arguments);
				    } 
    			}
            });
        }
        for (var i = 0; i < colCount; i++) {
            if (cm.isHidden(i)) {
                this.hideColumn(i);
            }
            if (cm.config[i].align) {
                this.css.updateRule(this.colSelector + i, "textAlign", cm.config[i].align);
                this.css.updateRule(this.hdSelector + i, "textAlign", cm.config[i].align);
            }
        }

        this.updateHeaderSortState();

        this.beforeInitialResize();
        this.layout(true);

        // two part rendering gives faster view to the user
        this.renderPhase2.defer(1, this);
    },
	    handleHdMenuClick : function (item) {
        var index = this.hdCtxIndex;
        var cm = this.cm, ds = this.ds;
        switch (item.id) {
            case "asc":
                ds.sort(cm.getDataIndex(index), "ASC");
                break;
            case "desc":
                ds.sort(cm.getDataIndex(index), "DESC");
                break;
            case "lock":
                var lc = cm.getLockedCount();
                if (cm.getColumnCount(true) <= lc+1) {
                    this.onDenyColumnLock();
                    return;
                }
                if (lc != index) {
                    cm.setLocked(index, true);  // enable to trigger a columnlockchange event.
                    cm.moveColumn(index, lc);
                    this.grid.fireEvent("columnmove", index, lc);
                }else{
                    cm.setLocked(index, true);
                }
            break;
            case "unlock":
                var lc = cm.getLockedCount();
                if ((lc-1) != index) {
                    cm.setLocked(index, false, true);
                    cm.moveColumn(index, lc-1);
                    this.grid.fireEvent("columnmove", index, lc-1);
                }else{
                    cm.setLocked(index, false);
                }
            break;
            default:
                index = cm.getIndexById(item.id.substr(4));
                if (index != -1) {
                    if (item.checked && cm.getColumnCount(true) <= 1) {
                        this.onDenyColumnHide();
                        return false;
                    }
                    cm.setHidden(index, item.checked);
                }
        }
        return true;
    }
});
});