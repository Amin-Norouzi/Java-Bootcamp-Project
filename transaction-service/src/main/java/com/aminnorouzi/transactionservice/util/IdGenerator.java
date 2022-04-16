package com.aminnorouzi.transactionservice.util;

public class IdGenerator {

    private static final Long LIMIT = 10000000000L;
    private static Long last = 0L;

    // returns 10 digits id
    public static Long get() {
        long id = System.currentTimeMillis() % LIMIT;
        if (id <= last) {
            id = (last + 1) % LIMIT;
        }

        last = id;
        return id;
    }
}
