package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Case;
import com.autotest.LiuMa.database.domain.Collection;
import com.autotest.LiuMa.database.domain.CollectionCase;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectionDTO extends Collection {

    private String username;

    private String versionName;

    private List<CollectionCaseDTO> collectionCases;

}
