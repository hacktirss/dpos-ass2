SELECT 
    isla ISLA,
    descripcion DESCRIPCION,
    turno TURNO,
    activo ACTIVO,
    status STATUS,
    corte CORTE
FROM islas
WHERE isla = $ISLA

