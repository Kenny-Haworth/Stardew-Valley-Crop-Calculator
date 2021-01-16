/**
 * This class keeps track of a growing crop's status, including:
 *      the number of days the crop has been growing
 *      whether the crop can be harvested
 *      
 * This class can represent multiple growing crops with the same
 * status.
 */
public class CropStatus extends Crop
{
    int number; //the number of crops that are exactly the same as this one
    int age; //the number of days this plant has been alive
    boolean fullyGrown;

    public CropStatus(Crop crop, int number)
    {
        super(crop.getName(), crop.getBuyPrice(), crop.getSellPrice(), crop.getGrowthTime(), crop.getRegrowthTime(), crop.getNumHarvested(), crop.getChanceForMore());
        age = 1;
        fullyGrown = false;
        this.number = number;
    }

    //advances the crop to a new a day
    public void advance()
    {
        age++;
    }

    //if this crop is ready to be harvested, returns the money amount for harvesting & selling it
    //resets the age to 1 after first harvest if this crop can be reharvested
    public int harvest()
    {
        if (fullyGrown)
        {
            if (age == this.regrowthTime)
            {
                age = 1;
                return number*getSellPrice();
            }
        }
        else if (age == this.growthTime)
        {
            age = 1;
            fullyGrown = true;
            return number*getSellPrice();
        }
        return 0;
    }

    /**
     * @param daysRemaining The days left before the end of the season
     * @return  true if the crop can produce more before the end of the season, false otherwise
     *              crops that have been harvested that cannot be regrown will return false
     *              crops that will not be able to be harvested again before the end of the season will return false
     */
    public boolean canProduceMore(int daysRemaining)
    {
        if ((fullyGrown && !canRegrow()) ||
           (!fullyGrown && daysRemaining < (this.growthTime - age)) ||
           (fullyGrown && daysRemaining < (this.regrowthTime - age)))
        {
            return false;
        }
        
        return true;
    }
}
