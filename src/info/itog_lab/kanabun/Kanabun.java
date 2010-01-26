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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Kanabun extends Activity implements OnTouchListener {
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
	Dialog dialog;
	Handler handler;
	OkListener okListener;

	SoundEffect soundEffect;
	boolean isGaming; 
	String locale;
	int descriptionColumnNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		dict = new Dictionary(context);
		dict.init();
		btns = new Button[BUTTON_NUM_X * BUTTON_NUM_Y];
		setContentView(R.layout.game);

		LinearLayout layout = (LinearLayout)findViewById(R.id.board_main);
		LinearLayout v = new LinearLayout(this);
		v.setOrientation(LinearLayout.VERTICAL);
		layout.addView(v);

		soundEffect = new SoundEffect(this);

		locale = context.getResources().getConfiguration().locale.getCountry();
		if (locale.equals("JP")) {
			descriptionColumnNum = Dictionary.COLUMN_NUM_DESC_J;
		} else {
			descriptionColumnNum = Dictionary.COLUMN_NUM_DESC_E;
		}

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
			    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		     	layoutParams.setMargins(5, 5, 5, 5);
		     	btns[key].setLayoutParams(layoutParams);
				btns[key].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_small_orange));
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

		okButton = (Button)findViewById(R.id.ok_button);
		okListener = new OkListener();

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isGaming) {
			stopGame(false);
		}
		soundEffect.stopBgm();
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
					btns[i].setOnTouchListener(this);
				}
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
		}
	}

	void disableButtons() {
		for (int i = 0; i < BUTTON_NUM_Y; i++) {
			for (int j = 0; j < BUTTON_NUM_X; j++) {
				btns[i * BUTTON_NUM_X + j].setOnTouchListener(null);
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			soundEffect.playClick();
			Button btn = (Button)arg0;
			CharSequence text = answerText.getText();
			answerText.setText(String.valueOf(text) + btn.getText().toString());
		}
		return false;
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
		okButton.setOnTouchListener(okListener);
		shuffleButtons(true);
		playTime = PLAY_TIME;
		pointText.setText("0"); // 必要ない。念のため
		answerText.setText(""); // 必要ない。念のため
		matchedId = null;
		commentText.setText(R.string.start_msg);
		matchedId = new LinkedList<Integer>();
		startTimer();
		soundEffect.playBgm();
	}
	private void stopGame(boolean isComplete) {
		disableButtons();
		timerText.setText(String.valueOf(0));
		stopTimer();
		commentText.setText(pointText.getText() + getString(R.string.after_point).toString());
		okButton.setOnTouchListener(null);
		isGaming = false;
		// 得点をダイアログで表示する
		if (isComplete) {
//			dialog = new AlertDialog.Builder(this).setTitle(R.string.title)
//			.setMessage(pointText.getText().toString() + getString(R.string.after_point).toString())
//			.setPositiveButton(R.string.restart_btn,
//					new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					commentText.setText(R.string.pregame_msg);
//				}
//			}).create();
//			dialog.show();
			dialog = new Result(this, Integer.parseInt(pointText.getText().toString()), matchedId.toArray(new Integer[0]), locale);
			dialog.show();
			commentText.setText(R.string.pregame_msg);
		}
		pointText.setText("0");
		answerText.setText("");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		String action = "";
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			action = "ACTION_DOWN";
			ImageView character = (ImageView)findViewById(R.id.character);
			int[] coordinate = new int[2];
			character.getLocationOnScreen(coordinate);
			if ( (coordinate[0] < event.getRawX()) && (event.getRawX() < coordinate[0] + character.getWidth()) &&
				 (coordinate[1] < event.getRawY()) && (event.getRawY() < coordinate[1] + character.getHeight())) {
				if (!isGaming) {
					//TODO add count down animation to start
					startGame();
				}				
			}
			break;
		}
		Log.v(TAG, action);
		return true;
	}
	
	class SoundEffect {
//		boolean isBgmEnabled;
//		boolean isEffectEnabled;
		Context context;
		
		MediaPlayer bgmPlayer;
		MediaPlayer clickPlayer;
		MediaPlayer goodPlayer;
		MediaPlayer badPlayer;
		MediaPlayer okPlayer;
		
		SoundEffect(Context context) {
			this.context = context;
			bgmPlayer = MediaPlayer.create(context, R.raw.kanabun_fixed);
			clickPlayer = MediaPlayer.create(context, R.raw.pon001);
			goodPlayer = MediaPlayer.create(context, R.raw.quiz001good);
			badPlayer = MediaPlayer.create(context, R.raw.quiz002bad);
			okPlayer = MediaPlayer.create(context, R.raw.metalhit000);
		}

//		public void enableBgm(boolean on) {
//			isBgmEnabled = on;
//		}
//		
//		public void enableEffect(boolean on) {
//			isEffectEnabled = on;
//		}
		
		public void playBgm() {
			if (!Settings.isBgmOn(context)) return ;
			try {
				bgmPlayer.seekTo(0);
				bgmPlayer.start();
			} catch (Exception e) {
				Log.w(TAG, "MediaPlayer Exception");
			}
		}

		void stopBgm() {
			bgmPlayer.stop();
		}

		void playOk() {
			if (!Settings.isSeOn(context)) return ;
			try {
				okPlayer.seekTo(0);
				okPlayer.start();
			} catch (Exception e) {
				Log.w(TAG, "MediaPlayer Exception");
			}
		}

		void playGood() {
			if (!Settings.isSeOn(context)) return ;
			try {
				goodPlayer.seekTo(0);
				goodPlayer.start();
			} catch (Exception e) {
				Log.w(TAG, "MediaPlayer Exception");
			}
		}
		
		void playBad() {
			if (!Settings.isSeOn(context)) return ;
			try {
				badPlayer.seekTo(0);
				badPlayer.start();
			} catch (Exception e) {
				Log.w(TAG, "MediaPlayer Exception");
			}
		}
		
		void playClick() {
			if (!Settings.isSeOn(context)) return ;
			try {
				clickPlayer.seekTo(0);
				clickPlayer.start();
			} catch (Exception e) {
				Log.w(TAG, "MediaPlayer Exception");
			}
		}
	}

	class OkListener implements OnTouchListener {
		public boolean onTouch(View arg0, MotionEvent event) {
			if (isGaming) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					soundEffect.playOk();
					Cursor c = dict.matchResult(String.valueOf(answerText.getText()));
					if (c.getCount() > 0) {
						c.moveToFirst();
						Integer id = new Integer(c.getInt(Dictionary.COLUMN_NUM_ID));
						if (!matchedId.contains(id)) {
							soundEffect.playGood();
							//Toast.makeText(context, c.getString(2), Toast.LENGTH_SHORT).show();
							commentText.setText(c.getString(descriptionColumnNum));
							int point = c.getString(1).length();
							point = (point * (point - 1)) / 2;
							point += Integer.parseInt((pointText.getText().toString()));
							pointText.setText(String.valueOf(point));
							matchedId.add(id);
						} else {
							soundEffect.playBad();

							//Toast.makeText(context, R.string.duplicate,Toast.LENGTH_SHORT).show();
							commentText.setText(R.string.duplicate);
						}
					} else {
						soundEffect.playBad();

						//Toast.makeText(context, "NG", Toast.LENGTH_SHORT).show();
						commentText.setText(R.string.answer_ng);
					}
					answerText.setText("");
				}
			}
			return false;
		}
	}
}
