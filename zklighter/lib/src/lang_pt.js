_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

mesg = {};
mesg.NOT_FOUND = "Não encontrado: ";
mesg.UNSUPPORTED = "Ainda não suportado: ";
mesg.FAILED_TO_SEND = "Falhou ao enviar requisição para o servidor.";
mesg.FAILED_TO_RESPONSE = "O servidor falhou ao processar sua requisição.";
mesg.TRY_AGAIN = "Tentar novamente?";
mesg.UNSUPPORTED_BROWSER = "Navegador não suportado: ";
mesg.ILLEGAL_RESPONSE = "Resposta desconhecida enviada pelo servidor. Por favor, recarregue e tente novamente.\n";
mesg.FAILED_TO_PROCESS = "Falhou ao processar ";
mesg.GOTO_ERROR_FIELD = "Ir para o campo errado";
mesg.PLEASE_WAIT = "Processando...";

mesg.FILE_SIZE = "Tamanho do arquivo: ";
mesg.KBYTES = "KB";

mesg.FAILED_TO_LOAD="Falhou ao carregar ";
mesg.FAILED_TO_LOAD_DETAIL="Isto pode ser causado por um mau tráfego. Você pode recarregar esta página e tentar novamente.";
mesg.CAUSE="Causa: ";

;zk.GROUPING=".";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['Seg','Ter','Qua','Qui','Sex','Sáb','Dom'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['Segunda-feira','Terça-feira','Quarta-feira','Quinta-feira','Sexta-feira','Sábado','Domingo'];
zk.SMON=['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'];
zk.S2MON=zk.SMON;
zk.FMON=['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

msgzul = {};
msgzul.UNKNOWN_TYPE = "Tipo de componente desconhecido: ";
msgzul.DATE_REQUIRED = "Você deve especificar uma data. Formato: ";
msgzul.OUT_OF_RANGE = "Fora de faixa";
msgzul.NO_AUDIO_SUPPORT = "Seu navegador não suporta áudio dinâmico";

;zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Você deve especificar um ou mais valores na lista de selecção',
EMPTY_NOT_ALLOWED:'Em branco não permitido.\nMesmo somente espaços não são permitidos',
INTEGER_REQUIRED:'Você deve especificar um inteiro, em vez de {0}.',
NUMBER_REQUIRED:'Você deve especificar um número, em vez de {0}.',
DATE_REQUIRED:'Você deve especificar uma data, em vez de {0}.\nFormato: {1}.',
CANCEL:'Cancelar',
NO_POSITIVE_NEGATIVE_ZERO:'Permitido somente número positivo',
NO_POSITIVE_NEGATIVE:'Permitido somente zero',
NO_POSITIVE_ZERO:'Permitido somente número negativo',
NO_POSITIVE:'Permitido somente número não-positivo',
NO_NEGATIVE_ZERO:'Permitido somente número positivo',
NO_NEGATIVE:'Permitido somente número não-negativo',
NO_ZERO:'Permitido somente número diferente de zero',
NO_FUTURE_PAST_TODAY:'Permitido somente vazio',
NO_FUTURE_PAST:'Permitido somente zero',
NO_FUTURE_TODAY:'Permitido somente data passada',
NO_FUTURE:'Somente não permitida data futura',
NO_PAST_TODAY:'Permitido somente data futura',
NO_PAST:'Somente não permitido data passada',
NO_TODAY:'Somente hoje é permitido',
FIRST:'Primeiro',
LAST:'Último',
PREV:'Anterior',
NEXT:'Próximo'});
}finally{zPkg.end(_z);}}