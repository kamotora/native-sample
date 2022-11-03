package ru.kamotora.graal.spring.impl;

import com.oracle.svm.core.jni.headers.JNIEnvironment;
import com.oracle.svm.core.jni.headers.JNIMethodId;
import com.oracle.svm.core.jni.headers.JNIObjectHandle;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.Pointer;
import org.graalvm.word.WordFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import static org.graalvm.nativeimage.c.type.CTypeConversion.toCString;

@Slf4j
public class SpringNativeImpl {
    @CEntryPoint(name = "Java_ru_kamotora_graal_spring_api_SpringNativeApi_printLocale")
    public static void printLocale(JNIEnvironment jniEnv, Pointer clazz,
                                   @CEntryPoint.IsolateThreadContext long isolateId) {
        log.debug("Current local (native): {}", LocaleContextHolder.getLocale());
    }

    @CEntryPoint(name = "Java_ru_kamotora_graal_spring_impl_SpringNativeApi_test")
    public static void print(JNIEnvironment jniEnv, Pointer clazz,
                             @CEntryPoint.IsolateThreadContext long isolateId,
                             CCharPointer pEmail) {
        var fn = jniEnv.getFunctions();
        JNIObjectHandle configClass = fn
                .getFindClass()
                .invoke(jniEnv, toCString("ru/kamotora/graal/spring/api/SimpleSpringConfig").get());
        assert configClass != null;
        JNIMethodId methodId = fn.getGetStaticMethodID()
                .invoke(jniEnv, configClass,
                        toCString("getApplicationContext").get(),
                        toCString("()Lorg/springframework/context/ApplicationContext;").get());
        assert methodId != null && methodId.isNonNull();
        final JNIObjectHandle handle = fn.getCallStaticObjectMethodA()
                .invoke(jniEnv, configClass, methodId, WordFactory.nullPointer());
        assert handle != null;
    }

    @CEntryPoint(name = "Java_ru_kamotora_graal_spring_api_SpringNativeApi_createIsolate",
                 builtin = CEntryPoint.Builtin.CREATE_ISOLATE)
    public static native IsolateThread createIsolate();

}
