_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {};
mesg.NOT_FOUND = "No s'ha trobat: ";
mesg.UNSUPPORTED = "Encara no és compatible: ";
mesg.FAILED_TO_SEND = "Ha fallat l'enviament de la petició al servidor.";
mesg.FAILED_TO_RESPONSE = "El servidor ha fallat en processar la petició.";
mesg.TRY_AGAIN = "Voleu provar una altra vegada?";
mesg.UNSUPPORTED_BROWSER = "El navegador no és compatible: ";
mesg.ILLEGAL_RESPONSE = "Resposta desconeguda enviada des del servidor. Si us plau, recarregueu i proveu una altra vegada.\n";
mesg.FAILED_TO_PROCESS = "Ha fallat el procés ";
mesg.GOTO_ERROR_FIELD = "Aneu al camp erroni";
mesg.PLEASE_WAIT = "S'està processant...";

mesg.FILE_SIZE = "Mida de l'arxiu: ";
mesg.KBYTES = "KB";

mesg.FAILED_TO_LOAD = "Ha fallat la càrrega ";
mesg.FAILED_TO_LOAD_DETAIL = "Pot haver estat provocat pel mal trànsit de les comunicacions. Podríeu recarregar aquesta pàgina i provar una altra vegada.";
mesg.CAUSE = "Motiu: ";

;zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['dl.','dt.','dc.','dj.','dv.','ds.','dg.'];
zk.S2DOW=['dl','dt','dc','dj','dv','ds','dg'];
zk.FDOW=['dilluns','dimarts','dimecres','dijous','divendres','dissabte','diumenge'];
zk.SMON=['gen.','feb.','març','abr.','maig','juny','jul.','ag.','set.','oct.','nov.','des.'];
zk.S2MON=['gen','feb','març','abr','maig','juny','jul','ag','set','oct','nov','des'];
zk.FMON=['gener','febrer','març','abril','maig','juny','juliol','agost','setembre','octubre','novembre','desembre'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {};
msgzul.UNKNOWN_TYPE = "El tipus de component és desconegut: ";
msgzul.DATE_REQUIRED = "Heu d'especificar una data. Format: ";
msgzul.OUT_OF_RANGE = "Fora dels valors a l'abast";
msgzul.NO_AUDIO_SUPPORT = "El navegador no admet l'àudio dinàmic";

;zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Heu d\'especificar un dels valors de la llista desplegable.',
EMPTY_NOT_ALLOWED:'Cal emplenar el camp.\nNo es pot deixar buit ni a blancs.',
INTEGER_REQUIRED:'S\'ha d\'especificar un nombre enter, en lloc de {0}.',
NUMBER_REQUIRED:'S\'ha d\'especificar un nombre, en lloc de {0}.',
DATE_REQUIRED:'S\'ha d\'especificar una data, en lloc de {0}.\nFormat: {1}.',
CANCEL:'Cancel·la',
NO_POSITIVE_NEGATIVE_ZERO:'Només són permesos nombres majors que zero',
NO_POSITIVE_NEGATIVE:'Només es permet el valor zero',
NO_POSITIVE_ZERO:'Només es permeten nombres negatius',
NO_POSITIVE:'Només es permeten nombres negatius o zero',
NO_NEGATIVE_ZERO:'Només es permeten nombres positius',
NO_NEGATIVE:'Només es permeten nombres positius o zero',
NO_ZERO:'No es permet zero',
NO_FUTURE_PAST_TODAY:'Només es permet buit',
NO_FUTURE_PAST:'Només es permet la data d\'avui',
NO_FUTURE_TODAY:'Només es permet una data anterior a la d\'avui',
NO_FUTURE:'No es permet una data posterior a la d\'avui',
NO_PAST_TODAY:'Només es permet una data posterior a la d\'avui',
NO_PAST:'No es permet una data anterior a la d\'avui',
NO_TODAY:'No es permet la data d\'avui',
FIRST:'Primera',
LAST:'Darrera',
PREV:'Precedent',
NEXT:'Següent'});
}finally{zPkg.end(_z);}}