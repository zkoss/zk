_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="Ej hittat: ";mesg.UNSUPPORTED="Stödjes inte än: ";mesg.FAILED_TO_SEND="Kunde inte sända begäran till servern.";mesg.FAILED_TO_RESPONSE="Servern kunde inte hantera din begäran.";mesg.TRY_AGAIN="Try again?";mesg.UNSUPPORTED_BROWSER="Icke-supportad webbläsare: ";mesg.ILLEGAL_RESPONSE="Okänt svar från server. Var god uppdatera sidan och försök igen..\n";mesg.FAILED_TO_PROCESS="Kunde inte hantera ";mesg.GOTO_ERROR_FIELD="Gå till felaktigt fält";mesg.PLEASE_WAIT="Arbetar...";mesg.FILE_SIZE="Filstorlek: ";mesg.KBYTES="KB";mesg.FAILED_TO_LOAD="Uppladdning misslyckades ";mesg.FAILED_TO_LOAD_DETAIL="Kan ha orsakats av trafikstörning. Ladda om denna sida och försök igen.";mesg.CAUSE="Orsak: ";mesg.LOADING="Arbetar...";
zk.GROUPING=" ";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['må','ti','on','to','fr','lö','sö'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['måndag','tisdag','onsdag','torsdag','fredag','lördag','söndag'];
zk.SMON=['jan','feb','mar','apr','maj','jun','jul','aug','sep','okt','nov','dec'];
zk.S2MON=zk.SMON;
zk.FMON=['januari','februari','mars','april','maj','juni','juli','augusti','september','oktober','november','december'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Okänd komponenttyp: ";msgzul.DATE_REQUIRED="Du måste ange ett datum. Format: ";msgzul.OUT_OF_RANGE="Utom intervall";msgzul.NO_AUDIO_SUPPORT="Din webbläsare stödjer inte dynamic audio";
zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Only values in the drop-down list are allowed',
EMPTY_NOT_ALLOWED:'Blank inte tillåtet.\nDu kan inte ange annat än blanksteg',
INTEGER_REQUIRED:'Du måste ange ett heltal, istället för {0}.',
NUMBER_REQUIRED:'Du måste ange ett tal, istället för {0}.',
DATE_REQUIRED:'Du måste ange ett datum, istället för {0}.\nFormat: {1}.',
CANCEL:'Avbryt',
NO_POSITIVE_NEGATIVE_ZERO:'Endast positivt tal tillåtet',
NO_POSITIVE_NEGATIVE:'Endast noll tillåtet',
NO_POSITIVE_ZERO:'Endast negativt tal tillåtet',
NO_POSITIVE:'Endast icke-positivt tal tillåtet',
NO_NEGATIVE_ZERO:'Endast positivt tal tillåtet',
NO_NEGATIVE:'Endast icke-negativt tal tillåtet',
NO_ZERO:'Endast icke-nollvärde tillåtet',
NO_FUTURE_PAST_TODAY:'Endast blank tillåten',
NO_FUTURE_PAST:'Endast noll tillåten',
NO_FUTURE_TODAY:'Endast förflutet datum tillåtet',
NO_FUTURE:'Datum i framtiden ej tillåtet',
NO_PAST_TODAY:'Endast framtida datum tillåtet',
NO_PAST:'Endast icke-förflutna datum tillåtna',
NO_TODAY:'Endast dagens datum tillåtet',
FIRST:'Först',
LAST:'Sist',
PREV:'Föreg',
NEXT:'Nästa'});
}finally{zPkg.end(_z);}}