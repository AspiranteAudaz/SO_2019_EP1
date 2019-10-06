import java.io.File;
import java.io.FileReader;

/*
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileReader.html
 * https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html#read-char:A-
 * 
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
        BCP arrayBCP[] = null;

        // INCOMPLETO //

        return arrayBCP;
    }

    //Retorna o valor do quantum
    int CarregaQuantum()
    {
        char[] buffer = CarregaArquivo(path_entrada + path_quantum);

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

    public char[] CarregaArquivo(String path)
    {
        File       file   = null;
        FileReader reader = null;

        //Abre arquivo e cria leitor
        try 
        {
            file   = new File(path);
            reader = new FileReader(file);
        } 
        catch (Exception ex) 
        {
            //Tomamos GG, path errado ou arquivos não existem
            System.out.print("ERRO ES, erro de localizacao de arquivo " + path + " :\n" + ex.toString() + "\n");
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
            System.out.print("ERRO ES, falha ao ler arquivo " + path + " :\n" + ex.toString() + "\n");
        }

        return buffer;
    }
}