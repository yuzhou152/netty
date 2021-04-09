package com.zgg.request.service.archive.impl;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zgg.common.enums.CodeEC;
import com.zgg.common.exception.RestExceptionHandler.ZcyException;
import com.zgg.request.bean.archive.ZcyPtArchiveAsk;
import com.zgg.request.dao.archive.ZcyPtArchiveAskMapper;
import com.zgg.request.service.archive.ZcyPtArchiveAskService;
import com.zgg.session.UserSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * 案件要求信息表
 *
 * @author zy
 * @datatime 2020-09-02 02:13:21
 */
@Service
public class ZcyPtArchiveAskServiceImpl implements ZcyPtArchiveAskService {

    @Autowired
    private ZcyPtArchiveAskMapper zcyPtArchiveAskMapper;

    @Override
    public ZcyPtArchiveAsk saveZcyPtArchiveAsk(ZcyPtArchiveAsk zcyPtArchiveAsk, UserSession userSession) throws Exception {
        if (null == zcyPtArchiveAsk) {
            throw new ZcyException(CodeEC.PARAM_INVALID);
        }
        this.zcyPtArchiveAskMapper.insertUseGeneratedKeys(zcyPtArchiveAsk);
        return zcyPtArchiveAsk;
    }

    @Override
    public void deleteZcyPtArchiveAskById(Integer id) {
        zcyPtArchiveAskMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateZcyPtArchiveAsk(ZcyPtArchiveAsk zcyPtArchiveAsk, UserSession userSession) {
        if (null == zcyPtArchiveAsk || zcyPtArchiveAsk.getId() == null) {
            throw new ZcyException(CodeEC.PARAM_INVALID);
        }
        this.zcyPtArchiveAskMapper.updateByPrimaryKeySelective(zcyPtArchiveAsk);
    }

    @Override
    public ZcyPtArchiveAsk getZcyPtArchiveAskById(Integer id) {
        return zcyPtArchiveAskMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ZcyPtArchiveAsk> getZcyPtArchiveAskList(ZcyPtArchiveAsk zcyPtArchiveAsk) {
        Example exe = new Example(ZcyPtArchiveAsk.class);
        Example.Criteria criteria = exe.createCriteria();
        if (StringUtils.isNotEmpty(zcyPtArchiveAsk.getContent()))
            criteria.andCondition(" content like '%" + zcyPtArchiveAsk.getContent() + "%'");
        throw new ZcyException(CodeEC.PARAM_INVALID);
        //return this.zcyPtArchiveAskMapper.selectByExample(exe);
    }

    @Override
    public PageInfo<ZcyPtArchiveAsk> getZcyPtArchiveAskPageList( ZcyPtArchiveAsk zcyPtArchiveAsk, PageInfo pageInfo) {
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        Example exe = new Example(ZcyPtArchiveAsk.class);
        Example.Criteria criteria = exe.createCriteria();
        if (StringUtils.isNotEmpty(zcyPtArchiveAsk.getContent()))
            criteria.andCondition(" content like '%" + zcyPtArchiveAsk.getContent() + "%'");
        exe.setOrderByClause(" create_time asc");

        List<ZcyPtArchiveAsk> list =  this.zcyPtArchiveAskMapper.selectByExample(exe);
        PageInfo<ZcyPtArchiveAsk> page = new PageInfo<ZcyPtArchiveAsk>(list);
        return page;
    }
}

