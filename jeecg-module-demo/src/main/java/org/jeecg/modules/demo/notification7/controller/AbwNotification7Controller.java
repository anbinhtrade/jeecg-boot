package org.jeecg.modules.demo.notification7.controller;

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
import org.jeecg.modules.demo.notification7.entity.AbwNotification7;
import org.jeecg.modules.demo.notification7.service.IAbwNotification7Service;

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
 * @Description: Notification 7
 * @Author: jeecg-boot
 * @Date:   2023-11-21
 * @Version: V1.0
 */
@Api(tags="Notification 7")
@RestController
@RequestMapping("/notification7/abwNotification7")
@Slf4j
public class AbwNotification7Controller extends JeecgController<AbwNotification7, IAbwNotification7Service> {
	@Autowired
	private IAbwNotification7Service abwNotification7Service;
	
	/**
	 * 分页列表查询
	 *
	 * @param abwNotification7
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification 7-分页列表查询")
	@ApiOperation(value="Notification 7-分页列表查询", notes="Notification 7-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification7>> queryPageList(AbwNotification7 abwNotification7,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification7> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification7, req.getParameterMap());
		Page<AbwNotification7> page = new Page<AbwNotification7>(pageNo, pageSize);
		IPage<AbwNotification7> pageList = abwNotification7Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param abwNotification7
	 * @return
	 */
	@AutoLog(value = "Notification 7-添加")
	@ApiOperation(value="Notification 7-添加", notes="Notification 7-添加")
	@RequiresPermissions("notification7:abw_notification7:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification7 abwNotification7) {
		abwNotification7Service.save(abwNotification7);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param abwNotification7
	 * @return
	 */
	@AutoLog(value = "Notification 7-编辑")
	@ApiOperation(value="Notification 7-编辑", notes="Notification 7-编辑")
	@RequiresPermissions("notification7:abw_notification7:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification7 abwNotification7) {
		abwNotification7Service.updateById(abwNotification7);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification 7-通过id删除")
	@ApiOperation(value="Notification 7-通过id删除", notes="Notification 7-通过id删除")
	@RequiresPermissions("notification7:abw_notification7:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification7Service.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification 7-批量删除")
	@ApiOperation(value="Notification 7-批量删除", notes="Notification 7-批量删除")
	@RequiresPermissions("notification7:abw_notification7:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification7Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification 7-通过id查询")
	@ApiOperation(value="Notification 7-通过id查询", notes="Notification 7-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification7> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification7 abwNotification7 = abwNotification7Service.getById(id);
		if(abwNotification7==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification7);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param abwNotification7
    */
    @RequiresPermissions("notification7:abw_notification7:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification7 abwNotification7) {
        return super.exportXls(request, abwNotification7, AbwNotification7.class, "Notification 7");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification7:abw_notification7:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification7.class);
    }

}
