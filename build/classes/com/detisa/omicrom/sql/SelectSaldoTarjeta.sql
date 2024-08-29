SELECT 
    IFNULL( U.importe, 0 ) - IF( U.periodo = 'B', 0.00, IFNULL( ROUND( SUM( C.pesos ), 2 ), 0.00 ) ) permitido,
    IF( U.importe = 0 AND U.periodo != 'B', 'L', 'P' ) tipo,
    ( CASE 
        WHEN U.tipodepago = 'Prepago' THEN 
            ROUND( IFNULL( abonos, 0 )-IFNULL( cargos, 0 ), 2 ) 
        WHEN U.tipodepago = 'Pospago' OR U.tipodepago = 'Credito' THEN
            ROUND( IFNULL( limite, 0 )+IFNULL( abonos, 0 )-IFNULL( cargos, 0 ), 2) 
        ELSE 0
    END ) saldo
FROM (
        SELECT ROUND( importe, 2 ) importe, ROUND( litros, 2 ) litros, periodo, unidades.codigo, cliente, cli.limite, cli.tipodepago
        FROM unidades 
        JOIN cli ON cli.id = unidades.cliente
        WHERE unidades.codigo = '$CODIGO' AND unidades.estado = 'a'
) U
LEFT JOIN rm C ON C.cliente = U.cliente AND C.codigo = U.codigo
JOIN ( 
    SELECT
	CAST( DATE_FORMAT( CURDATE(), '%Y%m%d' ) AS UNSIGNED ) today, 
	CAST( DATE_FORMAT( CURDATE(), '%Y%m01' ) AS UNSIGNED ) start_month, 
	CAST( DATE_FORMAT( CURDATE(), '%Y%m15' ) AS UNSIGNED ) middle_month, 
	CAST( DATE_FORMAT( LAST_DAY( CURDATE() ), '%Y%m%d' ) AS UNSIGNED ) end_month,
	CAST( DATE_FORMAT( CURDATE() + INTERVAL - WEEKDAY( CURDATE() ) DAY, '%Y%m%d' ) AS UNSIGNED ) start_week, 
	CAST( DATE_FORMAT( CURDATE() + INTERVAL 6 - WEEKDAY( CURDATE() ) DAY, '%Y%m%d' ) AS UNSIGNED ) end_week
    FROM dual
) date_idx ON TRUE
AND
(
       ( U.periodo = 'M' AND fecha_venta BETWEEN date_idx.start_month AND date_idx.end_month ) -- Mensual
   OR  ( U.periodo = 'S' AND fecha_venta BETWEEN date_idx.start_week AND date_idx.end_week ) -- Semanal
   OR  ( U.periodo = 'Q' AND fecha_venta BETWEEN 
            ( CASE WHEN DAY( CURDATE() ) < 16 THEN date_idx.start_month ELSE ( date_idx.middle_month + 1 ) END ) AND 
            ( CASE WHEN DAY( CURDATE() ) < 16 THEN date_idx.middle_month ELSE date_idx.end_month END ) )
   OR  ( U.periodo = 'D' AND fecha_venta = date_idx.today ) -- Diario
   OR  ( U.periodo = 'B' )
)
LEFT JOIN (
    SELECT 
            cxc.cliente, 
            IFNULL( SUM( ( CASE WHEN cxc.tm = 'H' THEN cxc.importe ELSE 0 END ) ), 0 ) abonos, 
            IFNULL( SUM( ( CASE WHEN cxc.tm = 'C' THEN cxc.importe ELSE 0 END ) ), 0 ) cargos
    FROM cxc
    JOIN unidades ON unidades.codigo = '$CODIGO' AND unidades.cliente = cxc.cliente
) cxc ON cxc.cliente = U.cliente
