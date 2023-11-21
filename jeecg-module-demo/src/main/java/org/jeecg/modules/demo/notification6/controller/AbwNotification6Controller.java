package org.jeecg.modules.demo.notification6.controller;

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
import org.jeecg.modules.demo.notification6.entity.AbwNotification6;
import org.jeecg.modules.demo.notification6.service.IAbwNotification6Service;

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
 * @Description: Notification 6
 * @Author: jeecg-boot
 * @Date:   2023-11-21
 * @Version: V1.0
 */
@Api(tags="Notification 6")
@RestController
@RequestMapping("/notification6/abwNotification6")
@Slf4j
public class AbwNotification6Controller extends JeecgController<AbwNotification6, IAbwNotification6Service> {
	@Autowired
	private IAbwNotification6Service abwNotification6Service;
	
	/**
	 * 分页列表查询
	 *
	 * @param abwNotification6
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification 6-分页列表查询")
	@ApiOperation(value="Notification 6-分页列表查询", notes="Notification 6-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification6>> queryPageList(AbwNotification6 abwNotification6,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification6> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification6, req.getParameterMap());
		Page<AbwNotification6> page = new Page<AbwNotification6>(pageNo, pageSize);
		IPage<AbwNotification6> pageList = abwNotification6Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param abwNotification6
	 * @return
	 */
	@AutoLog(value = "Notification 6-添加")
	@ApiOperation(value="Notification 6-添加", notes="Notification 6-添加")
	@RequiresPermissions("notification6:abw_notification6:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification6 abwNotification6) {
		abwNotification6Service.save(abwNotification6);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param abwNotification6
	 * @return
	 */
	@AutoLog(value = "Notification 6-编辑")
	@ApiOperation(value="Notification 6-编辑", notes="Notification 6-编辑")
	@RequiresPermissions("notification6:abw_notification6:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification6 abwNotification6) {
		abwNotification6Service.updateById(abwNotification6);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification 6-通过id删除")
	@ApiOperation(value="Notification 6-通过id删除", notes="Notification 6-通过id删除")
	@RequiresPermissions("notification6:abw_notification6:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification6Service.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification 6-批量删除")
	@ApiOperation(value="Notification 6-批量删除", notes="Notification 6-批量删除")
	@RequiresPermissions("notification6:abw_notification6:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification6Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification 6-通过id查询")
	@ApiOperation(value="Notification 6-通过id查询", notes="Notification 6-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification6> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification6 abwNotification6 = abwNotification6Service.getById(id);
		if(abwNotification6==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification6);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param abwNotification6
    */
    @RequiresPermissions("notification6:abw_notification6:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification6 abwNotification6) {
        return super.exportXls(request, abwNotification6, AbwNotification6.class, "Notification 6");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification6:abw_notification6:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification6.class);
    }

}
