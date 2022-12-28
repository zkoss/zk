/* showShadowTree.js

	Purpose:
		
	Description:
		
	History:
		2:11 PM 2022/12/16, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
(function () {
	function Iterator(target) {
		this.hasNext = function () {
			return this.current ? this.current.nextSibling : target.firstChild;
		};
		this.next = function () {
			this.current = this.current ? this.current.nextSibling : target.firstChild;
			return this.current;
		};
		this.previous = function () {
			this.current = this.current.previousSibling;
			return this.current;
		};
	}
	function getDistributedChildren(se) {
		let host = se.getShadowHostIfAny();
		if (host) {
			let children = [];
			for (let w = se._firstInsertion; w; w = w.nextSibling) {
				children.push(w.toString());
				if (w == se._lastInsertion) {
					break;
				}
			}
			return children;
		} else {
			return [];
		}
	}
	function toJSONString(data) {
		return data ? data.toString() : '';
	}
	function log1(se, data) {
		let name = se.toString(),
			index = name.lastIndexOf('->');
		if (index > 0)
			name = name.substring(index + 2);
		data['name'] = name;
		data['allChildren'] = getDistributedChildren(se);
		data['prev'] = toJSONString(se.getPreviousInsertion());
		data['first'] = toJSONString(se.getFirstInsertion());
		data['last'] = toJSONString(se.getLastInsertion());
		data['next'] = toJSONString(se.getNextInsertion());
		let children = [];
		for (let sw = se.firstChild; sw; sw = sw = sw.nextSibling) {
			let child = {};
			log1(sw, child);
			children.push(child);
		}
		if (children.length)
			data['children'] = children;
	}

	function logTree(root, host) {
		let rootData = {};
		if (root) {
			log1(root, rootData);
		}
		window.DrawTree(rootData, host.id);
	}
	function fillComponentOnly(child) {
		return {
			name: child.toString(),
			real: true
		};
	}
	function fillShadowElement(se) {
		let name = se.toString(),
			index = name.lastIndexOf('->');
		if (index > 0)
			name = name.substring(index + 2);
		let data = {};
		data['name'] = name;
		data['allChildren'] = getDistributedChildren(se);
		data['prev'] = toJSONString(se.getPreviousInsertion());
		data['first'] = toJSONString(se.getFirstInsertion());
		data['last'] = toJSONString(se.getLastInsertion());
		data['next'] = toJSONString(se.getNextInsertion());
		let childrenArray = [];
		data['children'] = childrenArray;
		for (let sw = se.firstChild; sw; sw = sw.nextSibling) {
			childrenArray.push(fillShadowElement(sw));
		}
		return data;
	}
	function fillComponent(child) {
		if (child != null) {
			let shadowRoots = child.getShadowRoots();
			if (shadowRoots && shadowRoots.length) {
				return fillShadowHost(child);
			}
		}
		return {
			name: child.toString(),
			real: true
		};
	}
	function fillShadowElement2(se, current, cit) {
		let name = se.toString(),
			index = name.lastIndexOf('->');
		if (index > 0)
			name = name.substring(index + 2);
		let data = {};

		data['name'] = name;
		data['allChildren'] = getDistributedChildren(se);
		data['prev'] = toJSONString(se.getPreviousInsertion());
		data['first'] = toJSONString(se.getFirstInsertion());
		data['last'] = toJSONString(se.getLastInsertion());
		data['next'] = toJSONString(se.getNextInsertion());
		let childrenArray = [];
		data['children'] = childrenArray;
		let next = current,
			sit = new Iterator(se);
		if (sit.hasNext()) {
			let seNext = sit.next(), first = true;
			do {
				if (!first) { // avoid first time to invoke
					next = cit.next();
				}
				first = false;
				if (seNext != null) {
					switch (seNext.inRange(seNext, next)) {
						case 'NEXT':
						case 'AFTER_NEXT':
							childrenArray.push(fillShadowElement(seNext));
							cit.previous(); // go back
							if (sit.hasNext()) {
								seNext = sit.next();
							} else {
								seNext = undefined; // not in the range
							}
							break;
						case 'IN_RANGE':
						case 'FIRST':
						case 'LAST':
							childrenArray.push(fillShadowElement2(seNext, next, cit));
							if (sit.hasNext()) {
								seNext = sit.next();
								if (!cit.hasNext()) // draw the rest
									sit.previous();
							} else {
								seNext = undefined; // not in the range
							}
							break;
						case 'UNKNOWN':
							childrenArray.push(fillShadowElement(seNext));
							if (sit.hasNext())
								seNext = sit.next();
							else {
								seNext = undefined; // not in the range
							}
							cit.previous(); // go back
							break;
						default:
							childrenArray.push(fillComponent(next));

							// draw the reset shadows
							if (!cit.hasNext()) {
								sit.previous();
								while (sit.hasNext()) {
									childrenArray.push(fillShadowElement(sit.next()));
								}
							}
					}
				} else {
					switch (se.inRange(se, next)) { // in parent range
						case 'IN_RANGE':
						case 'FIRST':
						case 'LAST':
							childrenArray.push(fillComponent(next));
							break;
						default:
							cit.previous(); // go back

							return data; // out of parent range;
					}
				}
			} while (cit.hasNext());

			// draw the reset shadows
			while (sit.hasNext()) {
				childrenArray.push(fillShadowElement(sit.next()));
			}
		} else {
			cit.previous();
			do {
				next = cit.next();
				switch (se.inRange(se, next)) { // in parent range
					case 'IN_RANGE':
					case 'FIRST':
					case 'LAST':
						childrenArray.push(fillComponent(next));
						break;
					default:
						cit.previous(); // go back

						return data; // out of parent range;
				}
			} while (cit.hasNext());
		}
		return data;
	}
	function fillShadowHost(host) {
		let rootData = fillComponentOnly(host),
			childrenArray = [];
		rootData['children'] = childrenArray;
		let seChildren = host.getShadowRoots(),
			sit = 0,
			seNext = sit < seChildren.length ? seChildren[sit++] : null;
		if (!host.firstChild) {
			if (seNext != null)
				childrenArray.push(fillShadowElement(seNext));
		} else {
			for (let cit = new Iterator(host); cit.hasNext();) {
				let next = cit.next();
				if (seNext != null) {
					switch (seNext.inRange(seNext, next)) {
						case 'AFTER_NEXT':
						case 'NEXT':
							cit.previous(); // go back
							childrenArray.push(fillShadowElement(seNext));
							if (sit < seChildren.length)
								seNext = seChildren[sit++];
							else
								seNext = undefined;
							break;
						case 'FIRST':
						case 'IN_RANGE':
						case 'LAST':
							childrenArray.push(fillShadowElement2(seNext, next, cit));
							if (sit < seChildren.length)
								seNext = seChildren[sit++];
							else
								seNext = undefined;
							break;
						case 'UNKNOWN':
							childrenArray.push(fillShadowElement(seNext));
							if (sit < seChildren.length)
								seNext = seChildren[sit++];
							else {
								seNext = undefined; // not in the range
							}
							cit.previous(); // go back
							break;
						default:
							childrenArray.push(fillComponent(next));
					}
				} else {
					childrenArray.push(fillComponent(next));
				}
			}
			if (seNext != null) {
				childrenArray.push(fillShadowElement(seNext));
			}
		}
		return rootData;
	}

	function logWholeTree(host) {
		window.DrawTree(fillShadowHost(host), host.id);
	}
window.showShadowTree = function (host, wholeTree) {
	if (wholeTree) {
		logWholeTree(host);
	} else {
		let shadowRoots = host.getShadowRoots();
		if (shadowRoots && shadowRoots.length) {
			logTree(shadowRoots[0], host);
		} else {
			logTree(undefined, host);
		}
	}
};
})();