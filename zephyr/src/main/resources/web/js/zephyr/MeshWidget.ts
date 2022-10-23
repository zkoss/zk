/* MeshWidget.ts

	Purpose:
		
	Description:
		
	History:
		2:49 PM 2021/12/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
export default {};
zk.afterLoad('zul.mesh', () => {
	zk.augment(zul.mesh.MeshWidget.prototype, {
		isZephyr(): boolean {
			return !!(this as unknown as {_zephyr: boolean})._zephyr;
		}
	});
});