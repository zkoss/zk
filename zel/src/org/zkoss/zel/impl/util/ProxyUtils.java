package org.zkoss.zel.impl.util;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ProxyUtils {

  /** The CGLIB class separator character "$$" */
  public static final String CGLIB_CLASS_SEPARATOR = "$$";

  private static List<String> AOP_TYPE_NAME = initAopTypeNameList();

  private static Map<Class<?>, Class<?>> cache = new HashMap<Class<?>, Class<?>>();
  
  private static final String ORG_SPRINGFRAMEWORK_AOP_SPRING_PROXY = "org.springframework.aop.SpringProxy";

  public static Class<?> getTargetClass(Object viewModel) {

    Class<?> result = null;

    Class<?> vmClass = viewModel.getClass();

    if (cache.containsKey(vmClass)) {

      result = cache.get(vmClass);

    } else {

      result = findTargetClass(viewModel);
      cache.put(vmClass, result);

    }

    return result;

  }

  /**
   * Check whether the specified class is a CGLIB-generated class.
   * 
   * @param clazz the class to check
   */
  public static boolean isCglibProxyClass(Class<?> clazz) {
    return clazz != null && isCglibProxyClassName(clazz.getName());
  }

  /**
   * Check whether the specified class name is a CGLIB-generated class.
   * 
   * @param className the class name to check
   */
  public static boolean isCglibProxyClassName(String className) {
    return className != null && className.contains(CGLIB_CLASS_SEPARATOR);
  }
  
  public static boolean isSpringJdkDynamicProxy(Class<?> vmClass){
    boolean isSpringProxy = false;
    Class<?>[] classes = vmClass.getInterfaces();
    for (Class<?> clazz : classes) {
      
      String typeName = clazz.getName();
      
      if (ORG_SPRINGFRAMEWORK_AOP_SPRING_PROXY.equals(typeName)) {
        isSpringProxy = true;
        break;
      }
    }
    
    return isSpringProxy && Proxy.isProxyClass(vmClass);
  }

  private static Class<?> findTargetClass(Object viewModel) {

    Class<?> vmClass = viewModel.getClass();
    Class<?> result = vmClass;

    try {

      boolean isAopType = false;
      boolean isSpringProxy = false;

      Class<?>[] classes = vmClass.getInterfaces();
      for (Class<?> clazz : classes) {

        String typeName = clazz.getName();

        if (ORG_SPRINGFRAMEWORK_AOP_SPRING_PROXY.equals(typeName)) {
          isSpringProxy = true;
        }

        if (AOP_TYPE_NAME.contains(typeName)) {
          isAopType = true;
        }

        if (isSpringProxy && isAopType) {
          break;
        }

      }

      if (isAopType) {

        Method[] methods = vmClass.getDeclaredMethods();
        for (Method method : methods) {

          if ("getTargetClass".equals(method.getName())) {
            result = (Class<?>) method.invoke(viewModel);
            break;
          }

        }

        if (result == null && isSpringProxy && isCglibProxyClass(vmClass)) {
          result = vmClass.getSuperclass();
        }

      }

    } catch (ReflectiveOperationException e) {

      // Silent error

    }

    return result;
  }

  private static synchronized List<String> initAopTypeNameList() {
    AOP_TYPE_NAME = new ArrayList<String>();
    AOP_TYPE_NAME.add("org.springframework.aop.TargetClassAware");
    AOP_TYPE_NAME.add("org.springframework.aop.framework.Advised");
    AOP_TYPE_NAME.add("org.springframework.aop.TargetSource");
    AOP_TYPE_NAME.add(ORG_SPRINGFRAMEWORK_AOP_SPRING_PROXY);
    AOP_TYPE_NAME.add("org.springframework.cglib.proxy.Factory");

    return AOP_TYPE_NAME;
  }

  private ProxyUtils() {
  }

}
