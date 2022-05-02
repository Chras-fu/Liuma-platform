package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.TestFile;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TestFileDTO extends TestFile {

    private String username;
}
