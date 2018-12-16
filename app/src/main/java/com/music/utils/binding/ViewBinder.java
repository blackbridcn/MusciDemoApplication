package com.music.utils.binding;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewBinder {

    public static void bind(Activity activity) {
        bind(activity, activity.getWindow().getDecorView());
    }

    public static void bind(Object target, View source) {
        bindField(target, source);
        bindOnClickEvent(target, source);
    }

    private static void bindField(Object target, View source) {
        Field[] fields = target.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    if (field.get(target) != null) {
                        continue;
                    }
                    field.setAccessible(true);
                    Bind bind = field.getAnnotation(Bind.class);
                    if (bind != null) {
                        int viewId = bind.value();

                        field.set(target, source.findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void bindOnClickEvent(Object target, View source) {
        Method[] methods = target.getClass().getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                OnClick onClick = method.getAnnotation(OnClick.class);
                if (onClick != null) {
                    int[] value = onClick.value();
                    method.setAccessible(true);
                    /* Object proxyInstance = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(),
                             new Class[]{View.OnClickListener.class.getClass()},
                             new InvocationHandler() {
                                 @Override
                                 public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                     return method.invoke(target, args);
                                 }
                             });*/

                     for(int id:value){
                          View targetView = source.findViewById(id);
                         targetView.setOnClickListener((view -> {
                             try {
                                 method.invoke(target,targetView);
                             } catch (IllegalAccessException e) {
                                 e.printStackTrace();
                             } catch (InvocationTargetException e) {
                                 e.printStackTrace();
                             }
                         }));

                     }

                }
            }
        }
    }

    private static InvocationHandler invocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(proxy, args);
        }
    };
}
