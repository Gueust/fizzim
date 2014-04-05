package attributes;

/**
 * Index of the list of properties within the GlobalList
 * in FizzimGui
 */
public class EnumGlobalList {
  /* Positions of the attributes lists in globalList */
  public static int MACHINE = 0;
  public static int INPUTS = 1;
  public static int OUTPUTS = 2;
  public static int STATES = 3;
  public static int TRANSITIONS = 4;

  public static int fromInt(int parseInt) {
    if (parseInt == 0)
      return MACHINE;
    else if (parseInt == 1)
      return INPUTS;
    else if (parseInt == 2)
      return OUTPUTS;
    else if (parseInt == 3)
      return STATES;
    else
      return TRANSITIONS;
  }
}