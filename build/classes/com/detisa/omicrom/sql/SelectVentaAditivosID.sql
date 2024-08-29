SELECT
    V.id,
    V.clave,
    V.cantidad,
    IFNULL( un.nombre, 'Pieza' ) unidad,
    V.unitario,
    V.total,
    V.corte,
    V.posicion,
    DATE_FORMAT( V.fecha,'%d/%m/%Y %H:%i:%s' ) FECHA_VENTA,
    DATE_FORMAT( NOW(),'%d/%m/%Y %H:%i:%s' ) FECHA_IMPRESION,
    V.descripcion,
    V.cliente,
    V.vendedor,
    V.referencia,
    VAR.msgfae FACT_MENSAJE,
    FAEURL QRC_FAE,
    FURL FACT_LIGA,
    'ORIGINAL' TIPO_IMPRESION,
    ( CASE WHEN V.tm != 'C' OR VAR.fae = 'No' OR ( V.cliente > 0 AND C.tipodepago NOT REGEXP 'Tarjeta|Monedero' ) THEN 'No' ELSE 'Si' END ) FACTURACION,
    SUBSTR( UPPER( SHA1( CONCAT( '|', LPAD( V.id, 7, '0' ), '|', LPAD( V.posicion, 2, '0' ), '|', LPAD( E.idfae, 5, '0' ), '|', DATE_FORMAT( V.fecha, '%Y-%m-%dT%H:%i:%s' ), '|', CAST( ROUND( V.cantidad, 4 ) AS DECIMAL( 10, 4 ) ), '|', CAST( ROUND( V.total, 2 ) AS DECIMAL( 10, 2 ) ), '|' ) ) ), 1, 23 ) FOLIO_FAE
FROM vtaditivos V
JOIN inv ON inv.id = V.clave
LEFT JOIN cfdi33_c_unidades un ON un.clave = inv.umedida
JOIN cia E ON TRUE
JOIN cli C ON C.id = V.cliente
JOIN variables VAR ON 1 = 1
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'url_fact_online' ), 'https://omicrom.com.mx/GlobalFAE/rfc.php?ticket=' ) FAEURL ) urlfae ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'url_facturacion' ), 'omicrom.com.mx/GlobalFAE' ) FURL ) url ON TRUE
WHERE 
    V.id = $ID