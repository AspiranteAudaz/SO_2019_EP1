import java.util.List;

public class Escalonador
{
    static final char ES    = 'E';
    static final char RX    = 'X';
    static final char RY    = 'Y';
    static final char COM   = 'C';
    static final char SAIDA = 'S'; 

    //Referencia ao sistema
    Sistema sys;

    //Tabela de bcps
    BCP tabelaBCP[];

    //Cada valor aponta para uma linha na tabela BCP
    List<Integer> listaProntos;
    List<Integer> listaBloqueados;

    Escalonador(Sistema sys)
    {
        this.sys  = sys;

        //Carrega os programas na memoria, ja preparando os bcps
        tabelaBCP = sys.CarregaProgramas();

        //Adiciona todos os processos na lista de prontos
        for(int i = 0; i < tabelaBCP.length; i++)
        {
            listaProntos.add(i);
        }
    }
}