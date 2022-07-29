package RiskSim;

import java.util.Arrays;
import java.util.Random;

public class Simulator {

	private static Random generator = new Random();

	private static int[] attackStrengthList = range(28+15+9);
	private static int[] defenderList = {2,1,1,1,1,1,1,6,1,1,1,1,1,1};

	private static int battle1att = 1;
	private static int battle2att = 3;
	private static int additional = 22;

	private static int[] defender1List = {3,1,2};
	private static int[] defender2List = {1,2};

	private static int maxTries = 100000;

	public static void main(String[] args) {
		for (int attackStrength : attackStrengthList) {
			successProb(attackStrength, defenderList);
		}
		System.out.println();
		System.out.println("optimal: " + optimal());
//		System.out.println(fullBattle(4,3));
		System.out.println(randsong(36, 38));
	}

	private static int optimal() {
		double maxprob = 0;
		int besti = -1;
		for (int i = 0; i <= additional; i++) {
			double prob = successProb(battle1att + i, defender1List)
					* successProb(battle2att + additional - i, defender2List);
			System.out.printf("%3.1f%%\n", 100*prob);
			if (prob > maxprob) {
				maxprob = prob;
				besti = i;
			}
		}
		return besti;
	}
	
	private static int[] range(int single) {
		return range(single, single+1);
	}
	
	private static int[] range(int start, int end) {
		int[] result = new int[end - start];
		for (int i = start; i < end; i++)
			result[i - start] = i;
		return result;
	}

	private static double successProb(int attackStrength, int[] defenderList) {
		int sum = 0;
		for (int v : defenderList)
			sum += v;
		System.out.printf("regions: %d armies: %d - ", defenderList.length, sum);

		int i = 0;
		int wins = 0;
		int losses = 0;
		int remain = 0;
		while (i < maxTries && !converged()) {
			i++;
			int single = campaign(attackStrength, defenderList);
			if (single > 0) {
				wins++;
				remain += single;
			} else
				losses++;
		}
		double result = wins / (double) (wins + losses);
		System.out.printf("attackers: %d, win: %3.1f%%, remaining: %.2f%n", attackStrength, 100 * result,
				remain / (double) wins);
		return result;
	}

	private static boolean converged() {
		return false;
	}

	private static int campaign(int attArmies, int[] defArmies) {
		for (int i = 0; i < defArmies.length; i++) {
			int remain = fullBattle(attArmies, defArmies[i]);
			if (remain == 1)
				return 0;
			attArmies = remain - 1;
		}
		return attArmies;
	}

	/**
	 * 
	 * @param armies - The first is the number of attacking armies; the second is
	 *               the number of defenders.
	 * @return - the number of attackers remaining (before occupying) If 1, the
	 *         attack has failed.
	 * 
	 */
	private static int fullBattle(int attArmies, int defArmies) {
		while (attArmies > 1 && defArmies > 0) {
			int[] res = battle(attArmies, defArmies);
			attArmies -= res[0];
			defArmies -= res[1];
		}
		return attArmies;
	}

	/**
	 * 
	 * @param armies - The first is the number of attacking armies; the second is
	 *               the number of defenders.
	 * @return - the number of attackers and defenders lost in the battle.
	 */
	private static int[] battle(int attArmies, int defArmies) {
		int attackDice = Math.min(3, attArmies - 1);
		int defendDice = Math.min(2, defArmies);
		int[] attackValues = new int[attackDice];
		for (int i = 0; i < attackDice; i++)
			attackValues[i] = roll(6);
		Arrays.sort(attackValues);
		reverse(attackValues);

		int[] defendValues = new int[defendDice];
		for (int i = 0; i < defendDice; i++)
			defendValues[i] = roll(6);
		Arrays.sort(defendValues);
		reverse(defendValues);

		int sig = Math.min(attackDice, defendDice);

		int attLost = 0;
		int defLost = 0;
		for (int i = 0; i < sig; i++) {
			if (attackValues[i] > defendValues[i])
				defLost += 1;
			else
				attLost += 1;
		}

		int[] result = new int[2];
		result[0] = attLost;
		result[1] = defLost;

		return result;
	}

	private static void reverse(int[] input) {
		int i, temp;
		int size = input.length;
		for (i = 0; i < size / 2; i++) {
			temp = input[i];
			input[i] = input[size - i - 1];
			input[size - i - 1] = temp;
		}
	}

	/**
	 *
	 * @param sides - the number of faces on the die to roll
	 * @return - a random number in the range
	 */
	private static int roll(int sides) {
		return 1 + generator.nextInt(sides);
	}

	private static int randsong(int curr, int max) {
		return curr + generator.nextInt(max-curr);
	}
}
