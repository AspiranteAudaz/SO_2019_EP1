import java.util.Vector;
import java.util.Arrays;
import java.util.Scanner;

public class Escalonador
{
    final int TEMPO_ESPERA = 2;

    //Para debug
    private static final int     _sleep_time = 0;
    private static final boolean _debug      = true;
    private Scanner scanner;

    //Referencia ao sistema
    private Sistema sys;

    //Tabela de bcps
    private Vector<BCP> tabelaBCP;

    private Vector<BCP> listaBloqueados = new Vector<BCP>();

    //Lista de listas de processos prontos, por creditos
    private Vector<Vector<BCP>> listasProntos;

    private int quantas_passados = 0;

    //Numero de processos não terminados
    int num_processos_ativos = 0;

    int fila_atual = 0;

    Escalonador(Sistema sys)
    {
        if(_debug)
            scanner = new Scanner(System.in);

        this.sys = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = OrdenaListaProcessos(sys.CarregaProgramas());

        //Gera as listas de creditos (prontos)
        listasProntos = GeraListas(tabelaBCP);

        //DEBUG
        Vector<BCP> p;
        System.out.println("Lista de listas de processos, inicialmente: ");
        int size = listasProntos.size();
        int size_p;
        for(int i = 0; i < size; i++)
        {
            p = listasProntos.get(i);
            size_p = p.size();
            for(int j = 0; j < size_p; j++)
                System.out.println("lista: " + i + " prioridade: " + p.get(j).prioridade);
        }
    }

    void Escalona()
    {
        BCP processo_escalonado;
        char evento;

        while(num_processos_ativos > 0)
        {
            processo_escalonado = EscalonaProcesso();

            if(processo_escalonado == null)
            {
                DecrementaTempoBloqueados();
                continue;
            }

            evento = sys.Executa(processo_escalonado);

            //DEBUG
            Sleep();
            System.out.println("| N: " + processo_escalonado.nomeProcesso + " | C: " + processo_escalonado.creditos_atual + " | E: " + evento  + " | PC: " + processo_escalonado.PC + " | F: " + processo_escalonado.fresco + " | Q: " + quantas_passados);

            switch (evento) 
            {
                case Sistema.PREEMPCAO:
                
                    RemoveProcesso(processo_escalonado);
                    if(processo_escalonado.fresco)
                        AdicionaProcesso(AdicionaQuantum(RetiraCredito(processo_escalonado, 2), 2));
                    else
                        AdicionaProcesso(AdicionaQuantum(RetiraCredito(processo_escalonado, 2), 1));
                    DecrementaTempoBloqueados();
                    break;

                case Sistema.SAIDA:
                    MataProcesso(processo_escalonado);
                    DecrementaTempoBloqueados();
                    break;
                
                case Sistema.BLOQUEADO:
                    DecrementaTempoBloqueados();
                    BloqueiaProcesso(processo_escalonado);
                    break;

                case Sistema.ERROFATAL:
                    System.out.println("ERROFATAL");
                    //Wait();

                default:
                    break;
            }

            quantas_passados++;
        }
    }


    private BCP EscalonaProcesso()
    {
        if(fila_atual == 0)
        {
            RedistribuiProcessosFilas(listasProntos);
            System.out.println("Processos redistribuidos!");

            return EscalonaProcesso();
        }

        if(listasProntos.get(fila_atual).size() == 0)
        {
            fila_atual--;

            return EscalonaProcesso();
        }

        OrdenaListaProcessos(listasProntos.get(fila_atual));

        //n testei se e o maior
        return listasProntos.get(fila_atual).remove(listasProntos.get(fila_atual).size() - 1);
    }

    private void DecrementaTempoBloqueados()
    {
        BCP p;

        int size = listaBloqueados.size();
        int k = 0;

        for(int i = 0; i < size; i++)
        {
            p = listaBloqueados.get(k);
            p.tempo_espera -= 1;
            if(p.tempo_espera == 0)
            {
                //DEBUG
                System.out.println("| N: " + p.nomeProcesso + " | C: " + p.creditos_atual + " | PC: " + p.PC + " | F: " + p.fresco + " | Q: " + quantas_passados + " | DESBLOQUEADO!");
                DesloqueiaProcesso(p);
                k--;
            }
            k++;
        }
    }

