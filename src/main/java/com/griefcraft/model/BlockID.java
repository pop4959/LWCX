package com.griefcraft.model;

import com.griefcraft.lwc.LWC;

public class BlockID {

    /**
     * Block id in the database
     */
    private int id;

    /**
     * Block name
     */
    private String name;

    /**
     * Force a protection update to the live database
     */
    public void saveNow() {
        LWC.getInstance().getPhysicalDatabase().saveBlock(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
