import java.util.HashMap;
import java.util.Map;

/**
 * A class for calculating the number of tiles the player
 * can water a day based on the following characteristics:
 *      max player energy (stardrops eaten)
 *      watering can level
 *      farming proficiency level
 *
 * This class is a Singleton.
 */
public class Energy
{
    private static int maxWaterableTiles;

    public static void initialize(int numStardropsEaten, Level wateringCanLevel, int farmingProficiencyLevel)
    {
        //set up temporary associations
        Map<Level, Double> wateringCanEnergyCosts = new HashMap<>();
        wateringCanEnergyCosts.put(Level.BASIC, 2 - (farmingProficiencyLevel*0.1)); //watering can level - proficiency
        wateringCanEnergyCosts.put(Level.COPPER, 4 - (farmingProficiencyLevel*0.1));
        wateringCanEnergyCosts.put(Level.STEEL, 6 - (farmingProficiencyLevel*0.1));
        wateringCanEnergyCosts.put(Level.GOLD, 8 - (farmingProficiencyLevel*0.1));
        wateringCanEnergyCosts.put(Level.IRIDIUM, 10 - (farmingProficiencyLevel*0.1));

        Map<Level, Integer> numTilesWateredByLevel = new HashMap<>();
        numTilesWateredByLevel.put(Level.BASIC, 1);
        numTilesWateredByLevel.put(Level.COPPER, 3);
        numTilesWateredByLevel.put(Level.STEEL, 5);
        numTilesWateredByLevel.put(Level.GOLD, 9);
        numTilesWateredByLevel.put(Level.IRIDIUM, 18);

        //calculate max energy and max waterable tiles
        double maxEnergy = 270 + (numStardropsEaten*34) - 0.1; //-0.1 offset to ensure you have at least 1 energy remaining, so as to not exhaust yourself
        maxWaterableTiles = (int)(Math.floor(maxEnergy/wateringCanEnergyCosts.get(wateringCanLevel))*numTilesWateredByLevel.get(wateringCanLevel)); //energy/cost = numActions. numActions * number of tiles watered per action gives max waterable tiles
        double remainingEnergy = maxEnergy % wateringCanEnergyCosts.get(wateringCanLevel);

        //if there is enough energy remaining to perform a lower-level watering action, use it
        for (int i = wateringCanLevel.getValue(); i > 0; i--)
        {
            if (remainingEnergy >= wateringCanEnergyCosts.get(Level.fromInt(i-1)))
            {
                remainingEnergy -= wateringCanEnergyCosts.get(Level.fromInt(i-1));
                maxWaterableTiles += numTilesWateredByLevel.get(Level.fromInt(i-1));
            }
        }
    }

    /**
     * Returns the maximum number of tiles the
     * player can water in a single day while
     * staying above their energy limit.
     */
    public static int maxWaterableTiles()
    {
        return maxWaterableTiles;
    }
}
