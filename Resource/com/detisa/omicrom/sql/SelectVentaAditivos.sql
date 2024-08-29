SELECT
    vta.id,
    vta.clave,
    vta.cantidad,
    vta.unitario,
    vta.total,
    vta.corte,
    vta.posicion,
    DATE_FORMAT( vta.fecha, '%d/%m/%Y %H:%i:%s' ) fecha,
    vta.descripcion,
    vta.cliente,
    vta.vendedor,
    vta.referencia,
    IFNULL( un.nombre, 'Pieza' ) unidad
FROM
    vtaditivos vta
JOIN 
	inv ON inv.id = vta.clave
LEFT JOIN 
	cfdi33_c_unidades un ON un.clave = inv.umedida
WHERE 
    referencia = $REFERENCIA