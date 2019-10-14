import java.util.Vector;
import java.util.Arrays;

public class Escalonador
{
    //Referencia ao sistema
    Sistema sys;

    //Tabela de bcps
    BCP tabelaBCP[]; 

    //Cada valor aponta para uma linha na tabela BCP
    Vector<Integer> listaBloqueados = new Vector<Integer>();

    Vector<Vector<Integer>> listasProntos;

    Escalonador(Sistema sys)
    {
        this.sys  = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = OrdenaTabelaBCP(sys.CarregaProgramas());

        listasProntos = GeraListas(tabelaBCP);
    }

    private Vector<Vector<Integer>> GeraListas(BCP tabelaBCP[])
    {
        int maior_credito = 0;

        //Pega maior valor
        for(int i = 0; i < tabelaBCP.length; i++)
        {
            if(tabelaBCP[i].creditos > maior_credito)
                maior_credito = tabelaBCP[i].creditos;
        }

        Vector<Vector<Integer>> listas_creditos = new Vector<Vector<Integer>>();

        //Cria listas
        for(int i = 0; i < listas_creditos.size(); i++)
            listas_creditos.add(new Vector<Integer>());

        //Adiciona uma referencia para o processo na tabela BCP na sua respectiva fila
        for(int i = 0; i < tabelaBCP.length; i++)
        {
            listas_creditos.get(i).add(tabelaBCP[i].creditos);
        }

        return listas_creditos;
    }

    private BCP[] OrdenaTabelaBCP(BCP tabelaBCP[])
    {
        String nomes[] = new String[2];
        int max        = 0;

        for(int i = 0; i < tabelaBCP.length; i++)
        {
            BCP temp;
            for(int j = 0; j < tabelaBCP.length - i - 1; j++)
            {
                if(tabelaBCP[j].prioridade > tabelaBCP[j + 1].prioridade)
                {
                    temp             = tabelaBCP[j];
                    tabelaBCP[j]     = tabelaBCP[j + 1];
                    tabelaBCP[j + 1] = temp;
                }
                else if(tabelaBCP[j].prioridade == tabelaBCP[j+1].prioridade)
                {
                    //Se empatar 
                    nomes[0] = tabelaBCP[j  ].nomeProcesso;
                    nomes[1] = tabelaBCP[j+1].nomeProcesso;
                    Arrays.sort(nomes);

                    if(tabelaBCP[j].nomeProcesso.equals(nomes[1]))
                    {
                        temp             = tabelaBCP[j];
                        tabelaBCP[j]     = tabelaBCP[j + 1];
                        tabelaBCP[j + 1] = temp;
                    }
                }
            }
        }

        return tabelaBCP;
    }
}