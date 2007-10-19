REM Remember to include full path with directory
FOR /F %%G IN ('DIR /b %CD%\*.odt') DO soffice -invisible macro:///Standard.Module1.SaveAsDocbook(%CD%\%%G)
FOR /F %%G IN ('DIR /b %CD%\ch4\*.odt') DO soffice -invisible macro:///Standard.Module1.SaveAsDocbook(%CD%\ch4\%%G)