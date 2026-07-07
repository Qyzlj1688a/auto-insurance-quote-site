package com.example.quote;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteApplicationTests {

    @Test
    void applicationClassNameIsStable() {
        assertEquals("QuoteApplication", QuoteApplication.class.getSimpleName());
    }
}
