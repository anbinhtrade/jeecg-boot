package org.jeecg.modules.demo.notification.controller;

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
import org.jeecg.modules.demo.notification.entity.AbwNotification;
import org.jeecg.modules.demo.notification.service.IAbwNotificationService;

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
 * @Description: Notification3
 * @Author: jeecg-boot
 * @Date:   2023-11-20
 * @Version: V1.0
 */
@Api(tags="Notification3")
@RestController
@RequestMapping("/notification/abwNotification")
@Slf4j
public class AbwNotificationController extends JeecgController<AbwNotification, IAbwNotificationService> {
	@Autowired
	private IAbwNotificationService abwNotificationService;
	
	/**
	 * 分页列表查询
	 *
	 * @param abwNotification
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification3-分页列表查询")
	@ApiOperation(value="Notification3-分页列表查询", notes="Notification3-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification>> queryPageList(AbwNotification abwNotification,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification, req.getParameterMap());
		Page<AbwNotification> page = new Page<AbwNotification>(pageNo, pageSize);
		IPage<AbwNotification> pageList = abwNotificationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param abwNotification
	 * @return
	 */
	@AutoLog(value = "Notification3-添加")
	@ApiOperation(value="Notification3-添加", notes="Notification3-添加")
	@RequiresPermissions("notification:abw_notification:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification abwNotification) {
		abwNotificationService.save(abwNotification);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param abwNotification
	 * @return
	 */
	@AutoLog(value = "Notification3-编辑")
	@ApiOperation(value="Notification3-编辑", notes="Notification3-编辑")
	@RequiresPermissions("notification:abw_notification:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification abwNotification) {
		abwNotificationService.updateById(abwNotification);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification3-通过id删除")
	@ApiOperation(value="Notification3-通过id删除", notes="Notification3-通过id删除")
	@RequiresPermissions("notification:abw_notification:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotificationService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification3-批量删除")
	@ApiOperation(value="Notification3-批量删除", notes="Notification3-批量删除")
	@RequiresPermissions("notification:abw_notification:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotificationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification3-通过id查询")
	@ApiOperation(value="Notification3-通过id查询", notes="Notification3-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification abwNotification = abwNotificationService.getById(id);
		if(abwNotification==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param abwNotification
    */
    @RequiresPermissions("notification:abw_notification:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification abwNotification) {
        return super.exportXls(request, abwNotification, AbwNotification.class, "Notification3");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification:abw_notification:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification.class);
    }

}
