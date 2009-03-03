_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {};
mesg.NOT_FOUND = "Ni mogoče najti: ";
mesg.UNSUPPORTED = "Še ni podporto: ";
mesg.FAILED_TO_SEND = "Napaka pri pošiljanju zahteve strežniku.";
mesg.FAILED_TO_RESPONSE = "Strežnik je sporočil napako pri obdelavi vaše zahteve.";
mesg.TRY_AGAIN = "Try again?";
mesg.UNSUPPORTED_BROWSER = "Brskalnik ni prepoznan: ";
mesg.ILLEGAL_RESPONSE = "Strežnik je poslal neznan odgovor. Prosim osvežite stran .\n";
mesg.FAILED_TO_PROCESS = "Napaka pri obdelavi";
mesg.GOTO_ERROR_FIELD = "Pojdite na napačno polje";
mesg.PLEASE_WAIT = "Procesiram...";

mesg.FILE_SIZE = "Velikost datoteke: ";
mesg.KBYTES = "KB";

mesg.FAILED_TO_LOAD="Failed to load ";
mesg.FAILED_TO_LOAD_DETAIL="It may be caused by bad traffic. You could reload this page and try again.";
mesg.CAUSE="Cause: ";

mesg.LOADING = "Procesiram...";

zk.GROUPING=",";
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

msgzul = {};
msgzul.UNKNOWN_TYPE = "Neznan tip komponente: ";
msgzul.DATE_REQUIRED = "Vnesti je potrebno datum. Oblika: ";
msgzul.OUT_OF_RANGE = "Out of range";
msgzul.NO_AUDIO_SUPPORT = "Vaš brskalnik ne podpira dinamiène glasbe";

zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Prazno polje ni dovoljeno.\nTudi sami presledki niso dovoljeni',
INTEGER_REQUIRED:'Potrebna je številka, in ne {0}.',
NUMBER_REQUIRED:'Potrebna je številka, in ne {0}.',
DATE_REQUIRED:'You must specify a date, rather than {0}.\nFormat: {1}.',
CANCEL:'Prekliči',
NO_POSITIVE_NEGATIVE_ZERO:'Dovoljena so samo pozitivna števila',
NO_POSITIVE_NEGATIVE:'Dovoljena je samo ničla',
NO_POSITIVE_ZERO:'Dovoljena so samo negativna števila',
NO_POSITIVE:'Dovoljena so samo ne-pozitivna števila',
NO_NEGATIVE_ZERO:'Dovoljena so samo pozitivna števila',
NO_NEGATIVE:'Dovoljena so samo ne-negativna števila',
NO_ZERO:'Dovoljena so samo števila različna od nič',
NO_FUTURE_PAST_TODAY:'Dovoljena je samo prazna vrednost',
NO_FUTURE_PAST:'Dovoljena je samo ničla',
NO_FUTURE_TODAY:'Dovoljen je samo pretekli datum',
NO_FUTURE:'Dovoljen je datum, ki ni v prihodnosti',
NO_PAST_TODAY:'Dovoljen je datumv prihodnosti',
NO_PAST:'Dovoljen je datum, ki ni v preteklosti',
NO_TODAY:'Dovoljen je samo današnji datum',
FIRST:'Prvi',
LAST:'Zadnji',
PREV:'Prej',
NEXT:'Naslednji'});
}finally{zPkg.end(_z);}}