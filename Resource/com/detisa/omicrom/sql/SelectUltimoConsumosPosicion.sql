SELECT
      IFNULL( rmt.id, '' ) ttr,
      rm.id TR,
      rm.posicion POSICION,
      rm.manguera MANGUERA,
      rm.producto PRODUCTO,
      rm.volumen VOLUMEN,
      ROUND( rm.pesos, 2 ) PESOS,
      rm.tipo_venta TIPO,
      com.clave CLAVE,
      com.descripcion DESCRIPCION,
      DATE_FORMAT(rm.fin_venta,'%d/%m/%Y %H:%i:%s') FIN_VENTA,
      cli.id cliente, cli.tipodepago, cli.formadepago
FROM
      rm
      JOIN com ON rm.producto = com.clavei
      JOIN cli ON cli.id = rm.cliente
      LEFT JOIN rm_transacciones rmt ON rm.id = rmt.id
WHERE rm.id = (  
        SELECT MAX( id )  
        FROM rm 
        WHERE posicion =  $POSICION )
AND rm.importe > 0
AND rm.tipo_venta = 'D' 
AND rm.uuid = '-----'
AND ( cli.id = 0 OR ( cli.tipodepago = 'Tarjeta' AND cli.formadepago REGEXP '01|04|28' ) )
