package com.synchftp.model;

import java.util.List;

/**
 * Created by bhadz on 08.02.2018.
 */
public class SettingList {
    public List<Setting> manufacturers;

    public List<Setting> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<Setting> manufacturers) {
        this.manufacturers = manufacturers;
    }
}
