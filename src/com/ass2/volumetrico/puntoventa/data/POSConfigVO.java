/*
 * POSConfigVO
 * ASS2PuntoVenta®
 * © 2014, ASS2 
 * http://www.ass2.com.mx
 * @author Rolando Esquivel Villafaña, Softcoatl
 * @version 1.0
 * @since may 2014
 */
package com.ass2.volumetrico.puntoventa.data;

import com.softcoatl.database.entity.DBField;
import com.softcoatl.database.entity.DBMapping;
import com.softcoatl.database.entity.vo.BaseVO;
import com.softcoatl.utils.CollectionsUtils;
import com.softcoatl.data.DinamicVO;
import com.softcoatl.utils.FileUtils;
import com.softcoatl.utils.logging.LogManager;
import java.sql.Types;
import java.util.Map;

public class POSConfigVO extends BaseVO {

    public static final String ENTITY_NAME = "pos_config";
    private byte [] dataBuffer;

    public static enum FILETYPES {

        bin,
        image,
        font,
        plain
    }

    public static enum FIELDS {

        id,
        pos_model,
        pos_id,
        description,
        filetype,
        location,
        filename,
        status
    }

   private static final int[] FLAGS = new int [] {DBField.PRIMARY_KEY,
                                                 0,
                                                 0,
                                                 0,
                                                 0,
                                                 0,
                                                 0,
                                                 0};

    private static final int[] TYPES = new int[]{Types.NUMERIC,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR};

    public static final DBMapping MAPPING = new DBMapping(ENTITY_NAME, CollectionsUtils.fncsEnumAsArray(FIELDS.class),TYPES,FLAGS);

    public POSConfigVO() {
        super(true);
    }

    public POSConfigVO(DinamicVO<String, String> vo) {
        super(vo);
        LogManager.debug("POSConfigVO[" + this + "]");
    }

    public POSConfigVO(Map<String, String> fields) {
        super(fields, true);
        LogManager.debug("POSConfigVO[" + this + "]");
    }
    
    @Override
    public String getEntity() {
        return ENTITY_NAME;
    }

   @Override
    public DBMapping getMapping() {
        return MAPPING;
    }
    

    public String getID() {
        return NVL(FIELDS.id.name());
    }

    public void setID(String id) {
        setField(FIELDS.id, id);
    }
    
    public String getPOSModel() {
        return NVL(FIELDS.pos_model.name());
    }
    public void setPOSModel(String posModel) {
        setField(FIELDS.pos_model, posModel);
    }
    
    public String getPOSID() {
        return NVL(FIELDS.pos_id.name());
    }
    public void setPOSID(String posID) {
        setField(FIELDS.pos_id, posID);
    }
    
    public String getDescription() {
        return NVL(FIELDS.description.name());
    }
    public void setDescription(String description) {
        setField(FIELDS.description, description);
    }
    
    public String getFileType() {
        return NVL(FIELDS.filetype.name());
    }
    public void setFileType(String fileType) {
        setField(FIELDS.filetype, fileType);
    }

    public String getFileName() {
        return NVL(FIELDS.filename.name());
    }
    public void setFileName(String fileName) {
        setField(FIELDS.filename, fileName);
    }
    
    public String getLocation() {
        return FileUtils.getUnixStyleFilePath(NVL(FIELDS.location.name()));
    }

    public void setLocation(String location) {
        setField(FIELDS.location, FileUtils.getUnixStyleFilePath(location));
    }
    
    public String getStatus() {
        return NVL(FIELDS.status.name());
    }
    public void setStatus(String status) {
        setField(FIELDS.status, status);
    }
    
    public boolean isActive() {
        return "A".equals(getStatus());
    }
    public String getAbsoluteName() {
        return getLocation() + getFileName();
    }

    public byte [] getDataBuffer() {
        return dataBuffer;
    }
    public void setDataBuffer(byte [] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }
}//POSConfigVO
