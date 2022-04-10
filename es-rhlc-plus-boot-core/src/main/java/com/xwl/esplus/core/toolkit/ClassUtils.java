package com.xwl.esplus.core.toolkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取接口的所有实现类 理论上也可以用来获取类的所有子类
 * 查询路径有限制，只局限于接口所在模块下，比如pandora-gateway,而非整个pandora（会递归搜索该文件夹下所有的实现类）
 * 路径中不可含中文，否则会异常。若要支持中文路径，需对该模块代码中url.getPath() 返回值进行urldecode.
 *
 * @author xwl
 * @since 2022/2/18 21:58
 */
public class ClassUtils {
    /**
     * 获取某个接口的所有实现类
     * .isAssignableFrom()方法与 instanceof 关键字的区别总结为以下两个点：
     * isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
     * isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
     * 使用方法：
     * 父类.class.isAssignableFrom(子类.class)
     * 子类实例 instanceof 父类类型
     *
     * @param clazz class
     * @return list
     */
    public static List<Class> getAllClassByInterface(Class clazz) {
        List<Class> list = new ArrayList<>();
        // 判断是否是一个接口
        if (clazz.isInterface()) {
            ArrayList<Class> allClass = getAllClassByPackageName(clazz.getPackage().getName());
            // 循环判断路径下的所有类是否实现了指定的接口 并且排除接口类自己
            for (int i = 0; i < allClass.size(); i++) {
                // 判断是不是同一个接口
                // isAssignableFrom:判定此 Class 对象所表示的类或接口与指定的 Class
                // 参数所表示的类或接口是否相同，或是否是其超类或超接口
                if (clazz.isAssignableFrom(allClass.get(i))) {
                    if (!clazz.equals(allClass.get(i))) {
                        // 自身并不加进去
                        list.add(allClass.get(i));
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查找指定包名下的所有类
     *
     * @param packageName 包名
     * @return Class集合
     */
    public static ArrayList<Class> getAllClassByPackageName(String packageName) {
        List<String> classNameList = getClassNameByPackageName(packageName);
        ArrayList<Class> list = new ArrayList<>();
        for (String className : classNameList) {
            try {
                list.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("load class from name failed:" + className + e.getMessage());
            }
        }
        return list;
    }

    /**
     * 查找指定包名下的所有类，返回全类名集合
     *
     * @param packageName 包名
     * @return 全类名集合
     */
    public static List<String> getClassNameByPackageName(String packageName) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                String fileSearchPath = url.getPath();
                fileSearchPath = fileSearchPath.substring(0, fileSearchPath.indexOf("/classes"));
                fileNames = getClassNameByFile(fileSearchPath);
            } else if (type.equals("jar")) {
                try {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    fileNames = getClassNameByJar(jarFile, packagePath);
                } catch (IOException e) {
                    throw new RuntimeException("open Package URL failed：" + e.getMessage(), e);
                }
            } else {
                throw new RuntimeException("file system not support! cannot load MsgProcessor！");
            }
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                myClassName.addAll(getClassNameByFile(childFile.getPath()));
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }
        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJar(JarFile jarFile, String packagePath) {
        List<String> myClassName = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                myClassName.add(entryName);
            }
        }
        return myClassName;
    }

    /**
     * 获取某个类中某个字段的值
     *
     * @param object    实体对象
     * @param fieldName 字段名
     * @return
     */
    public static Object getValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setValue(T t, String fieldName, Object value) {
        try {
            Class<T> tClass = (Class<T>) t.getClass();
            Field declaredField = tClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(t, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}