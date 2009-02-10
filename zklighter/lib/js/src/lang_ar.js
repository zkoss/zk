_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
﻿/* mesg.js "charset=utf-8"
 * created by Ayman Elgharabawy (aaaeg@hotmail.com) 28 Mars2008
 * Copyright (C) 2005,2008 Potix Corporation
 */
mesg={};
mesg.NOT_FOUND="لا يوجد";
mesg.UNSUPPORTED="لا يدعم ";
mesg.FAILED_TO_SEND="غير قادر على الأرسال";
mesg.FAILED_TO_RESPONSE="غير قادر على الأستجابة.";
mesg.TRY_AGAIN ="هل تحب ان تحاول مرة أخرى";
mesg.UNSUPPORTED_BROWSER="المتصفح غير مدعوم";
mesg.ILLEGAL_RESPONSE="أستجابة خاطئة.\n";
mesg.FAILED_TO_PROCESS="فشل فى القيام بالعملية";
mesg.EMPTY_NOT_ALLOWED="غير مسموح بفراغ";
mesg.GOTO_ERROR_FIELD="اذهب الى مكان الخطأ";
mesg.PLEASE_WAIT="من فضلك الأنتظار ...";

mesg.FILE_SIZE="حجم الملف : ";
mesg.KBYTES="كيلو بايت ";

mesg.FAILED_TO_LOAD="فشل فى التحميل  ";
mesg.FAILED_TO_LOAD_DETAIL="فشل فى تحميل تفاصيل البيانات.";
mesg.CAUSE="السبب: ";

zk.GROUPING=",";
zk.DECIMAL=".";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=6;
zk.SDOW=['س','ح','ن','ث','ر','خ','ج'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['السبت','الأحد','الاثنين','الثلاثاء','الأربعاء','الخميس','الجمعة'];
zk.SMON=['ينا','فبر','مار','أبر','ماي','يون','يول','أغس','سبت','أكت','نوف','ديس'];
zk.S2MON=zk.SMON;
zk.FMON=['يناير','فبراير','مارس','أبريل','مايو','يونيو','يوليو','أغسطس','سبتمبر','أكتوبر','نوفمبر','ديسمبر'];
zk.APM=['ص','م'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {};
msgzul.UNKNOWN_TYPE = "نوع غير معروف";
msgzul.DATE_REQUIRED = "يتطلب وجود تاريخ";
msgzul.NO_AUDIO_SUPPORT = "لا يوجد دعم للصوت";

zk.$default(msgzul, {
VALUE_NOT_MATCHED:'يجب أن تحدد قيمة من القائمة',
EMPTY_NOT_ALLOWED:'اغير مسموح بفراغ',
INTEGER_REQUIRED:'{0}. مطلوب رقم عددى صحيح',
NUMBER_REQUIRED:'{0}. مطلوب رقم عددى',
DATE_REQUIRED:'{0}. مطلوب تاريخ',
CANCEL:'ألغاء',
NO_POSITIVE_NEGATIVE_ZERO:'لا يوجد رقم صفر او موجب أو سالب',
NO_POSITIVE_NEGATIVE:'لا يوجد رقم موجب أو سالب',
NO_POSITIVE_ZERO:'لا يوجد رقم موجب أو صفر',
NO_POSITIVE:'لا يوجد رقم موجب',
NO_NEGATIVE_ZERO:'لا يوجد رقم سالب أو صفر',
NO_NEGATIVE:'لا يوجد رقم سالب',
NO_ZERO:'لا يوجد رقم صفر',
NO_FUTURE_PAST_TODAY:'غير مسموح بتاريخ ماضى أو اليوم أو مستقبل',
NO_FUTURE_PAST:'غير مسموح بتاريخ فى المستقبل أو الماضى',
NO_FUTURE_TODAY:'غير مسموح بتاريخ فى المستقبل أو الآن',
NO_FUTURE:'غير مسموح بتاريخ فى المستقبل',
NO_PAST_TODAY:'غير مسموح بتاريخ فى الماضى أو الآن',
NO_PAST:'غير مسموح بتاريخ فى الماضى',
NO_TODAY:'غير مسموح بتاريخ الآن',
FIRST:'الأول',
LAST:'الأخير',
PREV:'السابق',
NEXT:'التالى'});
}finally{zPkg.end(_z);}}