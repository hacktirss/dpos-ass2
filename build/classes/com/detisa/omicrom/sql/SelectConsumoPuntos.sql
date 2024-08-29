SELECT
        C.id TR,
        C.posicion POSICION,
        C.manguera MANGUERA,
        C.producto PRODUCTO,
        C.pesos PESOS,
        P.clave CLAVE,
        P.descripcion DESCRIPCION,
        DATE_FORMAT( NOW(), '%d/%m/%Y' ) FECHA_IMPRESION,
        DATE( C.fin_venta ) FECHA_CONSUMO,
        DATE_FORMAT( DATE_ADD( C.fin_venta, INTERVAL ppv.vigencia DAY ), '%d/%m/%Y' ) FECHA_VIGENCIA,
        ppa.pp * IF( IFNULL( rmp.id, 0 )=0, 1, 0 ) * IF ( DATE( fin_venta ) BETWEEN DATE( ppb.start ) AND DATE( IF( ppc.end = '', DATE_FORMAT( NOW(), "%Y-%m-%d" ), ppc.end ) ), 1, 0 ) * (
                ( CASE WHEN ppe.tipo = 'V' 
                    THEN ROUND( ppd.valor * C.volumen, 4 )
                    ELSE ROUND( ppd.valor * C.pesos, 4 )
                END )
        ) PPT
FROM (
        SELECT
                rm.id,
                rm.posicion,
                rm.manguera,
                rm.fin_venta,
                ROUND( rm.volumen, 4 ) volumen,
                ROUND( rm.pesos, 2 ) pesos,
                rm.producto,
                rm.comprobante
        FROM rm ) C
JOIN (
        SELECT MAX( rm.id ) ID
        FROM rm
        WHERE rm.posicion = $POSICION AND rm.completo = 1) MAX ON MAX.ID = C.id
JOIN com P ON C.producto = P.clavei
LEFT JOIN rm_puntos rmp ON C.id = rmp.id 
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_enabled' ), '0' ) pp ) ppa ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_inicio' ), DATE_FORMAT( NOW(), "%Y-%m-%d" ) ) start ) ppb ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_fin' ), DATE_FORMAT( NOW(), "%Y-%m-%d" ) ) end ) ppc ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_promo' ), '0.1' ) valor ) ppd ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_tipo' ), 'I' ) tipo ) ppe ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_vigencia' ), '30' ) vigencia ) ppv ON TRUE
