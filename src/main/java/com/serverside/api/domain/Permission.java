package com.serverside.api.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Entity
@Table(name = "permission", schema = "public")
public class Permission extends BaseIdDomain {

    @Column
    private String role;

    @Column
    private String description;
}
