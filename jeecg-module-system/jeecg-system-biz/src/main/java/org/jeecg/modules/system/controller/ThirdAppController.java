package org.jeecg.modules.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeecg.dingtalk.api.core.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.constant.enums.MessageTypeEnum;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.entity.SysThirdAppConfig;
import org.jeecg.modules.system.service.ISysThirdAccountService;
import org.jeecg.modules.system.service.ISysThirdAppConfigService;
import org.jeecg.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import org.jeecg.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import org.jeecg.modules.system.vo.thirdapp.SyncInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Third-party app docking
 * @author: jeecg-boot
 */
@Slf4j
@RestController("thirdAppController")
@RequestMapping("/sys/thirdApp")
public class ThirdAppController {

    @Autowired
    ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
    @Autowired
    ThirdAppDingtalkServiceImpl dingtalkService;

    @Autowired
    private ISysThirdAppConfigService appConfigService;

    @Autowired
    private ISysThirdAccountService sysThirdAccountService;

    /**
     * Get the enabled system
     */
    @GetMapping("/getEnabledType")
    public Result getEnabledType() {
        Map<String, Boolean> enabledMap = new HashMap(5);
        //update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]Isolation through tenant mode ------------
        int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
        //Query the third-party configurations of the current tenant
        List<SysThirdAppConfig> list = appConfigService.getThirdConfigListByThirdType(tenantId);
        //Whether DingTalk is configured
        boolean dingConfig = false;
        //Whether WeCom has been configured
        boolean qywxConfig = false;
        if(null != list && list.size()>0){
            for (SysThirdAppConfig config:list) {
                if(MessageTypeEnum.DD.getType().equals(config.getThirdType())){
                    dingConfig = true;
                    continue;
                }
                if(MessageTypeEnum.QYWX.getType().equals(config.getThirdType())){
                    qywxConfig = true;
                    continue;
                }
            }
        }
        enabledMap.put("wechatEnterprise", qywxConfig);
        enabledMap.put("dingtalk", dingConfig);
        //update-end---author:wangshuai ---date:20230224  for：[QQYUN-3440]Isolation through tenant mode------------
        return Result.OK(enabledMap);
    }

    /**
     * Sync local [user] to [WeCom]
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/wechatEnterprise/user/toApp")
    public Result syncWechatEnterpriseUserToApp(@RequestParam(value = "ids", required = false) String ids) {
        //update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]Isolation through tenant mode ------------
        //Obtain the WeCom configuration
        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.QYWX.getType());
        if (null != config) {
        //update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]通过租户模式隔离 ------------
            SyncInfoVo syncInfo = wechatEnterpriseService.syncLocalUserToThirdApp(ids);
            if (syncInfo.getFailInfo().size() == 0) {
                return Result.OK("Synchronization succeeded", syncInfo);
            } else {
                return Result.error("Synchronization failed", syncInfo);
            }
        }
        return Result.error("WeCom has not been configured, please configure WeCom");
    }

    /**
     * Synchronize [WeCom] [user] to the local computer
     *
     * @param ids VOID
     * @return
     */
    @GetMapping("/sync/wechatEnterprise/user/toLocal")
    public Result syncWechatEnterpriseUserToLocal(@RequestParam(value = "ids", required = false) String ids) {
        return Result.error("Due to the adjustment of the WeCom interface, the synchronization to local function has become invalid");

//        if (thirdAppConfig.isWechatEnterpriseEnabled()) {
//            SyncInfoVo syncInfo = wechatEnterpriseService.syncThirdAppUserToLocal();
//            if (syncInfo.getFailInfo().size() == 0) {
//                return Result.OK("同步成功", syncInfo);
//            } else {
//                return Result.error("同步失败", syncInfo);
//            }
//        }
//        return Result.error("企业微信同步功能已禁用");
    }

