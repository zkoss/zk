/* zephyr.ts

	Purpose:

	Description:

	History:
		3:36 PM 2021/10/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
export default {};
declare module '@zk/widget' {
	interface Widget {
		getPath_(): number[];
	}
}
const _AT_ACTION = '@action',
	_PARAM_RICHLET_PATH = '_',
	_PARAM_ACTION_ARGS = '0',
	_PARAM_ACTION_METHOD = '0.1',
	_PARAM_TYPE = '1',
	_PARAM_ACTION_VARIABLE_TYPE = '2',
	_PARAM_PRIMITIVE_TYPE = '2', _PARAM_POJO_TYPE = '3',
	_PARAM_DATETIME_TYPE = '4', _PARAM_QUERY = '5',
	_PARAM_QUERY_FIELD = '6',
	_auBfSend = zAu.beforeSend;

interface ActionParam {
	[_PARAM_TYPE]: typeof _PARAM_ACTION_VARIABLE_TYPE | typeof _PARAM_PRIMITIVE_TYPE |
		typeof _PARAM_DATETIME_TYPE;
	[_PARAM_QUERY]: string;
	[_PARAM_QUERY_FIELD]: string;
}
interface ActionPojoParam {
	[_PARAM_TYPE]: typeof _PARAM_POJO_TYPE;
	[_PARAM_QUERY]: Record<string, string[] | {'': string []}>;
	[_PARAM_QUERY_FIELD]: string;
}
interface ActionCmd {
	[_PARAM_ACTION_ARGS]: (ActionParam | ActionPojoParam)[];
	[_PARAM_RICHLET_PATH]: string;
	[_PARAM_ACTION_METHOD]: string;
}
type ActionCmds = ActionCmd | (ActionParam | ActionPojoParam)[];

function resolveAction(target: zk.Widget, args: ActionCmds, req: zk.Event): string | undefined {
	if (!args) return undefined;
	let richletMethod, uri: string | undefined, resolvedArgs: unknown[] = [];
	if (!Array.isArray(args)) {
		const {
			[_PARAM_ACTION_ARGS]: values,
			[_PARAM_RICHLET_PATH]: path,
			[_PARAM_ACTION_METHOD]: method,
		} = args;
		args = values;
		uri = path;
		richletMethod = method;
	}
	for (let arg of args) {
		let {
			[_PARAM_TYPE]: paramType,
			...others
		} = arg;
		switch (paramType) {
			case _PARAM_ACTION_VARIABLE_TYPE:
			case _PARAM_PRIMITIVE_TYPE:
			case _PARAM_DATETIME_TYPE: {
				let {
					[_PARAM_QUERY]: query,
					[_PARAM_QUERY_FIELD]: field
				} = others as ActionParam;
				if (query.startsWith('.')) {
					if (query != '.') {
						field = query.substring(1) + '.' + field;
					}
					resolvedArgs.push(target.get(field || 'value'));
				} else {
					resolvedArgs.push(zk.$('$' + query)?.get(field || 'value'));
				}
				break;
			}
			case _PARAM_POJO_TYPE: {
				let {
						[_PARAM_QUERY]: queries
					} = others as ActionPojoParam,
					resolvedPojo: Record<string, unknown> = {};
				for (let q in queries) {
					let fields = queries[q];
					if (fields instanceof Array) {
						let fieldName = q; // query as the field
						if (fieldName.startsWith('.')) {
							if (fieldName != '.') {
								fields[0] = fieldName.substring(1) + '.' + fields[0];
							}
							resolvedPojo[fields[1]] = target.get(fields[0] || 'value');
						} else {
							resolvedPojo[fieldName] = zk.$('$' + q)?.get(fields[0] || 'value');
						}
					} else {
						let queryTarget = q.startsWith('.') ? target : zk.$('$' + q);
						if (queryTarget) {
							// nested fields
							fields = fields[''];
							for (let field of fields) {
								// if no specifying filed name, the filed is the name
								let fieldName = field[1] || field[0];
								resolvedPojo[fieldName] = queryTarget.get(field[0] || 'value');
							}
						}
					}
				}
				resolvedArgs.push(resolvedPojo);
				break;
			}
			default:
				resolvedArgs.push(0);
		}
	}
	if (typeof req.data == 'object') {
		req.data!['@args'] = resolvedArgs;
	} else {
		req.data = {
			'': req.data,
			'@args': resolvedArgs
		};
	}
	if (richletMethod) {
		(req.data as object)['@mtd'] = richletMethod as never;
	}
	return uri;
}

function getActions(target: zk.Widget): Record<string, ActionCmds[]>[] | undefined {
	return target && target[_AT_ACTION]
		? (Array.isArray(target[_AT_ACTION]) ? target[_AT_ACTION]
			: [target[_AT_ACTION]]) : undefined;
}
function getRootComponents(dt: zk.Desktop): zk.Widget[] {
	let wgts = [] as zk.Widget[];
	for (let page = dt.firstChild; page; page = page.nextSibling) {
		for (let w = page.firstChild; w; w = w.nextSibling) {
			wgts.push(w);
		}
	}
	return wgts;
}

zAu.beforeSend = function (uri: string, req: zk.Event & {_handled?: boolean; _uri?: string}, dt: zk.Desktop | undefined) {
	let target = req.target,
		actions = !req._handled && target ? getActions(target) : undefined;
	try {
		if (!target && dt) { // event for desktop scope.
			let wgts = getRootComponents(dt), oriUri = uri, firstOneOnly = true;
			for (const wgt of wgts) {
				const acts = getActions(wgt);
				if (!acts) continue;
				const auuri = processMultipleActions(oriUri, req, dt, wgt, acts);
				if (firstOneOnly) {
					firstOneOnly = false;
					if (auuri) {
						uri = auuri; // only assign to the first AuRequest.
					}
					req.target = wgt;
				}
			}
		} else if (actions) {
			if (!dt) {
				//original dt is decided by aureq.target.desktop, so start by it's parent.
				let wgt: zk.Widget | undefined = req.target.parent;
				while (!wgt?.desktop) {
					wgt = wgt?.parent;
				}
				dt = wgt?.desktop;
			}

			uri = processMultipleActions(uri, req, dt, target, actions);
		} else if (req._uri) {
			uri = req._uri; // if the original uri exists, reset the uri.
		}

		// ignore $rms$ event
		if (req.name != '$rms$') {
			convertUuidToIndexIfPossible(req as never);
		}
	} catch (e) {
		zk.error((e as Error).message ?? e, true);
	}
	return _auBfSend(uri, req, dt);
};

function processMultipleActions(uri: string, req: zk.Event, dt: zk.Desktop | undefined, target: zk.Widget, actions: Record<string, ActionCmds[]>[]): string {
	let oriUri = uri, firstOneOnly = true;
	for (let action of actions) {
		let args = action[req.name];
		if (args) { // may have multiple event handlers on the same event name as server.
			for (let arg of args) {
				let newReq = req;
				if (!firstOneOnly) { // put other events to send later.
					newReq = new zk.Event(req.target, req.name, zk.copy({}, req.data), zk.copy({}, req.opts), req.domEvent);
					newReq['_handled'] = true;
					zAu.addAuRequest(dt!, newReq);
				} else {
					newReq['_handled'] = true;
				}
				const auuri = resolveAction(target, arg, newReq);
				if (firstOneOnly) {
					firstOneOnly = false;
					if (auuri) {
						uri = auuri; // only assign to the first AuRequest.
					}
				}
				newReq['_uri'] = auuri ?? oriUri; // store the original uri
			}
		}
	}
	return uri;
}

function isSelectableModule(): boolean {
	return zk.isLoaded('zul.sel');
}

function getIndex(wgt: zk.Widget & {_index?: number}): number {
	return wgt._index != null ? wgt._index : wgt.getChildIndex();
}

function convertUuidToIndexIfPossible(req: zk.Event & {data: object}): void {
	const target = req.target;
	if (target) {
		if (isSelectableModule()) {
			if (target instanceof zul.sel.Tree) {
				const {items, reference} = req.data as {items: zk.Widget[]; reference: zk.Widget};
				if (items && items.length) {
					req.data['itemsPath'] = items.map((wgt): number[] => wgt.getPath_()) as never;
				}
				if (reference) {
					req.data['referencePath'] = reference.getPath_();
				}
			} else if (target instanceof zul.sel.Treeitem) {
				req.data['referencePath'] = target.getPath_();
			}
		} else if (zk.isLoaded('zkmax.layout')) {
			if (target instanceof zkmax.layout.Organigram) {
				let selectItem = req.data['selectedItem'] as zk.Widget | undefined;
				if (selectItem && selectItem instanceof zkmax.layout.Orgitem)
					req.data['itemsPath'] = [selectItem.getPath_()];
			} else if (target instanceof zkmax.layout.Orgitem) {
				req.data['referencePath'] = target.getPath_();
			}
		} else if (zk.isLoaded('zkmax.nav')) {
			if (target instanceof zkmax.nav.Navbar) {
				let {items, reference} = req.data as {items: zk.Widget[]; reference: zk.Widget};
				if (items && items.length) {
					req.data['itemsPath'] = items.map((wgt) => wgt.getPath_());
				}
				if (reference) {
					req.data['referencePath'] = [0]; // always the navbar itself.
				}
			}
		}
	}
	if (req.data) {
		const {items, reference, dragged /*for onDrop and onPortalDrop */,
			from, to /*for onPortalDrop*/
		} = req.data as { items: zk.Widget[]; reference: zk.Widget; dragged?: zk.Widget; from?: zk.Widget; to?: zk.Widget };
		if (items && items.length) {
			req.data['itemsIndex'] = items.map((wgt) => getIndex(wgt));
		}
		if (reference) {
			req.data['referenceIndex'] = getIndex(reference);
		}
		if (dragged instanceof zk.Widget) {
			req.data['draggedIndex'] = getIndex(dragged);
		}
		if (from instanceof zk.Widget) {
			req.data['fromIndex'] = getIndex(from);
		}
		if (to instanceof zk.Widget) {
			req.data['toIndex'] = getIndex(to);
		}
	}
}

// Support @ActionVariable for expressions, such as "a.b.c" or "a|b" (a or b, if any)
const oldZKGetter = zk.get;
zk.get = function (oo: zk.Object, names: string): unknown {
	const nameArray = names.split('.');
	let o = oo as zk.Object | unknown;
	for (let name of nameArray) {
		if (o === undefined) return undefined;
		// unwrap "()" if any
		if (name.length > 2 && name.charAt(0) == '(' && name.charAt(name.length - 1) == ')') {
			name = name.substring(1, name.length - 1);
		}
		const nms = name.split('|');
		let result: undefined | unknown;
		for (let nm of nms) {
			result = oldZKGetter.call(zk, o as never, nm);
			if (result !== undefined) {
				break;
			}
			// try $n() if possible
			if (o instanceof zk.Widget) {
				let elem = o.$n<HTMLElement>();
				result = elem ? elem[nm] : undefined;
				if (result !== undefined) {
					break;
				}
			}
		}
		o = result;
	}
	return o;
};
