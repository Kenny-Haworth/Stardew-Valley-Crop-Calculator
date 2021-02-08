//to represent crop and tool quality
public enum Level
{
    BASIC   (0),
    COPPER  (1),
    STEEL   (2),
    GOLD    (3),
    IRIDIUM (4);

    private int value;
    private Level(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static Level fromInt(int x)
    {
        switch(x)
        {
            default:
            case 0:
                return BASIC;
            case 1:
                return COPPER;
            case 2:
                return STEEL;
            case 3:
                return GOLD;
            case 4:
                return IRIDIUM;
        }
    }
}