    /**
     * Sync the local [department] to [WeCom]
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/wechatEnterprise/depart/toApp")
    public Result syncWechatEnterpriseDepartToApp(@RequestParam(value = "ids", required = false) String ids) {
        //获取企业微信配置
        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.QYWX.getType());
        if (null != config) {
            SyncInfoVo syncInfo = wechatEnterpriseService.syncLocalDepartmentToThirdApp(ids);
            if (syncInfo.getFailInfo().size() == 0) {
                return Result.OK("Synchronization succeeded", null);
            } else {
                return Result.error("Synchronization failed", syncInfo);
            }
        }
        return Result.error("WeCom has not been configured, please configure WeCom");
    }

    /**
     * Synchronize [WeCom] [Department] to the local area
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/wechatEnterprise/depart/toLocal")
    public Result syncWechatEnterpriseDepartToLocal(@RequestParam(value = "ids", required = false) String ids) {
        return Result.error("Due to the adjustment of the WeCom interface, the synchronization of the local department of WeCom is invalid");
//        //获取企业微信配置
//        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
//        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.QYWX.getType());
//        if (null != config) {
//            SyncInfoVo syncInfo = wechatEnterpriseService.syncThirdAppDepartmentToLocal(ids);
//            if (syncInfo.getFailInfo().size() == 0) {
//                return Result.OK("同步成功", syncInfo);
//            } else {
//                return Result.error("同步失败", syncInfo);
//            }
//        }
//        return Result.error("企业微信尚未配置,请配置企业微信");
    }

    /**
     * Synchronizing the local [department] to [DingTalk]
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/dingtalk/depart/toApp")
    public Result syncDingtalkDepartToApp(@RequestParam(value = "ids", required = false) String ids) {
        //获取钉钉配置
        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.DD.getType());
        if (null != config) {
            SyncInfoVo syncInfo = dingtalkService.syncLocalDepartmentToThirdApp(ids);
            if (syncInfo.getFailInfo().size() == 0) {
                return Result.OK("Synchronization succeeded", null);
            } else {
                return Result.error("Synchronization failed", syncInfo);
            }
        }
        return Result.error("DingTalk has not been configured, please configure DingTalk");
    }

//    /**
//     * 同步【钉钉】[部门]到本地
//     *
//     * @param ids
//     * @return
//     */
//   @GetMapping("/sync/dingtalk/depart/toLocal")
//    public Result syncDingtalkDepartToLocal(@RequestParam(value = "ids", required = false) String ids) {
//        //获取钉钉配置
//        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
//        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.DD.getType());
//        if (null!= config) {
//            SyncInfoVo syncInfo = dingtalkService.syncThirdAppDepartmentToLocal(ids);
//            if (syncInfo.getFailInfo().size() == 0) {
//                return Result.OK("同步成功", syncInfo);
//            } else {
//                return Result.error("同步失败", syncInfo);
//            }
//        }
//        return Result.error("钉钉尚未配置,请配置钉钉");
//    }

    /**
     * Synchronizing Local [Users] to [DingTalk]
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/dingtalk/user/toApp")
    public Result syncDingtalkUserToApp(@RequestParam(value = "ids", required = false) String ids) {
        //获取钉钉配置
        int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
        //根据租户id和第三方类别获取租户数据
        SysThirdAppConfig appConfig = appConfigService.getThirdConfigByThirdType(tenantId,MessageTypeEnum.DD.getType());
        if(null != appConfig){
            SyncInfoVo syncInfo = dingtalkService.syncLocalUserToThirdApp(ids);
            if (syncInfo.getFailInfo().size() == 0) {
                return Result.OK("Synchronization succeeded", syncInfo);
            } else {
                return Result.error("Synchronization failed", syncInfo);
            }
        }
        return Result.error("DingTalk has not been configured, please configure DingTalk");
    }

//    /**
//     * 同步【钉钉】[用户]到本地
//     *
//     * @param ids 作废
//     * @return
//     */
//    @GetMapping("/sync/dingtalk/user/toLocal")
//    public Result syncDingtalkUserToLocal(@RequestParam(value = "ids", required = false) String ids) {
//        //获取钉钉配置
//        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
//        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.DD.getType());
//        if (null != config) {
//            SyncInfoVo syncInfo = dingtalkService.syncThirdAppUserToLocal();
//            if (syncInfo.getFailInfo().size() == 0) {
//                return Result.OK("同步成功", syncInfo);
//            } else {
//                return Result.error("同步失败", syncInfo);
//            }
//        }
//        return Result.error("钉钉尚未配置,请配置钉钉");
//    }

    /**
     * Send a message test
     *
     * @return
     */
    @PostMapping("/sendMessageTest")
    public Result sendMessageTest(@RequestBody JSONObject params, HttpServletRequest request) {
        /* 获取前台传递的参数 */
        // 第三方app的类型
        String app = params.getString("app");
        // 是否发送给全部人
        boolean sendAll = params.getBooleanValue("sendAll");
        // 消息接收者，传sys_user表的username字段，多个用逗号分割
        String receiver = params.getString("receiver");
        // 消息内容
        String content = params.getString("content");
        // 租户id
        int tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);

