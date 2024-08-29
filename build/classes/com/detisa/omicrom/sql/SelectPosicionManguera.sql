SELECT
      man_pro.id MAN_ID,
      man_pro.dispensario DISPENSARIO,
      man_pro.posicion POSICION,
      man_pro.manguera MANGUERA,
      ( CASE
            WHEN UPPER( variables.Dispensarios ) LIKE 'WAYNE' AND UPPER( man.lado ) = 'A' THEN
                  man_pro.manguera+4
            ELSE
                  man_pro.manguera 
      END ) MANGUERA_INT,
      man_pro.dis_mang DIS_MANG,
      man_pro.producto PRODUCTO,
      man_pro.isla ISLA,
      man_pro.activo ACTIVO,
      ep.estado ESTADO,
      man_pro.factor FACTOR,
      man_pro.enable ENABLE,
      variables.Clave cDispensarios,
      variables.Dispensarios,
      man.hw_version HW,
      man.lado,
      IF( marca IN ( 'G', 'T', 'B' ) AND hw_version = 'V7', 1, 0 ) acceptFull
FROM man_pro
JOIN man on man.posicion = man_pro.posicion 
JOIN estado_posiciones ep ON ep.posicion = man.posicion
JOIN (
    SELECT v.llave_lista_valor Clave, v.valor_lista_valor Dispensarios 
    FROM listas l JOIN listas_valor v ON ( l.id_lista = v.id_lista_lista_valor ) 
    WHERE l.nombre_lista = 'MARCA DISPENSARIOS' ) variables ON variables.Clave = man.marca
WHERE man_pro.activo = 'Si'
AND man.activo = 'Si'
AND man_pro.posicion = $POSICION
AND man_pro.manguera = $MANGUERA
