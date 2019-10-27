import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import java.util.Arrays;

/*
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileReader.html
 * https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html#read-char:A-
 * https://docs.oracle.com/javase/8/docs/api/java/util/List.html
 * https://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html
 */

/**Classe auxiliar para a execução de E/S(não relacionada ao escolonador), como leitura de programas e escrita de resultados.
* @author Lucas Moura de Carvalho, Kevin Gabriel Gonçalves Oliveira, Willy Lee
* @version 1.00
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
    Vector<BCP> CarregaProgramas()
    {
        //BCPs
        Vector<BCP> listaBCP = new Vector<BCP>();

        //Arquivos do diretorio
        File   file  = null;
        File[] files = ListaArquivos(path_entrada);

        //Lista de prioridades
        Vector<Integer> prioridades = ParsaPrioridades();

        //Percorre os arquivos
        for(int i = 0; i < files.length; i++)
        {
            file = files[i];

            //Testa se de fato e arquivo de processo, por exclusao
            if(file.getName().equals(path_quantum) || file.getName().equals(path_prioridades))
                continue;
            
            //Adiciona o bcp com a prioridade ja setada
            listaBCP.add(ParsaPrograma(CarregaArquivo(file), (Integer)prioridades.remove(0)));
        }

        return listaBCP;
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
            //testa chars indesejaveis
            if(buffer[i] == '\n' || buffer[i] == '\f' || buffer[i] == '\r' || buffer[i] == ' ')
                continue;

            num += buffer[i];
        }

        //parsa para inteiro
        return Integer.parseInt(num);
    }

    void EscreveLogDisco(String log, String nome)
    {
        try
        {
            FileWriter writter = new FileWriter(path_saida + "/" + nome + ".txt");
            writter.write(log, 0, log.length());
            writter.close();
        }
        catch(Exception ex)
        {
            System.out.println("Erro critico na escrita do log file: " + ex.toString());
        }
    }

    void EscreveLogDisco(String log, String nome, boolean concatena)
    {
        try
        {
            FileWriter writter = new FileWriter(path_saida + "/" + nome + ".txt", concatena);
            writter.write(log, 0, log.length());
            writter.close();
        }
        catch(Exception ex)
        {
            System.out.println("Erro critico na escrita do log file: " + ex.toString());
        }
    }

    private Vector<Integer> ParsaPrioridades()
    {
        //Parsa as linhas do arquivo carregado
        Vector<String>  linhas      = ParsaLinhas(CarregaArquivo(path_entrada + "/" + path_prioridades));

        //Lista de prioridades
        Vector<Integer> prioridades = new Vector<Integer>();

        int size = linhas.size();

        //Adiciona a nova lista parsando todos os inteiros
        for(int i = 0; i < size; i++)
            prioridades.add(Integer.parseInt(linhas.remove(0)));  

        return prioridades;
    }

    private BCP ParsaPrograma(char[] buffer, int prioridade)
    {
        //Parsa linhas do programa
        Vector<String> listaMemoria = ParsaLinhas(buffer);

        BCP bcp            = new BCP();
        bcp.prioridade     = prioridade;
        bcp.nomeProcesso   = listaMemoria.remove(0);
        bcp.creditos       = prioridade;
        bcp.creditos_atual = prioridade;

        //Aloca memoria para o programa
        String memoria[] = new String[listaMemoria.size()];
        int    size      = listaMemoria.size();

        //Passa para formato array
        for(int i = 0; i < size; i++)
            memoria[i] = listaMemoria.remove(0);

        bcp.memoria = memoria;

        return bcp;
    }

    private Vector<String> ParsaLinhas(char[] buffer)
    {

        Vector<String> linhas = new Vector<String>();
        
        //Uma linha
        String linha = "";
        char last_char = 0;

        for(int i = 0; i < buffer.length; i++)
        {
            last_char = buffer[i];

            //Testa se e nova linha, carriage return ou line feed
            if(buffer[i] == '\n' || buffer[i] == '\f' || buffer[i] == '\r')
            {
                if(linha.length() > 0)
                {
                    linhas.addElement(linha.toUpperCase());
                    linha = "";
                }

                continue;
            }
            
            //Concatena valor
            linha += buffer[i];
        }

        if(last_char != '\n' || last_char != '\f' || last_char != '\r')
            linhas.addElement(linha.toUpperCase());

        return linhas;
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
            reader.close();
        }
        catch(Exception ex)
        {
            //So se estiverem de zuera
            System.out.print("ERRO ES, falha ao ler arquivo :\n" + ex.toString() + "\n");
        }

        return buffer;
    }
}