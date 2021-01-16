import java.util.ArrayList;

/**
 * This program calculates the most lucrative combination of planting crops in Stardew Valley.
 * It takes into account the following information:
 *      the season
 *      the day number
 *      the player's gold
 * 
 * Note that I make the following associations in wording within the comments of these classes.
 *      buying = planting
 *      harvesting = selling
 * 
 * Such that, if I comment "harvest crops here", I mean we are both harvesting and selling them,
 * and if I comment "plant seeds here", it means we are both buying the seeds and planting them.
 * 
 * Buying and planting, and harvesting and selling, always happens on the same day.
 */

//TODO
/**
 * take into account the following things as well:
 *      max player energy
 *          watering can efficiency
 *          watering can upgrade
 *  
 *      ability to turn crop into other products that sell for even more
 *          artisan profession
 * 
 *      crop price modifiers
 *          tiller profession
 * 
 *          crop quality
 *              farming skill https://stardewcommunitywiki.com/Farming#Farming_Skill
 *              fertilized soil... that one's gonna suck to calculate
 *              food buff... not sure I will even bother with that one but we can
 * 
 *      seeds that yield more seeds for free
 *          coffee beans
 *          sunflowers
 *  
 *      chance that crop yields more when harvested
 *          blueberry plants yield 3 blueberry plants when harvested, but have a 2% chance of giving another blueberry
 * 
 *      GIANT crops
 * 
 *      Crops the player already has planted in their fields
 * 
 *      Multi-seasonal calculations
 *          Add trees!
 *              this requires a cross-season calculation, as they grow for 28 days and only produce in a specific season
 *              they produce one crop per day, and every year their quality raises by one level (nothing->silver->gold->iridium)
 *              This is obviously very efficient, but how long does it take to get your money back?
 *      
 *          Crops that last longer than one season?
 * 
 *      etc.
 */

public class CropCalculator
{
    //seasons
    static enum SEASON
    {
        SPRING,
        SUMMER,
        FALL,
        WINTER
    };

    //all crop types, listed by season

    //SUMMER                                       name              buyPrice       sellPrice     growthTime     regrowthTime     numHarvested     chanceForMore
    static final Crop TOMATO = new Crop         ("TOMATO",              50,            60,            11,            4,                1,                5);
    static final Crop PEPPER = new Crop         ("PEPPER",              40,            40,            5,             3,                1,                3);
    static final Crop BLUEBERRY = new Crop      ("BLUEBERRY",           80,            50,            13,            4,                3,                2);
    static final Crop CORN = new Crop           ("CORN",                150,           50,            14,            4,                1,                0);
    static final Crop HOPS = new Crop           ("HOPS",                60,            25,            11,            1,                1,                0);
    static final Crop MELON = new Crop          ("MELON",               80,            250,           12,            0,                1,                0); //TODO giant crop
    static final Crop POPPY = new Crop          ("POPPY",               100,           140,           7,             0,                1,                0);
    static final Crop RADISH = new Crop         ("RADISH",              40,            90,            6,             0,                1,                0);
    static final Crop RED_CABBAGE = new Crop    ("RED_CABBAGE",         100,           260,           9,             0,                1,                0); //only available from year 2+
    static final Crop STARFRUIT = new Crop      ("STARFRUIT",           400,           750,           13,            0,                1,                0);
    static final Crop SUMMER_SPANGLE = new Crop ("SUMMER_SPANGLE",      50,            90,            8,             0,                1,                0);
 // static final Crop SUNFLOWER = new Crop      ("SUNFLOWER",           200,           80,            8,             0,                1,                0); //TODO yields 0-2 sunflower seeds when harvested, same thing for coffee beans
    static final Crop WHEAT = new Crop          ("WHEAT",               10,            25,            4,             0,                1,                0);
    static final int MAX_DAYS = 28;

    //define which crops are available in each season
    static final Crop[] SPRING_CROPS = {};
    static final Crop[] SUMMER_CROPS = {TOMATO, PEPPER, BLUEBERRY, CORN, HOPS, MELON, POPPY, RADISH, RED_CABBAGE, STARFRUIT, SUMMER_SPANGLE, WHEAT};
    static final Crop[] FALL_CROPS = {};
    static final Crop[] WINTER_CROPS = {};

    public static void main(String[] args)
    {
        //editable variables
        final int day = 1;
        final SEASON season = SEASON.SUMMER;
        final int gold = 50;

        Crop[] crops;

        switch (season)
        {
            case SPRING:
                crops = SPRING_CROPS.clone();
                break;
            case SUMMER:
                crops = SUMMER_CROPS.clone();
                break;
            case FALL:
                crops = FALL_CROPS.clone();
                break;
            case WINTER:
            default:
                crops = WINTER_CROPS.clone();
                break;
        }

        int daysRemaining = MAX_DAYS - day;
        ArrayList<Farm> farms = new ArrayList<>();
        Farm startingFarm = new Farm(crops, null, gold, 0, daysRemaining, true);
        farms.add(startingFarm);

        //simulate every possible permutation of farms
        for (int i = 0; i < daysRemaining; i++)
        {
            System.out.println("Day " + (day + i));
            ArrayList<Farm> newFarms = new ArrayList<>();

            for (Farm farm : farms)
            {
                newFarms.addAll(farm.simulateDay());
            }

            farms.clear();
            farms = newFarms;
        }

        //determine which permutation was the most profitable
        Farm mostProfitableFarm = farms.get(0);
        for (int i = 1; i < farms.size(); i++)
        {
            if (farms.get(i).getGold() > mostProfitableFarm.getGold())
            {
                mostProfitableFarm = farms.get(i);
            } 
        }

        //TODO do a quicksort or some fast sort of all the farms.
        //     then print them out nicely for some easy comparison.
        //     can probably call some sort of array util to sort them

        System.out.println("For day " + day + " of " + season + " starting with " + gold + " gold, " +
                           "the most profitable strategy you can pursue is:");
        mostProfitableFarm.printStrategy();
    }    
}
