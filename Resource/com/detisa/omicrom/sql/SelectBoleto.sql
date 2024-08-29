SELECT
    boletos.id ID,
    boletos.idnvo IDNVO,
    boletos.secuencia SECUENCIA,
    boletos.codigo CODIGO,
    boletos.importe IMPORTE,
    boletos.vigente VIGENTE,
    boletos.ticket TICKET,
    boletos.ticket2 TICKET2,
    boletos.importe1 IMPORTE1,
    boletos.importe2 IMPORTE2,
    boletos.importecargado IMPORTECARGADO,
    ROUND( boletos.importe-boletos.importecargado, 2 ) IMPORTEDISPONIBLE,
    genbol.cliente CLIENTE
FROM boletos
JOIN genbol ON genbol.id = boletos.id
WHERE codigo IN ( $BOLETO )
ORDER BY idnvo