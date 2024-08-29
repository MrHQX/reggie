package com.it.reggie.dto;

import com.it.reggie.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;

}
