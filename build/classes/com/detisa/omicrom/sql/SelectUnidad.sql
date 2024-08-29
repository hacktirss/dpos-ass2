SELECT
      unidades.id ID,
      unidades.descripcion DESCRIPCION,
      unidades.cliente CLIENTE,
      unidades.placas PLACAS,
      unidades.numeco NUMECO,
      unidades.codigo CODIGO,
      unidades.impreso IMPRESO,
      unidades.combustible COMBUSTIBLE,
      unidades.aditivos ADITIVOS,
      IFNULL(com.descripcion, '') COM,
      unidades.litros LITROS,
      unidades.importe IMPORTE,
      unidades.interes INTERES,
      unidades.estado ESTADO,
      unidades.periodo PERIODO,
      unidades.simultaneo SIMULTANEO,
      unidades.nip NIP,
      variables.nipterminal PIDENIP,
      UCASE( DATE_FORMAT( CURDATE(),'%w' ) ) as DIA,
( CASE
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '1' AND luni <= CURTIME() AND CURTIME() <= lunf THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '2' AND mari <= CURTIME() AND CURTIME() <= marf THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '3' AND miei <= CURTIME() AND CURTIME() <= mief THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '4' AND juei <= CURTIME() AND CURTIME() <= juef THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '5' AND viei <= CURTIME() AND CURTIME() <= vief THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '6' AND sabi <= CURTIME() AND CURTIME() <= sabf THEN 'S'
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '0' AND domi <= CURTIME() AND CURTIME() <= domf THEN 'S'
        ELSE 'N' END ) ALLOWED,
( CASE
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '1' THEN luni
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '2' THEN mari
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '3' THEN miei
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '4' THEN juei
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '5' THEN viei
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '6' THEN sabi
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '0' THEN domi END ) HORAI,
( CASE
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '1' THEN lunf
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '2' THEN marf
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '3' THEN mief
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '4' THEN juef
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '5' THEN vief
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '6' THEN sabf
	WHEN UCASE(DATE_FORMAT(CURDATE(),'%w')) LIKE '0' THEN domf END ) HORAF
FROM unidades
JOIN variables ON 1=1
LEFT JOIN com ON INSTR( combustible, com.id )
WHERE UCASE( codigo ) = UCASE( '$CODIGO' )