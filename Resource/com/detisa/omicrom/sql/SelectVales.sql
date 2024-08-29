SELECT 
    id ID,
    idnvo IDNVO,
    importe IMPORTE,
    vigente VIGENTE,
    codigo CODIGO,
    ticket TICKET,
    IFNULL(importecargado, 0) ABONOS, IMPORTECARGADO
    importe-IFNULL(importecargado, 0) SALDO,
FROM boletos
WHERE TRIM(CODIGO) IN ($CODIGOS)
ORDER BY idnvo