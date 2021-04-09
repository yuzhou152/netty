package com.zgg.request.controller.archive;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import com.zgg.common.action.BaseAction;
import com.zgg.common.json.JsonResult;
import com.zgg.request.bean.archive.ZcyPtArchiveAsk;
import com.zgg.request.service.archive.ZcyPtArchiveAskService;
import com.zgg.session.UserSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 案件要求信息表
 *
 * @author zy
 * @datatime 2020-09-02 02:13:22
 */
@RestController
@RequestMapping("/zcyPtArchiveAsk")
public class ZcyPtArchiveAskController extends BaseAction {

    @Resource
    private ZcyPtArchiveAskService zcyPtArchiveAskService;


    /**
     * Description: 保存
     * Author: zy
     * Date: 2020-09-07 15:51:49
     */
    @RequestMapping(value = "/saveZcyPtArchiveAsk", method = RequestMethod.POST)
    public JsonResult<ZcyPtArchiveAsk> saveZcyPtArchive(@RequestBody ZcyPtArchiveAsk zcyPtArchiveAsk) throws Exception {
        UserSession userSession = getCurrentUser();
        return JsonResult.SUCCESS(this.zcyPtArchiveAskService.saveZcyPtArchiveAsk(zcyPtArchiveAsk, userSession));
    }

    /**
     * Description: 删除
     * Author: zy
     * Date: 2020-09-07 15:52:11
     */
    @RequestMapping(value = "/deleteZcyPtArchiveAskById/{id}", method = RequestMethod.GET)
    public JsonResult deleteZcyPtArchiveAskById(@PathVariable Integer id) {
        this.zcyPtArchiveAskService.deleteZcyPtArchiveAskById(id);
        return JsonResult.SUCCESS();
    }

    /**
     * Description: 修改
     * Author: zy
     * Date: 2020-09-07 15:51:56
     */
    @RequestMapping(value = "/updateZcyPtArchiveAsk", method = RequestMethod.PUT)
    public JsonResult<ZcyPtArchiveAsk> updateZcyPtArchiveAsk(@RequestBody ZcyPtArchiveAsk zcyPtArchiveAsk) {
        UserSession userSession = getCurrentUser();
        this.zcyPtArchiveAskService.updateZcyPtArchiveAsk(zcyPtArchiveAsk, userSession);
        return JsonResult.SUCCESS();
    }

    /**
     * Description: 详情
     * Author: zy
     * Date: 2020-09-07 15:52:05
     */
    @RequestMapping(value = "/getZcyPtArchiveAskById/{id}", method = RequestMethod.GET)
    public JsonResult<ZcyPtArchiveAsk> getZcyPtArchiveAskById(@PathVariable Integer id) {
        ZcyPtArchiveAsk sr = zcyPtArchiveAskService.getZcyPtArchiveAskById(id);
        return JsonResult.SUCCESS(sr);
    }

    /**
     * Description: 列表
     * Author: zy
     * Date: 2020-09-07 15:52:11
     */
    @RequestMapping(value = "/getZcyPtArchiveAskList", method = RequestMethod.POST)
    public JsonResult<List<ZcyPtArchiveAsk>> getZcyPtArchiveAskList(@RequestBody ZcyPtArchiveAsk zcyPtArchiveAsk) {
        List<ZcyPtArchiveAsk> sr = this.zcyPtArchiveAskService.getZcyPtArchiveAskList(zcyPtArchiveAsk);
        return JsonResult.SUCCESS(sr);
    }


    /**
     * Description: 分页
     * Author: zy
     * Date: 2020-09-07 15:52:11
     */
    @RequestMapping(value = "/getZcyPtArchiveAskPageList", method = RequestMethod.POST)
    public JsonResult<PageInfo<ZcyPtArchiveAsk>> getZcyPtArchiveAskPageList(@RequestBody ZcyPtArchiveAsk zcyPtArchiveAsk, PageInfo pageInfo) {
        //UserSession userSession = getCurrentUser();
        PageInfo<ZcyPtArchiveAsk> sr = this.zcyPtArchiveAskService.getZcyPtArchiveAskPageList(zcyPtArchiveAsk, pageInfo);
        return JsonResult.SUCCESS(sr);
    }

}

