SELECT
      id ID,
      descripcion DESCRIPCION,
      umedida UMEDIDA,
      rubro RUBRO,
      activo ACTIVO,
      invd.existencia EXISTENCIA,
      costo COSTO,
      precio PRECIO
FROM inv
JOIN invd USING( id )
WHERE
      rubro = 'Aceites'
      AND activo = 'Si'
      AND invd.isla_pos = $ISLA