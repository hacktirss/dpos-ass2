SELECT 
        rm.id TR, rm.posicion POSICION, rm.manguera MANGUERA,
        rm.producto PRODUCTO, rm.volumen VOLUMEN, round( rm.pesos, 2 ) PESOS,
        rm.tipo_venta TIPO, com.clave CLAVE, com.descripcion DESCRIPCION,
        DATE_FORMAT(rm.fin_venta,'%d/%m/%Y %H:%i:%s') FIN_VENTA,
        rm.cliente CLI, cli.nombre CLI_NOMBRE, cli.tipodepago CLI_TIPO
FROM rm 
    JOIN com ON rm.producto = com.clavei 
    JOIN cli ON cli.id = rm.cliente
    JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'impresiones_pos' ), '0' ) impresiones ) var ON TRUE 
WHERE rm.fecha_venta > CAST( DATE_FORMAT( DATE_SUB( NOW(), INTERVAL 7 DAY ), '%Y%m%d' ) AS UNSIGNED ) 
        AND rm.posicion = $POSICION AND rm.completo = 1 
        AND ( var.impresiones = 0 OR var.impresiones > rm.comprobante )
ORDER BY rm.FIN_VENTA DESC
LIMIT $LIMITE
