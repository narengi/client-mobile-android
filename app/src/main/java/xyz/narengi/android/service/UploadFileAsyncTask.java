package xyz.narengi.android.service;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sepehr Behroozi on 11/22/16.
 */

public class UploadFileAsyncTask extends AsyncTask<Void, Integer, String> {
    private static final String CRLF = "\r\n";
    private static final String TWOHYPHENS = "--";
    private static final String BOUNDARY = "***************";
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private static final int FILE_UPLOAD_BUFFER_SIZE = 4096;

    private OnUpdateListener responseHandler;
    private String token;
    private List<String> filesList;
    private String url;
    private String requestMethod;
    private String fileKey;

    public UploadFileAsyncTask(String url, String requestMethod, String token) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.token = token;
    }

    public UploadFileAsyncTask(OnUpdateListener responseHandler) {
        this.responseHandler = responseHandler;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFilesList(List<String> filesList) {
        this.filesList = filesList;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (responseHandler != null)
            responseHandler.onPre();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            if (filesList == null || filesList.size() == 0)
                return SUCCESS;

            //calculate total size
            int totalUploadSize = 0;
            for (String filePath : filesList) {
                File file = new File(filePath);
                if (file.exists())
                    totalUploadSize += file.length();
            }


            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5);
            connection.setReadTimeout(15);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod.toUpperCase(Locale.ENGLISH));

            //set headers
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);


            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(TWOHYPHENS + BOUNDARY + CRLF);

            for (int i = 0; i < filesList.size(); i++) {
                String filePath = filesList.get(i);
                File uploadFile = new File(filePath);
                if (!uploadFile.exists())
                    continue;


                InputStream in = new FileInputStream(uploadFile);
                DataInputStream stream = new DataInputStream(in);
                String attachmentName = uploadFile.getName();
                writer.writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\";filename=\"" + attachmentName + "\"" + CRLF);
                writer.writeBytes(CRLF);

                long read = 0;
                int readStatus = 0;
                while (readStatus != -1) {
                    byte[] readedChars = new byte[FILE_UPLOAD_BUFFER_SIZE];
                    readStatus = stream.read(readedChars, 0, readedChars.length);
                    read += readStatus;

                    writer.write(readedChars);

                    double progress = ((double) read / (double) totalUploadSize) * 100;
                    publishProgress((int) progress);
                }

                writer.writeBytes(CRLF);
                writer.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + CRLF);
            }

            writer.flush();
            writer.close();

            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";

            while ((line = reader.readLine()) != null) {
                result += line;
            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (responseHandler != null)
            responseHandler.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // TODO: 11/22/16 publish response
    }


    public interface OnUpdateListener {
        void onPre();

        void onPost(Object response);

        void onProgressUpdate(int porgress);
    }
}
