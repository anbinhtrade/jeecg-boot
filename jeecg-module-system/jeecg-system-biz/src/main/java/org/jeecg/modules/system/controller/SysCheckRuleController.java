package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
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
import org.jeecg.modules.system.entity.SysCheckRule;
import org.jeecg.modules.system.service.ISysCheckRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * @Description: Encoding check rules
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Encoding check rules")
@RestController
@RequestMapping("/sys/checkRule")
public class SysCheckRuleController extends JeecgController<SysCheckRule, ISysCheckRuleService> {

    @Autowired
    private ISysCheckRuleService sysCheckRuleService;

    /**
     * Paginated list queries
     *
     * @param sysCheckRule
     * @param pageNo
     * @param pageSize
     * @param request
     * @return
     */
    @AutoLog(value = "Encoding Validation Rules - Paginated List Query")
    @ApiOperation(value = "Encoding Validation Rules - Paginated List Query", notes = "Encoding Validation Rules - Paginated List Query")
    @GetMapping(value = "/list")
    public Result queryPageList(
            SysCheckRule sysCheckRule,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        QueryWrapper<SysCheckRule> queryWrapper = QueryGenerator.initQueryWrapper(sysCheckRule, request.getParameterMap());
        Page<SysCheckRule> page = new Page<>(pageNo, pageSize);
        IPage<SysCheckRule> pageList = sysCheckRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    /**
     * Query by ID
     *
     * @param ruleCode
     * @return
     */
    @AutoLog(value = "Encoding Validation Rule - Verify the incoming value by using Code")
    @ApiOperation(value = "Encoding Validation Rule - Verify the incoming value by using Code",
            notes = "Encoding Validation Rule - Verify the incoming value by using Code")
    @GetMapping(value = "/checkByCode")
    public Result checkByCode(
            @RequestParam(name = "ruleCode") String ruleCode,
            @RequestParam(name = "value") String value
    ) throws UnsupportedEncodingException {
        SysCheckRule sysCheckRule = sysCheckRuleService.getByCode(ruleCode);
        if (sysCheckRule == null) {
            return Result.error("The encoding does not exist");
        }
        JSONObject errorResult = sysCheckRuleService.checkValue(sysCheckRule, URLDecoder.decode(value, "UTF-8"));
        if (errorResult == null) {
            return Result.ok();
        } else {
            Result<Object> r = Result.error(errorResult.getString("message"));
            r.setResult(errorResult);
            return r;
        }
    }

    /**
     * Add to
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "Encoding Checksum Rule - Added")
    @ApiOperation(value = "Encoding Checksum Rule - Added", notes = "Encoding Checksum Rule - Added")
    @PostMapping(value = "/add")
    public Result add(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.save(sysCheckRule);
        return Result.ok("The addition was successful！");
    }

    /**
     * Edit
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "Encoding Checksum Rules - Editing")
    @ApiOperation(value = "Encoding Checksum Rules - Editing", notes = "Encoding Checksum Rules - Editing")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result edit(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.updateById(sysCheckRule);
        return Result.ok("编辑成功!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Encoding check rule - Delete by ID")
    @ApiOperation(value = "Encoding check rule - Delete by ID", notes = "Encoding check rule - Delete by ID")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestParam(name = "id", required = true) String id) {
        sysCheckRuleService.removeById(id);
        return Result.ok("The deletion is successful!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Encoding Verification Rule - Batch deletion")
    @ApiOperation(value = "Encoding Verification Rule - Batch deletion", notes = "Encoding Verification Rule - Batch deletion")
    @DeleteMapping(value = "/deleteBatch")
    public Result deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysCheckRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("The batch deletion is successful！");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Encoding Checksum Rule - Query by ID")
    @ApiOperation(value = "Encoding Checksum Rule - Query by ID", notes = "Encoding Checksum Rule - Query by ID")
    @GetMapping(value = "/queryById")
    public Result queryById(@RequestParam(name = "id", required = true) String id) {
        SysCheckRule sysCheckRule = sysCheckRuleService.getById(id);
        return Result.ok(sysCheckRule);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysCheckRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysCheckRule sysCheckRule) {
        return super.exportXls(request, sysCheckRule, SysCheckRule.class, "Encoding check rules");
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysCheckRule.class);
    }

}
