/**
 * This class keeps track of a growing crop's status, including:
 *      the number of days the crop has been growing
 *      whether the crop can be harvested
 *
 * This class can represent multiple growing crops with the same
 * status.
 */
public class CropGroup extends Crop
{
    int number; //the number of crops that are exactly the same as this one
    int age; //the number of days this plant has been alive
    boolean fullyGrown;

    public CropGroup(Crop crop, int number)
    {
        super(crop.getName(), crop.getBuyPrice(), crop.getSellPrice(), crop.getGrowthTime(), crop.getRegrowthTime(), crop.getNumHarvested(), crop.getChanceForMore());
        this.number = number;
        age = 0;
        fullyGrown = false;
    }

    //creates a deep copy of this crop group
    @Override
    public CropGroup clone()
    {
        CropGroup cropGroup = new CropGroup(this, this.number);
        cropGroup.setAge(this.age);
        cropGroup.setFullyGrown(this.fullyGrown);
        return cropGroup;
    }

    //advances the crop to a new a day
    public void advance()
    {
        age++;
    }

    //if this crop is ready to be harvested, returns the gold for harvesting & selling it
    //resets the age to 1 after first harvest if this crop can be reharvested
    //Takes the FarmEvent as a parameter to log the harvesting
    public int harvest(FarmEvent event)
    {
        if (fullyGrown)
        {
            if (age == this.regrowthTime)
            {
                int gold = 0;
                if (chanceForMore != 0)
                {
                    gold = (int) Math.floor((chanceForMore*number)/100)*getSellPrice();
                }
                else
                {
                    gold = number*getSellPrice();
                }

                age = 1;
                event.addHarvestedCrops(this.clone(), gold);
                return gold;
            }
        }
        else if (age == this.growthTime)
        {
            int gold = 0;
            if (chanceForMore != 0)
            {
                gold = (int) Math.floor((chanceForMore*number)/100)*getSellPrice();
            }
            else
            {
                gold = number*getSellPrice();
            }

            age = 1;
            fullyGrown = true;
            event.addHarvestedCrops(this.clone(), gold);
            return gold;
        }
        return 0;
    }

    public int getNumber()
    {
        return this.number;
    }

    public int getAge()
    {
        return this.age;
    }

    private void setAge(int age)
    {
        this.age = age;
    }

    private void setFullyGrown(boolean fullyGrown)
    {
        this.fullyGrown = fullyGrown;
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
