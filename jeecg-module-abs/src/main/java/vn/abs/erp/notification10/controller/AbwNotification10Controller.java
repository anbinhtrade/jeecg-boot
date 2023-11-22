package vn.abs.erp.notification10.controller;

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
import vn.abs.erp.notification10.entity.AbwNotification10;
import vn.abs.erp.notification10.service.IAbwNotification10Service;

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
 * @Description: Notification 10
 * @Author: jeecg-boot
 * @Date:   2023-11-21
 * @Version: V1.0
 */
@Api(tags="Notification 10")
@RestController
@RequestMapping("/notification10/abwNotification10")
@Slf4j
public class AbwNotification10Controller extends JeecgController<AbwNotification10, IAbwNotification10Service> {
	@Autowired
	private IAbwNotification10Service abwNotification10Service;
	
	/**
	 * Paging list query
	 *
	 * @param abwNotification10
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification 10-Paging list query")
	@ApiOperation(value="Notification 10-Paging list query", notes="Notification 10-Paging list query")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification10>> queryPageList(AbwNotification10 abwNotification10,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification10> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification10, req.getParameterMap());
		Page<AbwNotification10> page = new Page<AbwNotification10>(pageNo, pageSize);
		IPage<AbwNotification10> pageList = abwNotification10Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   Add
	 *
	 * @param abwNotification10
	 * @return
	 */
	@AutoLog(value = "Notification 10-Add")
	@ApiOperation(value="Notification 10-Add", notes="Notification 10-Add")
	@RequiresPermissions("notification10:abw_notification10:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification10 abwNotification10) {
		abwNotification10Service.save(abwNotification10);
		return Result.OK("Successfully Added！");
	}
	
	/**
	 *  Edit
	 *
	 * @param abwNotification10
	 * @return
	 */
	@AutoLog(value = "Notification 10-Edit")
	@ApiOperation(value="Notification 10-Edit", notes="Notification 10-Edit")
	@RequiresPermissions("notification10:abw_notification10:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification10 abwNotification10) {
		abwNotification10Service.updateById(abwNotification10);
		return Result.OK("Successfully Edited!");
	}
	
	/**
	 *   Delete by id
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification 10-Delete by id")
	@ApiOperation(value="Notification 10-Delete by id", notes="Notification 10-Delete by id")
	@RequiresPermissions("notification10:abw_notification10:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification10Service.removeById(id);
		return Result.OK("Successfully Deleted!");
	}
	
	/**
	 *  Batch Delete
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification 10-Batch Delete")
	@ApiOperation(value="Notification 10-Batch Delete", notes="Notification 10-Batch Delete")
	@RequiresPermissions("notification10:abw_notification10:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification10Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("Successfully Batch Deleted!");
	}
	
	/**
	 * Query by id
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification 10-Query by id")
	@ApiOperation(value="Notification 10-Query by id", notes="Notification 10-Query by id")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification10> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification10 abwNotification10 = abwNotification10Service.getById(id);
		if(abwNotification10==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification10);
	}

    /**
    * Export to excel
    *
    * @param request
    * @param abwNotification10
    */
    @RequiresPermissions("notification10:abw_notification10:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification10 abwNotification10) {
        return super.exportXls(request, abwNotification10, AbwNotification10.class, "Notification 10");
    }

    /**
      * Import data via excel
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification10:abw_notification10:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification10.class);
    }

}
