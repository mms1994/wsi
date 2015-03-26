package pop;
import java.util.Random;

/**
 * Created by Michał on 2015-03-26.
 */

public class Populacja {
    public static int bity(double dx, double a, double b)
    {
        int bit = 0;
        while (Math.pow(2, (double)bit) - 1 < Math.ceil((b - a) * (1 / dx)))
            bit++;
        return bit;
    }
    // ustawienie kroku
    public static double krok(int bit, double a, double b)
    {
        double k = (Math.abs(b - a)) / (Math.pow(2, bit) - 1);
        return k;
    }
// losowanie populacji
    public static int pop(int P, int N, int G)
    {

        int tab [][]= new int [P][N*G];

        for (int i = 0; i<P; i++)
            for (int j = 0; j<N*G; j++)
            {
                Random generator = new Random();
                tab[i][j]=generator.nextInt()*2;
            }


        return tab[P][N*G];
    }
//wyswietlenie populacji
    public static void wyswietl_pop(int pop[][], int P, int N, int G)
    {
        for(int i=0; i<P; i++)
        {
            System.out.print("| ");
            for(int j=0; j<N*G; j++)
            {
                System.out.print(pop[i][j]);
                if ((j+1)%G==0)
                    System.out.print("| ");
            }
            System.out.println("");
        }
    }

}
