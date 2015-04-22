import pop.Populacja;

public class Main {
    static final int P = 30;  //P-liczba osobnikow
    static final int N = 3; //N-liczba kolumn kazdego osobnika tzn. X1, X2, X3 ...
    static final double dx = 0.0001; //dx-poczatkowy krok czyli dokladnosc.
    static final int a = -2;  //a-poczatek b-koniec przedzialu.
    static final int b = 2;
    static final double	Pk = 0.75; //Pk-prawdopodobienstwo krzyzowania
    static final double	Pm  = 0.25; //Pm-Parametr mutacji(prawdopodobienstwo).
    static final int T = 5000; //T-liczba iteracji w petli

    public static void main(String[] args) {
        double najl_osobnik[] = new double[N];
        double srednie_wart_osobnikow[] = new double[T];
        double najl_wart;

        Populacja pop = new Populacja(P,N,a,b,dx);

        double wc_tab[][]; //ocena populacji
        Double nr_wiersza_max = new Double(0);
        wc_tab = pop.ocena_populacji(P, nr_wiersza_max);


        //wartosc max przed metla
        for(int i=0; i<N; i++)
        {
            najl_osobnik[i] = pop.getmaxroz(nr_wiersza_max.doubleValue(),i);
        }
        najl_wart = wc_tab[(int)nr_wiersza_max.doubleValue()][0];


        for(int h=0; h<T; h++)
        {
            pop.ruletka(wc_tab, P, N);

            //krzyzowanie
            pop.Krzyzowanie(P, N, Pk);

            //mutacja
            pop.Mutacja(P, N,Pm);

            //rozkodowanie
            pop.do_dziesietnej(P, N, pop.bits, pop.dx2, a);

            //ocena populacji
            wc_tab = pop.ocena_populacji(P, nr_wiersza_max);

            if (najl_wart < wc_tab[(int)nr_wiersza_max.doubleValue()][0])
            {
                najl_wart = wc_tab[(int)nr_wiersza_max.doubleValue()][0];
                for(int i=0; i<N; i++)
                {
                    najl_osobnik[i] = pop.getmaxroz((int)nr_wiersza_max.doubleValue(),i);
                }
            }

            srednie_wart_osobnikow[h]= pop.sr_przystosowanie(wc_tab, P);
        }
        System.out.println("\n----------- NAJLEPSZY OSOBNIK ----------- ");
        System.out.println("Wartosc najlepszego osobnika: " + najl_wart);
        System.out.println("Najlepszy osobnik to: ");

        for(double j: najl_osobnik)
        {
            System.out.print(j + " | ");
        }
        System.out.println();
    }
}