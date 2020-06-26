package com.hccake.ballcat.codegen.model.converter;

import com.hccake.ballcat.codegen.model.dto.TemplateDirectoryCreateDTO;
import com.hccake.ballcat.codegen.model.dto.TemplateInfoDTO;
import com.hccake.ballcat.codegen.model.entity.TemplateDirectoryEntry;
import com.hccake.ballcat.codegen.model.entity.TemplateInfo;
import com.hccake.ballcat.codegen.model.vo.TemplateDirectory;
import com.hccake.ballcat.codegen.model.vo.TemplateDirectoryEntryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/6/19 20:01
 */
@Mapper
public interface TemplateModelConverter {

	TemplateModelConverter INSTANCE = Mappers.getMapper(TemplateModelConverter.class);

	/**
	 * 转换 DirectoryEntryPO 为 DirectoryEntryVO
	 * @param templateDirectoryEntry templateDirectoryEntry
	 * @return VO
	 */
	TemplateDirectoryEntryVO entryPoToVo(TemplateDirectoryEntry templateDirectoryEntry);

	/**
	 * 转换 DirectoryEntryCreateDTO to DirectoryEntry
	 * @param directoryCreateDTO directoryCreateDTO
	 * @return TemplateDirectoryEntry 持久对象
	 */
	TemplateDirectoryEntry entryCreateDtoToPo(TemplateDirectoryCreateDTO directoryCreateDTO);

	/**
	 * 转换为目录树
	 * @param templateDirectoryEntry templateDirectoryEntry
	 * @return TemplateDirectoryTree
	 */
	TemplateDirectory entryPoToTree(TemplateDirectoryEntry templateDirectoryEntry);

	/**
	 * TemplateInfoDto 转换为 TemplateInfo
	 * @param templateInfoDTO 模板详情信息DTO
	 * @return TemplateInfo
	 */
	TemplateInfo infoDtoToPo(TemplateInfoDTO templateInfoDTO);

}
