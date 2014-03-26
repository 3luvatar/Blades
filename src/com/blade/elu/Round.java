package com.blade.elu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eluvatar
 * Date: 9/8/12
 * Time: 1:36 AM
 */
public class Round implements Serializable {

	private int player1Score;
	private int player2Score;
	private int player3Score;
	private int player4Score;

	private int player1Bid;
	private int player2Bid;
	private int player3Bid;
	private int player4Bid;
	private boolean isCalculated = false;

	private Round previousRound;
	private Round nextRound;

	public Round(Round previousRound) {
		this.previousRound = previousRound;
		if (previousRound != null) {
			previousRound.nextRound = this;
		}
	}

	public void Bid(int Player1, int Player2, int Player3, int Player4) {
		setPlayer1Bid(Player1);//bags are calculated automatically via the setters
		setPlayer2Bid(Player2);//team bid is also set
		setPlayer3Bid(Player3);
		setPlayer4Bid(Player4);

	}

	public void Score(int Player1, int Player2, int Player3, int Player4) {
		setPlayer1Score(Player1);
		setPlayer2Score(Player2);
		setPlayer3Score(Player3);
		setPlayer4Score(Player4);
		isCalculated = true;
	}

	private void setIsCalculated(boolean bol) {
		isCalculated = bol;
	}

	public boolean isCalculated() {
		return isCalculated;
	}

	public int getRoundBags(){
		int roundBags = 13;
		roundBags -= Math.max(player1Bid, 0);
		roundBags -= Math.max(player2Bid, 0);
		roundBags -= Math.max(player3Bid, 0);
		roundBags -= Math.max(player4Bid, 0);
		return roundBags;
	}

	public int getTeam1Net() {
		int prevBags = previousRound == null ? 0 : previousRound.getTeam1Bags();
		return getNet(getPlayer1Bid(), getPlayer1Score(), getPlayer3Bid(), getPlayer3Score(), prevBags + getTeam1NetBags() >= 5);
	}


	public int getTeam2Net() {
		int prevBags = previousRound == null ? 0 : previousRound.getTeam2Bags();
		return getNet(getPlayer2Bid(), getPlayer2Score(), getPlayer4Bid(), getPlayer4Score(), prevBags + getTeam2NetBags() >= 5);
	}

	private int getNet(int playerABid, int playerAScore, int playerBBid, int playerBScore, boolean wentDownOnBags) {
		int net = (Math.max(playerABid, 0) + Math.max(playerBBid, 0));//not counting nill;
		boolean isSet = (playerABid >=0 ? playerAScore : 0) + (playerBBid >=0 ? playerBScore : 0) < net;
		if (isSet) {
			net = -net;
		}
		net *= 10;
		net += wentDownOnBags ? -50 : 0;

		//we've already calculated non nill bids
		if (playerABid < 0) {
			net += playerAScore == 0 ? calcPotential(playerABid) : -calcPotential(playerABid) ;
		}
		if (playerBBid < 0) {
			net += playerBScore == 0 ? calcPotential(playerBBid) : -calcPotential(playerBBid);
		}
		return net;
	}

	private int calcPotential(int bid) {
		switch (bid) {
			case -2:
				return 200;
			case -1:
				return 100;
			default:
				return bid * 10;
		}
	}

	public int getTeam1Score() {
		if (previousRound == null) return getTeam1Net();
		return previousRound.getTeam1Score() + getTeam1Net();
	}

	public int getTeam2Score() {
		if (previousRound == null) return getTeam2Net();
		return previousRound.getTeam2Score() + getTeam2Net();
	}

	private int getTeam1Tricks(){
		return (player1Bid < 0 ? 0 : player1Score) + (player3Bid < 0 ? 0 : player3Score);
	}

	private int getTeam2Tricks(){
		return (player2Bid < 0 ? 0 : player2Score) + (player4Bid < 0 ? 0 : player4Score);
	}

	private int getTeam1Bid(){
		return Math.max(player1Bid, 0) + Math.max(player3Bid, 0);
	}

	private int getTeam2Bid(){
		return Math.max(player2Bid, 0) + Math.max(player4Bid, 0);
	}

	public int getTeam1NetBags() {
		return Math.max(getTeam1Tricks() - getTeam1Bid(), 0);
	}

	public int getTeam2NetBags() {
		return Math.max(getTeam2Tricks() - getTeam2Bid(), 0);
	}

	public int getTeam1Bags(){
		if (previousRound == null) return getTeam1NetBags();
		int bags = previousRound.getTeam1Bags() + getTeam1NetBags();
		return bags >= 5 ? 0 : bags;
	}

	public int getTeam2Bags(){
		if (previousRound == null) return getTeam2NetBags();
		int bags = previousRound.getTeam2Bags() + getTeam2NetBags();
		return bags >= 5 ? 0 : bags;
	}



	public int getPlayer1Bid() {
		return player1Bid;
	}

	void setPlayer1Bid(int player1Bid) {
		this.player1Bid = player1Bid;
	}

	public int getPlayer2Bid() {
		return player2Bid;
	}

	void setPlayer2Bid(int player2Bid) {
		this.player2Bid = player2Bid;
	}

	public int getPlayer3Bid() {
		return player3Bid;
	}

	void setPlayer3Bid(int player3Bid) {
		this.player3Bid = player3Bid;
	}

	public int getPlayer4Bid() {
		return player4Bid;
	}

	void setPlayer4Bid(int player4Bid) {
		this.player4Bid = player4Bid;
	}

	public int getPlayer1Score() {
		return player1Score;
	}

	void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public int getPlayer3Score() {
		return player3Score;
	}

	void setPlayer3Score(int player3Score) {
		this.player3Score = player3Score;
	}

	public int getPlayer4Score() {
		return player4Score;
	}

	void setPlayer4Score(int player4Score) {
		this.player4Score = player4Score;
	}

	public void setPlayer(int i, int bid, int score) {
		switch (i) {
			case 1:
				setPlayer1Bid(bid);
				setPlayer1Score(score);
				break;
			case 2:
				setPlayer2Bid(bid);
				setPlayer2Score(score);
				break;
			case 3:
				setPlayer3Bid(bid);
				setPlayer3Score(score);
				break;
			case 4:
				setPlayer4Bid(bid);
				setPlayer4Score(score);
				break;
		}
	}

	public boolean isScoreValid() {
		return getPlayer1Score() + getPlayer2Score() + getPlayer3Score() + getPlayer4Score() == 13;
	}

	public Map<String, Integer> getPlayer(int i) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		switch (i) {
			case 1:
				map.put("score", getPlayer1Score());
				map.put("bid", getPlayer1Bid());
				break;
			case 2:
				map.put("score", getPlayer2Score());
				map.put("bid", getPlayer2Bid());
				break;
			case 3:
				map.put("score", getPlayer3Score());
				map.put("bid", getPlayer3Bid());
				break;
			case 4:
				map.put("score", getPlayer4Score());
				map.put("bid", getPlayer4Bid());
				break;
		}
		return map;
	}
}
