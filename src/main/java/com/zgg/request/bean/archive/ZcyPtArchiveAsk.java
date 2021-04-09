package com.zgg.request.bean.archive;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * 案件要求信息表
 *
 * @author zy
 * @datatime 2020-09-02 01:39:16
 */
@Alias(value = "zcyPtArchiveAsk")
@Table(name = "zcy_pt_archive_ask")
@Data
public class ZcyPtArchiveAsk implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键ID
     */
    @Id
    private Integer id;
    /**
     * 案件信息ID
     */
    @Column(name = "archive_id")
    private Integer archiveId;
    /**
     * 要求类型
     */
    @Column(name = "ask_type")
    private String askType;
    /**
     * 要求名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 要求内容
     */
    @Column(name = "content")
    private String content;
    /**
     * 创建人id
     */
    @Column(name = "create_id")
    private Integer createId;
    /**
     * 创建人名称
     */
    @Column(name = "create_name")
    private String createName;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private java.util.Date createTime;
    /**
     * 最后修改人id
     */
    @Column(name = "modify_id")
    private Integer modifyId;
    /**
     * 修改人名称
     */
    @Column(name = "modify_name")
    private String modifyName;
    /**
     * 最后修改时间
     */
    @Column(name = "modify_time")
    private java.util.Date modifyTime;


}

