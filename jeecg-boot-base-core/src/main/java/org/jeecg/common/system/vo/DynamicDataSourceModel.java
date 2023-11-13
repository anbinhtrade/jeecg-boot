package org.jeecg.common.system.vo;

import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Description: Data sources
 * @author: jeecg-boot
 */
@Data
public class DynamicDataSourceModel {

    public DynamicDataSourceModel() {

    }

    public DynamicDataSourceModel(Object dbSource) {
        if (dbSource != null) {
            BeanUtils.copyProperties(dbSource, this);
        }
    }

    /**
     * id
     */
    private java.lang.String id;
    /**
     * Data source encoding
     */
    private java.lang.String code;
    /**
     * Database type
     */
    private java.lang.String dbType;
    /**
     * Driver class
     */
    private java.lang.String dbDriver;
    /**
     * The address of the data source
     */
    private java.lang.String dbUrl;

//    /**
//     * The name of the database
//     */
//    private java.lang.String dbName;

    /**
     * USERNAME
     */
    private java.lang.String dbUsername;
    /**
     * PASSWORD
     */
    private java.lang.String dbPassword;

}