package pop;
import java.util.Random;

/**
 * Created by Michał on 2015-03-26.
 */

public class Populacja {
    public static int bity(double dx, double a, double b) {
        int bit = 0;
        while (Math.pow(2, (double) bit) - 1 < Math.ceil((b - a) * (1 / dx)))
            bit++;
        return bit;
    }

    // ustawienie kroku
    public static double krok(int bit, double a, double b) {
        double k = (Math.abs(b - a)) / (Math.pow(2, bit) - 1);
        return k;
    }

    // losowanie populacji
    public static int[][]pop(int P, int N, int G) {

        int tab[][] = new int[P][N * G];

        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++) {
                Random generator = new Random();
                tab[i][j] = generator.nextInt() * 2;
            }


        return tab;
    }

    //wyswietlenie populacji
    public static void wyswietl_pop(int pop[][], int P, int N, int G) {
        for (int i = 0; i < P; i++) {
            System.out.print("| ");
            for (int j = 0; j < N * G; j++) {
                System.out.print(pop[i][j]);
                if ((j + 1) % G == 0)
                    System.out.print("| ");
            }
            System.out.println("");
        }
    }

    //rozkodowanie tablicy
    public static double[][] rozkoduj(int pop[][], int P, int N, int G, double dx, double a) {
        double tab[][] = new double[P][N];
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N; j++)
                tab[i][j] = 0;

        int waga;
        for (int i = 0; i < P; i++) {
            for (int k = 1; k <= N; k++) {
                waga = 0;
                for (int j = (k * G) - 1; j >= G * (k - 1); j--) {
                    tab[i][k - 1] += Math.pow(2, (double) waga) * pop[i][j];
                    waga++;
                }
                tab[i][k - 1] = tab[i][k - 1] * dx + a;
            }
        }
        return tab;
    }

    //wyswietlenie rozkodowanej tablicy
    public static void wyswietl_rozkod(double tab_rozk[][], int P, int N) {
        for (int i = 0; i < P; i++) {
            System.out.println("| ");
            for (int j = 0; j < N; j++) {
                System.out.print(tab_rozk[i][j]);
                System.out.print(" | ");
            }
            System.out.println("");
        }
    }

    //funkcja celu
    public static double f_celu(double x, double y) {
        return (-(20 + ((x * x) - (10 * Math.cos(2 * 3.14 * x)) + ((y * y) - (10 * Math.cos(2 * 3.14 * y))))));
    }

    //ocena populacji
    public static double[][] ocena_populacji(double tab_rozk[][], int P, int nr_wiersza_max) {
        double[][] tab = new double[P][1];

        double max = f_celu(tab_rozk[0][0], tab_rozk[0][1]);        //modyfikujemy z zaleznosci od tego ile zmiennych mamy w funkcji
        nr_wiersza_max = 0;

        for (int i = 0; i < P; i++) {
            tab[i][0] = f_celu(tab_rozk[i][0], tab_rozk[i][1]); //modyfikujemy z zaleznosci od tego ile zmiennych mamy w funkcji
            if (tab[i][0] > max)
                nr_wiersza_max = i;
        }

        return tab;
    }

    //suma wartosci
    public static double sr_wart(double wc_tab[][], int P) {
        double suma = 0;
        for (int i = 0; i < P; i++)
            suma += wc_tab[i][0];

        return suma / P;
    }

    //ruletka
    public static void Ruletka(int pop[][], double wc_tab[][], int P, int N, int G) {
        //tablica z pradopodobieństwami
        double[][] tab = new double[P][1];

        //kopia wejsciowej tablicy z nowa populacja (tablica pop)
        int[][] pop_temp = new int[P][N * G];

        //szukamy wartosc minimalnej w tablicy z wartosciami funkcji oraz sumujemy elementy z tej tablicy
        double min = wc_tab[0][0], suma_wc_tab = 0, suma_wekt_prawdo = 0;
        for (int i = 0; i < P; i++) {
            if (wc_tab[i][0] < min)
                min = wc_tab[i][0];
            suma_wc_tab += wc_tab[i][0]; //suma elementow tablicy z wartosciami
        }

        if (min >= 0) {
            for (int i = 0; i < P; i++) {
                tab[i][0] = wc_tab[i][0] / suma_wc_tab;
                suma_wekt_prawdo += tab[i][0];
            }
        } else {
            suma_wc_tab += P * (Math.abs(min) + 1);
            for (int i = 0; i < P; i++) {
                tab[i][0] = (wc_tab[i][0] + Math.abs(min) + 1) / suma_wc_tab;
                suma_wekt_prawdo += tab[i][0];
            }
        }
        Random generatore = new Random();
        double wart_losowa;
        double suma_el = 0;
        int wiersz = 0;
        boolean wynik;

        //losowanie wartosci
        for (int k = 0; k < P; k++) {
            wart_losowa = (double) generatore.nextDouble();
            suma_el = 0;
            wynik = false;

            for (int i = 0; i < P; i++) {
                suma_el += tab[i][0];
                if (wart_losowa < suma_el) {
                    wiersz = i;
                    wynik = true;
                }
                if (wynik) break;
            }

            for (int j = 0; j < N * G; j++)
                pop_temp[k][j] = pop[wiersz][j];
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie ruletki
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++)
                pop[i][j] = pop_temp[i][j];
    }

    //krzyzowanie
    public static void Krzyzowanie(int pop[][], int P, int N, int G, double Pk) {
        if ((Pk < 0) || (Pk > 1)) {
            System.out.println("Bledna wartosc prawodpodobienswa krzyzowania. Musi sie zawierac w przedziale [0;1]");
            return;
        }

        //kopia wejsciowej tablicy z nowa populacja (tablica pop)
        int pop_temp[][] = new int[P][N * G];

        //przekopiowanie elementow z wejsciowej tablicy pop do jej kopii
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++)
                pop_temp[i][j] = pop[i][j];


        int bit_przeciecia;
        double r;
        Random generatora = new Random();
        for (int j = 0; j < (P - 1); j = j + 2) {
            r = generatora.nextDouble();
            //cout << "Wartosc losowa: " << r << endl;

            bit_przeciecia = generatora.nextInt(N * G - 1) + 1;
            //cout << "Bit przeciecia: " << bit_przeciecia << endl;

            if (r >= Pk)
                continue;

            //poczatek
            for (int i = 0; i < bit_przeciecia; i++) {
                pop_temp[j][i] = pop[j][i];
                pop_temp[j + 1][i] = pop[j + 1][i];
            }

            //koniec
            for (int i = bit_przeciecia; i < N * G; i++) {
                pop_temp[j][i] = pop[j + 1][i];
                pop_temp[j + 1][i] = pop[j][i];
            }
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie ruletki
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++)
                pop[i][j] = pop_temp[i][j];
    }

    //mutacja
    public static void Mutacja(int pop[][], int P, int N, int G, double Pm) {
        if ((Pm < 0) || (Pm > 1)) {
            System.out.println("Bledna wartosc prawodpodobienswa mutacji. Musi sie zawierac w przedziale [0;1]");
            return;
        }

        //kopia wejsciowej tablicy z nowa populacja (tablica pop)
        int pop_temp[][] = new int[P][N * G];

        //przekopiowanie elementow z wejsciowej tablicy pop do jej kopii
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++)
                pop_temp[i][j] = pop[i][j];

        double r;
        Random generatoro = new Random();
        for (int i = 0; i < P; i++) {
            for (int j = 0; j < N * G; j++) {
                r = generatoro.nextDouble();

                if (r >= Pm)
                    continue;

                if (pop_temp[i][j] == 0)
                    pop_temp[i][j] = 1;
                else pop_temp[i][j] = 0;
            }
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie ruletki
        for (int i = 0; i < P; i++)
            for (int j = 0; j < N * G; j++)
                pop[i][j] = pop_temp[i][j];
    }
}