        String fromUser = JwtUtil.getUserNameByToken(request);
        String title = "Third-party APP message testing";
        MessageDTO message = new MessageDTO(fromUser, receiver, title, content);
        message.setToAll(sendAll);
        //update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]钉钉、企业微信通过租户模式隔离 ------------
        String weChatType = MessageTypeEnum.QYWX.getType();
        String dingType = MessageTypeEnum.DD.getType();
        if (weChatType.toUpperCase().equals(app)) {
            SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, weChatType);
            if (null != config) {
        //update-end---author:wangshuai ---date:20230224  for：[QQYUN-3440]钉钉、企业微信通过租户模式隔离 ------------
                JSONObject response = wechatEnterpriseService.sendMessageResponse(message, false);
                return Result.OK(response);
            }
            return Result.error("WeCom has not been configured, please configure WeCom");
            //update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]钉钉、企业微信通过租户模式隔离 ------------
        } else if (dingType.toUpperCase().equals(app)) {
            SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, dingType);
            if (null != config) {
            //update-end---author:wangshuai ---date:20230224  for：[QQYUN-3440]钉钉、企业微信通过租户模式隔离 ------------
                Response<String> response = dingtalkService.sendMessageResponse(message, false);
                return Result.OK(response);
            }
            return Result.error("DingTalk has not been configured, please configure DingTalk");
        }
        return Result.error("Third-party apps that are not recognized");
    }

    /**
     * Retract message test
     *
     * @return
     */
    @PostMapping("/recallMessageTest")
    public Result recallMessageTest(@RequestBody JSONObject params) {
        /* 获取前台传递的参数 */
        // 第三方app的类型
        String app = params.getString("app");
        // 消息id
        String msgTaskId = params.getString("msg_task_id");
        //租户id
        int tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
        if (CommonConstant.WECHAT_ENTERPRISE.equals(app)) {
            SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.QYWX.getType());
            if (null != config) {
                return Result.error("WeCom does not support retracting messages");
            }
            return Result.error("WeCom has not been configured, please configure WeCom");
        } else if (CommonConstant.DINGTALK.equals(app)) {
            SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.DD.getType());
            if (null != config) {
                Response<JSONObject> response = dingtalkService.recallMessageResponse(msgTaskId);
                if (response.isSuccess()) {
                    return Result.OK("The withdrawal was successful", response);
                } else {
                    return Result.error("Withdrawal failed:" + response.getErrcode() + "——" + response.getErrmsg(), response);
                }
            }
            return Result.error("DingTalk has not been configured, please configure DingTalk");
        }
        return Result.error("Third-party apps that are not recognized");
    }

    //========================begin 应用低代码钉钉/企业微信同步用户部门专用 =============================
    /**
     * Add third-party app configurations
     *
     * @param appConfig
     * @return
     */
    @RequestMapping(value = "/addThirdAppConfig", method = RequestMethod.POST)
    public Result<String> addThirdAppConfig(@RequestBody SysThirdAppConfig appConfig) {
        Result<String> result = new Result<>();
        //Determine whether the tenant ID and third-party category have been created
        Integer tenantId = oConvertUtils.isNotEmpty(appConfig.getTenantId()) ? appConfig.getTenantId() : oConvertUtils.getInt(TenantContext.getTenant(), 0);
        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, appConfig.getThirdType());
        if (null != config) {
            result.error500("If the operation fails, only one DingTalk or WeCom can be bound to the same tenant");
            return result;
        }
        String clientId = appConfig.getClientId();
        //通过应用key获取第三方配置
        List<SysThirdAppConfig> thirdAppConfigByClientId = appConfigService.getThirdAppConfigByClientId(clientId);
        if(CollectionUtil.isNotEmpty(thirdAppConfigByClientId)){
            result.error500("The App Key already exists, please do not add it repeatedly");
            return result;
        }
        try {
            appConfig.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
            appConfigService.save(appConfig);
            result.success("Added successfully!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("The operation failed");
        }
        return result;
    }

    /**
     * Edit the configuration of a third-party app
     *
     * @param appConfig
     * @return
     */
    @RequestMapping(value = "/editThirdAppConfig", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> editThirdAppConfig(@RequestBody SysThirdAppConfig appConfig) {
        Result<String> result = new Result<>();
        SysThirdAppConfig config = appConfigService.getById(appConfig.getId());
        if (null == config) {
            result.error500("The data does not exist");
            return result;
        }
        String clientId = appConfig.getClientId();
        //如果编辑的应用key,和数据库中的不一致，需要判断应用key是否已存在
        if(!clientId.equals(config.getClientId())){
            //通过应用key获取第三方配置
            List<SysThirdAppConfig> thirdAppConfigByClientId = appConfigService.getThirdAppConfigByClientId(clientId);
            if(CollectionUtil.isNotEmpty(thirdAppConfigByClientId)){
                result.error500("The App Key already exists, please do not add it repeatedly");
                return result;
            }
        }
        try {
            appConfigService.updateById(appConfig);
            result.success("Modification successful!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("The operation failed");
        }
        return result;
    }

    /**
     * Obtain third-party app configuration information based on tenant ID and third-party type
     *
     * @param tenantId
     * @param thirdType
     * @return
     */
    @GetMapping("/getThirdConfigByTenantId")
    public Result<SysThirdAppConfig> getThirdAppByTenantId(@RequestParam(name = "tenantId", required = false) Integer tenantId,
                                                           @RequestParam(name = "thirdType") String thirdType) {
        Result<SysThirdAppConfig> result = new Result<>();
        LambdaQueryWrapper<SysThirdAppConfig> query = new LambdaQueryWrapper<>();
        query.eq(SysThirdAppConfig::getThirdType,thirdType);

        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            if (tenantId == null) {
                return Result.error("If you enable multi-tenant mode, the tenant ID parameter cannot be empty.");
            }
        } else {
            //租户未传递，则采用平台的
            if (tenantId == null) {
                tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
            }
        }
        
        query.eq(SysThirdAppConfig::getTenantId,tenantId);
        SysThirdAppConfig sysThirdAppConfig = appConfigService.getOne(query);
        result.setSuccess(true);
        result.setResult(sysThirdAppConfig);
        return result;
    }

    /**
     * Synchronize DingTalk [departments and users] to the local computer
     *
     * @param ids
     * @return
     */
    @GetMapping("/sync/dingtalk/departAndUser/toLocal")
    public Result syncDingTalkDepartAndUserToLocal(@RequestParam(value = "ids", required = false) String ids) {
        Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
        SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(tenantId, MessageTypeEnum.DD.getType());
        if (null != config) {
            SyncInfoVo syncInfo = dingtalkService.syncThirdAppDepartmentUserToLocal();
            if (syncInfo.getFailInfo().size() == 0) {
                return Result.OK("Synchronization succeeded", syncInfo);
            } else {
                return Result.error("Synchronization failed", syncInfo);
            }
        }
        return Result.error("DingTalk has not been configured, please configure DingTalk");
    }
    //========================end 应用低代码钉钉/企业微信同步用户部门专用 ========================


    //========================begin 应用低代码账号设置第三方账号绑定 ================================
    /**
     * Get a third-party account
     * @param thirdType
     * @return
     */
    @GetMapping("/getThirdAccountByUserId")
    public Result<List<SysThirdAccount>> getThirdAccountByUserId(@RequestParam(name="thirdType") String thirdType){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
        //根据id查询
        query.eq(SysThirdAccount::getSysUserId,sysUser.getId());
        //扫码登录只有租户为0
        query.eq(SysThirdAccount::getTenantId,CommonConstant.TENANT_ID_DEFAULT_VALUE);
        //根据第三方类别查询
        if(oConvertUtils.isNotEmpty(thirdType)){
            query.in(SysThirdAccount::getThirdType, Arrays.asList(thirdType.split(SymbolConstant.COMMA)));
        }
        List<SysThirdAccount> list = sysThirdAccountService.list(query);
        return Result.ok(list);
    }

    /**
     * Bind a third-party account
     * @return
     */
    @PostMapping("/bindThirdAppAccount")
    public Result<SysThirdAccount> bindThirdAppAccount(@RequestBody SysThirdAccount sysThirdAccount){
        SysThirdAccount thirdAccount = sysThirdAccountService.bindThirdAppAccountByUserId(sysThirdAccount);
        return Result.ok(thirdAccount);
    }

    /**
     * Deletion of third-party user information
     * @param sysThirdAccount
     * @return
     */
    @DeleteMapping("/deleteThirdAccount")
    public Result<String> deleteThirdAccountById(@RequestBody SysThirdAccount sysThirdAccount){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(!sysUser.getId().equals(sysThirdAccount.getSysUserId())){
            return Result.error("There is no right to modify the information of others");
        }
        SysThirdAccount thirdAccount = sysThirdAccountService.getById(sysThirdAccount.getId());
        if(null == thirdAccount){
            return Result.error("The third-party account information could not be found");
        }
        sysThirdAccountService.removeById(thirdAccount.getId());
        return Result.ok("The unbinding is successful");
    }
    //========================end Apply a low-code account to set up a third-party account binding ================================
}
