package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.ReflectHelper;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysCategoryService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description: Classification dictionary
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/category")
@Slf4j
public class SysCategoryController {
	@Autowired
	private ISysCategoryService sysCategoryService;

     /**
      * Classification code 0
      */
     private static final String CATEGORY_ROOT_CODE = "0";

	/**
	  * Paginated list queries
	 * @param sysCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/rootList")
	public Result<IPage<SysCategory>> queryPageList(SysCategory sysCategory,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		if(oConvertUtils.isEmpty(sysCategory.getPid())){
			sysCategory.setPid("0");
		}
		Result<IPage<SysCategory>> result = new Result<IPage<SysCategory>>();
		//------------------------------------------------------------------------------------------------
		//WHETHER TO ENABLE MULTI-TENANT DATA ISOLATION IN THE SYSTEM MANAGEMENT MODULE [SAAS MULTI-TENANT MODE]
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysCategory.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
		}
		//------------------------------------------------------------------------------------------------
		
		//--author:os_chengtgen---date:20190804 -----for: 分类字典页面显示错误,issues:377--------start
		//--author:liusq---date:20211119 -----for: 【vue3】分类字典页面查询条件配置--------start
		QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
		String name = sysCategory.getName();
		String code = sysCategory.getCode();
		//QueryWrapper<SysCategory> queryWrapper = new QueryWrapper<SysCategory>();
		if(StringUtils.isBlank(name)&&StringUtils.isBlank(code)){
			queryWrapper.eq("pid", sysCategory.getPid());
		}
		//--author:liusq---date:20211119 -----for: 分类字典页面查询条件配置--------end
		//--author:os_chengtgen---date:20190804 -----for:【vue3】 分类字典页面显示错误,issues:377--------end

		Page<SysCategory> page = new Page<SysCategory>(pageNo, pageSize);
		IPage<SysCategory> pageList = sysCategoryService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	@GetMapping(value = "/childList")
	public Result<List<SysCategory>> queryPageList(SysCategory sysCategory,HttpServletRequest req) {
		//------------------------------------------------------------------------------------------------
		//WHETHER TO ENABLE MULTI-TENANT DATA ISOLATION IN THE SYSTEM MANAGEMENT MODULE [SAAS MULTI-TENANT MODE]
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysCategory.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		Result<List<SysCategory>> result = new Result<List<SysCategory>>();
		QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
		List<SysCategory> list = sysCategoryService.list(queryWrapper);
		result.setSuccess(true);
		result.setResult(list);
		return result;
	}
	
	
	/**
	  * Add to
	 * @param sysCategory
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<SysCategory> add(@RequestBody SysCategory sysCategory) {
		Result<SysCategory> result = new Result<SysCategory>();
		try {
			sysCategoryService.addSysCategory(sysCategory);
			result.success("The addition was successful！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}
	
	/**
	  *  Edit
	 * @param sysCategory
	 * @return
	 */
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT,RequestMethod.POST })
	public Result<SysCategory> edit(@RequestBody SysCategory sysCategory) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategoryEntity = sysCategoryService.getById(sysCategory.getId());
		if(sysCategoryEntity==null) {
			result.error500("No corresponding entity found");
		}else {
			sysCategoryService.updateSysCategory(sysCategory);
			result.success("The modification was successful!");
		}
		return result;
	}
	
	/**
	  *   Delete by ID
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<SysCategory> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.error500("No corresponding entity found");
		}else {
			this.sysCategoryService.deleteSysCategory(id);
			result.success("The deletion is successful!");
		}
		
		return result;
	}
	
	/**
	  *  Delete in bulk
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<SysCategory> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysCategory> result = new Result<SysCategory>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized！");
		}else {
			this.sysCategoryService.deleteSysCategory(ids);
			result.success("The deletion is successful!");
		}
		return result;
	}
	
	/**
	  * Query by ID
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<SysCategory> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.error500("No corresponding entity found");
		}else {
			result.setResult(sysCategory);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * Export to Excel
   *
   * @param request
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SysCategory sysCategory) {
	  //------------------------------------------------------------------------------------------------
	  //Specifies whether to enable multi-tenant data isolation in the system management module【SAAS多租户模式】
	  if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
		  sysCategory.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
	  }
	  //------------------------------------------------------------------------------------------------
	  
      // Step.1 Assemble query criteria to query data
      QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, request.getParameterMap());
      List<SysCategory> pageList = sysCategoryService.list(queryWrapper);
      // Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      // Filter the selected data
      String selections = request.getParameter("selections");
      if(oConvertUtils.isEmpty(selections)) {
    	  mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      }else {
    	  List<String> selectionList = Arrays.asList(selections.split(","));
    	  List<SysCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
    	  mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
      }
      //The name of the export file
      mv.addObject(NormalExcelConstants.FILE_NAME, "List of categorical dictionaries");
      mv.addObject(NormalExcelConstants.CLASS, SysCategory.class);
      LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Categorical dictionary list data", "Exporter:"+user.getRealname(), "Export information"));
      return mv;
  }

  /**
      * Import data via Excel
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException{
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
	  // Error Message
	  List<String> errorMessage = new ArrayList<>();
	  int successLines = 0, errorLines = 0;
	  for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // Obtain the object to which the file was uploaded
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<SysCategory> listSysCategorys = ExcelImportUtil.importExcel(file.getInputStream(), SysCategory.class, params);
			 //按照编码长度排序
              Collections.sort(listSysCategorys);
			  log.info("Sorted list====>",listSysCategorys);
              for (int i = 0; i < listSysCategorys.size(); i++) {
				  SysCategory sysCategoryExcel = listSysCategorys.get(i);
				  String code = sysCategoryExcel.getCode();
				  if(code.length()>3){
					  String pCode = sysCategoryExcel.getCode().substring(0,code.length()-3);
					  log.info("pCode====>",pCode);
					  String pId=sysCategoryService.queryIdByCode(pCode);
					  log.info("pId====>",pId);
					  if(StringUtils.isNotBlank(pId)){
						  sysCategoryExcel.setPid(pId);
					  }
				  }else{
					  sysCategoryExcel.setPid("0");
				  }
				  try {
					  sysCategoryService.save(sysCategoryExcel);
					  successLines++;
				  } catch (Exception e) {
					  errorLines++;
					  String message = e.getMessage().toLowerCase();
					  int lineNumber = i + 1;
					  // 通过索引名判断出错信息
					  if (message.contains(CommonConstant.SQL_INDEX_UNIQ_CATEGORY_CODE)) {
						  errorMessage.add("Clause " + lineNumber + " Yes：The classification code already exists, ignore the import.");
					  }  else {
						  errorMessage.add("Clause " + lineNumber + " Line: Unknown error, ignore import");
						  log.error(e.getMessage(), e);
					  }
				  }
              }
          } catch (Exception e) {
			  errorMessage.add("An exception has occurred：" + e.getMessage());
			  log.error(e.getMessage(), e);
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
  }
  
  
  
  /**
     * Load a single piece of data for echoing
   */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
 	public Result<SysCategory> loadOne(@RequestParam(name="field") String field,@RequestParam(name="val") String val) {
 		Result<SysCategory> result = new Result<SysCategory>();
 		try {
			//update-begin-author:taoyan date:2022-5-6 for: issues/3663 sql注入问题
			boolean isClassField = ReflectHelper.isClassField(field, SysCategory.class);
			if (!isClassField) {
				return Result.error("The field is invalid, please check!");
			}
			//update-end-author:taoyan date:2022-5-6 for: issues/3663 sql注入问题
 			QueryWrapper<SysCategory> query = new QueryWrapper<SysCategory>();
 			query.eq(field, val);
 			List<SysCategory> ls = this.sysCategoryService.list(query);
 			if(ls==null || ls.size()==0) {
 				result.setMessage("The query was fruitless");
 	 			result.setSuccess(false);
 			}else if(ls.size()>1) {
 				result.setMessage("The query data is abnormal,["+field+"]There are multiple values:"+val);
 	 			result.setSuccess(false);
 			}else {
 				result.setSuccess(true);
 				result.setResult(ls.get(0));
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 			result.setMessage(e.getMessage());
 			result.setSuccess(false);
 		}
 		return result;
 	}
   
    /**
	 * Load the node's child data
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	public Result<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name="pid") String pid) {
		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		try {
			List<TreeSelectModel> ls = this.sysCategoryService.queryListByPid(pid);
			result.setResult(ls);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}
    
    /**
     * Load a level 1 node / if it's synchronous then all data
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
   	public Result<List<TreeSelectModel>> loadTreeRoot(@RequestParam(name="async") Boolean async,@RequestParam(name="pcode") String pcode) {
   		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
   		try {
   			List<TreeSelectModel> ls = this.sysCategoryService.queryListByCode(pcode);
   			if(!async) {
   				loadAllCategoryChildren(ls);
   			}
   			result.setResult(ls);
   			result.setSuccess(true);
   		} catch (Exception e) {
   			e.printStackTrace();
   			result.setMessage(e.getMessage());
   			result.setSuccess(false);
   		}
   		return result;
   	}
  
    /**
	 * Recursive child nodes are used for synchronous loading
     */
  	private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
  		for (TreeSelectModel tsm : ls) {
			List<TreeSelectModel> temp = this.sysCategoryService.queryListByPid(tsm.getKey());
			if(temp!=null && temp.size()>0) {
				tsm.setChildren(temp);
				loadAllCategoryChildren(temp);
			}
		}
  	}

	 /**
	  * Check encoding
	  * @param pid
	  * @param code
	  * @return
	  */
	 @GetMapping(value = "/checkCode")
	 public Result<?> checkCode(@RequestParam(name="pid",required = false) String pid,@RequestParam(name="code",required = false) String code) {
		if(oConvertUtils.isEmpty(code)){
			return Result.error("Error, type encoding is empty!");
		}
		if(oConvertUtils.isEmpty(pid)){
			return Result.ok();
		}
		SysCategory parent = this.sysCategoryService.getById(pid);
		if(code.startsWith(parent.getCode())){
			return Result.ok();
		}else{
			return Result.error("The code does not meet the specification and must be used\""+parent.getCode()+"\"Beginning!");
		}

	 }


	 /**
	  * Classification Dictionary Tree Control Load node
	  * @param pid
	  * @param pcode
	  * @param condition
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
	 public Result<List<TreeSelectModel>> loadDict(@RequestParam(name="pid",required = false) String pid,@RequestParam(name="pcode",required = false) String pcode, @RequestParam(name="condition",required = false) String condition) {
		 Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		 //If the value of pid is passed, the role of pcode will be ignored
		 if(oConvertUtils.isEmpty(pid)){
		 	if(oConvertUtils.isEmpty(pcode)){
				result.setSuccess(false);
				result.setMessage("Error loading classification dictionary tree parameter. [null]!");
				return result;
			}else{
		 		if(ISysCategoryService.ROOT_PID_VALUE.equals(pcode)){
					pid = ISysCategoryService.ROOT_PID_VALUE;
				}else{
					pid = this.sysCategoryService.queryIdByCode(pcode);
				}
				if(oConvertUtils.isEmpty(pid)){
					result.setSuccess(false);
					result.setMessage("The Classification Dictionary Tree parameter is loading incorrectly.[code]!");
					return result;
				}
			}
		 }
		 Map<String, String> query = null;
		 if(oConvertUtils.isNotEmpty(condition)) {
			 query = JSON.parseObject(condition, Map.class);
		 }
		 List<TreeSelectModel> ls = sysCategoryService.queryListByPid(pid,query);
		 result.setSuccess(true);
		 result.setResult(ls);
		 return result;
	 }

	 /**
	  * Classification Dictionary Control Data Echo [Form Page]
	  *
	  * @param ids
	  * @param delNotExist If a key does not exist in the database,
	  *                    the default value is true, and if a key does not exist in the database, the key itself is returned
	  * @return
	  */
	 @RequestMapping(value = "/loadDictItem", method = RequestMethod.GET)
	 public Result<List<String>> loadDictItem(@RequestParam(name = "ids") String ids, @RequestParam(name = "delNotExist", required = false, defaultValue = "true") boolean delNotExist) {
		 Result<List<String>> result = new Result<>();
		 // 非空判断
		 if (StringUtils.isBlank(ids)) {
			 result.setSuccess(false);
			 result.setMessage("ids It can't be empty");
			 return result;
		 }
		 // 查询数据
		 List<String> textList = sysCategoryService.loadDictItem(ids, delNotExist);
		 result.setSuccess(true);
		 result.setResult(textList);
		 return result;
	 }

	 /**
	  * [List Page] Load Categorical Dictionary Data for value substitution
	  * @param code
	  * @return
	  */
	 @RequestMapping(value = "/loadAllData", method = RequestMethod.GET)
	 public Result<List<DictModel>> loadAllData(@RequestParam(name="code",required = true) String code) {
		 Result<List<DictModel>> result = new Result<List<DictModel>>();
		 LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<SysCategory>();
		 if(oConvertUtils.isNotEmpty(code) && !CATEGORY_ROOT_CODE.equals(code)){
			 query.likeRight(SysCategory::getCode,code);
		 }
		 List<SysCategory> list = this.sysCategoryService.list(query);
		 if(list==null || list.size()==0) {
			 result.setMessage("No data, wrong parameters. [code]");
			 result.setSuccess(false);
			 return result;
		 }
		 List<DictModel> rdList = new ArrayList<DictModel>();
		 for (SysCategory c : list) {
			 rdList.add(new DictModel(c.getId(),c.getName()));
		 }
		 result.setSuccess(true);
		 result.setResult(rdList);
		 return result;
	 }

	 /**
	  * Query child nodes in batches based on their parent IDs
	  * @param parentIds
	  * @return
	  */
	 @GetMapping("/getChildListBatch")
	 public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
		 try {
			 QueryWrapper<SysCategory> queryWrapper = new QueryWrapper<>();
			 List<String> parentIdList = Arrays.asList(parentIds.split(","));
			 queryWrapper.in("pid", parentIdList);
			 List<SysCategory> list = sysCategoryService.list(queryWrapper);
			 IPage<SysCategory> pageList = new Page<>(1, 10, list.size());
			 pageList.setRecords(list);
			 return Result.OK(pageList);
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 return Result.error("Failed to query child nodes in batches：" + e.getMessage());
		 }
	 }


}
