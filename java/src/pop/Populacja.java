package pop;

import java.util.Random;

public class Populacja {
    public int bits;
    int pop[][];
    public double dx2;
    double pop_dec[][];

    //liczenie liczby bitow (genów)
    private void bity(double a, double b, double dx) {
        int bit=0;
        while(Math.pow(2, (double)bit)-1 < Math.ceil((b-a) * (1/dx)))
            bit++;
        bits=bit;
    }

    //nowy krok (dx_2)
    private void krok(double a, double b, double bit) {
        dx2 = (Math.abs(b-a)) / (Math.pow(2,bit)-1);
    }

    //konstruktor:
    public Populacja(int P, int N, int a, int b, double dx) {
        this.bity(a, b, dx);	//liczenie liczby bitow (genów)
        this.krok(a, b, bits);	//nowy krok (dx_2)

        pop = new int[P][N*bits];	//losowanie nowej populacji:
        Random generator = new Random();
        for (int i=0; i<P; i++)
        {
            for (int j=0; j<N*bits; j++)
            {
                pop[i][j] = generator.nextInt(2);
            }
        }
        this.do_dziesietnej(P, N, bits, dx2, a);
    }

    //zamiana POPulacji na dziesietne...
    public void do_dziesietnej(int P, int N, int B, double dx, double a) {
        pop_dec = new double[P][N];	//tablica rozkodowanej populacji.

        for(int i=0; i<P; i++)
            for(int j=0; j<N; j++)
                pop_dec[i][j] = 0;
        int waga;
        for(int i=0; i<P; i++)
        {
            for(int k = 1; k<=N; k++)
            {
                waga=0;
                for(int j=(k*B)-1; j>=B*(k-1); j--)
                {
                    pop_dec[i][k-1] += Math.pow(2, (double)waga) * pop[i][j];
                    waga++;
                }
                pop_dec[i][k-1] = pop_dec[i][k-1] * dx + a;
            }
        }
    }

    private double f_celu(double x1, double x2, double x3) {
        //double cel = (-x1*x1-x2*x2-x3*x3);
        double tabx[]= new double[3];
        tabx[0]=x1;
        tabx[1]=x2;
        tabx[2]=x3;
        double xi=0;
        double cos=0;
        for(int i=0; i<3; i++)
        {
            xi+=tabx[i]*tabx[i];
            cos+=Math.cos(2*Math.PI*tabx[i]);
        }
        double cel = -(-20*Math.exp(-0.2*Math.sqrt((xi/3.0)-Math.exp(cos/3.0))));
        return cel;
    }

    public double[][] ocena_populacji(int P, double wiersz_max)
    {
        double mat[][] = new double[P][1];
        double max = f_celu(this.pop_dec[0][0], this.pop_dec[0][1], this.pop_dec[0][2]);
        wiersz_max = 0.0;
        for(int i=0; i<P; i++)
        {
            mat[i][0] = f_celu(this.pop_dec[i][0], this.pop_dec[i][1], this.pop_dec[i][2]);
            if(mat[i][0]>max) {
                wiersz_max=(double)i;
                max=mat[i][0];
            }
        }
        return mat;
    }


