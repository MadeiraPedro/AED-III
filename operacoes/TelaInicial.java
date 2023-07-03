package operacoes;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Compressores.HuffmanCode;
import Compressores.LZWCompression;
import Compressores.LZWDecompression;
import entidades.usuario.Movies;
import CasamentoDePadroes.*;

public class TelaInicial{

  public static Movies cliente;
  public static int escolha;

  public static void dataLoading() throws Exception {

    System.out.println("\n===================\n");
    System.out.println("CARREGAMENTO DE DADOS\n");
    System.out.println("1) Quero continuar com os dados atuais.");
    System.out.println("2) Regarregar todos os dados");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,2);
    
    if(escolha == 1){
      File file = new File("dados/movies.db");
      if(!file.exists()) {
        deleteOldFiles();
        CRUD.newData(); //adicionar infomacao de caso nn exista o arquivo que sera feito uma nova carga de dados.
      }
    } else if(escolha == 2){
      deleteOldFiles();
      CRUD.newData(); //carregar uma nova base de dados atraves do arquivo movies.csv
    } else {
      Logout();
      System.exit(0);
    }
  }

  public static void deleteOldFiles() {
    File movies = new File("dados/movies.db");
    File indices = new File("dados/indices.db");
    movies.delete();
    indices.delete();
  }

  public static boolean telaInicial() throws Exception {
    boolean logado = true;

    System.out.println("\n===================\n");
    System.out.println("INÍCIO\n");
    System.out.println("1) CRUD");
    System.out.println("2) Reordenar arquivos");
    System.out.println("3) Compressores");
    System.out.println("4) Casamento de Padroes");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,4);
    

    switch (escolha) {
      case 1:
        CRUD.menu();
        break;
      case 2:
        reorderFiles();
        break;
      case 3:
        Compressores();
        break;
      case 4:
        casamentoDePadroes();
        break;
      default:
        Logout();
        logado = false;
        break;
    }
    return logado;
  }

  public static void Logout() {
    System.out.println("\nLogout feito com sucesso!");
  }

  private static void reorderFiles() throws IOException, ParseException, InterruptedException {
    CRUD.reorderFiles();
    System.out.println("\n\t*** Reordenacao realizada com sucesso! ***");
  }

  private static void Compressores() throws IOException, InterruptedException {

    System.out.println("\n\t*** COMPRESSORES ***");
    System.out.println("1) LZW");
    System.out.println("2) HUFFMAN");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,2);
    

    switch (escolha) {
      case 1:
        LZW();
        break;
      case 2:
        Huffman();
        break;
      default:
        break;
    }

  }

  private static void LZW() throws IOException, InterruptedException {

    System.out.println("\n\t*** LZW ***");
    System.out.println("1) Comprimir");
    System.out.println("2) Descomprimir");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,2);
    

    switch (escolha) {
      case 1:
        LZWCompression();
        break;
      case 2:
        LZWDecompression();
        break;
      default:
        break;
    }

  } // fim LZW()

  private static void LZWCompression() throws IOException, InterruptedException {
    LZWCompression.init("dados/movies.db");
    LZWCompression.init("dados/indices.db");
    TimeUnit.SECONDS.sleep(2);
  }

  private static void LZWDecompression() throws IOException, InterruptedException {

    File arq = new File("dados/movies.db"); 
    File arqInd = new File("dados/indices.db");
    arq.delete();
    arqInd.delete();

    TimeUnit.SECONDS.sleep(2);
    
    LZWDecompression.init("dados/movies.lzw");
    LZWDecompression.init("dados/indices.lzw");
  }

  private static void Huffman() throws IOException {

    System.out.println("\n\t*** Huffman ***");
    System.out.println("1) Comprimir");
    System.out.println("2) Descomprimir");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,2);
    

    switch (escolha) {
      case 1:
        HuffmanCode.init("dados/movies.db", escolha);
        break;
      case 2:
        HuffmanCode.init("dados/movies.db", escolha);
        break;
      default:
        break;
    }

  }

  private static void casamentoDePadroes() throws IOException, ParseException {

    System.out.println("\n\t*** Casamento de padroes ***");
    System.out.println("1) KMP");
    System.out.println("2) BoyerMoore");
    System.out.println("\n0) Sair");
    escolha = Utils.leOpcoes(0,2);

    switch (escolha) {
      case 1:
        KMPSearch();
        break;
      case 2:
        BoyerMooreSearch();
        break;
      default:
        break;
    }
  }

  private static void KMPSearch() throws IOException, ParseException {
    ArrayList<Movies> movieList = CRUD.readAll();
    KMP.init(movieList);
  }

  private static void BoyerMooreSearch() throws IOException, ParseException {
    ArrayList<Movies> movieList = CRUD.readAll();
    BoyerMoore.init(movieList);
  }
}