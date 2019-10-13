public class BCP
{
    static final char EXECUTANDO = 'E';
    static final char BLOQUEADO  = 'B';
    static final char PRONTO     = 'P';

    int RX = 0;
    int RY = 0;
    int PC = 0;
    
    char estado         = PRONTO;
    int  prioridade     = 0;
    int  creditos       = 0;
    int  creditos_atual = 0;
    int  tempo_espera   = 0; 
    int  quantum_atual  = 1;
    
    String nomeProcesso;
    String memoria[];
}