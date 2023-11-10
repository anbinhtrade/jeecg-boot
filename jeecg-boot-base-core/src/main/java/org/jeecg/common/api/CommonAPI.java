package org.jeecg.common.api;

import org.jeecg.common.system.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic APIs
 * @author: jeecg-boot
 */
public interface CommonAPI {

    /**
     * 1. Query user role information
     * @param username
     * @return
     */
    Set<String> queryUserRoles(String username);


    /**
     * 2. Query user permission information
     * @param username
     * @return
     */
    Set<String> queryUserAuths(String username);

    /**
     * 3 according to id Query the data stored in the database DynamicDataSourceModel
     *
     * @param dbSourceId
     * @return
     */
    DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId);

    /**
     * 4 according to code Query the data stored in the database DynamicDataSourceModel
     *
     * @param dbSourceCode
     * @return
     */
    DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode);

    /**
     * 5. Query user information according to user account
     * @param username
     * @return
     */
    public LoginUser getUserByName(String username);


    /**
     * 6. Dictionary table TRANSLATION
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    String translateDictFromTable(String table, String text, String code, String key);

    /**
     * 7. Translation of common dictionaries
     * @param code
     * @param key
     * @return
     */
    String translateDict(String code, String key);

    /**
     * 8. Permission to query data
     * @param component SUBASSEMBLY
     * @param username USERNAME
     * @param requestPath The address of the previous request
     * @return
     */
    List<SysPermissionDataRuleModel> queryPermissionDataRule(String component, String requestPath, String username);


    /**
     * 9. Query user information
     * @param username
     * @return
     */
    SysUserCacheInfo getCacheUser(String username);

    /**
     * 10. Get a data dictionary
     * @param code
     * @return
     */
    public List<DictModel> queryDictItemsByCode(String code);

    /**
     * Get a valid data dictionary entry
     * @param code
     * @return
     */
    public List<DictModel> queryEnableDictItemsByCode(String code);

    /**
     * 13. Get a table data dictionary
     * @param tableFilterSql
     * @param text
     * @param code
     * @return
     */
    List<DictModel> queryTableDictItemsByCode(String tableFilterSql, String text, String code);

    /**
     * 14 The translation of a common dictionary is based on multiple dict codes and multiple pieces of data, which are separated by commas
     * @param dictCodes 例如：user_status,sex
     * @param keys 例如：1,2,0
     * @return
     */
    Map<String, List<DictModel>> translateManyDict(String dictCodes, String keys);

    /**
     * 15 Dictionary table Translation, can be in batches
     * @param table
     * @param text
     * @param code
     * @param keys Multiple are separated by commas
     * @return
     */
    List<DictModel> translateDictFromTableByKeys(String table, String text, String code, String keys);

}
