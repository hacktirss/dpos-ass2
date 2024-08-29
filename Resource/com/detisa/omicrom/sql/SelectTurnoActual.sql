SELECT *
FROM tur
WHERE curtime() BETWEEN horai AND horaf
AND activo = 'Si'
