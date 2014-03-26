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
	private int bid;
	//    private int score;
	private Round round;
	private Spinner spPlayer;
	private ArrayAdapter<String> adapter;
	private String[] playerArray = new String[4];
	private DialogListener listener;
	private View v;

	static ScoreEditDialog newInstance(Round round, HashMap<String, String> players) {
		ScoreEditDialog dialog = new ScoreEditDialog();
		Bundle args = new Bundle();
		args.putSerializable("round", round);
		args.putSerializable("players", players);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		round = (Round) getArguments().getSerializable("round");
		HashMap<String, String> players = (HashMap<String, String>) getArguments().getSerializable("players");
		playerArray[0] = players.get("p1");
		playerArray[1] = players.get("p2");
		playerArray[2] = players.get("p3");
		playerArray[3] = players.get("p4");
		adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item,
				playerArray);

		v = View.inflate(getActivity(), R.layout.score_edit_dialog, null);
		etBid = (EditText) v.findViewById(R.id.etBid);
        etScore = (EditText) v.findViewById(R.id.etScore);
		spPlayer = (Spinner) v.findViewById(R.id.spinner);


		spPlayer.setAdapter(adapter);
		spPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Integer> player = round.getPlayer(position + 1);
				etBid.setText(String.valueOf(player.get("bid")));
                etScore.setText(String.valueOf(player.get("score")));
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
						round.setPlayer(spPlayer.getSelectedItemPosition() + 1, Integer.valueOf(etBid.getText().toString()),
								Integer.valueOf(etScore.getText().toString()));
						if (round.isCalculated() && !round.isScoreValid()) {
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
