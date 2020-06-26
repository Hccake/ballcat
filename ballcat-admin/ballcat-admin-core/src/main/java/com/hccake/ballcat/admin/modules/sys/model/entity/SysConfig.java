package com.hccake.ballcat.admin.modules.sys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置表
 *
 * @author ballcat code generator
 * @date 2019-10-14 17:42:23
 */
@Data
@TableName("sys_config")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "基础配置")
public class SysConfig extends Model<SysConfig> {

	/**
	 * 主键
	 */
	@TableId
	@ApiModelProperty(value = "主键ID")
	private Integer id;

	/**
	 * 配置名称
	 */
	@ApiModelProperty(value = "配置名称")
	private String name;

	/**
	 * 配置在缓存中的key名
	 */
	@ApiModelProperty(value = "配置在缓存中的key名")
	private String confKey;

	/**
	 * 配置值
	 */
	@ApiModelProperty(value = "配置值")
	private String confValue;

	/**
	 * 分类
	 */
	@ApiModelProperty(value = "分类")
	private String category;

	/**
	 * 描述
	 */
	@ApiModelProperty(value = "描述")
	private String description;

	/**
	 * 逻辑删除标识，已删除:0，未删除：删除时间戳
	 */
	@TableLogic
	@ApiModelProperty(value = "逻辑删除标识，已删除:0，未删除：删除时间戳")
	private Long deleted;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

}
