package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.dto.CollectionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.CollectionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/autotest/collection")
public class CollectionController {

    @Resource
    private CollectionService collectionService;

    @PostMapping("/save")
    public void saveCollection(@RequestBody CollectionDTO collectionDTO, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        collectionDTO.setUpdateUser(user);
        collectionService.saveCollection(collectionDTO);
    }

    @PostMapping("/delete")
    public void deleteCollection(@RequestBody CollectionDTO collectionDTO) {
        collectionService.deleteCollection(collectionDTO);
    }

    @GetMapping("/detail/{collectionId}")
    public CollectionDTO getCollectionDetail(@PathVariable String collectionId){
        return collectionService.getCollectionDetail(collectionId);
    }

    @GetMapping("/types/{collectionId}")
    public Map<String, Boolean> getCollectionCaseTypes(@PathVariable String collectionId){
        return collectionService.getCollectionCaseTypes(collectionId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<CollectionDTO>> getCollectionList(@PathVariable int goPage, @PathVariable int pageSize,
                                           @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, collectionService.getCollectionList(request));
    }
}
