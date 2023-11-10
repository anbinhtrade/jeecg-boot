package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictQuery;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysDict;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.model.SysDictTree;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.vo.SysDictPage;
import org.jeecg.modules.system.vo.lowapp.SysDictVo;
import org.jeecgframework.poi.excel.ExcelImportCheckUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * <p>
 * Dictionary table Front-end controllers
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dict")
@Slf4j
public class SysDictController {

	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	private ISysDictItemService sysDictItemService;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RedisUtil redisUtil;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDict>> queryPageList(SysDict sysDict,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysDict>> result = new Result<IPage<SysDict>>();
		//------------------------------------------------------------------------------------------------
		//WHETHER TO ENABLE MULTI-TENANT DATA ISOLATION IN THE SYSTEM MANAGEMENT MODULE [SAAS MULTI-TENANT MODE]
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysDict.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
		}
		//------------------------------------------------------------------------------------------------
		QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, req.getParameterMap());
		Page<SysDict> page = new Page<SysDict>(pageNo, pageSize);
		IPage<SysDict> pageList = sysDictService.page(page, queryWrapper);
		log.debug("Query the current page："+pageList.getCurrent());
		log.debug("Query the number of current pages："+pageList.getSize());
		log.debug("The number of query results："+pageList.getRecords().size());
		log.debug("Total number of data："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * @Function: Get tree dictionary data
	 * @param sysDict
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/treeList", method = RequestMethod.GET)
	public Result<List<SysDictTree>> treeList(SysDict sysDict,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<List<SysDictTree>> result = new Result<>();
		LambdaQueryWrapper<SysDict> query = new LambdaQueryWrapper<>();
		// 构造查询条件
		String dictName = sysDict.getDictName();
		if(oConvertUtils.isNotEmpty(dictName)) {
			query.like(true, SysDict::getDictName, dictName);
		}
		query.orderByDesc(true, SysDict::getCreateTime);
		List<SysDict> list = sysDictService.list(query);
		List<SysDictTree> treeList = new ArrayList<>();
		for (SysDict node : list) {
			treeList.add(new SysDictTree(node));
		}
		result.setSuccess(true);
		result.setResult(treeList);
		return result;
	}

	/**
	 * Get all dictionary data
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryAllDictItems", method = RequestMethod.GET)
	public Result<?> queryAllDictItems(HttpServletRequest request) {
		Map<String, List<DictModel>> res = new HashMap(5);
		res = sysDictService.queryAllDictItems();
		return Result.ok(res);
	}

	/**
	 * Get dictionary data
	 * @param dictCode
	 * @return
	 */
	@RequestMapping(value = "/getDictText/{dictCode}/{key}", method = RequestMethod.GET)
	public Result<String> getDictText(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key) {
		log.info(" dictCode : "+ dictCode);
		Result<String> result = new Result<String>();
		String text = null;
		try {
			text = sysDictService.queryDictTextByKey(dictCode, key);
			 result.setSuccess(true);
			 result.setResult(text);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
			return result;
		}
		return result;
	}


	/**
	 * Get dictionary data 【Verify the signature of the interface】
	 * @param dictCode Dictionary code
	 * @param dictCode Table name, text field, code field  | Examples: sys user, realname, id
	 * @return
	 */
	@RequestMapping(value = "/getDictItems/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> getDictItems(@PathVariable("dictCode") String dictCode, @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {
		log.info(" dictCode : "+ dictCode);
		Result<List<DictModel>> result = new Result<List<DictModel>>();
		try {
			List<DictModel> ls = sysDictService.getDictItems(dictCode);
			if (ls == null) {
				result.error500("The dictionary Code is formatted incorrectly！");
				return result;
			}
			result.setSuccess(true);
			result.setResult(ls);
			log.debug(result.toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("The operation failed");
			return result;
		}
		return result;
	}

	/**
	 * [Interface Signature Verification]
	 * [JSearchSelectTag drop-down search component dedicated interface]
	 * Dictionaries for large data volumes are loaded asynchronously, i.e. the front-end input content filters the data
	 * @param dictCode dictionary code format: table, text, code
     * @return
	 */
	@RequestMapping(value = "/loadDict/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> loadDict(@PathVariable("dictCode") String dictCode,
			@RequestParam(name="keyword",required = false) String keyword,
			@RequestParam(value = "sign",required = false) String sign,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		
		//update-begin-author:taoyan date:2023-5-22 for: /issues/4905 因为中括号(%5)的问题导致的 表单生成器字段配置时，选择关联字段，在进行高级配置时，无法加载数据库列表，提示 Sgin签名校验错误！ #4905 RouteToRequestUrlFilter
		if(keyword!=null && keyword.indexOf("%5")>=0){
			try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("Failed to decode the drop-down search keyword", e);
			}
		}
		//update-end-author:taoyan date:2023-5-22 for: /issues/4905 因为中括号(%5)的问题导致的  表单生成器字段配置时，选择关联字段，在进行高级配置时，无法加载数据库列表，提示 Sgin签名校验错误！ #4905

		log.info(" Load dictionary table data and load keywords: "+ keyword);
		Result<List<DictModel>> result = new Result<List<DictModel>>();
		try {
			List<DictModel> ls = sysDictService.loadDict(dictCode, keyword, pageSize);
			if (ls == null) {
				result.error500("The dictionary Code is formatted incorrectly！");
				return result;
			}
			result.setSuccess(true);
			result.setResult(ls);
			log.info(result.toString());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed：" + e.getMessage());
			return result;
		}
	}

	/**
	 * [Interface Signature Verification]
	 * [Use for the table dictionary of the form designer] drop-down search mode, and dynamically splice the data when there is a value
	 * @param dictCode
	 * @param keyword The value of the current control, which can be separated by a comma
	 * @param sign
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/loadDictOrderByValue/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> loadDictOrderByValue(
			@PathVariable("dictCode") String dictCode,
			@RequestParam(name = "keyword") String keyword,
			@RequestParam(value = "sign", required = false) String sign,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		// The first query finds out the value selected by the user and is not pagination
		Result<List<DictModel>> firstRes = this.loadDict(dictCode, keyword, sign, null);
		if (!firstRes.isSuccess()) {
			return firstRes;
		}
		// Then query out the data on the first page
		Result<List<DictModel>> result = this.loadDict(dictCode, "", sign, pageSize);
		if (!result.isSuccess()) {
			return result;
		}
		// Merge the data from the two queries
		List<DictModel> firstList = firstRes.getResult();
		List<DictModel> list = result.getResult();
		for (DictModel firstItem : firstList) {
			// anyMatch indicates that if any element is successfully matched within the judgment condition, true will be returned
			// allMatch means that all elements in the condition are successfully matched, and true is returned
			// noneMatch is the opposite of allMatch, which means that all elements in the condition fail to match and return true
			boolean none = list.stream().noneMatch(item -> item.getValue().equals(firstItem.getValue()));
			// When an element does not exist, it is added to the collection
			if (none) {
				list.add(0, firstItem);
			}
		}
		return result;
	}

	/**
	 * [Interface Signature Verification]
	 * Load the dictionary text according to the dictionary code return
	 * @param dictCode Order：tableName,text,code
	 * @param keys The key to be queried
	 * @param sign
	 * @param delNotExist Whether to remove non-existent items, which is set to true by default and set to falseIf a key does not exist in the database, the key itself is returned
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/loadDictItem/{dictCode}", method = RequestMethod.GET)
	public Result<List<String>> loadDictItem(@PathVariable("dictCode") String dictCode,@RequestParam(name="key") String keys, @RequestParam(value = "sign",required = false) String sign,@RequestParam(value = "delNotExist",required = false,defaultValue = "true") boolean delNotExist,HttpServletRequest request) {
		Result<List<String>> result = new Result<>();
		try {
			if(dictCode.indexOf(SymbolConstant.COMMA)!=-1) {
				String[] params = dictCode.split(SymbolConstant.COMMA);
				if(params.length!=3) {
					result.error500("The dictionary Code is formatted incorrectly！");
					return result;
				}
				List<String> texts = sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys, delNotExist);

				result.setSuccess(true);
				result.setResult(texts);
				log.info(result.toString());
			}else {
				result.error500("The dictionary Code is formatted incorrectly！");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
			return result;
		}

		return result;
	}

	/**
	 * 【Verify the signature of the interface】
	 * Load tree data based on table name - display field - store field pid
	 * @param hasChildField Whether or not the leaf node field
	 * @param converIsLeafVal Whether system conversion is required Whether the value of the leaf node (0 indicates no conversion, 1 standard system automatic conversion.))
	 * @param tableName Table name
	 * @param text label field
	 * @param code value field
	 * @param condition  Query criteria  ？
	 *            
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
	public Result<List<TreeSelectModel>> loadTreeData(@RequestParam(name="pid",required = false) String pid,@RequestParam(name="pidField") String pidField,
												  @RequestParam(name="tableName") String tableName,
												  @RequestParam(name="text") String text,
												  @RequestParam(name="code") String code,
												  @RequestParam(name="hasChildField") String hasChildField,
												  @RequestParam(name="converIsLeafVal",defaultValue ="1") int converIsLeafVal,
												  @RequestParam(name="condition") String condition,
												  @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {
		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		// 1.获取查询条件参数
		Map<String, String> query = null;
		if(oConvertUtils.isNotEmpty(condition)) {
			query = JSON.parseObject(condition, Map.class);
		}
		
		// 2.返回查询结果
		List<TreeSelectModel> ls = sysDictService.queryTreeList(query,tableName, text, code, pidField, pid,hasChildField,converIsLeafVal);
		result.setSuccess(true);
		result.setResult(ls);
		return result;
	}

	/**
	 * 【APP interface】Configure the dictionary data of the query table according to the dictionary (no place to call is currently found)
	 * @param query
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Deprecated
	@GetMapping("/queryTableData")
	public Result<List<DictModel>> queryTableData(DictQuery query,
												  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
												  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
												  @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request){
		Result<List<DictModel>> res = new Result<List<DictModel>>();
		List<DictModel> ls = this.sysDictService.queryDictTablePageList(query,pageSize,pageNo);
		res.setResult(ls);
		res.setSuccess(true);
		return res;
	}

	/**
	 * @Feature: New dictionary data
	 * @param sysDict
	 * @return
	 */
    @RequiresPermissions("system:dict:add")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysDict> add(@RequestBody SysDict sysDict) {
		Result<SysDict> result = new Result<SysDict>();
		try {
			sysDict.setCreateTime(new Date());
			sysDict.setDelFlag(CommonConstant.DEL_FLAG_0);
			sysDictService.save(sysDict);
			result.success("Save successfully!");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}

	/**
	 * @Function: Editing
	 * @param sysDict
	 * @return
	 */
    @RequiresPermissions("system:dict:edit")
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT,RequestMethod.POST })
	public Result<SysDict> edit(@RequestBody SysDict sysDict) {
		Result<SysDict> result = new Result<SysDict>();
		SysDict sysdict = sysDictService.getById(sysDict.getId());
		if(sysdict==null) {
			result.error500("No corresponding entity found");
		}else {
			sysDict.setUpdateTime(new Date());
			boolean ok = sysDictService.updateById(sysDict);
			if(ok) {
				result.success("Edited successfully!");
			}
		}
		return result;
	}

	/**
	 * @Feature: Delete
	 * @param id
	 * @return
	 */
    @RequiresPermissions("system:dict:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDict> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysDict> result = new Result<SysDict>();
		boolean ok = sysDictService.removeById(id);
		if(ok) {
			result.success("The deletion is successful!");
		}else{
			result.error500("Deletion failed!");
		}
		return result;
	}

	/**
	 * @Function: Batch deletion
	 * @param ids
	 * @return
	 */
    @RequiresPermissions("system:dict:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDict> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysDict> result = new Result<SysDict>();
		if(oConvertUtils.isEmpty(ids)) {
			result.error500("The parameter is not recognized！");
		}else {
			sysDictService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("The deletion is successful!");
		}
		return result;
	}

	/**
	 * @Function: Refresh cache
	 * @return
	 */
	@RequestMapping(value = "/refleshCache")
	public Result<?> refleshCache() {
		Result<?> result = new Result<SysDict>();
		//清空字典缓存
//		Set keys = redisTemplate.keys(CacheConstant.SYS_DICT_CACHE + "*");
//		Set keys7 = redisTemplate.keys(CacheConstant.SYS_ENABLE_DICT_CACHE + "*");
//		Set keys2 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_CACHE + "*");
//		Set keys21 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_BY_KEYS_CACHE + "*");
//		Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
//		Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
//		Set keys5 = redisTemplate.keys( "jmreport:cache:dict*");
//		Set keys6 = redisTemplate.keys( "jmreport:cache:dictTable*");
//		redisTemplate.delete(keys);
//		redisTemplate.delete(keys2);
//		redisTemplate.delete(keys21);
//		redisTemplate.delete(keys3);
//		redisTemplate.delete(keys4);
//		redisTemplate.delete(keys5);
//		redisTemplate.delete(keys6);
//		redisTemplate.delete(keys7);

		//update-begin-author:liusq date:20230404 for:  [issue/4358]springCache中的清除缓存的操作使用了“keys”
		redisUtil.removeAll(CacheConstant.SYS_DICT_CACHE);
		redisUtil.removeAll(CacheConstant.SYS_ENABLE_DICT_CACHE);
		redisUtil.removeAll(CacheConstant.SYS_DICT_TABLE_CACHE);
		redisUtil.removeAll(CacheConstant.SYS_DICT_TABLE_BY_KEYS_CACHE);
		redisUtil.removeAll(CacheConstant.SYS_DEPARTS_CACHE);
		redisUtil.removeAll(CacheConstant.SYS_DEPART_IDS_CACHE);
		redisUtil.removeAll("jmreport:cache:dict");
		redisUtil.removeAll("jmreport:cache:dictTable");
		//update-end-author:liusq date:20230404 for:  [issue/4358]springCache中的清除缓存的操作使用了“keys”
		return result;
	}

	/**
	 * Export to Excel
	 *
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysDict sysDict,HttpServletRequest request) {
		//------------------------------------------------------------------------------------------------
		//WHETHER TO ENABLE MULTI-TENANT DATA ISOLATION IN THE SYSTEM MANAGEMENT MODULE [SAAS MULTI-TENANT MODE]
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysDict.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		
		// Step.1 组装查询条件
		QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, request.getParameterMap());
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysDictPage> pageList = new ArrayList<SysDictPage>();

		List<SysDict> sysDictList = sysDictService.list(queryWrapper);
		for (SysDict dictMain : sysDictList) {
			SysDictPage vo = new SysDictPage();
			BeanUtils.copyProperties(dictMain, vo);
			// 查询机票
			List<SysDictItem> sysDictItemList = sysDictItemService.selectItemsByMainId(dictMain.getId());
			vo.setSysDictItemList(sysDictItemList);
			pageList.add(vo);
		}

		// The name of the export file
		mv.addObject(NormalExcelConstants.FILE_NAME, "Data Dictionary");
		// Annotation object Class
		mv.addObject(NormalExcelConstants.CLASS, SysDictPage.class);
		// 自定义表格参数
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("A list of data dictionaries", "Exporter:"+user.getRealname(), "Data Dictionary"));
		// Export a list of data
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * Import data via Excel
	 *
	 * @param request
	 * @param
	 * @return
	 */
    @RequiresPermissions("system:dict:importExcel")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
 		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(2);
			params.setNeedSave(true);
			try {
				//Import Excel format to check the probability of matching field text
				Boolean t = ExcelImportCheckUtil.check(file.getInputStream(), SysDictPage.class, params);
				if(t!=null && !t){
					throw new RuntimeException("Failed to import Excel for verification ！");
				}
				List<SysDictPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SysDictPage.class, params);
				// 错误信息
				List<String> errorMessage = new ArrayList<>();
				int successLines = 0, errorLines = 0;
				for (int i=0;i< list.size();i++) {
					SysDict po = new SysDict();
					BeanUtils.copyProperties(list.get(i), po);
					po.setDelFlag(CommonConstant.DEL_FLAG_0);
					try {
						Integer integer = sysDictService.saveMain(po, list.get(i).getSysDictItemList());
						if(integer>0){
							successLines++;
                        //update-begin---author:wangshuai ---date:20220211  for：[JTC-1168]如果字典项值为空，则字典项忽略导入------------
						}else if(integer == -1){
                            errorLines++;
                            errorMessage.add("Dictionary name：" + po.getDictName() + "，The dictionary item value of the corresponding dictionary list cannot be empty, ignore the import.");
                        }else{
                        //update-end---author:wangshuai ---date:20220211  for：[JTC-1168]如果字典项值为空，则字典项忽略导入------------
							errorLines++;
							int lineNumber = i + 1;
                            //update-begin---author:wangshuai ---date:20220209  for：[JTC-1168]字典编号不能为空------------
                            if(oConvertUtils.isEmpty(po.getDictCode())){
                                errorMessage.add("Clause " + lineNumber + "Line: The dictionary encoding cannot be empty, ignore the import.");
                            }else{
                                errorMessage.add("Clause " + lineNumber + "Line: The dictionary encoding already exists, ignore the import.");
                            }
                            //update-end---author:wangshuai ---date:20220209  for：[JTC-1168]字典编号不能为空------------
                        }
					}  catch (Exception e) {
						errorLines++;
						int lineNumber = i + 1;
						errorMessage.add("Clause " + lineNumber + "Line: The dictionary encoding already exists, ignore the import.");
					}
				}
				return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("File import failed:"+e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("File import failed！");
	}


	/**
	 * Query the list that was deleted
	 * @return
	 */
	@RequestMapping(value = "/deleteList", method = RequestMethod.GET)
	public Result<List<SysDict>> deleteList() {
		Result<List<SysDict>> result = new Result<List<SysDict>>();
		List<SysDict> list = this.sysDictService.queryDeleteList();
		result.setSuccess(true);
		result.setResult(list);
		return result;
	}

	/**
	 * Physical deletion
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deletePhysic/{id}", method = RequestMethod.DELETE)
	public Result<?> deletePhysic(@PathVariable("id") String id) {
		try {
			sysDictService.deleteOneDictPhysically(id);
			return Result.ok("The deletion is successful!");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("Deletion failed!");
		}
	}

	/**
	 * TOMBSTONED FIELDS TO RETRIEVE
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/back/{id}", method = RequestMethod.PUT)
	public Result<?> back(@PathVariable("id") String id) {
		try {
			sysDictService.updateDictDelFlag(0,id);
			return Result.ok("The operation was successful!");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("Operation failed!");
		}
	}

	/**
	 * VUEN-2584【issue】There are several problems with the SQL injection vulnerability of the platform
	 * Some special functions can mix query results with error messages, resulting in database information being exposed	 * @param e
	 * @return
	 */
	@ExceptionHandler(java.sql.SQLException.class)
	public Result<?> handleSQLException(Exception e){
		String msg = e.getMessage();
		String extractvalue = "extractvalue";
		String updatexml = "updatexml";
		if(msg!=null && (msg.toLowerCase().indexOf(extractvalue)>=0 || msg.toLowerCase().indexOf(updatexml)>=0)){
			return Result.error("The verification fails, and the SQL parsing is abnormal！");
		}
		return Result.error("The verification fails, and the SQL parsing is abnormal！" + msg);
	}

	/**
	 * Obtain the dictionary list and details based on the application ID
	 * @param request
	 */
	@GetMapping("/getDictListByLowAppId")
	public Result<List<SysDictVo>> getDictListByLowAppId(HttpServletRequest request){
		String lowAppId = oConvertUtils.getString(TokenUtils.getLowAppIdByRequest(request));
		List<SysDictVo> list = sysDictService.getDictListByLowAppId(lowAppId);
		return Result.ok(list);
	}

	/**
	 * Add a dictionary
	 * @param sysDictVo
	 * @param request
	 * @return
	 */
	@PostMapping("/addDictByLowAppId")
	public Result<String> addDictByLowAppId(@RequestBody SysDictVo sysDictVo,HttpServletRequest request){
		String lowAppId = oConvertUtils.getString(TokenUtils.getLowAppIdByRequest(request));
		String tenantId = oConvertUtils.getString(TokenUtils.getTenantIdByRequest(request));
		sysDictVo.setLowAppId(lowAppId);
		sysDictVo.setTenantId(oConvertUtils.getInteger(tenantId, null));
		sysDictService.addDictByLowAppId(sysDictVo);
		return Result.ok("The addition was successful");
	}

	@PutMapping("/editDictByLowAppId")
	public Result<String> editDictByLowAppId(@RequestBody SysDictVo sysDictVo,HttpServletRequest request){
		String lowAppId = oConvertUtils.getString(TokenUtils.getLowAppIdByRequest(request));
		sysDictVo.setLowAppId(lowAppId);
		sysDictService.editDictByLowAppId(sysDictVo);
		return Result.ok("Edited successfully");
	}
}
