_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {
NOT_FOUND: "找不到：",
UNSUPPORTED: "尚未支援：",
FAILED_TO_SEND: "無法傳送資料到伺服器。",
FAILED_TO_RESPONSE: "伺服器暫時無法處理你的請求。",
TRY_AGAIN: "再試一次?",
UNSUPPORTED_BROWSER: "尚未支援你使用的瀏覽器：",
ILLEGAL_RESPONSE: "無法辨識伺服器傳回的資料。請按重新載入，再試試。\n",
FAILED_TO_PROCESS: "無法處理：",
GOTO_ERROR_FIELD: "回錯誤欄",
PLEASE_WAIT: "處理中…",

FILE_SIZE: "檔案大小：",
KBYTES: "KB",

FAILED_TO_LOAD: "無法載入：",
FAILED_TO_LOAD_DETAIL: "可能是連線問題，請按重載再試一次。",
CAUSE: "原因："
};
;zk.GROUPING=",";
zk.DECIMAL=".";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=0;
zk.SDOW=['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
zk.S2DOW=['日','一','二','三','四','五','六'];
zk.FDOW=zk.SDOW;
zk.SMON=['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'];
zk.S2MON=['一','二','三','四','五','六','七','八','九','十','十一','十二'];
zk.FMON=zk.SMON;
zk.APM=['上午','下午'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {
UNKNOWN_TYPE: "未知元件類型：",
DATE_REQUIRED: "只能輸入日期。格式：",
OUT_OF_RANGE: "超出範圍",
NO_AUDIO_SUPPORT: "你的瀏覽器不支援動態音效"
};
;zk.$default(msgzul, {
VALUE_NOT_MATCHED:'你必需指定一個在下拉選單裡的值',
EMPTY_NOT_ALLOWED:'不能空白，也不能只含空白字元',
INTEGER_REQUIRED:'只能輸入整數，而不是 {0}',
NUMBER_REQUIRED:'只能輸入數字，而不是 {0}',
DATE_REQUIRED:'只能輸入日期：{0}。\n格式：{1}。',
CANCEL:'取消',
NO_POSITIVE_NEGATIVE_ZERO:'只能清為空白',
NO_POSITIVE_NEGATIVE:'只能為零',
NO_POSITIVE_ZERO:'只能為負數',
NO_POSITIVE:'只能為負數或零',
NO_NEGATIVE_ZERO:'只能為正數',
NO_NEGATIVE:'只能為正數或零',
NO_ZERO:'不能為零',
NO_FUTURE_PAST_TODAY:'只能清為空白',
NO_FUTURE_PAST:'只能為今天',
NO_FUTURE_TODAY:'只能為過去的日期',
NO_FUTURE:'只能為過去的日期或今天',
NO_PAST_TODAY:'只能為未來的日期或今天',
NO_PAST:'只能為未來的日期',
NO_TODAY:'不能為今天',
FIRST:'首頁',
LAST:'尾頁',
PREV:'上一頁',
NEXT:'下一頁'});
}finally{zPkg.end(_z);}}