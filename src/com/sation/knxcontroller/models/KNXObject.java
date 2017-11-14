package com.sation.knxcontroller.models;

import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.util.MapUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wangchunfeng on 2017/8/15.
 */

public class KNXObject implements Serializable {
    /// <summary>
    /// 对象群组地址
    /// </summary>
    private Map<String, KNXSelectedAddress> MapSelectedAddress;
    public Map<String, KNXSelectedAddress> getSelectedAddress() {
        return this.MapSelectedAddress;
    }
    public KNXGroupAddress getGroupAddress() {
        try {
            KNXSelectedAddress addr = MapUtils.getFirstOrNull(this.getSelectedAddress());
            if (null != addr) {
                String addressId = addr.getId();
                return STKNXControllerApp.getInstance().getGroupAddressIdMap().get(addressId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /// <summary>
    /// 对象是否启用
    /// </summary>
    private boolean Enable;
    public boolean getEnable() {
        return this.Enable;
    }
}
