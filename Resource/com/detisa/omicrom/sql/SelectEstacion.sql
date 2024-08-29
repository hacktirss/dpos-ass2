SELECT
      cia.cia CIA,
      cia.direccion DIRECCION,
      cia.numeroext NUMEROEXT,
      cia.numeroint NUMEROINT,
      cia.colonia COLONIA,
      cia.ciudad CUIDAD,
      cia.estado ESTADO,
      cia.telefono TELEFONO,
      cia.desgloce DESGLOCE,
      cia.iva IVA,
      cia.rfc RFC,
      cia.codigo CODIGO_POSTAL,
      cia.estacion ESTACION,
      cia.factor FACTOR,
      CONCAT('CRE-', cu.permiso ) NUMESTACION,
      '00' RUBRO,
      cia.numestacion LNUMESTACION,
      cia.serie SERIE,
      cia.zonahoraria ZONAHORARIA,
      variables.fae FACTURACION,
      variables.uso_de_corporativo CORPORATIVO,
      variables.nipticket TICKETNIP,
      variables.nipterminal PIDENIP,
      variables.vtaditivos ADITIVOS,
      cu.permiso PERMISO,
      conf.pwd_conf_pos CONFNIP,
      fact.pwd_fact_pos FACTNIP,
      ( CASE WHEN variables.odometro='S' THEN 'Si' ELSE 'No' END ) PIDEODOMETRO,
      ( CASE WHEN variables.adtBarcode='S' THEN 'Si' ELSE 'No' END ) BARCODE,
      ( CASE WHEN fp.posFormaPago = '1' THEN 'Si' ELSE 'No' END ) PIDEFORMAPAGO,
      ( CASE WHEN fo.factOmicrom = '1' THEN 'Si' ELSE 'No' END ) FACTOMICROM,
      ( CASE WHEN pb.presetbancos = '1' THEN 'Si' ELSE 'No' END ) PRESETBANCOS,     
      ( CASE WHEN pm.pidemanguera = '1' THEN 'Si' ELSE 'No' END ) PIDEMANGUERA,
      ( CASE WHEN ask.print = '1' THEN 'Si' ELSE 'No' END ) ASKPRINT,
      ( CASE WHEN ppa.pp = '1' THEN 'Si' ELSE 'No' END ) PUNTOS
FROM cia
JOIN variables on TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'fact_password_pos' ),  '0' ) pwd_fact_pos ) fact ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'pwd_conf_pos' ),  '347568' ) pwd_conf_pos ) conf ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'pos_formaPago' ),  '0' ) posFormaPago ) fp ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'fact_online_omicrom' ),  '1' ) factOmicrom ) fo ON TRUE
JOIN ( SELECT IFNULL( ( SELECT permiso FROM permisos_cre WHERE catalogo =  'VARIABLES_EMPRESA' AND llave = 'PERMISO_CRE' ), ""  ) permiso ) cu ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'pos_presetbancos' ),  '1' ) presetbancos ) pb ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave =  'pos_pidemanguera' ),  '1' ) pidemanguera ) pm ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'pp_enabled' ), '0' ) pp ) ppa ON TRUE
JOIN ( SELECT IFNULL( ( SELECT valor FROM variables_corporativo WHERE llave = 'ask_print' ), '0' ) print ) ask ON TRUE
