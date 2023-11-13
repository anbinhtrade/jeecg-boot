package org.jeecg.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.dto.DataLogDTO;
import org.jeecg.common.api.dto.OnlineAuthDTO;
import org.jeecg.common.api.dto.message.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.desensitization.util.SensitiveInfoUtil;
import org.jeecg.common.system.vo.*;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Servitization SystemMODULE Requests For External Interfaces
 * @author: jeecg-boot
 */
@Slf4j
@RestController
@RequestMapping("/sys/api")
public class SystemApiController {

    @Autowired
    private SysBaseApiImpl sysBaseApi;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * Send a system message
     * @param message Use the constructor to assign parameters 如果不设置category(消息类型)则默认为2If you do not set category (message type), it defaults to 2 Send a system message
     */
    @PostMapping("/sendSysAnnouncement")
    public void sendSysAnnouncement(@RequestBody MessageDTO message){
        sysBaseApi.sendSysAnnouncement(message);
    }

    /**
     * Send a message Comes with business parameters
     * @param message Use the constructor to assign parameters
     */
    @PostMapping("/sendBusAnnouncement")
    public void sendBusAnnouncement(@RequestBody BusMessageDTO message){
        sysBaseApi.sendBusAnnouncement(message);
    }

    /**
     * Send a message through a template
     * @param message Use the constructor to assign parameters
     */
    @PostMapping("/sendTemplateAnnouncement")
    public void sendTemplateAnnouncement(@RequestBody TemplateMessageDTO message){
        sysBaseApi.sendTemplateAnnouncement(message);
    }

    /**
     * Send a message through a template Comes with business parameters
     * @param message Use the constructor to assign parameters
     */
    @PostMapping("/sendBusTemplateAnnouncement")
    public void sendBusTemplateAnnouncement(@RequestBody BusTemplateMessageDTO message){
        sysBaseApi.sendBusTemplateAnnouncement(message);
    }

    /**
     * Use the Message Center template to generate push content
     * @param templateDTO Use the constructor to assign parameters
     * @return
     */
    @PostMapping("/parseTemplateByCode")
    public String parseTemplateByCode(@RequestBody TemplateDTO templateDTO){
        return sysBaseApi.parseTemplateByCode(templateDTO);
    }

    /**
     * Message Reads is modified based on the service type, bus type, and service bus ID
     */
    @GetMapping("/updateSysAnnounReadFlag")
    public void updateSysAnnounReadFlag(@RequestParam("busType") String busType, @RequestParam("busId")String busId){
        sysBaseApi.updateSysAnnounReadFlag(busType, busId);
    }

