package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Assertion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AssertionMapper {
    List<Assertion> getAssertion();
}