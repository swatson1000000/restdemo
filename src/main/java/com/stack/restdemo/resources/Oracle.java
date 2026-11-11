package com.stack.restdemo.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;

@Path("oracle")
public class Oracle {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response fetchOracle() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(jdkDefaultSslContext())
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://docs.oracle.com/en/"))
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "identity")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Sec-Fetch-User", "?1")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Response.status(response.statusCode()).entity(response.body()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Failed to fetch docs.oracle.com: " + e.getMessage())
                    .build();
        }
    }

    // GlassFish overrides javax.net.ssl.trustStore with its own domain trust store,
    // which lacks public CA roots. Build an SSLContext from the JDK's default cacerts
    // so this handler can validate public HTTPS endpoints.
    private static SSLContext jdkDefaultSslContext() throws Exception {
        java.nio.file.Path cacerts = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
        KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream in = Files.newInputStream(cacerts)) {
            ks.load(in, "changeit".toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), new SecureRandom());
        return ctx;
    }
}
