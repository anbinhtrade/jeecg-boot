package org.jeecg.modules.system.controller;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.vo.SysUserRoleCountVo;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Role table Front-end controllers
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {
	@Autowired
	private ISysRoleService sysRoleService;
	
	@Autowired
	private ISysPermissionDataRuleService sysPermissionDataRuleService;
	
	@Autowired
	private ISysRolePermissionService sysRolePermissionService;
	
	@Autowired
	private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;
	@Autowired
	private BaseCommonService baseCommonService;
	
	/**
	  * Paginated list queries [System roles, no tenant isolation]
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequiresPermissions("system:role:list")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysRole>> queryPageList(SysRole role,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
		//QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		//IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		//换成不做租户隔离的方法，实际上还是存在缺陷（缺陷：如果开启租户隔离，虽然能看到其他租户下的角色，编辑会提示报错）
		IPage<SysRole> pageList = sysRoleService.listAllSysRole(page, role);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * Paginated List Query [Tenant Role, Do Tenant Isolation]
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/listByTenant", method = RequestMethod.GET)
	public Result<IPage<SysRole>> listByTenant(SysRole role,
												@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												HttpServletRequest req) {
		Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
		//------------------------------------------------------------------------------------------------
		//此接口必须通过租户来隔离查询
		role.setTenantId(oConvertUtils.getInt(!"0".equals(TenantContext.getTenant()) ? TenantContext.getTenant() : "", -1));
		
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   Add to
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("system:role:add")
	public Result<SysRole> add(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		try {
			//开启多租户隔离,角色id自动生成10位
			if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
				role.setRoleCode(RandomUtil.randomString(10));
			}
			role.setCreateTime(new Date());
			sysRoleService.save(role);
			result.success("Added successfully!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("The operation failed");
		}
		return result;
	}
	
	/**
	  *  EDIT
	 * @param role
	 * @return
	 */
    @RequiresPermissions("system:role:edit")
	@RequestMapping(value = "/edit",method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<SysRole> edit(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(role.getId());
		if(sysrole==null) {
			result.error500("No character found!");
		}else {
			role.setUpdateTime(new Date());

			//------------------------------------------------------------------
			//如果是saas隔离的情况下，判断当前租户id是否是当前租户下的
			if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
				//获取当前用户
				LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				Integer tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
				String username = "admin";
				if (!tenantId.equals(role.getTenantId()) && !username.equals(sysUser.getUsername())) {
					baseCommonService.addLog("Modify the role ID of a non-tenant without authorization:" + role.getId() + ", Operated by:" + sysUser.getUsername(), CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_3);
					return Result.error("The modification of the role failed, and the current role is not in this tenant.");
				}
			}
			//------------------------------------------------------------------
			
			boolean ok = sysRoleService.updateById(role);
			if(ok) {
				result.success("Modification successful!");
			}
		}
		return result;
	}
	
	/**
	  *   Delete by ID
	 * @param id
	 * @return
	 */
    @RequiresPermissions("system:role:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
    	//如果是saas隔离的情况下，判断当前租户id是否是当前租户下的
    	if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			//获取当前用户
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
			Long getRoleCount = sysRoleService.getRoleCountByTenantId(id, tenantId);
			String username = "admin";
			if(getRoleCount == 0 && !username.equals(sysUser.getUsername())){
				baseCommonService.addLog("To delete a role ID that is not a tenant without authorization:" + id + ", Operated by:" + sysUser.getUsername(), CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_4);
				return Result.error("Deleting the role failed, and the current role is not in this tenant.");
			}
		}
		sysRoleService.deleteRole(id);
		return Result.ok("The role was deleted");
	}
	
	/**
	  *  Delete in bulk
	 * @param ids
	 * @return
	 */
    @RequiresPermissions("system:role:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		baseCommonService.addLog("To delete a role, the role IDS:" + ids, CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_4);
		Result<SysRole> result = new Result<SysRole>();
		if(oConvertUtils.isEmpty(ids)) {
			result.error500("Unchecked!");
		}else {
			//如果是saas隔离的情况下，判断当前租户id是否是当前租户下的
			if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
				int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
				String[] roleIds = ids.split(SymbolConstant.COMMA);
				LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
				String username = "admin";
				for (String id:roleIds) {
					Long getRoleCount = sysRoleService.getRoleCountByTenantId(id, tenantId);
					//如果存在角色id为0，即不存在，则删除角色
					if(getRoleCount == 0 && !username.equals(sysUser.getUsername()) ){
						baseCommonService.addLog("To delete a role ID that is not a tenant without authorization:" + id + ", Operated by:" + sysUser.getUsername(), CommonConstant.LOG_TYPE_2, CommonConstant.OPERATE_TYPE_4);
						return Result.error("If the bulk deletion of a role fails, and the existing roles are not in this tenant, the bulk deletion is prohibited");
					}
				}
			}
			sysRoleService.deleteBatchRole(ids.split(","));
			result.success("Character deletion successful!");
		}
		return result;
	}
	
	/**
	  * Query by ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysRole> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(id);
		if(sysrole==null) {
			result.error500("No corresponding entity found");
		}else {
			result.setResult(sysrole);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * Querying All Roles (Participating in Tenant Isolation)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryall", method = RequestMethod.GET)
	public Result<List<SysRole>> queryall() {
		Result<List<SysRole>> result = new Result<>();
		LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
		//------------------------------------------------------------------------------------------------
		//是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			query.eq(SysRole::getTenantId, oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		List<SysRole> list = sysRoleService.list(query);
		if(list==null||list.size()<=0) {
			result.error500("No role information found");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * Querying All System Roles (Without Tenant Isolation)
	 *
	 * @return
	 */
	@RequiresPermissions("system:role:queryallNoByTenant")
	@RequestMapping(value = "/queryallNoByTenant", method = RequestMethod.GET)
	public Result<List<SysRole>> queryallNoByTenant() {
		Result<List<SysRole>> result = new Result<>();
		LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
		List<SysRole> list = sysRoleService.list(query);
		if(list==null||list.size()<=0) {
			result.error500("No role information found");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * The verification role code is unique
	 */
	@RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
	public Result<Boolean> checkUsername(String id,String roleCode) {
		Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
		result.setResult(true);
		log.info("--Verify that the role code is unique --- ID:"+id+"--roleCode:"+roleCode);
		try {
			SysRole role = null;
			if(oConvertUtils.isNotEmpty(id)) {
				role = sysRoleService.getById(id);
			}
			//SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
			SysRole newRole = sysRoleService.getRoleNoTenant(roleCode);
			if(newRole!=null) {
				//如果根据传入的roleCode查询到信息了，那么就需要做校验了。
				if(role==null) {
					//role为空=>新增模式=>只要roleCode存在则返回false
					result.setSuccess(false);
					result.setMessage("The role code already exists");
					return result;
				}else if(!id.equals(newRole.getId())) {
					//否则=>编辑模式=>判断两者ID是否一致-
					result.setSuccess(false);
					result.setMessage("The role code already exists");
					return result;
				}
			}
		} catch (Exception e) {
			result.setSuccess(false);
			result.setResult(false);
			result.setMessage(e.getMessage());
			return result;
		}
		result.setSuccess(true);
		return result;
	}

	/**
	 * Export to Excel
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysRole sysRole,HttpServletRequest request) {
		//------------------------------------------------------------------------------------------------
		//是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysRole.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		
		// Step.1 组装查询条件
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysRole> pageList = sysRoleService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME,"List of roles");
		mv.addObject(NormalExcelConstants.CLASS,SysRole.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("Role list data","Exporter:"+user.getRealname(),"Export information"));
		mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
		return mv;
	}

	/**
	 * Import data via Excel
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				return sysRoleService.importExcelCheckRoleCode(file, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("File import failed:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return Result.error("File import failed!");
	}
	
	/**
	 * Query data rule data
	 */
	@GetMapping(value = "/datarule/{permissionId}/{roleId}")
	public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("roleId") String roleId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return Result.error("Permission configuration information not found");
		}else {
			Map<String,Object> map = new HashMap(5);
			map.put("datarule", list);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.isNotNull(SysRolePermission::getDataRuleIds)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				//return Result.error("未找到角色菜单配置信息");
			}else {
				String drChecked = sysRolePermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return Result.ok(map);
			//TODO Later the query of the button permission also goes to this request It's nothing more than adding two more keys to the map
		}
	}
	
	/**
	 * Save the data rule to the role menu association table
	 */
	@PostMapping(value = "/datarule")
	public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String roleId = jsonObject.getString("roleId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("Save the data rule >>"+"Menu ID:"+permissionId+"Role ID:"+ roleId+"Data Permission ID:"+dataRuleIds);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				return Result.error("Please save the role menu permissions first!");
			}else {
				sysRolePermission.setDataRuleIds(dataRuleIds);
				this.sysRolePermissionService.updateById(sysRolePermission);
			}
		} catch (Exception e) {
			log.error("SysRoleController.saveDatarule()An exception has occurred：" + e.getMessage(),e);
			return Result.error("Save failed");
		}
		return Result.ok("Saved successfully!");
	}
	
	
	/**
	 * User role authorization function, query menu permission tree
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<Map<String,Object>> queryTreeList(HttpServletRequest request) {
		Result<Map<String,Object>> result = new Result<>();
		//全部权限ids
		List<String> ids = new ArrayList<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			for(SysPermission sysPer : list) {
				ids.add(sysPer.getId());
			}
			List<TreeModel> treeList = new ArrayList<>();
			getTreeModelList(treeList, list, null);
			Map<String,Object> resMap = new HashMap(5);
            //全部树节点数据
			resMap.put("treeList", treeList);
            //全部树ids
			resMap.put("ids", ids);
			result.setResult(resMap);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	private void getTreeModelList(List<TreeModel> treeList,List<SysPermission> metaList,TreeModel temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(),permission.getRuleFlag(), permission.isLeaf());
			if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}else if(temp!=null && tempPid!=null && tempPid.equals(temp.getKey())){
				temp.getChildren().add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}
			
		}
	}

    /**
     * TODO 权限未完成（敲敲云接口，租户应用）
     * Paginate to get the full list of roles (including the number of each role)
     * @return
     */
    @RequestMapping(value = "/queryPageRoleCount", method = RequestMethod.GET)
    public Result<IPage<SysUserRoleCountVo>> queryPageRoleCount(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Result<IPage<SysUserRoleCountVo>> result = new Result<>();
		LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
		//------------------------------------------------------------------------------------------------
		//是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			query.eq(SysRole::getTenantId, oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
        Page<SysRole> page = new Page<>(pageNo, pageSize);
        IPage<SysRole> pageList = sysRoleService.page(page, query);
        List<SysRole> records = pageList.getRecords();
        IPage<SysUserRoleCountVo> sysRoleCountPage = new PageDTO<>();
        List<SysUserRoleCountVo> sysCountVoList = new ArrayList<>();
        //循环角色数据获取每个角色下面对应的角色数量
        for (SysRole role:records) {
            LambdaQueryWrapper<SysUserRole> countQuery = new LambdaQueryWrapper<>();
			countQuery.eq(SysUserRole::getRoleId,role.getId());
            long count = sysUserRoleService.count(countQuery);
            SysUserRoleCountVo countVo = new SysUserRoleCountVo();
            BeanUtils.copyProperties(role,countVo);
            countVo.setCount(count);
            sysCountVoList.add(countVo);
        }
        sysRoleCountPage.setRecords(sysCountVoList);
        sysRoleCountPage.setTotal(pageList.getTotal());
        sysRoleCountPage.setSize(pageList.getSize());
        result.setSuccess(true);
        result.setResult(sysRoleCountPage);
        return result;
    }
}
