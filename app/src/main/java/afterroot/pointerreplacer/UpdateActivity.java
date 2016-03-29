/*
 * Copyright (C) 2016 Sandip Vaghela (AfterROOT)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afterroot.pointerreplacer;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static afterroot.pointerreplacer.Utils.getMimeType;
import static afterroot.pointerreplacer.Utils.showSnackbar;

public class UpdateActivity extends AppCompatActivity {
    TextView textCurrentVersion, textNewVersion, textWhatsNew, textNewChangelog;
    AppCompatButton buttonUpdate, buttonCheckUpdate;
    CardView cardView_update;
    LinearLayout layoutNoConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
    }

    @Override
    protected void onResume() {
        setup();
        super.onResume();
    }

    public void initialize(){
        findViews();
    }

    public void findViews(){
        cardView_update = (CardView) findViewById(R.id.cardView_update);
        textCurrentVersion = (TextView) findViewById(R.id.textCurrentVersion);
        textNewVersion = (TextView) findViewById(R.id.textNewVersion);
        textWhatsNew = (TextView) findViewById(R.id.textWhatsNew);
        textNewChangelog = (TextView) findViewById(R.id.textNewChangelog);

        buttonUpdate = (AppCompatButton) findViewById(R.id.buttonUpdate);
        buttonCheckUpdate = (AppCompatButton) findViewById(R.id.buttonCheckUpdate);
        layoutNoConnection = (LinearLayout) findViewById(R.id.layoutNoConnection);

        setup();
    }

    public void setup(){
        layoutNoConnection.setVisibility(View.GONE);
        buttonCheckUpdate.setVisibility(View.GONE);
        cardView_update.setVisibility(View.GONE);
        if (isNetworkAvailable()){
            buttonCheckUpdate.setVisibility(View.VISIBLE);
        } else {
            layoutNoConnection.setVisibility(View.VISIBLE);
        }
    }

    public void checkForUpdate(View view) {
        Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
        setupUpdater();
        buttonCheckUpdate.setVisibility(View.GONE);
        cardView_update.setVisibility(View.VISIBLE);
    }

    public void setupUpdater(){
        if (isNetworkAvailable()){
            try {
                textCurrentVersion.setText(String.format("Current Version: %s (%s)",
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));

                String latestVersionCode = dlString(UpdateURLs.URL_VERSION_CODE, false);
                int vercode = Integer.valueOf(latestVersionCode);

                final String newVersionName = dlString(UpdateURLs.URL_VERSION_NAME, false);
                textNewVersion.setText(String.format("Latest Version: %s (%s)", newVersionName, vercode));

                if (vercode > getPackageManager().getPackageInfo(getPackageName(), 0).versionCode){
                    buttonUpdate.setVisibility(View.VISIBLE);
                } else {
                    buttonUpdate.setEnabled(false);
                    buttonUpdate.setText("You already have latest version.");
                }

                String changelogVerCode = String.format("Changelog for v%s (%s)", newVersionName, vercode);
                String changelog = dlString(UpdateURLs.URL_CHANGELOG, true);
                textNewChangelog.setText(String.format("%s\n%s", changelogVerCode, changelog));

                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String apkName = "PR_"+newVersionName+".apk";
                        String apkURL = "https://raw.githubusercontent.com/sandipv22/pointer_replacer/master/updater/"+apkName;
                        final File downlaodedApk = new File(Environment.getExternalStorageDirectory()+"/Pointer Replacer/Downloads/"+apkName);
                        if (downlaodedApk.exists()){
                            showSnackbar(findViewById(R.id.main_layoutUpdate), apkName+" already exists.");
                        } else {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkURL));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setVisibleInDownloadsUi(true);
                            request.setDestinationInExternalPublicDir("/Pointer Replacer/Downloads/", apkName);
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            BroadcastReceiver onComplete = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    openFile(apkName, Uri.fromFile(downlaodedApk));
                                }
                            };
                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            showSnackbar(findViewById(R.id.main_layoutUpdate), "Downloading PR_"+ newVersionName+".apk");
                        }
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            showSnackbar(findViewById(R.id.main_layoutUpdate), "No Connection");
        }
    }
    private static String convertStreamToString(InputStream inputStream) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkWiFi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return networkWiFi != null && networkWiFi.isAvailable() && networkWiFi.isConnectedOrConnecting() ||
                networkMobile != null && networkMobile.isAvailable() && networkMobile.isConnectedOrConnecting();
    }

    public void openFile(String filname, Uri downloadedFile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(downloadedFile, getMimeType(filname));
        startActivity(intent);
    }

    public static String dlString(String url, boolean fetchLines) {
        String dS = "";
        try {
            StringAsync.setFetchLines(fetchLines);
            StringAsync stringAsync = new StringAsync ();
            dS = stringAsync.execute(new String[] {url}).get();
        } catch(Exception ex) {
            //
        }

        return dS;
    }

    public void retry(View view) {
        setup();
    }

    public static class StringAsync extends AsyncTask< String, Integer, String > {
        static boolean isFetchLines;

        public static void setFetchLines(boolean fetchLines){
            isFetchLines = fetchLines;
        }

        @Override
        protected String doInBackground(String... downloadURL) {
            URL url;
            String result  = null;
            InputStream is;
            try {
                url = new URL(downloadURL[0]);
                is = url.openStream();
                if (isFetchLines){
                    result = convertStreamToString(is);
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    result = br.readLine();
                    br.close();
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public class UpdateURLs {

        public static final String
                URL_GITHUB = "https://raw.githubusercontent.com/",
                URL_REPO = URL_GITHUB + "sandipv22/pointer_replacer/master/updater/",
                URL_VERSION_CODE = URL_REPO + "version_code.txt",
                URL_VERSION_NAME = URL_REPO + "version_name.txt",
                URL_CHANGELOG = URL_REPO + "changelog.txt";

    }
}
