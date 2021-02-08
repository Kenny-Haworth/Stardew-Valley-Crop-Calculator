import java.util.ArrayList;

/**
 * The purpose of this class is to document and record the events that occurred
 * on the farm for a single day.
 */
public class FarmEvent
{
    private static final int MAX_DAYS = 28;
    private final int day;
    private final int startingGold;
    private int endingGold;
    private int endingGoldCache;
    private ArrayList<CropGroup> cropsHarvested;
    private ArrayList<Integer> goldFromHarvestedCrops;
    private ArrayList<CropGroup> seedsPurchased;
    private ArrayList<CropGroup> startingCrops;
    //TODO all crops that are on the farm at the end of the day

    public FarmEvent(int daysRemaining, int startingGold, ArrayList<CropGroup> startingCrops)
    {
        this.startingGold = startingGold;
        this.endingGold = 0;
        this.endingGoldCache = 0;
        this.day = MAX_DAYS - daysRemaining;
        this.cropsHarvested = new ArrayList<>();
        this.goldFromHarvestedCrops = new ArrayList<>();
        this.seedsPurchased = new ArrayList<>();
        this.startingCrops = new ArrayList<>();

        if (startingCrops != null)
        {
            for (CropGroup cropGroup : startingCrops)
            {
                this.startingCrops.add(cropGroup.clone());
            }
        }
    }

    private FarmEvent(int day,
                      int startingGold,
                      int endingGold,
                      int endingGoldCache,
                      ArrayList<CropGroup> cropsHarvested,
                      ArrayList<Integer> goldFromHarvestedCrops,
                      ArrayList<CropGroup> seedsPurchased,
                      ArrayList<CropGroup> startingCrops)
    {
        this.day = day;
        this.startingGold = startingGold;
        this.endingGold = endingGold;
        this.endingGoldCache = endingGoldCache;
        this.cropsHarvested = new ArrayList<>(cropsHarvested);
        this.goldFromHarvestedCrops = new ArrayList<>(goldFromHarvestedCrops);
        this.seedsPurchased = new ArrayList<>(seedsPurchased);
        this.startingCrops = new ArrayList<>(startingCrops);
    }

    //creates and returns a deep copy of this FarmEvent
    @Override
    public FarmEvent clone()
    {
        ArrayList<CropGroup> cropsHarvestedClone = new ArrayList<>();
        for (CropGroup cropGroup : this.cropsHarvested)
        {
            cropsHarvestedClone.add(cropGroup.clone());
        }

        ArrayList<Integer> goldFromHarvestedCropsClone = new ArrayList<>();
        for (Integer gold : this.goldFromHarvestedCrops)
        {
            goldFromHarvestedCropsClone.add(gold);
        }

        ArrayList<CropGroup> seedsPurchasedClone = new ArrayList<>();
        for (CropGroup cropGroup : this.seedsPurchased)
        {
            seedsPurchasedClone.add(cropGroup.clone());
        }

        ArrayList<CropGroup> startingCropsClone = new ArrayList<>();
        for (CropGroup cropGroup : this.startingCrops)
        {
            startingCropsClone.add(cropGroup.clone());
        }

        return new FarmEvent(this.day,
                             this.startingGold,
                             this.endingGold,
                             this.endingGoldCache,
                             cropsHarvestedClone,
                             goldFromHarvestedCropsClone,
                             seedsPurchasedClone,
                             startingCropsClone);
    }

    /**
     * Keeps track of the crops harvested for the day.
     *
     * @param cropGroup The crop(s) harvested
     * @param gold The amount of gold earned for harvesting these crops
     */
    public void addHarvestedCrops(CropGroup cropGroup, int gold)
    {
        cropsHarvested.add(cropGroup);
        goldFromHarvestedCrops.add(gold);
    }

    /**
     * Keeps track of the seeds purchased for the day.
     *
     * @param seeds An arraylist of all the different types of seeds purchased.
     *              The amount purchased is saved within the CropGroup.
     *
     *              If seeds is null, sets an empty list.
     */
    public void setSeedsPurchased(ArrayList<CropGroup> seeds)
    {
        if (seeds == null)
        {
            this.seedsPurchased = new ArrayList<>();
        }
        else
        {
            this.seedsPurchased = new ArrayList<>(seeds);
        }
    }

    /**
     * Saves the farm's gold at the end of the day.
     *
     * @param endingGold The gold the player has at the end of the day
     * @param endingGoldCache The gold the player will gain tomorrow
     *                        from selling the crops they sold today
     */
    public void setEndingGold(int endingGold, int endingGoldCache)
    {
        this.endingGold = endingGold;
        this.endingGoldCache = endingGoldCache;
    }

