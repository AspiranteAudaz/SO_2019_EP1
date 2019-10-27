import java.util.Vector;

/**Classe representando o sistema vulgo CPU.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
*/

public class Sistema
{
    //Retornos de execucao
    static final char PREEMPCAO = 'P';
    static final char BLOQUEADO = 'B';
    static final char SAIDA     = 'S';
    static final char ERROFATAL = 'E';
    
    //Instrucoes
    static final String ASMX     = "X=";
    static final String ASMY     = "Y=";
    static final String ASMCOM   = "COM";
    static final String ASMES    = "E/S";
    static final String ASMSAIDA = "SAIDA";

    //Registrador geral X
    private int RX;
    //Registrador geral Y
    private int RY;
    //Contador de programa
    private int PC;
    //Memoria principal
    private String Memoria[];
    //Tamnho de cada programa se especificado
    private int tamanho_programa;

    //Numero de instrucoes;
    private int quantum;
    
    //Numero de processos
    private int n_processos = 0;

    //Log de saida
    private String log    = "";
    private int n_instruc = 0;
    private int n_trocas  = 0;

    //Objeto de entrada e saida
    private ES es;


    Sistema(String path_entrada, String path_saida, String path_quantum, String path_prioridades)
    {
        es      = new ES(path_entrada, path_saida, path_quantum, path_prioridades);
        Memoria = null;
        tamanho_programa = 0;
        CarregaQuantum(0);
    }

    Sistema(String path_entrada, String path_saida, String path_quantum, String path_prioridades, int tamanho, boolean tamanho_memoria, int forca_quantum)
    {
        es = new ES(path_entrada, path_saida, path_quantum, path_prioridades);
        
        if(tamanho_memoria)
        {
            Memoria = new String[tamanho];
            tamanho_programa = 0;
        }
        else
        {
            Memoria = null;
            tamanho_programa = tamanho;
        }

        CarregaQuantum(forca_quantum);
    }

    /////////////////////////////////////////////////////////////////////
    // Execucao de programas

    private void CarregaRegistradores(BCP processo)
    {
        PC = processo.PC;
        RX = processo.RX;
        RY = processo.RY;
    }

    private void GuardaRegistradores(BCP processo, char estado)
    {
        processo.estado = estado;
        processo.PC     = PC;
        processo.RX     = RX;
        processo.RY     = RY;
    }

    char Executa(BCP processo)
    {
        CarregaRegistradores(processo);
        processo.estado = BCP.EXECUTANDO;

        LogaExecutaProcesso(processo.nomeProcesso);

        //O processo necessariamente troca
        incrementaTroca();

        int num = 0;
        for(int i = 0; i < processo.quantum_atual * quantum; i++)
        {
            num++;
            //Incrementa toda vez que executa instrucao
            incrementaInstruc(1);
            
            String asm = Memoria[PC];

            //DEBUG
            //System.out.print("ASM: " + asm + " | ");

            switch (asm) 
            {
                case ASMSAIDA:
                    //System.out.print("EXEC: SAIDA");
                    LogaInterrompido(processo.nomeProcesso, num);
                    AsmSAIDA(processo);
                    return SAIDA;
                
                case ASMES:
                    //System.out.print("EXEC: ES");
                    AsmES(processo);
                    LogaInterrompido(processo.nomeProcesso, num);
                    return BLOQUEADO;

                case ASMCOM:
                    //System.out.print("EXEC: COM");
                    AsmCOM(processo);
                    break;

                default:
                    if(asm.charAt(0) == ASMX.charAt(0) && asm.charAt(1) == ASMX.charAt(1))
                    {
                        //System.out.print("EXEC: RX");
                        AsmRX(Integer.parseInt(asm.substring(2, asm.length())));
                    }
                    else if(asm.charAt(0) == ASMY.charAt(0) && ASMY.charAt(1) == ASMY.charAt(1))
                    {
                        //System.out.print("EXEC: RY");
                        AsmRY(Integer.parseInt(asm.substring(2, asm.length())));
                    }
                    else
                    {
                        //System.out.print("EXEC: ERROR");
                        //erro de syntax, assembly errado
                        return ERROFATAL;
                    }
                    break;
            }
        }

        GuardaRegistradores(processo, BCP.PRONTO);
        LogaInterrompido(processo.nomeProcesso, num);
        return PREEMPCAO;
    }

    private void AsmRX(int x)
    {
        RX = x;
        PC++;
    }

    private void AsmRY(int y)
    {
        RY = y;
        PC++;
    }
    
    private void AsmES(BCP processo)
    {
        PC++;
        LogaES(processo.nomeProcesso);
        GuardaRegistradores(processo, BCP.BLOQUEADO);
    }

    private void AsmCOM(BCP processo)
    {
        PC++;
    }

    private void AsmSAIDA(BCP processo)
    {
        LogaTerminou(processo.nomeProcesso, RX, RY);
    }

    /////////////////////////////////////////////////////////////////////
    // Leitura de programas

