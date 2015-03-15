package com.blade.elu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.blade.elu.entities.Game;
import com.blade.elu.entities.HandScore;
import com.blade.elu.entities.Player;
import com.blade.elu.entities.Round;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eluvatar
 * Date: 10/7/12
 * Time: 2:17 PM
 */
public class ScoreEditDialog extends DialogFragment {
	EditText etScore;
	EditText etBid;
	private Round round;
	private Game game;
	private Spinner spPlayer;
	private ArrayAdapter<Player> adapter;
	private DialogListener listener;
	private View v;

	static ScoreEditDialog newInstance(Round round, Game game) {
		ScoreEditDialog dialog = new ScoreEditDialog();
		Bundle args = new Bundle();
		args.putSerializable("round", round);
		args.putSerializable("game", game);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		round = (Round) getArguments().getSerializable("round");
		game = (Game) getArguments().getSerializable("game");

		adapter = new ArrayAdapter<Player>(getActivity().getApplicationContext(), R.layout.spinner_item,
				game.getPlayers());

		v = View.inflate(getActivity(), R.layout.score_edit_dialog, null);
		etBid = (EditText) v.findViewById(R.id.etBid);
		etScore = (EditText) v.findViewById(R.id.etScore);
		etScore.setEnabled(round.isFinished());
		spPlayer = (Spinner) v.findViewById(R.id.spinner);


		spPlayer.setAdapter(adapter);
		spPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				HandScore handScore = round.getPlayerHand(position + 1);
				etBid.setText(String.valueOf(handScore.getBid()));
				etScore.setText(String.valueOf(handScore.getScore()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Change Player Bid");
		builder.setNeutralButton("Save", null);
		builder.setView(v);
		final AlertDialog d = builder.create();
		d.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Player player = (Player) spPlayer.getSelectedItem();
						HandScore handScore = round.getPlayerHand(player);
						handScore.setBid(Integer.valueOf(etBid.getText().toString()));
						handScore.setScore(Integer.valueOf(etScore.getText().toString()));
						if (round.isFinished() && !round.isScoreValid()) {
							Toast.makeText(getActivity(), "error score not valid", Toast.LENGTH_SHORT).show();
							return;
						}
						listener.onComplete();
						d.dismiss();
					}
				});
			}
		});
		return d;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container != null) container.addView(v);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setDialogListener(DialogListener listener) {
		this.listener = listener;
	}

	public interface DialogListener {
		public void onComplete();
	}


}
