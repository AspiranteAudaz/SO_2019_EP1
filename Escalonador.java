import java.util.Vector;
import java.util.Arrays;

public class Escalonador
{
    //Referencia ao sistema
    Sistema sys;

    //Tabela de bcps
    Vector<BCP> tabelaBCP;

    //Cada valor aponta para uma linha na tabela BCP
    Vector<Integer> listaBloqueados = new Vector<Integer>();

    Vector<Vector<Integer>> listasProntos;

    Escalonador(Sistema sys)
    {
        this.sys  = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = OrdenaTabelaBCP(sys.CarregaProgramas());

        //Gera as listas de creditos (prontos)
        listasProntos = GeraListas(tabelaBCP);
    }

    private Vector<Vector<Integer>> GeraListas(Vector<BCP> tabelaBCP)
    {
        int maior_credito = 0;

        //Pega maior valor
        for(int i = 0; i < tabelaBCP.size(); i++)
        {
            if(tabelaBCP.get(i).creditos > maior_credito)
                maior_credito = tabelaBCP.get(i).creditos;
        }

        Vector<Vector<Integer>> listas_creditos = new Vector<Vector<Integer>>();

        //Cria listas
        for(int i = 0; i < maior_credito + 1; i++)
            listas_creditos.add(new Vector<Integer>());

        //Adiciona uma referencia para o processo na tabela BCP na sua respectiva fila
        for(int i = 0; i < tabelaBCP.size(); i++)
            listas_creditos.get(tabelaBCP.get(i).creditos).add(i);

        return listas_creditos;
    }

    private Vector<BCP> OrdenaTabelaBCP(Vector<BCP> tabelaBCP)
    {
        String nomes[] = new String[2];
        int max        = 0;

        for(int i = 0; i < tabelaBCP.size(); i++)
        {
            BCP temp;
            for(int j = 0; j < tabelaBCP.size() - i - 1; j++)
            {
                if(tabelaBCP.get(j).prioridade > tabelaBCP.get(j + 1).prioridade)
                {
                    temp = tabelaBCP.get(j);
                    tabelaBCP.setElementAt(tabelaBCP.get(j+1), j);
                    tabelaBCP.setElementAt(temp, j+1);
                }
                else if(tabelaBCP.get(j).prioridade == tabelaBCP.get(j+1).prioridade)
                {
                    //Se empatar 
                    nomes[0] = tabelaBCP.get(j).nomeProcesso;
                    nomes[1] = tabelaBCP.get(j+1).nomeProcesso;
                    Arrays.sort(nomes);

                    if(tabelaBCP.get(j).nomeProcesso.equals(nomes[1]))
                    {
                        temp = tabelaBCP.get(j);
                        tabelaBCP.setElementAt(tabelaBCP.get(j+1), j);
                        tabelaBCP.setElementAt(temp, j+1);
                    }
                }
            }
        }

        return tabelaBCP;
    }
}