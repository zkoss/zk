_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="No encontrado: ";mesg.UNSUPPORTED="No es soportado por esta version: ";mesg.FAILED_TO_SEND="Fallo al enviar peticiones (request) al servidor.";mesg.FAILED_TO_RESPONSE="El servidor fallo al procesar la peticion.";mesg.TRY_AGAIN="Inténtalo de nuevo?";mesg.UNSUPPORTED_BROWSER="Explorador (Browser) no compatible: ";mesg.ILLEGAL_RESPONSE="Respuesta no valida por parte del servidor. Por favor actualice la pagina (reload) e intente de nuevo.\n";mesg.FAILED_TO_PROCESS="Fallo al procesar ";mesg.GOTO_ERROR_FIELD="Ir al campo que contiene el error ";mesg.PLEASE_WAIT="Procesando...";mesg.FILE_SIZE="Tamaño del archivo: ";mesg.KBYTES="KB";mesg.FAILED_TO_LOAD="Fallo al cargar ";mesg.FAILED_TO_LOAD_DETAIL="Puede ser a causa de un tráfico erroneo. Podria refrescar de nuevo la página.";mesg.CAUSE="Causa: ";mesg.LOADING="Cargando...";
zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['lun','mar','mié','jue','vie','sáb','dom'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['lunes','martes','miércoles','jueves','viernes','sábado','domingo'];
zk.SMON=['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];
zk.S2MON=zk.SMON;
zk.FMON=['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Tipo de componente desconocido: ";msgzul.DATE_REQUIRED="Debe especificar una fecha. Formato: ";msgzul.OUT_OF_RANGE="Valor fuera de rango";msgzul.NO_AUDIO_SUPPORT="Tu explorador (browser) no soporta audio dinamico ";
zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Debe especificar uno de los valores de la lista desplegable.',
EMPTY_NOT_ALLOWED:'No se permite vacio o espacios en blanco.\nDebe especificar un valor diferente',
INTEGER_REQUIRED:'Debe especificar un numero entero, en lugar de {0}.',
NUMBER_REQUIRED:'Debe especificar un numero, en lugar de {0}.',
DATE_REQUIRED:'Debe especificar una fecha, en lugar de {0}.\nFormato: {1}.',
CANCEL:'Cancelar',
NO_POSITIVE_NEGATIVE_ZERO:'Solo numeros positivos son permitidos',
NO_POSITIVE_NEGATIVE:'Solo cero es permitido',
NO_POSITIVE_ZERO:'Solo numeros negativos son permitidos',
NO_POSITIVE:'Solo numeros negativos o cero son permitidos',
NO_NEGATIVE_ZERO:'Solo numeros positivos son permitidos',
NO_NEGATIVE:'Solo numeros positivos o cero son permitidos',
NO_ZERO:'Solo numeros distintos de cero son permitidos',
NO_FUTURE_PAST_TODAY:'Unicamente se permite un valor vacio',
NO_FUTURE_PAST:'Solo cero es permitido',
NO_FUTURE_TODAY:'Solo se permiten fechas anteriores',
NO_FUTURE:'Solo se permiten fechas anteriores o la fecha actual',
NO_PAST_TODAY:'Solo se permiten fecha posteriores',
NO_PAST:'Solo se permiten fechas posteriores o la fecha actual',
NO_TODAY:'Solo la fecha actual es permitida',
FIRST:'Primero',
LAST:'Ultimo',
PREV:'Anterior',
NEXT:'Siguiente'});
}finally{zPkg.end(_z);}}