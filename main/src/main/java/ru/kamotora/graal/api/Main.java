package ru.kamotora.graal.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;

@Slf4j
public class Main {

    public static final int BIG_ARRAY_SIZE = 1_000_000;

    public static void main(String[] args) {
        NativeApi.add(5, 5);
        NativeApi.print("kamotora@yandex.ru");
//        reverseBytesTest();
//        randomBytes();
        NativeApi.generateRandomPoint();
    }

    private static void randomBytes() {
        var randomArray = NativeApi.generateRandomArray();
        log.info("Returned array");
        logArray(randomArray);
    }

    private static void reverseBytesTest() {
        NativeApi.reverseBytes(new byte[]{10, 20, 30, 40});
        var bigArray = RandomUtils.nextBytes(1_000_000);
        var reversedBigArray = NativeApi.reverseBytes(bigArray);
        ArrayUtils.reverse(reversedBigArray);
        assert Arrays.equals(reversedBigArray, bigArray);
    }

    private static void logArray(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            log.info("Byte[{}]: {}", i, (int) bytes[i]);
        }
    }

}
