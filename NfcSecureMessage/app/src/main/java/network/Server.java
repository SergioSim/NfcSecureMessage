package network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import utils.Logging;

public class Server {

    private static final String TAG = Logging.getTAG(Server.class);
    private static final String SERVER = "http://82.255.166.104:8083/api/";

    public Server() {}

    //client.setRequestProperty("msg", message); //this is for the header...
    //client.setFixedLengthStreamingMode(outputPost.getBytes().length);
    //client.setChunkedStreamingMode(0);

    //as this method performes blocking operations
    //it's called in a async task
    public String postRequest(Address address, String message, boolean isFormated) {
        HttpURLConnection aHttpURLConnection = null;
        BufferedReader br = null;
        OutputStream os = null;
        BufferedWriter writer = null;
        String response = "";
        try {
            aHttpURLConnection = getHttpURLConnection(address);
            aHttpURLConnection.setRequestMethod("POST");
            aHttpURLConnection.setRequestProperty("content-type", "application/json");
            aHttpURLConnection.setDoInput(true);
            aHttpURLConnection.setDoOutput(true);
            os = aHttpURLConnection.getOutputStream();
            writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            if(isFormated) {
                writer.write(message);
            }else{
                writer.write(formatMessage(message));
            }
            writer.flush();
            int responseCode = aHttpURLConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                br = new BufferedReader(new InputStreamReader(aHttpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
                Log.i(TAG, "responseCode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception: " + e.getMessage());
        } finally {
            closeStreams(aHttpURLConnection, null, br, os, writer);
        }
        Log.i(TAG, "response form server: " + response);
        return response;
    }

    private String formatMessage(String message) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        result.append(URLEncoder.encode("msg", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(message, "UTF-8"));
        return result.toString();
    }

    public String getRequest(Address address) {
        HttpURLConnection aHttpURLConnection = null;
        BufferedReader bufReader = null;
        InputStreamReader isReader = null;
        try {
            aHttpURLConnection = getHttpURLConnection(address);
            aHttpURLConnection.setRequestMethod("GET");
            InputStream inputStream = aHttpURLConnection.getInputStream();
            isReader = new InputStreamReader(inputStream);
            bufReader = new BufferedReader(isReader);
            String line = bufReader.readLine();
            StringBuffer readTextBuf = new StringBuffer();
            while(line != null) {
                readTextBuf.append(line);
                line = bufReader.readLine();
            }
            Log.i(TAG, "response form server: " + readTextBuf.toString());
            return readTextBuf.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.i(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException: " + e.getMessage());
        } finally {
            closeStreams(aHttpURLConnection, isReader, bufReader, null, null);
        }
        return null;
    }

    private HttpURLConnection getHttpURLConnection(Address address) throws IOException {
        URL aURL = new URL(SERVER + address.toString());
        HttpURLConnection aHttpURLConnection = (HttpURLConnection) aURL.openConnection();
        aHttpURLConnection.setConnectTimeout(15000);
        aHttpURLConnection.setReadTimeout(15000);
        return aHttpURLConnection;
    }

    private void closeStreams(HttpURLConnection httpConn, InputStreamReader isReader , BufferedReader bufReader, OutputStream os , BufferedWriter writer){
        try {
            if (bufReader != null) {
                bufReader.close();
            }
            if (isReader != null) {
                isReader.close();
            }
            if (httpConn != null) {
                httpConn.disconnect();
            }
            if (os != null) {
                os.close();
            }
            if (writer != null) {
                writer.close();
            }
        }catch (IOException ex) {
            Log.wtf(TAG, "Very Bad exception: " + ex.getMessage(), ex);
        }
    }
}