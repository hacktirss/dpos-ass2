SELECT
        C.id TR,
        'D' TIPO,
        C.posicion POSICION,
        C.manguera MANGUERA,
        C.producto PRODUCTO,
        IF( C.tipo_venta = 'D' AND ABS( C.diferencia ) > 0.02, ROUND( ( C.importe + C.diferencia )/( C.preciouu ), 4 ), C.volumen ) VOLUMEN,
        C.pesos PESOS,
        C.importeiva TIVA,
        C.importeieps TIEPS,
        C.importe + IF( IFNULL( desgloseieps, 'N' ) = 'S', 0.00, importeieps ) + IF( C.tipo_venta = 'D' AND ABS( C.diferencia ) > 0.02, C.diferencia, 0.00 ) IMPORTE,
        C.precio PRECIO,
        C.preciouu + ROUND( IF( IFNULL( desgloseieps, 'N' ) = 'S', 0.0000, C.ieps ), 4 ) PRECIOU,
        C.iva IVA,
        C.ieps IEPS,
        DATE_FORMAT( C.fin_venta, '%d/%m/%Y %H:%i:%s' ) FIN_VENTA,
        DATE_FORMAT( NOW(), '%d/%m/%Y %H:%i:%s' ) FECHA_IMPRESION,
        C.comprobante+1 COMPROBANTE,
        C.enviado ENVIADO,
        C.cliente CLIENTE,
        C.placas PLACAS,
        IFNULL( C.codigo, '' ) CODIGO,
        C.kilometraje ODOMETRO,
        C.kilometrajea ODOMETROA,
        ROUND( ( C.kilometraje - C.kilometrajea ) / IF( C.tipo_venta = 'D' AND ABS( C.diferencia ) > 0.02, ROUND( ( C.importe + C.diferencia )/( C.preciouu ), 4 ), C.volumen ), 2 ) RENDIMIENTO,
        P.clave CLAVE,
        P.descripcion DESCRIPCION,
        cli.nombre NOMBRE,
        LOWER( IF( cli.tipodepago IS NULL OR TRIM( cli.tipodepago ) = '', 'Contado', cli.tipodepago ) ) TIPODEPAGO,
        cli.ID CLI_ID,
        ( CASE WHEN cli.tipodepago REGEXP 'prepago|credito' AND psc.saldo = '1' THEN 1 ELSE 0 END ) PRINT_SALDO,
        ( CASE WHEN cli.tipodepago REGEXP 'prepago|credito' THEN 1 ELSE 0 END ) FIRMA,
        ( CASE WHEN C.tipo_venta != 'D' 
                    OR VAR.fae = 'No'
                    OR ( cli.id > 0 AND (cli.tipodepago REGEXP 'Credito|Prepago|Monedero|Vales' OR cli.formadepago NOT REGEXP '01|04|28' ) )
                THEN 'No' 
                ELSE 'Si' END ) FACTURACION,
        VAR.msgfae FACT_MENSAJE,
        direccionexp EXPDIRECCION,
        numeroextexp EXPNUMEROEXT,
        numerointexp EXPNUMEROINT,
        coloniaexp EXPCOLONIA,
        ciudadexp EXPCIUDAD,
        ciudadexp EXPCUIDAD,
        estadoexp EXPESTADO,
        codigoexp EXPCODIGO_POSTAL,
        IF ( IFNULL( FP.descripcion, 'MXN' )='USD', 'S', 'N' ) MONEDA, 
        ROUND( IFNULL( FP.detalle, 1.0 ), 2 ) TIPO_CAMBIO, 
        ROUND( IFNULL( FP.monto, C.pesos ), 2 ) DOLARES, 
        ( CASE
           WHEN C.tipo_venta = 'N' THEN 'CONSIGNACION'
           WHEN C.tipo_venta = 'J' THEN 'AUTO JARREO'
           WHEN C.tipo_venta = 'A' THEN 'UVAS / PEMEX'
           WHEN C.comprobante = 0 THEN 'ORIGINAL'
           ELSE 'COPIA'
        END ) TIPO_IMPRESION,
        ppa.pp * IF( C.comprobante=0, 1, 0 ) * IF ( DATE( fin_venta ) BETWEEN DATE( ppb.start ) AND DATE( IF( ppc.end = '', DATE_FORMAT( NOW(), "%Y-%m-%d" ), ppc.end ) ), 1, 0 ) * (
                ( CASE WHEN ppe.tipo = 'V' 
                    THEN ROUND( ppd.valor * C.volumen, 4 )
                    ELSE ROUND( ppd.valor * C.pesos, 4 )
                END )
        ) PPT,
        IF( rm_transacciones.id IS NULL, '0' , '1' ) TTR,
        IF( rm_transacciones.id IS NULL, '' , rm_transacciones.transaccion ) TRJSON
FROM (
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
                rm.enviado,
                rm.cliente,
                rm.placas,
                rm.codigo,
                rm.kilometraje,
                IFNULL( rma.kilometraje, 0 ) kilometrajea,
                ROUND( (rm.precio-rm.ieps)/(1+rm.iva), 4 ) preciouu,
                ROUND( rm.volumen * ( (rm.precio-rm.ieps)/(1+rm.iva) ), 2 ) importe, 
                ROUND( rm.volumen * ( (rm.precio-rm.ieps)/(1+rm.iva) ) * rm.iva, 2 ) importeiva,
                ROUND( rm.volumen * rm.ieps, 2 ) importeieps,
                ROUND( rm.pesos, 2 )
                        -ROUND( rm.volumen * ( (rm.precio-rm.ieps)/(1+rm.iva) ), 2 )
                        -ROUND( rm.volumen * ( (rm.precio-rm.ieps)/(1+rm.iva) ) * rm.iva, 2 )
                        -ROUND( rm.volumen * rm.ieps, 2 ) diferencia
        FROM rm 
        LEFT JOIN ( SELECT MAX( id_consumo ) id, ba.codigo FROM bitacora_autorizaciones ba WHERE ba.id_consumo IS NOT NULL AND ba.id_consumo < $TR GROUP BY ba.codigo ) ba ON ba.codigo = rm.codigo 
        LEFT JOIN rm rma ON rma.id = ba.id 
        WHERE rm.id = $TR
) C
JOIN com P ON C.producto = P.clavei
JOIN cia E ON 1 = 1
JOIN variables VAR ON 1 = 1
LEFT JOIN cli ON C.cliente = cli.id
LEFT JOIN formas_de_pago FP ON FP.id = C.id
LEFT JOIN rm_transacciones ON rm_transacciones.id = C.id
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_enabled' ), '0' ) pp ) ppa ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_inicio' ), DATE_FORMAT( NOW(), "%Y-%m-%d" ) ) start ) ppb ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_fin' ), DATE_FORMAT( NOW(), "%Y-%m-%d" ) ) end ) ppc ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_promo' ), '0.1' ) valor ) ppd ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_tipo' ), 'I' ) tipo ) ppe ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pos_saldo_cliente' ), '0' ) saldo ) psc ON TRUE
