SELECT
    cli.id ID,
    nombre NOMBRE,
    ( CASE WHEN v.encrypt THEN deencrypt_data( direccion ) ELSE direccion END ) DIRECCION,
    ( CASE WHEN v.encrypt THEN deencrypt_data( colonia ) ELSE colonia END ) COLONIA,
    ( CASE WHEN v.encrypt THEN deencrypt_data( municipio ) ELSE municipio END ) MUNICIPIO,
    alias ALIAS,
    ( CASE WHEN v.encrypt THEN deencrypt_data( telefono ) ELSE telefono END ) TELEFONO,
    activo ACTIVO,
    contacto CONTACTO,
    observaciones OBSERVACIONES,
    tipodepago TIPODEPAGO,
    limite LIMITE,
    rfc RFC,
    codigo CODIGO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( correo ) ELSE correo END ) CORREO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( numeroext ) ELSE numeroext END ) NUMEROEXT,
    ( CASE WHEN v.encrypt THEN deencrypt_data( numeroint ) ELSE numeroint END ) NUMEROINT,
    enviarcorreo ENVIARCORREO,
    ( CASE WHEN v.encrypt THEN deencrypt_data( cuentaban ) ELSE cuentaban END ) CUENTABAN,
    estado ESTADO,
    formadepago FORMADEPAGO,
    correo2 CORREO2,
    puntos PUNTOS,
    cia CIA,
    ( CASE 
      WHEN uc.ucorporativo AND ac.acorporativo THEN '1'
      ELSE cli.autorizaCorporativo
    END ) CORPORATIVO,
    IFNULL( cxc.ABONOS, 0 ) AS ABONOS,
    IFNULL( cxc.CARGOS, 0 )+IFNULL( PROCESO.IMPORTE, 0 ) CARGOS,
    ( CASE 
        WHEN tipodepago = 'Contado' OR tipodepago = 'Puntos' THEN 'N'
        ELSE 'S' 
    END ) CHECK_PARAMETERS,
    ( CASE 
        WHEN tipodepago = 'Prepago' OR tipodepago = 'Pospago' OR tipodepago = 'Credito' THEN 'S'
        ELSE 'N' 
    END ) CHECK_IMPORTES,
    ( CASE 
        WHEN tipodepago = 'Prepago' OR tipodepago = 'Credito' OR tipodepago = 'Pospago' THEN 'S'
        ELSE 'N' 
    END ) CHECK_BALANCE,
    ( CASE 
        WHEN tipodepago = 'Prepago' THEN 
            ROUND( IFNULL( cxc.ABONOS, 0 )-IFNULL( cxc.CARGOS, 0 )-IFNULL( PROCESO.IMPORTE, 0 ), 2 ) 
        WHEN tipodepago = 'Pospago' OR tipodepago = 'Credito' THEN
            ROUND( IFNULL( cli.limite, 0 )+IFNULL( cxc.ABONOS, 0 )-IFNULL( cxc.CARGOS, 0 )-IFNULL( PROCESO.IMPORTE, 0 ), 2) 
        ELSE 0
    END ) SALDO
FROM cli
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'encrypt_fields' ), '0' ) = '1' encrypt )  v ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'uso_corporativo' ), '0' ) = '1' ucorporativo ) uc ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'autorizacion_corporativo' ), 'N' ) = 'S' acorporativo ) ac ON TRUE
LEFT JOIN (
SELECT
        cliente,
        SUM( IF( tm = 'H', importe, 0 ) ) AS ABONOS, SUM( IF( tm = 'C', importe, 0 )  ) CARGOS
  FROM cxc
  WHERE cxc.cliente = $CLIENTE
) cxc ON cli.id = cxc.cliente
LEFT JOIN
      ( SELECT
            id_cliente cliente,
            SUM( autorizado ) IMPORTE
       FROM bitacora_autorizaciones
       WHERE id_cliente = $CLIENTE AND status = 0
) PROCESO ON PROCESO.cliente = cli.id
WHERE cli.id = $CLIENTE
