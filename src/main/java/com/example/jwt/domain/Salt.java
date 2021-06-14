package com.example.jwt.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class Salt {

    @Id
    @GeneratedValue
    private int id;

    @NotNull()
    private String sait;

    public Salt(){
    }
    public Salt(String salt){
        this.sait = salt;
    }
}
