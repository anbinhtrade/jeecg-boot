package org.jeecg.config.firewall.SqlInjection.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.exception.JeecgSqlInjectionException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.sqlparse.JSqlParserUtils;
import org.jeecg.common.util.sqlparse.vo.SelectSqlInfo;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.config.firewall.SqlInjection.IDictTableWhiteListHandler;
import org.jeecg.config.firewall.interceptor.LowCodeModeInterceptor;
import org.jeecg.modules.system.entity.SysTableWhiteList;
import org.jeecg.modules.system.security.DictQueryBlackListHandler;
import org.jeecg.modules.system.service.ISysTableWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.util.*;

/**
 * For the whitelist processing of general cases, if there are cases that cannot be handled, you can write a separate implementation class
 */
@Slf4j
@Component("dictTableWhiteListHandlerImpl")
public class DictTableWhiteListHandlerImpl implements IDictTableWhiteListHandler {

    /**
     * key—the name of the table
     * value-Field name, separated by multiple commas
     * Two configurations -- all in lowercase
     * whiteTablesRuleMap.put("sys_user", "*")  sys_user All fields can be queried
     * whiteTablesRuleMap.put("sys_user", "username,password")  The username and password in sys user can be queried
     */
    private static final Map<String, String> whiteTablesRuleMap = new HashMap<>();
    /**
     * LowCode Whether it's dev mode
     */
    private static Boolean LOW_CODE_IS_DEV = null;


    @Autowired
    private ISysTableWhiteListService sysTableWhiteListService;
    @Autowired
    private JeecgBaseConfig jeecgBaseConfig;

    
    /**
     * INITIALIZE whiteTablesRuleMap METHOD
     */
    private void init() {
        // If the current dev mode is used, the database is queried every time to prevent caching
        if (this.isDev()) {
            DictTableWhiteListHandlerImpl.whiteTablesRuleMap.clear();
        }
        // If the map is empty, it is queried from the database
        if (DictTableWhiteListHandlerImpl.whiteTablesRuleMap.isEmpty()) {
            Map<String, String> ruleMap = sysTableWhiteListService.getAllConfigMap();
            log.info("Table Dictionary Whitelist Initialization Completed: {}", ruleMap);
            DictTableWhiteListHandlerImpl.whiteTablesRuleMap.putAll(ruleMap);
        }
    }

