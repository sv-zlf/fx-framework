package com.fxly.demo.util.tree;

import java.util.List;

/**
 * Treeable接口定义了树形结构对象的基本行为。
 * 该接口适用于需要表示具有层级关系的树状结构的数据模型。
 */
public interface Treeable<T> {
    /**
     * 获取当前节点的父节点ID。
     *
     * @return 父节点的ID，通常用于构建节点之间的关系。
     */
    Long getParentId();

    /**
     * 获取当前节点的ID。
     *
     * @return 当前节点的唯一标识符。
     */
    Long getId();

    /**
     * 获取当前节点的子节点列表。
     *
     * @return 子节点的列表，列表中的每个元素都实现了Treeable接口。
     */
    List<T> getChildren();

    Integer getSort();


}

