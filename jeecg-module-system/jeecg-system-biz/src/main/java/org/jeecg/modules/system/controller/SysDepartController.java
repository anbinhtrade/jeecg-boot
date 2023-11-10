package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.model.SysDepartTreeModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.lowapp.ExportDepartVo;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * Department Table Front-End Controllers
 * <p>
 * 
 * @Author: Steve @Since： 2019-01-22
 */
@RestController
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	/**
	 * Query Data Find out my department and respond to the frontend in a tree structure data format
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryMyDeptTreeList", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryMyDeptTreeList() {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
				//update-begin--Author:liusq  Date:20210624  for:部门查询ids为空后的前端显示问题 issues/I3UD06
				String departIds = user.getDepartIds();
				if(StringUtils.isNotBlank(departIds)){
					List<SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(departIds);
					result.setResult(list);
				}
				//update-end--Author:liusq  Date:20210624  for:部门查询ids为空后的前端显示问题 issues/I3UD06
				result.setMessage(CommonConstant.USER_IDENTITY_2.toString());
				result.setSuccess(true);
			}else{
				result.setMessage(CommonConstant.USER_IDENTITY_1.toString());
				result.setSuccess(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * Query data Isolate all departments and respond to the frontend in a tree structure data format
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryTreeList(@RequestParam(name = "ids", required = false) String ids) {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		try {
			// Read from memory
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
			if(oConvertUtils.isNotEmpty(ids)){
				List<SysDepartTreeModel> departList = sysDepartService.queryTreeList(ids);
				result.setResult(departList);
			}else{
				List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
				result.setResult(list);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * Asynchronously query the department list
	 * @param parentId Parent node is passed when loaded asynchronously
	 * @param ids The front-end echo is transitive
	 * @param primaryKey Primary key field (id or org code)
	 * @return
	 */
	@RequestMapping(value = "/queryDepartTreeSync", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryDepartTreeSync(@RequestParam(name = "pid", required = false) String parentId,@RequestParam(name = "ids", required = false) String ids, @RequestParam(name = "primaryKey", required = false) String primaryKey) {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		try {
			List<SysDepartTreeModel> list = sysDepartService.queryTreeListByPid(parentId,ids, primaryKey);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.setSuccess(false);
			result.setMessage("The query failed");
		}
		return result;
	}

	/**
	 * Get the IDs of all parent departments of a department
	 *
	 * @param departId Check according to the depart ID
	 * @param orgCode  According to the org code query, one of the depart id and org code must not be empty
	 */
	@GetMapping("/queryAllParentId")
	public Result queryParentIds(
			@RequestParam(name = "departId", required = false) String departId,
			@RequestParam(name = "orgCode", required = false) String orgCode) {
		try {
			JSONObject data;
			if (oConvertUtils.isNotEmpty(departId)) {
				data = sysDepartService.queryAllParentIdByDepartId(departId);
			} else if (oConvertUtils.isNotEmpty(orgCode)) {
				data = sysDepartService.queryAllParentIdByOrgCode(orgCode);
			} else {
				return Result.error("departId and orgCode can't be both null！");
			}
			return Result.OK(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * Add new data Add the object data of the department created by the user,and save to the database
	 * 
	 * @param sysDepart
	 * @return
	 */
    @RequiresPermissions("system:depart:add")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		Result<SysDepart> result = new Result<SysDepart>();
		String username = JwtUtil.getUserNameByToken(request);
		try {
			sysDepart.setCreateBy(username);
			sysDepartService.saveDepartData(sysDepart, username);
			//清除部门树内存
			// FindsDepartsChildrenUtil.clearSysDepartTreeList();
			// FindsDepartsChildrenUtil.clearDepartIdModel();
			result.success("The addition was successful！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}

	/**
	 * Edit Data Edit part of the department's data and save it to the database
	 * 
	 * @param sysDepart
	 * @return
	 */
    @RequiresPermissions("system:depart:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		String username = JwtUtil.getUserNameByToken(request);
		sysDepart.setUpdateBy(username);
		Result<SysDepart> result = new Result<SysDepart>();
		SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
		if (sysDepartEntity == null) {
			result.error500("No corresponding entity found");
		} else {
			boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
			// TODO What does returning false mean？
			if (ok) {
				//清除部门树内存
				//FindsDepartsChildrenUtil.clearSysDepartTreeList();
				//FindsDepartsChildrenUtil.clearDepartIdModel();
				result.success("The modification was successful!");
			}
		}
		return result;
	}
	
	 /**
     *   Delete by ID
    * @param id
    * @return
    */
    @RequiresPermissions("system:depart:delete")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
   public Result<SysDepart> delete(@RequestParam(name="id",required=true) String id) {

       Result<SysDepart> result = new Result<SysDepart>();
       SysDepart sysDepart = sysDepartService.getById(id);
       if(sysDepart==null) {
           result.error500("No corresponding entity found");
       }else {
           sysDepartService.deleteDepart(id);
			//清除部门树内存
		   //FindsDepartsChildrenUtil.clearSysDepartTreeList();
		   // FindsDepartsChildrenUtil.clearDepartIdModel();
		   result.success("The deletion is successful!");
       }
       return result;
   }


	/**
	 * Batch deletion Perform the operation of deleting the data of relevant departments on the database based on multiple IDs requested by the frontend
	 * 
	 * @param ids
	 * @return
	 */
    @RequiresPermissions("system:depart:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		Result<SysDepart> result = new Result<SysDepart>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized！");
		} else {
			this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
			result.success("The deletion is successful!");
		}
		return result;
	}

	/**
	 * The Query Data Add or Edit page makes a request for this method, loading the names of all departments in a tree structure for easy user operation
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
	public Result<List<DepartIdModel>> queryIdTree() {
//		Result<List<DepartIdModel>> result = new Result<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setResult(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setResult(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.setSuccess(false);
//			return result;
//		}
		Result<List<DepartIdModel>> result = new Result<>();
		try {
			List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}
	 
	/**
	 * <p>
	 * The department search function method is to search for related departments based on keyword fuzziness
	 * </p>
	 * 
	 * @param keyWord
	 * @return
	 */
	@RequestMapping(value = "/searchBy", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord,@RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
		Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
		//部门查询，myDeptSearch为1时为我的部门查询，登录用户为上级时查只查负责部门下数据
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String departIds = null;
		if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
			departIds = user.getDepartIds();
		}
		List<SysDepartTreeModel> treeList = this.sysDepartService.searchByKeyWord(keyWord,myDeptSearch,departIds);
		if (treeList == null || treeList.size() == 0) {
			result.setSuccess(false);
			result.setMessage("Matching data was not queried！");
			return result;
		}
		result.setResult(treeList);
		return result;
	}


	/**
     * Export to Excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepart sysDepart,HttpServletRequest request) {
		//------------------------------------------------------------------------------------------------
		//是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysDepart.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		
        // Step.1 组装查询条件
        QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepart> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepart>() {
            @Override
			public int compare(SysDepart arg0, SysDepart arg1) {
            	return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "List of departments");
        mv.addObject(NormalExcelConstants.CLASS, SysDepart.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Department list data", "Exporter:"+user.getRealname(), "Export information"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * Import data via Excel
	 * Departmental import scenario 1: The parent ID of the department is calculated through the organization code to maintain the relationship between superiors and subordinates;
	 * Departmental import scenario 2: You can also modify the program, the organization code is directly imported, and the parent ID is not set first; After all the imports are imported, write a SQL statement and fill in the parent ID.
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("system:depart:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<String> errorMessageList = new ArrayList<>();
		List<SysDepart> listSysDeparts = null;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
            	// The length of the org code
            	int codeLength = YouBianCodeUtil.ZHANWEI_LENGTH;
                listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), SysDepart.class, params);
                //Sort by length
                Collections.sort(listSysDeparts, new Comparator<SysDepart>() {
                    @Override
					public int compare(SysDepart arg0, SysDepart arg1) {
                    	return arg0.getOrgCode().length() - arg1.getOrgCode().length();
                    }
                });

                int num = 0;
                for (SysDepart sysDepart : listSysDeparts) {
                	String orgCode = sysDepart.getOrgCode();
                	if(orgCode.length() > codeLength) {
                		String parentCode = orgCode.substring(0, orgCode.length()-codeLength);
                		QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<SysDepart>();
                		queryWrapper.eq("org_code", parentCode);
                		try {
                		SysDepart parentDept = sysDepartService.getOne(queryWrapper);
                		if(!parentDept.equals(null)) {
							sysDepart.setParentId(parentDept.getId());
							//The update parent department is not a leaf node
							sysDepartService.updateIzLeaf(parentDept.getId(),CommonConstant.NOT_LEAF);
						} else {
							sysDepart.setParentId("");
						}
                		}catch (Exception e) {
                			//No parent Dept found
                		}
                	}else{
                		sysDepart.setParentId("");
					}
                    //update-begin---author:liusq   Date:20210223  for：批量导入部门以后，不能追加下一级部门 #2245------------
					sysDepart.setOrgType(sysDepart.getOrgCode().length()/codeLength+"");
                    //update-end---author:liusq   Date:20210223  for：批量导入部门以后，不能追加下一级部门 #2245------------
					sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
                    //update-begin---author:wangshuai ---date:20220105  for：[JTC-363]部门导入 机构类别没有时导入失败，赋默认值------------
					if(oConvertUtils.isEmpty(sysDepart.getOrgCategory())){
					    sysDepart.setOrgCategory("1");
                    }
                    //update-end---author:wangshuai ---date:20220105  for：[JTC-363]部门导入 机构类别没有时导入失败，赋默认值------------
					ImportExcelUtil.importDateSaveOne(sysDepart, ISysDepartService.class, errorMessageList, num, CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE);
					num++;
                }
				//清空部门缓存
				Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
				Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
				redisTemplate.delete(keys3);
				redisTemplate.delete(keys4);
				return ImportExcelUtil.imporReturnRes(errorMessageList.size(), listSysDeparts.size() - errorMessageList.size(), errorMessageList);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("File import failed:"+e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("File import failed！");
    }


	/**
	 * Query all department information
	 * @return
	 */
	@GetMapping("listAll")
	public Result<List<SysDepart>> listAll(@RequestParam(name = "id", required = false) String id) {
		Result<List<SysDepart>> result = new Result<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.orderByAsc(SysDepart::getOrgCode);
		if(oConvertUtils.isNotEmpty(id)){
			String[] arr = id.split(",");
			query.in(SysDepart::getId,arr);
		}
		List<SysDepart> ls = this.sysDepartService.list(query);
		result.setSuccess(true);
		result.setResult(ls);
		return result;
	}
	/**
	 * Query data Isolate all departments and respond to the frontend in a tree structure data format
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeByKeyWord", method = RequestMethod.GET)
	public Result<Map<String,Object>> queryTreeByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
		Result<Map<String,Object>> result = new Result<>();
		try {
			Map<String,Object> map=new HashMap(5);
			List<SysDepartTreeModel> list = sysDepartService.queryTreeByKeyWord(keyWord);
			//根据keyWord获取用户信息
			LambdaQueryWrapper<SysUser> queryUser = new LambdaQueryWrapper<SysUser>();
			queryUser.eq(SysUser::getDelFlag,CommonConstant.DEL_FLAG_0);
			queryUser.and(i -> i.like(SysUser::getUsername, keyWord).or().like(SysUser::getRealname, keyWord));
			List<SysUser> sysUsers = this.sysUserService.list(queryUser);
			map.put("userList",sysUsers);
			map.put("departList",list);
			result.setResult(map);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * Get department information based on department code
	 *
	 * @param orgCode
	 * @return
	 */
	@GetMapping("/getDepartName")
	public Result<SysDepart> getDepartName(@RequestParam(name = "orgCode") String orgCode) {
		Result<SysDepart> result = new Result<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<>();
		query.eq(SysDepart::getOrgCode, orgCode);
		SysDepart sysDepart = sysDepartService.getOne(query);
		result.setSuccess(true);
		result.setResult(sysDepart);
		return result;
	}

	/**
	 * Obtain user information based on department ID
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/getUsersByDepartId")
	public Result<List<SysUser>> getUsersByDepartId(@RequestParam(name = "id") String id) {
		Result<List<SysUser>> result = new Result<>();
		List<SysUser> sysUsers = sysUserDepartService.queryUserByDepId(id);
		result.setSuccess(true);
		result.setResult(sysUsers);
		return result;
	}

	/**
	 * @Function: Query in batches based on ID
	 * @param deptIds
	 * @return
	 */
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public Result<Collection<SysDepart>> queryByIds(@RequestParam String deptIds) {
		Result<Collection<SysDepart>> result = new Result<>();
		String[] ids = deptIds.split(",");
		Collection<String> idList = Arrays.asList(ids);
		Collection<SysDepart> deptList = sysDepartService.listByIds(idList);
		result.setSuccess(true);
		result.setResult(deptList);
		return result;
	}

	@GetMapping("/getMyDepartList")
    public Result<List<SysDepart>> getMyDepartList(){
        List<SysDepart> list = sysDepartService.getMyDepartList();
        return Result.ok(list);
    }

	/**
	 * Asynchronously query the department list
	 * @param parentId Parent node is passed when loaded asynchronously
	 * @return
	 */
	@RequestMapping(value = "/queryBookDepTreeSync", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryBookDepTreeSync(@RequestParam(name = "pid", required = false) String parentId,
																 @RequestParam(name = "tenantId") Integer tenantId,
																 @RequestParam(name = "departName",required = false) String departName) {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		try {
			List<SysDepartTreeModel> list = sysDepartService.queryBookDepTreeSync(parentId, tenantId, departName);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * Acquire users by department ID and tenant ID [Low-code apps: Used to select department heads]
	 * @param departId
	 * @return
	 */
	@GetMapping("/getUsersByDepartTenantId")
	public Result<List<SysUser>> getUsersByDepartTenantId(@RequestParam("departId") String departId){
		int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
		List<SysUser> sysUserList = sysUserDepartService.getUsersByDepartTenantId(departId,tenantId);
		return Result.ok(sysUserList);
	}

	/**
	 * Export to Excel [Low-code app: for exporting departments]
	 *
	 * @param request
	 */
	@RequestMapping(value = "/appExportXls")
	public ModelAndView appExportXls(SysDepart sysDepart,HttpServletRequest request) {
		// Step.1 Assemble query criteria
		int tenantId = oConvertUtils.getInt(TenantContext.getTenant(), 0);
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<ExportDepartVo> pageList = sysDepartService.getExcelDepart(tenantId);
		//Step.2 AutoPoi 导出Excel
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "List of departments");
		mv.addObject(NormalExcelConstants.CLASS, ExportDepartVo.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Department list data", "Exporter:"+user.getRealname(), "Export information"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * Import Excel [Low-code app: Used to export departments]
	 *
	 * @param request
	 */
	@RequestMapping(value = "/appImportExcel", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<?> appImportExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<String> errorMessageList = new ArrayList<>();
		List<ExportDepartVo> listSysDeparts = null;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), ExportDepartVo.class, params);
				sysDepartService.importExcel(listSysDeparts,errorMessageList);
				//清空部门缓存
				Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
				Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
				redisTemplate.delete(keys3);
				redisTemplate.delete(keys4);
				return ImportExcelUtil.imporReturnRes(errorMessageList.size(), listSysDeparts.size() - errorMessageList.size(), errorMessageList);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("File import failed:"+e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("File import failed!");
	}
	
}
