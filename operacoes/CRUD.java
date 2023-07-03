package operacoes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import entidades.usuario.Movies;

public class CRUD {

  public static int amountOfMovies, lastID, validObject = 1, invalidObject = 0; //quantidade de dados de filmes 
  public static Movies newMovie; //trocar esse nome
	private static Scanner input = new Scanner(System.in);
  static SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
  static RandomAccessFile arq, arqInd;
  static byte[] ba;
  static long lastFilePointer, lastIndiceFilePointer;

  public static void menu() throws Exception{

    int escolha;
    do {
      System.out.println("\n===================\n");
      System.out.println("INÍCIO > C R U D\n");
      System.out.println("1) Criar novos dados (create).");
      System.out.println("2) Ler dados existentes (read).");
      System.out.println("3) Atualizar dados (update).");
      System.out.println("4) Deletar dados (delete).");
      System.out.println("\n0) Retornar ao menu anterior");

      escolha = Utils.leOpcoes(0, 4);

      if(escolha == 1) create(); 
      else if(escolha == 2) read(); 
      else if(escolha == 3) update();
      else if(escolha == 4) {
        // Lê o valor digitado pelo usuário
        System.out.println("\n\t*** Deletar filme ***");
        System.out.println("Importante!!! Esta acao nao pode ser revertida.");
        System.out.print("\nDigite o ID do filme que deseja deletar: ");
        int ID = input.nextInt();
        delete(ID);
        System.out.print("\n\t*** Filme deletado com sucesso! ***");
      }
      else return;
    } while(escolha != 0);
    arq.close();
    arqInd.close();
  }//fim void menu()


  private static void amountOfData() throws IOException {
    /*
     * Esta funcao nos da a quantidade de linhas contidas no
     * arquivo "movies.cvs" e este valor e salvo na variavel global
     * amountOfMovies para ser usado em diversas funcoes desta classe
    */
    LineNumberReader lnr = new LineNumberReader(new FileReader("movies.csv"));
    lnr.skip(Long.MAX_VALUE);
    amountOfMovies = (lnr.getLineNumber() -1);
    lnr.close();
  }

