package com.fxly.demo.util.tree;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fxly.demo.system.global.GlobalException;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形结构工具类
 */
public class TreeUtils {

    /**
     * 将具有父子关系的数据列表转换为树形结构列表。
     * @param dataList 原始数据列表，不能为空，其中元素需要有getPid()获取父ID和getId()获取ID以及getChildren()设置子节点集合的方法。
     * @return 转换后的树形结构列表。如果输入列表为空或null，则返回空列表。
     * @throws RuntimeException 当父节点在列表中不存在时抛出异常。
     */
    public static <T extends Treeable<T>> List<T> buildTree(List<T> dataList) {
        // 检查输入列表是否为空
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        // 将列表转换为树形结构
        List<T> treeList = new LinkedList<>();
        // 存储所有节点，包括根节点和非根节点，以便快速查找
        Map<Long, T> treeMap = dataList.stream().collect(Collectors.toMap(T::getId, data -> data));
        // 处理根节点
        dataList.forEach(data -> {
            Long parentId = data.getParentId();
            if (parentId == null || parentId == 0) {
                treeList.add(data);
            } else {
                // 处理非根节点，将其添加到其父节点下
                T parent = treeMap.get(data.getParentId());
                if (parent != null) {
                    parent.getChildren().add(data);
                } else {
                    // 如果找不到父节点，自身作为主节点
                    treeList.add(data);
//                    throw new GlobalException("找不到父节点：" + data);
                }
            }
        });
        sortTree(treeList);
        return treeList;
    }
    //
    public static <T extends Treeable<T>> void sortTree(List<T> dataList) {
        dataList.sort(Comparator.comparingInt(T::getSort));

        for (T data : dataList) {
            if (data.getChildren() != null && !data.getChildren().isEmpty()) {
                sortTree(data.getChildren());
            }
        }
    }
}

