_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {
NOT_FOUND: "找不到：",
UNSUPPORTED: "尚未支持：",
FAILED_TO_SEND: "无法传送资料到服务器。",
FAILED_TO_RESPONSE: "伺服器暂时无法处理你的请求。",
TRY_AGAIN: "再试一次?",
UNSUPPORTED_BROWSER: "尚未支持你使用的浏览器：",
ILLEGAL_RESPONSE: "无法辨识服务器传回的资料。请按重新载入，再试试。\n",
FAILED_TO_PROCESS: "无法处理：",
GOTO_ERROR_FIELD: "回错误栏",
PLEASE_WAIT: "处理中，请稍候…",

FILE_SIZE: "文件大小：",
KBYTES: "KB",

FAILED_TO_LOAD: "无法载入：",
FAILED_TO_LOAD_DETAIL: "可能是连线问题，请按重载再试一次。",
CAUSE: "原因："
};
;zk.GROUPING=",";
zk.DECIMAL=".";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=0;
zk.SDOW=['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
zk.SMON=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
zk.S2MON=zk.SMON;
zk.FMON=['January','February','March','April','May','June','July','August','September','October','November','December'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {
UNKNOWN_TYPE: "未知元件类型：",
DATE_REQUIRED: "只能输入日期。格式：",
OUT_OF_RANGE: "超出范围",
NO_AUDIO_SUPPORT: "你的浏览器不支持动态音效"
};
;zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Empty is not allowed.\nYou cannot specify nothing but spaces, either.',
INTEGER_REQUIRED:'You must specify an integer, rather than {0}.',
NUMBER_REQUIRED:'You must specify a number, rather than {0}.',
DATE_REQUIRED:'You must specify a date, rather than {0}.\nFormat: {1}.',
CANCEL:'Cancel',
NO_POSITIVE_NEGATIVE_ZERO:'Only positive number is allowed',
NO_POSITIVE_NEGATIVE:'Only zero is allowed',
NO_POSITIVE_ZERO:'Only negative number is allowed',
NO_POSITIVE:'Only negative number or zero is allowed',
NO_NEGATIVE_ZERO:'Only positive number is allowed',
NO_NEGATIVE:'Only positive number or zero is allowed',
NO_ZERO:'Zero number is not allowed',
NO_FUTURE_PAST_TODAY:'Only empty is allowed',
NO_FUTURE_PAST:'Only today is allowed',
NO_FUTURE_TODAY:'Only date in the past is allowed',
NO_FUTURE:'Only date in the past or today is allowed',
NO_PAST_TODAY:'Only date in the future is allowed',
NO_PAST:'Only date in the future or today is allowed',
NO_TODAY:'Today is not allowed',
FIRST:'First',
LAST:'Last',
PREV:'Prev',
NEXT:'Next'});
}finally{zPkg.end(_z);}}