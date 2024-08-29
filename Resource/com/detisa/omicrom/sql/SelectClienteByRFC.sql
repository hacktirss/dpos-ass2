SELECT
    C.id ID,
    C.nombre NOMBRE,
    C.tipodepago TIPODEPAGO,
    C.rfc RFC,
    LOWER(C.formadepago) FORMADEPAGO
FROM cli C
WHERE C.rfc = '$RFC'