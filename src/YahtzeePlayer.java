class YahtzeePlayer
{
	private String name;
	private int[] val, scores;
	
	YahtzeePlayer(String myName)
	{
		name = myName;
		val = new int[5];
		scores = new int[5];
	}
	
	public String name(){
		return name;
	}
	
	public void setVal(int[] d){
		for (int i = 0; i < 5; i++)
			val[i] = d[i];
	}
	
	//sort array of values so easier to evaluate
	public void setScores()
	{
		int i, j, temp;
		
		for (i = 0; i < val.length; i++) 
			for (j = 1; j < (val.length - i); j++) 
				if (val[j - 1] > val[j]) //swap
				{
					temp = val[j - 1];
					val[j - 1] = val[j];
					val[j] = temp;
				}
		
		int[] counts = new int[]{0,0,0,0,0,0};
		for (i = 0; i < 6; i++)
			for (j = 0; j < 5; j++)
				if (val[j] == i + 1) //+1 because index starts at 0
					counts[i]++;
			
		scores = new int[13];
		for (i = 0; i < 13; i++)
				scores[i] = 0;
			
		//0-5 = ones -> sixes, 6 = three o kind, 7 = four o kind, 8 = full house
		//9 = small straight, 10 = large straight, 11 = yahtzee, 12 = chance
		//Upper level scoring
		for (i = 0; i < 6; i++)
			scores[i] = counts[i] * (i+1);
		//three of a kind
		if (val[0] == val[2] || val[1] == val[3] || val[2] == val[4])
			scores[6] = addValues(val);
		//four of a kind
		if (val[0] == val[3] || val[1] == val[4])
			scores[7] = addValues(val);
		//full house
		if (((val[0] == val[2])&&(val[3] == val[4]))||
			 (val[0] == val[1])&&(val[2] == val[4]))
			scores[8] = 25;
		//straights
		if (checkStraights(val) == 9)
			scores[9] = 30;
		else if (checkStraights(val) == 10)
			scores[10] = 40;
		//yahtzee
		for (i = 0; i < 5; i++)	
			if (counts[i] == 5)
				scores[11] = 50;
		//chance
		scores[12] = addValues(val);
	}
		
	public int addValues(int values[])
	{
		int total = 0;
		for (int i = 0; i < 5; i++)
				total += values[i];
		return total;
	}
	
	public int checkStraights(int values[])
	{
		int i;
		boolean sStraight1 = true, sStraight2 = true, lStraight = true;
		//check for small straight
		for (i = 0; i < 3; i++)
			if (values[i] + 1 != values[i+1]) //doesnt all ascend by 1
				sStraight1 = false;
		for (i = 1; i < 4; i++)
			if (values[i] + 1 != values[i+1])
				sStraight2 = false;
		//check large straight
		for (i = 0; i < 4; i++)
			if (values[i] + 1 != values[i+1])
				lStraight = false;
		
		if (lStraight)
			return 10;
		else if (sStraight1 || sStraight2)
			return 9;
		else
			return 0;
	}
	public int[] getScores(){
		return scores;
	}
}
