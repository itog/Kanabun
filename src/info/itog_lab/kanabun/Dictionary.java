/**
 * @author itog
 */
package info.itog_lab.kanabun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author itog
 * 
 *         Database Schema Table: dictionary |id|reading|similarSoundingWords|
 *         Database Schema Table: dictionary |id|reading|description_j|description_e|
 * 
 */
public class Dictionary {
	public static final int COLUMN_NUM_ID = 0;
	public static final int COLUMN_NUM_READING = 1;
	public static final int COLUMN_NUM_DESC_J = 2;
	public static final int COLUMN_NUM_DESC_E = 3;

	final String TAG = "Kanabun";
	final String DB_NAME = "kanabun_m.sqlite";
	final String ZIP_NAME = "kanabun_m.zip";
	final String TABLE_NAME = "dictionary";
	final String COLUMN_KANA = "reading";
	final String COLUMN_ID = "_id";
	final int DEFAULT_BUFFER_SIZE = 1024;

	final int FILE_DIVIDED_NUM = 3;
	final String DEVIDED_FILE_PREFIX = "kanabun.";

	Context context;
	SQLiteDatabase db;

	Dictionary(Context c) {
		context = c;
	}
	
//	public static int getDescriptionIdCurrentLocale() {
//		int ret = COLUMN_NUM_DESC_E;
//		String locale = context.getResources().getConfiguration().locale.getDisplayName();
//		if (locale == "ja") {
//			ret = COLUMN_NUM_DESC_J;
//		}
//		return ret;
//	}
	
	public void init() {
		File path = new File("/data/data/" + context.getPackageName() + "/databases/" + DB_NAME);
		if (path.exists()) {
			try {
				db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
			} catch (Exception e) {
				path.delete();
				db = null;
			}
		} else {
			try {
				String dest;
				dest = "/data/data/" + context.getPackageName() + "/databases/";
				File dir = new File(dest);
				dir.mkdirs();
				extractZipFiles(dest, ZIP_NAME);
//				combineDevidedFiles(dest, DB_NAME);
				db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
			} catch (Exception e2) {
				path.delete();
				db = null;
			}
		}
	}
	
	public Cursor matchResult(String str) {
		Cursor c = null;
		try {
			c = db.query(TABLE_NAME, null, COLUMN_KANA + "='" + str + "'",
					null, null, null, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return c;
	}

	public Cursor getResultByIds(Integer[] ids) {
		Cursor c = null;
		String tmp = Arrays.toString(ids); // [1,2,3]
		tmp = tmp.replace("[", "(");
		tmp = tmp.replace("]", ")");
		try {
			c = db.query(TABLE_NAME, null, COLUMN_ID + " in " + tmp,
					null, null, null, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return c;
	}
	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	private void combineDevidedFiles(String destination, String devidedFilePrefix) {
		File destFile = new File(destination);
		destFile.mkdirs();
		destination += devidedFilePrefix;
		destFile = new File(destination);
		InputStream input = null;
			
		try {
			AssetManager assetManager = context.getAssets();
			OutputStream output = new FileOutputStream(destFile);

			for (int i=1;i<=FILE_DIVIDED_NUM;i++) {
				input = assetManager.open(DEVIDED_FILE_PREFIX + String.valueOf(i), AssetManager.ACCESS_STREAMING);
				byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
				int size;
				while (true) {
					size = input.read(buf);
					if (size <= 0) {
						break;
					}
					output.write(buf, 0, size);
				}
				input.close();
			}
			output.close();
		} catch (Exception e) {
			destFile.delete();
			Log.w(TAG, e.getMessage());
		}
	}

	public void extractZipFiles(String dest, String zipName) {
		try {
			AssetManager assetManager = context.getAssets();			
			InputStream inputStream = assetManager.open(zipName, AssetManager.ACCESS_STREAMING);

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			
			while (zipEntry != null) { 
				String entryName = zipEntry.getName();
				int n;
				FileOutputStream fileOutputStream;
//				File newFile = new File(entryName);
//				String directory = newFile.getParent();
//				if(directory == null) {
//					if(newFile.isDirectory())
//						break;
//				}
				fileOutputStream = new FileOutputStream(dest + "/" + entryName);             

				byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
				while ((n = zipInputStream.read(buf, 0, DEFAULT_BUFFER_SIZE)) > -1) {
					fileOutputStream.write(buf, 0, n);
				}

				fileOutputStream.close(); 
				zipInputStream.closeEntry();
				zipEntry = zipInputStream.getNextEntry();

			}
			zipInputStream.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
