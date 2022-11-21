/* flex.ts

Purpose:

Description:

History:
	Fri Jun  3 14:14:21 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export type FlexOrient = 'w' | 'h';
export interface FlexSize {
	width?: string | number;
	height?: string | number;
}

function _getTextSize(zkc: zk.JQZK, zkp: zk.JQZK, zkpOffset: zk.Offset): zk.Offset {
	let $zkc = zkc.jq,
		$prev = $zkc.prev(),
		pos: zk.Offset = [0, 0],
		coldVal: Record<string, string>,
		poldVal: Record<string, string>,
		zs: CSSStyleDeclaration | undefined, ps: CSSStyleDeclaration | undefined;

	if ($prev.length) {
		ps = $prev[0].style;
		// ZK-700 ignore prev if not displayed
		if (!zk($prev[0]).isRealVisible()) // B65-ZK-1925: Use isRealVisible() to determine it is visible or not
			return pos;
		else {
			zs = $zkc[0].style;
			// store the old value
			coldVal = {};
			poldVal = {};
			for (let margins = ['marginTop', 'marginBottom', 'marginLeft', 'marginRight'],
					 len = margins.length; len-- > 0;) {
				coldVal[margins[len]] = zs[margins[len]] as string;
				poldVal[margins[len]] = ps[margins[len]] as string;

				// clean margin
				zs[margins[len]] = ps[margins[len]] = '0px';
			}

			let offset = $prev.zk.revisedOffset();
			pos[0] = offset[0] + $prev.zk.offsetWidth();
			pos[1] = offset[1] + $prev.zk.offsetHeight();
		}
	} else {
		pos[0] = zkpOffset[0] + zkp.sumStyles('l', jq.paddings) + zkp.sumStyles('l', jq.borders);
		pos[1] = zkpOffset[1] + zkp.sumStyles('t', jq.paddings) + zkp.sumStyles('t', jq.borders);
	}

	// ZK-700
	const offset = zkc.revisedOffset();
	pos[0] = offset[0] - pos[0];
	pos[1] = offset[1] - pos[1];

	// revert the values
	zk.copy(zs, coldVal!);
	zk.copy(ps, poldVal!);

	return [Math.max(0, pos[0]), Math.max(0, pos[1])]; // ZK-2414 and 3298164
}

// check whether the two elements are the same baseline, if so, we need to
// sum them together.
function _isSameBaseline(ref: zk.Dimension & {_hgh?: number; _wdh?: number}, cur: zk.Dimension, vertical): boolean {
	let hgh: number, wdh: number;
	if (vertical) {
		hgh = ref._hgh ?? (ref._hgh = ref.top + ref.height);
		wdh = ref._wdh ?? (ref._wdh = ref.left + ref.width);
		return cur.top >= hgh || cur.left < wdh;
	} else {
		hgh = ref._hgh ?? (ref._hgh = ref.top + ref.height);
		wdh = ref._wdh ?? (ref._wdh = ref.left + ref.width);
		return cur.left >= wdh || cur.top < hgh;
	}
}

function _fixMinFlex(isVflex?): ((wgt: zk.Widget, wgtn: HTMLElement, o: FlexOrient,
								  min?: number) => number) {
	let flexsz: string, sizePos: string, flex: string, offsetPos: string, marginPos: string, maxFlexPos: string, sumFlexPos: string,
		index: number, contentPos: string;
	if (isVflex) {
		flexsz = '_vflexsz';
		sizePos = 'height';
		flex = '_vflex';
		offsetPos = 'offsetHeight';
		marginPos = 'marginHeight';
		maxFlexPos = '_maxFlexHeight';
		sumFlexPos = '_sumFlexHeight';
		index = 1;
		contentPos = 'getContentEdgeHeight_';
	} else {
		flexsz = '_hflexsz';
		sizePos = 'width';
		flex = '_hflex';
		offsetPos = 'offsetWidth';
		marginPos = 'marginWidth';
		maxFlexPos = '_maxFlexWidth';
		sumFlexPos = '_sumFlexWidth';
		index = 0;
		contentPos = 'getContentEdgeWidth_';
	}
	return function (wgt: zk.Widget, wgtn: HTMLElement, o: FlexOrient, min?: number) {
		if (wgt[flexsz] === undefined) { //cached?
			let cwgt = wgt.firstChild, //bug #2928109
				n = wgtn,
				zkn = zk(n),
				max = 0;

			if (min != null)
				max = min;
			else {
				const map = {};
				map[sizePos] = 'auto';
				wgt.setFlexSize_(map, true);
				let totalsz = 0,
					vmax = 0;
				if (cwgt && cwgt.desktop) { //try child widgets, bug ZK-1575: should check if child widget is bind to desktop
					let first: zk.Widget | undefined = cwgt,
						refDim: zk.Dimension | undefined;


					// ZK-2248: ignore widget dimension in vflex/hflex calculation
					while (first && first.ignoreFlexSize_(o))
						first = first.nextSibling;

					for (; cwgt; cwgt = cwgt.nextSibling) { //bug 3132199: hflex="min" in hlayout
						if (!cwgt.ignoreFlexSize_(o)) {
							let c = cwgt.$n();
							if (c) { //node might not exist if rod on
								//Skip absolute or fixed DOM element size
								const cpos = c.style.position;
								if (cpos == 'absolute' || cpos == 'fixed')
									continue;

								let zkc = zk(c),
									sz = 0;
								if (cwgt[flex] == 'min') {
									if (zkc.isVisible()) {
										sz += cwgt[flexsz] === undefined ? zFlex.fixMinFlex(cwgt, c, o) : cwgt[flexsz];
									}
								} else {
									cwgt.beforeParentMinFlex_(o);
									sz += wgt.getChildMinSize_(o, cwgt) // fixed for B50-3157031.zul
										+ (zkc[marginPos] as CallableFunction)();
								}

								//bug #3006276: East/West bottom cut if East/West higher than Center.
								if (cwgt[maxFlexPos] && sz > vmax) //@See West/East/Center
									vmax = sz;
								else if (cwgt[sumFlexPos]) //@See North/South
									totalsz += sz;
								else if (!cwgt[maxFlexPos] && first != cwgt
										&& _isSameBaseline(refDim || (refDim = zk(first).dimension(true)), zkc.dimension(true), isVflex))
									max += sz;
								else if (sz > max)
									max = sz;
							}
						}
					}
				} else {
					let c = wgtn.firstChild;
					if (c) { //no child widget, try html element directly
						//feature 3000339: The hflex of the cloumn will calculate by max width
						let isText = c.nodeType == 3,
							ignore = wgt.ignoreChildNodeOffset_(o),
							first = c,
							refDim: zk.Dimension | undefined;
						for (; c; c = c.nextSibling) {
							//Skip absolute or fixed DOM element size
							isText = c.nodeType == 3; // Bug ZK-2275, we need to update c.nodeType
							if (!isText) {
								const cpos = (c as HTMLElement).style.position;
								if (cpos == 'absolute' || cpos == 'fixed')
									continue;
							}

							let zkc = zk(c),
								sz = 0;
							if (ignore) {
								for (let el = c.firstChild; el; el = el.nextSibling) {
									let txt = el && el.nodeType == 3 ? el.nodeValue : undefined,
										zel: zk.JQZK;
									if (txt) {
										const dim = zkc.textSize(txt);
										if (dim[1] > sz)
											sz = dim[1];
									} else if ((zel = zk(el)).isVisible()) {
										const s = (zel[offsetPos] as (() => number))() + (zel[marginPos] as (() => number))();
										if (s > sz)
											sz = s;
									}
								}
							} else {
								if (isText)
									sz = c.nodeValue ? zkn.textSize(c.nodeValue)[index] : 0;
								else {
									sz = (zkc[offsetPos] as (() => number))() + (zkc[marginPos] as (() => number))();
								}
							}
							if (isText) {
								if (sz > max)
									max = sz;
							} else {
								const curDim = zkc.dimension(true);
								if (_isSameBaseline(refDim || (refDim = zk(first).dimension(true)),
										curDim, isVflex))
									max += sz;
								else if (sz > max)
									max = sz;
							}
						}
					} else //no kids at all, use self
						max = (zkn[offsetPos] as (() => number))();
				}
				if (vmax)
					totalsz += vmax;
				if (totalsz > max)
					max = totalsz;
			}

			//bug #3005284: (Chrome)Groupbox hflex="min" in borderlayout wrong sized
			//bug #3006707: The title of the groupbox shouldn't be strikethrough(Chrome)
			let margin = wgt.getMarginSize_(o);
			if (zk.webkit && margin < 0)
				margin = 0;


			// ZK-970: refixed for caption
			let map: Record<string, number> = {},
				wn = wgt.$n()!,
				hasChildren = zk.isLoaded('zul.wgt') && (wgt instanceof zul.wgt.Caption) && wgt.nChildren > 0,
				size = hasChildren ? (zk(wgt.$n('cave'))[offsetPos] as (() => number))() : max;

			map[sizePos] = size + (wgt[contentPos] as ((n) => number))(size);
			wgt.setFlexSize_(map, true);
			const szInfo = {height: wn.offsetHeight, width: wn.offsetWidth};
			if (szInfo[sizePos] >= 0)
				wgt[flexsz] = szInfo[sizePos] as number + margin;

			wgt.afterChildrenMinFlex_(o);

			// notify my parent when my size has been changed and my parent is not in min flex
			// Bug ZK-2117
			if (wgt.parent && wgt.parent[flex] != 'min')
				wgt.parent.afterChildMinFlexChanged_(wgt, o);
		}
		return wgt[flexsz] as number;
	};
}

const _fixMinVflex = _fixMinFlex(true),
	_fixMinHflex = _fixMinFlex();

function _zero(): 0 {
	return 0;
}

export namespace flex_global {
	export const zFlex = { //static methods
		/**
		 * beforeSize for read was created to prevent tremendous forced reflows
		 * as a result of doing both read and write in beforeSize.
		 * @since 9.5.1
		 */
		beforeSizeForRead(this: zk.Widget): void {
			const wgt = this,
				n = wgt.$n();
			// ZK-4154 prevent from forced reflow
			wgt._beforeSizeHasScroll = !!(n && (n.scrollTop || n.scrollLeft));
		},
		beforeSize(this: zk.Widget, ctl: unknown, opts: unknown, cleanup: boolean): void {
			let wgt = this, p;
			if (cleanup)
				wgt.clearCachedSize_();

			//bug#3042306: H/Vflex in IE6 can't shrink; others cause scrollbar space
			if (!zk.mounting && wgt.isRealVisible()) {
				if (wgt._hflex && wgt._hflex != 'min') {
					wgt.resetSize_('w');
					// Bug ZK-597
					delete wgt._flexFixed;
					if ((p = wgt.parent))
						(p as zk.Widget).afterResetChildSize_('w');
				}
				if (wgt._vflex && wgt._vflex != 'min') {
					wgt.resetSize_('h');
					// Bug ZK-597
					delete wgt._flexFixed;
					if ((p = wgt.parent))
						(p as zk.Widget).afterResetChildSize_('h');
				}
				delete wgt._beforeSizeHasScroll;
			}
		},

		beforeSizeClearCachedSize(this: zk.Widget, ctl: unknown, opts: unknown, cleanup: boolean): void {
			const wgt = this;
			if (cleanup)
				wgt.clearCachedSize_();
		},

		onSize(this: zk.Widget): void {
			zFlex.fixFlex(this);
		},
		fixFlex(wgt: zk.Widget): void {
			let hflexWgt;
			if (wgt._flexFixed || (!wgt._nvflex && !wgt._nhflex)) { //other vflex/hflex sibliing has done it!
				delete wgt._flexFixed;
				return;
			}
			//avoid firedown("onSize") calling in again
			if ((wgt._vflex === undefined || (wgt._vflexsz && wgt._vflex == 'min'))
				&& (wgt._hflex === undefined || (wgt._hflexsz && wgt._hflex == 'min')))
				return;

			if (wgt.ignoreFlexSize_('w') && wgt.ignoreFlexSize_('h'))
				return;

			if (!wgt.parent?.beforeChildrenFlex_(wgt)) { //don't do fixflex if return false
				return;
			}
			wgt._flexFixed = true;

			let pretxt = false, //pre node is a text node
				vflexs: zk.Widget[] = [],
				vflexsz = 0,
				hflexs: zk.Widget[] = [],
				hflexsz = 0,
				p = wgt.$n()?.parentNode as HTMLElement,
				zkp = zk(p),
				psz = wgt.getParentSize_(p),
				hgh = psz.height,
				wdh = psz.width,
				c = p.firstElementChild as HTMLElement,
				vflexsRe: zk.Widget[] = [],
				hasVScroll = zkp.hasVScroll(),
				hasHScroll = zkp.hasHScroll(),
				meshBodyHasVScroll = false,
				meshBodyHasHScroll = false,
				scrollbarWidth = jq.scrollbarWidth(),
				isMeshLoaded = zk.isLoaded('zul.mesh');

			// B86-ZK-4123
			if (isMeshLoaded && (wgt instanceof zul.mesh.HeaderWidget)) {
				const mesh = wgt.parent.parent;
				if (mesh && mesh._nativebar) {
					const body = mesh.ebody as HTMLElement;
					meshBodyHasVScroll = zk(body).hasVScroll();
					meshBodyHasHScroll = zk(body).hasHScroll();
				}
			}

			// Bug 3185686, B50-ZK-452
			if (hasVScroll || meshBodyHasVScroll) //with vertical scrollbar
				wdh -= scrollbarWidth;

			// B50-3312936.zul
			if (hasHScroll || meshBodyHasHScroll) //with horizontal scrollbar
				hgh -= scrollbarWidth;

			for (let zkpOffset: zk.Offset | undefined; c; c = c.nextElementSibling as HTMLElement) {
				//In ZK, we assume all text node is space (otherwise, it will be span enclosed)
				if (c.nodeType === 3) { //a text node
					pretxt = true;
					continue;
				}
				//Skip absolute or fixed DOM element size
				const cpos = c.style.position;
				if (cpos == 'absolute' || cpos == 'fixed')
					continue;

				const zkc = zk(c);
				if (zkc.isVisible(true)) {
					let offhgh = zkc.offsetHeight(),
						offwdh = offhgh > 0 ? zkc.offsetWidth() : 0,
						cwgt = zk.Widget.$(c, {exact: true});

					//ZK-2776: we can't and shouldn't set width to auxheader
					if (cwgt && isMeshLoaded && (cwgt instanceof zul.mesh.Auxheader))
						continue;

					//Bug ZK-1647: should consider header width
					//Bug Flex-138: skip if width exists
					if (offwdh == 0 && cwgt && isMeshLoaded
						&& (cwgt instanceof zul.mesh.HeaderWidget)) {
						// don't use jq().width() in chrome for performance issue
						const hdfaker = cwgt.$n('hdfaker');
						offwdh = hdfaker ? zk.parseInt(hdfaker.style.width) || hdfaker.offsetWidth : 0; //use faker width
					}

					//Bug ZK-1706: should consider all text node size
					if (pretxt) {
						if (!zkpOffset)
							zkpOffset = zkp.revisedOffset();
						const size = _getTextSize(zkc, zkp, zkpOffset);
						if (!cwgt || !cwgt.isExcludedHflex_()) // fixed ZK-1706 sideeffect
							wdh -= size[0];
						if (!cwgt || !cwgt.isExcludedVflex_()) // fixed ZK-1706 sideeffect for B60-ZK-917.zul
							hgh -= size[1];
					}
					//horizontal size
					if (cwgt && cwgt._nhflex && cwgt.isVisible()) { //Bug ZK-3883: to prevent invisible cwgt
						if (cwgt !== wgt)
							cwgt._flexFixed = true; //tell other hflex siblings I have done it.
						if (cwgt._hflex == 'min') {
							wdh -= zFlex.fixMinFlex(cwgt, c, 'w');
						} else {
							hflexs.push(cwgt);
							hflexsz += cwgt._nhflex;
						}
					} else if ((!cwgt &&
							// panelchild cannot include panel's bottombar.
							(!zk.isLoaded('zul.wnd') || !(wgt instanceof zul.wnd.Panelchildren)))
						|| (cwgt && !cwgt.isExcludedHflex_())) {
						wdh -= offwdh;
						wdh -= zkc.marginWidth();
					}

					//vertical size
					if (cwgt && cwgt._nvflex && cwgt.isVisible()) { //Bug ZK-3883: to prevent invisible cwgt
						if (cwgt !== wgt)
							cwgt._flexFixed = true; //tell other vflex siblings I have done it.
						if (cwgt._vflex == 'min') {
							hgh -= zFlex.fixMinFlex(cwgt, c, 'h');
						} else {
							vflexs.push(cwgt);
							vflexsRe.push(cwgt);
							vflexsz += cwgt._nvflex;
						}
					} else if (!cwgt || !cwgt.isExcludedVflex_()) {
						hgh -= offhgh;
						hgh -= zkc.marginHeight();
					}

					pretxt = false;
				}
			}

			// ZK-3411: use local function for setting up height
			let setHghForVflexChild = function (vfxs: zk.Widget[], h: number, lsz: number): void {
					for (let j = vfxs.length - 1; j > 0; --j) {
						const cwgt = vfxs.shift()!,
							vsz = cwgt.isExcludedVflex_() ? h : (cwgt._nvflex! * h / vflexsz) | 0; //cast to integer
						cwgt.setFlexSize_({height: vsz});
						cwgt._vflexsz = vsz;
						if (!cwgt.isExcludedVflex_())
							lsz -= vsz;
					}
					//last one with vflex
					if (vfxs.length) {
						const cwgt = vfxs.shift()!;
						cwgt.setFlexSize_({height: lsz});
						cwgt._vflexsz = lsz;
					}
				},
				lastsz = hgh = Math.max(hgh, 0);

			//setup the height for the vflex child
			//avoid floating number calculation error(TODO: shall distribute error evenly)
			setHghForVflexChild(vflexs, hgh, lastsz);

			//3042306: H/Vflex in IE6 can't shrink; others cause scrollbar space
			//vertical scrollbar might disappear after height was set
			const newpsz = wgt.getParentSize_(p);
			if (newpsz.width > psz.width) {//yes, the scrollbar gone!
				wdh += (newpsz.width - psz.width);
			} else if (!zkp.hasVScroll() && hasVScroll) { // ZK-3858: Window inside a <center> with autoscroll true doesn't resize itself correctly
				wdh += scrollbarWidth;
			}

			//setup the width for the hflex child
			//avoid floating number calculation error(TODO: shall distribute error evenly)
			lastsz = wdh = Math.max(wdh, 0);
			for (let j = hflexs.length - 1; j > 0; --j) {
				hflexWgt = hflexs.shift();
				if (!hflexWgt) break;
				const cwgt = hflexWgt as zk.Widget, //{n: node, f: hflex}
					hsz = cwgt.isExcludedHflex_() ? wdh : (cwgt._nhflex! * wdh / hflexsz) | 0; //cast to integer
				cwgt.setFlexSize_({width: hsz});
				cwgt._hflexsz = hsz;
				if (!cwgt.isExcludedHflex_())
					lastsz -= hsz;
			}
			//last one with hflex
			hflexWgt = hflexs.shift();
			if (hflexWgt) {
				const cwgt = hflexWgt as zk.Widget;
				cwgt.setFlexSize_({width: lastsz});
				cwgt._hflexsz = lastsz;
			}

			// ZK-3411: height need to be reset if the horizontal scrollbar disappeared
			const fixForParentHeight = newpsz.height > psz.height,
				fixForParentHScroll = !zkp.hasHScroll() && hasHScroll;
			if (fixForParentHeight || fixForParentHScroll) {   //horizontal scrollbar disappeared
				if (fixForParentHeight) {
					hgh += (newpsz.height - psz.height);
				} else if (fixForParentHScroll) { // ZK-3858: Window inside a <center> with autoscroll true doesn't resize itself correctly
					hgh += scrollbarWidth;
				}
				lastsz = hgh = Math.max(hgh, 0);
				setHghForVflexChild(vflexsRe, hgh, lastsz);
			}

			//notify parent widget that all of its children with hflex/vflex is done.
			wgt.parent.afterChildrenFlex_(wgt);
			wgt._flexFixed = false;
		},
		onFitSize(this: zk.Widget): void {
			const wgt = this,
				c = wgt.$n();
			if (c && zk(c).isVisible()) {
				// Bug ZK-3014: offsetWidth will be available only when wgt is real visible
				if (wgt._hflex == 'min' && wgt._hflexsz === undefined && wgt.isRealVisible() && !wgt.ignoreFlexSize_('w'))
					zFlex.fixMinFlex(wgt, c, 'w');
				// Bug ZK-3014: offsetHeight will be available only when wgt is real visible
				if (wgt._vflex == 'min' && wgt._vflexsz === undefined && wgt.isRealVisible() && !wgt.ignoreFlexSize_('h'))
					zFlex.fixMinFlex(wgt, c, 'h');
			}
		},
		fixMinFlex(wgt: zk.Widget, wgtn: HTMLElement, o: FlexOrient): number {
			//find the max size of all children
			return (o == 'h' ? _fixMinVflex : o == 'w' ? _fixMinHflex : _zero)(wgt, wgtn, o, wgt.beforeMinFlex_(o));
		},
		applyCSSFlex(this: zk.Widget): void {
			const wgt = this;
			if (!wgt._nvflex && !wgt._nhflex)
				return;

			let cssFlexAppliedInfo = wgt._cssFlexApplied,
				pwgt = wgt.parent;
			if (!pwgt) return;
			let minFlexInfoList: zk.MinFlexInfo[] | undefined = [];
			const fContainer = (pwgt instanceof zk.Page) ? pwgt.$n() : pwgt.getFlexContainer_();

			if ((cssFlexAppliedInfo && cssFlexAppliedInfo.flexApplied)) { //other vflex/hflex sibling has done it!
				minFlexInfoList = cssFlexAppliedInfo.minFlexInfoList;
				if (minFlexInfoList) { //still need to call fixMinFlex
					for (let i = 0, l = minFlexInfoList.length; i < l; i++) {
						const info = minFlexInfoList[i];
						zFlex.fixMinFlex(info.wgt, info.wgtn, info.orient);
					}
					pwgt.afterChildrenFlex_(wgt);
				}
				// Fix ZK-5153
				const fcWgt: zk.Widget & {_cssflexContainer?: boolean} | undefined = zk.$(fContainer),
					fcJQ = jq(fContainer);
				if (fcWgt && fcWgt._cssflexContainer && !fcJQ.hasClass('z-flex') /* fix for B95-ZK-4764.zul */) {
					fcJQ.addClass('z-flex').addClass(zFlex.getFlexInfo(wgt).isFlexRow ? 'z-flex-row' : 'z-flex-column');
				}
				return;
			}
			if (fContainer == null) { //using old flex
				wgt._cssflex = false;
				return;
			}

			const flexInfo = zFlex.getFlexInfo(wgt);

			// reassigned after getFlexInfo
			cssFlexAppliedInfo = wgt._cssFlexApplied!;

			let isRow = flexInfo.isFlexRow,
				fccs: HTMLElement[] = flexInfo.flexContainerChildren,
				cwgts = flexInfo.childrenWidgets,
				isAllMin = true,
				cwgtsz: Record<string, string>[] = [],
				flexItemClass = 'z-flex-item';

			for (let i = 0, length = fccs.length; i < length; i++) {
				let fcc = fccs[i],
					jqFcc = jq(fcc),
					cwgt = cwgts[i],
					c = cwgt.$n()!,
					flexs = [cwgt._nvflex, cwgt._nhflex],
					sz: Record<string, string> = {},
					dim = isRow ? 'width' : 'height',
					flex = flexs[isRow ? 1 : 0];

				if (flex && flex > 0) {
					isAllMin = false;
					jqFcc.addClass(flexItemClass);
					if (flex != 1 || fcc.style.flexGrow)
						fcc.style.flexGrow = flex as unknown as string; //update flex
					if (fcc != c && !c.style[dim])
						c.style[dim] = '100%';
					else
						sz[dim] = 'auto';
				} else if (flex && flex < 0) {//min
					const orient = dim.substring(0, 1) as FlexOrient;
					zFlex.fixMinFlex(cwgt, c, orient);
					minFlexInfoList.push({wgt: cwgt, wgtn: c, orient: orient});
				}

				//check else flex
				flex = flexs[isRow ? 0 : 1];
				dim = isRow ? 'height' : 'width';
				if (flex && flex > 0) {
					const marginSize = isRow ? zk(jqFcc).marginHeight() : zk(jqFcc).marginWidth(); //handle margin issue
					fcc.style[dim] = marginSize > 0 ? 'calc(100% - ' + jq.px0(marginSize) + ')' : '100%';
					if (fcc != c)
						c.style[dim] = '100%';
					else
						sz[dim] = 'auto';
				} else if (flex && flex < 0) {//min
					const orient = dim.substring(0, 1) as FlexOrient;
					zFlex.fixMinFlex(cwgt, c, orient);
					minFlexInfoList.push({wgt: cwgt, wgtn: c, orient: orient});
				}
				cwgtsz.push(sz);
			}

			if (minFlexInfoList.length > 0)
				cssFlexAppliedInfo.minFlexInfoList = minFlexInfoList;

			const fcWgt: zk.Widget & {_cssflexContainer?: boolean} | undefined = zk.$(fContainer);
			if (!isAllMin) {
				if (fcWgt) {
					fcWgt._cssflexContainer = true;
				}
				jq(fContainer).addClass('z-flex').addClass(isRow ? 'z-flex-row' : 'z-flex-column');
			} else {
				if (fcWgt) {
					delete fcWgt._cssflexContainer;
				}
			}

			for (let i = 0, length = cwgtsz.length; i < length; i++) {
				const szInfo = cwgtsz[i];
				if (Object.keys(szInfo).length > 0) //es5
					cwgts[i].setFlexSize_(szInfo);
			}

			pwgt.afterChildrenFlex_(wgt);
		},
		clearCSSFlex(wgt: zk.Widget, o: FlexOrient, clearAllSiblings?: boolean): void {
			if (!wgt._cssFlexApplied) return;

			const pwgt = wgt.parent;
			if (!pwgt) return;
			let fContainer = pwgt instanceof zk.Page ? pwgt.$n() : pwgt.getFlexContainer_(),
				isHorizontal = o == 'h',
				flexInfo = zFlex.getFlexInfo(wgt),
				isRow = flexInfo.isFlexRow,
				fccs = flexInfo.flexContainerChildren,
				cwgts = flexInfo.childrenWidgets,
				noSibFlex = true,
				flexItemClass = 'z-flex-item';

			for (let i = 0, length = fccs.length; i < length; i++) {
				let fcc = fccs[i],
					jqFcc = jq(fcc),
					cwgt = cwgts[i],
					isTargetWgt = cwgt == wgt,
					c = cwgt.$n()!,
					dim = isRow ? 'width' : 'height';

				if ((clearAllSiblings || isTargetWgt) && (isHorizontal && isRow) || (!isHorizontal && !isRow)) {
					fcc.style.flexGrow = '';
					jqFcc.removeClass(flexItemClass);
					if (fcc != c && !c.style[dim])
						c.style[dim] = '';
					delete cwgt._cssFlexApplied;
				}

				//check else flex
				dim = isRow ? 'height' : 'width';
				if (clearAllSiblings || isTargetWgt) {
					if ((isHorizontal && !isRow) || (!isHorizontal && isRow)) {
						fcc.style[dim] = '';
						if (fcc != c)
							c.style[dim] = '';
						delete cwgt._cssFlexApplied;
					}
				} else
					noSibFlex = noSibFlex ? !jqFcc.hasClass(flexItemClass) : false;
			}

			if (clearAllSiblings || noSibFlex) {
				const fcWgt: zk.Widget & {_cssflexContainer?: boolean} | undefined = zk.$(fContainer);
				if (fcWgt) delete fcWgt._cssflexContainer;
				jq(fContainer).removeClass('z-flex').removeClass(isRow ? 'z-flex-row' : 'z-flex-column');
			}

			wgt.afterClearFlex_();
		},
		getFlexInfo(wgt: zk.Widget): { isFlexRow: boolean; flexContainerChildren: HTMLElement[]; childrenWidgets: zk.Widget[] } {
			let pwgt = wgt.parent,
				cwgt = pwgt!.firstChild,
				fContainer = pwgt!.getFlexContainer_()!,
				fcc = fContainer.firstElementChild as HTMLElement,
				flexD = pwgt!.getFlexDirection_(),
				isRow = 'row' == flexD,
				fccs: HTMLElement[] = [],
				cwgts: zk.Widget[] = [],
				checkColumn = flexD == null,
				toColumn = false;

			for (let c: HTMLElement | undefined; fcc && cwgt;) { // assume cwgt -> fcc
				c = cwgt.$n();
				if (cwgt.ignoreFlexSize_(isRow ? 'w' : 'h') || (c && !fContainer.contains(c))) { // skip ignored or moved dom (ex.caption)
					cwgt._cssFlexApplied = {flexApplied: true};
					cwgt = cwgt.nextSibling;
					continue;
				}
				if (c && fContainer.contains(c)) {
					if (fcc.contains(c)) {
						cwgt._cssFlexApplied = {flexApplied: true};
						fccs.push(fcc);
						cwgts.push(cwgt);
						if (checkColumn && jq(fcc).css('display') === 'block') { // no default direction, check first block
							toColumn = true;
							checkColumn = false;
						}
					} else {
						fcc = fcc.nextElementSibling as HTMLElement; //skip c not in fcc (ex. splitter)
						continue;
					}
				}
				fcc = fcc.nextElementSibling as HTMLElement;
				cwgt = cwgt.nextSibling;
			}

			if (flexD == null) {//should alter flex-direction
				isRow = !toColumn;
				return {
					isFlexRow: isRow,
					flexContainerChildren: fccs,
					childrenWidgets: cwgts
				};
			}
			return {
				isFlexRow: isRow,
				flexContainerChildren: fccs,
				childrenWidgets: cwgts
			};
		}
	};
}
zk.copy(window, flex_global);
const _xWidget = zk.augment(zk.Widget.prototype, {
	/** @internal */
	domClass_(no?: zk.DomClassOptions): string {
		let domClass = _xWidget.domClass_.call(this, no),
			n = this.$n() as HTMLElement | undefined;
		if (n) {
			const jqn = jq(n),
				flexClasses = ['z-flex', 'z-flex-row', 'z-flex-column', 'z-flex-item'];
			for (let i = 0, length = flexClasses.length; i < length; i++) {
				const flexClass = flexClasses[i];
				if (jqn.hasClass(flexClass)) {
					domClass += ' ' + flexClass;
				}
			}
		}
		return domClass;
	}
});