SELECT
      C.id TR,
      C.tipo_venta TIPO,
      C.posicion POSICION,
      C.manguera MANGUERA,
      C.producto PRODUCTO,
      IF( C.tipo_venta = 'D' AND ABS( C.diferencia ) > 0.02, ROUND( ( C.importe + IF( IFNULL( cli.desgloseieps, 'N' ) = 'S', 0.00, C.importeieps ) + C.diferencia )/( C.preciouu + IF( IFNULL( cli.desgloseieps, 'N' ) = 'S', 0.0000, C.ieps ) ), 4 ), C.volumen ) VOLUMEN,
      C.pesos PESOS,
      C.importeiva TIVA,
      C.importeieps TIEPS,
      C.importe + IF( IFNULL( desgloseieps, 'N' ) = 'S', 0.00, importeieps ) + IF( C.tipo_venta = 'D' AND ABS( C.diferencia ) > 0.02, C.diferencia, 0.00 ) IMPORTE,
      C.precio PRECIO,
      C.preciouu + IF( IFNULL( desgloseieps, 'N' ) = 'S', 0.0000, C.ieps ) PRECIOU,
      C.iva IVA,
      C.ieps IEPS,
      DATE_FORMAT( C.fin_venta, '%d/%m/%Y %H:%i:%s' ) FIN_VENTA,
      DATE_FORMAT( NOW(), '%d/%m/%Y %H:%i:%s' ) FECHA_IMPRESION,
      C.comprobante+1 COMPROBANTE,
      C.cliente CLIENTE,
      C.placas PLACAS,
      IFNULL( C.codigo, '' ) CODIGO,
      C.kilometraje ODOMETRO,
      P.clave CLAVE,
      P.descripcion DESCRIPCION,
      cli.nombre NOMBRE,
      LOWER( IF( cli.tipodepago IS NULL OR TRIM( cli.tipodepago ) = '', 'Contado', cli.tipodepago ) ) TIPODEPAGO,
      cli.ID CLI_ID,
      ( CASE WHEN VAR.fae = 'No' OR ( C.cliente > 0 AND cli.tipodepago NOT REGEXP 'Tarjeta|Monedero' ) THEN 'No' ELSE 'Si' END ) FACTURACION,
      VAR.msgfae FACT_MENSAJE,
      IFNULL( VARC.valor, VAR.urlfae ) FACT_LIGA,
      direccionexp EXPDIRECCION,
      numeroextexp EXPNUMEROEXT,
      numerointexp EXPNUMEROINT,
      coloniaexp EXPCOLONIA,
      ciudadexp EXPCIUDAD,
      ciudadexp EXPCUIDAD,
      estadoexp EXPESTADO,
      codigoexp EXPCODIGO_POSTAL,
      CONCAT( LPAD( C.id, 7, '0'), LPAD( C.posicion, 2, '0' ), LPAD( C .manguera, 2, '0' ), LPAD( SUBSTRING( E.numestacion, 2 ), 5, '0' ) ) NUM_TICKET,
      SUBSTR( UPPER( SHA1( CONCAT( '|', LPAD( C.id, 7, '0' ), '|', LPAD( C.posicion, 2, '0' ), '|', LPAD( C.manguera, 2, '0' ), '|', LPAD( E.idfae, 5, '0' ), '|', DATE_FORMAT( C.fin_venta, '%Y-%m-%dT%H:%i:%s' ), '|', CAST( ROUND( C.volumen, 4 ) AS DECIMAL( 10, 4 ) ), '|', CAST( ROUND( C.pesos, 2 ) AS DECIMAL( 10, 2 ) ), '|' ) ) ), 1, 23 ) FOLIO_FAE,
      IF ( IFNULL( FP.descripcion, 'MXN' )='USD', 'S', 'N' ) MONEDA, 
      ROUND( IFNULL( FP.detalle, 1.0 ), 2 ) TIPO_CAMBIO, 
      ROUND( IFNULL( FP.monto, C.pesos ), 2 ) DOLARES, 
      ( CASE
         WHEN C.comprobante=0 THEN 'ORIGINAL'
         ELSE 'COPIA'
      END ) TIPO_IMPRESION
FROM
      (
        SELECT
              rm.id,
              rm.tipo_venta,
              rm.posicion,
              rm.manguera,
              rm.fin_venta,
              rm.precio,
              ROUND( rm.volumen, 4 ) volumen,
              ROUND( rm.pesos, 2 ) pesos,
              rm.producto,
              rm.iva,
              rm.ieps,
              rm.comprobante,
              rm.cliente,
              rm.placas,
              rm.codigo,
              rm.kilometraje,
              ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ) preciouu,
              ROUND( rm.volumen * ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ), 2 ) importe, 
              ROUND( rm.volumen * ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ) * rm.iva, 2 ) importeiva,
              ROUND( rm.volumen * rm.ieps, 2 ) importeieps,
              ROUND( rm.pesos, 2 )
                -ROUND( rm.volumen * ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ), 2 )
                -ROUND( rm.volumen * ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ) * rm.iva, 2 )
                -ROUND( rm.volumen * rm.ieps, 2 ) diferencia
        FROM rm
      ) C
      JOIN (
              SELECT MAX( rm.id ) ID
              FROM rm
              WHERE rm.posicion = $POSICION AND rm.completo = 1 ) MAX ON MAX.ID = C.id
      JOIN com P ON C.producto = P.clavei
      JOIN cia E ON 1 = 1
      JOIN variables VAR ON 1 = 1
      LEFT JOIN variables_corporativo VARC ON VARC.llave = 'url_fact_online'
      LEFT JOIN cli ON C.cliente = cli.id
      LEFT JOIN formas_de_pago FP ON FP.id = C.id
