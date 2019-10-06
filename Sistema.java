public class Sistema
{
    static final char ASMES    = 'E';
    static final char ASMRX    = 'X';
    static final char ASMRY    = 'Y';
    static final char ASMCOM   = 'C';
    static final char ASMSAIDA = 'S'; 

    //Registrador geral X
    int RX;
    //Registrador geral Y
    int RY;
    //Contador de programa
    int PC;

    //Numero de instrucoes;
    int quantum;

    //Log de saida
    String log; 

    //Objeto de entrada e saida
    ES es;

    Sistema(String path_entrada, String path_saida, String path_quantum, String path_prioridades)
    {
        es = new ES(path_entrada, path_saida, path_quantum, path_prioridades);
    }

    /////////////////////////////////////////////////////////////////////
    // Execucao de programas
    int Executa(BCP processo)
    {
        
        return 0;
    }

    private void AsmRX(int x)
    {
        RX = x;
    }

    private void AsmRY(int y)
    {
        RY = y;
    }
    
    private void AsmES(BCP processo)
    {

    }

    private void AsmCOM(BCP processo)
    {

    }

    private void AsmSAIDA(BCP processo)
    {

    }

    /////////////////////////////////////////////////////////////////////
    // Leitura de programas

    BCP[] CarregaProgramas()
    {
        return es.CarregaProgramas();
    } 

    void CarregaQuantum()
    {
        this.quantum = es.CarregaQuantum();

    }

    ////////////////////////////////////////////////////////////////////
    // Logger

    private boolean CarregaPrograma()
    {
        return false;
    }   

    void EscreveLog()
    {
        
    }

    private void LogaProcesso(String nome_proc)
    {
        
    }

    private void LogaInterrompido(String nome_proc, int num_instru)
    {

    }

    private void LogaES(String nome_proc)
    {

    }

    private void LogaTerminou(String nome_proc, int rx, int ry)
    {
        
    }
}