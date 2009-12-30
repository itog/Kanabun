package info.itog_lab.kanabun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KanabunStart extends Activity implements OnClickListener {

	Button startButton;
	Button ruleButton;
	Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		Dictionary dict = new Dictionary(this);
		dict.init();
		
		startButton = (Button)findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		ruleButton = (Button)findViewById(R.id.rule_button);
		ruleButton.setOnClickListener(this);

	}
	@Override
	public void onClick(View btn) {
		switch (btn.getId()) {
		case R.id.rule_button:
			dialog = new AlertDialog.Builder(this).setTitle(R.string.rule_title)
			.setMessage(R.string.rule_msg).create();
			dialog.show();
			break;
		case R.id.start_button:
			Intent intent = new Intent(this, Kanabun.class);
			startActivity(intent);
			break;
		}
		
	}
}

