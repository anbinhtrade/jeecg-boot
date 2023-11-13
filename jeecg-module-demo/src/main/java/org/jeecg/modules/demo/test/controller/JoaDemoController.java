package org.jeecg.modules.demo.test.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.test.entity.JoaDemo;
import org.jeecg.modules.demo.test.service.IJoaDemoService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: Process testing
 * @Author: jeecg-boot
 * @Date:   2019-05-14
 * @Version: V1.0
 */
@RestController
@RequestMapping("/test/joaDemo")
@Slf4j
public class JoaDemoController {
	@Autowired
	private IJoaDemoService joaDemoService;
	
	/**
	  * Paginated list queries
	 * @param joaDemo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<JoaDemo>> queryPageList(JoaDemo joaDemo,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<JoaDemo>> result = new Result<IPage<JoaDemo>>();
		QueryWrapper<JoaDemo> queryWrapper = QueryGenerator.initQueryWrapper(joaDemo, req.getParameterMap());
		Page<JoaDemo> page = new Page<JoaDemo>(pageNo, pageSize);
		IPage<JoaDemo> pageList = joaDemoService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   Add to
	 * @param joaDemo
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<JoaDemo> add(@RequestBody JoaDemo joaDemo) {
		Result<JoaDemo> result = new Result<JoaDemo>();
		try {
			joaDemoService.save(joaDemo);
			result.success("Added successfully!");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}
	
	/**
	  *  EDIT
	 * @param joaDemo
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<JoaDemo> edit(@RequestBody JoaDemo joaDemo) {
		Result<JoaDemo> result = new Result<JoaDemo>();
		JoaDemo joaDemoEntity = joaDemoService.getById(joaDemo.getId());
		if(joaDemoEntity==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = joaDemoService.updateById(joaDemo);
			//TODO What does returning false mean?
			if(ok) {
				result.success("Modification successful!");
			}
		}
		
		return result;
	}
	
	/**
	  *   Delete by ID
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<JoaDemo> delete(@RequestParam(name="id",required=true) String id) {
		Result<JoaDemo> result = new Result<JoaDemo>();
		JoaDemo joaDemo = joaDemoService.getById(id);
		if(joaDemo==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = joaDemoService.removeById(id);
			if(ok) {
				result.success("Deleted successfully!");
			}
		}
		
		return result;
	}
	
	/**
	  *  Delete in bulk
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<JoaDemo> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<JoaDemo> result = new Result<JoaDemo>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized!");
		}else {
			this.joaDemoService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("Deleted successfully!");
		}
		return result;
	}
	
	/**
	  * Query by ID
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<JoaDemo> queryById(@RequestParam(name="id",required=true) String id) {
		Result<JoaDemo> result = new Result<JoaDemo>();
		JoaDemo joaDemo = joaDemoService.getById(id);
		if(joaDemo==null) {
			result.error500("No corresponding entity found");
		}else {
			result.setResult(joaDemo);
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
      // Step.1 Assemble query criteria
      QueryWrapper<JoaDemo> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              JoaDemo joaDemo = JSON.parseObject(deString, JoaDemo.class);
              queryWrapper = QueryGenerator.initQueryWrapper(joaDemo, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi Export to Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<JoaDemo> pageList = joaDemoService.list(queryWrapper);
      //The name of the export file
      mv.addObject(NormalExcelConstants.FILE_NAME, "A list of process tests");
      mv.addObject(NormalExcelConstants.CLASS, JoaDemo.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("Process test list data", "Exporter: Jeecg", "Export information"));
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
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // Obtain the object to which the file was uploaded
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<JoaDemo> listJoaDemos = ExcelImportUtil.importExcel(file.getInputStream(), JoaDemo.class, params);
              for (JoaDemo joaDemoExcel : listJoaDemos) {
                  joaDemoService.save(joaDemoExcel);
              }
              return Result.ok("The file was imported successfully！Number of rows of data:" + listJoaDemos.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("File import failed:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("File import failed！");
  }

}
