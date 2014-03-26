package com.blade.elu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BladesActivity extends Activity {

	final private int STATE_BIDDING = 0;
	final private int STATE_SCORING = 1;
	/**
	 * Called when the activity is first created.
	 */

	private TextView quickBags;
	private int scoreTeam1 = 0;
	private int scoreTeam2 = 0;
	private int bagsTeam1 = 0;
	private int bagsTeam2 = 0;
	private Button btDo;
	private ArrayList<NumberPicker> bidPicker;
	private OnClickListener bidListener;
	private OnClickListener scoreListener;
	private ListView rowList;
	private ArrayList<Round> roundList = new ArrayList<Round>();
	private ArrayAdapter<Round> adapter;
	private Round currentRound;
	private int btAction;
	private boolean isSetup = false;
	private String[] scoreArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
	private String[] biddingArray = {"B Nill", "Nill", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			btAction = savedInstanceState.getInt("doBtState");
			scoreTeam1 = savedInstanceState.getInt("scoreTeam1");
			scoreTeam2 = savedInstanceState.getInt("scoreTeam2");
			bagsTeam1 = savedInstanceState.getInt("bagsTeam1");
			bagsTeam2 = savedInstanceState.getInt("bagsTeam2");
			roundList.clear();
			roundList.addAll((ArrayList<Round>) savedInstanceState.getSerializable("round_list"));
			currentRound = !roundList.isEmpty() ? roundList.get(roundList.size() - 1) : null;
		}

		setContentView(R.layout.score_sheet);
		getActionBar().hide();

		TextView player1 = (TextView) findViewById(R.id.player1);
		TextView player2 = (TextView) findViewById(R.id.player2);
		TextView player3 = (TextView) findViewById(R.id.player3);
		TextView player4 = (TextView) findViewById(R.id.player4);

		btDo = (Button) findViewById(R.id.buttonDo);
		quickBags = (TextView) findViewById(R.id.quickBags);
		rowList = (ListView) findViewById(R.id.rowContainer);
		bidPicker = new ArrayList<NumberPicker>();
		bidPicker.add((NumberPicker) findViewById(R.id.player1Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player2Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player3Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player4Picker));

		final HashMap<String, String> players = (HashMap<String, String>) getIntent().getSerializableExtra("players");
		player1.setText(players.get("p1"));
		player2.setText(players.get("p2"));
		player3.setText(players.get("p3"));
		player4.setText(players.get("p4"));

		bidListener = new OnClickListener() {
			public void onClick(View view) {
				bid();
			}
		};
		scoreListener = new OnClickListener() {
			public void onClick(View view) {
				if (!is13()) {
					Toast.makeText(getApplicationContext(), "Score must equal 13", Toast.LENGTH_SHORT).show();
					return;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(BladesActivity.this);
				builder.setMessage("Score Round?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						score();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				});
				builder.show();
			}
		};

		adapter = new ArrayAdapter<Round>(this, 0, roundList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = getLayoutInflater().inflate(R.layout.view_row, null);
				Round round = getItem(position);
				setupRow(round, convertView);
				return convertView;
			}
		};

		rowList.setAdapter(adapter);
		rowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//if you clicked the last row and we have not already scored the round
				final Round row = roundList.get(position);
				ScoreEditDialog dialog = ScoreEditDialog.newInstance(row, players);
				dialog.setDialogListener(new ScoreEditDialog.DialogListener() {
					@Override
					public void onComplete() {
						adapter.notifyDataSetChanged();
					}
				});
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		setupUI();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("isSetup", isSetup);
		savedInstanceState.putSerializable("round_list", roundList);
		savedInstanceState.putInt("doBtState", btAction);
		savedInstanceState.putInt("scoreTeam1", scoreTeam1);
		savedInstanceState.putInt("scoreTeam2", scoreTeam2);
		savedInstanceState.putInt("bagsTeam1", bagsTeam1);
		savedInstanceState.putInt("bagsTeam2", bagsTeam2);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		AlertDialog dialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Closing Activity")
				.setMessage("Are you sure you want to close this activity?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				})
				.create();
		dialog.show();
	}

	private void setupUI() {
		switch (btAction) {
			case STATE_BIDDING:
				btDo.setText("Bid");
				setupPickers(biddingArray, 2);
				btDo.setOnClickListener(bidListener);
				quickBags.setVisibility(View.VISIBLE);
				break;
			case STATE_SCORING:
				btDo.setText("Score");
				setupPickers(scoreArray, 0);
				btDo.setOnClickListener(scoreListener);
				quickBags.setVisibility(View.INVISIBLE);
				break;
		}
	}

	private void setupRow(Round round, View convertView) {
		TextView scorePlayer1 = (TextView) convertView.findViewById(R.id.scorePlayer1);
		TextView scorePlayer2 = (TextView) convertView.findViewById(R.id.scorePlayer2);
		TextView scorePlayer3 = (TextView) convertView.findViewById(R.id.scorePlayer3);
		TextView scorePlayer4 = (TextView) convertView.findViewById(R.id.scorePlayer4);

		TextView bidPlayer1 = (TextView) convertView.findViewById(R.id.bidPlayer1);
		TextView bidPlayer2 = (TextView) convertView.findViewById(R.id.bidPlayer2);
		TextView bidPlayer3 = (TextView) convertView.findViewById(R.id.bidPlayer3);
		TextView bidPlayer4 = (TextView) convertView.findViewById(R.id.bidPlayer4);

		TextView netTeam1 = (TextView) convertView.findViewById(R.id.netTeam1);
		TextView netTeam2 = (TextView) convertView.findViewById(R.id.netTeam2);

		TextView scoreTeam1View = (TextView) convertView.findViewById(R.id.scoreTeam1);
		TextView scoreTeam2View = (TextView) convertView.findViewById(R.id.scoreTeam2);
		TextView bagView = (TextView) convertView.findViewById(R.id.bags);

		for (int i = 1; i < 5; i++) {
			String value = "";
			value = round.getPlayer(i).get("bid").toString();
			if (value.equals("-1")) {
				value = "Nill";
			} else if (value.equals("-2")) {
				value = "B Nill";
			}
			switch (i) {
				case 1:
					bidPlayer1.setText(value);
					break;
				case 2:
					bidPlayer2.setText(value);
					break;
				case 3:
					bidPlayer3.setText(value);
					break;
				case 4:
					bidPlayer4.setText(value);
					break;
			}
		}

		bagView.setText("Bags " + round.getRoundBags());
		if (round.isCalculated()) {
			scorePlayer1.setText(String.valueOf(round.getPlayer1Score()));
			scorePlayer2.setText(String.valueOf(round.getPlayer2Score()));
			scorePlayer3.setText(String.valueOf(round.getPlayer3Score()));
			scorePlayer4.setText(String.valueOf(round.getPlayer4Score()));

			netTeam1.setText((round.getTeam1Net() > 0 ? "+" + round.getTeam1Net() : round.getTeam1Net()) +
			                 (round.getTeam1NetBags() > 0 ? " B: +" + round.getTeam1NetBags() : ""));
			netTeam2.setText((round.getTeam2Net() > 0 ? "+" + round.getTeam2Net() : round.getTeam2Net()) +
			                 (round.getTeam2NetBags() > 0 ? " B: +" + round.getTeam2NetBags() : ""));

			scoreTeam1View.setText("Score " + round.getTeam1Score() + " B:" + round.getTeam1Bags());
			scoreTeam2View.setText("Score " + round.getTeam2Score() + " B:" + round.getTeam2Bags());

		} else {
			scorePlayer1.setText("");
			scorePlayer2.setText("");
			scorePlayer3.setText("");
			scorePlayer4.setText("");

			netTeam1.setText("");
			netTeam2.setText("");

			scoreTeam1View.setText("");
			scoreTeam2View.setText("");
		}
	}

	void bid() { // on bid click
		currentRound = new Round(currentRound);
		currentRound.Bid(bidPicker.get(0).getValue() - 2, bidPicker.get(1).getValue() - 2,
				bidPicker.get(2).getValue() - 2, bidPicker.get(3).getValue() - 2);
		roundList.add(currentRound);
		adapter.notifyDataSetChanged();

		btAction = STATE_SCORING;
		setupUI();
	}

	void score() {
		currentRound.Score(bidPicker.get(0).getValue(), bidPicker.get(1).getValue(), bidPicker.get(2).getValue(),
				bidPicker.get(3).getValue());
		adapter.notifyDataSetChanged();
		scoreTeam1 = currentRound.getTeam1Score();
		scoreTeam2 = currentRound.getTeam2Score();
		bagsTeam1 = currentRound.getTeam1Bags();
		bagsTeam2 = currentRound.getTeam2Bags();

		btAction = STATE_BIDDING;
		setupUI();
	}

	private void setupPickers(String[] array, int defaultValue) {

		for (NumberPicker picker : bidPicker) {
			picker.setDisplayedValues(array);
			picker.setMinValue(0);
			picker.setMaxValue(array.length - 1);
			picker.setValue(defaultValue);
			picker.setWrapSelectorWheel(false);
			picker.setClickable(false);
			picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				public void onValueChange(NumberPicker numberPicker, int i, int i1) {
					calcBidBags();
				}
			});
		}
		calcBidBags();
	}

	private boolean is13() {
		int i1 = bidPicker.get(0).getValue();
		int i2 = bidPicker.get(1).getValue();
		int i3 = bidPicker.get(2).getValue();
		int i4 = bidPicker.get(3).getValue();
		return i1 + i2 + i3 + i4 == 13;
	}

	private void calcBidBags() {
		if (quickBags == null || quickBags.getVisibility() != View.VISIBLE) return;
		int bags = 13;
		bags -= Math.max(bidPicker.get(0).getValue() - 2, 0);
		bags -= Math.max(bidPicker.get(1).getValue() - 2, 0);
		bags -= Math.max(bidPicker.get(2).getValue() - 2, 0);
		bags -= Math.max(bidPicker.get(3).getValue() - 2, 0);
		quickBags.setText("Bags:" + bags);
	}
}