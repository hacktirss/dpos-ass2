SELECT
      id CARD_ID,
      descripcion DESCRIPCION,
      cliente CLIENTE,
      placas PLACAS,
      codigo CODIGO,
      impreso IMPRESO,
      combustible COMBUSTIBLE,
      litros LITROS,
      importe IMPORTE,
      interes INTERES,
      estado ESTADO,
( CASE
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'LUN%' THEN luni
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'MAR%' THEN mari
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'MIE%' THEN miei
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'JUE%' THEN juei
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'VIE%' THEN viei
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'SÁB%' THEN sabi
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'I' ) ) LIKE 'DOM%' THEN domi END ) HORAI,
( CASE
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'LUN%' THEN lunf
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'MAR%' THEN marf
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'MIE%' THEN mief
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'JUE%' THEN juef
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'VIE%' THEN vief
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'SÁB%' THEN sabf
	WHEN UCASE( CONCAT( DATE_FORMAT( CURDATE(),'%a' ), 'F' ) ) LIKE 'DOM%' THEN domf END ) HORAF
FROM unidades
WHERE UCASE( codigo ) = UCASE( '$ID_TARJETA' )
