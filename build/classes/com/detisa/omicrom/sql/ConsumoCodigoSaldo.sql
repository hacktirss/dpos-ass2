SELECT 
    UCASE( u.codigo ), u.periodo, u.importe, u.litros, 
    u.importe consumo_permitido,
    u.litros volumen_permitido,
    0.00 consumo_periodo, 
    0.00 volumen_periodo,
    IF( u.importe = 0, 'Litros', 'Pesos' ) tipo_limite
FROM unidades u
WHERE u.codigo = '$CODIGO';