    /**
     * Query user information based on user account
     * @param username
     * @return
     */
    @GetMapping("/getUserByName")
    public LoginUser getUserByName(@RequestParam("username") String username){
        LoginUser loginUser = sysBaseApi.getUserByName(username);
        //用户信息加密
        try {
            SensitiveInfoUtil.handlerObject(loginUser, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return loginUser;
    }

    /**
     * Query user information based on user ID
     * @param id
     * @return
     */
    @GetMapping("/getUserById")
    LoginUser getUserById(@RequestParam("id") String id){
        LoginUser loginUser = sysBaseApi.getUserById(id);
        //Encryption of user information
        try {
            SensitiveInfoUtil.handlerObject(loginUser, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return loginUser;
    }

    /**
     * Query the set of roles by using the user account
     * @param username
     * @return
     */
    @GetMapping("/getRolesByUsername")
    List<String> getRolesByUsername(@RequestParam("username") String username){
        return sysBaseApi.getRolesByUsername(username);
    }

    /**
     * You can query department collections by user account
     * @param username
     * @return 部门 id
     */
    @GetMapping("/getDepartIdsByUsername")
    List<String> getDepartIdsByUsername(@RequestParam("username") String username){
        return sysBaseApi.getDepartIdsByUsername(username);
    }

    /**
     * Query the department through the user account name
     * @param username
     * @return 部门 name
     */
    @GetMapping("/getDepartNamesByUsername")
    List<String> getDepartNamesByUsername(@RequestParam("username") String username){
        return sysBaseApi.getDepartNamesByUsername(username);
    }


    /**
     * Get a data dictionary
     * @param code
     * @return
     */
    @GetMapping("/queryDictItemsByCode")
    List<DictModel> queryDictItemsByCode(@RequestParam("code") String code){
        return sysBaseApi.queryDictItemsByCode(code);
    }

    /**
     * Get a valid data dictionary
     * @param code
     * @return
     */
    @GetMapping("/queryEnableDictItemsByCode")
    List<DictModel> queryEnableDictItemsByCode(@RequestParam("code") String code){
        return sysBaseApi.queryEnableDictItemsByCode(code);
    }


    /** Query all parent dictionaries, sorted by create time */
    @GetMapping("/queryAllDict")
    List<DictModel> queryAllDict(){
//        try{
//            //睡10秒，gateway网关5秒超时，会触发熔断降级操作
//            Thread.sleep(10000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        log.info("--I'm a jeecg-system service node, and the microservice interface query All Dict is called--");
        return sysBaseApi.queryAllDict();
    }

    /**
     * Query all classification dictionaries
     * @return
     */
    @GetMapping("/queryAllSysCategory")
    List<SysCategoryModel> queryAllSysCategory(){
        return sysBaseApi.queryAllSysCategory();
    }


    /**
     * Inquire about all departments AS DICTIONARY INFORMATION id -->value,departName -->text
     * @return
     */
    @GetMapping("/queryAllDepartBackDictModel")
    List<DictModel> queryAllDepartBackDictModel(){
        return sysBaseApi.queryAllDepartBackDictModel();
    }

    /**
     * Get all the characters With ginseng
     * roleIds The role is selected by default
     * @return
     */
    @GetMapping("/queryAllRole")
    public List<ComboModel> queryAllRole(@RequestParam(name = "roleIds",required = false)String[] roleIds){
        if(roleIds==null || roleIds.length==0){
            return sysBaseApi.queryAllRole();
        }else{
            return sysBaseApi.queryAllRole(roleIds);
        }
    }

    /**
     * Query the set of role IDs by using the user account
     * @param username
     * @return
     */
    @GetMapping("/getRoleIdsByUsername")
    public List<String> getRoleIdsByUsername(@RequestParam("username")String username){
        return sysBaseApi.getRoleIdsByUsername(username);
    }

    /**
     * Search for department ID by department number
     * @param orgCode
     * @return
     */
    @GetMapping("/getDepartIdsByOrgCode")
    public String getDepartIdsByOrgCode(@RequestParam("orgCode")String orgCode){
        return sysBaseApi.getDepartIdsByOrgCode(orgCode);
    }

    /**
     * Inquire about all departments
     * @return
     */
    @GetMapping("/getAllSysDepart")
    public List<SysDepartModel> getAllSysDepart(){
        return sysBaseApi.getAllSysDepart();
    }

    /**
     * ACCORDING TO id Query the data stored in the database DynamicDataSourceModel
     *
     * @param dbSourceId
     * @return
     */
    @GetMapping("/getDynamicDbSourceById")
    DynamicDataSourceModel getDynamicDbSourceById(@RequestParam("dbSourceId")String dbSourceId){
        return sysBaseApi.getDynamicDbSourceById(dbSourceId);
    }



    /**
     * Obtain the department head based on the department ID
     * @param deptId
     * @return
     */
    @GetMapping("/getDeptHeadByDepId")
    public List<String> getDeptHeadByDepId(@RequestParam("deptId") String deptId){
        return sysBaseApi.getDeptHeadByDepId(deptId);
    }

    /**
     * Find the parent department
     * @param departId
     * @return
     */
    @GetMapping("/getParentDepartId")
    public DictModel getParentDepartId(@RequestParam("departId")String departId){
        return sysBaseApi.getParentDepartId(departId);
    }

    /**
     * ACCORDING TO code Query the data stored in the database DynamicDataSourceModel
     *
     * @param dbSourceCode
     * @return
     */
    @GetMapping("/getDynamicDbSourceByCode")
    public DynamicDataSourceModel getDynamicDbSourceByCode(@RequestParam("dbSourceCode") String dbSourceCode){
        return sysBaseApi.getDynamicDbSourceByCode(dbSourceCode);
    }

    /**
     * Send a message to a specified user
     * @param userIds
     * @param cmd
     */
    @GetMapping("/sendWebSocketMsg")
    public void sendWebSocketMsg(String[] userIds, String cmd){
        sysBaseApi.sendWebSocketMsg(userIds, cmd);
    }


    /**
     * Get all participating users based on their IDs
     * userIds
     * @return
     */
    @GetMapping("/queryAllUserByIds")
    public List<UserAccountInfo> queryAllUserByIds(@RequestParam("userIds") String[] userIds){
        return sysBaseApi.queryAllUserByIds(userIds);
    }

    /**
     * Query all users RETURN ComboModel
     * @return
     */
    @GetMapping("/queryAllUserBackCombo")
    public List<ComboModel> queryAllUserBackCombo(){
        return sysBaseApi.queryAllUserBackCombo();
    }

    /**
     * Paginate the user RETURN JSONObject
     * @return
     */
    @GetMapping("/queryAllUser")
    public JSONObject queryAllUser(@RequestParam(name="userIds",required=false)String userIds, @RequestParam(name="pageNo",required=false) Integer pageNo,@RequestParam(name="pageSize",required=false) int pageSize){
        return sysBaseApi.queryAllUser(userIds, pageNo, pageSize);
    }



    /**
     * Push the meeting check-in information to the preview
     * userIds
     * @return
     * @param userId
     */
    @GetMapping("/meetingSignWebsocket")
    public void meetingSignWebsocket(@RequestParam("userId")String userId){
        sysBaseApi.meetingSignWebsocket(userId);
    }

    /**
     * Get all participating users based on name
     * userNames
     * @return
     */
    @GetMapping("/queryUserByNames")
    public List<UserAccountInfo> queryUserByNames(@RequestParam("userNames")String[] userNames){
        return sysBaseApi.queryUserByNames(userNames);
    }

    /**
     * Get a collection of the user's roles
     * @param username
     * @return
     */
    @GetMapping("/getUserRoleSet")
    public Set<String> getUserRoleSet(@RequestParam("username")String username){
        return sysBaseApi.getUserRoleSet(username);
    }

    /**
     * Get a user's set of permissions
     * @param username
     * @return
     */
    @GetMapping("/getUserPermissionSet")
    public Set<String> getUserPermissionSet(@RequestParam("username") String username){
        return sysBaseApi.getUserPermissionSet(username);
    }

    //-----

    /**
     * Check whether you have the online permission
     * @param onlineAuthDTO
     * @return
     */
    @PostMapping("/hasOnlineAuth")
    public boolean hasOnlineAuth(@RequestBody OnlineAuthDTO onlineAuthDTO){
        return sysBaseApi.hasOnlineAuth(onlineAuthDTO);
    }

    /**
     * Query user role information
     * @param username
     * @return
     */
    @GetMapping("/queryUserRoles")
    public Set<String> queryUserRoles(@RequestParam("username") String username){
        return sysUserService.getUserRolesSet(username);
    }


    /**
     * Query user permission information
     * @param username
     * @return
     */
    @GetMapping("/queryUserAuths")
    public Set<String> queryUserAuths(@RequestParam("username") String username){
        return sysUserService.getUserPermissionsSet(username);
    }

    /**
     * Obtain all department information by department ID
     */
    @GetMapping("/selectAllById")
    public SysDepartModel selectAllById(@RequestParam("id") String id){
        return sysBaseApi.selectAllById(id);
    }

    /**
     * Query all user IDs of the company to which the user belongs based on the user ID
     * @param userId
     * @return
     */
    @GetMapping("/queryDeptUsersByUserId")
    public List<String> queryDeptUsersByUserId(@RequestParam("userId") String userId){
        return sysBaseApi.queryDeptUsersByUserId(userId);
    }


    /**
     * Query data permissions
     * @return
     */
    @GetMapping("/queryPermissionDataRule")
    public List<SysPermissionDataRuleModel> queryPermissionDataRule(@RequestParam("component") String component, @RequestParam("requestPath")String requestPath, @RequestParam("username") String username){
        return sysBaseApi.queryPermissionDataRule(component, requestPath, username);
    }

    /**
     * Query user information
     * @param username
     * @return
     */
    @GetMapping("/getCacheUser")
    public SysUserCacheInfo getCacheUser(@RequestParam("username") String username){
        return sysBaseApi.getCacheUser(username);
    }

    /**
     * Translation of ordinary dictionaries
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/translateDict")
    public String translateDict(@RequestParam("code") String code, @RequestParam("key") String key){
        return sysBaseApi.translateDict(code, key);
    }


    /**
     * 36. According to multiple user accounts (comma separated), multiple user information is returned for query
     * @param usernames
     * @return
     */
    @RequestMapping("/queryUsersByUsernames")
    List<JSONObject> queryUsersByUsernames(@RequestParam("usernames") String usernames){
        return this.sysBaseApi.queryUsersByUsernames(usernames);
    }

    /**
     * 37 Query returns multiple user information based on multiple user IDs (separated by commas).
     * @param ids
     * @return
     */
    @RequestMapping("/queryUsersByIds")
    List<JSONObject> queryUsersByIds(@RequestParam("ids") String ids){
        return this.sysBaseApi.queryUsersByIds(ids);
    }

    /**
     * 38 According to the multiple department codes (comma separated), the query returns multiple department information
     * @param orgCodes
     * @return
     */
    @GetMapping("/queryDepartsByOrgcodes")
    List<JSONObject> queryDepartsByOrgcodes(@RequestParam("orgCodes") String orgCodes){
        return this.sysBaseApi.queryDepartsByOrgcodes(orgCodes);
    }

    /**
     * 39 According to the multiple department IDs (comma separated), the query returns multiple department information
     * @param ids
     * @return
     */
    @GetMapping("/queryDepartsByIds")
    List<JSONObject> queryDepartsByIds(@RequestParam("ids") String ids){
        return this.sysBaseApi.queryDepartsByIds(ids);
    }

    /**
     * 40 Send mail messages
     * @param email
     * @param title
     * @param content
     */
    @GetMapping("/sendEmailMsg")
    public void sendEmailMsg(@RequestParam("email")String email,@RequestParam("title")String title,@RequestParam("content")String content){
         this.sysBaseApi.sendEmailMsg(email,title,content);
    };
    /**
     * 41 Obtain information about the company's subordinate departments and all users under the company
     * @param orgCode
     */
    @GetMapping("/getDeptUserByOrgCode")
    List<Map> getDeptUserByOrgCode(@RequestParam("orgCode")String orgCode){
       return this.sysBaseApi.getDeptUserByOrgCode(orgCode);
    }

    /**
     * Query classification dictionary translations
     *
     * @param ids Classification dictionary table ID
     * @return
     */
    @GetMapping("/loadCategoryDictItem")
    public List<String> loadCategoryDictItem(@RequestParam("ids") String ids) {
        return sysBaseApi.loadCategoryDictItem(ids);
    }

    /**
     * Load the dictionary text based on the dictionary code
     *
     * @param dictCode ORDER：tableName,text,code
     * @param keys     The key to be queried
     * @return
     */
    @GetMapping("/loadDictItem")
    public List<String> loadDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("keys") String keys) {
        return sysBaseApi.loadDictItem(dictCode, keys);
    }

    /**
     * Copy all the dictionaries under the app to the new tenant
     *
     * @param originalAppId Original low-code app ID
     * @param appId         New low-code app IDs
     * @param tenantId      The new tenant ID
     * @return Map<String, String>  Map<Original dictionary encoding, New dictionary encoding>
     */
    @GetMapping("/sys/api/copyLowAppDict")
    Map<String, String> copyLowAppDict(@RequestParam("originalAppId") String originalAppId, @RequestParam("appId") String appId, @RequestParam("tenantId") String tenantId) {
        return sysBaseApi.copyLowAppDict(originalAppId, appId, tenantId);
    }
    
    /**
     * Query dictionary entries based on the dictionary code
     *
     * @param dictCode ORDER：table Name,text,code
     * @param dictCode The key to be queried
     * @return
     */
    @GetMapping("/getDictItems")
    public List<DictModel> getDictItems(@RequestParam("dictCode") String dictCode) {
        return sysBaseApi.getDictItems(dictCode);
    }

    /**
     * Query multiple dictionary entries based on multiple dictionary codes
     *
     * @param dictCodeList
     * @return key = dictCode ； value=corresponding dictionary item
     */
    @RequestMapping("/getManyDictItems")
    public Map<String, List<DictModel>> getManyDictItems(@RequestParam("dictCodeList") List<String> dictCodeList) {
        return sysBaseApi.getManyDictItems(dictCodeList);
    }

    /**
     * 【Drop-down search】
     * Dictionary table of large data volumes Asynchronous loading is used, i.e., the front-end input content filters the data
     *
     * @param dictCode Dictionary code format: table, text, code
     * @param keyword  Filter keywords
     * @return
     */
    @GetMapping("/loadDictItemByKeyword")
    public List<DictModel> loadDictItemByKeyword(@RequestParam("dictCode") String dictCode, @RequestParam("keyword") String keyword, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return sysBaseApi.loadDictItemByKeyword(dictCode, keyword, pageSize);
    }

    /**
     * 48 The translation of a common dictionary is based on multiple dict codes and multiple pieces of data, which are separated by commas
     * @param dictCodes
     * @param keys
     * @return
     */
    @GetMapping("/translateManyDict")
    public Map<String, List<DictModel>> translateManyDict(@RequestParam("dictCodes") String dictCodes, @RequestParam("keys") String keys){
        return this.sysBaseApi.translateManyDict(dictCodes, keys);
    }


    /**
     * Obtain a table data dictionary [Interface Signature Verification]
     * @param tableFilterSql The table name can have a where condition
     * @param text
     * @param code
     * @return
     */
    @GetMapping("/queryTableDictItemsByCode")
    List<DictModel> queryTableDictItemsByCode(@RequestParam("tableFilterSql") String tableFilterSql, @RequestParam("text") String text, @RequestParam("code") String code){
        return sysBaseApi.queryTableDictItemsByCode(tableFilterSql, text, code);
    }

    /**
     * Query table dictionary Data can be filtered [Interface Signature Verification]
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    @GetMapping("/queryFilterTableDictInfo")
    List<DictModel> queryFilterTableDictInfo(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("filterSql") String filterSql){
        return sysBaseApi.queryFilterTableDictInfo(table, text, code, filterSql);
    }

    /**
     * [Interface Signature Verification]
     * Query for a specified table text code Get a dictionary containing text and value
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Deprecated
    @GetMapping("/queryTableDictByKeys")
    public List<String> queryTableDictByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keyArray") String[] keyArray){
        return sysBaseApi.queryTableDictByKeys(table, text, code, keyArray);
    }


    /**
     * Dictionary table Translation [API Signature Verification]
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/translateDictFromTable")
    public String translateDictFromTable(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("key") String key){
        return sysBaseApi.translateDictFromTable(table, text, code, key);
    }


    /**
     * [Interface Signature Verification]
     * 49 Dictionary table Translation, can be in batches
     *
     * @param table
     * @param text
     * @param code
     * @param keys  Multiple are separated by commas
     * @return
     */
    @GetMapping("/translateDictFromTableByKeys")
    public List<DictModel> translateDictFromTableByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keys") String keys) {
        return this.sysBaseApi.translateDictFromTableByKeys(table, text, code, keys);
    }

    /**
     * Send template information
     * @param message
     */
    @PostMapping("/sendTemplateMessage")
    public void sendTemplateMessage(@RequestBody MessageDTO message){
        sysBaseApi.sendTemplateMessage(message);
    }

    /**
     * Get the message template content
     * @param code
     * @return
     */
    @GetMapping("/getTemplateContent")
    public String getTemplateContent(@RequestParam("code") String code){
        return this.sysBaseApi.getTemplateContent(code);
    }

    /**
     * Save a data log
     * @param dataLogDto
     */
    @PostMapping("/saveDataLog")
    public void saveDataLog(@RequestBody DataLogDTO dataLogDto){
        this.sysBaseApi.saveDataLog(dataLogDto);
    }

    @PostMapping("/addSysFiles")
    public void addSysFiles(@RequestBody SysFilesModel sysFilesModel){this.sysBaseApi.addSysFiles(sysFilesModel);}

    @GetMapping("/getFileUrl")
    public String getFileUrl(@RequestParam(name="fileId") String fileId){
        return this.sysBaseApi.getFileUrl(fileId);
    }

    /**
     * Update your profile picture
     * @param loginUser
     * @return
     */
    @PutMapping("/updateAvatar")
    public void updateAvatar(@RequestBody LoginUser loginUser){
        this.sysBaseApi.updateAvatar(loginUser);
    }

    /**
     * TO THE APP WebSocket pushes chat refresh messages
     * @param userId
     * @return
     */
    @GetMapping("/sendAppChatSocket")
    public void sendAppChatSocket(@RequestParam(name="userId") String userId){
        this.sysBaseApi.sendAppChatSocket(userId);
    }

    /**
     * You can query the role information based on the role code, which can be separated by commas
     *
     * @param roleCodes
     * @return
     */
    @GetMapping("/queryRoleDictByCode")
    public List<DictModel> queryRoleDictByCode(@RequestParam(name = "roleCodes") String roleCodes) {
        return this.sysBaseApi.queryRoleDictByCode(roleCodes);
    }

    /**
     * Get the message template content
     * @param id
     * @return
     */
    @GetMapping("/getRoleCode")
    public String getRoleCode(@RequestParam("id") String id){
        return this.sysBaseApi.getRoleCodeById(id);
    }
    
    /**
     * VUEN-2584【issue】There are several problems with the SQL injection vulnerability of the platform
     * Some special functions You can mix the query results with error messages, exposing the information in the database
     * @param e
     * @return
     */
    @ExceptionHandler(java.sql.SQLException.class)
    public Result<?> handleSQLException(Exception e){
        String msg = e.getMessage();
        String extractvalue = "extractvalue";
        String updatexml = "updatexml";
        if(msg!=null && (msg.toLowerCase().indexOf(extractvalue)>=0 || msg.toLowerCase().indexOf(updatexml)>=0)){
            return Result.error("The verification fails, and the SQL parsing is abnormal!");
        }
        return Result.error("The verification fails, and the SQL parsing is abnormal!" + msg);
    }

    /**
     * Query users based on advanced query criteria
     * @param superQuery
     * @param matchType
     * @return
     */
    @GetMapping("/queryUserBySuperQuery")
    public List<JSONObject> queryUserBySuperQuery(@RequestParam("superQuery")  String superQuery, @RequestParam("matchType") String matchType) {
        return sysBaseApi.queryUserBySuperQuery(superQuery,matchType);
    }

    /**
     * Query users based on ID conditions
     * @param id
     * @return
     */
    @GetMapping("/queryUserById")
    public JSONObject queryUserById(@RequestParam("id")  String id) {
        return sysBaseApi.queryUserById(id);
    }

    /**
     * Search for departments based on advanced search criteria
     * @param superQuery
     * @param matchType
     * @return
     */
    @GetMapping("/queryDeptBySuperQuery")
    public List<JSONObject> queryDeptBySuperQuery(@RequestParam("superQuery")  String superQuery, @RequestParam("matchType") String matchType) {
        return sysBaseApi.queryDeptBySuperQuery(superQuery,matchType);
    }

    /**
     * Query roles based on advanced query criteria
     * @param superQuery
     * @param matchType
     * @return
     */
    @GetMapping("/queryRoleBySuperQuery")
    public List<JSONObject> queryRoleBySuperQuery(@RequestParam("superQuery")  String superQuery, @RequestParam("matchType") String matchType) {
        return sysBaseApi.queryRoleBySuperQuery(superQuery,matchType);
    }


    /**
     * Query the user ID based on the tenant ID
     * @param tenantId 租户ID
     * @return List<String>
     */
    @GetMapping("/selectUserIdByTenantId")
    public List<String> selectUserIdByTenantId(@RequestParam("tenantId")  String tenantId) {
        return sysBaseApi.selectUserIdByTenantId(tenantId);
    }


    /**
     * Query user IDs based on department IDs
     * @param deptIds
     * @return
     */
    @GetMapping("/sys/api/queryUserIdsByDeptIds")
    public List<String> queryUserIdsByDeptIds(@RequestParam("deptIds") List<String> deptIds){
        return sysBaseApi.queryUserIdsByDeptIds(deptIds);
    }
    
    /**
     * Query user IDs based on department IDs
     * @param deptIds
     * @return
     */
    @GetMapping("/sys/api/queryUserAccountsByDeptIds")
    public List<String> queryUserAccountsByDeptIds(@RequestParam("deptIds") List<String> deptIds){
        return sysBaseApi.queryUserAccountsByDeptIds(deptIds);
    }

    /**
     * Coded according to the role Query the user ID
     * @param roleCodes
     * @return
     */
    @GetMapping("/sys/api/queryUserIdsByRoleds")
    public List<String> queryUserIdsByRoleds(@RequestParam("roleCodes")  List<String> roleCodes){
        return sysBaseApi.queryUserIdsByRoleds(roleCodes);
    }

    /**
     * Query user IDs based on job IDs
     * @param positionIds
     * @return
     */
    @GetMapping("/sys/api/queryUserIdsByPositionIds")
    public List<String> queryUserIdsByPositionIds(@RequestParam("positionIds") List<String> positionIds){
        return sysBaseApi.queryUserIdsByPositionIds(positionIds);
    }


    /**
     * Based on all user accounts under the department and sub-department
     *
     * @param orgCode Department code
     * @return
     */
    @GetMapping("/sys/api/getUserAccountsByDepCode")
    public List<String> getUserAccountsByDepCode(String orgCode){
        return sysBaseApi.getUserAccountsByDepCode(orgCode);
    }

    /**
     * Check whether the tables and fields of the query SQL statement are in the whitelist
     *
     * @param selectSql
     * @return
     */
    @GetMapping("/sys/api/dictTableWhiteListCheckBySql")
    public boolean dictTableWhiteListCheckBySql(@RequestParam("selectSql") String selectSql) {
        return sysBaseApi.dictTableWhiteListCheckBySql(selectSql);
    }

    /**
     * Check whether the dictionary is in the whitelist based on the dictionary table or dictionary encoding
     *
     * @param tableOrDictCode Table name or dict code
     * @param fields          If you are passing a dict code, this parameter must be null
     * @return
     */
    @GetMapping("/sys/api/dictTableWhiteListCheckByDict")
    public boolean dictTableWhiteListCheckByDict(
            @RequestParam("tableOrDictCode") String tableOrDictCode,
            @RequestParam(value = "fields", required = false) String[] fields
    ) {
        return sysBaseApi.dictTableWhiteListCheckByDict(tableOrDictCode, fields);
    }

}
