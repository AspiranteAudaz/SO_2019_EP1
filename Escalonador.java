import java.util.Vector;
import java.util.Arrays;

public class Escalonador
{
    //Referencia ao sistema
    Sistema sys;

    //Tabela de bcps
    Vector<BCP> tabelaBCP;

    Vector<BCP> listaBloqueados = new Vector<BCP>();

    //Lista de listas de processos prontos, por creditos
    Vector<Vector<BCP>> listasProntos;

    //Numero de processos não terminados
    int num_processos_ativos = 0;

    int fila_atual = 0;

    Escalonador(Sistema sys)
    {
        this.sys = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = OrdenaListaProcessos(sys.CarregaProgramas());

        //Gera as listas de creditos (prontos)
        listasProntos = GeraListas(tabelaBCP);

        //DEBUG
        System.out.println("Lista de listas de processos, inicialmente: ");
        for(int i = 0; i < listasProntos.size(); i++)
        {
            for(int j = 0; j < listasProntos.get(i).size(); j++)
                System.out.println("lista: " + i + " prioridade: " + listasProntos.get(i).get(j).prioridade);
        }
    }

    void Escalona()
    {
        BCP processo_escalonado = EscalonaProcesso();
        while(num_processos_ativos > 0)
        {
            char evento = sys.Executa(processo_escalonado);

            switch (evento) 
            {
                case Sistema.SAIDA:
                    num_processos_ativos--;
                    break;
            
                default:
                    break;
            }
        }
    }

    private BCP EscalonaProcesso()
    {
        if(listasProntos.get(fila_atual).size() == 0)
        {
            if(fila_atual == 0)
            {
                RedistribuiProcessosFilas(listasProntos);
            }

            fila_atual--;
            return EscalonaProcesso();
        }

        OrdenaListaProcessos(listasProntos.get(fila_atual));

        //n testei se e o maior
        return listasProntos.get(fila_atual).remove(0);
    }

    private void RedistribuiProcessosFilas(Vector<Vector<BCP>> listas_creditos)
    {
        fila_atual = listas_creditos.size();

        //Adiciona uma referencia para o processo na tabela BCP na sua respectiva fila
        for(int i = 0; i < tabelaBCP.size(); i++)
        {
            listas_creditos.get(tabelaBCP.get(i).creditos).add(tabelaBCP.get(i));
            tabelaBCP.get(i).creditos_atual = tabelaBCP.get(i).creditos;
        }
    }

    //Gera as listas de processos prontos, por creditos
    private Vector<Vector<BCP>> GeraListas(Vector<BCP> tabelaBCP)
    {
        num_processos_ativos = tabelaBCP.size();

        int maior_credito = 0;

        //Pega maior valor
        for(int i = 0; i < tabelaBCP.size(); i++)
        {
            if(tabelaBCP.get(i).creditos > maior_credito)
                maior_credito = tabelaBCP.get(i).creditos;
        }

        Vector<Vector<BCP>> listas_creditos = new Vector<Vector<BCP>>();

        //Cria listas
        for(int i = 0; i < maior_credito + 1; i++)
            listas_creditos.add(new Vector<BCP>());

        RedistribuiProcessosFilas(listas_creditos);

        return listas_creditos;
    }

    private Vector<BCP> OrdenaListaProcessos(Vector<BCP> tabelaBCP)
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