package org.jeecg.modules.system.model;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Title: DuplicateCheckVo
 * @Description: Repeat the verification of VO
 * @Author Zhang Daihao
 * @Date 2019-03-25
 * @Version V1.0
 */
@Data
@ApiModel(value="Duplicate validation of the data model",description="Duplicate validation of the data model")
public class DuplicateCheckVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Table name
	 */
	@ApiModelProperty(value="Table name",name="tableName",example="sys_log")
	private String tableName;
	
	/**
	 * The name of the field
	 */
	@ApiModelProperty(value="The name of the field",name="fieldName",example="id")
	private String fieldName;
	
	/**
	 * Field values
	 */
	@ApiModelProperty(value="Field values",name="fieldVal",example="1000")
	private String fieldVal;
	
	/**
	 * Data ID
	*/
	@ApiModelProperty(value="Data ID",name="dataId",example="2000")
	private String dataId;

}