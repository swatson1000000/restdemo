package com.stack.restdemo.resources;
 
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
 
import java.security.SecureRandom;
 
@Path("generateotp")
public class GenerateOTP {
 
    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private SecureRandom random = new SecureRandom();
 
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        return otp.toString();
    }
 
}
