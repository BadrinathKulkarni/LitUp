package com.baug.litup;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class Litup extends Activity implements ViewSwitcher.ViewFactory,
		View.OnClickListener {

	private GridView mGridView = null;

	private int[] mMatrix = new int[25];
	private int mMoves = 0;

	boolean isStarted = false;

	private TextView mMovesText = null;
	private TextSwitcher mSwitcher;

	/**
	 * This will set the activity layout to full screen.
	 */
	public void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullscreen();
		setContentView(R.layout.main);
		
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setAdapter(new GridViewAdapter(this));

		Button start = (Button) findViewById(R.id.start);
		start.setOnClickListener(mStartClickListener);

		Button reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(mResetClickListener);

		mMovesText = (TextView) findViewById(R.id.cnt);

		mSwitcher = (TextSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);

		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		mSwitcher.setInAnimation(in);
		mSwitcher.setOutAnimation(out);
		mSwitcher.setText(String.valueOf(mSecondCounter));

	}

	/* ********************************************************************** */
	/* Timer methods. */
	/* ********************************************************************** */

	private int mSecondCounter = 0;
	private Handler mTimerHandler = new Handler();

	private final OnClickListener mStartClickListener = new OnClickListener() {
		public void onClick(View v) {
			mTimerHandler.removeCallbacks(mTimerUpdateTask);
			if (!isStarted) {
				mTimerHandler.postDelayed(mTimerUpdateTask, 0);
				isStarted = true;
			} else {
				showToast("Game is already started !!!");
			}

		}
	};

	private final Runnable mTimerUpdateTask = new Runnable() {
		public void run() {
			mTimerHandler.removeCallbacks(mTimerUpdateTask);
			mSecondCounter++;
			mSwitcher.setText(String.valueOf(mSecondCounter));
			mTimerHandler.postDelayed(this, 1000);// 1 sec
		}
	};

	private final OnClickListener mResetClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (isStarted) {
				mTimerHandler.removeCallbacks(mTimerUpdateTask);
				isStarted = false;

				mSecondCounter = 0;
				mSwitcher.setText(String.valueOf(mSecondCounter));
				
				mMoves = 0;
				mMovesText.setText("Moves : " + mMoves);

				mGridView.setAdapter(new GridViewAdapter(v.getContext()));
				mMatrix = new int[25];
			}
		}
	};

	/* ********************************************************************** */
	/* Generic toast method. */
	/* ********************************************************************** */
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public View makeView() {
		TextView t = new TextView(this);
		t.setGravity(Gravity.CENTER);
		t.setTextColor(Color.WHITE);
		t.setTextSize(100.0f);
		return t;
	}

	/**
	 * Grid view which holds the game board
	 * 
	 * @author badrinath
	 * 
	 */
	public class GridViewAdapter extends BaseAdapter {
		private Context mContext = null;
		private HashMap<Integer, Button> hm = new HashMap<Integer, Button>();

		public GridViewAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return 25;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Button buttonView;
			if (convertView == null) {
				// if it's not recycled, initialise some attributes
				buttonView = new Button(mContext);
				buttonView.setLayoutParams(new GridView.LayoutParams(70, 70));
				buttonView.setBackgroundResource(getId(1));
				buttonView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						if (!isStarted) {
							showToast("Start the game !!!");
							return;
						}

						Button imgView = (Button) v;
						imgView.setBackgroundResource(getId(mMatrix[position]));
						mMatrix[position] = 0 == mMatrix[position] ? 1 : 0;

						int pL = -1, pR = -1, pU = -1, pD = -1;
						if (0 != position % 5) {
							pL = position - 1;
							imgView = (Button) mGridView.getChildAt(pL);

							imgView.setBackgroundResource(getId(mMatrix[pL]));
							mMatrix[pL] = 0 == mMatrix[pL] ? 1 : 0;
						}
						if (4 != position % 5) {
							pR = position + 1;
							imgView = (Button) mGridView.getChildAt(pR);

							imgView.setBackgroundResource(getId(mMatrix[pR]));
							mMatrix[pR] = 0 == mMatrix[pR] ? 1 : 0;
						}
						if (0 != position / 5) {
							pU = position - 5;
							imgView = (Button) mGridView.getChildAt(pU);

							imgView.setBackgroundResource(getId(mMatrix[pU]));
							mMatrix[pU] = 0 == mMatrix[pU] ? 1 : 0;
						}
						if (4 != position / 5) {
							pD = position + 5;
							imgView = (Button) mGridView.getChildAt(pD);

							imgView.setBackgroundResource(getId(mMatrix[pD]));
							mMatrix[pD] = 0 == mMatrix[pD] ? 1 : 0;
						}

						mMoves++;
						mMovesText.setText("Moves : " + mMoves);
					}
				});

				hm.put(position, buttonView);
			} else {
				buttonView = (Button) convertView;
			}

			return buttonView;
		}

		private int getId(int i) {
			return 0 == i ? R.drawable.red1 : R.drawable.gray1;
		}

	}

	@Override
	public void onClick(View v) {
	}
}