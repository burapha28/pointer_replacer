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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class Utils {
    static String POINTER_SAVE_DIR;
    static String POINTER_FOLDER_PATH;

    /**

     * @param context the context
     * @return Pointer folder directory
     */
    public static String getPointerFolderPath(Context context){
        return POINTER_FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + context.getString(R.string.pointerFolderName);
    }

    @Override
    public String toString() {
        POINTER_SAVE_DIR = Environment.getDataDirectory().toString() + "/data/afterroot.pointerreplacer/files/pointer.png";
        return super.toString();
    }

    /**
     * @param view view that snackbar will displayed
     * @param message message that to be displayed in snackbar
     */
    public static void showSnackbar(View view, String message){
        final Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    /**
     * @param view view that snackbar will displayed
     * @param message message that to be displayed in snackbar
     * @param action action name that will display in snackbar
     */
    public static void showSnackbar(View view, String message, String action){
        final Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackBar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    /**
     * @param context the context
     * @return current DPI
     */
    public static int getDpi(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * @param view view that snackbar will displayed
     * @param message message that to be displayed in snackbar
     * @param action action name that will display in snackbar
     * @param listener onclick listener for action
     */
    public static void showSnackbar(View view, String message, String action, View.OnClickListener listener){
        final Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackBar.setAction(action, listener);
        snackBar.show();
    }

    /**
     * @param isEnable whather to enable night mode or not
     */
    public static void setNightModeEnabled(boolean isEnable){
        if (isEnable){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }
    }

    /**
     * @param fileName name of file
     * @return extension of fileName
     */
    public static String getFileExt(String fileName) {
        return fileName.substring((fileName.lastIndexOf(".")+ 1 ), fileName.length());
    }

    /**
     * @param fileName name of file
     * @return mime type of fileName
     */
    public static String getMimeType(String fileName) {
        String type = null;
        try {
            String extension = getFileExt(fileName);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } catch (Exception e){
            e.printStackTrace();
        }
        return type;
    }

    public static void loadToBottomSheetGrid(Context context,
                                             GridView target,
                                             String targetPath,
                                             AdapterView.OnItemClickListener listener){
        final PointerAdapter pointerAdapter = new PointerAdapter(context);
        if (getDpi(context) <= 240){
            pointerAdapter.setLayoutParams(49);
        } else if (getDpi(context) >= 240){
            pointerAdapter.setLayoutParams(66);
        }

        target.setAdapter(pointerAdapter);
        target.setOnItemClickListener(listener);

        File[] files = new File(targetPath).listFiles();
        try {
            for (File file : files){
                pointerAdapter.add(file.getAbsolutePath());
            }
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    /**GridView Image Adapter.**/
    public static class PointerAdapter extends BaseAdapter {
        private Context mContext;
        static ArrayList<String> itemList = new ArrayList<>();
        public PointerAdapter(Context context) {
            mContext = context;
        }
        void add(String path) {
            itemList.add(path);
        }

        static String getPath(int index){
            return itemList.get(index);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int arg0){
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        int param = 49;
        public void setLayoutParams(int i){
            param = i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(param, param));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //imageView.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap bm = decodeSampleBitmapFromUri(itemList.get(position), param, param);
            imageView.setImageBitmap(bm);
            return imageView;
        }

        public Bitmap decodeSampleBitmapFromUri(String path, int reqWidth, int reqHeight) {
            Bitmap bm;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);
            return bm;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }
            return inSampleSize;
        }
    }
}
