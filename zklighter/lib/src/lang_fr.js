_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {
NOT_FOUND: "Pas trouvé: ",
UNSUPPORTED: "Pas encore supporté: ",
FAILED_TO_SEND: "Echec lors de l'envoi d'une requête vers le serveur.",
FAILED_TO_RESPONSE: "Le serveur à échoué dans le traitement de votre requête.",
TRY_AGAIN: "Voulez-vous essayer à nouveau?",
UNSUPPORTED_BROWSER: "Navigateur non supporté: ",
ILLEGAL_RESPONSE: "Réponse inconnue envoyée depuis le serveur. Rechagez cette page et essayez de nouveau.\n",
FAILED_TO_PROCESS: "N'a pas été traité ",
GOTO_ERROR_FIELD: "Allez sur le mauvais champ",
PLEASE_WAIT: "Chargement...",

FILE_SIZE: "Taille du fichier: ",
KBYTES: "KB",

FAILED_TO_LOAD: "Erreur de chargement ",
FAILED_TO_LOAD_DETAIL: "Cela peut être dû à un problème de réseau. Rechagez cette page et essayez de nouveau.",
CAUSE: "Cause: "
};
zk.GROUPING=" ";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['lun.','mar.','mer.','jeu.','ven.','sam.','dim.'];
zk.S2DOW=['lun','mar','mer','jeu','ven','sam','dim'];
zk.FDOW=['lundi','mardi','mercredi','jeudi','vendredi','samedi','dimanche'];
zk.SMON=['janv.','févr.','mars','avr.','mai','juin','juil.','août','sept.','oct.','nov.','déc.'];
zk.S2MON=['janv','févr','mars','avr','mai','juin','juil','août','sept','oct','nov','déc'];
zk.FMON=['janvier','février','mars','avril','mai','juin','juillet','août','septembre','octobre','novembre','décembre'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {
UNKNOWN_TYPE: "Type inconnue de composant: ",
DATE_REQUIRED: "Vous devez specifiez une date. Format: ",
OUT_OF_RANGE: "Dépassement du rang",
NO_AUDIO_SUPPORT: "Votre navigateur ne supporte pas l'audio dynamique"
};

zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Vous deviez spécifier l\'une des valeurs dans la liste déroulante',
EMPTY_NOT_ALLOWED:'Champ vide non autorisé.\nVous devez spécifier une valeur',
INTEGER_REQUIRED:'Vous devez spécifier un entier, au lieu de {0}.',
NUMBER_REQUIRED:'Vous devez spécifier un nombre au lieu de {0}.',
DATE_REQUIRED:'Vous devez spécifier une date au lieu de {0}.\nFormat: {1}.',
CANCEL:'Annuler',
NO_POSITIVE_NEGATIVE_ZERO:'Seul les chiffres positifs sont autorisés',
NO_POSITIVE_NEGATIVE:'Seul zéro est autorisé',
NO_POSITIVE_ZERO:'Seul les chiffres négatifs sont autorisés',
NO_POSITIVE:'Seul les chiffres non positifs sont autorisés',
NO_NEGATIVE_ZERO:'Seul les chiffres positifs sont autorisés',
NO_NEGATIVE:'Seul les chiffres non-négatifs sont autorisés.',
NO_ZERO:'Seul les chiffres différents de 0 sont autorisés',
NO_FUTURE_PAST_TODAY:'Seulement le champ vide est autorisé',
NO_FUTURE_PAST:'Seul zéro est autorisé',
NO_FUTURE_TODAY:'Seul les dates passées sont autorisées',
NO_FUTURE:'Seul les dates qui ne sont pas dans le futur sont autorisées',
NO_PAST_TODAY:'Seul les dates dans le futur sont autorisées',
NO_PAST:'Seul les dates qui ne sont pas dans le passé sont autorisées',
NO_TODAY:'Seul la date du jour est autorisée.',
FIRST:'Premier',
LAST:'Dernier',
PREV:'Précédent',
NEXT:'Suivant'});
}finally{zPkg.end(_z);}}