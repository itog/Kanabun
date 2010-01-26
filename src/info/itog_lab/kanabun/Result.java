package info.itog_lab.kanabun;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Result extends Dialog {
	static String[] FROM = {"reading", "description_e"};
	static int[] TO = {R.id.answered_kana, R.id.answered_description};

	Context context;
	ListView resultList;
	
	public Result(Context context, int point, Integer[] answer_ids, String locale) {
		super(context);
		setContentView(R.layout.result);
		this.context = context;
		
		setTitle(R.string.result_title);
		TextView tv = (TextView)findViewById(R.id.point);
		tv.setText(String.valueOf(point));
		Dictionary dict = new Dictionary(context);
		dict.init();
		Cursor cursor = dict.getResultByIds(answer_ids);

		Button ok_button = (Button)findViewById(R.id.dialog_ok_button);
		ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		
		if (locale.equals("JP")) {
			FROM[1] = "description_j";
		}
		resultList = (ListView)findViewById(R.id.result_list);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, R.layout.item, cursor, FROM, TO);
		resultList.setAdapter(adapter);
		dict.close();
	}

//	private void showResults(Cursor cursor) {
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, R.layout.item, cursor, FROM, TO);
//		setListAdapter(adapter);
//	}
}
