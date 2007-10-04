REM Remember to include full path with directory
FOR /F %%G IN ('DIR /b %1\*.odt') DO soffice -invisible macro:///Standard.Module1.SaveAsDocbook(%1\%%G)