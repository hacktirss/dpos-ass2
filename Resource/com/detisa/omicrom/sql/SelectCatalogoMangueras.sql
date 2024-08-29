SELECT
      man.isla ISLA,
      man.posicion POSICION,
      manguera MANGUERA,
      producto PRODUCTO,
      descripcion DESCRIPCION,
      manguera MANF,
      man.inventario INV
FROM man, man_pro, com
WHERE man.posicion = man_pro.posicion 
AND man.activo = 'Si' 
AND man_pro.activo = 'Si' 
AND com.activo = 'Si' 
AND com.clavei = man_pro.producto 
ORDER BY POSICION, MANGUERA