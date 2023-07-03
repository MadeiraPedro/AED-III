package operacoes;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils 
{
  private static Scanner input = new Scanner(System.in);  

  public static void pulaLinha(int linhas){
      for(int i = 0; i < linhas; i++)
        System.out.println();
  }

  /* --- Métodos de leitura --- */
  public static boolean lerConfirmacao() {
      char entrada;
      boolean confirmacao = false;
      boolean validacao = false;
      do {
        entrada = input.next().charAt(0);
        if (Character.toLowerCase(entrada) == 's' || Character.toLowerCase(entrada) == 'n') {
          validacao = true;
          if (entrada == 'S' || entrada == 's')
            confirmacao = true;
        } else
          System.out.print("entrada invalida, por favor confirme novamente: ");
      } while (validacao != true);
      return confirmacao;
    }
    
    public static int leOpcoes(int min, int max){
      int escolha;
      do{
        System.out.print("-> ");
        escolha = input.nextInt();
        if(escolha < min || escolha > max)
          System.out.println("Valor invalido! Escolha novamente um valor entre " + min + " e " + max +".");
      }while(escolha < min || escolha > max);
      input.nextLine();//consumimos o resto da linha
      return escolha;
    }

    public static String leString(String palavra, String artigo){
      String leitura;
      do{
          System.out.print("Insira "+ artigo + " " + palavra + ": ");
          leitura = input.nextLine();
          if(leitura.isEmpty())
              System.out.println("Entrada invalida, por favor insira novamente!");
      }while(leitura.isEmpty());
      return leitura;
    }

    public static float leFloat(String palavra, String artigo){
      float leitura;
        System.out.print("Insira "+ artigo + " " + palavra + ": ");
        leitura = input.nextFloat();

      return leitura;
    }

    public static int leInt(String palavra, String artigo){
      int leitura;
      System.out.print("Insira "+ artigo + " " + palavra + ": ");
      leitura = input.nextInt();

      return leitura;
    }

    public static Date leData(String palavra, String artigo) throws ParseException{
      String dataString;
      SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
      System.out.print("Insira "+ artigo + " " + palavra + ": ");
      dataString = input.nextLine();
      Date data = formato.parse(dataString);
      return data;
    }

  public static String removeAcento(String str) {
    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase();
  }
}
