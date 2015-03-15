package com.blade.elu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.blade.elu.entities.Game;
import com.blade.elu.entities.HandScore;
import com.blade.elu.entities.Player;
import com.blade.elu.entities.Round;

import java.util.ArrayList;

public class BladesActivity extends FragmentActivity {

	/**
	 * Called when the activity is first created.
	 */

	private TextView quickBags;
	private Button btDo;
	private ArrayList<NumberPicker> bidPicker;
	private OnClickListener bidListener;
	private OnClickListener scoreListener;
	private ListView rowList;
	private ListView playerList;
	private DrawerLayout drawerLayout;
	private ArrayAdapter<Round> roundAdapter;
	private ArrayAdapter<Player> playerAdapter;
	private Game game;
	private String[] scoreArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
	private String[] biddingArray = {"B Nill", "Nill", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			game = (Game) savedInstanceState.getSerializable("game");
		} else {
			game = (Game) getIntent().getSerializableExtra("game");
		}

		setContentView(R.layout.main_layout);

		getActionBar().hide();

		final TextView player1 = (TextView) findViewById(R.id.player1);
		final TextView player2 = (TextView) findViewById(R.id.player2);
		final TextView player3 = (TextView) findViewById(R.id.player3);
		final TextView player4 = (TextView) findViewById(R.id.player4);

