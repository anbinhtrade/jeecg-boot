package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.model.DuplicateCheckVo;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Title: DuplicateCheckAction
 * @Description: Repeat verification tool
 * @Author Zhang Daihao
 * @Date 2019-03-25
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/duplicate")
@Api(tags="Repeat check")
public class DuplicateCheckController {

	@Autowired
	ISysDictService sysDictService;

	/**
	 * Verify whether the data exists in the system
	 * 
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	@ApiOperation("Duplicate verification interface")
	public Result<String> doDuplicateCheck(DuplicateCheckVo duplicateCheckVo, HttpServletRequest request) {
		log.debug("----duplicate check------："+ duplicateCheckVo.toString());
		
		// 1.填值为空，直接返回
		if(StringUtils.isEmpty(duplicateCheckVo.getFieldVal())){
			Result rs = new Result();
			rs.setCode(500);
			rs.setSuccess(true);
			rs.setMessage("The data is empty and will not be processed.！");
			return rs;
		}
		
		// 2.返回结果
		if (sysDictService.duplicateCheckData(duplicateCheckVo)) {
			// 该值可用
			return Result.ok("The value is available!");
		} else {
			// 该值不可用
			log.info("The value is not available and already exists in the system!");
			return Result.error("The value is not available and already exists in the system!");
		}
	}


}
