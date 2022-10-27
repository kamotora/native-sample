package ru.kamotora.graal.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

@Slf4j
public class NativeImpl {


    @CEntryPoint(name = "Java_ru_kamotora_graal_api_NativeApi_test")
    public static void test(Pointer jniEnv, Pointer clazz,
                            @CEntryPoint.IsolateThreadContext long isolateId,
                            CCharPointer pEmail) {
        //Convert C *char to Java String
        String email = CTypeConversion.toJavaString(pEmail);
        log.info("Email: {}", email);
        logArray(email.getBytes());
    }

    private static void logArray(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            log.info("Byte[{}]: {}", i, (int) bytes[i]);
        }
    }

    @CEntryPoint(name = "Java_ru_kamotora_graal_api_NativeApi_testBytes")
    public static CCharPointer testBytes(Pointer jniEnv, Pointer clazz,
                                         @CEntryPoint.IsolateThreadContext long isolateId,
                                         CCharPointer pBytes, int size) {
        var bytesBuffer = CTypeConversion.asByteBuffer(pBytes, size);
        var bytes = new byte[size];
        bytesBuffer.get(bytes);
        logArray(bytes);
        log.info("Reversed");
        ArrayUtils.reverse(bytes);
        logArray(bytes);
        try (var bufferHandler = CTypeConversion.toCBytes(bytes)) {
            return bufferHandler.get();
        }
    }

//    @CEntryPoint(name = "Java_ru_kamotora_graal_api_NativeApi_test")
//    public static void testObject(JNIEnvironment jniEnv, Pointer clazz,
//                                  @CEntryPoint.IsolateThreadContext long isolateId,
//                                  JObject applicationContext) {
//        //Convert C *char to Java String
//        PinnedObject.create()
//        String email = CTypeConversion.toJavaString(pEmail);
//        log.info("Email: {}", email);
//        for (int i = 0; i < email.length(); i++) {
//            log.info("Byte[{}]: {}", i, (int) email.getBytes()[i]);
//        }
//    }

    @CEntryPoint(name = "Java_ru_kamotora_graal_api_NativeApi_add")
    public static void add(Pointer jniEnv, Pointer clazz,
                           @CEntryPoint.IsolateThreadContext long isolateId,
                           int a, int b) {
        System.out.println(a + b);
        log.info("Add result: {}", a + b);
    }

    @CEntryPoint(name = "Java_ru_kamotora_graal_api_NativeApi_createIsolate",
                 builtin = CEntryPoint.Builtin.CREATE_ISOLATE)
    public static native IsolateThread createIsolate();
}