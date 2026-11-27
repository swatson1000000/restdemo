package com.stack.restdemo.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64ConverterTest {

    private final Base64Converter c = new Base64Converter();

    @Test
    void encodesAsciiToKnownBase64() {
        assertEquals("aGVsbG8=", c.toBase64("hello"));
    }

    @Test
    void decodesKnownBase64ToAscii() {
        assertEquals("hello", c.fromBase64("aGVsbG8="));
    }

    @Test
    void encodeDecodeRoundTripsAcrossSamples() {
        String[] samples = {
                "",
                "a",
                "ab",
                "abc",
                "Jakarta EE 10",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "symbols !@#$%^&*()_+-={}[]:;\"'<>,.?/|\\",
        };
        for (String s : samples) {
            assertEquals(s, c.fromBase64(c.toBase64(s)), "round trip failed for: " + s);
        }
    }
}
