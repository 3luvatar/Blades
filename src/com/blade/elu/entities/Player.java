package com.blade.elu.entities;

import java.io.Serializable;

/**
 * Created by hahnk_000 on 12/31/2014.
 */
public class Player implements Serializable {
	String Name;
	Game game;
	int playerNumber;

	public Player(Game game, int playerNumber) {
		this.game = game;
		this.playerNumber = playerNumber;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@Override
	public String toString() {
		return Name;
	}

	public String getStats() {
		if (game.getRounds().isEmpty()) return "";
		double averageBid = 0;
		double averageScore = 0;
		int scoreCountOffset = 0;
		for (Round round : game.getRounds()) {
			averageScore += round.getPlayerHand(playerNumber).getScore();
			averageBid += Math.max(round.getPlayerHand(playerNumber).getBid(), 0);
			if (!round.isFinished()) {
				scoreCountOffset++;
			}
		}
		averageBid = averageBid / game.getRounds().size();
		averageScore = averageScore / (game.getRounds().size() - scoreCountOffset);
		return String.format("Bid: %.2f  Score: %.2f, Ratio: %.2f", averageBid, averageScore,
				averageScore / averageBid);
	}
}
