import java.util.ArrayList;

public class Farm
{
    private final Crop[] cropTypes; //all unique types of crops TODO all farms have this same list passed around, it's not very efficient
    private ArrayList<CropStatus> crops; //the crops currently on the farm
    private int gold;
    private int goldCache; //crops are not sold immediately after harvesting; the gold is obtained the following day
    private int daysRemaining;
    private boolean firstFarm;
    //TODO save a snapshot of the Farm each day with it

    public Farm(Crop[] cropTypes, ArrayList<CropStatus> crops, int gold, int goldCache, int daysRemaining, boolean firstFarm) //TODO deal with crops type
    {
        this.gold = gold;
        this.goldCache = goldCache;
        this.daysRemaining = daysRemaining;
        this.cropTypes = cropTypes.clone();
        this.firstFarm = firstFarm;

        if (crops == null)
        {
            this.crops = new ArrayList<>();
        }
        else
        {
            this.crops = crops;
        }
    }

    ArrayList<Farm> simulateDay()
    {
        if (!firstFarm)
        {
            gold += goldCache;
            goldCache = 0;
            advanceCrops();
            harvest();

            //there are no days remaining before the end of the season //TODO why is this 1 and not 0?
            //so pretend the crops you sold today give you instant gold
            if (daysRemaining-1 == 0)
            {
                gold += goldCache;
            }
        }
        
        ArrayList<Farm> farms = invest();
        daysRemaining--; //most occur after investing/harvesting
        return farms;
    }

    private void advanceCrops()
    {
        for (CropStatus cropStatus : crops)
        {
            cropStatus.advance();
        }
    }

    //harvest the crops you can get from the farm for the day
    //delete any crops from the list of crops if they cannot regrow before the end of the season
    private void harvest()
    {
        //maintain a list of all the crops to remove
        ArrayList<CropStatus> worthlessCrops = new ArrayList<>();

        for (int i = 0; i < crops.size(); i++)
        {
            goldCache += crops.get(i).harvest();

            if (!crops.get(i).canProduceMore(daysRemaining))
            {
                worthlessCrops.add(crops.get(i));
            }
        }
        crops.removeAll(worthlessCrops);
    }

    public ArrayList<Farm> invest()
    {
        //filter out all seeds that could not possibly yield crops before the end of the season
        ArrayList<Crop> validCrops = new ArrayList<>();
        for (Crop cropType : cropTypes)
        {
            if (cropType.getGrowthTime() <= daysRemaining)
            {
                validCrops.add(cropType);
            }
        }

        //there are no possible crops to plant, so return this farm only
        if (validCrops.size() == 0)
        {
            ArrayList<Farm> noPermutation = new ArrayList<>();
            noPermutation.add(this);
            return noPermutation;
        }
        //calculate all permutations of farms
        else
        {
            FarmPermutation permutation = new FarmPermutation(this, validCrops);
            return permutation.calculateFarmPermutations();
        }
    }

    public Crop[] getCropTypes() 
    {
        return this.cropTypes;
    }

    public ArrayList<CropStatus> getCrops() 
    {
        return this.crops;
    }

    public int getGold() 
    {
        return this.gold;
    }

    public int getGoldCache() 
    {
        return this.goldCache;
    }

    public int getDaysRemaining() 
    {
        return this.daysRemaining;
    }

    //TODO
    /**
     * You can make this method extremely detailed. For each day print:
     *      all crops harvested on this day (if any)
     *      the seeds & number of seeds purchased on this day (also assumed to be planted on the same day)
     *      the stage of life of every crop on this day
     *      
     * It would also be useful to show the player when something special happens,
     * like you harvest an extra crop from the % chance or you make a giant crop from the % chance.
     * 
     * At some point, also display the quality of crops that were harvested
     */
    public void printStrategy()
    {
        System.out.println("Gold: " + gold);
    }
}