    //TODO output this to a text file! By CropCalculator.java sorting all the farms, it could be printed to the file in order of what is most lucrative
    /**
     * Outputs only the strategy of this farm, with no extra information.
     *
     * This tells you exactly what to do on each day for making the most
     * lucrative farm possible without clogging the output with unnecessary
     * information.
     */
    //TODO change this to printAll() and break up prints into easier to manage methods
    public void printStrategy()
    {
        System.out.println("Day " + day);
        System.out.println("\tStarting gold: " + startingGold);

        //print the crops this farm started with
        if (startingCrops.size() > 0)
        {
            System.out.println("\tCrops on the farm at the beginning of the day:");
            System.out.format("\t\t%20s%20s%20s", "Crop", "Number", "Age");

            for (CropGroup cropGround : startingCrops)
            {
                System.out.print("\n\t\t");
                System.out.format("%20s%20d%20d", cropGround.getName(),
                                                  cropGround.getNumber(),
                                                  cropGround.getAge());
            }
            System.out.println();
        }
        else
        {
            System.out.println("\tBegan the day with no crops planted on the farm.");
        }

        //check for any harvested crops, display the number harvested as totals
        if (cropsHarvested.size() > 0)
        {
            System.out.println("\tHarvested crops:");
            for (CropGroup cropGroup : cropsHarvested)
            {
                System.out.println("\t\t" + cropGroup.getNumber() + " " + cropGroup.getName());
            }

            //sell the crops, x gold each, for a sum of y gold and a grand total of z gold
            System.out.println("\tProfit from harvested crops:");
            System.out.print("\t\t"); //TODO try to concatenate
            System.out.format("%20s%20s%20s%20s", "Crop", "Sell Price", "Number", "Total");

            for (int i = 0; i < cropsHarvested.size(); i++)
            {
                System.out.print("\n\t\t");
                System.out.format("%20s%20d%20s%20s", cropsHarvested.get(i).getName(),
                                                      cropsHarvested.get(i).getSellPrice(),
                                                      cropsHarvested.get(i).getNumber(),
                                                      goldFromHarvestedCrops.get(i));

                //TODO here you could compute chanceForMore and tell the player if they gained any extra crops during harvesting
            }
            System.out.println();

            //display a grand total
            if (cropsHarvested.size() > 1)
            {
                int totalGold = 0;
                for (int gold : goldFromHarvestedCrops)
                {
                    totalGold += gold;
                }

                System.out.print("\t\t");
                System.out.format("%20s%20s%20s%20d", "", "", "", totalGold);
                System.out.println();
            }

            //tell the player they will receive their money tomorrow
            System.out.println("\t\tA total of " + endingGoldCache + " gold will be added to your account tomorrow.");
        }
        else
        {
            System.out.println("\tNo crops could be harvested.");
        }

        //check for any investments
        if (seedsPurchased.size() > 0)
        {
            System.out.println("\tPurchased seeds:");
            for (CropGroup cropGroup : seedsPurchased)
            {
                System.out.println("\t\t" + cropGroup.getNumber() + " " + cropGroup.getName());
            }

            //purchase the seeds, x gold each, for a sum of y gold and a grand total of z gold
            System.out.println("\tCost of purchased seeds:");
            System.out.print("\t\t"); //TODO try to concatenate
            System.out.format("%20s%20s%20s%20s", "Seed", "Buy Price", "Number", "Total");

            for (CropGroup seedPurchased : seedsPurchased)
            {
                System.out.print("\n\t\t");
                System.out.format("%20s%20d%20s%20s", seedPurchased.getName(),
                                                      seedPurchased.getBuyPrice(),
                                                      seedPurchased.getNumber(),
                                                      seedPurchased.getBuyPrice()*seedPurchased.getNumber());
            }
            System.out.println();

            //display a grand total
            if (seedsPurchased.size() > 1)
            {
                int totalGold = 0;
                for (CropGroup seedPurchased : seedsPurchased)
                {
                    totalGold += seedPurchased.getBuyPrice()*seedPurchased.getNumber();
                }

                System.out.print("\t\t");
                System.out.format("%20s%20s%20s%20d", "", "", "", totalGold);
                System.out.println();
            }
            System.out.println("\tPlanted and watered all seeds on the farm.");
        }
        else
        {
            System.out.println("\tNo seeds were purchased."); //TODO if because of energy/space constraints, say so
        }

        System.out.println("\tEnding gold: " + endingGold);

        int totalEndingGold = endingGold+endingGoldCache;
        int goldDifference = totalEndingGold-startingGold;

        if (day == MAX_DAYS)
        {
            goldDifference -= endingGoldCache;
        }

        if (goldDifference > 0)
        {
            System.out.println("\tNet gold difference for today: +" + goldDifference + "\n");
        }
        else //goldDifference <= 0
        {
            System.out.println("\tNet gold difference for today: " + goldDifference + "\n");
        }
    }

    /**
     * Outputs every single detail about this farm, including:
     *      Day number
     *      Starting gold
     *      Ending gold
     *      The crops on the farm at the beginning of the day
     *          Crop type
     *          Crop number
     *          Crop growth phase
     *      The crops on the farm at the end of the day
     *          Crop type
     *          Crop number
     *          Crop growth phase
     *          Number of days until the crops can be harvested
     *      Investments made
     *          Number of each seed purchased with individual seed price
     *          Total for each type of seed purchased with sum price
     *          Grand total for all seeds purchased
     *      Crops planted
     */

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
    public void printAll()
    {

    }
}
