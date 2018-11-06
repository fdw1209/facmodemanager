

package com.cbox.facmodemanager;

/**
 * device list bean
 *
 * @author ben-jb.chiu
 *
 */
public class StorageItem {
    /**
     * Storage Item path
     */
    public String path;

    /**
     * Storage Item can be write?
     */
    public String state;

    /**
     * Storage Item can be remove?
     */
    public boolean isRemoveable;

    public StorageItem(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }

    @Override
    public String toString() {
        return "StorageInfo [path=" + path + ", state=" + state + ", isRemoveable=" + isRemoveable + "]";
    }
}
