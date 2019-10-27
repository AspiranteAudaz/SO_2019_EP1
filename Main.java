/**Classe para a execução do escalonador.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
*/

public class Main
{
    public static void main(String[] args) 
    {    
        try
        {
            Sistema     sys = new Sistema("./Entrada", "./Saida", "quantum.txt", "prioridades.txt", 42, false, 0);
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