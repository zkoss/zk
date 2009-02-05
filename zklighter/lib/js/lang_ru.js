_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="Не найден: ";mesg.UNSUPPORTED="Не поддерживается: ";mesg.FAILED_TO_SEND="Не удалось послать запрос на сервер.";mesg.FAILED_TO_RESPONSE="Сервер временно недоступен.";mesg.TRY_AGAIN="Попробуйте снова?";mesg.UNSUPPORTED_BROWSER="Неподдерживаемый браузер: ";mesg.ILLEGAL_RESPONSE="Неизвестный ответ получен от сервера. Пожалуйста перезагрузите страницу и попробуйте вновь.\n";mesg.FAILED_TO_PROCESS="Не удалось обработать ";mesg.GOTO_ERROR_FIELD="Перемещение к неверному полю";mesg.PLEASE_WAIT="Обработка запроса...";mesg.FILE_SIZE="Размер файла: ";mesg.KBYTES="KB";mesg.FAILED_TO_LOAD="Не удалось загрузить ";mesg.FAILED_TO_LOAD_DETAIL="Возможно, проблема с подключением к сети. Перезагрузите страницу и попробуйте снова.";mesg.CAUSE="Причина: ";
zk.GROUPING=" ";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=1;
zk.SDOW=['Пн','Вт','Ср','Чт','Пт','Сб','Вс'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['понедельник','вторник','среда','четверг','пятница','суббота','воскресенье'];
zk.SMON=['янв','фев','мар','апр','май','июн','июл','авг','сен','окт','ноя','дек'];
zk.S2MON=zk.SMON;
zk.FMON=['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Неизвестный тип компонента: ";msgzul.DATE_REQUIRED="Необходимо указать дату. Формат: ";msgzul.OUT_OF_RANGE="Не вписывается в диапазон";msgzul.NO_AUDIO_SUPPORT="Ваш браузер не поддерживает динамическое аудио";
zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Вы должны выбрать одно из значений в выпадающем списке.',
EMPTY_NOT_ALLOWED:'Пустой ввод не разрешен',
INTEGER_REQUIRED:'Вы должны ввести целое число, а не {0}.',
NUMBER_REQUIRED:'Вы должны ввести число, а не {0}.',
DATE_REQUIRED:'Вы должны ввести дату, а не {0}.\nФормат: {1}.',
CANCEL:'Отмена',
NO_POSITIVE_NEGATIVE_ZERO:'Разрешены только положительные числа',
NO_POSITIVE_NEGATIVE:'Разрешен только ноль',
NO_POSITIVE_ZERO:'Разрешены только отрицательные числа',
NO_POSITIVE:'Разрешены только не положительные числа',
NO_NEGATIVE_ZERO:'Разрешены только положительные числа',
NO_NEGATIVE:'Разрешены только не отрицательные числа',
NO_ZERO:'Разрешены только ненулевые числа',
NO_FUTURE_PAST_TODAY:'Разрешено только пустое значение',
NO_FUTURE_PAST:'Разрешена только сегодняшняя дата',
NO_FUTURE_TODAY:'Разрешена только дата, лежащая в прошлом',
NO_FUTURE:'Разрешена только дата, лежащая не в будущем',
NO_PAST_TODAY:'Разрешена только дата, лежащая в будущем',
NO_PAST:'Разрешена только дата, лежащая не в прошлом',
NO_TODAY:'Сегодняшняя дата не разрешена',
FIRST:'Первый',
LAST:'Последний',
PREV:'Предыдущий',
NEXT:'Следующий'});
}finally{zPkg.end(_z);}}