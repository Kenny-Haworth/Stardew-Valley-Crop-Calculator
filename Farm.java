import java.util.ArrayList;

public class Farm
{
    private ArrayList<Crop> cropTypes; //all unique types of crops TODO all farms have this same list passed around, it's not very efficient
    private ArrayList<CropGroup> crops; //the crops currently on the farm
    private int gold;
    private int goldCache; //crops are not sold immediately after harvesting; the gold is obtained the following day
    private int daysRemaining;
    private ArrayList<FarmEvent> events; //a snapshot of the events that occurred on this farm every day
    private FarmEvent event; //the events for the current day on this farm

    public Farm(ArrayList<Crop> cropTypes, ArrayList<CropGroup> crops, int gold, int goldCache, int daysRemaining, ArrayList<FarmEvent> events)
    {
        this.gold = gold;
        this.goldCache = goldCache;
        this.daysRemaining = daysRemaining;
        this.event = null;
        this.cropTypes = new ArrayList<>(cropTypes);

        if (crops == null)
        {
            this.crops = new ArrayList<>();
        }
        else
        {
            this.crops = new ArrayList<>(crops);
        }

        if (events == null)
        {
            this.events = new ArrayList<>();
        }
        else
        {
            this.events = new ArrayList<>(events);
        }
    }

    /**
     * Main logic loop for a farm.
     * 
     * Advances the crops, harvests the crops, and invests in more crops.
     * 
     * @return Every single valid permutation of this farm possible, advanced one day.
     */
    ArrayList<Farm> simulateDay()
    {
        advanceCrops();
        gold += goldCache;
        goldCache = 0;

        event = new FarmEvent(daysRemaining, gold, crops);
        events.add(event);

        harvest();

        //there are no days remaining before the end of the season
        //so pretend the crops you sold today give you instant gold
        if (daysRemaining == 0)
        {
            gold += goldCache;
        }
        
        ArrayList<Farm> farmPermutations = invest();
        return farmPermutations;
    }

    private void advanceCrops()
    {
        for (CropGroup cropGroup : crops)
        {
            cropGroup.advance();
        }
    }

    //harvest the crops you can get from the farm for the day
    //delete any crops from the list of crops if they cannot regrow before the end of the season
    private void harvest()
    {
        //maintain a list of all the crops to remove
        ArrayList<CropGroup> worthlessCrops = new ArrayList<>();
        for (CropGroup cropGroup : crops)
        {
            int amount = cropGroup.harvest(event);
            goldCache += amount;

            if (!cropGroup.canProduceMore(daysRemaining))
            {
                worthlessCrops.add(cropGroup);
            }
        }
        crops.removeAll(worthlessCrops);
    }

    public ArrayList<Farm> invest()
    {
        //filter out all seeds that could not possibly yield crops before the end of the season
        ArrayList<Crop> invalidCrops = new ArrayList<>();
        for (Crop cropType : cropTypes)
        {
            if (cropType.getGrowthTime() > daysRemaining)
            {
                invalidCrops.add(cropType);
            }
        }
        cropTypes.removeAll(invalidCrops);

        //there are no possible crops to plant, so return this farm only.
        //note that if you don't have enough gold to buy the least expensive crop,
        //you can also return this farm immediately here, but FarmPermutation handles this case
        if (cropTypes.size() == 0)
        {
            ArrayList<Farm> noPermutation = new ArrayList<>();
            daysRemaining--;
            event.setEndingGold(gold, goldCache);
            event.setSeedsPurchased(null);
            noPermutation.add(this);
            return noPermutation;
        }
        //calculate all permutations of farms
        else
        {
            FarmPermutation permutation = new FarmPermutation(this, cropTypes);
            return permutation.calculateFarmPermutations();
        }
    }

    public ArrayList<Crop> getCropTypes() 
    {
        return this.cropTypes;
    }

    public ArrayList<CropGroup> getCrops() 
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

    public ArrayList<FarmEvent> getEvents() 
    {
        return this.events;
    }

    public void print()
    {
        for (FarmEvent event : events)
        {
            event.printStrategy();
        }
    }
}
