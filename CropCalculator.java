import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
 * Such that, if you see a method harvest(), I mean we are both harvesting and selling them,
 * and if see a comment "purchase seeds", it means we are both buying the seeds and planting them.
 * Buying and planting, and harvesting and selling, always happen on the same day.
 * 
 * In the case of selling, I assume you will sell all your crops through the Shipping Box, meaning
 * the money is obtained the following day. While this is less lucrative (as you are unable to
 * invest in crops as soon), according to the wiki at https://stardewcommunitywiki.com/Shipping:
 *      "Items sold to merchants are not included in the statistics on the Collection tab, nor do they count towards shipping-specific Achievements."
 * Thus, it is best the player sells all of their crops through the Shipping Box, and it is what
 * this algorithm assumes you will do.
 */

//TODO
/**
 * take into account the following things as well:
 *      max player energy
 *          watering can efficiency
 *          watering can upgrade
 *          should take into account sprinklers, which increases the number of tillable squares depending on the sprinkler type
 *          it would be cool to tell the player that they have extra money stored up after a certain point.
 *              Like they've invested all they possibly can, and can now use the leftover money to
 *              purchase other things (e.g. tool upgrades, break open geos, etc.). This could be checked in permutate()
 *          allow the player to increase their max energy by telling the algorithm they are willing to eat food everyday to keep it up
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
 *      The ability to sell your crops the same day you harvest them. No goldCache. Gets rid of
 *      statistic tracking and achievements, but it is more lucrative to sell them the same day.
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
    static final Crop TOMATO = new Crop         ("Tomato",              50,            60,            11,            4,                1,                5);
    static final Crop PEPPER = new Crop         ("Pepper",              40,            40,            5,             3,                1,                3);
    static final Crop BLUEBERRY = new Crop      ("Blueberry",           80,            50,            13,            4,                3,                2);
    static final Crop CORN = new Crop           ("Corn",                150,           50,            14,            4,                1,                0);
    static final Crop HOPS = new Crop           ("Hops",                60,            25,            11,            1,                1,                0);
    static final Crop MELON = new Crop          ("Melon",               80,            250,           12,            0,                1,                0);
    static final Crop POPPY = new Crop          ("Poppy",               100,           140,           7,             0,                1,                0);
    static final Crop RADISH = new Crop         ("Radish",              40,            90,            6,             0,                1,                0);
    static final Crop RED_CABBAGE = new Crop    ("Red Cabbage",         100,           260,           9,             0,                1,                0); //only available from year 2+
    static final Crop STARFRUIT = new Crop      ("Starfruit",           400,           750,           13,            0,                1,                0);
    static final Crop SUMMER_SPANGLE = new Crop ("Summer Spangle",      50,            90,            8,             0,                1,                0);
 // static final Crop SUNFLOWER = new Crop      ("Sunflower",           200,           80,            8,             0,                1,                0); //TODO yields 0-2 sunflower seeds when harvested, same thing for coffee beans
    static final Crop WHEAT = new Crop          ("Wheat",               10,            25,            4,             0,                1,                0);
    static final int MAX_DAYS = 28;

    //define which crops are available in each season
    static final Crop[] SPRING_CROPS = {};
    static final Crop[] SUMMER_CROPS = {TOMATO, PEPPER, BLUEBERRY, CORN, HOPS, MELON, POPPY, RADISH, RED_CABBAGE, STARFRUIT, SUMMER_SPANGLE, WHEAT};
    static final Crop[] FALL_CROPS = {};
    static final Crop[] WINTER_CROPS = {};

    public static void main(String[] args)
    {
        //editable variables
        final int day = 15; //1
        final SEASON season = SEASON.SUMMER;
        final int gold = 350; //27 //note here that when gold increases beyond a reasonable level, algorithm runtime drastically increases,
                             //as there are many more combinations possible. However, once player energy is factored in, gold will
                             //have a cap number for increasing runtime (e.g. a value of gold over x no longer makes the program slower).
                             //Specifically, this cap = the most expensive crop * number of squares the player can water in a day

        ArrayList<Crop> crops;
        switch (season)
        {
            case SPRING:
                crops = new ArrayList<>(Arrays.asList(SPRING_CROPS));
                break;
            case SUMMER:
                crops = new ArrayList<>(Arrays.asList(SUMMER_CROPS));
                break;
            case FALL:
                crops = new ArrayList<>(Arrays.asList(FALL_CROPS));
                break;
            case WINTER:
            default:
                crops = new ArrayList<>(Arrays.asList(WINTER_CROPS));
                break;
        }

        //sort the crops in descending order of buy price (most expensive crops first)
        Collections.sort(crops);

        //first time setup
        int daysRemaining = MAX_DAYS - day;
        ArrayList<Farm> farms = new ArrayList<>();
        Farm.initialize(crops, daysRemaining+1);
        Farm.update();
        Farm startingFarm = new Farm(null, gold, 0, null);
        farms.add(startingFarm);
        double startTime = System.nanoTime();

        //simulate every possible permutation of farms
        //this is a breadth-first search
        for (int i = 0; i < daysRemaining+1; i++) //plus one to ensure we have a FarmEvent log for the last day
        {
            System.out.println("Day " + (day + i));
            
            ArrayList<Farm> newFarms = new ArrayList<>();
            for (Farm farm : farms)
            {
                newFarms.addAll(farm.simulateDay());
            }
            System.out.println("Permutations: " + newFarms.size());

            Farm.update();
            farms.clear();
            farms = newFarms;
        }
        double endTime = System.nanoTime() - startTime;

        System.out.println("Total number of farm combinations: " + farms.size());

        //TODO overload Farm comparable function for sorting
        //     then print them out nicely for some easy comparison.
        //determine which permutation was the most profitable
        Farm mostProfitableFarm = farms.get(0);
        for (int i = 0; i < farms.size(); i++)
        {
            if (farms.get(i).getGold() > mostProfitableFarm.getGold())
            {
                mostProfitableFarm = farms.get(i);
            }
        }

        System.out.println("For day " + day + " of " + season + " starting with " + gold + " gold, " +
                           "the most profitable strategy you can pursue is:");
        mostProfitableFarm.print();
        System.out.println("Time: " + endTime/1000000000 + " seconds");
    }
}
