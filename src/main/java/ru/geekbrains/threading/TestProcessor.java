package ru.geekbrains.threading;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestProcessor {

    /**
     * Данный метод находит все void методы без аргументов в классе, и запускеет их.
     * <p>
     * Для запуска создается тестовый объект с помощью конструткора без аргументов.
     */
    public static void runTest(Class<?> testClass) {
        final Object testObj = getObject(testClass);

        List<Method> methods = new ArrayList<>();
        Method afteEachMethod = null;
        Method beforeEachMethod = null;

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterEach.class)) {
                afteEachMethod = method;
            }
            if (method.isAnnotationPresent(BeforeEach.class)) {
                beforeEachMethod = method;
            }
        }

        for (Method method : testClass.getDeclaredMethods()) {
            checkTestMethod(method);
            if (method.isAnnotationPresent(Skip.class)) {
                continue;
            }

            if (method.isAnnotationPresent(Test.class)) {
                methods.add(beforeEachMethod);
                methods.add(method);
                methods.add(afteEachMethod);
            }
        }

        sortOrderTests(methods);

        methods.forEach(it -> runTest(it, testObj));
    }

    private static Object getObject(Class<?> testClass) {
        final Constructor<?> declaredConstructor;
        try {
            declaredConstructor = testClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Для класса \"" + testClass.getName() + "\" не найден конструктор без аргументов");
        }

        final Object testObj;
        try {
            testObj = declaredConstructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект класса \"" + testClass.getName() + "\"");
        }
        return testObj;
    }

    private static void sortOrderTests(List<Method> methods) {
        methods.sort(((o1, o2) -> {
            if (o1.isAnnotationPresent(Test.class) && o2.isAnnotationPresent(Test.class)) {
                return o1.getAnnotation(Test.class).order() - o2.getAnnotation(Test.class).order();
            }

            return 0;
        }));
    }

    private static void checkTestMethod(Method method) {
        if (!method.getReturnType().isAssignableFrom(void.class) || method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Метод \"" + method.getName() + "\" должен быть void и не иметь аргументов");
        }
    }

    private static void runTest(Method testMethod, Object testObj) {
        try {
            testMethod.invoke(testObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось запустить тестовый метод \"" + testMethod.getName() + "\"");
        }
    }

}

