package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Version;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VersionMapper {

    Version getVersionByName(String projectId, String name);

    void saveVersion(Version version);

    void deleteVersion(String id);

    List<Version> getVersionList(String projectId, String condition);
}