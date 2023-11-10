package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.system.entity.SysFormFile;
import org.jeecg.modules.system.service.ISysFormFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: Form comment file
 * @Author: jeecg-boot
 * @Date: 2022-07-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Form comment file")
@RestController
@RequestMapping("/sys/formFile")
public class SysFormFileController extends JeecgController<SysFormFile, ISysFormFileService> {
    @Autowired
    private ISysFormFileService sysFormFileService;

    /**
     * Paginated List Queries
     *
     * @param sysFormFile
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Form Comment File - Paginated List Query")
    @ApiOperation(value = "Form Comment File Paginated List Query", notes = "Form Comment File- Paginated List Query")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysFormFile sysFormFile,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysFormFile> queryWrapper = QueryGenerator.initQueryWrapper(sysFormFile, req.getParameterMap());
        Page<SysFormFile> page = new Page<SysFormFile>(pageNo, pageSize);
        IPage<SysFormFile> pageList = sysFormFileService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * addTo
     *
     * @param sysFormFile
     * @return
     */
    @AutoLog(value = "Form Comment File - Add")
    @ApiOperation(value = "Form Comment File - Add", notes = "Form Comment File - Add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysFormFile sysFormFile) {
        sysFormFileService.save(sysFormFile);
        return Result.OK("Added successfully!");
    }

    /**
     * EDIT
     *
     * @param sysFormFile
     * @return
     */
    @AutoLog(value = "Form Comment File - Edit")
    @ApiOperation(value = "Form Comment File - Edit", notes = "Form Comment File - Edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody SysFormFile sysFormFile) {
        sysFormFileService.updateById(sysFormFile);
        return Result.OK("Edit successful!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Form comment file - delete by id")
    @ApiOperation(value = "Form comment file - delete by id", notes = "Form comment file - delete by id")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysFormFileService.removeById(id);
        return Result.OK("Deleted successfully!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Form Comment Files - Bulk Deletion")
    @ApiOperation(value = "Form Comment Files - Bulk Deletion", notes = "Form Comment Files - Bulk Deletion")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysFormFileService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("Batch deletion successful!");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Form Comment File - Query by ID")
    @ApiOperation(value = "Form Comment File - Query by ID", notes = "Form Comment File - Query by ID")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFormFile sysFormFile = sysFormFileService.getById(id);
        return Result.OK(sysFormFile);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysFormFile
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysFormFile sysFormFile) {
        return super.exportXls(request, sysFormFile, SysFormFile.class, "表单评论文件");
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysFormFile.class);
    }

}
