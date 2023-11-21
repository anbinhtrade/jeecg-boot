package vn.abs.erp.notification9.controller;

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
import vn.abs.erp.notification9.entity.AbwNotification9;
import vn.abs.erp.notification9.service.IAbwNotification9Service;

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
 * @Description: Notification 9
 * @Author: jeecg-boot
 * @Date:   2023-11-21
 * @Version: V1.0
 */
@Api(tags="Notification 9")
@RestController
@RequestMapping("/notification9/abwNotification9")
@Slf4j
public class AbwNotification9Controller extends JeecgController<AbwNotification9, IAbwNotification9Service> {
	@Autowired
	private IAbwNotification9Service abwNotification9Service;
	
	/**
	 * 分页列表查询
	 *
	 * @param abwNotification9
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification 9-分页列表查询")
	@ApiOperation(value="Notification 9-分页列表查询", notes="Notification 9-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AbwNotification9>> queryPageList(AbwNotification9 abwNotification9,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AbwNotification9> queryWrapper = QueryGenerator.initQueryWrapper(abwNotification9, req.getParameterMap());
		Page<AbwNotification9> page = new Page<AbwNotification9>(pageNo, pageSize);
		IPage<AbwNotification9> pageList = abwNotification9Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param abwNotification9
	 * @return
	 */
	@AutoLog(value = "Notification 9-添加")
	@ApiOperation(value="Notification 9-添加", notes="Notification 9-添加")
	@RequiresPermissions("notification9:abw_notification9:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification9 abwNotification9) {
		abwNotification9Service.save(abwNotification9);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param abwNotification9
	 * @return
	 */
	@AutoLog(value = "Notification 9-编辑")
	@ApiOperation(value="Notification 9-编辑", notes="Notification 9-编辑")
	@RequiresPermissions("notification9:abw_notification9:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification9 abwNotification9) {
		abwNotification9Service.updateById(abwNotification9);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification 9-通过id删除")
	@ApiOperation(value="Notification 9-通过id删除", notes="Notification 9-通过id删除")
	@RequiresPermissions("notification9:abw_notification9:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotification9Service.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification 9-批量删除")
	@ApiOperation(value="Notification 9-批量删除", notes="Notification 9-批量删除")
	@RequiresPermissions("notification9:abw_notification9:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotification9Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification 9-通过id查询")
	@ApiOperation(value="Notification 9-通过id查询", notes="Notification 9-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification9> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification9 abwNotification9 = abwNotification9Service.getById(id);
		if(abwNotification9==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(abwNotification9);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param abwNotification9
    */
    @RequiresPermissions("notification9:abw_notification9:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification9 abwNotification9) {
        return super.exportXls(request, abwNotification9, AbwNotification9.class, "Notification 9");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("notification9:abw_notification9:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AbwNotification9.class);
    }

}
