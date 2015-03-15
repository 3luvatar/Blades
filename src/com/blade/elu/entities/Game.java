package com.blade.elu.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hahnk_000 on 12/31/2014.
 */
public class Game implements Serializable {

	List<Player> players;
	Calendar startTime;
	List<Round> rounds = new ArrayList<Round>();

	public Game() {
		players = Arrays.asList(new Player(this, 1), new Player(this, 2), new Player(this, 3), new Player(this, 4));
		startTime = Calendar.getInstance();
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public Player getPlayer1() {
		return players.get(0);
	}

	public Player getPlayer2() {
		return players.get(1);
	}

	public Player getPlayer3() {
		return players.get(2);
	}

	public Player getPlayer4() {
		return players.get(3);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Round getCurrentRound() {
		return !rounds.isEmpty() ? rounds.get(rounds.size() - 1) : null;
	}

	public RoundState getRoundState() {
		Round currentRound = getCurrentRound();
		return currentRound == null || currentRound.isFinished() ? RoundState.bidding : RoundState.scoring;
	}

	public enum RoundState {
		bidding,
		scoring
	}

	public Round newRound() {
		Round newRound = new Round(getCurrentRound());
		rounds.add(newRound);
		return newRound;
	}

}
