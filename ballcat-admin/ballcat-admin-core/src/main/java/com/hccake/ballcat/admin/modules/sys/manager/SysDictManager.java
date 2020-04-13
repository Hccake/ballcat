package com.hccake.ballcat.admin.modules.sys.manager;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccake.ballcat.admin.modules.sys.model.converter.SysDictConverter;
import com.hccake.ballcat.admin.modules.sys.model.entity.SysDict;
import com.hccake.ballcat.admin.modules.sys.model.entity.SysDictItem;
import com.hccake.ballcat.admin.modules.sys.model.qo.SysDictQO;
import com.hccake.ballcat.admin.modules.sys.model.vo.DictDataAndHashVO;
import com.hccake.ballcat.admin.modules.sys.model.vo.DictItemVO;
import com.hccake.ballcat.admin.modules.sys.service.SysDictItemService;
import com.hccake.ballcat.admin.modules.sys.service.SysDictService;
import com.hccake.ballcat.common.core.constant.enums.BooleanEm;
import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.core.result.BaseResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hccake
 * @version 1.0
 * @date 2020/3/27 19:50
 */
@Service
@RequiredArgsConstructor
public class SysDictManager {
    private final SysDictService sysDictService;
    private final SysDictItemService sysDictItemService;


    /**
     * 字典表分页
     *
     * @param page      分页参数
     * @param sysDictQO 查询参数
     * @return 字典表分页数据
     */
    public IPage<SysDict> dictPage(Page<SysDict> page, SysDictQO sysDictQO) {
        return sysDictService.page(page, sysDictQO);
    }

    /**
     * 保存字典
     *
     * @param sysDict 字典对象
     * @return 执行是否成功
     */
    public boolean dictSave(SysDict sysDict) {
        sysDict.setHashCode(IdUtil.fastSimpleUUID());
        sysDict.setDelFlag(BooleanEm.FALSE.getValue());
        return sysDictService.save(sysDict);
    }

    /**
     * 更新字典
     *
     * @param sysDict 字典对象
     * @return 执行是否成功
     */
    public boolean updateDictById(SysDict sysDict) {
        // 查询现有数据
        SysDict dict = sysDictService.getById(sysDict.getId());
        if (BooleanEm.TRUE.getValue() != dict.getEditable()) {
            throw new BusinessException(
                    BaseResultCode.LOGIC_CHECK_ERROR.getCode(), "该字典项目不能修改"
            );
        }
        sysDict.setHashCode(IdUtil.fastSimpleUUID());
        return sysDictService.updateById(sysDict);
    }

    /**
     * 删除字典
     *
     * @param id 字典id
     * @return 执行是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removeDictById(Integer id) {
        // 查询现有数据
        SysDict dict = sysDictService.getById(id);
        if (BooleanEm.TRUE.getValue() != dict.getEditable()) {
            throw new BusinessException(
                    BaseResultCode.LOGIC_CHECK_ERROR.getCode(), "该字典项目不能删除"
            );
        }
        // 需级联删除对应的字典项
        if (sysDictService.removeById(id)) {
            sysDictItemService.remove(Wrappers.<SysDictItem>lambdaUpdate()
                    .eq(SysDictItem::getDictCode, dict.getCode())
            );
            return true;
        }
        return false;
    }

    /**
     * 字典项分页
     *
     * @param page     分页属性
     * @param dictCode 字典标识
     * @return 字典项分页数据
     */
    public IPage<SysDictItem> dictItemPage(Page<SysDictItem> page, String dictCode) {
        return sysDictItemService.page(page, dictCode);
    }

    /**
     * 新增字典项
     *
     * @param sysDictItem 字典项
     * @return 执行是否成功
     */
    public boolean saveDictItem(SysDictItem sysDictItem) {
        // 更新字典项Hash值
        if (!sysDictService.updateHashCode(sysDictItem.getDictCode())) {
            return false;
        }
        sysDictItem.setDelFlag(BooleanEm.FALSE.getValue());
        return sysDictItemService.save(sysDictItem);
    }


    /**
     * 更新字典项
     *
     * @param sysDictItem 字典项
     * @return 执行是否成功
     */
    public boolean updateDictItemById(SysDictItem sysDictItem) {
        // 根据ID查询字典
        SysDict dict = sysDictService.getByCode(sysDictItem.getDictCode());
        // 校验是否可编辑
        if (BooleanEm.TRUE.getValue() != dict.getEditable()) {
            throw new BusinessException(
                    BaseResultCode.LOGIC_CHECK_ERROR.getCode(), "该字典项目不能修改"
            );
        }
        // 更新字典项Hash值
        if (!sysDictService.updateHashCode(sysDictItem.getDictCode())) {
            return false;
        }
        return sysDictItemService.updateById(sysDictItem);

    }


    /**
     * 删除字典项
     *
     * @param id 字典项
     * @return 执行是否成功
     */
    public boolean removeDictItemById(Integer id) {
        // 根据ID查询字典
        SysDictItem dictItem = sysDictItemService.getById(id);
        SysDict dict = sysDictService.getByCode(dictItem.getDictCode());
        // 校验是否系统内置
        if (BooleanEm.TRUE.getValue() != dict.getEditable()) {
            throw new BusinessException(
                    BaseResultCode.LOGIC_CHECK_ERROR.getCode(), "该字典项目不能删除"
            );
        }
        return sysDictItemService.removeById(id);
    }

    /**
     * 查询字典数据
     *
     * @param dictCode
     * @return
     */
    public DictDataAndHashVO queryDictDataAndHashVO(String dictCode) {
        // 查询对应hash值，以及字典项数据
        SysDict sysDict = sysDictService.getByCode(dictCode);
        if (sysDict == null) {
            return null;
        }
        List<SysDictItem> dictItems = sysDictItemService.getByDictCode(dictCode);
        // 排序并转换为VO
        List<DictItemVO> dictList = dictItems.stream()
                .sorted(Comparator.comparingInt(SysDictItem::getSort))
                .map(SysDictConverter.INSTANCE::itemPoToVo)
                .collect(Collectors.toList());

        // 组装DataVO
        DictDataAndHashVO dictDataAndHashVO = new DictDataAndHashVO();
        dictDataAndHashVO.setHashCode(sysDict.getHashCode());
        dictDataAndHashVO.setDictList(dictList);
        return dictDataAndHashVO;
    }

    /**
     * 返回失效的Hash
     *
     * @param dictHashCode 校验的hashCodeMap
     * @return
     */
    public List<String> invalidDictHash(Map<String, String> dictHashCode) {
        List<SysDict> byCode = sysDictService.getByCode(dictHashCode.keySet().toArray(new String[]{}));
        // 过滤相等Hash值的字典项，并返回需要修改的字典项的Code
        return byCode.stream()
                .filter(x -> !dictHashCode.get(x.getCode()).equals(x.getHashCode()))
                .map(SysDict::getCode).collect(Collectors.toList());
    }
}
