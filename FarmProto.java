import java.util.ArrayList;

/**
 * This class represents a prototype of a Farm.
 *
 * In particular, this class contains only new seeds to plant.
 * This class can be forged into a fully fledged Farm given additional information.
 */
public class FarmProto
{
    private ArrayList<CropGroup> newSeeds;
    private int gold;

    public FarmProto(ArrayList<CropGroup> newCrops, int gold)
    {
        this.newSeeds = new ArrayList<>(newCrops);
        this.gold = gold;
    }

    //creates a Farm from this proto using the calling farm's fields
    public Farm createFarm(ArrayList<CropGroup> currentCrops,
                           ArrayList<FarmEvent> events,
                           int remainingGold,
                           int goldCache)
    {
        //update log
        FarmEvent farmEvent = events.get(events.size()-1);
        farmEvent.setSeedsPurchased(newSeeds);
        farmEvent.setEndingGold(gold+remainingGold, goldCache);

        ArrayList<CropGroup> totalCrops = new ArrayList<>();

        //add the new seeds to plant
        for (CropGroup newSeed : newSeeds)
        {
            totalCrops.add(newSeed.clone());
        }

        //add the existing plants
        for (CropGroup currentCrop : currentCrops)
        {
            totalCrops.add(currentCrop.clone());
        }

        //clone the events
        ArrayList<FarmEvent> eventsCopy = new ArrayList<>(events.size());
        for (FarmEvent event : events)
        {
            eventsCopy.add(event.clone());
        }

        return new Farm(totalCrops, gold+remainingGold, goldCache, eventsCopy);
    }
}
