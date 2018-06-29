package com.irmsimapp.Uitils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utills {


    private static final int RESULT_LOAD_IMAGE = 200;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static Dialog dialog;

    public static PopupWindow popup_window_Menu;
    public static ProgressDialog progressDialog;
    public static AlertDialog.Builder builder;
    public static AlertDialog alert;
    String mCurrentPhotoPath;
    private Uri fileUri;


    public static Dialog dialog_PostBlog;
    public static RelativeLayout rl_PostNewBlog;
    public static ImageView iv_Video, iv_Image, iv_Link, iv_Article;


    public static void setupFont(Context context, TextView textView, String fontName) {
        Typeface font = Typeface.createFromAsset(context
                .getAssets(), fontName);

        textView.setTypeface(font);

    }

    //    public static void setupFont(Context context, EditText editText, String fontName) {
//        Typeface font = Typeface.createFromAsset(context
//                .getAssets(), fontName);
//
//        editText.setTypeface(font);
//
//    }
    public static void setupFont(Context context, Button button, String fontName) {
        Typeface font = Typeface.createFromAsset(context
                .getAssets(), fontName);

        button.setTypeface(font);

    }

    public static void setupFont(Context context, CheckBox checkBox, String fontName) {
        Typeface font = Typeface.createFromAsset(context
                .getAssets(), fontName);

        checkBox.setTypeface(font);

    }


    // Show Progress Dialog
    public static void showDialog(Context context,String message) {

        try{
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCanceledOnTouchOutside(false);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }catch (Exception e)
        {

        }


    }

    // Show Progress Dialog
    public static void showAlertOkDialog(Context context,String message) {

        builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();


    }


    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(pDateString);
        return new Date().before(date);
    }


    // Dismiss Progrss Dialog
    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing())

            try{


                progressDialog.dismiss();

            }catch(Exception e)
            {

            }

    }

    /**
     * Set Custom Fonts of EditText
     *
     * @param context
     * @param editText
     * @param fontName
     */
    public static void setupFont(Context context, EditText editText, String fontName) {
        Typeface font = Typeface.createFromAsset(context
                .getAssets(), fontName);

        editText.setTypeface(font);
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static int dpToPx(Context crnt_context, int dp) {

        DisplayMetrics displayMetrics = crnt_context.getResources()
                .getDisplayMetrics();

        int densityDpi = (int) (displayMetrics.densityDpi);
        float ratio = (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        int px;
        if (ratio == 0) {
            px = dp;
        } else {
            px = Math.round(dp * ratio);

        }

        return px;

    }

    /**
     * @param crnt_context
     * @param px
     * @return
     */
    public static int pxToDp(Context crnt_context, int px) {

        DisplayMetrics displayMetrics = crnt_context.getResources()
                .getDisplayMetrics();

        int densityDpi = (int) (displayMetrics.densityDpi);
        float ratio = (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        int dp = -1;
        if (ratio == 0) {
            dp = px;
        } else {
            dp = Math.round(px / ratio);
        }

        return dp;

    }


    /**
     * Set Custom Fonts of Radio Button
     *
     * @param context
     * @param rd
     * @param fontName
     */
    public static void setupFont(Context context, RadioButton rd, String fontName) {
        Typeface font = Typeface.createFromAsset(context
                .getAssets(), fontName);

        rd.setTypeface(font);
    }

    public static int getStatusBarHeight(Context context) {
        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }


    public static int get_device_width(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int width = metrics.widthPixels;

        return width;
    }

    @SuppressWarnings("resource")
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }


    public static String getTrimmedStringFromAPI(String value) {

        if (value == null) {
            return "";
        } else if (value.equals("null") || value.equals("Null") || value.equals("NULL")) {

            return "";
        } else if (value.equals("")) {

            return "";
        } else {
            return value;
        }
    }



    /**
     * To Hide Dialog
     */
    public static void hideDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static void setEditTextFocusListener(final Context context, final ArrayList<EditText> edtArray) {
        for (int i = 0; i < edtArray.size(); i++) {

            final int finalI = i;
            edtArray.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    } else {

                        //TODO :  For Capitalize Number After Loss Focus from Registration Number EditText
                       /* if (edtArray.get(finalI).getId() == R.id.edt_vin_number) {

                            EditText editText = edtArray.get(finalI);

                            if (!editText.getText().toString().equals("")) {

                                editText.setText(editText.getText().toString().toUpperCase());

                            }
                        }*/
                    }
                }
            });
        }


    }


    /**
     * To Convert Date Time Format
     *
     * @param fromFormat
     * @param toFormat
     * @param dateOriginalGot
     * @return
     */
    public static String convertDateTime(String fromFormat, String toFormat, String dateOriginalGot) {

        try {
            //SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //// Getting Source format here
            SimpleDateFormat fmt = new SimpleDateFormat(fromFormat);

            fmt.setTimeZone(TimeZone.getDefault());

            Date date = fmt.parse(dateOriginalGot);

            //SimpleDateFormat fmtOut = new SimpleDateFormat("EEE, MMM d, ''yyyy");

            //// Setting Destination format here
            SimpleDateFormat fmtOut = new SimpleDateFormat(toFormat);

            return fmtOut.format(date);

        } catch (Exception e) {

            e.printStackTrace();

            e.getMessage();

        }

        return "";

    }


    public static Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Bitmap smallBitmap, int radius, Context context) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    public static Bitmap RGB565toARGB888(Bitmap img) throws Exception {


        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    /**
     * returning image / video
     */
    public static File createImageFile() throws IOException {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/" + "Shatika");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, "shatika_" + System.currentTimeMillis() + ".jpg");
        return file;
    }


    /**
     * returning Rotate Image
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    /**
     * @param picturePath
     * @return
     */
    public static int getImageAngle(String picturePath) {
        try {
            ExifInterface ei = new ExifInterface(picturePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    Log.i("TEST", "orientation : " + 90);
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    Log.i("TEST", "orientation : " + 180);
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    Log.i("TEST", "orientation : " + 270);
                    return 270;
                default:
                    Log.i("TEST", "orientation : " + 0);
                    return 0;
            }

        } catch (IOException e) {
            Log.e("TEST", "" + e.getMessage());
            return 0;
        }

    }

    public static void log(String message) {
        Log.i("IM APP", message);
    }


    /**
     * @param context
     * @return
     */
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null && connectivity.getActiveNetworkInfo() != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    // Checking Device Support Camera or not
    public static boolean isDeviceSupportCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            Toast.makeText(context, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }


    // Setting Bitmap in Imageview from ImagePath
    public static Bitmap setFullImageFromFilePath(ImageView imageView, String imagePath) {

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        Utills.log("Imageview Width" + targetW);

        int targetH = imageView.getHeight();
        Utills.log("Imageview Height" + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        Utills.log("Imageview Bitmap Original Width" + photoW);
        int photoH = bmOptions.outHeight;
        Utills.log("Imageview Bitmap Original Height" + photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        int imageAngle = Utills.getImageAngle(imagePath);
        bitmap = Utills.rotateImage(bitmap, imageAngle);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    // for  Url is valid or not
    public static boolean isValidUrl(String string) {

        boolean b = Patterns.WEB_URL.matcher(string).matches();
        return b;
    }

    // For Email Id Is Valid Or Not
    public static boolean isValidEmail(String string) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }


   /*   For set Custom List View */

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter myListAdapter = listView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (myListAdapter.getCount() - 1));
        params.height = totalHeight;
        listView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }


    public static String convertToTrueTimeForm(long commentMinutes) {
        // TODO Auto-generated method stub

        String last_suffix = " ago";

        String suffixMin = " mins";

        String suffixMin1 = " min";

        String suffixHour = " hours";

        String suffixHour1 = " hour";

        String suffixDays = " days";

        String suffixDays1 = " yesterday";

        String suffixMonths = " months";

        String suffixMonths1 = " months";

        String suffixYears = " years";

        String suffixYears1 = " year";

        if (commentMinutes < 60) {

            if (commentMinutes < 2) {
                return commentMinutes + suffixMin1 + last_suffix;
            } else {
                return commentMinutes + suffixMin + last_suffix;
            }

        } else if (commentMinutes < (60 * 24) && commentMinutes > 60) {

            if ((commentMinutes / 60) < 2) {
                return (commentMinutes / 60) + suffixHour1 + last_suffix;
            } else {

                return (commentMinutes / 60) + suffixHour + last_suffix;
            }

        } else if (commentMinutes < (60 * 24 * 30)
                && commentMinutes > (60 * 24)) {

            if ((commentMinutes / (60 * 24)) < 2) {


                return suffixDays1;
            } else {
                return (commentMinutes / (60 * 24)) + suffixDays + last_suffix;
            }

        } else if (commentMinutes < (60 * 24 * 30 * 12)
                && commentMinutes > (60 * 24 * 30)) {

            if ((commentMinutes / (60 * 24 * 30)) < 2) {
                return (commentMinutes / (60 * 24 * 30)) + suffixMonths1
                        + last_suffix;
            } else {
                return (commentMinutes / (60 * 24 * 30)) + suffixMonths
                        + last_suffix;
            }

        } else if (commentMinutes >= (60 * 24 * 30 * 12)) {

            if ((commentMinutes / (60 * 24 * 30 * 12)) < 2) {


                return (commentMinutes / (60 * 24 * 30 * 12)) + suffixYears1
                        + last_suffix;
            } else {

                return (commentMinutes / (60 * 24 * 30 * 12)) + suffixYears
                        + last_suffix;
            }

        }

        return null;
    }

    public static String convertToTrueTimeFormCustom(long commentMinutes) {
        // TODO Auto-generated method stub

        String last_suffix = "ago";
        String suffixMin = " mins";
        String suffixMin1 = " min";
        String suffixHour = " hours";
        String suffixHour1 = " hour";
        String suffixDays1 = "yesterday";
        String defaultString = "DATE";

        if (commentMinutes < 60) {
            if (commentMinutes < 2) {
                return commentMinutes + suffixMin1 + " " + last_suffix;
            } else {
                return commentMinutes + suffixMin + " " + last_suffix;
            }

        } else if (commentMinutes < (60 * 24) && commentMinutes > 60) {
            if ((commentMinutes / 60) < 2) {
                return (commentMinutes / 60) + suffixHour1 + " " + last_suffix;
            } else {
                return (commentMinutes / 60) + suffixHour + " " + last_suffix;
            }

        } else if (commentMinutes < (60 * 24 * 30)
                && commentMinutes > (60 * 24)) {
            if ((commentMinutes / (60 * 24)) < 2) {
                return suffixDays1;
            } else {
                return defaultString;
            }

        } else if (commentMinutes < (60 * 24 * 30 * 12)
                && commentMinutes > (60 * 24 * 30)) {
            if ((commentMinutes / (60 * 24 * 30)) < 2) {
                return defaultString;
            } else {
                return defaultString;
            }

        } else if (commentMinutes >= (60 * 24 * 30 * 12)) {
            if ((commentMinutes / (60 * 24 * 30 * 12)) < 2) {
                return defaultString;
            } else {
                return defaultString;
            }
        }
        return null;
    }


    public static Date convertStringToDate(String date) {
        // TODO Auto-generated method stub
        SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }

    public static String getCommaSapratedStringFromList(List<String> list) {
        // Converting ArrayList to String in Java using advanced for-each loop
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append(","); //separating contents using semi comma
        }

        String strfromArrayList = sb.toString();
        if (list.size() > 0) {
            strfromArrayList = strfromArrayList.substring(0, strfromArrayList.length() - 1);
        }


        return strfromArrayList;
    }

    public static void showToast(Context context, String messages) {

        Toast toast = Toast.makeText(context, messages, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();


    }

    public static void finishFragmentOrActivity(View v, Activity activity, FragmentManager fragmentManager) {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            activity.finish();
        }
    }

    public static void finishFragmentOrActivityNew( Activity activity, FragmentManager fragmentManager) {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            activity.finish();
        }
    }

    public static String roundToOneDigit(float paramFloat) {
        return String.format("%.2f%n", paramFloat);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



}


