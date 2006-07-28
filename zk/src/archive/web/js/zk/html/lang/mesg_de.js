/* mesg.js

{{IS_NOTE
  Purpose:
    Locale dependent message
  Description:

  History:
    Jul 27, 2006, H.-Dirk Schmitt
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
  This program is distributed under GPL Version 2.0 in the hope that
  it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
function mesg() {};

//mesg.NOT_FOUND= "Not found: ";
mesg.NOT_FOUND = "Nicht gefunden: ";

//mesg.UNSUPPORTED= "Not supported yet: "
mesg.UNSUPPORTED = "Bisher nicht unterstützt: "

//mesg.FAILED_TO_SEND= "Failed to send requests to server: ";
mesg.FAILED_TO_SEND = "Request kann nicht an Server geschickt werden: ";

//mesg.FAILED_TO_RESPONSE= "Server failed to process your request: ";
mesg.FAILED_TO_RESPONSE = "Server kann Request nicht bearbeiten: ";

//!!! HDS-060727 spell error + most users know firefox, but not the old mozilla browser
//mesg.UNSUPPORTED_BROWSER = "Unsupported browser, please use Internet Explorer or Mozill: ";
mesg.UNSUPPORTED_BROWSER = "Nicht unterstützer Browser, bitte IE, Mozilla oder Firefox benutzen: ";

//mesg.ILLEGAL_RESPONSE= "Unknown response sent from the server. Please reload and try again.\n";
mesg.ILLEGAL_RESPONSE = "Unbekannte Antwort vom Server, Bitte aktualisieren Sie die Seite und probieren es erneut.\n";

//mesg.FAILED_TO_PROCESS= "Failed to process ";
mesg.FAILED_TO_PROCESS = "Verarbeitung fehlgeschlagen ";

//mesg.UUID_REQUIRED= "UUID is required";
mesg.UUID_REQUIRED = "UUID wird benötigt";

//mesg.INVALID_STRUCTURE= "Invalid structure: ";
mesg.INVALID_STRUCTURE = "Korrupte Struktur: ";

//mesg.COMP_OR_UUID_REQUIRED= "Component or its UUID is required";
mesg.COMP_OR_UUID_REQUIRED = "Komponente oder ihre UUID wird benötigt";

//mesg.NUMBER_REQUIRED= "You must specify a number, rather than ";
mesg.NUMBER_REQUIRED = "Sie müssen eine Zahl angeben, und nicht ";

//mesg.INTEGER_REQUIRED= "You must specify an integer, rather than ";
mesg.INTEGER_REQUIRED = "Sie müssen eine ganze Zahl angeben, und nicht ";

//mesg.EMPTY_NOT_ALLOWED= "Empty is not allowed.\nYou cannot specify nothing but spaces, either";
mesg.EMPTY_NOT_ALLOWED = "Keine Eingabe ist nicht erlaubt.\nAuch nur Leerzeichen sind nicht möglich";

//mesg.GOTO_ERROR_FIELD= "Go to the wrong field";
mesg.GOTO_ERROR_FIELD = "Gehe zum falschen Feld";

//mesg.PLEASE_WAIT = "Processing...";
mesg.PLEASE_WAIT = "Verarbeitung...";