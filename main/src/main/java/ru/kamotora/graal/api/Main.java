package ru.kamotora.graal.api;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;

public class Main {

    public static final int BIG_ARRAY_SIZE = 1_000_000;

    public static void main(String[] args) {
        NativeApi.add(5, 5);
        NativeApi.print("kamotora@yandex.ru");
        NativeApi.reverseBytes(new byte[]{10, 20, 30, 40});
        var bigArray = RandomUtils.nextBytes(1_000_000);
        var reversedBigArray = NativeApi.reverseBytes(bigArray);
        ArrayUtils.reverse(reversedBigArray);
        assert Arrays.equals(reversedBigArray, bigArray);
    }
}
