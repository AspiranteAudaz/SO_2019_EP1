import java.util.LinkedList;
import java.util.Arrays;

public class Escalonador
{
    //Referencia ao sistema
    Sistema sys;

    //Tabela de bcps
    BCP tabelaBCP[]; 

    //Cada valor aponta para uma linha na tabela BCP
    LinkedList<Integer> listaProntos    = new LinkedList<Integer>();
    LinkedList<Integer> listaBloqueados = new LinkedList<Integer>();

    Escalonador(Sistema sys)
    {
        this.sys  = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = OrdenaTabelaBCP(sys.CarregaProgramas());

        //Adiciona todos os processos na lista de prontos
        for(int i = 0; i < tabelaBCP.length; i++)
        {
            //DEBUG - Ordenacao dos bcps por prioridade e nomes em caso de empate
            //System.out.println(tabelaBCP[i].nomeProcesso + " | " + tabelaBCP[i].prioridade);

            listaProntos.add(i);
        }
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