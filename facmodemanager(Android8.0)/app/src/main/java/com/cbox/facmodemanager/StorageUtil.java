
package com.cbox.facmodemanager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.storage.StorageManager;

/**
 * for get device list
 *
 * @author ben-jb.chiu
 *
 */
public class StorageUtil {

    /**
     * get Storage device list
     *
     * @param context
     * @return
     */
    public static List<StorageItem> listAllStorage(Context context) {//context��һ�������࣬������һ���������Ի�ȡϵͳ��Դ�����������
        ArrayList<StorageItem> storages = new ArrayList<StorageItem>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);//ͨ�������ȡgetVolumeList����
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageItem info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageItem(path);

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                    storages.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    /**
     * get Avaliable Storage device list
     *
     * @param infos
     * @return
     */
    public static List<StorageItem> getAvaliableStorage(List<StorageItem> infos) {
        List<StorageItem> storages = new ArrayList<StorageItem>();
        for (StorageItem info : infos) {
            File file = new File(info.path);
            if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                if (info.isMounted()) {
                    storages.add(info);
                }
            }
        }

        return storages;
    }

}