    Vector<BCP> CarregaProgramas() throws Exception
    {
        Vector<BCP> processos  = es.CarregaProgramas();
        int memoria_necessaria = 0;
        BCP p;

        if(tamanho_programa == 0)
        {
            //Caso nao se tenha definido o tamanho do programa,
            //somam-se todos os tamanhos dos programas
            for(int i = 0; i < processos.size(); i++)
            {
                p                   = processos.get(i);
                memoria_necessaria += p.memoria.length;
                LogaCarregaProcesso(p.nomeProcesso);    
            }    
        }
        else
        {
            for(int i = 0; i < processos.size(); i++)
            {
                p                   = processos.get(i);
                LogaCarregaProcesso(p.nomeProcesso);    
            }

            //Caso tenha especificado um tamanho maximo de programa
            memoria_necessaria = processos.size() * tamanho_programa;
        }
        

        if(Memoria == null)
            Memoria = new String[memoria_necessaria];
        else if(memoria_necessaria > Memoria.length)
            throw new Exception("Impossivel carregar os programas na memoria - Memoria principal: " + Memoria.length + "L - Processos: " + memoria_necessaria);

        //Posicao na memoria principal
        int ptr = 0;
        String memoria[];

        int program_len = 0;

        //Passa programas carregados para memoria
        for(int i = 0; i < processos.size(); i++)
        {
            p           = processos.get(i);  
            memoria     = p.memoria;
            p.PC        = ptr;
            p.PTR_TEXTO = PC;
            
            if(tamanho_programa == 0)
                program_len = memoria.length;
            else
                program_len = tamanho_programa;

            for(int k = 0; k < program_len; k++)
            {
                if(k < memoria.length)
                    Memoria[ptr] = memoria[k];
                else 
                    Memoria[ptr] = null;
                    
                ptr++;
            }

            p.memoria = null;
        }

        /*
        for(int i = 0; i < Memoria.length; i++)
        {
            System.out.println(Memoria[i]);
        }
        */

        return processos;
    } 

    void CarregaQuantum(int forca_quantum)
    {
        if(forca_quantum == 0)
            this.quantum = es.CarregaQuantum();
        else
            this.quantum = forca_quantum;
    }

    ////////////////////////////////////////////////////////////////////
    // Logger

    private String metricaA = "";
    private String metricaB = "";
    private String metricaC = "";

    void EscreveMetricaA(String s)
    {
        metricaA += s + "\n";
    }

    void EscreveMetricaB(String s)
    {
        metricaB += s + "\n";
    }

    void EscreveMetricaC(String s)
    {
        metricaC += s + "\n";
    }

    void GraveMetrica()
    {
        es.EscreveLogDisco(metricaA, "metricaA", true);
        es.EscreveLogDisco(metricaB, "metricaB", true);
        es.EscreveLogDisco(metricaC, "metricaC", true);
    }

    void GravaLog(){
        EscreveMetricaA((((float)this.n_trocas/(float)this.n_processos)+"").replace(".", ","));
        EscreveMetricaB((((float)this.n_instruc/(float)this.n_trocas)+"").replace(".", ","));
        EscreveMetricaC(quantum + "");
        GraveMetrica();
        LogDados();
        String str_quantum = "" + quantum;
        if(str_quantum.length() == 1)
            str_quantum = "0" + str_quantum;
        es.EscreveLogDisco(log, "log" + str_quantum);
    }
            
    //grava texto na variavel String log
    private void EscreveLog(String text)
    {
        this.log = this.log + text + "\n";
    }

    //grava log ao carregar processo
    private void LogaCarregaProcesso(String nome_proc)
    {
        incrementaProcessos();
        EscreveLog("Carregando " + nome_proc);   
    }
    
    //grava log ao executar processo
    private void LogaExecutaProcesso(String nome_proc){
        EscreveLog("Executando " + nome_proc);
    }

    //grava log ao interromper processo
    private void LogaInterrompido(String nome_proc, int num_instru)
    {
        EscreveLog("Interrompendo " + nome_proc + " após " + num_instru + " instruções");
    }

    //grava log ao fazer E/S
    private void LogaES(String nome_proc)
    {
        EscreveLog("E/S iniciada em " + nome_proc);
    }

    //grava log ao terminar processo
    private void LogaTerminou(String nome_proc, int rx, int ry)
    {
        EscreveLog(nome_proc + " terminado. X=" + rx + ". Y=" + ry);
    }
    
    //grava log de dados do numero de instruções e trocas
    //deve ser executado após todos processos terminarem
    private void LogDados()
    {
        EscreveLog("MEDIA DE TROCAS: " + (float)((float)this.n_trocas/(float)this.n_processos) +"\n"
                 + "MEDIA INSTRUCOES: " + (float)((float)this.n_instruc/(float)this.n_trocas) + "\n"
                 + "QUANTUM: " + this.quantum);
    }
    
    /////////////////////////////////////////////////////////////////////
    //Outros
    //deve ser chamado toda vez que carregar um processo
    private void incrementaProcessos(){
        this.n_processos++;
    }
    
    //incrementa numero de trocas
    //deve ser chamado toda vez que realizar uma troca
    private void incrementaTroca(){
        this.n_trocas++;
    }
    
    //incrementa numero de instruções
    //deve ser chamado toda vez que executar uma instrução
    private void incrementaInstruc(int i){
        this.n_instruc = this.n_instruc + i;
    }
    
}