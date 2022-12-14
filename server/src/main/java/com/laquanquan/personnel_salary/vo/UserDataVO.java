package com.laquanquan.personnel_salary.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lqq
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataVO {
    @JsonProperty
    private String uid;
    @JsonProperty
    private String name;
    @JsonProperty
    private String gender;
    @JsonProperty
    private Date birthday;
    @JsonProperty
    private Date induction;
    @JsonProperty
    private String department;
    @JsonProperty
    private String role;
    @JsonProperty
    private Boolean isMarried;
    @JsonProperty
    private String resume;
}
