/**
 * @author itog
 */
package info.itog_lab.kanabun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import info.itog_lab.kanabun.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
	static final int PLAY_TIME = 120;
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
	TextView commentText;
	TextView pointText;
	AlertDialog dialog;
	Handler handler;
	MediaPlayer bgmPlayer;
	MediaPlayer clickPlayer;
	MediaPlayer goodPlayer;
	MediaPlayer badPlayer;
	MediaPlayer okPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		dict = new Dictionary(context);
		dict.init();
		btns = new Button[BUTTON_NUM_X * BUTTON_NUM_Y];
		setContentView(R.layout.game);

		LinearLayout layout = (LinearLayout) findViewById(R.id.board_main);
		LinearLayout v = new LinearLayout(this);
		v.setOrientation(LinearLayout.VERTICAL);
		layout.addView(v);

		bgmPlayer = MediaPlayer.create(this, R.raw.kanabun_fixed);
		clickPlayer = MediaPlayer.create(this, R.raw.pon001);
		goodPlayer = MediaPlayer.create(this, R.raw.quiz001good);
		badPlayer = MediaPlayer.create(this, R.raw.quiz002bad);
		okPlayer = MediaPlayer.create(this, R.raw.metalhit000);
		
		/**
		 * ボタンの配置
		 * TODO newするのに時間かかりそう。layout使う方がいい
		 */
		for (int i = 0; i < BUTTON_NUM_Y; i++) {
			LinearLayout h = new LinearLayout(this);
			h.setGravity(Gravity.CENTER);
			for (int j = 0; j < BUTTON_NUM_X; j++) {
				int key = i * BUTTON_NUM_X + j;
				h.setOrientation(LinearLayout.HORIZONTAL);
				btns[key] = new Button(this);
				btns[key].setWidth(50);
				btns[key].setHeight(50);
				btns[key].setGravity(Gravity.CENTER_VERTICAL);
			     LinearLayout.LayoutParams layoutParams = 
		              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		     	layoutParams.setMargins(5, 5, 5, 5);
		     	btns[key].setLayoutParams(layoutParams);
				btns[key].setBackgroundDrawable(getResources().getDrawable(
						R.drawable.button_small_orange));
				btns[key].setTextSize(28.0f);
				h.addView(btns[key]);
			}
			v.addView(h);
		}
		
		shuffleButtons(false);

		answerText = (TextView) findViewById(R.id.answer_text);
		timerText = (TextView) findViewById(R.id.timer_text);
		pointText = (TextView) findViewById(R.id.point_text);
		pointText.setText("0");
		commentText = (TextView) findViewById(R.id.comment_text);

		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			/**
			 * OKボタンが押された 辞書とのマッチングをして、正解ならポイント追加
			 */
			public void onClick(View arg0) {
				// Toast.makeText(context, answerText.getText(),
				// Toast.LENGTH_SHORT).show();
				try {
					okPlayer.seekTo(0);
					okPlayer.start();
				} catch (Exception e) {
					Log.w(TAG, "MediaPlayer Exception");
				}
				Cursor c = dict.matchResult(String.valueOf(answerText.getText()));
				if (c.getCount() > 0) {
					c.moveToFirst();
					Integer id = new Integer(c.getInt(0));
					if (!matchedId.contains(id)) {
						try {
							goodPlayer.seekTo(0);
							goodPlayer.start();
						} catch (Exception e) {
							Log.w(TAG, "MediaPlayer Exception");
						}

						//Toast.makeText(context, c.getString(2), Toast.LENGTH_SHORT).show();
						commentText.setText(c.getString(2));
						int point = c.getString(1).length();
						point = (point * (point - 1)) / 2;
						point += Integer.parseInt((pointText.getText().toString()));
						pointText.setText(String.valueOf(point));
						matchedId.add(id);
					} else {
						try {
							badPlayer.seekTo(0);
							badPlayer.start();
						} catch (Exception e) {
							Log.w(TAG, "MediaPlayer Exception");
						}

						//Toast.makeText(context, R.string.duplicate,Toast.LENGTH_SHORT).show();
						commentText.setText(R.string.duplicate);
					}
				} else {
					try {
						badPlayer.seekTo(0);
						badPlayer.start();
					} catch (Exception e) {
						Log.w(TAG, "MediaPlayer Exception");
					}

					//Toast.makeText(context, "NG", Toast.LENGTH_SHORT).show();
					commentText.setText(R.string.answer_ng);
				}
				answerText.setText("");
			}
		});


//		dialog.show();

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isGaming) {
			stopGame(false);
		}
		bgmPlayer.stop();
		dict.close();
		if (!isFinishing()) {
			finish();
		}
	}

	/**
	 * ボタンにかな文字をランダムに割り当て
	 * enableLisnter リスナーを有効にする
	 */
	void shuffleButtons(boolean enableListener) {
		ArrayList<Character> kanaList = new ArrayList<Character>();
		kanaList.addAll(Arrays.asList(kana));

		for (int i = 0; i < btns.length; i++) {
			try {
				int a = new Random().nextInt(kanaList.size());
				btns[i].setText(kanaList.remove(a).toString());
				if (enableListener) {
					btns[i].setOnClickListener(this);
				}
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
		}
	}

	void disableButtons() {
		for (int i = 0; i < BUTTON_NUM_Y; i++) {
			for (int j = 0; j < BUTTON_NUM_X; j++) {
				btns[i + BUTTON_NUM_X + j].setOnClickListener(null);
			}
		}
	}

	
	@Override
	public void onClick(View arg0) {
		try {
			clickPlayer.seekTo(0);
			clickPlayer.start();
		} catch (Exception e) {
			Log.w(TAG, "MediaPlayer Exception");
		}

		Button btn = (Button)arg0;
		CharSequence text = answerText.getText();
		answerText.setText(String.valueOf(text) + btn.getText().toString());
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
			playTime--;
			if (playTime > 0) {
				timerText.setText(String.valueOf(playTime));
				handler.postDelayed(this, TIMER_PERIOD);
			} else {
				stopGame(true);
			}
		}
	};

	private void startGame() {
		isGaming = true;
		shuffleButtons(true);
		playTime = PLAY_TIME;
		pointText.setText("0");
		answerText.setText("");
		commentText.setText(R.string.start_msg);
		matchedId = new LinkedList<Integer>();
		startTimer();
		try {
			bgmPlayer.seekTo(0);
			bgmPlayer.start();
		} catch (Exception e) {
			Log.w(TAG, "MediaPlayer Exception");
		}
	}
	private void stopGame(boolean isComplete) {
		disableButtons();
		timerText.setText(String.valueOf(0));
		matchedId = null;
		stopTimer();
		commentText.setText(pointText.getText() + "てんでした\n"); //TODO use @string
//		commentText.setText(R.string.pregame_msg);
		isGaming = false;
		if (isComplete) {
		/**
		 * ゲーム開始、終了ダイアログを生成
		 */
		dialog = new AlertDialog.Builder(this).setTitle(R.string.title)
				//.setMessage(pointText.getText().toString() + getString(R.string.after_point))
				.setMessage(pointText.getText().toString() + getString(R.string.after_point).toString())
				.setPositiveButton(R.string.restart_btn,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//startGame();
								commentText.setText(R.string.pregame_msg);
							}
						}).create();
		dialog.show();
		}
	}
	
	boolean isGaming; 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		String action = "";
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			action = "ACTION_UP";
			if (!isGaming) {
				startGame();
			}
			break;
		}

		Log.v(TAG, action);
		return true;
	}
}
