import java.util.ArrayList;

public class FarmPermutation
{
    private ArrayList<Farm> farmPermutations; //all unique farms with maximized spending
    private final ArrayList<Crop> validCrops;
    private final Farm farm; //the base farm to make all permutations of
    private int leastExpensiveCropValue;

    public FarmPermutation(Farm farm, ArrayList<Crop> validCrops)
    {
        this.farm = farm;
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
                    permutate(numEachSeed.clone(), gold, j);
                    break;
                }
            }
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
    public void permutate(int[] numEachSeed, int gold, int index)
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
                    permutate(numEachSeed.clone(), gold, j);
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
        farmPermutations.add(new Farm(crops, gold, farm.getGoldCache(), farm.getEvents()));
    }
}
