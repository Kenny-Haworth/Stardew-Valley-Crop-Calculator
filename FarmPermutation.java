import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FarmPermutation
{
    private Set<String> cachedFarms; //all farms that have already been seen //TODO compare static and nonstatic runtime; static will require saving gold amount with the String //TODO overload hash function for array??
    private ArrayList<Farm> farmPermutations; //all unique farms with maximized spending
    private final ArrayList<Crop> validCrops;
    private final Farm farm; //the base farm to make all permutations of
    private int leastExpensiveCropValue;

    public FarmPermutation(Farm farm, ArrayList<Crop> validCrops)
    {
        this.farm = farm;
        this.validCrops = validCrops;
        cachedFarms = new HashSet<>();
        farmPermutations = new ArrayList<>();

        leastExpensiveCropValue = Integer.MAX_VALUE;
        for (Crop crop : validCrops)
        {
            leastExpensiveCropValue = Math.min(leastExpensiveCropValue, crop.getBuyPrice());
        }
    }

    public ArrayList<Farm> calculateFarmPermutations()
    {
        int[] numEachSeed = new int[validCrops.size()]; //the number of seeds to plant for each crop
        permutate(numEachSeed, farm.getGold());
        return farmPermutations;
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
     * Saving involes generating a Farm from the gold and CropGroup based on the numEachSeed array.
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
            ArrayList<CropGroup> crops = new ArrayList<>(); //all the crops, with the new crops, on the farm

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
            Farm newFarm = new Farm(farm.getCropTypes(), crops, gold, farm.getGoldCache(), farm.getDaysRemaining()-1, farm.getEvents());
            farmPermutations.add(newFarm);
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
