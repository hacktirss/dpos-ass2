SELECT
      mp.id MAN_ID,
      mp.dispensario DISPENSARIO,
      mp.posicion POSICION,
      mp.manguera MANGUERA,
      mp.dis_mang DIS_MANG,
      mp.producto PRODUCTO,
      mp.isla ISLA,
      mp.activo ACTIVO,
      mp.factor * mp.enable FACTOR,
      mp.enable ENABLE,
      man.despachador VENDEDOR,
      mp.tanque,
      mp.totalizadorV,
      mp.totalizador$,
      IF( marca IN ( 'G', 'T', 'B' ) AND hw_version = 'V7', 1, 0 ) ACCEPT_FULL
FROM man_pro mp
JOIN man ON man.posicion = mp.posicion
WHERE mp.activo = 'Si'
AND mp.manguera = $MANGUERA
AND mp.posicion = $POSICION