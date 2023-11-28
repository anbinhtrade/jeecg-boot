package vn.abs.erp.notification.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import vn.abs.erp.notification.entity.AbwNotification;
import vn.abs.erp.notification.service.IAbwNotificationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: Notification ABS
 * @Author: jeecg-boot
 * @Date:   2023-11-23
 * @Version: V1.0
 */
@Api(tags="Notification ABS")
@RestController
@RequestMapping("/notification/abwNotification")
@Slf4j
public class AbwNotificationController extends JeecgController<AbwNotification, IAbwNotificationService> {
	@Autowired
	private IAbwNotificationService abwNotificationService;
	
	/**
	 * Paging list query
	 *
	 * @param abwNotification
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Notification ABS-Paging list query")
	@ApiOperation(value="Notification ABS-Paging list query", notes="Notification ABS-Paging list query")
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
	 *   Add
	 *
	 * @param abwNotification
	 * @return
	 */
	@AutoLog(value = "Notification ABS-Add")
	@ApiOperation(value="Notification ABS-Add", notes="Notification ABS-Add")
	@RequiresPermissions("notification:abw_notification:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AbwNotification abwNotification) {
		abwNotificationService.addNotification(abwNotification);
		return Result.OK("Successfully Added！");
	}
	
	/**
	 *  Edit
	 *
	 * @param abwNotification
	 * @return
	 */
	@AutoLog(value = "Notification ABS-Edit")
	@ApiOperation(value="Notification ABS-Edit", notes="Notification ABS-Edit")
	@RequiresPermissions("notification:abw_notification:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AbwNotification abwNotification) {
		abwNotificationService.updateById(abwNotification);
		return Result.OK("Successfully Edited!");
	}
	
	/**
	 *   Delete by id
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Notification ABS-Delete by id")
	@ApiOperation(value="Notification ABS-Delete by id", notes="Notification ABS-Delete by id")
	@RequiresPermissions("notification:abw_notification:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		abwNotificationService.removeById(id);
		return Result.OK("Successfully Deleted!");
	}
	
	/**
	 *  Batch Delete
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Notification ABS-Batch Delete")
	@ApiOperation(value="Notification ABS-Batch Delete", notes="Notification ABS-Batch Delete")
	@RequiresPermissions("notification:abw_notification:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.abwNotificationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("Successfully Batch Deleted!");
	}
	
	/**
	 * Query by id
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Notification ABS-Query by id")
	@ApiOperation(value="Notification ABS-Query by id", notes="Notification ABS-Query by id")
	@GetMapping(value = "/queryById")
	public Result<AbwNotification> queryById(@RequestParam(name="id",required=true) String id) {
		AbwNotification abwNotification = abwNotificationService.getById(id);
		if(abwNotification==null) {
			return Result.error("No corresponding data found");
		}
		return Result.OK(abwNotification);
	}

    /**
    * Export to excel
    *
    * @param request
    * @param abwNotification
    */
    @RequiresPermissions("notification:abw_notification:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AbwNotification abwNotification) {
        return super.exportXls(request, abwNotification, AbwNotification.class, "Notification ABS");
    }

    /**
      * Import data via excel
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
