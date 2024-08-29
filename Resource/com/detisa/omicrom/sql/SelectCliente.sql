SELECT
    C.id ID,
    C.nombre NOMBRE,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.direccion ) ELSE C.direccion END ) DIRECCION,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.colonia ) ELSE C.colonia END ) COLONIA,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.municipio ) ELSE C.municipio END ) MUNICIPIO,
    C.alias "ALIAS",
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.telefono ) ELSE C.telefono END ) TELEFONO,
    C.activo ACTIVO,
    C.contacto CONTACTO,
    C.observaciones OBSERVACIONES,
    C.tipodepago TIPODEPAGO,
    C.limite LIMITE,
    C.rfc RFC,
    C.codigo CODIGO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.correo ) ELSE C.correo END ) CORREO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.numeroext ) ELSE C.numeroext END ) NUMEROEXT,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.numeroint ) ELSE C.numeroint END ) NUMEROINT,
    C.enviarcorreo ENVIARCORREO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( C.cuentaban ) ELSE C.cuentaban END ) CUENTABAN,
    C.estado ESTADO,
    LOWER( C.formadepago ) FORMADEPAGO,
    C.correo2 CORREO2,
    C.puntos PUNTOS,
    C.cia CIA,
    ( CASE WHEN C.ID=NULL THEN 'N' ELSE 'S' END ) UPUNTOS,
    IFNULL( P.acumulado, 0 ) - IFNULL( P.consumido, 0 ) SPUNTOS,
    ( CASE 
        WHEN uc.ucorporativo AND ac.acorporativo THEN '1'
        ELSE C.autorizaCorporativo
      END ) CORPORATIVO
FROM cli C
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'encrypt_fields' ), '0' ) = '1' encrypt ) v ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'uso_corporativo' ), '0' ) = '1' ucorporativo ) uc ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'autorizacion_corporativo' ), 'N' ) = 'S' acorporativo ) ac ON TRUE
LEFT JOIN saldopuntos P ON P.cliente = C.id
WHERE C.id = $CLIENTE