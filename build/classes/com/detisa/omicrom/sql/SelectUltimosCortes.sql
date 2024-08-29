
SELECT 
    CTD.id, 
    TRUNCATE(TRUNCATE(SUM(monto), 0)/1000, 2) as monto
FROM (
    (SELECT
        id,
        '1' manguera,
        posicion,
        SUM(fmonto1*1000-imonto1*1000) monto
    FROM ctd
    GROUP BY id)
UNION ALL
    (SELECT
        id,
        '1' manguera,
        posicion,
        SUM(fmonto2*1000-imonto2*1000) monto
    FROM ctd
    GROUP BY id)
UNION ALL
    (SELECT
        id,
        '1' manguera,
        posicion,
        SUM(fmonto3*1000-imonto3*1000) monto
    FROM ctd
    GROUP BY id)
) CTD
JOIN ct ON ct.id = CTD.id
JOIN man_pro MANGUERON CTD.posicion = MANGUERAS.posicion AND CTD.manguera = MANGUERAS.manguera
JOIN com PROD ON PROD.clavei = MANGUERAS.producto
WHERE ct.isla = $ISLA
AND MANGUERAS.activo = 'Si'
AND PROD.activo = 'Si'
AND monto > 0
GROUP BY 1
ORDER BY 1 DESC
LIMIT 5