    //metoda selekcji(RULETKA):
    public void ruletka(double matrix_oc[][], int P, int N) {
        double tab[][] = new double [P][1]; //tablica z pradopodobienstwami
        int pop_temp[][] = new int [P][N*bits]; //kopia wejsciowej tablicy z nowa populacja

        //szukamy wartosc minimalnej w tablicy z wartosciami funkcji oraz sumujemy elementy z tej tablicy
        double min = matrix_oc[0][0], suma_matrix_oc = 0, suma_vec_prawdo = 0;
        for(int i=0; i<P; i++)
        {
            if(matrix_oc[i][0]<min)
                min=matrix_oc[i][0];
            suma_matrix_oc += matrix_oc[i][0]; //suma elementow tablicy z wartosciami
        }

        if(min>=0)
        {
            for(int i=0; i<P; i++)
            {
                tab[i][0] = matrix_oc[i][0]/suma_matrix_oc;
                suma_vec_prawdo+=tab[i][0];
            }
        }
        else
        {
            suma_matrix_oc += P*(Math.abs(min)+1);
            for(int i =0; i<P; i++)
            {
                tab[i][0] = (matrix_oc[i][0]+Math.abs(min)+1)/suma_matrix_oc;
                suma_vec_prawdo+=tab[i][0];
            }
        }

        Random generator = new Random();
        double war_losowa;
        double suma_el = 0;
        int nr_wiersza = 0;
        boolean wynik;

        //losowanie wartosci
        for(int k=0; k<P; k++)
        {
            war_losowa = generator.nextDouble();
            suma_el = 0;
            wynik=false;

            for(int i=0; i<P; i++)
            {
                suma_el += tab[i][0];
                if(war_losowa<suma_el)
                {
                    nr_wiersza = i;
                    wynik=true;
                }
                if(wynik) break;
            }

            for(int j=0; j<N*bits; j++)
                pop_temp[k][j] = this.pop[nr_wiersza][j];
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie ruletki
        for(int i =0; i<P; i++)
            for(int j=0; j<N*bits; j++)
                this.pop[i][j] = pop_temp[i][j];

    }

    public void Krzyzowanie(int P, int N, double Pk) {
        if ((Pk<0) || (Pk>1))
        {
            System.out.println("Bledna wartosc prawodpodobienswa krzyzowania. Musi sie zawierac w przedziale [0;1]");
            return;
        }

        //kopia wejsciowej tablicy z nowa populacja (tablica pop)
        int pop_temp[][] = new int [P][N*bits];

        //przekopiowanie elementow z wejsciowej tablicy pop do jej kopii
        for(int i =0; i<P; i++)
            for(int j=0; j<N*bits; j++)
                pop_temp[i][j] = this.pop[i][j];

        Random generator = new Random();
        int bit_przeciecia;
        double r;

        for(int j=0; j<(P-1); j=j+2)
        {
            r=generator.nextDouble();
            bit_przeciecia = Math.abs((generator.nextInt()%((N*bits)-1))) +1;

            if(r>=Pk)
                continue;

            //poczatek
            for(int i=0; i<bit_przeciecia; i++)
            {
                pop_temp[j][i]=this.pop[j][i];
                pop_temp[j+1][i]=this.pop[j+1][i];
            }

            //koniec
            for(int i=bit_przeciecia; i<N*bits; i++)
            {
                pop_temp[j][i]=this.pop[j+1][i];
                pop_temp[j+1][i]=this.pop[j][i];
            }
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie krzyzowanie
        for(int i =0; i<P; i++)
            for(int j=0; j<N*bits; j++)
                this.pop[i][j] = pop_temp[i][j];
    }

    //mutacja
    public void Mutacja(int P, int N, double Pm)
    {
        if ((Pm <0) || (Pm >1))
        {
            System.out.println("Bledna wartosc prawodpodobienswa krzyzowania. Musi sie zawierac w przedziale [0;1]");
            return;
        }

        //kopia wejsciowej tablicy z nowa populacja (tablica pop)
        int[][] pop_temp = new int [P][N*bits];

        //przekopiowanie elementow z wejsciowej tablicy pop do jej kopii
        for(int i =0; i<P; i++)
            for(int j=0; j<N*bits; j++)
                pop_temp[i][j] = this.pop[i][j];

        Random generator = new Random();
        double r;
        for(int i =0; i<P; i++)
        {
            for(int j=0; j<N*bits; j++)
            {
                r=generator.nextDouble();
                if(r>=Pm)
                    continue;
                if (pop_temp[i][j]==0)
                    pop_temp[i][j]=1;
                else
                    pop_temp[i][j]=0;
            }
        }

        //przypisanie tablicy pop przekazanej do funkcji elementow wylosowanych w procesie mutacji
        for(int i =0; i<P; i++)
            for(int j=0; j<N*bits; j++)
                this.pop[i][j] = pop_temp[i][j];
    }

    public double sr_przystosowanie(double matrix_oc[][], int P)
    {
        double suma = 0;
        for(int i=0; i<P; i++)
            suma+=matrix_oc[i][0];
        return suma/P;
    }

    public double getmaxroz(double d, int n)
    {
        return this.pop_dec[(int)d][n];
    }

    public void wyswietl_pop() {
        for(int i=0; i<pop.length; i++)
        {
            for(int j=0; j< pop[i].length; j++)
                System.out.print(pop[i][j] + " ");
            System.out.println();
        }
    }

    public void wyswietl_dziesietna() {
        for(int i=0; i<pop_dec.length; i++){
            for(int j=0; j<pop_dec[i].length; j++)
                System.out.print(pop_dec[i][j] + " ");
            System.out.println();
        }

    }

//koniec class Pupulacja//
}
