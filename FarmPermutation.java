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
    private int numCropsOnFarm; //the number of crops currently on the farm
    private int maxPlantableSeeds; //the maximum number of seeds that can be planted before hitting energy limits on watering

    //TODO move this to a setup function and make this class staticly used, no need for a constructor
    public FarmPermutation(Farm farm, ArrayList<Crop> validCrops)
    {
        this.farm = farm;
        cachedFarms = new HashSet<>();
        farmPermutations = new ArrayList<>();
        numCropsOnFarm = farm.getNumCrops();
        maxPlantableSeeds = Energy.maxWaterableTiles() - numCropsOnFarm;

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
        int[] numEachSeed = new int[validCrops.size()]; //the number of seeds to plant for each crop
        permutate(numEachSeed, farm.getGold(), 0);
        return farmPermutations;
    }

    private void addFarm(int[] numEachSeed, int gold)
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
        farmPermutations.add(new Farm(crops, gold, farm.getGoldCache(), farm.getEvents()));
    }


    /**
     * This function uses a special type of processing known as recursive memoization.
     *
     * Every type of farm is generated, investing money in one crop at a time and
     * working down the tree until there is no money left. Every farm is saved to a
     * cache. If the farm that is generated is already in the cache, it is ignored.
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
    private void permutate(int[] numEachSeed, int gold, int totalNumSeeds)
    {
        //base case
        //you cannot invest money into any more crops
        //or you are at maximum depth
        if (gold < leastExpensiveCropValue || totalNumSeeds == maxPlantableSeeds)
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
                        permutate(numEachSeed.clone(), gold - validCrops.get(i).getBuyPrice(), totalNumSeeds+1);
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
