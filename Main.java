public class Main
{
    public static void main(String[] args)
    {
        Sistema     sys = new Sistema("./Entrada", "./Saida", "quantum.txt", "prioridades.txt");
        Escalonador esc = new Escalonador(sys);

        esc.Escalona();
        sys.GravaLog();
    }
}