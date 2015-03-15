package com.blade.elu.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eluvatar
 * Date: 9/8/12
 * Time: 1:36 AM
 */
public class Round implements Serializable {

	private HandScore player1Hand;
	private HandScore player2Hand;
	private HandScore player3Hand;
	private HandScore player4Hand;
	List<HandScore> handScores;
	//	private int player1Score;
//	private int player2Score;
//	private int player3Score;
//	private int player4Score;
//
//	private int player1Bid;
//	private int player2Bid;
//	private int player3Bid;
//	private int player4Bid;
	private boolean isFinished = false;

	private Round previousRound;
	private Round nextRound;

	public Round(Round previousRound) {
		this.previousRound = previousRound;
		if (previousRound != null) {
			previousRound.nextRound = this;
		}
	}

	public void Bid(int Player1, int Player2, int Player3, int Player4) {
		player1Hand = new HandScore(1, Player1);
		player2Hand = new HandScore(2, Player2);
		player3Hand = new HandScore(3, Player3);
		player4Hand = new HandScore(4, Player4);
		handScores = Arrays.asList(player1Hand, player2Hand, player3Hand, player4Hand);
	}

	public void Score(int Player1, int Player2, int Player3, int Player4) {
		player1Hand.setScore(Player1);
		player2Hand.setScore(Player2);
		player3Hand.setScore(Player3);
		player4Hand.setScore(Player4);
		isFinished = true;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public int getRoundBags() {
		int roundBags = 13;
		roundBags -= player1Hand.getAbsoluteBid();
		roundBags -= player2Hand.getAbsoluteBid();
		roundBags -= player3Hand.getAbsoluteBid();
		roundBags -= player4Hand.getAbsoluteBid();
		return roundBags;
	}

	public int getTeam1Net() {
		int prevBags = previousRound == null ? 0 : previousRound.getTeam1Bags();
		return getNet(player1Hand, player3Hand, prevBags + getTeam1NetBags() >= 5);
	}


	public int getTeam2Net() {
		int prevBags = previousRound == null ? 0 : previousRound.getTeam2Bags();
		return getNet(player2Hand, player4Hand, prevBags + getTeam2NetBags() >= 5);
	}

	private int getNet(HandScore playerA, HandScore playerB, boolean wentDownOnBags) {
		int net = playerA.getAbsoluteBid() + playerB.getAbsoluteBid();//not counting nill;
		boolean isSet = playerA.scoreContribute() + playerB.scoreContribute() < net;
		if (isSet) {
			net = -net;
		}
		net *= 10;
		net += wentDownOnBags ? -50 : 0;

		//we've already calculated non nill bids
		net += playerA.nillContribute();
		net += playerB.nillContribute();
		return net;
	}

	public int getTeam1Score() {
		if (previousRound == null) return getTeam1Net();
		return previousRound.getTeam1Score() + getTeam1Net();
	}

	public int getTeam2Score() {
		if (previousRound == null) return getTeam2Net();
		return previousRound.getTeam2Score() + getTeam2Net();
	}

	private int getTeam1Tricks() {
		return player1Hand.getAbsoluteTaken() + player3Hand.getAbsoluteTaken();
	}

	private int getTeam2Tricks() {
		return player2Hand.getAbsoluteTaken() + player4Hand.getAbsoluteTaken();
	}

	private int getTeam1Bid() {
		return player1Hand.getAbsoluteBid() + player3Hand.getAbsoluteBid();
	}

	private int getTeam2Bid() {
		return player2Hand.getAbsoluteBid() + player4Hand.getAbsoluteBid();
	}

	public int getTeam1NetBags() {
		return Math.max(getTeam1Tricks() - getTeam1Bid(), 0);
	}

	public int getTeam2NetBags() {
		return Math.max(getTeam2Tricks() - getTeam2Bid(), 0);
	}

	public int getTeam1Bags() {
		if (previousRound == null) return getTeam1NetBags();
		int bags = previousRound.getTeam1Bags() + getTeam1NetBags();
		return bags >= 5 ? 0 : bags;
	}

	public int getTeam2Bags() {
		if (previousRound == null) return getTeam2NetBags();
		int bags = previousRound.getTeam2Bags() + getTeam2NetBags();
		return bags >= 5 ? 0 : bags;
	}

	public boolean isScoreValid() {
		int val = 0;
		for (HandScore handScore : handScores) {
			val += handScore.getScore();
		}
		return val == 13;
	}

	public HandScore getPlayerHand(int playerNo) {
		switch (playerNo) {
			case 1:
				return player1Hand;
			case 2:
				return player2Hand;
			case 3:
				return player3Hand;
			case 4:
				return player4Hand;
		}
		return null;
	}

	public HandScore getPlayerHand(Player player) {
		return getPlayerHand(player.playerNumber);
	}

	public List<HandScore> getHandScores() {
		return handScores;
	}
}
