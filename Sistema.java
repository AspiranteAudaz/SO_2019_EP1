public class Sistema
{
    //Registrador geral X
    int RX;
    //Registrador geral Y
    int RY;
    //Contador de programa
    int PC;

    //Log de saida
    String log; 

    ES es;

    Sistema(String path_entrada, String path_saida)
    {
        es = new ES();
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