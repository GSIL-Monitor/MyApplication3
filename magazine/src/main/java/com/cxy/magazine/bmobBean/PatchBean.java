package com.cxy.magazine.bmobBean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by cxy on 2018/3/6.
 * 补丁
 */

public class PatchBean extends BmobObject {
    private Integer patchVersion;     //当前应用补丁版本
    private BmobFile patchFile;          //补丁文件



    public Integer getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(Integer patchVersion) {
        this.patchVersion = patchVersion;
    }

    public BmobFile getPatchFile() {
        return patchFile;
    }

    public void setPatchFile(BmobFile patchFile) {
        this.patchFile = patchFile;
    }
}
