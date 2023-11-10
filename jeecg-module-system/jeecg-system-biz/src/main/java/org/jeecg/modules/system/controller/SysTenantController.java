package org.jeecg.modules.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.service.ISysTenantPackService;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.ISysUserTenantService;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.vo.SysUserTenantVo;
import org.jeecg.modules.system.vo.tenant.TenantDepartAuthInfo;
import org.jeecg.modules.system.vo.tenant.TenantPackModel;
import org.jeecg.modules.system.vo.tenant.TenantPackUser;
import org.jeecg.modules.system.vo.tenant.TenantPackUserCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Tenant configuration information
 * @author: jeecg-boot
 */
@Slf4j
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {

    @Autowired
    private ISysTenantService sysTenantService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserTenantService relationService;
    
    @Autowired
    private ISysTenantPackService sysTenantPackService;
    
    @Autowired
    private BaseCommonService baseCommonService;

    @Autowired
    private ISysDepartService sysDepartService;

    /**
     * Get list data
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequiresPermissions("system:tenant:list")
    @PermissionData(pageComponent = "system/TenantList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysTenant>> queryPageList(SysTenant sysTenant,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysTenant>> result = new Result<IPage<SysTenant>>();
        //---author:zhangyafei---date:20210916-----for: Add a date range query --- for tenant management
        Date beginDate=null;
        Date endDate=null;
        if(oConvertUtils.isNotEmpty(sysTenant)) {
            beginDate=sysTenant.getBeginDate();
            endDate=sysTenant.getEndDate();
            sysTenant.setBeginDate(null);
            sysTenant.setEndDate(null);
        }
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
        QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
        if(oConvertUtils.isNotEmpty(sysTenant)){
            queryWrapper.ge(oConvertUtils.isNotEmpty(beginDate),"begin_date",beginDate);
            queryWrapper.le(oConvertUtils.isNotEmpty(endDate),"end_date",endDate);
        }
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
		Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
		IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

    /**
     * Get a list of tenant deletions
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping("/recycleBinPageList")
    @RequiresPermissions("system:tenant:recycleBinPageList")
    public Result<IPage<SysTenant>> recycleBinPageList(SysTenant sysTenant,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req){
        Result<IPage<SysTenant>> result = new Result<IPage<SysTenant>>();
        Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
        IPage<SysTenant> pageList = sysTenantService.getRecycleBinPageList(page, sysTenant);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    
    /**
     *   Add to
     * @param
     * @return
     */
    @RequiresPermissions("system:tenant:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysTenant> add(@RequestBody SysTenant sysTenant) {
        Result<SysTenant> result = new Result();
        if(sysTenantService.getById(sysTenant.getId())!=null){
            return result.error500("The number already exists!");
        }
        try {
            sysTenantService.saveTenant(sysTenant);
            // Add a default package
            sysTenantPackService.addTenantDefaultPack(sysTenant.getId());
            result.success("Added successfully!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("The operation failed");
        }
        return result;
    }

    /**
     *  EDIT
     * @param
     * @return
     */
    @RequiresPermissions("system:tenant:edit")
    @RequestMapping(value = "/edit", method ={RequestMethod.PUT, RequestMethod.POST})
    public Result<SysTenant> edit(@RequestBody SysTenant tenant) {
        Result<SysTenant> result = new Result();
        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if(sysTenant==null) {
           return result.error500("No corresponding entity found");
        }
        if(oConvertUtils.isEmpty(sysTenant.getHouseNumber())){
            tenant.setHouseNumber(RandomUtil.randomStringUpper(6));
        }
        boolean ok = sysTenantService.updateById(tenant);
        if(ok) {
            result.success("Modification successful!");
        }
        return result;
    }

    /**
     *   Delete by ID
     * @param id
     * @return
     */
    @RequiresPermissions("system:tenant:delete")
    @RequestMapping(value = "/delete", method ={RequestMethod.DELETE, RequestMethod.POST})
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        //------------------------------------------------------------------
        //如果是saas隔离的情况下，判断当前租户id是否是当前租户下的
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            //获取当前用户
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            SysTenant sysTenant = sysTenantService.getById(id);

            String username = "admin";
            String createdBy = sysUser.getUsername();
            if (!sysTenant.getCreateBy().equals(createdBy) && !username.equals(createdBy)) {
                baseCommonService.addLog("Tenants that are not created by you cannot be deleted without authorization, and the tenant ID:" + id + ", Operated by:" + sysUser.getUsername(), CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_3);
                return Result.error("Failed to delete a tenant, and the current operator is not the creator of the tenant.");
            }
        }
        //------------------------------------------------------------------
                
        sysTenantService.removeTenantById(id);
        return Result.ok("The deletion is successful");
    }

    /**
     *  Delete in bulk
     * @param ids
     * @return
     */
    @RequiresPermissions("system:tenant:deleteBatch")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<?> result = new Result<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.error500("Tenant unchecked!");
        }else {
            String[] ls = ids.split(",");
            // Filter out tenants that have been referenced
            List<Integer> idList = new ArrayList<>();
            for (String id : ls) {
                //------------------------------------------------------------------
                //If SaaS is isolated, check whether the current tenant ID belongs to the current tenant
                if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
                    // Get the current user
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    SysTenant sysTenant = sysTenantService.getById(id);

                    String username = "admin";
                    String createdBy = sysUser.getUsername();
                    if (!sysTenant.getCreateBy().equals(createdBy) && !username.equals(createdBy)) {
                        baseCommonService.addLog("Tenants that are not created by you cannot be deleted without authorization, and the tenant ID:" + id + ", Operated by:" + sysUser.getUsername(), CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_3);
                        return Result.error("Failed to delete a tenant, and the current operator is not the creator of the tenant.");
                    }
                }
                //------------------------------------------------------------------
                
                idList.add(Integer.parseInt(id));
            }
            //update-begin---author:wangshuai ---date:20230710  for：【QQYUN-5723】3、租户删除直接删除，不删除中间表------------
            sysTenantService.removeByIds(idList);
            result.success("Deleted successfully!");
            //update-end---author:wangshuai ---date:20220523  for：【QQYUN-5723】3、租户删除直接删除，不删除中间表------------
        }
        return result;
    }

    /**
     * Query by ID
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysTenant> queryById(@RequestParam(name="id",required=true) String id) {
        Result<SysTenant> result = new Result<SysTenant>();
        if(oConvertUtils.isEmpty(id)){
            result.error500("The argument is empty!");
        }
        //------------------------------------------------------------------------------------------------
        //Obtain the logged-in user information
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】, admin给特权可以管理所有租户
        if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL && !"admin".equals(sysUser.getUsername())){
            Integer loginSessionTenant = oConvertUtils.getInt(TenantContext.getTenant());
            if(loginSessionTenant!=null && !loginSessionTenant.equals(Integer.valueOf(id))){
                result.error500("No access to other tenants!");
                return result;
            }
        }
        //------------------------------------------------------------------------------------------------
        SysTenant sysTenant = sysTenantService.getById(id);
        if(sysTenant==null) {
            result.error500("No corresponding entity found");
        }else {
            result.setResult(sysTenant);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * Query valid tenant data
     * @return
     */
    @RequiresPermissions("system:tenant:queryList")
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Result<List<SysTenant>> queryList(@RequestParam(name="ids",required=false) String ids) {
        Result<List<SysTenant>> result = new Result<List<SysTenant>>();
        LambdaQueryWrapper<SysTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysTenant::getStatus, 1);
        if(oConvertUtils.isNotEmpty(ids)){
            query.in(SysTenant::getId, ids.split(","));
        }
        //此处查询忽略时间条件
        List<SysTenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * Query the pagination list of product packages
     *
     * @param sysTenantPack
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/packList")
    @RequiresPermissions("system:tenant:packList")
    public Result<IPage<SysTenantPack>> queryPackPageList(SysTenantPack sysTenantPack,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest req) {
        QueryWrapper<SysTenantPack> queryWrapper = QueryGenerator.initQueryWrapper(sysTenantPack, req.getParameterMap());
        Page<SysTenantPack> page = new Page<SysTenantPack>(pageNo, pageSize);
        IPage<SysTenantPack> pageList = sysTenantPackService.page(page, queryWrapper);
        List<SysTenantPack> records = pageList.getRecords();
        if (null != records && records.size() > 0) {
            pageList.setRecords(sysTenantPackService.setPermissions(records));
        }
        return Result.OK(pageList);
    }

    /**
     * Create a tenant package
     *
     * @param sysTenantPack
     * @return
     */
    @PostMapping(value = "/addPackPermission")
    @RequiresPermissions("system:tenant:add:pack")
    public Result<String> addPackPermission(@RequestBody SysTenantPack sysTenantPack) {
        sysTenantPackService.addPackPermission(sysTenantPack);
        return Result.ok("The tenant package is created");
    }

    /**
     * Create a tenant package
     *
     * @param sysTenantPack
     * @return
     */
    @PutMapping(value = "/editPackPermission")
    @RequiresPermissions("system:tenant:edit:pack")
    public Result<String> editPackPermission(@RequestBody SysTenantPack sysTenantPack) {
        sysTenantPackService.editPackPermission(sysTenantPack);
        return Result.ok("The tenant package was modified");
    }

    /**
     * Delete user menus in bulk
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/deletePackPermissions")
    @RequiresPermissions("system:tenant:delete:pack")
    public Result<String> deletePackPermissions(@RequestParam(value = "ids") String ids) {
        sysTenantPackService.deletePackPermissions(ids);
        return Result.ok("The tenant package was deleted");
    }
    


    //===========【低代码应用，前端专用接口 —— 加入限制只能维护和查看自己拥有的租户】==========================================================
    /**
     *  Query all active tenants for the current user【Dedicated interfaces for low-code applications】
     * @return
     */
    @RequestMapping(value = "/getCurrentUserTenant", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserTenant() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //update-begin---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
            List<Integer> tenantIdList = relationService.getTenantIdsByUserId(sysUser.getId());
            Map<String,Object> map = new HashMap(5);
            if (null!=tenantIdList && tenantIdList.size()>0) {
            //update-end---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
                // 该方法仅查询有效的租户，如果返回0个就说明所有的租户均无效。
                List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
                map.put("list", tenantList);
            }
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Query failed!");
        }
        return result;
    }

    /**
     * Invite users to [low-code application specific interface]
     * @param ids
     * @param phone
     * @return
     */
    @PutMapping("/invitationUserJoin")
    @RequiresPermissions("system:tenant:invitation:user")
    public Result<String> invitationUserJoin(@RequestParam("ids") String ids,@RequestParam("phone") String phone){
        sysTenantService.invitationUserJoin(ids,phone);
        return Result.ok("The user was successfully invited");
    }

    /**
     * Get user list data [low-code application dedicated interface]
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/getTenantUserList", method = RequestMethod.GET)
    @RequiresPermissions("system:tenant:user:list")
    public Result<IPage<SysUser>> getTenantUserList(SysUser user,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                    @RequestParam(name="userTenantId") String userTenantId,
                                                    HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<>();
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        Page<SysUser> pageList = relationService.getPageUserList(page,Integer.valueOf(userTenantId),user);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * Please leave the user tenant [low-code application dedicated interface]
     * @param userIds
     * @param tenantId
     * @return
     */
    @PutMapping("/leaveTenant")
    @RequiresPermissions("system:tenant:leave")
    public Result<String> leaveTenant(@RequestParam("userIds") String userIds,
                                      @RequestParam("tenantId") String tenantId){
        Result<String> result = new Result<>();
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL && !"admin".equals(sysUser.getUsername())){
            Integer loginSessionTenant = oConvertUtils.getInt(TenantContext.getTenant());
            if(loginSessionTenant!=null && !loginSessionTenant.equals(Integer.valueOf(tenantId))){
                result.error500("No access to other tenants!");
                return result;
            }
        }
        sysTenantService.leaveTenant(userIds,tenantId);
        return Result.ok("Please leave the success");
    }

    /**
     *  Editing (only allowed to modify tenants owned by oneself) [low-code application-specific interface]
     * @param
     * @return
     */
    @RequestMapping(value = "/editOwnTenant", method ={RequestMethod.PUT, RequestMethod.POST})
    public Result<SysTenant> editOwnTenant(@RequestBody SysTenant tenant,HttpServletRequest req) {
        Result<SysTenant> result = new Result();
        String tenantId = TokenUtils.getTenantIdByRequest(req);
        if(!tenantId.equals(tenant.getId().toString())){
            return result.error500("No right to modify other tenants!");
        }

        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if(sysTenant==null) {
            return result.error500("No corresponding entity found");
        }
        if(oConvertUtils.isEmpty(sysTenant.getHouseNumber())){
            tenant.setHouseNumber(RandomUtil.randomStringUpper(6));
        }
        boolean ok = sysTenantService.updateById(tenant);
        if(ok) {
            result.success("Modification successful!");
        }
        return result;
    }
    
    /**
     * Create a tenant and save users to an intermediate table [low-code application-specific interface]
     * @param sysTenant
     */
    @PostMapping("/saveTenantJoinUser")
    public Result<Integer> saveTenantJoinUser(@RequestBody SysTenant sysTenant){
        Result<Integer> result = new Result<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Integer tenantId = sysTenantService.saveTenantJoinUser(sysTenant, sysUser.getId());
        result.setSuccess(true);
        result.setMessage("The creation is successful");
        result.setResult(tenantId);
        return result;
    }

    /**
     * Join a tenant through the house number [low-code application dedicated interface]
     * @param sysTenant
     */
    @PostMapping("/joinTenantByHouseNumber")
    public Result<Integer> joinTenantByHouseNumber(@RequestBody SysTenant sysTenant){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Integer tenantId = sysTenantService.joinTenantByHouseNumber(sysTenant, sysUser.getId());
        Result<Integer> result = new Result<>();
        if(tenantId != 0){
            result.setMessage("The tenant application is successful");
            result.setSuccess(true);
            result.setResult(tenantId);
            return result;
        }else{
            result.setMessage("The house number does not exist");
            result.setSuccess(false);
            return result;
        }
    }
    
    //update-begin---author:wangshuai ---date:20230107  for：[QQYUN-3725]申请加入租户，审核中状态增加接口------------
    /**
     * Get tenant user data by page (vue 3 user tenant page) [low-code application dedicated interface]
     *
     * @param pageNo
     * @param pageSize
     * @param userTenantStatus
     * @param type
     * @param req
     * @return
     */
    @GetMapping("/getUserTenantPageList")
    //@RequiresPermissions("system:tenant:tenantPageList")
    public Result<IPage<SysUserTenantVo>> getUserTenantPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @RequestParam(name = "userTenantStatus") String userTenantStatus,
                                                                @RequestParam(name = "type", required = false) String type,
                                                                SysUser user,
                                                                HttpServletRequest req) {
        Page<SysUserTenantVo> page = new Page<SysUserTenantVo>(pageNo, pageSize);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String tenantId = oConvertUtils.getString(TenantContext.getTenant(), "0");
        IPage<SysUserTenantVo> list = relationService.getUserTenantPageList(page, Arrays.asList(userTenantStatus.split(SymbolConstant.COMMA)), user, Integer.valueOf(tenantId));
        return Result.ok(list);
    }

    /**
     * Obtaining the Tenant List by User ID [Low-Code Application Dedicated Interface]
     *
     * @param userTenantStatus The status of the relationship table
     * @return
     */
    @GetMapping("/getTenantListByUserId")
    //@RequiresPermissions("system:tenant:getTenantListByUserId")
    public Result<List<SysUserTenantVo>> getTenantListByUserId(@RequestParam(name = "userTenantStatus", required = false) String userTenantStatus) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> list = null;
        if (oConvertUtils.isNotEmpty(userTenantStatus)) {
            list = Arrays.asList(userTenantStatus.split(SymbolConstant.COMMA));
        }
        //租户状态，用户id,租户用户关系状态
        List<SysUserTenantVo> sysTenant = relationService.getTenantListByUserId(sysUser.getId(), list);
        return Result.ok(sysTenant);
    }

    /**
     * Update User Tenant Relationship Status [Low-Code Application Dedicated Interface]
     */
    @PutMapping("/updateUserTenantStatus")
    //@RequiresPermissions("system:tenant:updateUserTenantStatus")
    public Result<String> updateUserTenantStatus(@RequestBody SysUserTenant userTenant) {
        String tenantId = TenantContext.getTenant();
        if (oConvertUtils.isEmpty(tenantId)) {
            return Result.error("The current tenant information was not found");
        }
        relationService.updateUserTenantStatus(userTenant.getUserId(), tenantId, userTenant.getStatus());
        return Result.ok("The update of the user's tenant status is successful");
    }

    /**
     * Deregistering a Tenant [Low-Code Application Dedicated Interface]
     *
     * @param sysTenant
     * @return
     */
    @PutMapping("/cancelTenant")
    //@RequiresPermissions("system:tenant:cancelTenant")
    public Result<String> cancelTenant(@RequestBody SysTenant sysTenant,HttpServletRequest request) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysTenant tenant = sysTenantService.getById(sysTenant.getId());
        if (null == tenant) {
            return Result.error("The current tenant information was not found");
        }
        if (!sysUser.getUsername().equals(tenant.getCreateBy())) {
            return Result.error("You have no permissions, you can only log out of the tenant you created!");
        }
        SysUser userById = sysUserService.getById(sysUser.getId());
        String loginPassword = request.getParameter("loginPassword");
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(),loginPassword, userById.getSalt());
        if (!passwordEncode.equals(userById.getPassword())) {
            return Result.error("The password is incorrect");
        }
        sysTenantService.removeById(sysTenant.getId());
        return Result.ok("The logout is successful");
    }
    //update-end---author:wangshuai ---date:20230107  for：[QQYUN-3725]申请加入租户，审核中状态增加接口------------

    /**
     * Obtaining the Number of Tenant Users in Different States [Low-Code Application Dedicated Interface]
     * @return
     */
    @GetMapping("/getTenantStatusCount")
    public Result<Long> getTenantStatusCount(@RequestParam(value = "status",defaultValue = "1") String status, HttpServletRequest req){
        String tenantId = TokenUtils.getTenantIdByRequest(req);
        if (null == tenantId) {
            return Result.error("The current tenant information was not found");
        }
        LambdaQueryWrapper<SysUserTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysUserTenant::getTenantId,tenantId);
        query.eq(SysUserTenant::getStatus,status);
        long count = relationService.count(query);
        return Result.ok(count);
    }

    /**
     * The user cancels the tenant application [low-code application dedicated interface]
     * @param tenantId
     * @return
     */
    @PutMapping("/cancelApplyTenant")
    public Result<String> cancelApplyTenant(@RequestParam("tenantId") String tenantId){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysTenantService.leaveTenant(sysUser.getId(),tenantId);
        return Result.ok("The cancellation application was successful");
    }

    //===========【低代码应用，前端专用接口 —— 加入限制只能维护和查看自己拥有的租户】==========================================================

    /**
     * Delete the tenant completely
     * @param ids
     * @return
     */
    @DeleteMapping("/deleteLogicDeleted")
    @RequiresPermissions("system:tenant:deleteTenantLogic")
    public Result<String> deleteTenantLogic(@RequestParam("ids") String ids){
        sysTenantService.deleteTenantLogic(ids);
        return Result.ok("The deletion was successful");
    }

    /**
     * Restore a deleted tenant
     * @param ids
     * @return
     */
    @PutMapping("/revertTenantLogic")
    @RequiresPermissions("system:tenant:revertTenantLogic")
    public Result<String> revertTenantLogic(@RequestParam("ids") String ids){
        sysTenantService.revertTenantLogic(ids);
        return Result.ok("The restore was successful");
    }

    /**
     * Exiting a Tenant [Low-Code Application Dedicated Interface]
     * @param sysTenant
     * @param request
     * @return
     */
    @DeleteMapping("/exitUserTenant")
    public Result<String> exitUserTenant(@RequestBody SysTenant sysTenant,HttpServletRequest request){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //验证用户是否已存在
        Integer count = relationService.userTenantIzExist(sysUser.getId(),sysTenant.getId());
        if (count == 0) {
            return Result.error("There are no current users under this tenant");
        }
        //验证密码
        String loginPassword = request.getParameter("loginPassword");
        SysUser userById = sysUserService.getById(sysUser.getId());
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(),loginPassword, userById.getSalt());
        if (!passwordEncode.equals(userById.getPassword())) {
            return Result.error("The password is incorrect");
        }
        //退出登录
        sysTenantService.exitUserTenant(sysUser.getId(),sysUser.getUsername(),String.valueOf(sysTenant.getId()));
        return Result.ok("The tenant exit is successful");
    }

    /**
     * Changing the Tenant Owner [Low-Code Application Dedicated Interface]
     * @param userId
     * @return
     */
    @PostMapping("/changeOwenUserTenant")
    public Result<String> changeOwenUserTenant(@RequestParam("userId") String userId,
                                               @RequestParam("tenantId") String tenantId){
        sysTenantService.changeOwenUserTenant(userId,tenantId);
        return Result.ok("The tenant exit is successful");
    }

    /**
     * Invite users to tenants and match them with mobile phone numbers [Low-code application dedicated interface]
     * @param phone
     * @param departId
     * @return
     */
    @PostMapping("/invitationUser")
    public Result<String> invitationUser(@RequestParam(name="phone") String phone,
                                         @RequestParam(name="departId",defaultValue = "") String departId){
        return sysTenantService.invitationUser(phone,departId);
    }


    /**
     * FETCH Tenant Package - The number of people with 3 default admins
     * @param tenantId
     * @return
     */
    @GetMapping("/loadAdminPackCount")
    public Result<List<TenantPackUserCount>> loadAdminPackCount(@RequestParam("tenantId") Integer tenantId){
        List<TenantPackUserCount> list = sysTenantService.queryTenantPackUserCount(tenantId);
        return Result.ok(list);
    }

    /**
     * Query tenant package information
     * @param packModel
     * @return
     */
    @GetMapping("/getTenantPackInfo")
    public Result<TenantPackModel> getTenantPackInfo(TenantPackModel packModel){
        TenantPackModel tenantPackModel = sysTenantService.queryTenantPack(packModel);
        return Result.ok(tenantPackModel);
    }


    /**
     * Add relational data for users and packages
     * @param sysTenantPackUser
     * @return
     */
    @PostMapping("/addTenantPackUser")
    public Result<?> addTenantPackUser(@RequestBody SysTenantPackUser sysTenantPackUser){
        sysTenantService.addBatchTenantPackUser(sysTenantPackUser);
        return Result.ok("Operation successful!");
    }

    /**
     * Remove a user from a package
     * @param sysTenantPackUser
     * @return
     */
    @PutMapping("/deleteTenantPackUser")
    public Result<?> deleteTenantPackUser(@RequestBody SysTenantPackUser sysTenantPackUser){
        sysTenantService.deleteTenantPackUser(sysTenantPackUser);
        return Result.ok("Operation successful!");
    }


    /**
     * Modify the status of the request
     * @param sysTenant
     * @return
     */
    @PutMapping("/updateApplyStatus")
    public Result<?> updateApplyStatus(@RequestBody SysTenant sysTenant){
        SysTenant entity = this.sysTenantService.getById(sysTenant.getId());
        if(entity==null){
            return Result.error("The tenant does not exist!");
        }
        entity.setApplyStatus(sysTenant.getApplyStatus());
        sysTenantService.updateById(entity);
        return Result.ok("");
    }


    /**
     * Get a list of package personnel requests
     * @param tenantId
     * @return
     */
    @GetMapping("/getTenantPackApplyUsers")
    public Result<?> getTenantPackApplyUsers(@RequestParam("tenantId") Integer tenantId){
        List<TenantPackUser> list = sysTenantService.getTenantPackApplyUsers(tenantId);
        return Result.ok(list);
    }

    /**
     * INDIVIDUAL Apply to become an administrator
     * @param sysTenantPackUser
     * @return
     */
    @PostMapping("/doApplyTenantPackUser")
    public Result<?> doApplyTenantPackUser(@RequestBody SysTenantPackUser sysTenantPackUser){
        sysTenantService.doApplyTenantPackUser(sysTenantPackUser);
        return Result.ok("Successful application!");
    }

    /**
     * The application is approved Become an administrator
     * @param sysTenantPackUser
     * @return
     */
    @PutMapping("/passApply")
    public Result<?> passApply(@RequestBody SysTenantPackUser sysTenantPackUser){
        sysTenantService.passApply(sysTenantPackUser);
        return Result.ok("Operation successful!");
    }

    /**
     *  Rejection of the application Become an administrator
     * @param sysTenantPackUser
     * @return
     */
    @PutMapping("/deleteApply")
    public Result<?> deleteApply(@RequestBody SysTenantPackUser sysTenantPackUser){
        sysTenantService.deleteApply(sysTenantPackUser);
        return Result.ok("");
    }

    /**
     * Check to see if you've already applied for a super administrator
     * @return
     */
    @GetMapping("/getApplySuperAdminCount")
    public Result<Long> getApplySuperAdminCount(){
        Long count = sysTenantService.getApplySuperAdminCount();
        return Result.ok(count);
    }

    /**
     * Go to the Application Organization page Query the tenant information and whether the current user exists Administrator Privileges--
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryTenantAuthInfo", method = RequestMethod.GET)
    public Result<TenantDepartAuthInfo> queryTenantAuthInfo(@RequestParam(name="id",required=true) String id) {
        TenantDepartAuthInfo info = sysTenantService.getTenantDepartAuthInfo(Integer.parseInt(id));
        return Result.ok(info);
    }

    /**
     * Get the list of users under a product package (pagination)
     * @param tenantId
     * @param packId
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/queryTenantPackUserList")
    public Result<IPage<TenantPackUser>> queryTenantPackUserList(@RequestParam("tenantId") String tenantId,
                                                                 @RequestParam("packId") String packId,
                                                                 @RequestParam("status") Integer status,
                                                                 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        Page<TenantPackUser> page = new Page<>(pageNo,pageSize);
        IPage<TenantPackUser> pageList = sysTenantService.queryTenantPackUserList(tenantId,packId,status,page);
        return Result.ok(pageList);
    }

    /**
     * Get the number of departments and members under the current tenant
     */
    @GetMapping("/getTenantCount")
    public Result<Map<String,Long>> getTenantCount(HttpServletRequest request){
        Map<String,Long> map = new HashMap<>();
        Integer tenantId = oConvertUtils.getInt(TokenUtils.getTenantIdByRequest(request),0);
        LambdaQueryWrapper<SysUserTenant> userTenantQuery = new LambdaQueryWrapper<>();
        userTenantQuery.eq(SysUserTenant::getTenantId,tenantId);
        userTenantQuery.eq(SysUserTenant::getStatus,CommonConstant.USER_TENANT_NORMAL);
        long userCount = relationService.count(userTenantQuery);
        map.put("userCount",userCount);
        LambdaQueryWrapper<SysDepart> departQuery = new LambdaQueryWrapper<>();
        departQuery.eq(SysDepart::getDelFlag,String.valueOf(CommonConstant.DEL_FLAG_0));
        departQuery.eq(SysDepart::getTenantId,tenantId);
        departQuery.eq(SysDepart::getStatus,CommonConstant.STATUS_1);
        long departCount = sysDepartService.count(departQuery);
        map.put("departCount",departCount);
        return Result.ok(map);
    }

    /**
     * Get the list of tenants by user ID (pagination)
     *
     * @param sysUserTenantVo
     * @return
     */
    @GetMapping("/getTenantPageListByUserId")
    public Result<IPage<SysTenant>> getTenantPageListByUserId(SysUserTenantVo sysUserTenantVo,
                                                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> list = null;
        String userTenantStatus = sysUserTenantVo.getUserTenantStatus();
        if (oConvertUtils.isNotEmpty(userTenantStatus)) {
            list = Arrays.asList(userTenantStatus.split(SymbolConstant.COMMA));
        }
        Page<SysTenant> page = new Page<>(pageNo,pageSize);
        IPage<SysTenant> pageList = relationService.getTenantPageListByUserId(page,sysUser.getId(),list,sysUserTenantVo);
        return Result.ok(pageList);
    }

    /**
     * Agree or decline to join the tenant
     */
    @PutMapping("/agreeOrRefuseJoinTenant")
    public Result<String> agreeOrRefuseJoinTenant(@RequestParam("tenantId") Integer tenantId, 
                                                  @RequestParam("status") String status){
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        SysTenant tenant = sysTenantService.getById(tenantId);
        if(null == tenant){
            return Result.error("The organization does not exist");
        }
        SysUserTenant sysUserTenant = relationService.getUserTenantByTenantId(userId, tenantId);
        if (null == sysUserTenant) {
            return Result.error("The user does not exist in the organization and does not have the right to modify it");
        }
        String content = "";
        SysUser user = new SysUser();
        user.setUsername(sysUserTenant.getCreateBy());
        String realname = oConvertUtils.getString(sysUser.getRealname(),sysUser.getUsername());
        //成功加入
        if(CommonConstant.USER_TENANT_NORMAL.equals(status)){
            //修改租户状态
            relationService.agreeJoinTenant(userId,tenantId);
            content = content + realname + "The join you sent has been approved " + tenant.getName() + " INVITATIONS";
            sysTenantService.sendMsgForAgreeAndRefuseJoin(user, content);
            return Result.OK("You have agreed to the organization's invitation");
        }else if(CommonConstant.USER_TENANT_REFUSE.equals(status)){
            //直接删除关系表即可
            relationService.refuseJoinTenant(userId,tenantId);
            content = content + realname + "The join you sent was declined " + tenant.getName() + " INVITATIONS";
            sysTenantService.sendMsgForAgreeAndRefuseJoin(user, content);
            return Result.OK("You have successfully declined the invitation from the organization");
        }
        return Result.error("If the types do not match, the data cannot be modified");
    }
    
}
