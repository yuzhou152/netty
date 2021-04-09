package com.zgg.request.service.archive;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.zgg.request.bean.archive.ZcyPtArchiveAsk;
import com.zgg.session.UserSession;

/**
 * 案件要求信息表
 *
 * @author zy
 * @datatime 2020-09-02 02:13:21
 */
public interface ZcyPtArchiveAskService {


    /**
     * Description: 保存
     * Author: zy
     * Date: 2020-09-07 15:51:49
     */
    ZcyPtArchiveAsk saveZcyPtArchiveAsk(ZcyPtArchiveAsk zcyPtArchiveAsk, UserSession userSession) throws Exception;

    /**
     * Description: 删除
     * Author: zy
     * Date: 2020-09-07 15:52:05
     */
    void deleteZcyPtArchiveAskById(Integer id);

    /**
     * Description: 修改
     * Author: zy
     * Date: 2020-09-07 15:51:56
     */
    void updateZcyPtArchiveAsk(ZcyPtArchiveAsk zcyPtArchiveAsk, UserSession userSession);

    /**
     * Description: 详情
     * Author: zy
     * Date: 2020-09-07 15:52:05
     */
    ZcyPtArchiveAsk getZcyPtArchiveAskById(Integer id);

    /**
     * Description: 列表
     * Author: zy
     * Date: 2020-09-07 15:52:11
     */
    List<ZcyPtArchiveAsk> getZcyPtArchiveAskList(ZcyPtArchiveAsk zcyPtArchiveAsk);

    /**
     * Description: 分页
     * Author: zy
     * Date: 2020-09-07 15:52:11
     */
    PageInfo<ZcyPtArchiveAsk> getZcyPtArchiveAskPageList( ZcyPtArchiveAsk zcyPtArchiveAsk, PageInfo pageInfo);
}

