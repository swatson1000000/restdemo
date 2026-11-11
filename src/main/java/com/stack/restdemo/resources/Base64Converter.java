package com.stack.restdemo.resources;
 
import java.util.Base64;
 
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
 
@Path("base64converter")
public class Base64Converter {
     
    /**
     * Converts a given string to its Base64 encoded format.
     *
     * @param input the string to be encoded
     * @return the Base64 encoded string
     */
    @GET
    @Path("encode/{data}")
    @Produces(MediaType.TEXT_PLAIN)
    public String toBase64(@PathParam("data") String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
 
    /**
     * Converts a given Base64 encoded string back to its original format.
     *
     * @param base64 the Base64 encoded string
     * @return the original string
     */
    @GET
    @Path("decode/{base64}")
    @Produces(MediaType.TEXT_PLAIN)
    public String fromBase64(@PathParam("base64") String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }
}
