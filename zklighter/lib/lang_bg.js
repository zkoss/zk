_z='zk.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
mesg={};mesg.NOT_FOUND="Не е открит: ";mesg.UNSUPPORTED="Още не се поддържа: ";mesg.FAILED_TO_SEND="Проблем при изпращане на заявките към сървъра.";mesg.FAILED_TO_RESPONSE="Сървъра не може да обработи заявката.";mesg.TRY_AGAIN="Try again?";mesg.UNSUPPORTED_BROWSER="Този броузер не се поддържа: ";mesg.ILLEGAL_RESPONSE="Проблем с отговора на сървъра. Презаредете страницата наново.\n";mesg.FAILED_TO_PROCESS="Възникна проблем в процеса на работа ";mesg.UUID_REQUIRED="UUID е задължителен";mesg.INVALID_STRUCTURE="Грешна структура: ";mesg.COMP_OR_UUID_REQUIRED="Компонента или неговия UUID са задължителни";mesg.NUMBER_REQUIRED="Числото трябва да е по-голямо от ";mesg.INTEGER_REQUIRED="Изберете по-голямо целочислено число от";mesg.EMPTY_NOT_ALLOWED="Не може да е празно или само интервали.";mesg.GOTO_ERROR_FIELD="Ползване на грешно поле";mesg.PLEASE_WAIT="Зарежда се...";mesg.VALUE_NOT_MATCHED="Трябва да посочите една от стойностите в падащия списък.";mesg.FILE_SIZE="Размер на файла: ";mesg.KBYTES="KB";mesg.CANCEL="Откажи";mesg.FAILED_TO_LOAD="Проблем при зареждане ";mesg.FAILED_TO_LOAD_DETAIL="Възможно е да се дължи на лоша връзка със сървъра. Опитайте да презаредите страницата отново.";mesg.CAUSE="Причина: ";
;zk.GROUPING=" ";
zk.DECIMAL=",";
zk.PERCENT="%";
zk.MINUS="-";
zk.PER_MILL="‰";
zk.DOW_1ST=0;
zk.SDOW=['Нд','Пн','Вт','Ср','Чт','Пт','Сб'];
zk.S2DOW=zk.SDOW;
zk.FDOW=['Неделя','Понеделник','Вторник','Сряда','Четвъртък','Петък','Събота'];
zk.SMON=['I','II','III','IV','V','VI','VII','VIII','IX','X','XI','XII'];
zk.S2MON=zk.SMON;
zk.FMON=['Януари','Февруари','Март','Април','Май','Юни','Юли','Август','Септември','Октомври','Ноември','Декември'];
zk.APM=['AM','PM'];

}finally{zPkg.end(_z);}}_z='zul.lang';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);
msgzul={};msgzul.UNKNOWN_TYPE="Непознат тип на компонента: ";msgzul.DATE_REQUIRED="Трябва да зададете дата в формат: ";msgzul.OUT_OF_RANGE="Извън обхвата";msgzul.NO_AUDIO_SUPPORT="Броузерът ви неподдържа динамично аудио";
;zk.$default(msgzul, {
VALUE_NOT_MATCHED:'Трябва да посочите една от стойностите в падащия списък.',
EMPTY_NOT_ALLOWED:'Трябва да зададете стойност',
INTEGER_REQUIRED:'Трябва да бъде цяло число, по-голямо от {0}.',
NUMBER_REQUIRED:'Трябва да бъде число, по-голямо от {0}.',
DATE_REQUIRED:'Датата трябва да е след {0}.\nФормата е: {1}.',
CANCEL:'Прекъсни',
NO_POSITIVE_NEGATIVE_ZERO:'Числото може да бъде само положително',
NO_POSITIVE_NEGATIVE:'Може да приема само стойност 0',
NO_POSITIVE_ZERO:'Числото може да бъде само отрицателно',
NO_POSITIVE:'Числото може да бъде само отрицателно и нула',
NO_NEGATIVE_ZERO:'Числото може да бъде само положително',
NO_NEGATIVE:'Числото може да бъде само положително и нула',
NO_ZERO:'Числото не може да бъде нула',
NO_FUTURE_PAST_TODAY:'Може да бъде само празно',
NO_FUTURE_PAST:'Приема само нула',
NO_FUTURE_TODAY:'Датата може да бъде само от минал период',
NO_FUTURE:'Приема само дати, които не са от бъдещ период',
NO_PAST_TODAY:'Датата може да бъде само от бъдещ период',
NO_PAST:'Приема само дати, които не са от минал период',
NO_TODAY:'Приема само днешна дата',
FIRST:'Първа',
LAST:'Последна',
PREV:'Предишна',
NEXT:'Следваща'});
}finally{zPkg.end(_z);}}