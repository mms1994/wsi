import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int P = 100,			//liczba osobników (rozwiazań)
                N = 2,				//liczba zmiennych funkcji
                H = 100;			//liczba iteracji programu

        double  a=-1,			//poczatek przedzialu
                b=1,			//koniec przedzialu
                Pk = 0.8,		//prawodpobienstwo krzyzowania
                Pm = 0.05;		//prawdopodobienstwo mutacji

        double  dx=0.1;		//krok (dokladnosc)
        PrintWriter zapis = new PrintWriter("wynik.txt");
        zapis.println("-------------------------------------------------\nP = " + P + ", N = " + N + ", H = " + H + "\nPrzedzial: <" + a + ";" + b + ">");

        //liczba bitow
        int liczba_bitow;
        liczba_bitow = pop.Populacja.bity(dx, a, b);

        //zmienna przechowujaca wiersz najlepszego z elementow
        double []najl_osobnik = new double[N];
        double najl_wart;
        double []srednie_wart_osobnikow = new double[H];

        //nowa populacja
        int [][]tablica = pop.Populacja.pop(P, N, liczba_bitow);

        System.out.println("\n----------- POCZATKOWA POPULACJA -----------");
        pop.Populacja.wyswietl_pop(tablica, P, N, liczba_bitow);

        //nowa liczba korkow
        double dx2;
        dx2 = pop.Populacja.krok(a, b, liczba_bitow);
        zapis.println("\tKrok: "+ dx2 + "\tPk: " + Pk + "\tPm: " + Pm + "\n \n Nr. petli\tMaks"); //zapis do pliku

        //rozkodowowana populacja
        double[][] tab_roz;
        tab_roz = pop.Populacja.rozkoduj(tablica, P, N, liczba_bitow, dx2, a);

        System.out.println("\n----------- ROZKODOWANA POPULACJA----------- ");
        pop.Populacja.wyswietl_rozkod(tab_roz, P, N);

        //ocena populacji
        double [][]wc_tab;
        int nr_wiersza_max = 0;
        wc_tab = pop.Populacja.ocena_populacji(tab_roz, P, nr_wiersza_max);

        //wartosc max przed petla
        for(int i=0; i<N; i++)
        {
            najl_osobnik[i] = tab_roz[nr_wiersza_max][i];
        }
        najl_wart = wc_tab[nr_wiersza_max][0];

        System.out.println("\n----------- OCENA POPULACJI ----------- ");
        pop.Populacja.wyswietl_rozkod(wc_tab, P,1);

        for(int h=0; h<H; h++)
        {
            //ruletka
            pop.Populacja.Ruletka(tablica, wc_tab, P, N, liczba_bitow);

            //krzyzowanie
            pop.Populacja.Krzyzowanie(tablica, P, N, liczba_bitow, Pk);

            //mutacja
            pop.Populacja.Mutacja(tablica, P, N, liczba_bitow, Pm);

            //rozkodowanie
            tab_roz = pop.Populacja.rozkoduj(tablica, P, N, liczba_bitow, dx2, a);

            //ocena populacji
            wc_tab = pop.Populacja.ocena_populacji(tab_roz, P, nr_wiersza_max);

            if (najl_wart < wc_tab[nr_wiersza_max][0])
            {
                zapis.println(h + "\t=WARTOŚĆ(PODSTAW(\"" + wc_tab[nr_wiersza_max][0] + "\";\".\";\",\"))");
                najl_wart = wc_tab[nr_wiersza_max][0];
                for(int i=0; i<N; i++)
                {
                    najl_osobnik[i] = tab_roz[nr_wiersza_max][i];
                }
            }

            srednie_wart_osobnikow[h]=pop.Populacja.sr_wart(wc_tab, P);
        }
        System.out.println("\n----------- NAJLEPSZY OSOBNIK ----------- ");
        System.out.println("Wartosc najlepszego osobnika: " + najl_wart);
        System.out.println("Najlepszy osobnik to: ");

        System.out.println("| ");
        for(int j=0; j<N; j++)
        {
            System.out.println(najl_osobnik[j] + " | ");
        }
        System.out.println("");
        zapis.close();
    }
}
