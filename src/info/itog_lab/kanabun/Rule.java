package info.itog_lab.kanabun;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class Rule extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rule);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		//return super.onTouchEvent(event);
		return true;
	}
}