		btDo = (Button) findViewById(R.id.buttonDo);
		quickBags = (TextView) findViewById(R.id.quickBags);
		rowList = (ListView) findViewById(R.id.rowContainer);
		playerList = (ListView) findViewById(R.id.left_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		bidPicker = new ArrayList<NumberPicker>();
		bidPicker.add((NumberPicker) findViewById(R.id.player1Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player2Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player3Picker));
		bidPicker.add((NumberPicker) findViewById(R.id.player4Picker));


		player1.setText(game.getPlayer1().getName());
		player2.setText(game.getPlayer2().getName());
		player3.setText(game.getPlayer3().getName());
		player4.setText(game.getPlayer4().getName());

		drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerClosed(View drawerView) {
				player1.setText(game.getPlayer1().getName());
				player2.setText(game.getPlayer2().getName());
				player3.setText(game.getPlayer3().getName());
				player4.setText(game.getPlayer4().getName());
			}
		});

		bidListener = new OnClickListener() {
			public void onClick(View view) {
				bid();
			}
		};
		scoreListener = new OnClickListener() {
			public void onClick(View view) {
				if (!is13()) {
					Toast.makeText(getApplicationContext(), "Score must equal 13, currently: " + currentTotal(),
							Toast.LENGTH_SHORT).show();
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

		roundAdapter = new ArrayAdapter<Round>(this, 0, game.getRounds()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = getLayoutInflater().inflate(R.layout.view_row, null);
				Round round = getItem(position);
				setupRow(round, convertView);
				return convertView;
			}
		};

		rowList.setAdapter(roundAdapter);
		rowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//if you clicked the last row and we have not already scored the round
				final Round row = roundAdapter.getItem(position);
				ScoreEditDialog dialog = ScoreEditDialog.newInstance(row, game);
				dialog.setDialogListener(new ScoreEditDialog.DialogListener() {
					@Override
					public void onComplete() {
						roundAdapter.notifyDataSetChanged();
					}
				});
				dialog.show(getFragmentManager(), "dialog");
			}
		});

		playerAdapter = new ArrayAdapter<Player>(this, 0, game.getPlayers()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = getLayoutInflater().inflate(R.layout.player_layout, parent, false);
				TextView playerName = (TextView) convertView.findViewById(R.id.playerName);
				TextView playerAverage = (TextView) convertView.findViewById(R.id.playerAverageBid);
				Player player = getItem(position);
				playerName.setText(player.getName());
				playerAverage.setText(player.getAverage());
				return convertView;
			}
		};

		playerList.setAdapter(playerAdapter);
		playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Player player = (Player) parent.getItemAtPosition(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(BladesActivity.this);
				builder.setTitle(String.format("Edit Player %d Name", player.getPlayerNumber()));

				// Use an EditText view to get user input.
				final EditText input = new EditText(BladesActivity.this);
				input.setText(player.getName());
				builder.setView(input);
				builder.setNegativeButton("Cancel", null);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						player.setName(input.getText().toString());
						playerAdapter.notifyDataSetChanged();
					}
				});
				builder.show();
			}
		});
		setupUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable("game", game);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		AlertDialog dialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Closing")
				.setMessage("Are you sure you want to close?")
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
		switch (game.getRoundState()) {
			case bidding:
				btDo.setText("Bid");
				setupPickers(biddingArray, 2);
				btDo.setOnClickListener(bidListener);
				quickBags.setVisibility(View.VISIBLE);
				break;
			case scoring:
				btDo.setText("Score");
				setupPickers(scoreArray, 0);
				btDo.setOnClickListener(scoreListener);
				quickBags.setVisibility(View.INVISIBLE);
				break;
		}
		playerList.invalidateViews();
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
		for (HandScore handScore : round.getHandScores()) {
			String bid = String.valueOf(handScore.getBid());
			String score = round.isFinished() ? String.valueOf(handScore.getScore()) : "";
			if (bid.equals("-1")) {
				bid = "Nill";
			} else if (bid.equals("-2")) {
				bid = "B Nill";
			}
			switch (handScore.getPlayerNo()) {
				case 1:
					bidPlayer1.setText(bid);
					scorePlayer1.setText(score);
					break;
				case 2:
					bidPlayer2.setText(bid);
					scorePlayer2.setText(score);
					break;
				case 3:
					bidPlayer3.setText(bid);
					scorePlayer3.setText(score);
					break;
				case 4:
					bidPlayer4.setText(bid);
					scorePlayer4.setText(score);
					break;
			}

		}


		bagView.setText("Bags " + round.getRoundBags());
		if (round.isFinished()) {
			netTeam1.setText((round.getTeam1Net() > 0 ? "+" + round.getTeam1Net() : round.getTeam1Net()) +
			                 (round.getTeam1NetBags() > 0 ? " B: +" + round.getTeam1NetBags() : ""));
			netTeam2.setText((round.getTeam2Net() > 0 ? "+" + round.getTeam2Net() : round.getTeam2Net()) +
			                 (round.getTeam2NetBags() > 0 ? " B: +" + round.getTeam2NetBags() : ""));

			scoreTeam1View.setText("Score " + round.getTeam1Score() + " B:" + round.getTeam1Bags());
			scoreTeam2View.setText("Score " + round.getTeam2Score() + " B:" + round.getTeam2Bags());

		} else {
			netTeam1.setText("");
			netTeam2.setText("");

			scoreTeam1View.setText("");
			scoreTeam2View.setText("");
		}
	}

	void bid() { // on bid click
		Round newRound = game.newRound();
		newRound.Bid(bidPicker.get(0).getValue() - 2, bidPicker.get(1).getValue() - 2, bidPicker.get(2).getValue() - 2,
				bidPicker.get(3).getValue() - 2);
		roundAdapter.notifyDataSetChanged();
		setupUI();
	}

	void score() {
		game.getCurrentRound()
				.Score(bidPicker.get(0).getValue(), bidPicker.get(1).getValue(), bidPicker.get(2).getValue(),
						bidPicker.get(3).getValue());
		roundAdapter.notifyDataSetChanged();
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
		return currentTotal() == 13;
	}

	private int currentTotal() {
		return bidPicker.get(0).getValue() + bidPicker.get(1).getValue() + bidPicker.get(2).getValue() +
		       bidPicker.get(3).getValue();
	}

	private void calcBidBags() {
		if (quickBags == null || quickBags.getVisibility() != View.VISIBLE) return;
		int bags = 13;
		for (NumberPicker numberPicker : bidPicker) {
			bags -= Math.max(0, numberPicker.getValue() - 2);
		}
		quickBags.setText("Bags:" + bags);
	}
}