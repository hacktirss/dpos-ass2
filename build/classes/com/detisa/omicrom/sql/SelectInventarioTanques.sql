SELECT
      T.tanque TANQUE,
      P.descripcion DESCRIPCION,
      T.clave_producto CLAVE_PRODUCTO,
      T.volumen_actual VOLUMEN_ACTUAL,
      T.volumen_compensado VOLUMEN_COMPENSADO,
      T.volumen_faltante VOLUMEN_POR_LLENAR,
      ROUND( ( T.volumen_actual+T.volumen_faltante )*0.95, 0 )-T.volumen_actual 95_VOLUMEN_POR_LLENAR,
      T.capacidad_total CAPACIDAD_TOTAL,
      DATE_FORMAT(T.fecha_hora_s,'%d/%m/%Y %H:%i:%s') FECHA_HORA_S,
      DATE_FORMAT(now(),'%d/%m/%Y %H:%i:%s') FECHA_HORA,
      T.temperatura TEMPERATURA,
      T.agua AGUA,
      T.volumen_operativo VOLUMEN_OPERATIVO,
      T.altura ALTURA
FROM
      com P
      JOIN tanques T ON T.clave_producto = P.clave
WHERE P.activo = 'Si' AND T.estado = 1
ORDER BY T.tanque
