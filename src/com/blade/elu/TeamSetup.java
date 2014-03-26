package com.blade.elu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: hahnk_000
 * Date: 12/22/12
 * Time: 2:49 PM
 */
public class TeamSetup extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.teampicker);
		Button btTeamSet = (Button) findViewById(R.id.set_team);
		btTeamSet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				HashMap<String, String> players = new HashMap<String, String>();
				players.put("p1", ((EditText) findViewById(R.id.player1)).getText().toString());
				players.put("p2", ((EditText) findViewById(R.id.player2)).getText().toString());
				players.put("p3", ((EditText) findViewById(R.id.player3)).getText().toString());
				players.put("p4", ((EditText) findViewById(R.id.player4)).getText().toString());
				Intent i = new Intent(TeamSetup.this, BladesActivity.class);
				i.putExtra("players", players);
				startActivity(i);
				finish();
			}
		});
		super.onCreate(savedInstanceState);
	}
}