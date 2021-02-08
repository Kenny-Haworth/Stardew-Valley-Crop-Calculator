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
 *      should take into account sprinklers, which increases the number of waterable squares depending on the sprinkler type
 *      it would be cool to tell the player that they have extra money stored up after a certain point.
 *          Like they've invested all they possibly can, and can now use the leftover money to
 *          purchase other things (e.g. tool upgrades, break open geos, etc.). This could be checked in permutate()
 *      allow the player to increase their max energy by telling the algorithm they are willing to eat food everyday to keep it up
 *
 *      ability to turn crops into other products that sell for even more
 *
 *      Professions
 *          artisan profession
 *          tiller profession
 *          Many others...
 *
 *      crop quality
 *          farming skill https://stardewcommunitywiki.com/Farming#Farming_Skill
 *          fertilized soil... that one's gonna suck to calculate
 *          food buff... not sure I will even bother with that one but we can
 *
 *      seeds that yield more seeds for free
 *          coffee beans
 *          sunflowers
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
    private static enum SEASON
    {
        SPRING,
        SUMMER,
        FALL,
        WINTER
    };

    //all crop types, listed by season

    //SUMMER                                       name              buyPrice       sellPrice     growthTime     regrowthTime     numHarvested     chanceForMore
    private static final Crop TOMATO = new Crop         ("Tomato",              50,            60,            11,            4,                1,                5);
    private static final Crop PEPPER = new Crop         ("Pepper",              40,            40,            5,             3,                1,                3);
    private static final Crop BLUEBERRY = new Crop      ("Blueberry",           80,            50,            13,            4,                3,                2);
    private static final Crop CORN = new Crop           ("Corn",                150,           50,            14,            4,                1,                0);
//  private static final Crop HOPS = new Crop           ("Hops",                60,            25,            11,            1,                1,                0); //TODO a special energy case. It cannot be walked through, so planting in a 3x3 or 3x6 is impossible
    private static final Crop MELON = new Crop          ("Melon",               80,            250,           12,            0,                1,                0);
    private static final Crop POPPY = new Crop          ("Poppy",               100,           140,           7,             0,                1,                0);
    private static final Crop RADISH = new Crop         ("Radish",              40,            90,            6,             0,                1,                0);
    private static final Crop RED_CABBAGE = new Crop    ("Red Cabbage",         100,           260,           9,             0,                1,                0);
    private static final Crop STARFRUIT = new Crop      ("Starfruit",           400,           750,           13,            0,                1,                0);
    private static final Crop SUMMER_SPANGLE = new Crop ("Summer Spangle",      50,            90,            8,             0,                1,                0);
 // private static final Crop SUNFLOWER = new Crop      ("Sunflower",           200,           80,            8,             0,                1,                0); //TODO yields 0-2 sunflower seeds when harvested, same thing for coffee beans
    private static final Crop WHEAT = new Crop          ("Wheat",               10,            25,            4,             0,                1,                0);
    private static final int DAYS_IN_A_SEASON = 28;

    //define which crops are available in each season
    private static final Crop[] SPRING_CROPS = {};
    private static final Crop[] SUMMER_CROPS = {TOMATO, PEPPER, BLUEBERRY, CORN, MELON, POPPY, RADISH, RED_CABBAGE, STARFRUIT, SUMMER_SPANGLE, WHEAT};
    private static final Crop[] FALL_CROPS = {};
    private static final Crop[] WINTER_CROPS = {};

    public static void main(String[] args)
    {
        //editable variables
        final int day = 1; //1-28
        final SEASON season = SEASON.SUMMER;
        final int gold = 100; //note here that when gold increases beyond a reasonable level, algorithm runtime drastically increases,
                              //as there are many more combinations possible. However, once player energy is factored in, gold will
                              //have a cap number for increasing runtime (e.g. a value of gold over x no longer makes the program slower).
                              //Specifically, this cap = the most expensive crop * number of squares the player can water in a day

        //define the player's energy
        final int numStardropsEaten = 0; //0-7
        final Level wateringCanLevel = Level.COPPER;
        final int farmingProficiencyLevel = 0; //0-10
        //TODO allow the user to specify maximum squares

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
        Energy.initialize(numStardropsEaten, wateringCanLevel, farmingProficiencyLevel);
        int daysRemaining = DAYS_IN_A_SEASON - day + 1; //plus one to ensure we have a FarmEvent log for the last day
        Farm.initialize(crops, daysRemaining);
        Farm.update();
        Farm startingFarm = new Farm(null, gold, 0, null);
        ArrayList<Farm> farms = new ArrayList<>();
        farms.add(startingFarm);
        double startTime = System.nanoTime();

        //simulate every possible permutation of farms
        //this is a breadth-first search
        for (int i = 0; i < daysRemaining; i++)
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
            //TODO scan for duplicates
            //TODO tree pruning here
        }
        double endTime = System.nanoTime() - startTime;

        System.out.println("Total number of farm permutations: " + farms.size());

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

        System.out.println("For day " + day + " of " + season + " starting with " + gold + " gold " +
                           "and a maximum of " + Energy.maxWaterableTiles() + " waterable tiles a day, " +
                           "the most profitable strategy you can pursue is:");
        mostProfitableFarm.print();
        System.out.println("Time: " + endTime/1000000000 + " seconds");
    }
}
