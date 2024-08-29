SELECT 
    id ID,
    clave CLAVE,
    clavei CLAVEI,
    descripcion DESCRIPCION,
    precio PRECIO,
    activo ACTIVO,
    iva IVA,
    ieps IEPS
FROM com
WHERE clavei = '$CLAVEI'

