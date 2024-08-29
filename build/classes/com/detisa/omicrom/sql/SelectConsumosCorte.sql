SELECT
      CONSUMO.id TR,
      CONSUMO.posicion POSICION,
      CONSUMO.vendedor VENDEDOR,
      CONSUMO.manguera MANGUERA,
      CONSUMO.producto PRODUCTO,
      CONSUMO.volumen VOLUMEN,
      CONSUMO.pesos PESOS,
      ROUND(CONSUMO.pesos*CONSUMO.iva, 2) IVA,
      ROUND(CONSUMO.pesos-(CONSUMO.pesos*CONSUMO.iva), 2) IMPORTE,
      CONSUMO.precio PRECIO,
      CONSUMO.ieps IEPS,
      DATE_FORMAT(CONSUMO.fin_venta,'%d/%m/%Y %H:%i:%s') FIN_VENTA,
      DATE_FORMAT(NOW(),'%d/%m/%Y %H:%i:%s') FECHA_IMPRESION,
      CONSUMO.comprobante+1 COMPROBANTE,
      CONSUMO.cliente CLIENTE,
      CONSUMO.placas PLACAS,
      CONSUMO.codigo CODIGO,
      PRODUCTOS.clave CLAVE,
      PRODUCTOS.descripcion DESCRIPCION,
      cli.nombre NOMBRE,
      cli.tipodepago TIPODEPAGO,
      cli.ID CLI_ID,
      CONCAT(LPAD(CONSUMO.id, 7, '0'), LPAD(CONSUMO.posicion, 2, '0'), LPAD(CONSUMO.manguera, 2, '0'), ESTACION.numestacion) NUM_TICKET,
      ( CASE
         WHEN CONSUMO.comprobante=0 THEN 'ORIGINAL'
         ELSE 'COPIA'
      END ) TIPO_IMPRESION
FROM
      rm CONSUMO
      JOIN com PRODUCTOS ON CONSUMO.producto = PRODUCTOS.clavei
      JOIN cia ESTACION ON 1 = 1
      LEFT JOIN cli ON CONSUMO.cliente = cli.id
WHERE CONSUMO.corte = $CORTE
AND CONSUMO.posicion = $POSICION
AND CONSUMO.manguera = $MANGUERA
