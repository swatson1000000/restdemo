package com.stack.restdemo.resources;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateOTPTest {

    @Test
    void otpIsSixDigitsOfDigitsOnly() {
        GenerateOTP g = new GenerateOTP();
        for (int i = 0; i < 100; i++) {
            String otp = g.generateOtp();
            assertEquals(6, otp.length(), "OTP must be 6 chars");
            assertTrue(otp.chars().allMatch(Character::isDigit),
                    "OTP must contain digits only, got: " + otp);
        }
    }

    @Test
    void otpSequenceHasVariety() {
        GenerateOTP g = new GenerateOTP();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            seen.add(g.generateOtp());
        }
        // SecureRandom over 10^6 space should essentially never collide to <5 distinct
        // values across 200 draws; this guards against accidentally hard-coded output.
        assertTrue(seen.size() > 5,
                "expected varied OTPs across 200 draws, got " + seen.size() + " unique");
    }
}
