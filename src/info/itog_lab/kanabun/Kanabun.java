/**
 * @author itog
 */
package info.itog_lab.kanabun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import info.itog_lab.kanabun.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Kanabun extends Activity implements OnClickListener {
	static final String TAG = "Kanabun";
	static final int BUTTON_NUM_X = 5;
	static final int BUTTON_NUM_Y = 5;

	static final int TIMER_PERIOD = 1000;
	static final int PLAY_TIME = 60;
	int playTime = PLAY_TIME;
	LinkedList<Integer> matchedId;

	Dictionary dict;
	static final Character[] kana = {
		'あ', 'い', 'う', 'え', 'お',
		'か', 'き', 'く',	'け', 'こ',
		'さ', 'し', 'す', 'せ', 'そ',
		'た', 'ち', 'つ', 'て', 'と',
		'な', 'に', 'ぬ', 'ね', 'の',
		'は', 'ひ', 'ふ', 'へ', 'ほ',
		'ま', 'み', 'む', 'め', 'も',
		'や', 'ゆ', 'よ',
		'ら', 'り', 'る', 'れ', 'ろ',
		'わ', 'を', 'ん',
		'が', 'ぎ', 'ぐ', 'げ', 'ご',
		'ざ', 'じ', 'ず', 'ぜ', 'ぞ',
		'だ', 'ぢ', 'づ', 'で','ど',
		'ば', 'び', 'ぶ', 'べ', 'ぼ',
		'ぱ', 'ぴ', 'ぷ', 'ぺ', 'ぽ', };

	Context context;
	Button[] btns;
	Button okButton;
	TextView answerText;
	TextView timerText;
	TextView pointText;
	AlertDialog dialog;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		dict = new Dictionary(context);
		btns = new Button[BUTTON_NUM_X * BUTTON_NUM_Y];
		setContentView(R.layout.main);

		LinearLayout layout = (LinearLayout) findViewById(R.id.board_main);
		LinearLayout v = new LinearLayout(this);
		v.setOrientation(LinearLayout.VERTICAL);
		layout.addView(v);

		/**
		 * ボタンの配置
		 */
		for (int i = 0; i < BUTTON_NUM_Y; i++) {
			LinearLayout h = new LinearLayout(this);
			h.setGravity(Gravity.CENTER);
			for (int j = 0; j < BUTTON_NUM_X; j++) {
				int key = i * BUTTON_NUM_X + j;
				h.setOrientation(LinearLayout.HORIZONTAL);
				btns[key] = new Button(this);
				btns[key].setWidth(60);
				btns[key].setBackgroundDrawable(getResources().getDrawable(
						R.drawable.button_normal));
				btns[key].setTextSize(30.0f);
				h.addView(btns[key]);
			}
			v.addView(h);
		}

		shuffleButtons();

		answerText = (TextView) findViewById(R.id.answer_text);
		timerText = (TextView) findViewById(R.id.timer_text);
		pointText = (TextView) findViewById(R.id.point_text);

		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			/**
			 * OKボタンが押された 辞書とのマッチングをして、正解ならポイント追加
			 */
			public void onClick(View arg0) {
				// Toast.makeText(context, answerText.getText(),
				// Toast.LENGTH_SHORT).show();
				Cursor c = dict.matchResult(String
						.valueOf(answerText.getText()));
				if (c.getCount() > 0) {
					c.moveToFirst();
					Integer id = new Integer(c.getInt(0));
					if (!matchedId.contains(id)) {
						// Toast.makeText(context, "OK",
						// Toast.LENGTH_SHORT).show();
						Toast.makeText(context, c.getString(2),
								Toast.LENGTH_SHORT).show();
						int point = c.getString(1).length();
						point = (point * (point - 1)) / 2;
						point += Integer.parseInt((pointText.getText()
								.toString()));
						pointText.setText(String.valueOf(point));
						matchedId.add(id);
					} else {
						Toast.makeText(context, "duplicated",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, "NG", Toast.LENGTH_SHORT).show();
				}
				answerText.setText("");
			}
		});

		/**
		 * ゲーム開始、終了ダイアログを生成
		 */
		dialog = new AlertDialog.Builder(this).setTitle(R.string.title)
				.setMessage(R.string.start_msg).setNegativeButton(
						R.string.exit_btn,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setPositiveButton(R.string.start_btn,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								shuffleButtons();
								playTime = PLAY_TIME;
								pointText.setText("0");
								answerText.setText("");
								matchedId = new LinkedList<Integer>();
								startTimer();
							}
						}).create();
		dialog.show();

		super.onCreate(savedInstanceState);
	}

	/**
	 * ボタンにかな文字をランダムに割り当てる
	 */
	void shuffleButtons() {
		ArrayList<Character> kanaList = new ArrayList<Character>();
		kanaList.addAll(Arrays.asList(kana));

		for (int i = 0; i < btns.length; i++) {
			try {
				btns[i].setBackgroundDrawable(getResources().getDrawable(
						R.drawable.button_normal));
				int a = new Random().nextInt(kanaList.size());
				btns[i].setText(kanaList.remove(a).toString());
				btns[i].setOnClickListener(this);
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		Button btn = (Button) arg0;
		CharSequence text = answerText.getText();
		answerText.setText(String.valueOf(text) + btn.getText().toString());
		Log.w(TAG, String.valueOf(btn.getId()));
	}

	public void startTimer() {
		handler = new Handler();
		handler.removeCallbacks(task);
		handler.postDelayed(task, TIMER_PERIOD);
	}

	public void stopTimer() {
		handler.removeCallbacks(task);
		handler = null;
	}

	private Runnable task = new Runnable() {
		public void run() {
			Log.v(TAG, "Timer run");
			playTime--;
			if (playTime > 0) {
				timerText.setText(String.valueOf(playTime));
				handler.postDelayed(this, TIMER_PERIOD);
			} else {
				timerText.setText(String.valueOf(0));
				matchedId = null;
				stopTimer();
				dialog.show();
			}
		}
	};
}
