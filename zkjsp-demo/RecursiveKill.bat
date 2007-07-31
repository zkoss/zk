



FOR /F "tokens=*" %%G IN ('DIR /B /AD /S *svn*') DO RMDIR /S /Q %%G
