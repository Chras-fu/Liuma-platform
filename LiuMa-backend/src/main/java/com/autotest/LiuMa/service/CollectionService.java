package com.autotest.LiuMa.service;

import com.autotest.LiuMa.database.domain.Collection;
import com.autotest.LiuMa.database.domain.CollectionCase;
import com.autotest.LiuMa.database.mapper.CollectionCaseMapper;
import com.autotest.LiuMa.database.mapper.CollectionMapper;
import com.autotest.LiuMa.dto.CollectionCaseDTO;
import com.autotest.LiuMa.dto.CollectionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class CollectionService {

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    public void saveCollection(CollectionDTO collectionDTO) {
        if(collectionDTO.getId().equals("") || collectionDTO.getId() == null){ // 新增集合
            collectionDTO.setId(UUID.randomUUID().toString());
            collectionDTO.setCreateTime(System.currentTimeMillis());
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionDTO.setCreateUser(collectionDTO.getUpdateUser());
            collectionMapper.addCollection(collectionDTO);
        }else{ // 修改集合
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionMapper.updateCollection(collectionDTO);
        }
        collectionCaseMapper.deleteCollectionCase(collectionDTO.getId());  //先删除全部集合用例
        List<CollectionCase> collectionCases = new ArrayList<>();
        for(CollectionCaseDTO collectionCaseDTO: collectionDTO.getCollectionCases()){
            CollectionCase collectionCase = new CollectionCase();
            collectionCase.setId(UUID.randomUUID().toString());
            collectionCase.setIndex(collectionCaseDTO.getIndex());
            collectionCase.setCollectionId(collectionDTO.getId());
            collectionCase.setCaseId(collectionCaseDTO.getCaseId());
            collectionCases.add(collectionCase);
        }
        if(collectionCases.size() > 0) {
            collectionCaseMapper.addCollectionCase(collectionCases);
        }
    }

    public void deleteCollection(Collection collection) {
        collectionCaseMapper.deleteCollectionCase(collection.getId());
        collectionMapper.deleteCollection(collection.getId());
    }

    public CollectionDTO getCollectionDetail(String collectionId) {
        CollectionDTO collectionDTO = collectionMapper.getCollectionDetail(collectionId);
        List<CollectionCaseDTO> collectionCaseDTOS = collectionCaseMapper.getCollectionCaseList(collectionId);
        collectionDTO.setCollectionCases(collectionCaseDTOS);

        return collectionDTO;
    }

    public List<CollectionDTO> getCollectionList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return collectionMapper.getCollectionList(request);
    }
}
