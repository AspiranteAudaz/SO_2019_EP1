import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Arrays;

/*
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileReader.html
 * https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html#read-char:A-
 * https://docs.oracle.com/javase/8/docs/api/java/util/List.html
 */

public class ES
{
    String path_entrada; 
    String path_saida;
    String path_quantum;
    String path_prioridades;

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
        //BCPs
        LinkedList<BCP> listaBCP = new LinkedList<BCP>();

        //Arquivos do diretorio
        File   file  = null;
        File[] files = ListaArquivos(path_entrada);

        //Lista de prioridades
        LinkedList<Integer> prioridades = ParsaPrioridades();

        //Percorre os arquivos
        for(int i = 0; i < files.length; i++)
        {
            file = files[i];

            //Testa se de fato e arquivo de processo, por exclusao
            if(file.getName().equals(path_quantum) || file.getName().equals(path_prioridades))
                continue;
            
            //Adiciona o bcp com a prioridade ja setada
            listaBCP.add(ParsaPrograma(CarregaArquivo(file), prioridades.poll()));
        }

        //Aloca tabela BCP
        BCP arrayBCP[] = new BCP[listaBCP.size()];
        int size       = listaBCP.size();

        //Passa para formato array
        for(int i = 0; i < size; i++)
            arrayBCP[i] = listaBCP.pop();

        return arrayBCP;
    }

    private LinkedList<Integer> ParsaPrioridades()
    {
        //Parsa as linhas do arquivo carregado
        LinkedList<String>  linhas      = ParsaLinhas(CarregaArquivo(path_entrada + "/" + path_prioridades));

        //Lista de prioridades
        LinkedList<Integer> prioridades = new LinkedList<Integer>();


        int size = linhas.size();

        //Adiciona a nova lista parsando todos os inteiros
        for(int i = 0; i < size; i++)
            prioridades.add(Integer.parseInt(linhas.pop()));  

        return prioridades;
    }

    private BCP ParsaPrograma(char[] buffer, int prioridade)
    {
        LinkedList<String> listaMemoria = ParsaLinhas(buffer);
        BCP bcp          = new BCP();
        bcp.prioridade   = prioridade;
        bcp.nomeProcesso = listaMemoria.poll();

        //Aloca memoria para o programa
        String memoria[] = new String[listaMemoria.size()];
        int    size      = listaMemoria.size();

        //Passa para formato array
        for(int i = 0; i < size; i++)
            memoria[i] = listaMemoria.pop();

        bcp.memoria = memoria;

        return bcp;
    }

    private LinkedList<String> ParsaLinhas(char[] buffer)
    {
        LinkedList<String> linhas = new LinkedList<String>();
        
        //Uma linha
        String linha = "";

        for(int i = 0; i < buffer.length; i++)
        {
            //Testa se e nova linha, carriage return ou line feed
            if(buffer[i] == '\n' || buffer[i] == '\f' || buffer[i] == '\r')
            {
                if(linha.length() > 0)
                {
                    linhas.offer(linha);
                    linha = "";
                }

                continue;
            }
            
            //Concatena valor
            linha += buffer[i];
        }

        return linhas;
    }

    //Retorna o valor do quantum
    int CarregaQuantum()
    {
        char[] buffer = CarregaArquivo(path_entrada + "/" + path_quantum);

        if(buffer.length == 0)
        {
            //throw new Exception("Buffer nao foi carregado adequadamente.");
        }

        String num = "";

        //Gera numero em formato string
        for(int i = 0; i < buffer.length; i++)
        {
            //testa nova linha
            if(buffer[i] == '\n')
                continue;

            num += buffer[i];
        }

        //parsa para inteiro
        return Integer.parseInt(num);
    }

    private File[] ListaArquivos(String path)
    {
        File     diretorio = new File(path);
        File[]   processos = diretorio.listFiles();
        Arrays.sort(processos);

        return processos;
    }

    private char[] CarregaArquivo(String path)
    {
        File file = null;

        try
        {
            file = new File(path);
        }
        catch(Exception ex)
        {
            System.out.print("ERRO ES, erro de localizacao de arquivo " + path + " :\n" + ex.toString() + "\n");
        }
        
        return CarregaArquivo(file);
    }

    private char[] CarregaArquivo(File file)
    {
        FileReader reader = null;

        //Abre arquivo e cria leitor
        try 
        {
            reader = new FileReader(file);
        } 
        catch (Exception ex) 
        {
            //Tomamos GG, path errado ou arquivos não existem
            System.out.print("ERRO ES, erro de localizacao de arquivo :\n" + ex.toString() + "\n");
        }

        //Buffer de leitura
        char buffer[] = new char[(int)file.length()];
        
        try
        {
            reader.read(buffer);
        }
        catch(Exception ex)
        {
            //So se estiverem de zuera
            System.out.print("ERRO ES, falha ao ler arquivo :\n" + ex.toString() + "\n");
        }

        return buffer;
    }
}