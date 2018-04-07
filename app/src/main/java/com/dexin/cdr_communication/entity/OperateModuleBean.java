package com.dexin.cdr_communication.entity;

/**
 * 操作模块实体
 */
public class OperateModuleBean {
    private int imgId;
    private String name;

    public OperateModuleBean(int imgId, String name) {
        this.imgId = imgId;
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