    @Override
    public boolean isPassBySql(String sql) {
        Map<String, SelectSqlInfo> parsedMap = null;
        try {
            parsedMap = JSqlParserUtils.parseAllSelectTable(sql);
        } catch (Exception e) {
            log.warn("Verify the SQL statement and parse the error：{}", e.getMessage());
        }
        // 如果sql有问题，则肯定执行不了，所以直接返回true
        if (parsedMap == null) {
            return true;
        }
        log.info("Obtain the Select SQL information ：{} ", parsedMap);
        // 遍历当前sql中的所有表名，如果有其中一个表或表的字段不在白名单中，则不通过
        for (Map.Entry<String, SelectSqlInfo> entry : parsedMap.entrySet()) {
            SelectSqlInfo sqlInfo = entry.getValue();
            if (sqlInfo.isSelectAll()) {
                log.warn("The query statement contains an * field, which is passed for the time being");
                continue;
            }
            Set<String> queryFields = sqlInfo.getAllRealSelectFields();
            // 校验表名和字段是否允许查询
            String tableName = entry.getKey();
            if (!this.checkWhiteList(tableName, queryFields)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPassByDict(String dictCodeString) {
        if (oConvertUtils.isEmpty(dictCodeString)) {
            return true;
        }
        try {
            // 针对转义字符进行解码
            dictCodeString = URLDecoder.decode(dictCodeString, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.throwException("The dictionary code failed to be decoded, and it may have used illegal characters！");
        }
        dictCodeString = dictCodeString.trim();
        String[] arr = dictCodeString.split(SymbolConstant.COMMA);
        // 获取表名
        String tableName = this.getTableName(arr[0]);
        // 获取查询字段
        arr = Arrays.copyOfRange(arr, 1, arr.length);
        // distinct的作用是去重，相当于 Set<String>
        String[] fields = Arrays.stream(arr).map(String::trim).distinct().toArray(String[]::new);
        // 校验表名和字段是否允许查询
        return this.isPassByDict(tableName, fields);
    }

    @Override
    public boolean isPassByDict(String tableName, String... fields) {
        if (oConvertUtils.isEmpty(tableName)) {
            return true;
        }
        if (fields == null || fields.length == 0) {
            fields = new String[]{"*"};
        }
        String sql = "select " + String.join(",", fields) + " from " + tableName;
        log.info("Dictionary concatenation query SQL: {}", sql);
        try {
            // 进行SQL解析
            JSqlParserUtils.parseSelectSqlInfo(sql);
        } catch (Exception e) {
            // 如果SQL解析失败，则通过字段名和表名进行校验
            return checkWhiteList(tableName, new HashSet<>(Arrays.asList(fields)));
        }
        // 通过SQL解析进行校验，可防止SQL注入
        return this.isPassBySql(sql);
    }

    /**
     * 校验表名和字段是否在白名单内
     *
     * @param tableName
     * @param queryFields
     * @return
     */
    public boolean checkWhiteList(String tableName, Set<String> queryFields) {
        this.init();
        // 1、判断“表名”是否通过校验，如果为空则未通过校验
        if (oConvertUtils.isEmpty(tableName)) {
            log.error("Whitelist check: The table name is empty");
            this.throwException();
        }
        // 统一转成小写
        tableName = tableName.toLowerCase();
        String allowFieldStr = DictTableWhiteListHandlerImpl.whiteTablesRuleMap.get(tableName);
        log.info("checkWhiteList tableName: {}", tableName);
        if (oConvertUtils.isEmpty(allowFieldStr)) {
            // 如果是dev模式，自动向数据库里添加数据
            if (this.isDev()) {
                this.autoAddWhiteList(tableName, String.join(",", queryFields));
                allowFieldStr = DictTableWhiteListHandlerImpl.whiteTablesRuleMap.get(tableName);
            } else {
                // prod模式下，直接抛出异常
                log.error("白名单校验：表\"{}\"未通过校验", tableName);
                this.throwException();
            }
        }
        // 2、判断“字段名”是否通过校验
        // 统一转成小写
        allowFieldStr = allowFieldStr.toLowerCase();
        Set<String> allowFields = new HashSet<>(Arrays.asList(allowFieldStr.split(",")));
        // 需要合并的字段
        Set<String> waitMergerFields = new HashSet<>();
        for (String field : queryFields) {
            if(oConvertUtils.isEmpty(field)){
                continue;
            }
            // 统一转成小写
            field = field.toLowerCase();
            // 如果允许的字段里不包含查询的字段，则直接抛出异常
            if (!allowFields.contains(field)) {
                // 如果是dev模式，记录需要合并的字段
                if (this.isDev()) {
                    waitMergerFields.add(field);
                } else {
                    log.error("白名单校验：字段 {} 不在 {} 范围内，拒绝访问！", field, allowFields);
                    this.throwException();
                }
            }
        }
        // 自动向数据库中合并未通过的字段
        if (!waitMergerFields.isEmpty()) {
            this.autoAddWhiteList(tableName, String.join(",", waitMergerFields));
        }
        log.info("Whitelist verification: query table \"{}\", query field {} passed the verification", tableName, queryFields);
        return true;
    }

    /**
     * 自动添加白名单，如果数据库已有，则字段会自动合并
     *
     * @param tableName
     * @param allowFieldStr
     */
    private void autoAddWhiteList(String tableName, String allowFieldStr) {
        try {
            SysTableWhiteList entity = sysTableWhiteListService.autoAdd(tableName, allowFieldStr);
            DictTableWhiteListHandlerImpl.whiteTablesRuleMap.put(tableName, entity.getFieldName());
            log.warn("表\"{}\"未通过校验，且当前为 dev 模式，已自动向数据库中增加白名单数据。查询字段：{}", tableName, allowFieldStr);
        } catch (Exception e) {
            log.error("表\"{}\"未通过校验，且当前为 dev 模式，但自动向数据库中增加白名单数据失败，请排查后重试。错误原因：{}", tableName, e.getMessage(), e);
            this.throwException();
        }
    }

    /**
     * 判断当前 LowCode 是否为 dev 模式
     */
    private boolean isDev() {
        if (DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV == null) {
            if (this.jeecgBaseConfig.getFirewall() != null) {
                String lowCodeMode = this.jeecgBaseConfig.getFirewall().getLowCodeMode();
                DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV = LowCodeModeInterceptor.LOW_CODE_MODE_DEV.equals(lowCodeMode);
            } else {
                // 如果没有 firewall 配置，则默认为 false
                DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV = false;
            }
        }
        return DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV;
    }

    @Override
    public boolean clear() {
        DictTableWhiteListHandlerImpl.whiteTablesRuleMap.clear();
        return true;
    }

    
    /**
     * 取where前面的为：table name
     *
     * @param str
     * @see DictQueryBlackListHandler#getTableName(String)
     */
    @SuppressWarnings("JavadocReference")
    private String getTableName(String str) {
        String[] arr = str.split("\\s+(?i)where\\s+");
        String tableName = arr[0].trim();
        //【20230814】解决使用参数tableName=sys_user t&复测，漏洞仍然存在
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length()).trim();
        }
        if (tableName.contains(" ")) {
            tableName = tableName.substring(0, tableName.indexOf(" ")).trim();
        }

        //【issues/4393】 sys_user , (sys_user), sys_user%20, %60sys_user%60
        String reg = "\\s+|\\(|\\)|`";
        return tableName.replaceAll(reg, "");
    }

    private void throwException() throws JeecgSqlInjectionException {
        this.throwException(this.getErrorMsg());
    }

    private void throwException(String message) throws JeecgSqlInjectionException {
        if (oConvertUtils.isEmpty(message)) {
            message = this.getErrorMsg();
        }
        log.error(message);
        throw new JeecgSqlInjectionException(message);
    }

    @Override
    public String getErrorMsg() {
        return "白名单校验未通过！";
    }

}