  public static void newData() throws Exception {

    amountOfData();

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");

    arqInd.writeInt(amountOfMovies); //Sanvando o ultimo ID no inicio do arquivo. Estou levando em consideracao que o arquivo esta totalmente ordenado e o maior/ultimo ID e = a quantidade de filmes disponiveis.
    arq.writeInt(amountOfMovies); //Sanvando a quntidade de filmes no inicio do arquivo.

    BufferedReader br = new BufferedReader(new FileReader("movies.csv")); //lendo arquivo de dados
		String linha = br.readLine(); //ignorando a primeira linha
    int x, lapide = 1;
    String aux;

    for(int i = 0; i < amountOfMovies; i++){

      x = 1; // = 1 para pular o primeiro caracter que é sempre ( " )
      aux = "";

			newMovie = new Movies();
			linha = br.readLine();

			//============================== ID ==============================
			while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      newMovie.setID(Integer.parseInt(aux));
			aux = "";
			x++;

      //=========================== Movie Name ==========================
      while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      newMovie.setMovieName(aux);
			aux = "";
			x++;
      //========================== Release Date =========================
      while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      newMovie.setReleaseDate(formato.parse(aux));
			aux = "";
			x++;
      //============================ Category ===========================
      while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      aux = Vigenere.encryption(aux); // aplixando criptografia 
      newMovie.setCategory(aux);
			aux = "";
			x++;
      //============================ Run Time ===========================
      while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      newMovie.setRunTime(aux);
			aux = "";
			x++;
      //============================= Genre =============================
      while(linha.charAt(x) != '"' || linha.charAt(x+1) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      aux = removerCaracter("\"", aux);
      newMovie.setGenre(aux);
			aux = "";
			x+=2;
      //========================== IMDB Rating ==========================
      while(linha.charAt(x) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      newMovie.setImdbRating(Float.parseFloat(aux));
			aux = "";
			x++;
      //============================= Votes =============================
      while(linha.charAt(x) != '"' || linha.charAt(x+1) != ','){
				aux += linha.charAt(x);
				x++;
			}//fim while()

      aux = removerCaracter("\"", aux);
      newMovie.setVotes(Integer.parseInt(aux));
			aux = "";
			x+=2;
      //========================== Gross Total ==========================
      while(linha.charAt(x) != '"'){
				aux += linha.charAt(x);
				x++;
			}//fim while()
      aux = Cesar.encryption(aux); //criptografando dados
      newMovie.setGrossTotal(aux);
      //============================= FIM ===============================

      long fp = arq.getFilePointer(); //pegando o ponteiro (poisicao) //salvar isso num arquivo de indice
      arqInd.writeInt(newMovie.getID());
      arqInd.writeLong(fp);

      saveMovie(newMovie.toByteArray(), fp);
    }
    br.close();
    arqInd.close();
    arq.close();
  }

  public static String removerCaracter(String caracter, String str) throws Exception {
    str = str.replaceAll(caracter, "");
    return str;
  }

  // CRIAR NOVO DADO ----------------------------------------------------------------------------------------------------------
  public static boolean create ()throws Exception {

    /* 
     * Esta funcao e responsavel por criar novos dados os adicionando
     * no final do arquivo e seu ID e dado de acordo com o ultimo ID
     * salvo +1
     * Tambem e salvo o ID e sua posicao no arquivo de indice para
     * facilitar as operacoes do CRUD
    */

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");


    amountOfMovies = arq.readInt(); // primeiro valor do arquivo referente a quantidade de objetos existentes nele
    lastID = arqInd.readInt(); // primeiro valor do arquivoDeIndice referente ao ultimo ID salvo
    lastFilePointer = arq.length(); //iniciando a gravacao a partir do final do arquivo.
    lastIndiceFilePointer = arqInd.length(); // pegando o ponteiro da ultima posicao do arquivoDeIndice

    int votes;
    String movieName, category, runTime, genre, grossTotal;
    Date releaseDate;
    float imdbRating;

    Utils.pulaLinha(2);
    System.out.println("**** CREATE ****");

    boolean cadastroConfimado;

    movieName = Utils.leString("nome do filme","o");
    releaseDate = (Date) Utils.leData("data no formato (yyyy/mm/dd)", "a");
    category = Utils.leString("categoria", "a");
    runTime = Utils.leString("tempo de duracao (em minutos)", "o") + " min";
    genre = Utils.leString("genero(s)", "o(s)");
    imdbRating = Utils.leFloat("nota IMDB", "a");
    votes = Utils.leInt("numero de votos", "o");
    grossTotal = "$" + Utils.leString("valor da arrecadacao total (em Milhoes)", "o")+ "M";
    Utils.pulaLinha(5);

    System.out.println("**** DADOS DO FILME ***");
    System.out.println("-> ID: " + (lastID+1) + "\n-> Nome do filme: " + movieName + "\n-> Data de lancamento: " + releaseDate + "\n-> Categoria: " + category + "\n-> Tempo de filme: " + runTime + "\n-> Genero: " + genre + "\n-> Nota IMDB: " + imdbRating + "\n-> votos: " + votes + "\n-> Arrecadacao total: " + grossTotal);
    System.out.print("Voce gostaria de confirmar o cadastro? (S/N)");
    cadastroConfimado = Utils.lerConfirmacao();
    Utils.pulaLinha(3);

    if (cadastroConfimado) {
      newMovie = new Movies((lastID+1), movieName, releaseDate, Vigenere.encryption(category), runTime, genre, imdbRating, votes, Cesar.encryption(grossTotal));

      arqInd.seek(lastIndiceFilePointer); //colocando o ponteiro pro final do arquivo de indice.
      arq.seek(lastFilePointer);

      arqInd.writeInt(newMovie.getID());
      arqInd.writeLong(lastFilePointer);

      saveMovie(newMovie.toByteArray(), lastFilePointer);

      arqInd.seek(0);
      arqInd.writeInt(newMovie.getID()); //salvando o novo ultimo ID no incio do arquivo.
      arq.seek(0);
      arq.writeInt((amountOfMovies+1)); //salvando a quantidade de filmes no inicio do arquivo
      System.out.println("\t*** Cadastro confirmado! ***");
    } else {
      System.out.println("\t*** Cadastro cancelado ***");
    }

    arq.close();
    arqInd.close();

    return cadastroConfimado;
  }//fim create()

  // CONSULTAR DADOS ----------------------------------------------------------------------------------------------------------
  public static void read() throws Exception {
    /*
     * ler o id desejado no arquivo de indice
     * caso exista receber os dados do arqInd
     * com a posicao (long) no arquivo de indice
     * se o retorno for -1 e pq nao existe
     * caso exista ele ja restornara onde o 
     * ponteiro deve estar no arquivo de movies.db
     * ai e so usar o arq.seek().
     */
  
    System.out.print("Digite o ID do filme a ser buscado: ");
    int ID = input.nextInt(); //fazer tratamento de entrada

    long fp = findID(ID), invalidID = -1;
    arq = new RandomAccessFile("dados/movies.db", "rw");

    if(fp != invalidID ) { //verificando se o ID existe
      arq.seek(fp);
      if(checkTombstone(arq.readInt())) { //verificando o estado da lapide
        int tam = arq.readInt();
        ba = new byte[tam];
        arq.read(ba);

        newMovie = new Movies();
        newMovie.fromByteArray(ba);

        System.out.println(newMovie);
      } else {
        System.out.println("\n\n >>>>> Erro! O ID procurado nao existe ou foi deletado. <<<<<\n");
      }
    } else {
      System.out.println("\n\n >>>>> Erro! O ID procurado nao existe ou foi deletado. <<<<<\n");
    }
    arq.close();
  } //fim read()

  //ATUALIZAR DADOS ----------------------------------------------------------------------------------------------------------
  public static void update() throws Exception {

    /*
     * Quando for dar update tenho que checar se o novo dado
     * tem um tamanho identico ao que esta armazenado
     * se sim, salvo na mesma posicao, se nao
     * coloco uma lapide no antigo e salvo o novo dado
     * na ultima posicao do arquivo utilizando o mesmo ID
     */

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");
    amountOfMovies = arq.readInt();
    arq.close();

    int opcao, novoInt, id;
    float novoFloat;
    Date novaData;
    String novaString = "";
  
    System.out.println("\n\n\t*** ATUALIZAR DE DADOS ***");
    System.out.print("\nDigite o ID do filme que deseja atualizar: ");
    id = input.nextInt();

    Movies movie = readAndReturn(id);
    if(movie == null) { return; } // validando

    System.out.println("\n\t*** Dados do filme a ser atualizado ***");
    System.out.println(movie);
    System.out.println("\n\t***                                 ***\n");

    System.out.println("\nQuais dados voce deseja atualizar?");
    System.out.println("1 -  Nome do Filme");
    System.out.println("2 -  Data de lancamento");
    System.out.println("3 -  Categoria");
    System.out.println("4 -  Tempo de filme");
    System.out.println("5 -  Genero");
    System.out.println("6 -  Nota IMDB");
    System.out.println("7 -  Numero de Votos");
    System.out.println("8 -  Arrecadacao total");
    System.out.println("\n0 -  Cancelar");

    opcao = Utils.leOpcoes(0, 8);

    switch (opcao) {
      case 1:
      System.out.println("\n\t= Atualizar Nome do filme =\n");
      novaString = Utils.leString("nome do filme","o");
      movie.setMovieName(novaString);
      break;
      case 2:
      System.out.println("\n\t= Atualizar Data de lancamento =\n");
      novaData = Utils.leData("data no formato (yyyy/mm/dd)", "a");
      movie.setReleaseDate(novaData);
      break;
      case 3:
      System.out.println("\n\t= Atualizar Categoria =\n");
      novaString = Utils.leString("categoria", "a");
      novaString = Vigenere.encryption(novaString); // aplixando criptografia
      movie.setCategory(novaString);
      break;
      case 4:
      System.out.println("\n\t= Atualizar Tempo de filme =\n");
      novaString = Utils.leString("tempo de duracao (em minutos)", "o") + " min";
      movie.setRunTime(novaString);
      break;
      case 5:
      System.out.println("\n\t= Atualizar Genero =\n");
      novaString = Utils.leString("genero(s)", "o(s)");
      movie.setGenre(novaString);
      break;
      case 6:
      System.out.println("\n\t= Atualizar Nota IMDB =\n");
      novoFloat = Utils.leFloat("nota IMDB", "a");
      movie.setImdbRating(novoFloat);
      break;
      case 7:
      System.out.println("\n\t= Atualizar Numero de Votos =\n");
      novoInt = Utils.leInt("numero de votos", "o");
      movie.setVotes(novoInt);
      break;
      case 8:
      System.out.println("\n\t= Atualizar Arrecadacao total =\n");
      novaString = "$" + Utils.leString("valor da arrecadacao total (em Milhoes)", "o") + "M";
      novaString = Cesar.encryption(novaString); //criptografando dados
      movie.setGrossTotal(novaString);
      break;
      case 0:
      break;
    }
    ba = movie.toByteArray();
    
    int lapide, oldSize, newSize;
    long fp = findID(movie.getID()), lfp;
    arq = new RandomAccessFile("dados/movies.db", "rw");
    arq.seek(fp);
    lapide = arq.readInt();
    oldSize = arq.readInt();
    newSize = ba.length;

    if(oldSize == newSize) {
      arq.write(ba); //escrevendo de fato dos dados do objeto
      System.out.println("\noldSize == newSize");
    } else {
      arq.seek(0);
      arq.writeInt((amountOfMovies+1)); //como o dado foi salvo em um novo lugar, entende-se que teve almento no numero de filmes por mais que o outro tenha tido sua lapide alterada.

      delete(movie.getID());

      lfp = arq.length();
      saveMovie(ba, lfp);

      changeIDFilePointer(movie.getID(), lfp);
      System.out.println("\noldSize != newSize");
    }

    arq.close();
    arqInd.close();
    System.out.println("\n== Atualizacao feita com sucesso! ==\n");
  } //fim update()


  // DELETAR DADOS ----------------------------------------------------------------------------------------------------------
  public static void delete(int ID) throws Exception {

    /*
    * Esta funcao redece um ID e altera 
    * o valor da lapide no arquivo de bytes
    */

    long fp = findID(ID), invalidID = -1;
    arq = new RandomAccessFile("dados/movies.db", "rw");

    if(fp != invalidID ) { //verificando se o ID existe
      arq.seek(fp);
      if(checkTombstone(arq.readInt())) { //verificando o estado da lapide
        arq.seek(fp);
        arq.writeInt(invalidObject); //setando lapide
      } else {
        System.out.println("\n\n >>>>> Erro! O ID procurado ja foi deletado. <<<<<\n");
      }
    } else {
      System.out.println("\n\n >>>>> Erro! O ID procurado nao existe ou ja foi deletado. <<<<<\n");
    }
    arq.close();
  } //fim delete()

  private static void saveMovie(byte[] byteArray, long fp) throws IOException {

    /*  
     * Esta funcao e a responsavel por salvar o vetor de byte
     * no arquivo movies.db
    */

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arq.seek(fp);
    arq.writeInt(validObject); //salvando lapide no arquivo
    arq.writeInt(byteArray.length); //escrevendo primeiramente o tamanho do byte[]
    arq.write(byteArray); //escrevendo de fato dos dados do objeto
  }

  public static long findID(int ID) throws IOException {
    /*
    * Faz uma busca no arquivo de indice
    * se o indice existir retorna a posiçao de 
    * inicio no arquivo de byte 
    * se nao, retorna -1.
    */

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");
    amountOfMovies = arq.readInt(); //pegando a primeiro valor do arquivo que e referente a quantidade de filmes
    lastID = arqInd.readInt();

    long pointer = -1, fp;
    int id;

    for(int i = 0; i < amountOfMovies; i++) {
      id = arqInd.readInt();
      fp = arqInd.readLong();

      if (id == ID) {
        pointer = fp;
        i = amountOfMovies;
      }
    }
    arqInd.close();
    arq.close();

    System.out.println("\n| LastID: " + lastID + " | amountOfMovies: " + amountOfMovies + " | FilePointer: " + pointer + " |\n");
    return pointer;
  } //fim findID()

  public static boolean checkTombstone(int lapide) {
    return lapide == 1;
  }

  public static Movies readAndReturn(int ID) throws Exception {
    /*
     * ler o id desejado no arquivo de indice
     * caso exista receber os dados do arqInd
     * com a posicao (long) no arquivo de indice
     * se o retorno for -1 e pq nao existe
     * caso exista ele ja restornara onde o 
     * ponteiro deve estar no arquivo de movies.db
     * ai e so usar o arq.seek().
    */

    /*
     * Talvez eu possa achar um jeito de utilizar apenas uma funcao READ
    */

    long fp = findID(ID), invalidID = -1;
    arq = new RandomAccessFile("dados/movies.db", "rw");

    if(fp != invalidID ) { //verificando se o ID existe
      arq.seek(fp);
      if(checkTombstone(arq.readInt())) { //verificando o estado da lapide
        int tam = arq.readInt();
        ba = new byte[tam];
        arq.read(ba);

        newMovie = new Movies();
        newMovie.fromByteArray(ba);

        return newMovie;
      } else {
        System.out.println("\n\n >>>>> Erro! O ID procurado nao existe ou foi deletado. <<<<<\n");
      }
    } else {
      System.out.println("\n\n >>>>> Erro! O ID procurado nao existe ou foi deletado. <<<<<\n");
    }
    arq.close();
    return null;
  } //fim read()

  private static void changeIDFilePointer(int ID, long newPointer) throws IOException {

    /* 
     * Esta funcao faz a troca do valor do ponteiro antigo para um novo valor
     * E utilizada em casos que ao fazer o update() o novo objeto tenha um valor 
     * diferente do que o anterior e com isso sera gravado no final do arquivo com 
     * uma nova posicao.
    */

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");
    amountOfMovies = arq.readInt(); //pegando a primeiro valor do arquivo que e referente a quantidade de filmes
    lastID = arqInd.readInt();

    for(int i = 0; i < amountOfMovies; i++) {
      if (arqInd.readInt() == ID) {
        arqInd.writeLong(newPointer);
        i = amountOfMovies;
      } else {
        arqInd.readLong();
      }
    }
    arqInd.close();
  }

  public static void reorderFiles() throws IOException, ParseException, InterruptedException {

    RandomAccessFile tmp_arq;
    int newAmountOfMovies = 0, tam;

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd = new RandomAccessFile("dados/indices.db", "rw");

    amountOfMovies = arq.readInt();
    lastID = arqInd.readInt();

    tmp_arq = new RandomAccessFile("dados/tmp_movies.db", "rw");

    for(int i = 0; i < amountOfMovies; i++) {
      int lapide = arq.readInt();
      tam = arq.readInt();
      ba = new byte[tam];
      arq.read(ba);
      if(lapide == validObject) {
        tmp_arq.writeInt(lapide); // lapide
        tmp_arq.writeInt(tam); // tamanho do byte[]
        tmp_arq.write(ba); // byte[]
        newAmountOfMovies++; // contanto quantos objetos validos existem no arquivo.

      }
    }
    System.out.println("|newAmountOfMovies = " + newAmountOfMovies + "|");

    arq.close();
    arqInd.close();
    TelaInicial.deleteOldFiles();
    TimeUnit.SECONDS.sleep(1);
    tmp_arq.seek(0);

    arq = new RandomAccessFile("dados/movies.db", "rw");
    arqInd =  new RandomAccessFile("dados/indices.db", "rw");
    arq.writeInt(newAmountOfMovies); // escrevendo no novo arquivo a nova quantidade de objetos
    arqInd.writeInt(lastID); // escrevendo no novo arquivo o ultimo ID salvo.

    for(int i = 0; i < newAmountOfMovies; i++) {
      long fp = arq.getFilePointer();

      arq.writeInt(tmp_arq.readInt()); // lapide
      tam = tmp_arq.readInt();
      arq.writeInt(tam); // tamanho do byte[]
      ba = new byte[tam];
      tmp_arq.read(ba);
      arq.write(ba); // byte[]

      newMovie = new Movies();
      newMovie.fromByteArray(ba);

      arqInd.writeInt(newMovie.getID());
      arqInd.writeLong(fp);
    }
    arq.close();
    arqInd.close();

    tmp_arq.close();
    File tmp = new File("dados/tmp_movies.db");
    tmp.delete();

    TimeUnit.SECONDS.sleep(1);
  }

  public static ArrayList<Movies> readAll() throws IOException, ParseException {

    arq = new RandomAccessFile("dados/movies.db", "rw");
    amountOfMovies = arq.readInt();

    ArrayList<Movies> movieList = new ArrayList<>(); 

    for(int i = 0; i < amountOfMovies; i++) {
      if(checkTombstone(arq.readInt())) { //verificando o estado da lapide (se a lapide for valida o objeto sera salvo na memoria principal)
        int tam = arq.readInt();
        ba = new byte[tam];
        arq.read(ba);

        newMovie = new Movies();
        newMovie.fromByteArray(ba);
        movieList.add(newMovie);
      }
    }

    //System.out.println(movieList);
    arq.close();
    return movieList;
  }

  public class Cesar {

    public static String encryption (String str) throws Exception {
      String cifrado = "";
      int chaveDeCriptografia = str.length(); //a chave de criptografia varia de acordo com cada senha pois a chave usada será o valor do tamanho da string (Senha)

      for(int i = 0; i < str.length(); i++){
        char str_cifra = (char)(( str.charAt(i)) + chaveDeCriptografia); //pegando caracteres separados da string e eleva seu valor em (ASCII) com a soma do valor da chave
        cifrado += str_cifra; //salvando os caracteres criptografados em uma nova string
      }//fim for(i)

      return cifrado;
    }

    public static String decryption (String str) {
      String descifrado = "";
      int chaveDeCriptografia = str.length(); //a chave de criptografia varia de acordo com cada senha pois a chave usada será o valor do tamanho da string (Senha)

      for(int i = 0; i < str.length(); i++){
        char str_cifra = (char)(( str.charAt(i)) - chaveDeCriptografia); //pegando caracteres separados da string e diminui seu valor em (ASCII) com a soma do valor da chave
        descifrado += str_cifra; //salvando os caracteres descriptografados em uma nova string
      }//fim for(i)

      return descifrado;
    }
  }

  public class Vigenere {

    static String Abcedario = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String chave = "AED";

    //Utilizando a Codificação através da crifra de Vigenere sempre em caixa Alta
    public static String encryption (String Mensagem) {
        String sair = "";
        char[] chaveEquals = new char[Mensagem.length()];
        char[] Msn = Mensagem.toUpperCase().toCharArray();
        int cont = 0;
        for (int c = 0; c < Mensagem.length(); c++) {
            if (Mensagem.charAt(c) == ' ') {
                c++;
            }
            chaveEquals[c] = chave.charAt(cont);
            cont++;
            if (cont == chave.length()) {
                cont = 0;
            }
        }
        int x = 0, y = 0, z;
        for (int c = 0; c < Mensagem.length(); c++) {
            if (Mensagem.charAt(c) == ' ') {
                sair += " ";
                c++;
            }
            for (int f = 0; f < Abcedario.length(); f++) {
                if (Msn[c] == Abcedario.charAt(f)) {
                    x = f;
                }
                if (chaveEquals[c] == Abcedario.charAt(f)) {
                    y = f;
                }
            }
            z = (x + y) % 26;
            sair += Abcedario.charAt(z);
        }
        return sair;
    }
    
    //Utilizando a Decodificação através da crifra de Vigenere sempre em caixa Alta
    public static String decryption(String Mensagem) {
        String sair = "";
        char[] chaveEquals = new char[Mensagem.length()];
        char[] Msg = Mensagem.toUpperCase().toCharArray();
        int cont = 0;
        for (int c = 0; c < Mensagem.length(); c++) {
            if (Mensagem.charAt(c) == ' ') {
                c++;
            }
            chaveEquals[c] = chave.charAt(cont);
            cont++;
            if (cont == chave.length()) {
                cont = 0;
            }
        }
        cont = 0;
        int x = 0, y = 0, z, t;
        for (int c = 0; c < Mensagem.length(); c++) {
            if (Mensagem.charAt(c) == ' ') {
                sair += " ";
                c++;
            }
            for (int f = 0; f < Abcedario.length(); f++) {
                if (Msg[c] == Abcedario.charAt(f)) {
                    x = f;
                }
                if (chaveEquals[c] == Abcedario.charAt(f)) {
                    y = f;
                }
            }
            z = (y - x);

            if (z <= 0) {
                if (z == 0) {
                    sair += "A";
                } else {
                    for (int j = 1; j <= Abcedario.length(); j++) {
                        cont++;
                        if (cont == (z * -1)) {
                            sair += Abcedario.charAt(j);
                            break;
                        }
                    }
                }
            } else {
                for (int i = 25; i >= 0; i--) {
                    cont++;
                    if (cont == z) {
                        sair += Abcedario.charAt(i);
                        break;
                    }
                }
            }

            cont = 0;
        }
        return sair;
    }
  }

}//fim class CRUD()