package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.system.entity.SysTableWhiteList;
import org.jeecg.modules.system.service.ISysTableWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: The system whitelist
 * @Author: jeecg-boot
 * @Date: 2023-09-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "The system whitelist")
@RestController
@RequestMapping("/sys/tableWhiteList")
public class SysTableWhiteListController extends JeecgController<SysTableWhiteList, ISysTableWhiteListService> {

    @Autowired
    private ISysTableWhiteListService sysTableWhiteListService;

    /**
     * Paginated list queries
     *
     * @param sysTableWhiteList
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@RequiresRoles("admin")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            SysTableWhiteList sysTableWhiteList,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req
    ) {
        QueryWrapper<SysTableWhiteList> queryWrapper = QueryGenerator.initQueryWrapper(sysTableWhiteList, req.getParameterMap());
        Page<SysTableWhiteList> page = new Page<>(pageNo, pageSize);
        IPage<SysTableWhiteList> pageList = sysTableWhiteListService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * Add to
     *
     * @param sysTableWhiteList
     * @return
     */
    @AutoLog(value = "System Whitelist - Add")
    @ApiOperation(value = "System Whitelist - Add", notes = "System Whitelist - Add")
    //@RequiresRoles("admin")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysTableWhiteList sysTableWhiteList) {
        if (sysTableWhiteListService.add(sysTableWhiteList)) {
            return Result.OK("Added successfully!");
        } else {
            return Result.error("Failed to add!");
        }
    }

    /**
     * EDIT
     *
     * @param sysTableWhiteList
     * @return
     */
    @AutoLog(value = "System Whitelist - Edit")
    @ApiOperation(value = "System Whitelist - Edit", notes = "System Whitelist - Edit")
    //@RequiresRoles("admin")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody SysTableWhiteList sysTableWhiteList) {
        if (sysTableWhiteListService.edit(sysTableWhiteList)) {
            return Result.OK("Edit successful!");
        } else {
            return Result.error("Edit failed!");
        }
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "System whitelist - Delete by ID")
    @ApiOperation(value = "System whitelist - Delete by ID", notes = "System whitelist - Delete by ID")
    //@RequiresRoles("admin")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        if (sysTableWhiteListService.deleteByIds(id)) {
            return Result.OK("Deleted successfully!");
        } else {
            return Result.error("Delete failed!");
        }
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "System whitelist - Batch deletion")
    @ApiOperation(value = "System whitelist - Batch deletion", notes = "System whitelist - Batch deletion")
    //@RequiresRoles("admin")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (sysTableWhiteListService.deleteByIds(ids)) {
            return Result.OK("Batch deletion successful!");
        } else {
            return Result.error("Batch deletion failed!");
        }
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "System whitelist - query by ID")
    @ApiOperation(value = "System whitelist - query by ID", notes = "System whitelist - query by ID")
    //@RequiresRoles("admin")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysTableWhiteList sysTableWhiteList = sysTableWhiteListService.getById(id);
        return Result.OK(sysTableWhiteList);
    }

}
