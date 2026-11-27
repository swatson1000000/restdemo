package com.stack.restdemo.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GreetUserTest {

    private final GreetUser greet = new GreetUser();

    @Test
    void greetingContainsUsername() {
        String html = greet.greetUser("Steve");
        assertTrue(html.contains("Hello, Steve!!"),
                "expected greeting for 'Steve' in: " + html);
    }

    @Test
    void outputIsWrappedInHtmlAndBody() {
        String html = greet.greetUser("x");
        assertTrue(html.startsWith("<html"), "expected <html prefix: " + html);
        assertTrue(html.contains("<body>"), "expected <body> element: " + html);
        assertTrue(html.endsWith("</body></html>"), "expected </body></html> suffix: " + html);
    }
}
