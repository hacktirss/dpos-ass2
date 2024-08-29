SELECT 
   UCASE( U.codigo ) codigo, U.periodo, U.importe, U.litros,
   U.importe - IF( U.periodo = 'B', IFNULL( B.autorizado, 0.00 ), IFNULL( ROUND( SUM( C.importe ), 2 ), 0.00 ) ) consumo_permitido,
   U.litros - IF( U.periodo = 'B', 0.00, IFNULL( ROUND( SUM( C.litros ), 2 ), 0.00 ) ) volumen_permitido,
   IFNULL( ROUND( SUM( C.importe ), 2 ), 0.00 ) AS consumo_periodo, 
   IFNULL( ROUND( SUM( C.litros ), 2 ), 0.00 ) AS volumen_periodo,
   IF( U.importe=0, 'Litros', 'Pesos' ) tipo_limite
FROM unidades U 
JOIN ( 
      SELECT 
            CAST( DATE_FORMAT( curr, '%Y%m%d' ) AS UNSIGNED ) hoy,
            CAST( DATE_FORMAT( curr, '%Y%m' ) AS UNSIGNED ) mes, 
            CAST( DATE_FORMAT( DATE_SUB( curr, INTERVAL WEEKDAY( curr ) DAY ), '%Y%m%d' ) AS UNSIGNED ) weeki, 
            CAST( DATE_FORMAT( DATE_ADD( curr, INTERVAL 6 - WEEKDAY( curr ) DAY ), '%Y%m%d' ) AS UNSIGNED ) weekf,
            CAST( DATE_FORMAT( DATE_SUB( curr, INTERVAL DAYOFMONTH( curr ) -1 DAY ), '%Y%m%d' ) AS UNSIGNED ) monthi,
            CAST( DATE_FORMAT( LAST_DAY ( curr ), '%Y%m%d' ) AS UNSIGNED ) monthf,
            CAST( CONCAT( DATE_FORMAT( curr, '%Y%m' ), '15' ) AS UNSIGNED ) quincena, 
            DAY( curr ) < 16 quin1
      FROM ( SELECT CURDATE() curr ) CD
) P ON TRUE
LEFT JOIN ( 
	SELECT 
                SUM( autorizado ) autorizado, 
                id_cliente cliente,
                codigo
        FROM bitacora_autorizaciones
        WHERE UCASE( bitacora_autorizaciones.codigo ) = UCASE( '$CODIGO' ) AND status = 0
        GROUP BY codigo
) B ON B.cliente = U.cliente
    AND B.codigo = U.codigo
LEFT JOIN (
   SELECT rm.pesos importe, rm.volumen litros, fecha_venta, rm.cliente, rm.codigo
   FROM unidades 
   JOIN cli ON unidades.cliente = cli.id 
   JOIN rm ON rm.cliente = cli.id AND rm.codigo = unidades.codigo
   WHERE unidades.estado = 'a' AND UCASE( unidades.codigo ) = UCASE( '$CODIGO' )
   UNION ALL
   SELECT vtaditivos.total importe, 0 litros, CAST( DATE_FORMAT( fecha, '%Y%m%d' ) AS UNSIGNED ) fecha_venta, vtaditivos.cliente, vtaditivos.codigo 
   FROM unidades 
   JOIN cli ON unidades.cliente = cli.id 
   JOIN vtaditivos ON vtaditivos.cliente = cli.id AND vtaditivos.codigo = unidades.codigo
   WHERE unidades.estado = 'a' AND UCASE( unidades.codigo ) = UCASE( '$CODIGO' )
) C 
    ON C.cliente = U.cliente 
    AND C.codigo = U.codigo 
    AND ( 
        CASE 
            WHEN U.periodo = 'M' AND ROUND( fecha_venta/100 ) = mes THEN TRUE  -- Mensual
            WHEN U.periodo = 'S' AND fecha_venta BETWEEN weeki AND weekf THEN TRUE -- Semanal
            WHEN U.periodo = 'Q' AND ( ( quin1 AND fecha_venta BETWEEN weeki AND quincena ) OR ( !quin1 AND fecha_venta BETWEEN ( quincena + 1 ) AND monthf ) ) THEN TRUE -- Quincenal
            WHEN U.periodo = 'D' AND fecha_venta = hoy THEN TRUE -- Diario)
            WHEN U.periodo = 'B' AND U.importe > 0 THEN TRUE
            ELSE FALSE
        END 
    )
WHERE U.codigo = '$CODIGO';
