package ru.kamotora.graal.spring.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringNativeApi {

    static {
        System.load(System.getProperty("user.dir") + "/lib-spring/target/lib.so");
    }

    public static void localesTest() {
        localeTest(new Locale("RU", "ru"));
        localeTest(Locale.ENGLISH);
    }

    private static void localeTest(Locale locale) {
        LocaleContextHolder.setLocale(locale);
        log.debug("Current locale (spring): {}", LocaleContextHolder.getLocale());
        printLocale(createIsolate());
    }

    private static native void printLocale(long isolateThreadId);

    private static native long createIsolate();
}
