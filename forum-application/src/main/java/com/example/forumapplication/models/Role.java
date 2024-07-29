package com.example.forumapplication.models;

import com.example.forumapplication.models.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "role", unique = true, nullable = false)
    private String name;

//    public void setName(String role) {
//        RoleEnum roleEnum = RoleEnum.valueOf(role);
//        this.name = roleEnum.name();
//    }

}
