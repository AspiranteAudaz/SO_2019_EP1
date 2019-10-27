/**Classe para a execução do escalonador e geração de testes automaticos.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
*/

public class GeraDataSet
{
    public static void main(String[] args) 
    {    
        int numero_testes = 30;

        for(int i = 1; i <= numero_testes; i++)
        {
            try
            {
                Sistema     sys = new Sistema("./Entrada", "./Saida", "quantum.txt", "prioridades.txt", 42, false, i);
                Escalonador esc = new Escalonador(sys);

                esc.Escalona();
                sys.GravaLog();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}