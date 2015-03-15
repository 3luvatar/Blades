package com.blade.elu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.blade.elu.entities.Game;

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
				Game game = new Game();
				game.getPlayer1().setName(((EditText) findViewById(R.id.player1)).getText().toString());
				game.getPlayer2().setName(((EditText) findViewById(R.id.player2)).getText().toString());
				game.getPlayer3().setName(((EditText) findViewById(R.id.player3)).getText().toString());
				game.getPlayer4().setName(((EditText) findViewById(R.id.player4)).getText().toString());
				Intent i = new Intent(TeamSetup.this, BladesActivity.class);
				i.putExtra("game", game);
				startActivity(i);
				finish();
			}
		});
		super.onCreate(savedInstanceState);
	}
}