SELECT
      inv.id ID,
      inv.codigo CODIGO,
      inv.descripcion DESCRIPCION,
      inv.umedida UMEDIDA,
      inv.rubro RUBRO,
      inv.activo ACTIVO,
      inv.existencia EXALMACEN,
      inv.costo COSTO,
      inv.precio PRECIO,
      IFNULL( invd.existencia, 0 ) EXISLA,
      IFNULL( invd.posicion, 0 ) POSICION,
      IFNULL( invd.inventario, 'No') HAS_INVENTARIO,
      IFNULL( invd.isla_pos, 0 ) ISLA,
      IFNULL( invd.despachador, 0 ) VENDEDOR
FROM inv
LEFT JOIN (
    SELECT  invd.id,
            invd.existencia,
            man.isla_pos,
            man.posicion,
            man.despachador,
            man.inventario inventario
    FROM invd 
    LEFT JOIN man ON man.isla_pos = invd.isla_pos
    WHERE man.activo = 'Si'
) invd ON invd.id = inv.id AND invd.posicion = $POSICION
WHERE inv.codigo = '$CODIGO'
      AND inv.rubro IN ( 'Aceites', 'Seguro', 'Servicio' )
      AND inv.activo = 'Si'