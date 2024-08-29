SELECT 
        UCASE( u.codigo ), u.periodo, u.importe, u.litros, 
        ( u.importe - IFNULL( ROUND( SUM( v.importe ), 2 ), 0.00 ) ) consumo_permitido,
        ( u.litros - IFNULL( ROUND( SUM( v.litros ), 2 ), 0.00 ) ) volumen_permitido,
        IFNULL( ROUND( SUM( v.importe ), 2 ), 0.00 ) AS consumo_periodo, 
        IFNULL( ROUND( SUM( v.litros ), 2 ), 0.00 ) AS volumen_periodo,
        IF( u.importe = 0, 'Litros', 'Pesos' ) tipo_limite
FROM unidades u
LEFT JOIN ( 
            SELECT v.total importe, 0 litros, CAST( DATE_FORMAT( v.fecha, '%Y%m%d' ) AS UNSIGNED ) fecha_venta, v.cliente, v.codigo 
            FROM vtaditivos v 
            JOIN fecha_filter_idx f ON TRUE
            WHERE v.codigo = '$CODIGO' AND CAST( DATE_FORMAT( v.fecha, '%Y%m%d' ) AS UNSIGNED ) BETWEEN f.start_week AND f.end_week
        UNION ALL 
            SELECT rm.pesos importe, rm.volumen litros, rm.fecha_venta, rm.cliente, rm.codigo 
            FROM rm 
            JOIN fecha_filter_idx f ON TRUE
            WHERE rm.codigo = '$CODIGO' AND rm.fecha_venta BETWEEN f.start_week AND f.end_week
) v ON v.codigo = u.codigo
WHERE u.codigo = '$CODIGO';
