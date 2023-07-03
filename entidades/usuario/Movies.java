package entidades.usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import estruturas.Registro;
import operacoes.CRUD.*;

public class Movies implements Registro {
  static SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

  protected int id;
  protected String movieName;
  protected Date releaseDate;
  protected String category;
  protected String runTime;
  protected String genre;
  protected float imdbRating;
  protected int votes;
  protected String grossTotal;
  
  // Constructors ------------------

  public Movies(int id, String movieName, Date releaseDate, String category, String runTime, String genre, Float imdbRating, int votes, String grossTotal) {
    this.id = id;
    this.movieName = movieName;
    this.releaseDate = releaseDate;
    this.category = category;
    this.runTime = runTime;
    this.genre = genre;
    this.imdbRating = imdbRating;
    this.votes = votes;
    this.grossTotal = grossTotal;
  }

  public Movies() {
    this.id = -1;
    this.movieName = "";
    this.releaseDate = null;
    this.category = "";
    this.runTime = "";
    this.genre = "";
    this.imdbRating = 0;
    this.votes = 0;
    this.grossTotal = "";
  }

  // SET methods ------------------------

  public void setID(int id) {
    this.id = id;
  }
  public void setMovieName(String movieName) {
    this.movieName = movieName;
  }
  public void setReleaseDate(Date releasDate) {
    this.releaseDate = releasDate;
  }
  public void setCategory(String category) {
    this.category = category;
  }
  public void setRunTime(String runTime) {
    this.runTime = runTime;
  }
  public void setGenre(String genre) {
    this.genre = genre;
  }
  public void setImdbRating(float imdbRating) {
    this.imdbRating = imdbRating;
  }
  public void setVotes(int votes) {
    this.votes = votes;
  }
  public void setGrossTotal(String grossTotal) {
    this.grossTotal = grossTotal;
  }

  // GET methods ------------------------

  public int getID() {
    return this.id;
  }
  public String getMovieName() {
    return this.movieName;
  }
  public Date getReleaseDate() {
    return this.releaseDate;
  }
  public String getCategory() {
    return this.category;
  }
  public String getRunTime() {
    return this.runTime;
  }
  public String getGenre() {
    return this.genre;
  }
  public float getImdbRating() {
    return this.imdbRating;
  }
  public int getVotes() {
    return this.votes;
  }
  public String getGrossTotal() {
    return this.grossTotal;
  }

 // ------------------------

 public String toString() {
  return "\nID: " + this.id + //id
         "\nNome: " + this.movieName + //movieName
         "\nData de lancamento: " + formato.format(this.releaseDate) + //releaseDate
         "\nCategoria: " + Vigenere.decryption(this.category) + //category
         "\nTempo de duracao: " + this.runTime + //runTime
         "\nGenero: " + this.genre + //genre
         "\nNota IMDB: " + this.imdbRating + //imdbRating
         "\nVotos: " + this.votes + //votes
         "\nArrecadacao: " + Cesar.decryption(this.grossTotal); //grossTotal
}

public String fullString() {
  return this.id + this.movieName + formato.format(this.releaseDate) +  Vigenere.decryption(this.category) + this.runTime + this.genre + this.imdbRating + this.votes + Cesar.decryption(this.grossTotal);
}

  public byte[] toByteArray() throws IOException{
    // escreve para memoria
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(id);
    dos.writeUTF(movieName);
    dos.writeUTF(formato.format(releaseDate)); //data em formato string
    dos.writeUTF(category);
    dos.writeUTF(runTime);
    dos.writeUTF(genre);
    dos.writeFloat(imdbRating);
    dos.writeInt(votes);
    dos.writeUTF(grossTotal);

    return baos.toByteArray(); // Representacao no vetor de bytes
  }
  
  public void fromByteArray(byte[] ba) throws IOException, ParseException {

    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);

    id = dis.readInt();
    movieName = dis.readUTF();
    releaseDate = formato.parse(dis.readUTF());
    category = dis.readUTF();
    runTime = dis.readUTF();
    genre = dis.readUTF();
    imdbRating = dis.readFloat();
    votes = dis.readInt();
    grossTotal = dis.readUTF();
  }
}
