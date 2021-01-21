import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FarmPermutation
{
    private Set<String> cachedFarms; //all farms that have already been seen //TODO compare static and nonstatic runtime; static will require saving gold amount with the String
    private ArrayList<Farm> farmPermutations; //all unique farms with maximized spending
    private final ArrayList<Crop> validCrops;
    private final Farm farm; //the base farm to make all permutations of
    private int leastExpensiveCropValue;

    public FarmPermutation(Farm farm, ArrayList<Crop> validCrops)
    {
        this.farm = farm;
        cachedFarms = new HashSet<>();
        farmPermutations = new ArrayList<>();

        //filter out seeds you don't have enough gold to purchase
        //you're guaranteed to be able to purchase at least one
        //seed at this point, as Farm.java handles the case that
        //you cannot purchase any seeds
        this.validCrops = new ArrayList<>(validCrops);

        ArrayList<Crop> expensiveSeeds = new ArrayList<>();
        for (Crop crop : this.validCrops)
        {
            if (crop.getBuyPrice() > farm.getGold())
            {
                expensiveSeeds.add(crop);
            }
        }
        this.validCrops.removeAll(expensiveSeeds);

        leastExpensiveCropValue = this.validCrops.get(this.validCrops.size()-1).getBuyPrice();
    }

    public ArrayList<Farm> calculateFarmPermutations()
    {
        boolean original = false;

        if (!original)
        {
            int[] numEachSeed = new int[validCrops.size()];

            if (validCrops.size() == 1)
            {
                numEachSeed[0] = farm.getGold()/validCrops.get(0).getBuyPrice();
                int gold = farm.getGold() - (numEachSeed[0]*validCrops.get(0).getBuyPrice());
                addFarm(numEachSeed, gold);
                return farmPermutations;
            }
            
            //generate all valid solutions for the first column
            int maxFirstColumn = farm.getGold()/validCrops.get(0).getBuyPrice();

            for (int i = 0; i < maxFirstColumn+1; i++)
            {
                numEachSeed[0] = (farm.getGold()/validCrops.get(0).getBuyPrice())-i;
                int gold = farm.getGold() - (numEachSeed[0]*validCrops.get(0).getBuyPrice());

                for (int j = 1; j < numEachSeed.length; j++)
                {
                    if (gold >= leastExpensiveCropValue)
                    {
                        numEachSeed[j] = gold/validCrops.get(j).getBuyPrice();
                        gold -= (gold/validCrops.get(j).getBuyPrice())*validCrops.get(j).getBuyPrice();
                    }
                    else
                    {
                        break;
                    }
                }
                addFarm(numEachSeed, gold);
                
                for (int j = 1; j < numEachSeed.length-1; j++)
                {
                    if (numEachSeed[j] != 0)
                    {
                        permutate2(numEachSeed.clone(), gold, j);
                        break;
                    }
                }
            }
        }
        else
        {
            int[] numEachSeed = new int[validCrops.size()]; //the number of seeds to plant for each crop
            permutate(numEachSeed, farm.getGold());
        }

        return farmPermutations;
    }

    /**
     * This function directly calculates all permutations of a farm using recursion.
     * 
     * The function iterates down each index, reducing the number and spawning more
     * recursive calls until each index hits (but the last one) hits zero.
     * 
     * @param numEachSeed The number of each valid crop to buy and plant
     * @param gold The amount of gold this farm currently has
     */
    public void permutate2(int[] numEachSeed, int gold, int index)
    {
        int counter = numEachSeed[index];
        for (int i = 0; i < counter; i++)
        {
            numEachSeed[index]--;
            gold += validCrops.get(index).getBuyPrice();

            //get your gold back from the other indexes
            for (int j = index+1; j < numEachSeed.length; j++)
            {
                gold += numEachSeed[j]*validCrops.get(j).getBuyPrice();
                numEachSeed[j] = 0;
            }

            for (int j = index+1; j < numEachSeed.length; j++)
            {
                if (gold >= leastExpensiveCropValue)
                {
                    numEachSeed[j] = gold/validCrops.get(j).getBuyPrice();
                    gold -= (gold/validCrops.get(j).getBuyPrice())*validCrops.get(j).getBuyPrice();
                }
                else
                {
                    break;
                }
            }
            addFarm(numEachSeed, gold);

            for (int j = index+1; j < numEachSeed.length-1; j++)
            {
                if (numEachSeed[j] != 0)
                {
                    permutate2(numEachSeed.clone(), gold, j);
                    break;
                }
            }
        }
    }

    public void addFarm(int[] numEachSeed, int gold)
    {
        //construct a Farm from the seeds to plant
        ArrayList<CropGroup> seedsToPlant = new ArrayList<>();
        for (int i = 0; i < numEachSeed.length; i++)
        {
            if (numEachSeed[i] > 0)
            {
                seedsToPlant.add(new CropGroup(validCrops.get(i), numEachSeed[i]));
            }
        }

        //add the new seeds to the farm
        ArrayList<CropGroup> currentCrops = farm.getCrops(); //the current crops on the farm
        ArrayList<CropGroup> crops = new ArrayList<>(); //all the crops, with the new seeds, on the farm

        //add the current crops
        for (int i = 0; i < currentCrops.size(); i++)
        {
            crops.add(currentCrops.get(i).clone());
        }

        //add the new seeds
        crops.addAll(seedsToPlant);

        //logger
        FarmEvent event = farm.getEvents().get(farm.getEvents().size()-1);
        event.setSeedsPurchased(seedsToPlant);
        event.setEndingGold(gold, farm.getGoldCache());

        //unique permutation
        farmPermutations.add(new Farm(farm.getCropTypes(), crops, gold, farm.getGoldCache(), farm.getDaysRemaining()-1, farm.getEvents()));
    }

    /**
     * This function uses a special type of processing known as recursive memoization.
     * 
     * Every type of farm is generated, investing money in one crop at a time and
     * working down the tree until there is no money left. Every farm is saved to a
     * cache. If the farm that is generated is already in the cache, it is ignored.
     * 
     * Once a farm can no longer invest in any more crops (and it is not already seen
     * in the cache), it is a unique permutation and can be saved.
     * 
     * Saving involves generating a Farm from the gold and CropGroup based on the numEachSeed array.
     * 
     * @param numEachSeed The number of each valid crop to buy and plant
     * @param gold The amount of gold this farm currently has
     */
    public void permutate(int[] numEachSeed, int gold)
    {
        //base case
        //you cannot invest money into any more crops
        if (gold < leastExpensiveCropValue)
        {
            addFarm(numEachSeed, gold);
        }
        //recursive case
        else
        {
            //check all possible permutations of crops to invest in
            for (int i = 0; i < validCrops.size(); i++)
            {
                //only invest in crops that we have enough gold to purchase
                if (gold >= validCrops.get(i).getBuyPrice())
                {
                    numEachSeed[i]++;

                    //only calculate the permutation if it has not been seen before
                    if (cachedFarms.add(arrayToString(numEachSeed)))
                    {
                        permutate(numEachSeed.clone(), gold - validCrops.get(i).getBuyPrice());
                    }
                    numEachSeed[i]--;
                }
            }
        }
    }

    //turns an int array into a string
    private String arrayToString(int[] array)
    {
        String string = "";
        for (int i = 0; i < array.length; i++)
        {
            string += array[i] + ",";
        }
        return string;
    }
}
