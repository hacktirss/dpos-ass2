SELECT
      id ID,
      descripcion DESCRIPCION,
      umedida UMEDIDA,
      rubro RUBRO,
      activo ACTIVO,
      existencia EXISTENCIA,
      costo COSTO,
      precio PRECIO
FROM inv
WHERE
      rubro = 'Aceites'
      AND activo = 'Si'
