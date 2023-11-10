package org.jeecg.modules.system.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: Department roles
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags="Department roles")
@RestController
@RequestMapping("/sys/sysDepartRole")
public class SysDepartRoleController extends JeecgController<SysDepartRole, ISysDepartRoleService> {
	@Autowired
	private ISysDepartRoleService sysDepartRoleService;

	@Autowired
	private ISysDepartRoleUserService departRoleUserService;

	@Autowired
	private ISysDepartPermissionService sysDepartPermissionService;

	 @Autowired
	 private ISysDepartRolePermissionService sysDepartRolePermissionService;

	 @Autowired
	 private ISysDepartService sysDepartService;

	 @Autowired
     private BaseCommonService baseCommonService;
     
	/**
	 * Paginated list queries
	 *
	 * @param sysDepartRole
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="Department Roles - Paginated List Query", notes="Department Roles - Paginated List Query")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysDepartRole sysDepartRole,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   @RequestParam(name="deptId",required=false) String deptId,
								   HttpServletRequest req) {
		QueryWrapper<SysDepartRole> queryWrapper = QueryGenerator.initQueryWrapper(sysDepartRole, req.getParameterMap());
		Page<SysDepartRole> page = new Page<SysDepartRole>(pageNo, pageSize);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<String> deptIds = null;
//		if(oConvertUtils.isEmpty(deptId)){
//			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2) ){
//				deptIds = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
//			}else{
//				return Result.ok(null);
//			}
//		}else{
//			deptIds = sysDepartService.getSubDepIdsByDepId(deptId);
//		}
//		queryWrapper.in("depart_id",deptIds);

		//我的部门，选中部门只能看当前部门下的角色
		queryWrapper.eq("depart_id",deptId);
		IPage<SysDepartRole> pageList = sysDepartRoleService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * Add to
	 *
	 * @param sysDepartRole
	 * @return
	 */
    @RequiresPermissions("system:depart:role:add")
	@ApiOperation(value="Department Roles - Add", notes="Department Roles - Add")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysDepartRole sysDepartRole) {
		sysDepartRoleService.save(sysDepartRole);
		return Result.ok("添加成功！");
	}
	
	/**
	 * Edit
	 *
	 * @param sysDepartRole
	 * @return
	 */
	@ApiOperation(value="Department Role - Editor", notes="Department Role - Editor")
    @RequiresPermissions("system:depart:role:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SysDepartRole sysDepartRole) {
		sysDepartRoleService.updateById(sysDepartRole);
		return Result.ok("Edited successfully!");
	}
	
	/**
	 * Delete by ID
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Department Role - Removed by ID")
	@ApiOperation(value="Department Role - Removed by ID", notes="Department Role - Removed by ID")
    @RequiresPermissions("system:depart:role:delete")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysDepartRoleService.removeById(id);
		return Result.ok("The deletion is successful!");
	}
	
	/**
	 * Delete in bulk
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Department Roles - Delete in bulk")
	@ApiOperation(value="Department Roles - Delete in bulk", notes="Department Roles - Delete in bulk")
    @RequiresPermissions("system:depart:role:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysDepartRoleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("The batch deletion is successful！");
	}
	
	/**
	 * Query by ID
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="Department Role - Query by ID", notes="Department Role - Query by ID")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysDepartRole sysDepartRole = sysDepartRoleService.getById(id);
		return Result.ok(sysDepartRole);
	}

	 /**
	  * Obtain the role under the department
	  * @param departId
	  * @return
	  */
	@RequestMapping(value = "/getDeptRoleList", method = RequestMethod.GET)
	public Result<List<SysDepartRole>> getDeptRoleList(@RequestParam(value = "departId") String departId,@RequestParam(value = "userId") String userId){
		Result<List<SysDepartRole>> result = new Result<>();
		//Query the role of the selected department
		List<SysDepartRole> deptRoleList = sysDepartRoleService.list(new LambdaQueryWrapper<SysDepartRole>().eq(SysDepartRole::getDepartId,departId));
		result.setSuccess(true);
		result.setResult(deptRoleList);
		return result;
	}

	 /**
	  * Set up
	  * @param json
	  * @return
	  */
     @RequiresPermissions("system:depart:role:userAdd")
	 @RequestMapping(value = "/deptRoleUserAdd", method = RequestMethod.POST)
	 public Result<?> deptRoleAdd(@RequestBody JSONObject json) {
		 String newRoleId = json.getString("newRoleId");
		 String oldRoleId = json.getString("oldRoleId");
		 String userId = json.getString("userId");
		 departRoleUserService.deptRoleUserAdd(userId,newRoleId,oldRoleId);
         //update-begin---author:wangshuai ---date:20220316  for：[VUEN-234]部门角色分配添加敏感日志------------
         LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
         baseCommonService.addLog("Give the department user ID："+userId+"Assign roles and operate people： " +loginUser.getUsername() ,CommonConstant.LOG_TYPE_2, 2);
         //update-end---author:wangshuai ---date:20220316  for：[VUEN-234]部门角色分配添加敏感日志------------
         return Result.ok("The addition was successful！");
	 }

	 /**
	  * Obtain the configured department role based on the user ID
	  * @param userId
	  * @return
	  */
	 @RequestMapping(value = "/getDeptRoleByUserId", method = RequestMethod.GET)
	 public Result<List<SysDepartRoleUser>> getDeptRoleByUserId(@RequestParam(value = "userId") String userId,@RequestParam(value = "departId") String departId){
		 Result<List<SysDepartRoleUser>> result = new Result<>();
		 //Query the roles of the department
		 List<SysDepartRole> roleList = sysDepartRoleService.list(new QueryWrapper<SysDepartRole>().eq("depart_id",departId));
		 List<String> roleIds = roleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
		 //Query the authorized role based on the user ID of the role
		 List<SysDepartRoleUser> roleUserList = null;
		 if(roleIds!=null && roleIds.size()>0){
			 roleUserList = departRoleUserService.list(new QueryWrapper<SysDepartRoleUser>().eq("user_id",userId).in("drole_id",roleIds));
		 }
		 result.setSuccess(true);
		 result.setResult(roleUserList);
		 return result;
	 }

	 /**
	  * Query data rule data
	  */
	 @GetMapping(value = "/datarule/{permissionId}/{departId}/{roleId}")
	 public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("departId") String departId,@PathVariable("roleId") String roleId) {
		//Query authorized department rules
	 	List<SysPermissionDataRule> list = sysDepartPermissionService.getPermRuleListByDeptIdAndPermId(departId,permissionId);
		 if(list==null || list.size()==0) {
			 return Result.error("Permission configuration information not found");
		 }else {
			 Map<String,Object> map = new HashMap(5);
			 map.put("datarule", list);
			 LambdaQueryWrapper<SysDepartRolePermission> query = new LambdaQueryWrapper<SysDepartRolePermission>()
					 .eq(SysDepartRolePermission::getPermissionId, permissionId)
					 .eq(SysDepartRolePermission::getRoleId,roleId);
			 SysDepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
			 if(sysRolePermission==null) {
				 //return Result.error("No role menu configuration information found");
			 }else {
				 String drChecked = sysRolePermission.getDataRuleIds();
				 if(oConvertUtils.isNotEmpty(drChecked)) {
					 map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				 }
			 }
			 return Result.ok(map);
			 //TODO In the future, the query of the button authority will also go through this request, which is nothing more than adding two more keys in the map
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
			 log.info("Save data rules>>"+"Menu ID:"+permissionId+"Role ID:"+ roleId+"Data permission ID:"+dataRuleIds);
			 LambdaQueryWrapper<SysDepartRolePermission> query = new LambdaQueryWrapper<SysDepartRolePermission>()
					 .eq(SysDepartRolePermission::getPermissionId, permissionId)
					 .eq(SysDepartRolePermission::getRoleId,roleId);
			 SysDepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
			 if(sysRolePermission==null) {
				 return Result.error("Please save the role menu permissions first!");
			 }else {
				 sysRolePermission.setDataRuleIds(dataRuleIds);
				 this.sysDepartRolePermissionService.updateById(sysRolePermission);
			 }
		 } catch (Exception e) {
			 log.error("SysRoleController.saveDatarule()An exception has occurred：" + e.getMessage(),e);
			 return Result.error("Save failed");
		 }
		 return Result.ok("The save was successful!");
	 }

  /**
   * Export to Excel
   *
   * @param request
   * @param sysDepartRole
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SysDepartRole sysDepartRole) {
      return super.exportXls(request, sysDepartRole, SysDepartRole.class, "Department roles");
  }

  /**
   * Import data via Excel
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      return super.importExcel(request, response, SysDepartRole.class);
  }

}
