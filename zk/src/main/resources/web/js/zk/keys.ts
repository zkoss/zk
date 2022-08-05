/* keys.ts

		Purpose:

		Description:

		History:
				Tue Sep 01 11:59:55 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/

export namespace keys_global {
	/** @class zKeys
	 * A collection of constants for KeyboardEvent.key values.
	 * @since 9.5.0
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values">Key Values</a>
	 */
	export enum zKeys {
		// Special values
		/** UNIDENTIFIED
		 * @type String
		 */
		UNIDENTIFIED = 'Unidentified',

		// Modifier keys
		/** ALT
		 * @type String
		 */
		ALT = 'Alt',
		/** ALTGRAPH
		 * @type String
		 */
		ALTGRAPH = 'AltGraph',
		/** CAPSLOCK
		 * @type String
		 */
		CAPSLOCK = 'CapsLock',
		/** CONTROL
		 * @type String
		 */
		CONTROL = 'Control',
		/** FN
		 * @type String
		 */
		FN = 'Fn',
		/** FNLOCK
		 * @type String
		 */
		FNLOCK = 'FnLock',
		/** HYPER
		 * @type String
		 */
		HYPER = 'Hyper',
		/** META
		 * @type String
		 */
		META = 'Meta',
		/** NUMLOCK
		 * @type String
		 */
		NUMLOCK = 'NumLock',
		/** SCROLLLOCK
		 * @type String
		 */
		SCROLLLOCK = 'ScrollLock',
		/** SHIFT
		 * @type String
		 */
		SHIFT = 'Shift',
		/** SUPER
		 * @type String
		 */
		SUPER = 'Super',
		/** SYMBOL
		 * @type String
		 */
		SYMBOL = 'Symbol',
		/** SYMBOLLOCK
		 * @type String
		 */
		SYMBOLLOCK = 'SymbolLock',

		// Whitespace keys
		/** ENTER
		 * @type String
		 */
		ENTER = 'Enter',
		/** TAB
		 * @type String
		 */
		TAB = 'Tab',
		/** SPACE
		 * @type String
		 */
		SPACE = ' ',

		// Navigation keys
		/** ARROWDOWN
		 * @type String
		 */
		ARROWDOWN = 'ArrowDown',
		/** ARROWLEFT
		 * @type String
		 */
		ARROWLEFT = 'ArrowLeft',
		/** ARROWRIGHT
		 * @type String
		 */
		ARROWRIGHT = 'ArrowRight',
		/** ARROWUP
		 * @type String
		 */
		ARROWUP = 'ArrowUp',
		/** END
		 * @type String
		 */
		END = 'End',
		/** HOME
		 * @type String
		 */
		HOME = 'Home',
		/** PAGEDOWN
		 * @type String
		 */
		PAGEDOWN = 'PageDown',
		/** PAGEUP
		 * @type String
		 */
		PAGEUP = 'PageUp',

		// Editing keys
		/** BACKSPACE
		 * @type String
		 */
		BACKSPACE = 'Backspace',
		/** CLEAR
		 * @type String
		 */
		CLEAR = 'Clear',
		/** COPY
		 * @type String
		 */
		COPY = 'Copy',
		/** CRSEL
		 * @type String
		 */
		CRSEL = 'CrSel',
		/** CUT
		 * @type String
		 */
		CUT = 'Cut',
		/** DELETE
		 * @type String
		 */
		DELETE = 'Delete',
		/** ERASEEOF
		 * @type String
		 */
		ERASEEOF = 'EraseEof',
		/** EXSEL
		 * @type String
		 */
		EXSEL = 'ExSel',
		/** INSERT
		 * @type String
		 */
		INSERT = 'Insert',
		/** PASTE
		 * @type String
		 */
		PASTE = 'Paste',
		/** REDO
		 * @type String
		 */
		REDO = 'Redo',
		/** UNDO
		 * @type String
		 */
		UNDO = 'Undo',

		// UI keys
		/** ACCEPT
		 * @type String
		 */
		ACCEPT = 'Accept',
		/** AGAIN
		 * @type String
		 */
		AGAIN = 'Again',
		/** ATTN
		 * @type String
		 */
		ATTN = 'Attn',
		/** CANCEL
		 * @type String
		 */
		CANCEL = 'Cancel',
		/** CONTEXTMENU
		 * @type String
		 */
		CONTEXTMENU = 'ContextMenu',
		/** ESCAPE
		 * @type String
		 */
		ESCAPE = 'Escape',
		/** EXECUTE
		 * @type String
		 */
		EXECUTE = 'Execute',
		/** FIND
		 * @type String
		 */
		FIND = 'Find',
		/** FINISH
		 * @type String
		 */
		FINISH = 'Finish',
		/** HELP
		 * @type String
		 */
		HELP = 'Help',
		/** PAUSE
		 * @type String
		 */
		PAUSE = 'Pause',
		/** PLAY
		 * @type String
		 */
		PLAY = 'Play',
		/** PROPS
		 * @type String
		 */
		PROPS = 'Props',
		/** SELECT
		 * @type String
		 */
		SELECT = 'Select',
		/** ZOOMIN
		 * @type String
		 */
		ZOOMIN = 'ZoomIn',
		/** ZOOMOUT
		 * @type String
		 */
		ZOOMOUT = 'ZoomOut',

		// Device keys
		/** BRIGHTNESSDOWN
		 * @type String
		 */
		BRIGHTNESSDOWN = 'BrightnessDown',
		/** BRIGHTNESSUP
		 * @type String
		 */
		BRIGHTNESSUP = 'BrightnessUp',
		/** EJECT
		 * @type String
		 */
		EJECT = 'Eject',
		/** LOGOFF
		 * @type String
		 */
		LOGOFF = 'LogOff',
		/** POWER
		 * @type String
		 */
		POWER = 'Power',
		/** POWEROFF
		 * @type String
		 */
		POWEROFF = 'PowerOff',
		/** PRINTSCREEN
		 * @type String
		 */
		PRINTSCREEN = 'PrintScreen',
		/** HIBERNATE
		 * @type String
		 */
		HIBERNATE = 'Hibernate',
		/** STANDBY
		 * @type String
		 */
		STANDBY = 'Standby',
		/** WAKEUP
		 * @type String
		 */
		WAKEUP = 'WakeUp',

		// Common IME keys
		/** ALLCANDIDATES
		 * @type String
		 */
		ALLCANDIDATES = 'AllCandidates',
		/** ALPHANUMERIC
		 * @type String
		 */
		ALPHANUMERIC = 'Alphanumeric',
		/** CODEINPUT
		 * @type String
		 */
		CODEINPUT = 'CodeInput',
		/** COMPOSE
		 * @type String
		 */
		COMPOSE = 'Compose',
		/** CONVERT
		 * @type String
		 */
		CONVERT = 'Convert',
		/** DEAD
		 * @type String
		 */
		DEAD = 'Dead',
		/** FINALMODE
		 * @type String
		 */
		FINALMODE = 'FinalMode',
		/** GROUPFIRST
		 * @type String
		 */
		GROUPFIRST = 'GroupFirst',
		/** GROUPLAST
		 * @type String
		 */
		GROUPLAST = 'GroupLast',
		/** GROUPNEXT
		 * @type String
		 */
		GROUPNEXT = 'GroupNext',
		/** GROUPPREVIOUS
		 * @type String
		 */
		GROUPPREVIOUS = 'GroupPrevious',
		/** MODECHANGE
		 * @type String
		 */
		MODECHANGE = 'ModeChange',
		/** NEXTCANDIDATE
		 * @type String
		 */
		NEXTCANDIDATE = 'NextCandidate',
		/** NONCONVERT
		 * @type String
		 */
		NONCONVERT = 'NonConvert',
		/** PREVIOUSCANDIDATE
		 * @type String
		 */
		PREVIOUSCANDIDATE = 'PreviousCandidate',
		/** PROCESS
		 * @type String
		 */
		PROCESS = 'Process',
		/** SINGLECANDIDATE
		 * @type String
		 */
		SINGLECANDIDATE = 'SingleCandidate',

		// Korean keyboards only
		/** HANGULMODE
		 * @type String
		 */
		HANGULMODE = 'HangulMode',
		/** HANJAMODE
		 * @type String
		 */
		HANJAMODE = 'HanjaMode',
		/** JUNJAMODE
		 * @type String
		 */
		JUNJAMODE = 'JunjaMode',

		// Japanese keyboards only
		/** EISU
		 * @type String
		 */
		EISU = 'Eisu',
		/** HANKAKU
		 * @type String
		 */
		HANKAKU = 'Hankaku',
		/** HIRAGANA
		 * @type String
		 */
		HIRAGANA = 'Hiragana',
		/** HIRAGANAKATAKANA
		 * @type String
		 */
		HIRAGANAKATAKANA = 'HiraganaKatakana',
		/** KANAMODE
		 * @type String
		 */
		KANAMODE = 'KanaMode',
		/** KANJIMODE
		 * @type String
		 */
		KANJIMODE = 'KanjiMode',
		/** KATAKANA
		 * @type String
		 */
		KATAKANA = 'Katakana',
		/** ROMAJI
		 * @type String
		 */
		ROMAJI = 'Romaji',
		/** ZENKAKU
		 * @type String
		 */
		ZENKAKU = 'Zenkaku',
		/** ZENKAKUHANAKU
		 * @type String
		 */
		ZENKAKUHANAKU = 'ZenkakuHanaku',

		// Skip: Dead keycodes for Linux

		// Function keys
		/** F1
		 * @type String
		 */
		F1 = 'F1',
		/** F2
		 * @type String
		 */
		F2 = 'F2',
		/** F3
		 * @type String
		 */
		F3 = 'F3',
		/** F4
		 * @type String
		 */
		F4 = 'F4',
		/** F5
		 * @type String
		 */
		F5 = 'F5',
		/** F6
		 * @type String
		 */
		F6 = 'F6',
		/** F7
		 * @type String
		 */
		F7 = 'F7',
		/** F8
		 * @type String
		 */
		F8 = 'F8',
		/** F9
		 * @type String
		 */
		F9 = 'F9',
		/** F10
		 * @type String
		 */
		F10 = 'F10',
		/** F11
		 * @type String
		 */
		F11 = 'F11',
		/** F12
		 * @type String
		 */
		F12 = 'F12',
		/** F13
		 * @type String
		 */
		F13 = 'F13',
		/** F14
		 * @type String
		 */
		F14 = 'F14',
		/** F15
		 * @type String
		 */
		F15 = 'F15',
		/** F16
		 * @type String
		 */
		F16 = 'F16',
		/** F17
		 * @type String
		 */
		F17 = 'F17',
		/** F18
		 * @type String
		 */
		F18 = 'F18',
		/** F19
		 * @type String
		 */
		F19 = 'F19',
		/** F20
		 * @type String
		 */
		F20 = 'F20',
		/** SOFT1
		 * @type String
		 */
		SOFT1 = 'Soft1',
		/** SOFT2
		 * @type String
		 */
		SOFT2 = 'Soft2',
		/** SOFT3
		 * @type String
		 */
		SOFT3 = 'Soft3',
		/** SOFT4
		 * @type String
		 */
		SOFT4 = 'Soft4',

		// Phone keys
		/** APPSWITCH
		 * @type String
		 */
		APPSWITCH = 'AppSwitch',
		/** CALL
		 * @type String
		 */
		CALL = 'Call',
		/** CAMERA
		 * @type String
		 */
		CAMERA = 'Camera',
		/** CAMERAFOCUS
		 * @type String
		 */
		CAMERAFOCUS = 'CameraFocus',
		/** ENDCALL
		 * @type String
		 */
		ENDCALL = 'EndCall',
		/** GOBACK
		 * @type String
		 */
		GOBACK = 'GoBack',
		/** GOHOME
		 * @type String
		 */
		GOHOME = 'GoHome',
		/** HEADSETHOOK
		 * @type String
		 */
		HEADSETHOOK = 'HeadsetHook',
		/** LASTNUMBERREDIAL
		 * @type String
		 */
		LASTNUMBERREDIAL = 'LastNumberRedial',
		/** NOTIFICATION
		 * @type String
		 */
		NOTIFICATION = 'Notification',
		/** MANNERMODE
		 * @type String
		 */
		MANNERMODE = 'MannerMode',
		/** VOICEDIAL
		 * @type String
		 */
		VOICEDIAL = 'VoiceDial',

		// Multimedia keys
		/** CHANNELDOWN
		 * @type String
		 */
		CHANNELDOWN = 'ChannelDown',
		/** CHANNELUP
		 * @type String
		 */
		CHANNELUP = 'ChannelUp',
		/** MEDIAFASTFORWARD
		 * @type String
		 */
		MEDIAFASTFORWARD = 'MediaFastForward',
		/** MEDIAPAUSE
		 * @type String
		 */
		MEDIAPAUSE = 'MediaPause',
		/** MEDIAPLAY
		 * @type String
		 */
		MEDIAPLAY = 'MediaPlay',
		/** MEDIAPLAYPAUSE
		 * @type String
		 */
		MEDIAPLAYPAUSE = 'MediaPlayPause',
		/** MEDIARECORD
		 * @type String
		 */
		MEDIARECORD = 'MediaRecord',
		/** MEDIAREWIND
		 * @type String
		 */
		MEDIAREWIND = 'MediaRewind',
		/** MEDIASTOP
		 * @type String
		 */
		MEDIASTOP = 'MediaStop',
		/** MEDIATRACKNEXT
		 * @type String
		 */
		MEDIATRACKNEXT = 'MediaTrackNext',
		/** MEDIATRACKPREVIOUS
		 * @type String
		 */
		MEDIATRACKPREVIOUS = 'MediaTrackPrevious',

		// Audio control keys
		/** AUDIOBALANCELEFT
		 * @type String
		 */
		AUDIOBALANCELEFT = 'AudioBalanceLeft',
		/** AUDIOBALANCERIGHT
		 * @type String
		 */
		AUDIOBALANCERIGHT = 'AudioBalanceRight',
		/** AUDIOBASSDOWN
		 * @type String
		 */
		AUDIOBASSDOWN = 'AudioBassDown',
		/** AUDIOBASSBOOSTDOWN
		 * @type String
		 */
		AUDIOBASSBOOSTDOWN = 'AudioBassBoostDown',
		/** AUDIOBASSBOOSTTOGGLE
		 * @type String
		 */
		AUDIOBASSBOOSTTOGGLE = 'AudioBassBoostToggle',
		/** AUDIOBASSBOOSTUP
		 * @type String
		 */
		AUDIOBASSBOOSTUP = 'AudioBassBoostUp',
		/** AUDIOBASSUP
		 * @type String
		 */
		AUDIOBASSUP = 'AudioBassUp',
		/** AUDIOFADERFRONT
		 * @type String
		 */
		AUDIOFADERFRONT = 'AudioFaderFront',
		/** AUDIOFADERREAR
		 * @type String
		 */
		AUDIOFADERREAR = 'AudioFaderRear',
		/** AUDIOSURROUNDMODENEXT
		 * @type String
		 */
		AUDIOSURROUNDMODENEXT = 'AudioSurroundModeNext',
		/** AUDIOTREBLEDOWN
		 * @type String
		 */
		AUDIOTREBLEDOWN = 'AudioTrebleDown',
		/** AUDIOTREBLEUP
		 * @type String
		 */
		AUDIOTREBLEUP = 'AudioTrebleUp',
		/** AUDIOVOLUMEDOWN
		 * @type String
		 */
		AUDIOVOLUMEDOWN = 'AudioVolumeDown',
		/** AUDIOVOLUMEMUTE
		 * @type String
		 */
		AUDIOVOLUMEMUTE = 'AudioVolumeMute',
		/** AUDIOVOLUMEUP
		 * @type String
		 */
		AUDIOVOLUMEUP = 'AudioVolumeUp',
		/** MICROPHONETOGGLE
		 * @type String
		 */
		MICROPHONETOGGLE = 'MicrophoneToggle',
		/** MICROPHONEVOLUMEDOWN
		 * @type String
		 */
		MICROPHONEVOLUMEDOWN = 'MicrophoneVolumeDown',
		/** MICROPHONEVOLUMEMUTE
		 * @type String
		 */
		MICROPHONEVOLUMEMUTE = 'MicrophoneVolumeMute',
		/** MICROPHONEVOLUMEUP
		 * @type String
		 */
		MICROPHONEVOLUMEUP = 'MicrophoneVolumeUp',

		// Document keys
		/** CLOSE
		 * @type String
		 */
		CLOSE = 'Close',
		/** NEW
		 * @type String
		 */
		NEW = 'New',
		/** OPEN
		 * @type String
		 */
		OPEN = 'Open',
		/** PRINT
		 * @type String
		 */
		PRINT = 'Print',
		/** SAVE
		 * @type String
		 */
		SAVE = 'Save',
		/** SPELLCHECK
		 * @type String
		 */
		SPELLCHECK = 'SpellCheck',
		/** MAILFORWARD
		 * @type String
		 */
		MAILFORWARD = 'MailForward',
		/** MAILREPLY
		 * @type String
		 */
		MAILREPLY = 'MailReply',
		/** MAILSEND
		 * @type String
		 */
		MAILSEND = 'MailSend',

		// Skip: TV control keys, Media controller keys, Speech recognition keys, Application selector keys

		// Browser control keys
		/** BROWSERBACK
		 * @type String
		 */
		BROWSERBACK = 'BrowserBack',
		/** BROWSERFAVORITES
		 * @type String
		 */
		BROWSERFAVORITES = 'BrowserFavorites',
		/** BROWSERFORWARD
		 * @type String
		 */
		BROWSERFORWARD = 'BrowserForward',
		/** BROWSERHOME
		 * @type String
		 */
		BROWSERHOME = 'BrowserHome',
		/** BROWSERREFRESH
		 * @type String
		 */
		BROWSERREFRESH = 'BrowserRefresh',
		/** BROWSERSEARCH
		 * @type String
		 */
		BROWSERSEARCH = 'BrowserSearch',
		/** BROWSERSTOP
		 * @type String
		 */
		BROWSERSTOP = 'BrowserStop',

		// Numeric keypad keys
		/** DECIMAL
		 * @type String
		 */
		DECIMAL = 'Decimal',
		/** KEY11
		 * @type String
		 */
		KEY11 = 'Key11',
		/** KEY12
		 * @type String
		 */
		KEY12 = 'Key12',
		/** MULTIPLY
		 * @type String
		 */
		MULTIPLY = '*',
		/** ADD
		 * @type String
		 */
		ADD = '+',
	//	CLEAR = 'Clear', // already defined in Editing keys
		/** DIVIDE
		 * @type String
		 */
		DIVIDE = '/',
		/** SUBTRACT
		 * @type String
		 */
		SUBTRACT = '-',
		/** SEPARATOR
		 * @type String
		 */
		SEPARATOR = 'Separator',
		/** NUMPAD0
		 * @type String
		 */
		NUMPAD0 = '0',
		/** NUMPAD1
		 * @type String
		 */
		NUMPAD1 = '1',
		/** NUMPAD2
		 * @type String
		 */
		NUMPAD2 = '2',
		/** NUMPAD3
		 * @type String
		 */
		NUMPAD3 = '3',
		/** NUMPAD4
		 * @type String
		 */
		NUMPAD4 = '4',
		/** NUMPAD5
		 * @type String
		 */
		NUMPAD5 = '5',
		/** NUMPAD6
		 * @type String
		 */
		NUMPAD6 = '6',
		/** NUMPAD7
		 * @type String
		 */
		NUMPAD7 = '7',
		/** NUMPAD8
		 * @type String
		 */
		NUMPAD8 = '8',
		/** NUMPAD9
		 * @type String
		 */
		NUMPAD9 = '9',
	}
}
zk.copy(window, keys_global);