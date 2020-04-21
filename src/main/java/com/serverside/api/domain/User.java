package com.serverside.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
@Table(name = "user", schema = "public")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends BaseIdDomain {
    @NotBlank
    @Column
    private String name;

    @NotBlank(message = "E-mail is mandatory")
    @Email(message = "E-mail is not valid")
    @Column
    private String email;

    @ToString.Exclude
    @JsonIgnore
    @Column
    private String password;

    @Column
    private boolean enable;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<Group> groups;

}
