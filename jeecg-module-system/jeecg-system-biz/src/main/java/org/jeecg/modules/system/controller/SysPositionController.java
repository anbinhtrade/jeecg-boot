package org.jeecg.modules.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysPosition;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysPositionService;
import org.jeecg.modules.system.service.ISysUserPositionService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: Job Title
 * @Author: jeecg-boot
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Job Title")
@RestController
@RequestMapping("/sys/position")
public class SysPositionController {

    @Autowired
    private ISysPositionService sysPositionService;

    @Autowired
    private ISysUserPositionService userPositionService;

    @Autowired
    private ISysUserService userService;

    /**
     * Paginated list queries
     *
     * @param sysPosition
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Job Title Table - Paginated List Query")
    @ApiOperation(value = "Job Title Table - Paginated List Query", notes = "Job Title Table - Paginated List Query")
    @GetMapping(value = "/list")
    public Result<IPage<SysPosition>> queryPageList(SysPosition sysPosition,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        Result<IPage<SysPosition>> result = new Result<IPage<SysPosition>>();
        //------------------------------------------------------------------------------------------------
        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
            sysPosition.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
        }
        //------------------------------------------------------------------------------------------------
        QueryWrapper<SysPosition> queryWrapper = QueryGenerator.initQueryWrapper(sysPosition, req.getParameterMap());
        Page<SysPosition> page = new Page<SysPosition>(pageNo, pageSize);
        IPage<SysPosition> pageList = sysPositionService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * Add to
     *
     * @param sysPosition
     * @return
     */
    @AutoLog(value = "Job Title Table - Add")
    @ApiOperation(value = "Job Title Table - Add", notes = "Job Title Table - Add")
    @PostMapping(value = "/add")
    public Result<SysPosition> add(@RequestBody SysPosition sysPosition) {
        Result<SysPosition> result = new Result<SysPosition>();
        try {
            //update-begin---author:wangshuai ---date:20230313  for：【QQYUN-4558】vue3职位功能调整，去掉编码和级别，可以先隐藏------------
            //编号是空的，不需要判断多租户隔离了
            if(oConvertUtils.isEmpty(sysPosition.getCode())){
                //生成职位编码10位
                sysPosition.setCode(RandomUtil.randomString(10));
            }
            //update-end---author:wangshuai ---date:20230313  for：【QQYUN-4558】vue3职位功能调整，去掉编码和级别，可以先隐藏-------------
            sysPositionService.save(sysPosition);
            result.success("Added successfully!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("The operation failed");
        }
        return result;
    }

    /**
     * EDIT
     *
     * @param sysPosition
     * @return
     */
    @AutoLog(value = "Job Title Table - Edit")
    @ApiOperation(value = "Job Title Table - Edit", notes = "Job Title Table - Edit")
    @RequestMapping(value = "/edit", method ={RequestMethod.PUT, RequestMethod.POST})
    public Result<SysPosition> edit(@RequestBody SysPosition sysPosition) {
        Result<SysPosition> result = new Result<SysPosition>();
        SysPosition sysPositionEntity = sysPositionService.getById(sysPosition.getId());
        if (sysPositionEntity == null) {
            result.error500("No corresponding entity found");
        } else {
            boolean ok = sysPositionService.updateById(sysPosition);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("Modification successful!");
            }
        }

        return result;
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Job Title Sheet - Deleted by ID")
    @ApiOperation(value = "Job Title Sheet - Deleted by ID", notes = "Job Title Sheet - Deleted by ID")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sysPositionService.removeById(id);
            //删除用户职位关系表
            userPositionService.removeByPositionId(id);
        } catch (Exception e) {
            log.error("Deletion failed", e.getMessage());
            return Result.error("Delete failed!");
        }
        return Result.ok("Deleted successfully!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Job Titles - Delete them in bulk")
    @ApiOperation(value = "Job Titles - Delete them in bulk", notes = "Job Titles - Delete them in bulk")
    @DeleteMapping(value = "/deleteBatch")
    public Result<SysPosition> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysPosition> result = new Result<SysPosition>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("The parameter is not recognized!");
        } else {
            this.sysPositionService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("Deleted successfully!");
        }
        return result;
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Job Title Table - Query by ID")
    @ApiOperation(value = "Job Title Table - Query by ID", notes = "Job Title Table - Query by ID")
    @GetMapping(value = "/queryById")
    public Result<SysPosition> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysPosition> result = new Result<SysPosition>();
        SysPosition sysPosition = sysPositionService.getById(id);
        if (sysPosition == null) {
            result.error500("No corresponding entity found");
        } else {
            result.setResult(sysPosition);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<SysPosition> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                SysPosition sysPosition = JSON.parseObject(deString, SysPosition.class);
                //------------------------------------------------------------------------------------------------
                //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
                if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
                    sysPosition.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
                }
                //------------------------------------------------------------------------------------------------
                queryWrapper = QueryGenerator.initQueryWrapper(sysPosition, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysPosition> pageList = sysPositionService.list(queryWrapper);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // The name of the export file
        mv.addObject(NormalExcelConstants.FILE_NAME, "List of job titles");
        mv.addObject(NormalExcelConstants.CLASS, SysPosition.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Job Title Table List Data", "Exporter:"+user.getRealname(),"Export information"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Object>  listSysPositions = ExcelImportUtil.importExcel(file.getInputStream(), SysPosition.class, params);
                List<String> list = ImportExcelUtil.importDateSave(listSysPositions, ISysPositionService.class, errorMessage,CommonConstant.SQL_INDEX_UNIQ_CODE);
                errorLines+=list.size();
                successLines+=(listSysPositions.size()-errorLines);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("File import failed:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
    }

    /**
     * Query by code
     *
     * @param code
     * @return
     */
    @AutoLog(value = "Job Title - Query by code")
    @ApiOperation(value = "Job Title - Query by code", notes = "Job Title - Query by code")
    @GetMapping(value = "/queryByCode")
    public Result<SysPosition> queryByCode(@RequestParam(name = "code", required = true) String code) {
        Result<SysPosition> result = new Result<SysPosition>();
        QueryWrapper<SysPosition> queryWrapper = new QueryWrapper<SysPosition>();
        queryWrapper.eq("code",code);
        SysPosition sysPosition = sysPositionService.getOne(queryWrapper);
        if (sysPosition == null) {
            result.error500("No corresponding entity found");
        } else {
            result.setResult(sysPosition);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * Query by multiple IDs
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Job Title Table - Through multiple queries")
    @ApiOperation(value = "Job Title Table - Query by multiple IDs", notes = "Job Title Table - Query by multiple IDs")
    @GetMapping(value = "/queryByIds")
    public Result<List<SysPosition>> queryByIds(@RequestParam(name = "ids") String ids) {
        Result<List<SysPosition>> result = new Result<>();
        QueryWrapper<SysPosition> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(true,"id",ids.split(","));
        List<SysPosition> list = sysPositionService.list(queryWrapper);
        if (list == null) {
            result.error500("No corresponding entity found");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }



    /**
     * Get a list of job users
     *
     * @param pageNo
     * @param pageSize
     * @param positionId
     * @return
     */
    @GetMapping("/getPositionUserList")
    public Result<IPage<SysUser>> getPositionUserList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "positionId") String positionId) {

        Page<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = userPositionService.getPositionUserList(page, positionId);
        List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        if (null != userIds && userIds.size() > 0) {
            Map<String, String> useDepNames = userService.getDepNamesByUserIds(userIds);
            pageList.getRecords().forEach(item -> {
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        return Result.ok(pageList);
    }

    /**
     * Add members to the User Position Relationships table
     *
     * @param userIds
     * @param positionId
     * @return
     */
    @PostMapping("/savePositionUser")
    public Result<String> saveUserPosition(@RequestParam(name = "userIds") String userIds,
                                           @RequestParam(name = "positionId") String positionId) {
        userPositionService.saveUserPosition(userIds, positionId);
        return Result.ok("The addition was successful");
    }

    /**
     * Job listings
     *
     * @param userIds
     * @param positionId
     * @return
     */
    @DeleteMapping("/removePositionUser")
    public Result<String> removeUserPosition(@RequestParam(name = "userIds") String userIds,
                                             @RequestParam(name = "positionId") String positionId) {
        userPositionService.removePositionUser(userIds, positionId);
        return Result.OK("The removal of the member is successful");
    }
}
