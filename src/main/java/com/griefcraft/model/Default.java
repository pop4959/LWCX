package com.griefcraft.model;

import com.griefcraft.lwc.LWC;

/**
 *
 */
public class Default {
    /**
     * The owner of the default value.
     */
    String owner;
    /**
     * The data, which
     */
    String data;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOwner() {
        return owner;
    }

    public String getData() {
        return data;
    }

    public void save() {
        LWC lwc = LWC.getInstance();
        lwc.getPhysicalDatabase().saveDefault(this);
    }
}
