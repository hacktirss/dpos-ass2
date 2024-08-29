SELECT
        id CLI_ID,
        nombre NOMBRE,
        ( CASE WHEN v.encrypt THEN deencrypt_data( direccion ) ELSE direccion END ) DIRECCION,
        ( CASE WHEN v.encrypt THEN deencrypt_data( colonia ) ELSE colonia END ) COLONIA,
        ( CASE WHEN v.encrypt THEN deencrypt_data( municipio ) ELSE municipio END ) MUNICIPIO,
        "alias" "ALIAS",
        telefono TELEFONO,
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
        IFNULL( cxc.ABONOS, 0 ) ABONOS,
        IFNULL( cxc.CARGOS, 0 ) CARGOS,
        IFNULL( cxc.CARGOS, 0 )-IFNULL( cxc.ABONOS, 0 ) SALDO
FROM cli
LEFT JOIN (
SELECT
        cliente,
        SUM( IF( tm = 'H', importe, 0 ) ) AS ABONOS, SUM( IF( tm = 'C', importe, 0 )  ) CARGOS
  FROM cxc
  WHERE cxc.cliente = $CLI_ID
) cxc ON cli.id = cxc.cliente
LEFT JOIN
      ( SELECT
            id_cliente cliente,
            SUM( autorizado ) IMPORTE
       FROM bitacora_autorizaciones
       WHERE id_cliente = $CLI_ID AND status = 0
) PROCESO ON PROCESO.cliente = cli.id
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'encrypt_fields' ), '0' ) = '1' encrypt ) v ON TRUE
WHERE cli.id = $CLI_ID
ORDER BY ABONOS.cliente
