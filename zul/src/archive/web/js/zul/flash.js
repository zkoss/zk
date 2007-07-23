zkFlash = {};

zkFlash.init = function (cmp) {
	
};

zkFlash.cleanup = function (cmp) {

};

zkFlash.setAttr = function (cmp, name, value) {
		
	switch(name)
	{
		case "z:height":
		{
			//$e(cmp.id+"!obj").height = value;
			$e(cmp.id).height = value;
			$e(cmp.id+"!emb").height = value;
			return true;
		}
		case "z:width":
		{
			//$e(cmp.id+"!obj").width = value;
			$e(cmp.id).width = value;
			$e(cmp.id+"!emb").width = value;
			return true;
		}
		case "z:play":
		{
			$e(cmp.id+"!emb").play = value;
			return true;
		}
		case "z:loop":
		{
			$e(cmp.id+"!emb").loop = value;
			return true;
		}
		case "z:wmode":
		{
			$e(cmp.id+"!emb").wmode = value;
			return true;
		}
		case "z:bgcolor":
		{
			$e(cmp.id+"!emb").bgcolor = value;
			return true;
		}
		
	}
    return false;

};

