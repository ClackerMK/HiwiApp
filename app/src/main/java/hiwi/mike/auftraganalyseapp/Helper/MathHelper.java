package hiwi.mike.auftraganalyseapp.Helper;


import java.util.ArrayList;

/**
 * Created by dave on 28.09.16.
 */
public class MathHelper {
    public static float[] mapArrayToRange(float[] source, float range_min, float range_max)
    {
        float source_min = source[0];
        float source_max = source[0];
        float[] output = new float[source.length];
        for (int i = 1; i < source.length; i++)
        {
            if (source[i] < source_min)
                source_min = source[i];
            else if (source[i] > source_max)
                source_max = source[i];
        }

        /*if (source_min == source_max)
        {
            for (int i = 0; i< source.length; i++)
            {
                if (source_min < range_min)
                    output[i] = range_min;
                else if(source_max > range_max)
                    output[i] = range_max;
                else
                    output[i] = range_min;
            }
        } else
        {*/
        if (source_min == source_max)
        {
            throw new IllegalArgumentException("Array benötigt mindestens zwei unterschiedliche Argumente");
        }

        for (int i = 0; i < source.length; i++)
        {
            output[i] = (range_min - source_min + source[i]) * (range_max-range_min) / (source_max-source_min);
        }
        //}
        return output;
    }

    public static ArrayList<Integer> createRange (int start, int end)
    {
        ArrayList<Integer> output = new ArrayList<>();

        for (int i = start; i < end; i++)
        {
            output.add(i);
        }
        return output;
    }

    public static ArrayList<Float> createRange (float start, float end)
    {
        ArrayList<Float> output = new ArrayList<>();

        for (float i = start; i < start-end; i++)
        {
            output.add(i);
        }
        return output;
    }
}
