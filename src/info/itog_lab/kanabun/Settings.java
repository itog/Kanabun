package info.itog_lab.kanabun;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	static final String OPT_BGM = "bgm";
	static final boolean OPT_BGM_DEFAULT = true;
	static final String OPT_SE = "se";
	static final boolean OPT_SE_DEFAULT = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
	}
	
	public static boolean isBgmOn(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
		.getBoolean(OPT_BGM, OPT_BGM_DEFAULT);
	}

	public static boolean isSeOn(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
		.getBoolean(OPT_SE, OPT_SE_DEFAULT);
	}
}
