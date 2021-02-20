import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FarmPermutation
{
    private static ArrayList<Crop> validCrops;
    private static Set<String> cachedFarms; //all farms that have already been seen
    private static int maxPlantableSeeds; //the maximum number of seeds that can be planted before hitting energy limits on watering
    private static ArrayList<FarmProto> farmPermutations; //all unique permutations of the given Farm for the next day

    //calculates all possible permutations of planting seeds for the next day
    public static ArrayList<FarmProto> calculate(ArrayList<Crop> crops, int numCrops, int gold)
    {
        validCrops = new ArrayList<>(crops);
        cachedFarms = new HashSet<>(); //TODO compare static and nonstatic runtime
        farmPermutations = new ArrayList<>();
        maxPlantableSeeds = Energy.maxWaterableTiles() - numCrops;

        int[] numEachSeed = new int[validCrops.size()]; //the number of seeds to plant for each crop
        permutate(numEachSeed, gold, 0);
        return farmPermutations;
    }

    /**
     * This function uses a special type of processing known as recursive memoization.
     *
     * Every type of farm is generated, investing money in one crop at a time. Every
     * farm is saved to a cache. If the farm that is generated is already in the cache, it is ignored.
     *
     * There are two base cases:
     *      1) The farm can no longer invest in any more crops
     *      2) The player does not have enough energy to water any more squares and
     *         so cannot plant any more crops
     *
     * @param numEachSeed The number of each valid crop to buy and plant
     * @param gold The amount of gold this farm currently has
     * @param totalNumSeeds The current depth of this tree
     */
    private static void permutate(int[] numEachSeed, int gold, int totalNumSeeds)
    {
        //base case
        //you cannot invest money into any more crops
        //or you are at maximum depth
        if (gold < validCrops.get(validCrops.size()-1).getBuyPrice()|| totalNumSeeds == maxPlantableSeeds)
        {
            createFarmProto(numEachSeed, gold);
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
                        permutate(numEachSeed, gold - validCrops.get(i).getBuyPrice(), totalNumSeeds+1);
                    }
                    numEachSeed[i]--;
                }
            }
        }
    }

    //creates a prototype farm
    private static void createFarmProto(int[] numEachSeed, int gold)
    {
        ArrayList<CropGroup> seeds = new ArrayList<>();
        for (int i = 0; i < numEachSeed.length; i++)
        {
            if (numEachSeed[i] > 0)
            {
                seeds.add(new CropGroup(validCrops.get(i), numEachSeed[i]));
            }
        }

        farmPermutations.add(new FarmProto(seeds, gold));
    }

    //turns an int array into a string
    private static String arrayToString(int[] array)
    {
        String string = "";
        for (int i = 0; i < array.length; i++)
        {
            string += array[i] + ",";
        }
        return string;
    }
}
