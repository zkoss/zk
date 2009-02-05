_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {};
mesg.NOT_FOUND = "Non trovato: ";
mesg.UNSUPPORTED = "Non ancora supportato: ";
mesg.FAILED_TO_SEND = "Invio della richiesta al server fallito.";
mesg.FAILED_TO_RESPONSE = "Il server ha fallito nel elaborare la tua richiesta.";
mesg.TRY_AGAIN = "Riprovare?";
mesg.UNSUPPORTED_BROWSER = "Browser non supportato: ";
mesg.ILLEGAL_RESPONSE = "Risposta sconosciuta inviata dal server. Per favore ricarica la pagina e riprova.\n";
mesg.FAILED_TO_PROCESS = "Errore nell'elaborazione di ";
mesg.GOTO_ERROR_FIELD = "Arrivato al campo sbagliato";
mesg.PLEASE_WAIT = "Attendere...";

mesg.FILE_SIZE = "Dimensione file: ";
mesg.KBYTES = "KB";

mesg.FAILED_TO_LOAD="Failed to load ";
mesg.FAILED_TO_LOAD_DETAIL="It may be caused by bad traffic. You could reload this page and try again.";
mesg.CAUSE="Cause: ";

zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['lun','mar','mer','gio','ven','sab','dom'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['lunedì','martedì','mercoledì','giovedì','venerdì','sabato','domenica'];
zk.SMON=['gen','feb','mar','apr','mag','giu','lug','ago','set','ott','nov','dic'];
zk.S2MON=zk.SMON;
zk.FMON=['gennaio','febbraio','marzo','aprile','maggio','giugno','luglio','agosto','settembre','ottobre','novembre','dicembre'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {};
msgzul.UNKNOWN_TYPE = "Tipo di componente sconosciuto: ";
msgzul.DATE_REQUIRED = "Devi inserire una data. Formato: ";
msgzul.OUT_OF_RANGE = "Fuori limite";
msgzul.NO_AUDIO_SUPPORT = "Il tuo browser non supporta la funzione di audio dinamico";

zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Non è permesso il campo vuoto.\nNon puoi neanche inserire solamente spazi.',
INTEGER_REQUIRED:'Devi inserire un intero, al posto di {0}.',
NUMBER_REQUIRED:'Devi inserire un numero, al posto di {0}.',
DATE_REQUIRED:'Devi inserire una data, al posto di {0}.\nFormato: {1}.',
CANCEL:'Annulla',
NO_POSITIVE_NEGATIVE_ZERO:'Sono permessi solo numeri positivi',
NO_POSITIVE_NEGATIVE:'Solo lo zero è permesso',
NO_POSITIVE_ZERO:'Sono permessi solo numeri negativi',
NO_POSITIVE:'Sono permessi solo numeri non positivi',
NO_NEGATIVE_ZERO:'Sono permessi solo numeri positivi',
NO_NEGATIVE:'Sono permessi solo numeri non negativi',
NO_ZERO:'Sono permessi solo numeri diversi da zero',
NO_FUTURE_PAST_TODAY:'E\' permesso solo il campo vuoto',
NO_FUTURE_PAST:'Solo lo zero è permesso',
NO_FUTURE_TODAY:'Sono permesse solo date del passato',
NO_FUTURE:'Sono permesse solo date non del futura',
NO_PAST_TODAY:'OSono permesse solo date del futuro',
NO_PAST:'Sono permesse solo date non del passato',
NO_TODAY:'Solo oggi è permesso',
FIRST:'Prima',
LAST:'Ultima',
PREV:'Precedente',
NEXT:'Successiva'});
}finally{zPkg.end(_z);}}