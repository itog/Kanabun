/**
 * @author itog
 */
package info.itog_lab.kanabun;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author itog
 * 
 *         Database Schema Table: dictionary |id|reading|similarSoundingWords|
 * 
 */
public class Dictionary {
	final String TAG = "Kanabun";
	final String DB_NAME = "kanabun.sqlite";
	final String TABLE_NAME = "dictionary";
	final String COLUMN_KANA = "reading";
	final String COLUMN_ID = "id";

	Context context;
	SQLiteDatabase db;

	Dictionary(Context c) {
		context = c;
		try {
			db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,
					null);
		} catch (Exception e) {
			db = null;
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
}
