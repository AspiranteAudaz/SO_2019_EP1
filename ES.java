public class ES
{
    String path_entrada; 
    String path_saida;
    String path_quantum;

    ES(String path_entrada, String path_saida, String path_quantum, String path_prioridades)
    {
        this.path_entrada     = path_entrada;
        this.path_saida       = path_saida;
        this.path_quantum     = path_quantum;
        this.path_prioridades = path_prioridades;
    }

    //Retorna a tabela de BCP com os programas carregados
    BCP[] CarregaProgramas()
    {
        BCP arrayBCP[] = null;

        // INCOMPLETO //

        return arrayBCP;
    }

    //Retorna o valor do quantum
    int CarregaQuantum()
    {
        int quantum;

        // INCOMPLETO //

        return quantum;
    }
}