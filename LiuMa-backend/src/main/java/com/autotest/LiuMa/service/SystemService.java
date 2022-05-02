package com.autotest.LiuMa.service;

import com.autotest.LiuMa.database.domain.Assertion;
import com.autotest.LiuMa.database.mapper.AssertionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SystemService {

    @Resource
    private AssertionMapper assertionMapper;

    public List<Assertion> getAssertion() {
        return assertionMapper.getAssertion();
    }

}
