package vn.abs.erp.msg.category.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import vn.abs.erp.msg.category.entity.AbwMsgCategory;
import vn.abs.erp.msg.category.service.IAbwMsgCategoryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: Message Category
 * @Author: jeecg-boot
 * @Date:   2023-11-23
 * @Version: V1.0
 */
@Api(tags="Message Category")
@RestController
@RequestMapping("/msg.category/abwMsgCategory")
@Slf4j
public class AbwMsgCategoryController extends JeecgController<AbwMsgCategory, IAbwMsgCategoryService> {
	@Autowired
	private IAbwMsgCategoryService abwMsgCategoryService;
	
	/**
	 * Paging list query
	 *
	 * @param abwMsgCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Message Category-Paging list query")
	@ApiOperation(value="Message Category-Paging list query", notes="Message Category-Paging list query")
	@GetMapping(value = "/list")
	public Result<IPage<AbwMsgCategory>> queryPageList(AbwMsgCategory abwMsgCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwMsgCategory> queryWrapper = QueryGenerator.initQueryWrapper(abwMsgCategory, req.getParameterMap());
		Page<AbwMsgCategory> page = new Page<AbwMsgCategory>(pageNo, pageSize);
		IPage<AbwMsgCategory> pageList = abwMsgCategoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   Add
	 *
	 * @param abwMsgCategory
	 * @return
	 */
	@AutoLog(value = "Message Category-Add")
	@ApiOperation(value="Message Category-Add", notes="Message Category-Add")
	@RequiresPermissions("msg.category:abw_msg_category:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwMsgCategory abwMsgCategory) {
		abwMsgCategoryService.save(abwMsgCategory);
		return Result.OK("Successfully Added！");
	}
	
	/**
	 *  Edit
	 *
	 * @param abwMsgCategory
	 * @return
	 */
	@AutoLog(value = "Message Category-Edit")
	@ApiOperation(value="Message Category-Edit", notes="Message Category-Edit")
	@RequiresPermissions("msg.category:abw_msg_category:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwMsgCategory abwMsgCategory) {
		abwMsgCategoryService.updateById(abwMsgCategory);
		return Result.OK("Successfully Edited!");
	}
	
	/**
	 *   Delete by id
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Message Category-Delete by id")
	@ApiOperation(value="Message Category-Delete by id", notes="Message Category-Delete by id")
	@RequiresPermissions("msg.category:abw_msg_category:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwMsgCategoryService.removeById(id);
		return Result.OK("Successfully Deleted!");
	}
	
	/**
	 *  Batch Delete
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Message Category-Batch Delete")
	@ApiOperation(value="Message Category-Batch Delete", notes="Message Category-Batch Delete")
	@RequiresPermissions("msg.category:abw_msg_category:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwMsgCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("Successfully Batch Deleted!");
	}
	
	/**
	 * Query by id
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Message Category-Query by id")
	@ApiOperation(value="Message Category-Query by id", notes="Message Category-Query by id")
	@GetMapping(value = "/queryById")
	public Result<AbwMsgCategory> queryById(@RequestParam(name="id",required=true) String id) {
		AbwMsgCategory abwMsgCategory = abwMsgCategoryService.getById(id);
		if(abwMsgCategory==null) {
			return Result.error("No corresponding data found");
		}
		return Result.OK(abwMsgCategory);
	}

    /**
    * Export to excel
    *
    * @param request
    * @param abwMsgCategory
    */
    @RequiresPermissions("msg.category:abw_msg_category:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwMsgCategory abwMsgCategory) {
        return super.exportXls(request, abwMsgCategory, AbwMsgCategory.class, "Message Category");
    }

    /**
      * Import data via excel
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("msg.category:abw_msg_category:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwMsgCategory.class);
    }

}
