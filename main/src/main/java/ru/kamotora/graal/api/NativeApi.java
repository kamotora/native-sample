package ru.kamotora.graal.api;

import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/foreign/package-summary.html
 *
 * <a href="https://graalvm.slack.com/archives/CN9KSFB40/p1629811417043700">Чатик по graal</a>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NativeApi {

    static {
        System.load(System.getProperty("user.dir") + "/lib/target/lib.so");
    }

    /**
     * Сложить 2 числа
     * @param a
     * @param b
     */
    public static void add(int a, int b) {
        var isolateThreadId = createIsolate();
        add(isolateThreadId, a, b);
    }

    /**
     * Вывести в лог строку (email)
     * @param email
     */
    public static void print(String email) {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        var cStringPointer = NativeUtils.toCStringPointer(email);
        print(isolateThreadId, cStringPointer);
    }

    /**
     * Получить "перевернутый" массив
     * @param bytes
     * @return
     */
    // todo проверить на утечку памяти
    public static byte[] reverseBytes(byte[] bytes) {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        var bytesSegment = NativeUtils.toBytesSegment(bytes);
        var bytesPointer = NativeUtils.address(bytesSegment);
        var reversedBytesPointer = testBytes(isolateThreadId, bytesPointer, bytes.length);
        return NativeUtils.fromBytesPointer(reversedBytesPointer, bytes.length);
    }

    // todo проверить на утечку памяти
    public static byte[] generateRandomArray() {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        try (var sharedScope = ResourceScope.newSharedScope()) {
            var segment = SegmentAllocator.ofScope(sharedScope).allocate(MemoryLayouts.JAVA_INT);
            var resultSizePointer = NativeUtils.address(segment);
            var randomByteArrayPointer = randomByteArray(isolateThreadId, resultSizePointer);
            var resultSize = segment.toIntArray()[0];
            assert resultSize > 0 && resultSize <= 5;
            return NativeUtils.fromBytesPointer(randomByteArrayPointer, resultSize);
        }
    }

    public static void generateRandomPoint() {
        int x = RandomUtils.nextInt();
        int y = RandomUtils.nextInt();
        log.debug("call method 'createPoint' with x: {}, y: {}", x, y);
        var pointPointer = createPoint(createIsolate(), x, y);
        // По факту, копируем point
        var point = NativeUtils.fromObjectPointer(pointPointer, Point.NATIVE_LAYOUT, Point::read);
        log.debug("created point with x: {}, y: {}", point.x(), point.y());
        // после выхода из try прочитанный point удалится, т.к. sharedScope
    }

    public static void getErrorTest() {
        var isolate = createIsolate();
        var errorResponsePtr = getErrorText(isolate, true);
        var errorResponse = NativeUtils.fromObjectPointer(errorResponsePtr, Response.NATIVE_LAYOUT, Response::read);
        log.debug("First response: code: {}, error text: {}, resultPtr is null: {}", errorResponse.code(),
                errorResponse.errorMessage(), NativeUtils.isNull(errorResponse.resultPtr()));
        assert errorResponse.code() != 0;
        assert StringUtils.isNotBlank(errorResponse.errorMessage());

        var noErrorResponsePtr = getErrorText(isolate, false);
        var noErrorResponse = NativeUtils.fromObjectPointer(noErrorResponsePtr, Response.NATIVE_LAYOUT, Response::read);
        assert errorResponse.code() == 0;
        log.debug("Second response: code: {}, error text: {}, resultPtr is null: {}", noErrorResponse.code(),
                noErrorResponse.errorMessage(), NativeUtils.isNull(noErrorResponse.resultPtr()));
        var result = NativeUtils.fromCStringPointer(noErrorResponse.resultPtr());
        log.debug("Second response result: {}", result);
        assert StringUtils.isNotBlank(result);
    }

    private static native int print(long isolateThreadId, long emailPointer);

    private static native long testBytes(long isolateThreadId, long bytesArrayPointer, int size);

    private static native long randomByteArray(long isolateThreadId, long resultSizePointer);

    private static native int add(long isolateThreadId, int a, int b);

    private static native long createPoint(long isolateThreadId, int x, int y);

    private static native long getErrorText(long isolateThreadId, boolean needError);

    private static native long createIsolate();
}
