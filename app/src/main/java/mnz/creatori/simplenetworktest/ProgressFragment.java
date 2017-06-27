package mnz.creatori.simplenetworktest;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.src;
import static java.net.URLDecoder.decode;

public class ProgressFragment extends Fragment {

    TextView contentView;
    String contentText = null;
    WebView webView;

    final private String TAG = "Mylogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        contentView = (TextView) view.findViewById(R.id.content);
        webView = (WebView) view.findViewById(R.id.webView);

        // если данные ранее были загружены
        if (contentText != null) {
            contentView.setText(contentText);
            webView.loadData(contentText, "text/html; charset=utf-8", "utf-8");
        }

        Button btnFetch = (Button) view.findViewById(R.id.downloadBtn);
        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contentText == null) {
                    contentView.setText("Загрузка...");
//                    new ProgressTask().execute("https://developer.android.com/index.html");
                    new ProgressTask().execute("http://www.cbr.ru/scripts/XML_daily.asp");
                }
            }
        });
        return view;
    }

    private class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                content = getContent(path[0]);
            } catch (IOException ex) {
                content = ex.getMessage();
            }

            return content;
        }

        @Override
        protected void onPostExecute(String content) {

            contentText = content;
            contentView.setText(content);
            webView.loadData(content, "text/xml; charset=utf-8", "utf-8");
            Toast.makeText(getActivity(), "Данные загружены", Toast.LENGTH_SHORT)
                    .show();
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                URL url = new URL(path);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept-Charset", "UTF-8");
                c.setReadTimeout(30000);

                c.connect();


                String temp = c.getContentEncoding();
                Log.d(TAG, "Encoding: " + temp);


                String contentType = c.getHeaderField("Content-Type");
                Log.d(TAG, "Content type: " + contentType);


                reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "windows-1251"));
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }

//                Scanner s = new Scanner(c.getInputStream(), "windows-1251").useDelimiter("\\A");
//                String answer = s.hasNext() ? s.next() : "";

                String answer = buf.toString();


                Log.d(TAG, "String get: " + answer);
                Log.d(TAG, "Длина ответа: " + answer.length());
                return (answer);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }
}
