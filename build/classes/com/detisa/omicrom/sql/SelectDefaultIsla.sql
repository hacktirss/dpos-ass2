SELECT isla
    FROM (
        SELECT islas.isla, count(*) posiciones
        FROM man_pro
        JOIN islas ON islas.isla = man_pro.isla
        WHERE islas.activo = 'Si' AND man_pro.activo = 'Si'
        GROUP BY islas.isla
    ) SUB
WHERE SUB.posiciones > 0