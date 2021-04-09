package com.zgg.request.dao.archive;

import java.util.List;

import com.zgg.common.config.MyMapper;
import com.zgg.request.bean.archive.ZcyPtArchiveAsk;
import org.apache.ibatis.annotations.Param;

/**
 * 案件要求信息表
 *
 * @author zy
 * @datatime 2020-09-02 02:13:21
 */
public interface ZcyPtArchiveAskMapper extends MyMapper<ZcyPtArchiveAsk> {


    List<ZcyPtArchiveAsk> getZcyPtArchiveAskListByIds(@Param("ids") List<Integer> ids);

    int deleteByPrimaryKeyOver(Integer id);

    int insertOver(ZcyPtArchiveAsk record);

    int updateByPrimaryKeySelectiveOver(ZcyPtArchiveAsk record);
}

