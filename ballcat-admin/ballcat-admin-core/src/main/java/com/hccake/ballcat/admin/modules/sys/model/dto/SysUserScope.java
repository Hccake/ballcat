package com.hccake.ballcat.admin.modules.sys.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Hccake
 * @version 1.0
 * @date 2019/9/24 10:13
 * <p>
 * 用户权限信息，基础只有roleIds
 * <p>
 * 后续业务相关的授权 按需扩展
 */
@Data
public class SysUserScope {

	private List<Integer> roleIds;

}
