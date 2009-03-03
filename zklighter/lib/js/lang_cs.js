_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="Nenalezeno: ";mesg.UNSUPPORTED="Nepodporováno: ";mesg.FAILED_TO_SEND="Chyba při komunikaci se serverem.";mesg.FAILED_TO_RESPONSE="Server je dočasně nedostupný, prosím opakujte akci později.";mesg.TRY_AGAIN="Try again?";mesg.UNSUPPORTED_BROWSER="Váš prohlížeč není podporován: ";mesg.ILLEGAL_RESPONSE="Neznámá odpověď od serveru, opakujte prosím akci.\n";mesg.FAILED_TO_PROCESS="Chyba zpracování ";mesg.GOTO_ERROR_FIELD="Jdi na pole s chybou";mesg.PLEASE_WAIT="Pracuji...";mesg.FILE_SIZE="Velikost souboru: ";mesg.KBYTES="KB";mesg.FAILED_TO_LOAD="Chyba při nahrávání ";mesg.FAILED_TO_LOAD_DETAIL="Chyba může být způsobena špatným připojením, zkuste obnovit stránku";mesg.CAUSE="Příčina: ";mesg.LOADING="Pracuji...";
zk.GROUPING=" ";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['Po','Út','St','Čt','Pá','So','Ne'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['Pondělí','Úterý','Středa','Čtvrtek','Pátek','Sobota','Neděle'];
zk.SMON=['I','II','III','IV','V','VI','VII','VIII','IX','X','XI','XII'];
zk.S2MON=zk.SMON;
zk.FMON=['leden','únor','březen','duben','květen','červen','červenec','srpen','září','říjen','listopad','prosinec'];
zk.APM=['dop.','odp.'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Neznámý typ komponenty: ";msgzul.DATE_REQUIRED="Je nutné zadat datum. Formát: ";msgzul.OUT_OF_RANGE="Hodnota je mimo daný rozsah";msgzul.NO_AUDIO_SUPPORT="Váš prohlížeč nepodporuje streamované audio";
zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Vyberte prosím jednu z nabízených hodnot',
EMPTY_NOT_ALLOWED:'Zadejte hodnotu',
INTEGER_REQUIRED:'Povoleny pouze celočíselné hodnoty, místo {0}',
NUMBER_REQUIRED:'Povoleny pouze číslené hodnoty, místo {0}',
DATE_REQUIRED:'Povoleno pouze datum ve formátu: {1}',
CANCEL:'Zrušit',
NO_POSITIVE_NEGATIVE_ZERO:'Povolena pouze kladná čísla',
NO_POSITIVE_NEGATIVE:'Povolena pouze nula',
NO_POSITIVE_ZERO:'Povolena pouze záporná čísla',
NO_POSITIVE:'Povolena pouze nekladná čísla',
NO_NEGATIVE_ZERO:'Povolena pouze kladná čísla',
NO_NEGATIVE:'Povolena pouze nezáporná čísla',
NO_ZERO:'Nula není povolena',
NO_FUTURE_PAST_TODAY:'Tento den nelze vybrat',
NO_FUTURE_PAST:'Lze vybrat pouze dnešek',
NO_FUTURE_TODAY:'Povoleno pouze datum v minulosti',
NO_FUTURE:'Povoleno pouze datum v minulosti a dnešek',
NO_PAST_TODAY:'Povolenou pouze datum v budoucnosti',
NO_PAST:'Povolenou pouze datum v budoucnosti a dnešek',
NO_TODAY:'Dnešek není povolen',
FIRST:'První',
LAST:'Poslední',
PREV:'Předchozí',
NEXT:'Následující'});
}finally{zPkg.end(_z);}}