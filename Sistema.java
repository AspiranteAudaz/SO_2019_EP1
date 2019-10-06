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
    
    //Numero de processos
    int n_processos = 0;

    //Log de saida
    String log; 
    int n_instruc = 0;
    int n_trocas = 0;

    //Objeto de entrada e saida
    ES es;

    Sistema(String path_entrada, String path_saida, String path_quantum, String path_prioridades)
    {
        es = new ES(path_entrada, path_saida, path_quantum, path_prioridades);
    }

    /////////////////////////////////////////////////////////////////////
    // Execucao de programas
    char Executa(BCP processo)
    {
        
        return ASMES;
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
    
    //incrementa numero de trocas
    //deve ser chamado toda vez que realizar uma troca
    private void LogAddTroca(){
        this.n_trocas++;
    }
    
    //incrementa numero de instruções
    //deve ser chamado toda vez que executar uma instrução
    private void LogAddInstruc(){
        this.n_instruc++;
    }
    
    //carrega programa
    private boolean CarregaPrograma()
    {
        return false;
    }   
    
    //grava texto na variavel String log
    private void EscreveLog(String text)
    {
        this.log = this.log + text + "\n";
    }

    //grava log ao carregar processo
    private void LogaCarregaProcesso(String nome_proc)
    {
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
    private void LogDados(){
        EscreveLog("MEDIA DE TROCAS: " + (this.n_trocas/this.n_processos) +"\n"
                 + "MEDIA INSTRUCOES: " + (this.n_instruc/this.quantum) + "\n"
                 + "QUANTUM: " + this.quantum);
    }
    
    private void GravaLog(){
        
    }
    
    /////////////////////////////////////////////////////////////////////
    //Outros
    //deve ser chamado toda vez que carregar um processo
    void incrementaProcessos(){
        this.n_processos++;
    }
}