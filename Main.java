import java.util.Scanner;
import operacoes.*;//import todas as operações

class Main {

  public static Scanner input;

  public static void main(String[] args) throws Exception {

    boolean usuarioLogado = true;
    TelaInicial.dataLoading();
    try{
      while(usuarioLogado){
        usuarioLogado = TelaInicial.telaInicial();
      }
      System.out.println("\nObrigado por usar o nosso CRUD\n\n");
    } catch(Exception ex){
      ex.printStackTrace();
    }

  }//fim void main()
  
}//fim Class Main()