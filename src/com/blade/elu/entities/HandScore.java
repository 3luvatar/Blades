package com.blade.elu.entities;

import java.io.Serializable;

/**
 * Created by hahnk_000 on 12/31/2014.
 */
public class HandScore implements Serializable {
	int playerNo;
	int bid;
	int score;

	public HandScore(int playerNo, int bid) {
		this.playerNo = playerNo;
		this.bid = bid;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getAbsoluteBid() {
		return Math.max(bid, 0);
	}

	public boolean isNill() {
		return bid < 0;
	}

	public int getAbsoluteTaken() {
		return bid < 0 ? 0 : score;
	}

	public int scoreContribute(){
		if (isNill()) return 0;
		return score;
	}

	public int nillContribute(){
		if (!isNill()) return 0;
		int multiplier = score == 0 ? -100 : 100;
		return bid * multiplier;
	}
}
