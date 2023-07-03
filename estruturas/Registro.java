package estruturas;
import java.io.IOException;
import java.text.ParseException;

public interface Registro {
  public void setID(int n);
  public int getID();
  public byte[] toByteArray() throws IOException;
  public void fromByteArray(byte[] ba) throws IOException, ParseException; //
}