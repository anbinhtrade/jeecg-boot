package org.jeecg.modules.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.model.SysUserSysDepartModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.vo.SysDepartUsersVO;
import org.jeecg.modules.system.vo.SysUserRoleVO;
import org.jeecg.modules.system.vo.lowapp.DepartAndUserInfo;
import org.jeecg.modules.system.vo.lowapp.UpdateDepartInfo;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 Front-end controllers
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

	@Autowired
	private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@Autowired
	private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysDepartRoleUserService departRoleUserService;

    @Autowired
    private ISysDepartRoleService departRoleService;

	@Autowired
	private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private BaseCommonService baseCommonService;

    @Autowired
    private ISysUserAgentService sysUserAgentService;

    @Autowired
    private ISysPositionService sysPositionService;

    @Autowired
    private ISysUserTenantService userTenantService;

    /**
     * Obtaining User Data in a Tenant (Tenant Isolation is Supported)
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/UserList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysUser>> queryPageList(SysUser user,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        //------------------------------------------------------------------------------------------------
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            String tenantId = oConvertUtils.getString(TenantContext.getTenant(), "-1");
            List<String> userIds = userTenantService.getUserIdsByTenantId(Integer.valueOf(tenantId));
            if (oConvertUtils.listIsNotEmpty(userIds)) {
                queryWrapper.in("id", userIds);
            }else{
                queryWrapper.eq("id", "No users can be queried through the tenant");
            }
        }
        //------------------------------------------------------------------------------------------------
        return sysUserService.queryPageList(req, queryWrapper, pageSize, pageNo);
	}

    /**
     * Obtain system user data (query all users, do not isolate tenants)
     *
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequiresPermissions("system:user:listAll")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Result<IPage<SysUser>> queryAllPageList(SysUser user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
        return sysUserService.queryPageList(req, queryWrapper, pageSize, pageNo);
    }

    @RequiresPermissions("system:user:add")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		String selectedDeparts = jsonObject.getString("selecteddeparts");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//设置创建时间
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			//用户表字段org_code不能在这里设置他的值
            user.setOrgCode(null);
			// 保存用户走一个service 保证事务
            //获取租户ids
            String relTenantIds = jsonObject.getString("relTenantIds");
            sysUserService.saveUser(user, selectedRoles, selectedDeparts, relTenantIds);
            baseCommonService.addLog("Add user, username: " +user.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
			result.success("Added successfully!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("The operation failed");
		}
		return result;
	}

    @RequiresPermissions("system:user:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			baseCommonService.addLog("Edit user, username: " +sysUser.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
			if(sysUser==null) {
				result.error500("No corresponding entity found");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
                if(oConvertUtils.isEmpty(departs)){
                    //vue3.0前端只传递了departIds
                    departs=user.getDepartIds();
                }
                //用户表字段org_code不能在这里设置他的值
                user.setOrgCode(null);
                // 修改用户走一个service 保证事务
                //获取租户ids
                String relTenantIds = jsonObject.getString("relTenantIds");
				sysUserService.editUser(user, roles, departs, relTenantIds);
				result.success("Modification successful!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("The operation failed");
		}
		return result;
	}

	/**
	 * Delete the user
	 */
    @RequiresPermissions("system:user:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		baseCommonService.addLog("Delete user, ID: " +id ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteUser(id);
		return Result.ok("The deletion of the user is successful");
	}

	/**
	 * Delete users in bulk
	 */
    @RequiresPermissions("system:user:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		baseCommonService.addLog("Deleting users in batches, ids： " +ids ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteBatchUsers(ids);
		return Result.ok("The batch deletion of users is successful");
	}

	/**
	  * Freeze & Unfreeze a user
	 * @param jsonObject
	 * @return
	 */
    @RequiresPermissions("system:user:frozenBatch")
	@RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			String ids = jsonObject.getString("ids");
			String status = jsonObject.getString("status");
			String[] arr = ids.split(",");
            for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
                    //update-begin---author:liusq ---date:20230620  for：[QQYUN-5577]用户列表-冻结用户，再解冻之后，用户还是无法登陆，有缓存问题 #5066------------
                    sysUserService.updateStatus(id,status);
                    //update-end---author:liusq ---date:20230620  for：[QQYUN-5577]用户列表-冻结用户，再解冻之后，用户还是无法登陆，有缓存问题 #5066------------
                }
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("The operation failed"+e.getMessage());
		}
		result.success("The operation was successful!");
		return result;

    }

    @RequiresPermissions("system:user:queryById")
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("No corresponding entity found");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequiresPermissions("system:user:queryUserRole")
    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("No user-related role information found");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
	  *  Check whether the user account is unique<br>
	  *  Others can be verified Whatever needs to be tested is passed on ...
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //If this parameter is false, the program has an exception
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            sysUser.setPassword(null);
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("The user account already exists");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * Change your password
     */
    @RequiresPermissions("system:user:changepwd")
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public Result<?> changePassword(@RequestBody SysUser sysUser) {
        SysUser u = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (u == null) {
            return Result.error("The user doesn't exist!");
        }
        sysUser.setId(u.getId());
        //update-begin---author:wangshuai ---date:20220316  for：[VUEN-234]修改密码添加敏感日志------------
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        baseCommonService.addLog("Modify the user "+sysUser.getUsername()+" Password, operator: " +loginUser.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
        //update-end---author:wangshuai ---date:20220316  for：[VUEN-234]修改密码添加敏感日志------------
        return sysUserService.changePassword(sysUser);
    }

    /**
     * Query the data associated with a specified user or department
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("The search was successful");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("Lookup failed");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("An exception occurred during the lookup: " + e.getMessage());
            return result;
        }

    }

    /**
     * Generate an issue where there is no primary key in the case of adding a user, return it to the frontend, and bind the department data according to the ID
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("I executed it, generating user ID============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * Query user information based on department ID
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id,@RequestParam(name="realname",required=false) String realname) {
        Result<List<SysUser>> result = new Result<>();
        //List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        SysDepart sysDepart = sysDepartService.getById(id);
        List<SysUser> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(),realname);

        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            userList.forEach(item->{
                //TODO Temporarily borrow this field for page display
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }

        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * The user selects the component SPECIAL  Pagination based on user account or department
     * @param departId
     * @param username
     * @return
     */
    @RequestMapping(value = "/queryUserComponentData", method = RequestMethod.GET)
    public Result<IPage<SysUser>> queryUserComponentData(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "departId", required = false) String departId,
            @RequestParam(name="realname",required=false) String realname,
            @RequestParam(name="username",required=false) String username,
            @RequestParam(name="id",required = false) String id) {
        //update-begin-author:taoyan date:2022-7-14 for: VUEN-1702【禁止问题】sql注入漏洞
        String[] arr = new String[]{departId, realname, username, id};
        SqlInjectionUtil.filterContent(arr, SymbolConstant.SINGLE_QUOTATION_MARK);
        //update-end-author:taoyan date:2022-7-14 for: VUEN-1702【禁止问题】sql注入漏洞
        IPage<SysUser> pageList = sysUserDepartService.queryDepartUserPageList(departId, username, realname, pageSize, pageNo,id);
        return Result.OK(pageList);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysUser
     */
    @RequiresPermissions("system:user:export")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser,HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
       if(!oConvertUtils.isEmpty(selections)){
           queryWrapper.in("id",selections.split(","));
       }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysUser> pageList = sysUserService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "List of users");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("User list data", "Exporter:"+user.getRealname(), "Export information");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("system:user:import")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (int i = 0; i < listSysUsers.size(); i++) {
                    SysUser sysUserExcel = listSysUsers.get(i);
                    if (StringUtils.isBlank(sysUserExcel.getPassword())) {
                        // 密码默认为 “123456”
                        sysUserExcel.setPassword("123456");
                    }
                    // 密码加密加盐
                    String salt = oConvertUtils.randomGen(8);
                    sysUserExcel.setSalt(salt);
                    String passwordEncode = PasswordUtil.encrypt(sysUserExcel.getUsername(), sysUserExcel.getPassword(), salt);
                    sysUserExcel.setPassword(passwordEncode);
                    try {
                        sysUserService.save(sysUserExcel);
                        successLines++;
                    } catch (Exception e) {
                        errorLines++;
                        String message = e.getMessage().toLowerCase();
                        int lineNumber = i + 1;
                        // 通过索引名判断出错信息
                        if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_USERNAME)) {
                            errorMessage.add("CLAUSE " + lineNumber + " Line: The username already exists, ignore the import.");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_WORK_NO)) {
                            errorMessage.add("CLAUSE " + lineNumber + " OK: The ID already exists, ignore the import.");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_PHONE)) {
                            errorMessage.add("CLAUSE " + lineNumber + " OK: The mobile phone number already exists, ignore the import.");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_EMAIL)) {
                            errorMessage.add("CLAUSE " + lineNumber + " Line: The email already exists, ignore the import.");
                        }  else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER)) {
                            errorMessage.add("CLAUSE " + lineNumber + " Row: The table uniqueness constraint is violated.");
                        } else {
                            errorMessage.add("CLAUSE " + lineNumber + " Line: Unknown error, ignore import");
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 批量将部门和用户信息建立关联关系
                    String departIds = sysUserExcel.getDepartIds();
                    if (StringUtils.isNotBlank(departIds)) {
                        String userId = sysUserExcel.getId();
                        String[] departIdArray = departIds.split(",");
                        List<SysUserDepart> userDepartList = new ArrayList<>(departIdArray.length);
                        for (String departId : departIdArray) {
                            userDepartList.add(new SysUserDepart(userId, departId));
                        }
                        sysUserDepartService.saveBatch(userDepartList);
                    }

                }
            } catch (Exception e) {
                errorMessage.add("Exception occurs:" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                	log.error(e.getMessage(), e);
                }
            }
        }
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
    }

    /**
	 * @FUNCTION: Based on ID Bulk queries
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
		Result<Collection<SysUser>> result = new Result<>();
		String[] userId = userIds.split(",");
		Collection<String> idList = Arrays.asList(userId);
		Collection<SysUser> userRole = sysUserService.listByIds(idList);
		result.setSuccess(true);
		result.setResult(userRole);
		return result;
	}


    /**
     * @FUNCTION: Based on ID Bulk queries
     * @param userNames
     * @return
     */
    @RequestMapping(value = "/queryByNames", method = RequestMethod.GET)
    public Result<Collection<SysUser>> queryByNames(@RequestParam String userNames) {
        Result<Collection<SysUser>> result = new Result<>();
        String[] names = userNames.split(",");
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper();
        queryWrapper.lambda().in(true,SysUser::getUsername,names);
        Collection<SysUser> userRole = sysUserService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(userRole);
        return result;
    }

	/**
	 * Reset the password for the homepage user
	 */
    @RequiresPermissions("system:user:updatepwd")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
	public Result<?> updatePassword(@RequestBody JSONObject json) {
		String username = json.getString("username");
		String oldpassword = json.getString("oldpassword");
		String password = json.getString("password");
		String confirmpassword = json.getString("confirmpassword");
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(!sysUser.getUsername().equals(username)){
            return Result.error("You are only allowed to change your own password!");
        }
		SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
		if(user==null) {
			return Result.error("The user doesn't exist!");
		}
        //update-begin---author:wangshuai ---date:20220316  for：[VUEN-234]修改密码添加敏感日志------------
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        baseCommonService.addLog("Change password, username: " +loginUser.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
        //update-end---author:wangshuai ---date:20220316  for：[VUEN-234]修改密码添加敏感日志------------
		return sysUserService.resetPassword(username,oldpassword,password,confirmpassword);
	}

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * Add users to a specified role
     *
     * @param
     * @return
     */
    @RequiresPermissions("system:user:addUserRole")
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        //TODO Determine that the role of the current operation is under the current logged-in tenant
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for(String sysUserId:sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId,sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id",sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if(one==null){
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("The addition was successful!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("Something went wrong: " + e.getMessage());
            return result;
        }
    }
    /**
     *   Deletes the user relationship for the specified role
     * @param
     * @return
     */
    @RequiresPermissions("system:user:deleteRole")
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("The deletion is successful!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Deletion failed！");
        }
        return result;
    }

    /**
     * Delete user relationships for specified roles in batches
     *
     * @param
     * @return
     */
    @RequiresPermissions("system:user:deleteRoleBatch")
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("The deletion is successful!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Deletion failed！");
        }
        return result;
    }

    /**
     * List of department users
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        //根据部门ID查询,当前和下级所有的部门IDS
        List<String> subDepids = new ArrayList<>();
        //部门id为空时，查询我的部门下所有用户
        if(oConvertUtils.isEmpty(depId)){
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null?user.getUserIdentity():CommonConstant.USER_IDENTITY_1;
            if(oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2 ){
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        }else{
            subDepids = sysDepartService.getSubDepIdsByDepId(depId);
        }
        if(subDepids != null && subDepids.size()>0){
            IPage<SysUser> pageList = sysUserService.getUserByDepIds(page,subDepids,username);
            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
            List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
            if(userIds!=null && userIds.size()>0){
                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
                pageList.getRecords().forEach(item -> {
                    //批量查询用户的所属部门
                    item.setOrgCode(useDepNames.get(item.getId()));
                });
            }
            //update-begin---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
            //设置租户id
            page.setRecords(userTenantService.setUserTenantIds(page.getRecords()));
            //update-end---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
            result.setSuccess(true);
            result.setResult(pageList);
        }else{
            result.setSuccess(true);
            result.setResult(null);
        }
        return result;
    }


    /**
     * ACCORDING TO orgCode Query users, including users in subdepartments
     * If a user has multiple departments, multiple records will be displayed, and you can process them into a single record
     */
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUser userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * ACCORDING TO orgCode Query users, including users in subdepartments
     * The interface for the address book module merges users from multiple departments into a single record and converts it into a front-end-friendly format
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode",required = false) String orgCode,
            SysUser userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 记录所有出现过的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在过该用户，直接合并数据
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getDepartName()));
            } else {
                // 将用户对象转换为json格式，并将部门信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getDepartId());
                json.put("departName", item.getDepartName());
//                json.put("avatar", item.getSysUser().getAvatar());
                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return Result.ok(result);
    }

    /**
     * Add users to the specified department
     */
    @RequiresPermissions("system:user:editDepartWithUser")
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for(String sysUserId:sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null,sysUserId,sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id",sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if(one==null){
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("Added successfully!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("Something went wrong: " + e.getMessage());
            return result;
        }
    }

    /**
     *   Deletes the user relationship of a specified organization
     */
    @RequiresPermissions("system:user:deleteUserInDepart")
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name="depId") String depId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id",userId);
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                List<SysDepartRole> sysDepartRoleList = departRoleService.list(new QueryWrapper<SysDepartRole>().eq("depart_id",depId));
                List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                if(roleIds != null && roleIds.size()>0){
                    QueryWrapper<SysDepartRoleUser> query = new QueryWrapper<>();
                    query.eq("user_id",userId).in("drole_id",roleIds);
                    departRoleUserService.remove(query);
                }
                result.success("Deleted successfully!");
            }else{
                result.error500("The selected department is not associated with the user!");
            }
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Delete failed!");
        }
        return result;
    }

    /**
     * Delete user relationships for specified organizations in bulk
     */
    @RequiresPermissions("system:user:deleteUserInDepartBatch")
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name="depId") String depId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id",Arrays.asList(userIds.split(",")));
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                departRoleUserService.removeDeptRoleUser(Arrays.asList(userIds.split(",")),depId);
            }
            result.success("Deleted successfully!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Delete failed!");
        }
        return result;
    }
    
    /**
         *  Query all departments/current department codes for the current user
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserDeparts() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
        	LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String,Object> map = new HashMap(5);
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Query failed!");
        }
        return result;
    }

    


	/**
	 * User registration interface
	 * 
	 * @param jsonObject
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");

        //update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
		Object code = redisUtil.get(redisKey);
        //update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906

		String username = jsonObject.getString("username");
		//未设置用户名，则用手机号作为用户名
		if(oConvertUtils.isEmpty(username)){
            username = phone;
        }
        //未设置密码，则随机生成一个密码
		String password = jsonObject.getString("password");
		if(oConvertUtils.isEmpty(password)){
            password = RandomUtil.randomString(8);
        }
		String email = jsonObject.getString("email");
		SysUser sysUser1 = sysUserService.getUserByName(username);
		if (sysUser1 != null) {
			result.setMessage("The username is registered");
			result.setSuccess(false);
			return result;
		}
		SysUser sysUser2 = sysUserService.getUserByPhone(phone);
		if (sysUser2 != null) {
			result.setMessage("The mobile phone number is registered");
			result.setSuccess(false);
			return result;
		}

		if(oConvertUtils.isNotEmpty(email)){
            SysUser sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMessage("The email address has been registered");
                result.setSuccess(false);
                return result;
            }
        }
        if(null == code){
            result.setMessage("The verification code of the mobile phone is invalid, please re-obtain it");
            result.setSuccess(false);
            return result;
        }
		if (!smscode.equals(code.toString())) {
			result.setMessage("The verification code on your phone is incorrect");
			result.setSuccess(false);
			return result;
		}

        String realname = jsonObject.getString("realname");
        if(oConvertUtils.isEmpty(realname)){
            realname = username;
        }
        
		try {
			user.setCreateTime(new Date());// 设置创建时间
			String salt = oConvertUtils.randomGen(8);
			String passwordEncode = PasswordUtil.encrypt(username, password, salt);
			user.setSalt(salt);
			user.setUsername(username);
			user.setRealname(realname);
			user.setPassword(passwordEncode);
			user.setEmail(email);
			user.setPhone(phone);
			user.setStatus(CommonConstant.USER_UNFREEZE);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			user.setActivitiSync(CommonConstant.ACT_SYNC_1);
			sysUserService.addUserWithRole(user,"");//默认临时角色 test
			result.success("Registration is successful");
		} catch (Exception e) {
			result.error500("Registration failed");
		}
		return result;
	}

//	/**
//	 * 根据用户名或手机号查询用户信息
//	 * @param
//	 * @return
//	 */
//	@GetMapping("/querySysUser")
//	public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
//		String phone = sysUser.getPhone();
//		String username = sysUser.getUsername();
//		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (oConvertUtils.isNotEmpty(phone)) {
//			SysUser user = sysUserService.getUserByPhone(phone);
//			if(user!=null) {
//				map.put("username",user.getUsername());
//				map.put("phone",user.getPhone());
//				result.setSuccess(true);
//				result.setResult(map);
//				return result;
//			}
//		}
//		if (oConvertUtils.isNotEmpty(username)) {
//			SysUser user = sysUserService.getUserByName(username);
//			if(user!=null) {
//				map.put("username",user.getUsername());
//				map.put("phone",user.getPhone());
//				result.setSuccess(true);
//				result.setResult(map);
//				return result;
//			}
//		}
//		result.setSuccess(false);
//		result.setMessage("验证失败");
//		return result;
//	}

	/**
	 * Verify the user's mobile phone number
	 */
	@PostMapping("/phoneVerification")
	public Result<Map<String,String>> phoneVerification(@RequestBody JSONObject jsonObject) {
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
        //update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
        String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
		Object code = redisUtil.get(redisKey);
		if (!smscode.equals(code)) {
			result.setMessage("The verification code on your phone is incorrect");
			result.setSuccess(false);
			return result;
		}
		//设置有效时间
		redisUtil.set(redisKey, smscode,600);
        //update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906

		//新增查询用户名
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getPhone,phone);
        SysUser user = sysUserService.getOne(query);
        Map<String,String> map = new HashMap(5);
        map.put("smscode",smscode);
        if(null == user){
            //前端根据文字做判断用户是否存在判断，不能修改
            result.setMessage("User information does not exist");
            result.setSuccess(false);
            return result;
        }
        map.put("username",user.getUsername());
        result.setResult(map);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * The user changes the password
	 */
	@GetMapping("/passwordChange")
	public Result<SysUser> passwordChange(@RequestParam(name="username")String username,
										  @RequestParam(name="password")String password,
			                              @RequestParam(name="smscode")String smscode,
			                              @RequestParam(name="phone") String phone) {
        Result<SysUser> result = new Result<SysUser>();
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode)  || oConvertUtils.isEmpty(phone) ) {
            result.setMessage("Failed to reset password!");
            result.setSuccess(false);
            return result;
        }

        SysUser sysUser=new SysUser();
        //update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
        String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
        Object object= redisUtil.get(redisKey);
        //update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
        if(null==object) {
        	result.setMessage("SMS verification code is invalid!");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object.toString())) {
        	result.setMessage("SMS verification code doesn't match!");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,username).eq(SysUser::getPhone,phone));
        if (sysUser == null) {
            result.setMessage("No user found!");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            //update-begin---author:wangshuai ---date:20220316  for：[VUEN-234]密码重置添加敏感日志------------
            baseCommonService.addLog("RESET "+username+" Password, operator: " +sysUser.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
            //update-end---author:wangshuai ---date:20220316  for：[VUEN-234]密码重置添加敏感日志------------
            result.setSuccess(true);
            result.setMessage("Password reset complete!");
            return result;
        }
    }
	

	/**
	 * GET PARTIAL INFORMATION ABOUT THE USER BASED ON THE TOKEN (THE DATA RETURNED IS THE DATA THAT CAN BE USED BY THE FORM DESIGNER)
	 * 
	 * @return
	 */
	@GetMapping("/getUserSectionInfoByToken")
	public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
		try {
			String username = null;
			// 如果没有传递token，就从header中获取token并获取用户信息
			if (oConvertUtils.isEmpty(token)) {
				 username = JwtUtil.getUserNameByToken(request);
			} else {
				 username = JwtUtil.getUsername(token);				
			}

			log.debug(" ------ Obtain some user information through the token, the current user: " + username);

			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sysUserId", sysUser.getId());
			map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
			map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
			map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

			log.debug(" ------ Obtain part of the user information through the token, and the user information obtained: " + map);

			return Result.ok(map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "Query failed:" + e.getMessage());
		}
	}
	
	/**
	 * [APP interface] to obtain the list of users  According to the username and real name Fuzzy matching
	 * @param keyword
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/appUserList")
	public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "username", required = false) String username,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "syncFlow", required = false) String syncFlow) {
		try {
			//TODO In terms of query efficiency, it is recommended not to use MP encapsulated page for pagination query, and it is recommended to write pagination statements by yourself
			LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
			if(oConvertUtils.isNotEmpty(syncFlow)){
                query.eq(SysUser::getActivitiSync, CommonConstant.ACT_SYNC_1);
            }
			query.eq(SysUser::getDelFlag,CommonConstant.DEL_FLAG_0);
			if(oConvertUtils.isNotEmpty(username)){
			    if(username.contains(",")){
                    query.in(SysUser::getUsername,username.split(","));
                }else{
                    query.eq(SysUser::getUsername,username);
                }
            }else{
                query.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
            }
			Page<SysUser> page = new Page<>(pageNo, pageSize);
			IPage<SysUser> res = this.sysUserService.page(page, query);
			return Result.ok(res);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "Query failed:" + e.getMessage());
		}
		
	}

    /**
     * Get a list of tombstoned users without pagination
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public Result getRecycleBin() {
        List<SysUser> logicDeletedUserList = sysUserService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(SysUser::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(logicDeletedUserList);
    }

    /**
     * Restore a tombstoned user
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            SysUser updateUser = new SysUser();
            updateUser.setUpdateBy(JwtUtil.getUserNameByToken(request));
            updateUser.setUpdateTime(new Date());
            sysUserService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return Result.ok("The restore was successful");
    }

    /**
     * Delete the user completely
     *
     * @param userIds Deleted user IDs, multiple IDs separated by commas
     * @return
     */
    @RequiresPermissions("system:user:deleteRecycleBin")
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public Result deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("The deletion is successful");
    }


    /**
     * Modify user information on mobile
     * @param jsonObject
     * @return
     */
    @RequiresRoles({"admin"})
    @RequestMapping(value = "/appEdit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<SysUser> appEdit(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            SysUser sysUser = sysUserService.getUserByName(username);
            baseCommonService.addLog("Mobile edit user, ID: " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
            String realname=jsonObject.getString("realname");
            String avatar=jsonObject.getString("avatar");
            String sex=jsonObject.getString("sex");
            String phone=jsonObject.getString("phone");
            String email=jsonObject.getString("email");
            Date birthday=jsonObject.getDate("birthday");
            SysUser userPhone = sysUserService.getUserByPhone(phone);
            if(sysUser==null) {
                result.error500("No user found!");
            }else {
                if(userPhone!=null){
                    String userPhonename = userPhone.getUsername();
                    if(!userPhonename.equals(username)){
                        result.error500("The phone number already exists!");
                        return result;
                    }
                }
                if(StringUtils.isNotBlank(realname)){
                    sysUser.setRealname(realname);
                }
                if(StringUtils.isNotBlank(avatar)){
                    sysUser.setAvatar(avatar);
                }
                if(StringUtils.isNotBlank(sex)){
                    sysUser.setSex(Integer.parseInt(sex));
                }
                if(StringUtils.isNotBlank(phone)){
                    sysUser.setPhone(phone);
                }
                if(StringUtils.isNotBlank(email)){
                    //update-begin---author:wangshuai ---date:20220708  for：[VUEN-1528]积木官网邮箱重复，应该提示准确------------
                    LambdaQueryWrapper<SysUser> emailQuery = new LambdaQueryWrapper<>();
                    emailQuery.eq(SysUser::getEmail,email);
                    long count = sysUserService.count(emailQuery);
                    if (!email.equals(sysUser.getEmail()) && count!=0) {
                        result.error500("Failed to save, mailbox already exists!");
                        return result;
                    }
                    //update-end---author:wangshuai ---date:20220708  for：[VUEN-1528]积木官网邮箱重复，应该提示准确--------------
                    sysUser.setEmail(email);
                }
                if(null != birthday){
                    sysUser.setBirthday(birthday);
                }
                sysUser.setUpdateTime(new Date());
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Save failed!");
        }
        return result;
    }
    /**
     * Save the device information on the mobile terminal
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/saveClientId", method = RequestMethod.GET)
    public Result<SysUser> saveClientId(HttpServletRequest request,@RequestParam("clientId")String clientId) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            SysUser sysUser = sysUserService.getUserByName(username);
            if(sysUser==null) {
                result.error500("No user found!");
            }else {
                sysUser.setClientId(clientId);
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("Operation failed!");
        }
        return result;
    }
    /**
     * Obtain user information and department employee information based on userID
     *
     * @return Result
     */
    @GetMapping("/queryChildrenByUsername")
    public Result queryChildrenByUsername(@RequestParam("userId") String userId) {
        //获取用户信息
        Map<String,Object> map=new HashMap(5);
        SysUser sysUser = sysUserService.getById(userId);
        String username = sysUser.getUsername();
        Integer identity = sysUser.getUserIdentity();
        map.put("sysUser",sysUser);
        if(identity!=null && identity==2){
            //获取部门用户信息
            String departIds = sysUser.getDepartIds();
            if(StringUtils.isNotBlank(departIds)){
                List<String> departIdList = Arrays.asList(departIds.split(","));
                List<SysUser> childrenUser = sysUserService.queryByDepIds(departIdList,username);
                map.put("children",childrenUser);
            }
        }
        return Result.ok(map);
    }
    /**
     * Query department user information on the mobile terminal
     * @param departId
     * @return
     */
    @GetMapping("/appQueryByDepartId")
    public Result<List<SysUser>> appQueryByDepartId(@RequestParam(name="departId", required = false) String departId) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<String> list=new ArrayList<String> ();
        list.add(departId);
        List<SysUser> childrenUser = sysUserService.queryByDepIds(list,null);
        result.setResult(childrenUser);
        return result;
    }
    /**
     * Query user information on mobile (query through user name fuzziness)
     * @param keyword
     * @return
     */
    @GetMapping("/appQueryUser")
    public Result<List<SysUser>> appQueryUser(@RequestParam(name = "keyword", required = false) String keyword,
                                              @RequestParam(name = "username", required = false) String username,
                                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest request) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<SysUser>();
        //TODO External simulation login to the temporary account, the list is not displayed
        queryWrapper.ne(SysUser::getUsername,"_reserve_user_external");
        //增加 username传参
        if(oConvertUtils.isNotEmpty(username)){
            if(username.contains(",")){
                queryWrapper.in(SysUser::getUsername,username.split(","));
            }else{
                queryWrapper.eq(SysUser::getUsername,username);
            }
        }else if(StringUtils.isNotBlank(keyword)){
            queryWrapper.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
        }
        //------------------------------------------------------------------------------------------------
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            String tenantId = TokenUtils.getTenantIdByRequest(request);
            //update-begin---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
            List<String> userIds = userTenantService.getUserIdsByTenantId(Integer.valueOf(tenantId));
            if (oConvertUtils.listIsNotEmpty(userIds)) {
                queryWrapper.in(SysUser::getId, userIds);
            }
            //update-end---author:wangshuai ---date:20221223  for：[QQYUN-3371]租户逻辑改造，改成关系表------------
        }
        //------------------------------------------------------------------------------------------------
        Page<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = this.sysUserService.page(page, queryWrapper);
        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            pageList.getRecords().forEach(item->{
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        result.setResult(pageList.getRecords());
        return result;
    }

    /**
     * Modify the mobile phone number based on the user name [this method is not used]
     * @param json
     * @return
     */
    @RequestMapping(value = "/updateMobile", method = RequestMethod.PUT)
    public Result<?> changMobile(@RequestBody JSONObject json,HttpServletRequest request) {
        String smscode = json.getString("smscode");
        String phone = json.getString("phone");
        Result<SysUser> result = new Result<SysUser>();
        //获取登录用户名
        String username = JwtUtil.getUserNameByToken(request);
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(smscode) || oConvertUtils.isEmpty(phone)) {
            result.setMessage("Failed to change mobile phone number!");
            result.setSuccess(false);
            return result;
        }
        //update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
        String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
        Object object= redisUtil.get(redisKey);
        //update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
        if(null==object) {
            result.setMessage("SMS verification code is invalid!");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object.toString())) {
            result.setMessage("SMS verification code doesn't match!");
            result.setSuccess(false);
            return result;
        }
        SysUser user = sysUserService.getUserByName(username);
        if(user==null) {
            return Result.error("The user doesn't exist!");
        }
        user.setPhone(phone);
        sysUserService.updateById(user);
        return Result.ok("The mobile phone number has been set successfully!");
    }


    /**
     * Perform an in query based on the attribute value in the object Properties may change User component
     * @param sysUser
     * @return
     */
    @GetMapping("/getMultiUser")
    public List<SysUser> getMultiUser(SysUser sysUser){
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, null);
        //update-begin---author:wangshuai ---date:20220104  for：[JTC-297]已冻结用户仍可设置为代理人------------
        queryWrapper.eq("status",Integer.parseInt(CommonConstant.STATUS_1));
        //update-end---author:wangshuai ---date:20220104  for：[JTC-297]已冻结用户仍可设置为代理人------------
        List<SysUser> ls = this.sysUserService.list(queryWrapper);
        for(SysUser user: ls){
            user.setPassword(null);
            user.setSalt(null);
        }
        return ls;
    }
    
    /**
     * CHAT Create a chat component that is dedicated  Query by user account, user name, and department ID
     * @param departId 部门id
     * @param keyword 搜索值
     * @return
     */
    @GetMapping(value = "/getUserInformation")
    public Result<IPage<SysUser>> getUserInformation(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "departId", required = false) String departId,
            @RequestParam(name="keyword",required=false) String keyword) {
        //------------------------------------------------------------------------------------------------
        Integer tenantId = null;
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
            tenantId = oConvertUtils.getInt(TenantContext.getTenant(),0);
        }
        //------------------------------------------------------------------------------------------------
        IPage<SysUser> pageList = sysUserDepartService.getUserInformation(tenantId,departId, keyword, pageSize, pageNo);
        return Result.OK(pageList);
    }

    /**
     * The simplified version of the process user selects the component
     * @param departId Department ID
     * @param roleId Character ID
     * @param keyword Search for a value
     * @return
     */
    @GetMapping(value = "/selectUserList")
    public Result<IPage<SysUser>> selectUserList(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "departId", required = false) String departId,
            @RequestParam(name = "roleId", required = false) String roleId,
            @RequestParam(name="keyword",required=false) String keyword) {
        //------------------------------------------------------------------------------------------------
        Integer tenantId = null;
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
            String tenantStr = TenantContext.getTenant();
            if(oConvertUtils.isNotEmpty(tenantStr)){
                tenantId = Integer.parseInt(tenantStr);
            }
        }
        //------------------------------------------------------------------------------------------------
        IPage<SysUser> pageList = sysUserDepartService.getUserInformation(tenantId, departId,roleId, keyword, pageSize, pageNo);
        return Result.OK(pageList);
    }

    /**
     * 用户离职(新增代理人和用户状态变更操作)【低代码应用专用接口】
     * @param sysUserAgent
     * @return
     */
    @PutMapping("/userQuitAgent")
    public Result<String> userQuitAgent(@RequestBody SysUserAgent sysUserAgent){
        //Check whether the ID is empty
        if(oConvertUtils.isNotEmpty(sysUserAgent.getId())){
            sysUserAgentService.updateById(sysUserAgent);
        }else{
            sysUserAgentService.save(sysUserAgent);
        }
        sysUserService.userQuit(sysUserAgent.getUserName());
        return Result.ok("Successful resignation");
    }

    /**
     * Get a list of tombstoned users, no pagination [low-code application dedicated interface]
     *
     * @return List<SysUser>
     */
    @GetMapping("/getQuitList")
    public Result<List<SysUser>> getQuitList(HttpServletRequest req) {
        Integer tenantId = oConvertUtils.getInt(TokenUtils.getTenantIdByRequest(req),0);
        List<SysUser> quitList = sysUserService.getQuitList(tenantId);
        if (null != quitList && quitList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = quitList.stream().map(SysUser::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            quitList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(quitList);
    }

    /**
     * Updating the Deletion Status and Resignation Status [Low-Code Application Specific Interface]
     * @param jsonObject
     * @return Result<String>
     */
    @PutMapping("/putCancelQuit")
    public Result<String> putCancelQuit(@RequestBody JSONObject jsonObject, HttpServletRequest request){
        String userIds = jsonObject.getString("userIds");
        String usernames = jsonObject.getString("usernames");
        Integer tenantId = oConvertUtils.getInt(TokenUtils.getTenantIdByRequest(request),0);
        //将状态改成未删除
        if (StringUtils.isNotBlank(userIds)) {
            userTenantService.putCancelQuit(Arrays.asList(userIds.split(SymbolConstant.COMMA)),tenantId);
        }
        if(StringUtils.isNotEmpty(usernames)){
            //根据用户名删除代理人
            LambdaQueryWrapper<SysUserAgent> query = new LambdaQueryWrapper<>();
            query.in(SysUserAgent::getUserName,Arrays.asList(usernames.split(SymbolConstant.COMMA)));
            sysUserAgentService.remove(query);
        }
        return Result.ok("The termination was successful");
    }

    /**
     * Obtaining User Information (Dedicated to Vue 3 User Settings) [Low-Code Application Dedicated Interface]
     * @return
     */
    @GetMapping("/login/setting/getUserData")
    public Result<SysUser> getUserData(HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        SysUser user = sysUserService.getUserByName(username);
        if(user==null) {
            return Result.error("The user data was not found");
        }

        //update-begin---author:wangshuai ---date:20230220  for：[QQYUN-3980]组织管理中 职位功能 职位表加租户id 加职位-用户关联表------------
        //获取用户id通过职位数据
        List<SysPosition> sysPositionList = sysPositionService.getPositionList(user.getId());
        if(null != sysPositionList && sysPositionList.size()>0){
        //update-end---author:wangshuai ---date:20230220  for：[QQYUN-3980]组织管理中 职位功能 职位表加租户id 加职位-用户关联表------------
            StringBuilder nameBuilder = new StringBuilder();
            StringBuilder idBuilder = new StringBuilder();
            String verticalBar = " | ";
            for (SysPosition sysPosition:sysPositionList){
                nameBuilder.append(sysPosition.getName()).append(verticalBar);
                idBuilder.append(sysPosition.getId()).append(SymbolConstant.COMMA);
            }
            String names = nameBuilder.toString();
            if(oConvertUtils.isNotEmpty(names)){
                names = names.substring(0,names.lastIndexOf(verticalBar));
                user.setPostText(names);
            }
            //拼接职位id
            String ids = idBuilder.toString();
            if(oConvertUtils.isNotEmpty(ids)){
                ids = ids.substring(0,ids.lastIndexOf(SymbolConstant.COMMA));
                user.setPost(ids);
            }
        }
        return Result.ok(user);
    }

    /**
     * User Editing (Vue 3 User Settings Only) [Low-Code Application Dedicated Interface]
     * @param sysUser
     * @return
     */
    @PostMapping("/login/setting/userEdit")
    @RequiresPermissions("system:user:setting:edit")
    public Result<String> userEdit(@RequestBody SysUser sysUser, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        SysUser user = sysUserService.getById(sysUser.getId());
        if(user==null) {
           return Result.error("The user data was not found");
        }
        if(!username.equals(user.getUsername())){
            return Result.error("You can only modify your own data");
        }
        sysUserService.updateById(sysUser);
        return Result.ok("The update of personal information is successful");
    }

    /**
     * Batch Modification [low-app]
     * @param jsonObject
     * @return
     */
    @PutMapping("/batchEditUsers")
    public Result<SysUser> batchEditUsers(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            sysUserService.batchEditUsers(jsonObject);
            result.setSuccess(true);
            result.setMessage("The operation was successful！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("The operation failed");
        }
        return result;
    }

    /**
     * Search for departments and users by keyword [low-app]
     * @param keyword
     * @return
     */
    @GetMapping("/searchByKeyword")
    public Result<DepartAndUserInfo> searchByKeyword(@RequestParam(name="keyword",required=false) String keyword) {
        DepartAndUserInfo info = sysUserService.searchByKeyword(keyword);
        return Result.ok(info);
    }

    /**
     * Get department-related information before editing a department [low-app]
     * @param id
     * @return
     */
    @GetMapping("/getUpdateDepartInfo")
    public Result<UpdateDepartInfo> getUpdateDepartInfo(@RequestParam(name="id",required=false) String id) {
        UpdateDepartInfo info = sysUserService.getUpdateDepartInfo(id);
        return Result.ok(info);
    }

    /**
     * Editorial Department 【low-app】
     * @param updateDepartInfo
     * @return
     */
    @PutMapping("/doUpdateDepartInfo")
    public Result<?> doUpdateDepartInfo(@RequestBody UpdateDepartInfo updateDepartInfo) {
        sysUserService.doUpdateDepartInfo(updateDepartInfo);
        return Result.ok();
    }

    /**
     * Set Person in Charge Cancel the person in charge
     * @param json
     * @return
     */
    @PutMapping("/changeDepartChargePerson")
    public Result<?> changeDepartChargePerson(@RequestBody JSONObject json) {
        sysUserService.changeDepartChargePerson(json);
        return Result.ok();
    }

    /**
     * Modifying Users in a Tenant [Low-Code Application Dedicated Interface]
     * @param sysUser
     * @param req
     * @return
     */
    @RequestMapping(value = "/editTenantUser", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> editTenantUser(@RequestBody SysUser sysUser,HttpServletRequest req){
        Result<String> result = new Result<>();
        String tenantId = TokenUtils.getTenantIdByRequest(req);
        if(oConvertUtils.isEmpty(tenantId)){
            return result.error500("No right to modify other people's information!");
        }
        LambdaQueryWrapper<SysUserTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysUserTenant::getTenantId,Integer.valueOf(tenantId));
        query.eq(SysUserTenant::getUserId,sysUser.getId());
        SysUserTenant one = userTenantService.getOne(query);
        if(null == one){
            return result.error500("Users who are not members of the current tenant are not allowed to be modified！");
        }
        String departs = req.getParameter("selecteddeparts");
        sysUserService.editTenantUser(sysUser,tenantId,departs,null);
        return Result.ok("The modification was successful");
    }

    /**
     * When you switch tenants Modifications are required loginTenantId
     * QQYUN-4491 【Application】Some minor issues  1. The tenant who was logged in last time is not remembered for the next login
     * @param sysUser
     * @return
     */
    @PutMapping("/changeLoginTenantId")
    public Result<?> changeLoginTenantId(@RequestBody SysUser sysUser){
        Result<String> result = new Result<>();
        Integer tenantId = sysUser.getLoginTenantId();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        
        // 判断 指定的租户ID是不是当前登录用户的租户
        LambdaQueryWrapper<SysUserTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysUserTenant::getTenantId, tenantId);
        query.eq(SysUserTenant::getUserId, userId);
        SysUserTenant one = userTenantService.getOne(query);
        if(null == one){
            return result.error500("Users who are not under the tenant are not allowed to be modified.");
        }
        
        // 修改 loginTenantId
        LambdaQueryWrapper<SysUser> update = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, userId);
        SysUser updateUser = new SysUser();
        updateUser.setLoginTenantId(tenantId);
        sysUserService.update(updateUser, update);
        return Result.ok();
    } 

    /**
     * App user exports
     * @param request
     * @return
     */
    @RequestMapping(value = "/exportAppUser")
    public ModelAndView exportAppUser(HttpServletRequest request) {
        return sysUserService.exportAppUser(request);
    }
    
   /**
     * App user import
     * @param request
     * @return
     */
    @RequestMapping(value = "/importAppUser", method = RequestMethod.POST)
    public Result<?> importAppUser(HttpServletRequest request, HttpServletResponse response)throws IOException {
        return sysUserService.importAppUser(request);
    }
}
