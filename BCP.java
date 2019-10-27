/**Classe dataholder representando o BCP.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
*/

public class BCP
{
    static final char EXECUTANDO = 'E';
    static final char BLOQUEADO  = 'B';
    static final char PRONTO     = 'P';
    
    int RX        = 0;
    int RY        = 0;
    int PC        = 0;
    int PTR_TEXTO = 0;

    char    estado         = PRONTO;
    int     prioridade     = 0;
    int     creditos       = 0;
    int     creditos_atual = 0;
    int     tempo_espera   = 0; 
    int     quantum_atual  = 1;
    boolean fresco      = true;

    String nomeProcesso;
    String memoria[];
}