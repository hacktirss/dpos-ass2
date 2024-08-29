SELECT
    v.version_id VERSION_ID,
    v.major_version MAJOR,
    v.minor_version MINOR,
    IFNULL( v.build, 0 ) BUILD,
    IFNULL( v.revision, 0 ) REVISION,
    CONCAT_WS('.', v.major_version, v.minor_version, IFNULL( v.build, 0 ), IFNULL( v.revision, 0 ) ) SVERSION, 
    v.location BIN_FILE,
    v.md5 MD5
FROM 
    versioning v
JOIN pos_version pv ON v.version_id = pv.version_id
JOIN pos_catalog pc ON pc.pos_id = pv.pos_id
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'actualiza_pos' ), '0' ) actualiza ) variables ON TRUE
WHERE variables.actualiza = '1'
    AND pv.status = 'P'
    AND pc.serial = '$POS_ID'
    AND pc.appVersion < CONCAT_WS('.', v.major_version, v.minor_version, IFNULL( v.build, 0 ), IFNULL( v.revision, 0 ) ) 
    AND current_date >= v.release_date
    AND current_date <= v.eol_date
    AND current_date >= pv.schedulled_date
