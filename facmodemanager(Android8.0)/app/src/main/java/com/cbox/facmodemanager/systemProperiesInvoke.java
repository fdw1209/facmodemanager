package com.cbox.facmodemanager;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexFile;


public class systemProperiesInvoke
{

    /**
     * 锟斤拷锟捷革拷锟斤拷Key锟斤拷取值.
     * @return 锟斤拷锟斤拷锟斤拷锟斤拷诟锟絢ey锟津返回匡拷锟街凤拷锟斤拷
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static String get(Context context, String key) throws IllegalArgumentException {

        String ret= "";

        try{

          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = cl.loadClass("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[1];
          paramTypes[0]= String.class;

          Method get = SystemProperties.getMethod("get", paramTypes);

          //锟斤拷锟斤拷
          Object[] params= new Object[1];
          params[0]= new String(key);

          ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
            //TODO
        }

        return ret;

    }

    /**
     * 锟斤拷锟斤拷Key锟斤拷取值.
     * @return 锟斤拷锟絢ey锟斤拷锟斤拷锟斤拷, 锟斤拷锟斤拷锟斤拷锟絛ef锟斤拷为锟斤拷锟津返伙拷def锟斤拷锟津返回匡拷锟街凤拷锟斤拷
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static String get(Context context, String key, String def) throws IllegalArgumentException {

        String ret= def;

        try{

          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = cl.loadClass("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= String.class;          

          Method get = SystemProperties.getMethod("get", paramTypes);

          //锟斤拷锟斤拷
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new String(def);

          ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 锟斤拷锟捷革拷锟斤拷锟斤拷key锟斤拷锟斤拷int锟斤拷锟斤拷值.
     * @param key 要锟斤拷询锟斤拷key
     * @param def 默锟较凤拷锟斤拷值
     * @return 锟斤拷锟斤拷一锟斤拷int锟斤拷锟酵碉拷值, 锟斤拷锟矫伙拷蟹锟斤拷锟斤拷蚍祷锟侥拷锟街�
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {

        Integer ret= def;

        try{

          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = cl.loadClass("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= int.class;  

          Method getInt = SystemProperties.getMethod("getInt", paramTypes);

          //锟斤拷锟斤拷
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new Integer(def);

          ret= (Integer) getInt.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 锟斤拷锟捷革拷锟斤拷锟斤拷key锟斤拷锟斤拷long锟斤拷锟斤拷值.
     * @param key 要锟斤拷询锟斤拷key
     * @param def 默锟较凤拷锟斤拷值
     * @return 锟斤拷锟斤拷一锟斤拷long锟斤拷锟酵碉拷值, 锟斤拷锟矫伙拷蟹锟斤拷锟斤拷蚍祷锟侥拷锟街�
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {

        Long ret= def;

        try{

          ClassLoader cl = context.getClassLoader();
          @SuppressWarnings("rawtypes")
              Class SystemProperties= cl.loadClass("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= long.class;  

          Method getLong = SystemProperties.getMethod("getLong", paramTypes);

          //锟斤拷锟斤拷
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new Long(def);

          ret= (Long) getLong.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 锟斤拷锟捷革拷锟斤拷锟斤拷key锟斤拷锟斤拷boolean锟斤拷锟斤拷值.
     * 锟斤拷锟街滴�'n', 'no', '0', 'false' or 'off' 锟斤拷锟斤拷false.
     * 锟斤拷锟街滴�y', 'yes', '1', 'true' or 'on' 锟斤拷锟斤拷true.
     * 锟斤拷锟絢ey锟斤拷锟斤拷锟斤拷, 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷值, 锟津返伙拷默锟斤拷值.
     * @param key 要锟斤拷询锟斤拷key
     * @param def 默锟较凤拷锟斤拷值
     * @return 锟斤拷锟斤拷一锟斤拷boolean锟斤拷锟酵碉拷值, 锟斤拷锟矫伙拷蟹锟斤拷锟斤拷蚍祷锟侥拷锟街�
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {

        Boolean ret= def;

        try{

          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = cl.loadClass("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= boolean.class;  

          Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

          //锟斤拷锟斤拷     
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new Boolean(def);

          ret= (Boolean) getBoolean.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 锟斤拷锟捷革拷锟斤拷锟斤拷key锟斤拷值锟斤拷锟斤拷锟斤拷锟斤拷, 锟矫凤拷锟斤拷锟斤拷要锟截讹拷锟斤拷权锟睫诧拷锟杰诧拷锟斤拷.
     * @throws IllegalArgumentException 锟斤拷锟絢ey锟斤拷锟斤拷32锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     * @throws IllegalArgumentException 锟斤拷锟絭alue锟斤拷锟斤拷92锟斤拷锟街凤拷锟斤拷锟阶筹拷锟斤拷锟届常
     */
    public static void set(Context context, String key, String val) throws IllegalArgumentException {

        try{

          @SuppressWarnings("unused")
          DexFile df = new DexFile(new File("/system/app/Settings.apk"));
          @SuppressWarnings("unused")
          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = Class.forName("android.os.SystemProperties");

          //锟斤拷锟斤拷锟斤拷锟斤拷
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= String.class;  

          Method set = SystemProperties.getMethod("set", paramTypes);

          //锟斤拷锟斤拷     
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new String(val);

          set.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            //TODO
        }

    }
}
