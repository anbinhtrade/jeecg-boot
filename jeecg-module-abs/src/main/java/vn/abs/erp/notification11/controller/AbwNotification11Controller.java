package vn.abs.erp.notification11.controller;

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
import vn.abs.erp.notification11.entity.AbwNotification11;
import vn.abs.erp.notification11.service.IAbwNotification11Service;

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
 * @Description: Notification 11
 * @Author: jeecg-boot
 * @Date:   2023-11-22
 * @Version: V1.0
 */
@Api(tags="Notification 11")
@RestController
@RequestMapping("/notification11/abwNotification11")
@Slf4j
public class AbwNotification11Controller extends JeecgController<AbwNotification11, IAbwNotification11Service> {
	@Autowired
	private IAbwNotification11Service abwNotification11Service;
	
	/**
	 * Paging list query
	 *
	 * @param abwNotification11
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification 11-Paging list query")
	@ApiOperation(value="Notification 11-Paging list query", notes="Notification 11-Paging list query")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification11>> queryPageList(AbwNotification11 abwNotification11,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification11> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification11, req.getParameterMap());
		Page<AbwNotification11> page = new Page<AbwNotification11>(pageNo, pageSize);
		IPage<AbwNotification11> pageList = abwNotification11Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   Add
	 *
	 * @param abwNotification11
	 * @return
	 */
	@AutoLog(value = "Notification 11-Add")
	@ApiOperation(value="Notification 11-Add", notes="Notification 11-Add")
	@RequiresPermissions("notification11:abw_notification11:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification11 abwNotification11) {
		abwNotification11Service.save(abwNotification11);
		return Result.OK("Successfully Added！");
	}
	
	/**
	 *  Edit
	 *
	 * @param abwNotification11
	 * @return
	 */
	@AutoLog(value = "Notification 11-Edit")
	@ApiOperation(value="Notification 11-Edit", notes="Notification 11-Edit")
	@RequiresPermissions("notification11:abw_notification11:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification11 abwNotification11) {
		abwNotification11Service.updateById(abwNotification11);
		return Result.OK("Successfully Edited!");
	}
	
	/**
	 *   Delete by id
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification 11-Delete by id")
	@ApiOperation(value="Notification 11-Delete by id", notes="Notification 11-Delete by id")
	@RequiresPermissions("notification11:abw_notification11:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification11Service.removeById(id);
		return Result.OK("Successfully Deleted!");
	}
	
	/**
	 *  Batch Delete
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification 11-Batch Delete")
	@ApiOperation(value="Notification 11-Batch Delete", notes="Notification 11-Batch Delete")
	@RequiresPermissions("notification11:abw_notification11:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification11Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("Successfully Batch Deleted!");
	}
	
	/**
	 * Query by id
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification 11-Query by id")
	@ApiOperation(value="Notification 11-Query by id", notes="Notification 11-Query by id")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification11> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification11 abwNotification11 = abwNotification11Service.getById(id);
		if(abwNotification11==null) {
			return Result.error("No corresponding data found");
		}
		return Result.OK(abwNotification11);
	}

    /**
    * Export to excel
    *
    * @param request
    * @param abwNotification11
    */
    @RequiresPermissions("notification11:abw_notification11:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification11 abwNotification11) {
        return super.exportXls(request, abwNotification11, AbwNotification11.class, "Notification 11");
    }

    /**
      * Import data via excel
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification11:abw_notification11:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification11.class);
    }

}
