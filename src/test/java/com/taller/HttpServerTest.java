package com.taller;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit test for simple web server.
 */
public class HttpServerTest {

    @Test
    public void testHelloEndpoint() throws Exception {
        URL url = new URL("http://localhost:35000/hello?name=test");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int code = con.getResponseCode();
        assertEquals(200, code);
    }

    @Test
    public void test404page() throws Exception {
        URL url = new URL("http://localhost:35000/test.html");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int code = con.getResponseCode();
        assertEquals(404, code);
    }

    @Test
    public void testStaticCss() throws Exception {
        URL url = new URL("http://localhost:35000/styles.css");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int code = con.getResponseCode();
        assertEquals(200, code);
        assertEquals("text/css", con.getContentType());
    }

}
