import java.util.Vector;
import java.util.Arrays;
import java.util.Scanner;

/**Classe agnóstica a CPU, e é responsavel pelo escalonamento de processos.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
*/
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

    private Vector<Integer> listaBloqueados = new Vector<Integer>();

    //Lista de listas de processos prontos, por creditos
    private Vector<Vector<Integer>> listasProntos;

    //private Vector<Integer> esperaRemocao = new Vector<Integer>();

    //Uma metrica
    private int numero_escalonamentos = 0;

    //Numero de processos não terminados
    private int num_processos_ativos = 0;

    //De qual fila se retiram processos, atualmente
    private int fila_atual = 0;

    //Processo para rodar por round robin
    private int pos_rrobin;

    private boolean rrobin = false;

    Escalonador(Sistema sys) throws Exception
    {
        if(_debug)
            scanner = new Scanner(System.in);

        this.sys = sys;

        tabelaBCP = sys.CarregaProgramas();
        //Carrega os programas na memoria, ja preparando os bcps
        //tabelaBCP = OrdenaListaProcessos(sys.CarregaProgramas());

        //Gera as listas de creditos (prontos)
        listasProntos = GeraListas(tabelaBCP);
        RedistribuiProcessosFilas(listasProntos);

        //DEBUG
        Vector<Integer> p;
        System.out.println("Lista de listas de processos, inicialmente: ");
        int size = listasProntos.size();
        int size_p;
        for(int i = 0; i < size; i++)
        {
            p = listasProntos.get(i);
            size_p = p.size();
            for(int j = 0; j < size_p; j++)
                System.out.println("lista: " + i + " prioridade: " + GetBCP(p.get(j)).prioridade);
        }
    }

    void Escalona()
    {
        BCP d;
        Integer processo_escalonado;
        char evento;

        while(num_processos_ativos > 0)
        {
            processo_escalonado = EscalonaProcesso();

            if(processo_escalonado == null)
            {
                DecrementaTempoBloqueados();
                continue;
            }

            d = GetBCP(processo_escalonado);

            evento = sys.Executa(GetBCP(processo_escalonado));

            //DEBUG
            Sleep();
            System.out.println("| N: " + d.nomeProcesso + " | C: " + d.creditos_atual + " | E: " + evento  + " | PC: " + d.PC + " | F: " + d.fresco + " | NE: " + numero_escalonamentos + " |");

            switch (evento) 
            {
                case Sistema.PREEMPCAO:
                
                    RemoveProcesso(processo_escalonado);
                    if(GetBCP(processo_escalonado).fresco)
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

            numero_escalonamentos++;
        }

        int size = tabelaBCP.size();
        for(int i = 0; i < size; i++)
            tabelaBCP.remove(0);
    }

    private BCP GetBCP(Integer index)
    {
        return tabelaBCP.get(index.intValue());
    }

    private Integer EscalonaRoundRobin()
    {
        Vector<Integer> fila = listasProntos.get(fila_atual);
        Integer p = fila.get(pos_rrobin % fila.size());
        pos_rrobin++;
        return p;
    }

    private Integer EscalonaProcesso()
    {
        if(fila_atual == 0)
        {
            if(listaBloqueados.size() == 0)
            {
                System.out.println("FA: " + fila_atual + " | NPFA: " + listasProntos.get(fila_atual).size() + " | NPFB: " + listaBloqueados.size() + " | REDISTRIBUI |");
                RedistribuiProcessosFilas(listasProntos);
                System.out.println("Processos redistribuidos!");
                pos_rrobin = 0;
                rrobin     = false;
                return EscalonaProcesso();
            }
            else
            {
                if(listasProntos.get(fila_atual).size() == 0)
                    return null;

                //DEBUG
                //System.out.println("FA: " + fila_atual + " | NPFA: " + listasProntos.get(fila_atual).size() + " | NPFB: " + listaBloqueados.size() + " | ROUND ROBIN |");
                rrobin = true;

                return EscalonaRoundRobin();
            }
        }
        
        if(listasProntos.get(fila_atual).size() == 0)
        {
            fila_atual--;
            System.out.println("FA: " + fila_atual + " | NPFA: " + listasProntos.get(fila_atual).size() + " | NPFB: " + listaBloqueados.size() + " | FILA VAZIA NORMAL |");
            return EscalonaProcesso();
        }

        OrdenaListaProcessos(listasProntos.get(fila_atual));
        //System.out.println("FA: " + fila_atual + " | NPFA: "+ listasProntos.get(fila_atual).size() + " | NPFB: " + listaBloqueados.size() + " | RETIRA NORMAL | ");

        //n testei se e o maior
        return listasProntos.get(fila_atual).remove(listasProntos.get(fila_atual).size() - 1);
    }

    private void DecrementaTempoBloqueados()
    {
        Integer p;
        BCP d;
        int size = listaBloqueados.size();
        int k = 0;

        for(int i = 0; i < size; i++)
        {
            p = listaBloqueados.get(k);
            d = GetBCP(p);
            GetBCP(p).tempo_espera -= 1;
            if(GetBCP(p).tempo_espera == 0)
            {
                //DEBUG
                System.out.println("| N: " + d.nomeProcesso + " | C: " + d.creditos_atual + " | PC: " + d.PC + " | F: " + d.fresco + " | Q: " + numero_escalonamentos + " | DESBLOQUEADO |");
                DesloqueiaProcesso(p);
                k--;
            }
            k++;
        }
    }
    
    private Integer BloqueiaProcesso(Integer p)
    {
        listaBloqueados.add(RemoveProcesso(AdicionaQuantum(RetiraCredito(p, 2), 1)));
        GetBCP(p).tempo_espera = TEMPO_ESPERA;
        
        return p;
    }
    
    private Integer DesloqueiaProcesso(Integer p)
    {
        AdicionaProcesso(p);
        listaBloqueados.remove(p);
        GetBCP(p).estado = BCP.PRONTO;

        return p;
    }

    private Integer RetiraCredito(Integer p, int num)
    {
        GetBCP(p).fresco          = false;
        GetBCP(p).creditos_atual -= num;

        if(GetBCP(p).creditos_atual < 0)
            GetBCP(p).creditos_atual = 0;

        return p;
    }

    private Integer AdicionaQuantum(Integer p, int num)
    {
        GetBCP(p).quantum_atual += num;

        return p;
    }
    
    /*
    private void RefazIndices()
    {
        int size = esperaRemocao.size();
        Vector<BCP> paraRemover = new Vector<BCP>();

        for(int i = 0; i < size; i++)
            paraRemover.add(GetBCP(esperaRemocao.remove(0)));
        
        for(int i = 0; i < size; i++)
            tabelaBCP.remove(paraRemover.remove(0));
    }
    */

    private void RedistribuiProcessosFilas(Vector<Vector<Integer>> listas_creditos)
    {
        if(fila_atual < 0)
            StackTrace("fila_atual com valor negativo");

        if(listasProntos != null)
        {
            fila_atual = 0;
            Vector<Integer> fila_zero = listasProntos.get(fila_atual);

            int fila_zero_size = fila_zero.size();
            for(int i = 0; i < fila_zero_size; i++)
            {
                fila_zero.remove(0);
            }
            // /System.out.println("| FA: "+ fila_atual + " | FLEN: " + listasProntos.get(fila_atual).size() + " |");
        }   
        
        //RefazIndices();
        
        fila_atual = listas_creditos.size() - 1;   

        BCP p;
        int tbcp_size = tabelaBCP.size();

        //Adiciona uma referencia para o processo na tabela BCP na sua respectiva fila
        for(int i = 0; i < tbcp_size; i++)
        {
            p = tabelaBCP.get(i);
            listas_creditos.get(p.creditos).add(i);
            p.creditos_atual = p.creditos;
            p.quantum_atual  = 1;
            p.fresco         = true;
        }
    }
    
    //MODIFICAR
    private Integer MataProcesso(Integer p)
    {
        //esperaRemocao.add(p);
        RemoveProcesso(p);
        num_processos_ativos--;

        if(num_processos_ativos < 0)
            StackTrace("num_processos_ativos negativo");

        return p;
    }

    private Integer RemoveProcesso(Integer p)
    {
        listasProntos.get(GetBCP(p).creditos_atual).remove(p);

        return p;
    }

    private Integer AdicionaProcesso(Integer p)
    {
        return AdicionaProcesso(p, GetBCP(p).creditos_atual);
    }

    private Integer AdicionaProcesso(Integer p, int fila)
    {
        GetBCP(p).creditos_atual = fila;
        listasProntos.get(fila).add(p);
        if(!rrobin)
            OrdenaListaProcessos(listasProntos.get(fila));

        return p;
    }

    //Gera as listas de processos prontos, por creditos
    private Vector<Vector<Integer>> GeraListas(Vector<BCP> tabelaBCP)
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

        Vector<Vector<Integer>> listas_creditos = new Vector<Vector<Integer>>();

        //Cria listas
        for(int i = 0; i < maior_credito + 1; i++)
            listas_creditos.add(new Vector<Integer>());

        //RedistribuiProcessosFilas(listas_creditos);

        return listas_creditos;
    }

    
    private Vector<BCP> OrdenaListaProcessos(Vector<Integer> tabela)
    {
        String nomes[] = new String[2];
        int tbcp_size = tabela.size();
        for(int i = 0; i < tbcp_size; i++)
        {
            Integer temp;
            Integer a;
            Integer b;
            for(int j = 0; j < tbcp_size - i - 1; j++)
            {
                a = tabela.get(j);
                b = tabela.get(j+1);

                if(GetBCP(a).prioridade > GetBCP(b).prioridade)
                {
                    temp = a;
                    tabela.setElementAt(b, j);
                    tabela.setElementAt(temp, j+1);
                }
                else if(GetBCP(a).prioridade == GetBCP(b).prioridade)
                {
                    //Regra adversidades
                    if(GetBCP(tabela.get(j)).estado == BCP.BLOQUEADO)
                    {
                        temp = a;
                        tabela.setElementAt(b, j);
                        tabela.setElementAt(temp, j+1);
                        continue;
                    }

                    //Se empatar 
                    nomes[0] = GetBCP(a).nomeProcesso;
                    nomes[1] = GetBCP(b).nomeProcesso;
                    Arrays.sort(nomes);

                    if(GetBCP(a).nomeProcesso.equals(nomes[0]))
                    {
                        temp = a;
                        tabela.setElementAt(b, j);
                        tabela.setElementAt(temp, j+1);
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