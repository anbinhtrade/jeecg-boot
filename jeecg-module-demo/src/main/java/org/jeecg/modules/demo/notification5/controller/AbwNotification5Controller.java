package org.jeecg.modules.demo.notification5.controller;

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
import org.jeecg.modules.demo.notification5.entity.AbwNotification5;
import org.jeecg.modules.demo.notification5.service.IAbwNotification5Service;

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
 * @Description: Notification5
 * @Author: jeecg-boot
 * @Date:   2023-11-21
 * @Version: V1.0
 */
@Api(tags="Notification5")
@RestController
@RequestMapping("/notification5/abwNotification5")
@Slf4j
public class AbwNotification5Controller extends JeecgController<AbwNotification5, IAbwNotification5Service> {
	@Autowired
	private IAbwNotification5Service abwNotification5Service;
	
	/**
	 * 分页列表查询
	 *
	 * @param abwNotification5
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification5-分页列表查询")
	@ApiOperation(value="Notification5-分页列表查询", notes="Notification5-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification5>> queryPageList(AbwNotification5 abwNotification5,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification5> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification5, req.getParameterMap());
		Page<AbwNotification5> page = new Page<AbwNotification5>(pageNo, pageSize);
		IPage<AbwNotification5> pageList = abwNotification5Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param abwNotification5
	 * @return
	 */
	@AutoLog(value = "Notification5-添加")
	@ApiOperation(value="Notification5-添加", notes="Notification5-添加")
	@RequiresPermissions("notification5:abw_notification:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification5 abwNotification5) {
		abwNotification5Service.save(abwNotification5);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param abwNotification5
	 * @return
	 */
	@AutoLog(value = "Notification5-编辑")
	@ApiOperation(value="Notification5-编辑", notes="Notification5-编辑")
	@RequiresPermissions("notification5:abw_notification:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification5 abwNotification5) {
		abwNotification5Service.updateById(abwNotification5);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification5-通过id删除")
	@ApiOperation(value="Notification5-通过id删除", notes="Notification5-通过id删除")
	@RequiresPermissions("notification5:abw_notification:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification5Service.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification5-批量删除")
	@ApiOperation(value="Notification5-批量删除", notes="Notification5-批量删除")
	@RequiresPermissions("notification5:abw_notification:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification5Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification5-通过id查询")
	@ApiOperation(value="Notification5-通过id查询", notes="Notification5-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification5> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification5 abwNotification5 = abwNotification5Service.getById(id);
		if(abwNotification5==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification5);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param abwNotification5
    */
    @RequiresPermissions("notification5:abw_notification:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification5 abwNotification5) {
        return super.exportXls(request, abwNotification5, AbwNotification5.class, "Notification5");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification5:abw_notification:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification5.class);
    }

}
