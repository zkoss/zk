_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {};
mesg.NOT_FOUND = "Bulunamadı : ";
mesg.UNSUPPORTED = "Henüz Desteklenmiyor : ";
mesg.FAILED_TO_SEND = "İstek sunucuya yollanamadı.";
mesg.FAILED_TO_RESPONSE = "Sunucu isteği yerine getiremedi.";
mesg.TRY_AGAIN="Try again?";
mesg.UNSUPPORTED_BROWSER = "Desteklenmeyen tarayıcı: ";
mesg.ILLEGAL_RESPONSE = "Sunucudan anlaşılmayan cevap döndü. Lütfen yeniden deneyiniz.\n";
mesg.FAILED_TO_PROCESS = "İşlem gerçekleştirilemedi ";
mesg.GOTO_ERROR_FIELD = "Yanlış alana gidildi";
mesg.PLEASE_WAIT = "Bekleyiniz...";

mesg.FILE_SIZE = "Dosya boyu: ";
mesg.KBYTES = "KB";

mesg.FAILED_TO_LOAD = "Yükleme başarısız: ";
mesg.FAILED_TO_LOAD_DETAIL = "Yoğunluktan kaynaklı problem oluşştu.Lütfen yeniden deneyiniz.";
mesg.CAUSE = "Neden: ";

zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['Pzt','Sal','Çar','Per','Cum','Cmt','Paz'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['Pazartesi','Salı','Çarşamba','Perşembe','Cuma','Cumartesi','Pazar'];
zk.SMON=['Oca','Şub','Mar','Nis','May','Haz','Tem','Ağu','Eyl','Eki','Kas','Ara'];
zk.S2MON=zk.SMON;
zk.FMON=['Ocak','Şubat','Mart','Nisan','Mayıs','Haziran','Temmuz','Ağustos','Eylül','Ekim','Kasım','Aralık'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {};
msgzul.UNKNOWN_TYPE = "Bilinmeyen bileşen(component) tipi: ";
msgzul.DATE_REQUIRED = "Tarih belirtilmeli. Format: ";
msgzul.OUT_OF_RANGE = "Aralık aşılıyor";
msgzul.NO_AUDIO_SUPPORT = "Tarayıcınız dinamik ses bileşenini desteklememektedir.";

zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Boş değer girilemez.\nBoşluk(space) girebilirsiniz',
INTEGER_REQUIRED:'{0}\'\'dan başka integer değer giremelisiniz.',
NUMBER_REQUIRED:'{0}\'\'dan başka sayısal değer giremelisiniz.',
DATE_REQUIRED:'{0} tarihinden başka tarih giremelisiniz.\nFormat: {1}.',
CANCEL:'İptal',
NO_POSITIVE_NEGATIVE_ZERO:'Sadece pozitif sayı girilebilir',
NO_POSITIVE_NEGATIVE:'Sadece sıfır girilebilir',
NO_POSITIVE_ZERO:'Sadece negatif sayı girilebilir',
NO_POSITIVE:'Sadece Pozitif olmayan sayı girilebilir',
NO_NEGATIVE_ZERO:'Sadece pozitif sayı girilebilir',
NO_NEGATIVE:'Sadece pozitif olmayan sayı girilebilir',
NO_ZERO:'Sadece sıfır olmayan sayı girilebilir',
NO_FUTURE_PAST_TODAY:'Değer girilemez',
NO_FUTURE_PAST:'Sadece sıfır girilebilir',
NO_FUTURE_TODAY:'Sadece geçmiş tarih girilebilir',
NO_FUTURE:'Gelecek tarih girilemez',
NO_PAST_TODAY:'Sadece gelecek tarih girilebilir',
NO_PAST:'Geçmişteki tarih girilemez',
NO_TODAY:'Only today is allowed',
FIRST:'İlk',
LAST:'Son',
PREV:'Önceki',
NEXT:'Sonraki'});
}finally{zPkg.end(_z);}}