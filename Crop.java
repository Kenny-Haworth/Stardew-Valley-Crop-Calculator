public class Crop implements Comparable<Crop>
{
    private String name;
    private int buyPrice;
    protected int sellPrice;
    protected int growthTime;
    protected int regrowthTime;
    private int numHarvested; //the number of times this crop can be harvested at harvest time
    protected int chanceForMore; //the chance for more of this crop to be harvested at harvest time (% chance)

    public Crop(String name, int buyPrice, int sellPrice, int growthTime, int regrowthTime, int numHarvested, int chanceForMore)
    {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.growthTime = growthTime;
        this.regrowthTime = regrowthTime;
        this.numHarvested = numHarvested;
        this.chanceForMore = chanceForMore;
    }

    //sorts crops into descending order of buy price
    @Override
    public int compareTo(Crop other)
    {
        return other.buyPrice - this.buyPrice;
    }

    //indicates whether this crop can continue to grow after harvest
    protected boolean canRegrow()
    {
        return (regrowthTime != 0);
    }

    public int getBuyPrice()
    {
        return this.buyPrice;
    }

    public int getSellPrice()
    {
        return this.sellPrice*this.numHarvested;
    }

    public int getGrowthTime()
    {
        return this.growthTime;
    }

    protected int getRegrowthTime()
    {
        return this.regrowthTime;
    }

    protected int getNumHarvested()
    {
        return this.numHarvested;
    }

    protected int getChanceForMore()
    {
        return this.chanceForMore;
    }

    public String getName()
    {
        return this.name;
    }
}
