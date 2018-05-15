package com.cxy.yuwen.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
	private static final String TAG = "SDK_Sample.Util";
	
	private static Dialog mProgressDialog;
	private static Toast mToast;

	public  static final String BmobApplicationId="af1195b5462c886be8636a6845ba773a";
	public static String TencentAppId="1106093430";
	public static final String KEY_SHA = "SHA";


    /**
     * 以最省内存的方式读取图片
     */
    public static Bitmap readBitmap(final String path){
        try{
            FileInputStream stream = new FileInputStream(new File(path+"test.jpg"));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 8;
            opts.inPurgeable=true;
            opts.inInputShareable=true;
            Bitmap bitmap = BitmapFactory.decodeStream(stream , null, opts);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }


	
	public static final void showResultDialog(Context context, String msg,
			String title) {
		if(msg == null) return;
		//String rmsg = msg.replace(",", "\n");
		Log.d("Util", msg);
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setNegativeButton("知道了", null).create().show();
	}

	public static final void showProgressDialog(Context context, String title,
			String message) {
		dismissDialog();
		if (TextUtils.isEmpty(title)) {
			title = "请稍候";
		}
		if (TextUtils.isEmpty(message)) {
			message = "正在加载...";
		}
		mProgressDialog = ProgressDialog.show(context, title, message);
	}
	
	public static AlertDialog showConfirmCancelDialog(Context context,
			String title, String message,
			DialogInterface.OnClickListener posListener) {
		AlertDialog dlg = new AlertDialog.Builder(context).setMessage(message)
				.setPositiveButton("确认", posListener)
				.setNegativeButton("取消", null).create();
		dlg.setCanceledOnTouchOutside(false);
		dlg.show();
		return dlg;
	}

	public static final void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	/**
	 * 打印消息并且用Toast显示消息
	 * 
	 * @param activity
	 * @param message
	 * @param logLevel
	 *            填d, w, e分别代表debug, warn, error; 默认是debug
	 */
	public static final void toastMessage(final Activity activity,
			final String message, String logLevel) {
		if ("w".equals(logLevel)) {
			Log.w("sdkDemo", message);
		} else if ("e".equals(logLevel)) {
			Log.e("sdkDemo", message);
		} else {
			Log.d("sdkDemo", message);
		}

		if (mToast != null) {
					mToast.cancel();
					mToast = null;
		}
		mToast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
		mToast.show();

	}

	/**
	 * 打印消息并且用Toast显示消息
	 * 
	 * @param activity
	 * @param message
	 * @param logLevel
	 *            填d, w, e分别代表debug, warn, error; 默认是debug
	 */
	public static final void toastMessage(final Activity activity,
			final String message) {
		toastMessage(activity, message, null);
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		Log.v(TAG, "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v(TAG, "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v(TAG, "getbitmap bmp fail---");
            bitmap = null;
		}
		return bitmap;
	}
	
	public static void release() {
	    mProgressDialog = null;
	    mToast = null;
	}

	public static String [] returnImageUrlsFromHtml(String htmlCode) {
		List<String> imageSrcList = new ArrayList<String>();
		//  String htmlCode = returnExampleHtml();
		Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(htmlCode);
		String quote = null;
		String src = null;
		while (m.find()) {
			quote = m.group(1);
			src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
			imageSrcList.add(src);
		}
		if (imageSrcList == null || imageSrcList.size() == 0) {
			Log.e("imageSrcList","资讯中未匹配到图片链接");
			return null;
		}
		return imageSrcList.toArray(new String[imageSrcList.size()]);
	}


	// =========
    // =通过URI获取本地图片的path
    // =兼容android 5.0
    // ==========

    public static String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
    public static int Build_VERSION_KITKAT = 19;
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;

        // DocumentProvider
        if (isKitKat && isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static final String PATH_DOCUMENT = "document";

    /**
     * Test if the given URI represents a {@link Document} backed by a
     * {@link DocumentsProvider}.
     */
    private static boolean isDocumentUri(Context context, Uri uri) {
        final List<String> paths = uri.getPathSegments();
        if (paths.size() < 2) {
            return false;
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            return false;
        }

        return true;
    }

    private static String getDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     *            [url=home.php?mod=space&uid=7300]@return[/url] The value of
     *            the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


	public static  boolean isEmpty(String s){
		if(s!=null&&s.length()>0){
			return false;
		}else{
			return true;
		}

	}


	/**
	 * 加密数据
	 * @param inputStr 加密前的数据
	 * @return
	 */
	public static  String  encryptBySHA(String inputStr)
	{
		BigInteger sha =null;
		System.out.println("=======加密前的数据:"+inputStr);
		byte[] inputData = inputStr.getBytes();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
			messageDigest.update(inputData);
			sha = new BigInteger(messageDigest.digest());
			System.out.println("SHA加密后:" + sha.toString(32));
		} catch (Exception e) {e.printStackTrace();}
		return sha.toString(32);
	}


	/**
	 * 校验手机号
	 *
	 * @param mobile
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	/**
	 * 验证密码
	 * @param password
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPassword(String password){
		//密码正则表达式 6-20 位，字母、数字
		String regStr = "^[a-zA-Z0-9]{6,20}$";
		return Pattern.matches(regStr, password);


	}
	public static boolean checkNetworkState(Activity activity){
		boolean networkSate=NetWorkUtils.isNetworkConnected(activity);
		if (!networkSate){
			Utils.toastMessage(activity,"网络连接不可用，请检查网络状态");
		}

		return networkSate;
	}

	//根据指定长度生成字母和数字的随机数
	//0~9的ASCII为48~57
	//A~Z的ASCII为65~90
	//a~z的ASCII为97~122
	public static String createRandomCharData(int length)
	{
		StringBuilder sb=new StringBuilder();
		Random rand=new Random();//随机用以下三个随机生成器
		Random randdata=new Random();
		int data=0;
		for(int i=0;i<length;i++)
		{
			int index=rand.nextInt(3);
			//目的是随机选择生成数字，大小写字母
			switch(index)
			{
				case 0:
					data=randdata.nextInt(10);//仅仅会生成0~9
					sb.append(data);
					break;
				case 1:
					data=randdata.nextInt(26)+65;//保证只会产生65~90之间的整数
					sb.append((char)data);
					break;
				case 2:
					data=randdata.nextInt(26)+97;//保证只会产生97~122之间的整数
					sb.append((char)data);
					break;
			}
		}
		String result=sb.toString();
		return result;

	}

}


