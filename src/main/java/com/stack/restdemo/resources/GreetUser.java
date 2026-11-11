package com.stack.restdemo.resources;
 
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
 
@Path("users/{username}")
public class GreetUser {
 
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String greetUser(@PathParam("username") String userName) {
        return "<html lang=\\\"en\\\"><body><h1>Hello, " + userName + "!!</h1></body></html>";
    }
     
}
