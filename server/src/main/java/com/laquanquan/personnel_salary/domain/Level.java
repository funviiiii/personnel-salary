package com.laquanquan.personnel_salary.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author lqq
 * @TableName t_level
 */
@Data
public class Level implements Serializable {
    /**
     * 主键
     */
    @JsonIgnore
    private Long id;

    /**
     * 操作行为级别号
     */
    @JsonProperty
    private Integer code;

    /**
     * 操作行为级别名
     */
    @JsonProperty
    private String levelName;

    /**
     * 创建时间
     */
    @JsonIgnore
    private Date createTime;

    /**
     * 上次更新时间
     */
    @JsonIgnore
    private Date updateTime;

    /**
     * 逻辑删除标识: 已删除(1), 未删除(0)
     */
    @JsonIgnore
    private Boolean isDeleted;

    private static final long serialVersionUID = 1L;
}