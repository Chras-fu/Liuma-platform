package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTO extends User {
    List<String> permissions;
}
