package info.itog_lab.kanabun;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import info.itog_lab.kanabun.R;

public class KanabunStart extends Activity implements OnClickListener {

	Button startButton;
	Button ruleButton;
	Button settingButton;
	Button infoButton;
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
	
	String[] str_items = {"BGM ON", "SoundEffect ON"};
	boolean[] flags = {true, true};

	@Override
	public void onClick(View btn) {
		switch (btn.getId()) {
		case R.id.rule_button:
//			Integer tmp[] = {1,2,3,4,5,6,7,8,9,10};
//			dialog = new Result(this, 100, tmp);
//			dialog.show();
			startActivity(new Intent(this, Rule.class));
			break;
		case R.id.start_button:
			startActivity(new Intent(this, Kanabun.class));
			break;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		}
		return false;
	}
}