    private BCP BloqueiaProcesso(BCP p)
    {
        listaBloqueados.add(RemoveProcesso(AdicionaQuantum(RetiraCredito(p, 2), 1)));
        p.tempo_espera = TEMPO_ESPERA;
        
        return p;
    }

    private BCP DesloqueiaProcesso(BCP p)
    {
        AdicionaProcesso(p);
        listaBloqueados.remove(p);
        p.estado = BCP.PRONTO;

        return p;
    }

    private BCP RetiraCredito(BCP p, int num)
    {
        p.fresco          = false;
        p.creditos_atual -= num;

        if(p.creditos_atual < 0)
            p.creditos_atual = 0;

        return p;
    }

    private BCP AdicionaQuantum(BCP p, int num)
    {
        p.quantum_atual += num;

        return p;
    }

    private void RedistribuiProcessosFilas(Vector<Vector<BCP>> listas_creditos)
    {
        fila_atual = listas_creditos.size() - 1;

        if(fila_atual < 0)
            StackTrace("fila_atual com valor negativo");

        BCP p;
        int tbcp_size = tabelaBCP.size();
        //Adiciona uma referencia para o processo na tabela BCP na sua respectiva fila
        for(int i = 0; i < tbcp_size; i++)
        {
            p = tabelaBCP.get(i);
            listas_creditos.get(p.creditos).add(p);
            p.creditos_atual = p.creditos;
            p.quantum_atual  = 1;
            p.fresco         = true;
        }
    }

    private BCP MataProcesso(BCP p)
    {
        tabelaBCP.remove(p);
        RemoveProcesso(p);
        num_processos_ativos--;

        if(num_processos_ativos < 0)
            StackTrace("num_processos_ativos negativo");

        return p;
    }

    private BCP RemoveProcesso(BCP p)
    {
        listasProntos.get(p.creditos_atual).remove(p);

        return p;
    }

    private BCP AdicionaProcesso(BCP p)
    {
        return AdicionaProcesso(p, p.creditos_atual);
    }

    private BCP AdicionaProcesso(BCP p, int fila)
    {
        p.creditos_atual = fila;
        listasProntos.get(fila).add(p);
        OrdenaListaProcessos(listasProntos.get(fila));

        return p;
    }

    //Gera as listas de processos prontos, por creditos
    private Vector<Vector<BCP>> GeraListas(Vector<BCP> tabelaBCP)
    {
        num_processos_ativos = tabelaBCP.size();

        int maior_credito = 0;
        BCP p;
        int tbcp_size = tabelaBCP.size();
        //Pega maior valor
        for(int i = 0; i < tbcp_size; i++)
        {
            p = tabelaBCP.get(i);
            if(p.creditos > maior_credito)
                maior_credito = p.creditos;
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
        int tbcp_size = tabelaBCP.size();
        for(int i = 0; i < tbcp_size; i++)
        {
            BCP temp;
            BCP a;
            BCP b;
            for(int j = 0; j < tbcp_size - i - 1; j++)
            {
                a = tabelaBCP.get(j);
                b = tabelaBCP.get(j+1);

                if(a.prioridade > b.prioridade)
                {
                    temp = a;
                    tabelaBCP.setElementAt(b, j);
                    tabelaBCP.setElementAt(temp, j+1);
                }
                else if(a.prioridade == b.prioridade)
                {
                    //Regra adversidades
                    if(tabelaBCP.get(j).estado == BCP.BLOQUEADO)
                    {
                        temp = a;
                        tabelaBCP.setElementAt(b, j);
                        tabelaBCP.setElementAt(temp, j+1);
                        continue;
                    }

                    //Se empatar 
                    nomes[0] = a.nomeProcesso;
                    nomes[1] = b.nomeProcesso;
                    Arrays.sort(nomes);

                    if(a.nomeProcesso.equals(nomes[0]))
                    {
                        temp = a;
                        tabelaBCP.setElementAt(b, j);
                        tabelaBCP.setElementAt(temp, j+1);
                    }
                }
            }
        }

        return tabelaBCP;
    }

    /////////////////////////////////////////////////////////////////////
    // Metodos de debug

    private void StackTrace(String s)
    {
        (new Exception(s)).printStackTrace();
        //Wait();
    } 

    private void Sleep()
    {
        if(_debug)
        {
            try
            {
                Thread.sleep(_sleep_time);
            }
            catch(Exception ex)
            {

            }
        }
    }

    private void Wait()
    {
        scanner.nextLine();
    }
}