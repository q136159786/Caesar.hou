package com.cloudwise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpPostTest {  
    void testPost(String urlStr) {  
        try {  
            URL url = new URL(urlStr);  
            URLConnection con = url.openConnection();  
            con.setDoOutput(true);  
            con.setRequestProperty("Pragma:", "no-cache");  
            con.setRequestProperty("Cache-Control", "no-cache");  
            con.setRequestProperty("Content-Type", "text/xml");  
  
            OutputStreamWriter out = new OutputStreamWriter(con  
                    .getOutputStream());      
            String xmlInfo = getXmlInfo();  
            System.out.println("urlStr=" + urlStr);  
            System.out.println("rumData=" + xmlInfo);  
            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));  
            out.flush();  
            out.close();  
            BufferedReader br = new BufferedReader(new InputStreamReader(con  
                    .getInputStream()));  
            String line = "";  
            for (line = br.readLine(); line != null; line = br.readLine()) {  
                System.out.println(line);  
            }  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    private String getXmlInfo() throws IOException {  
        StringBuilder sb = new StringBuilder();  
        String fileinfo=new ReadFile().readFileContent("/Users/houshengliang/Documents/ajax_aaa.txt");
        sb.append(fileinfo);  

        return sb.toString();  
    }  
  
    public static void main(String[] args) {  
        String url = "http://localhost:6577/ajaxApi";  
        new HttpPostTest().testPost(url);  
    }  
}  
