_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="Nu s-a găsit: ";mesg.UNSUPPORTED="Nu este suportat încă: ";mesg.FAILED_TO_SEND="Procesul de trimitere a cererii către server a eşuat";mesg.FAILED_TO_RESPONSE="Server-ul nu a putut procesa cererea dumneavoastră.";mesg.TRY_AGAIN="Try again?";mesg.UNSUPPORTED_BROWSER="Browser nu este suportat: ";mesg.ILLEGAL_RESPONSE="Răspuns necunoscut trimis de server. Vă rugăm reîncărcaţi şi încercaţi din nou.\n";mesg.FAILED_TO_PROCESS="Procesare eşuată ";mesg.GOTO_ERROR_FIELD="Selectaţi câmpul greşit";mesg.PLEASE_WAIT="Se procesează...";mesg.FILE_SIZE="Dimensiune fişier: ";mesg.KBYTES="KB";mesg.FAILED_TO_LOAD="Eşuare la încărcare ";mesg.FAILED_TO_LOAD_DETAIL="Poate fi cauzat de traficul anevoios. Puteţi reîncărca această pagină şi încerca din nou.";mesg.CAUSE="Cauza: ";mesg.LOADING="Se procesează...";
zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=0;
zk.SDOW=['D','L','Ma','Mi','J','V','S'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['duminică','luni','marţi','miercuri','joi','vineri','sîmbătă'];
zk.SMON=['Ian','Feb','Mar','Apr','Mai','Iun','Iul','Aug','Sep','Oct','Nov','Dec'];
zk.S2MON=zk.SMON;
zk.FMON=['ianuarie','februarie','martie','aprilie','mai','iunie','iulie','august','septembrie','octombrie','noiembrie','decembrie'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Tip necunoscut de componentă: ";msgzul.DATE_REQUIRED="Trebuie să specificaţi o dată. Formatul: ";msgzul.OUT_OF_RANGE="Se află în afara limitelor admise";msgzul.NO_AUDIO_SUPPORT="Browser-ul dumneavoastră nu suportă audio dinamic";
zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Valoarea vidă nu este permisă.\nDeasemenea, nu puteţi specifica numai spaţii',
INTEGER_REQUIRED:'Trebuie să specificaţi un număr întreg, în loc de {0}.',
NUMBER_REQUIRED:'Trebuie să specificaţi un număr, în loc de {0}.',
DATE_REQUIRED:'Trebuie să specificaţi o dată, în loc de {0}.\nFormatul: {1}.',
CANCEL:'Anulează',
NO_POSITIVE_NEGATIVE_ZERO:'Doar un număr pozitiv este permis',
NO_POSITIVE_NEGATIVE:'Doar numărul zero este permis',
NO_POSITIVE_ZERO:'Doar un număr negativ este permis',
NO_POSITIVE:'Este permis doar un număr negativ sau zero',
NO_NEGATIVE_ZERO:'Doar un număr pozitiv este permis',
NO_NEGATIVE:'Este permis doar un număr pozitiv sau zero',
NO_ZERO:'Este permis doar un număr diferit de zero',
NO_FUTURE_PAST_TODAY:'Doar valoarea vidă este permisă',
NO_FUTURE_PAST:'Doar zero este permis',
NO_FUTURE_TODAY:'Este permisă doar o dată anterioară',
NO_FUTURE:'Este permisă doar o dată anterioară sau curentă',
NO_PAST_TODAY:'Este permisă doar o dată ulterioară',
NO_PAST:'Este permisă doar o dată ulterioară sau curentă',
NO_TODAY:'Este permisă doar data curentă',
FIRST:'Prima',
LAST:'Ultima',
PREV:'Precedenta',
NEXT:'Următoarea'});
}finally{zPkg.end(_z);}}