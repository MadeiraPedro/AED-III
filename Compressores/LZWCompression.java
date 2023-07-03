package Compressores;

import java.io.*;
import java.util.*;

public class LZWCompression {

    // Define o HashMap e as outras variaveis que serao usadas no programa
    public HashMap<String, Integer> dictionary = new HashMap<>();
    public int dictSize = 256;
    public String str = "";
    public byte inputByte;
    public byte[] buffer = new byte[3];
    public boolean onleft = true;

    // Variaveis para comparacao
    public static long oldSize, newSize;

    /**
     * Pega o nome do arquivo nao compactado, compactara seu conteudo
     * e criara um novo arquivo com o nome + a extensao (.lzw)
     */
    public void compress(String uncompressed) throws IOException {
        // Tamanho do dicionario
        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
        }

        RandomAccessFile read = new RandomAccessFile(uncompressed, "r");
        RandomAccessFile out = new RandomAccessFile(uncompressed.replace(".db", ".lzw"), "rw");

        oldSize = read.length(); // salvando o tamanho do arquivo principal (sem compressao)

        try {
            // Lê o primeiro caractere do arquivo de entrada na string
            inputByte = read.readByte();
            int i = new Byte(inputByte).intValue();
            if (i < 0) {
                i += 256;
            }
            char ch = (char) i;
            str = "" + ch;

            // Lendo Character por Character
            while (true) {
                inputByte = read.readByte();
                i = new Byte(inputByte).intValue();

                if (i < 0) {
                    i += 256;
                }
                ch = (char) i;

                if (dictionary.containsKey(str + ch)) {
                    str = str + ch;
                } else {
                    String s12 = to12bit(dictionary.get(str));
                    if (onleft) {
                        buffer[0] = (byte) Integer.parseInt(
                                s12.substring(0, 8), 2);
                        buffer[1] = (byte) Integer.parseInt(
                                s12.substring(8, 12) + "0000", 2);
                    } else {
                        buffer[1] += (byte) Integer.parseInt(
                                s12.substring(0, 4), 2);
                        buffer[2] = (byte) Integer.parseInt(
                                s12.substring(4, 12), 2);
                        for (int b = 0; b < buffer.length; b++) {
                            out.writeByte(buffer[b]);
                            buffer[b] = 0;
                        }
                    }
                    onleft = !onleft;

                    if (dictSize < 4096) {
                        dictionary.put(str + ch, dictSize++);
                    }

                    str = "" + ch;
                }
            }
            /**
             * catch para lidar com falha de arquivo de entrada/saída convertendo 8 bits em 12 bits, 
             * armazenando números inteiros em byte e gravando no arquivo de saída, caso contrário, 
             * adicione os buffers a [1] ou use buffer[2] e use o comprimento e um loop for para gerar 
             * os bytes e, em seguida, zerar fora do buffer o que garante que os bits sejam armazenados
             */
        } catch (IOException e) {
            String str12bit = to12bit(dictionary.get(str));
            if (onleft) {
                buffer[0] = (byte) Integer.parseInt(str12bit.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(str12bit.substring(8, 12)
                        + "0000", 2);
                out.writeByte(buffer[0]);
                out.writeByte(buffer[1]);
            } else {
                buffer[1] += (byte) Integer.parseInt(str12bit.substring(0, 4), 2);
                buffer[2] = (byte) Integer.parseInt(str12bit.substring(4, 12), 2);

                for (int b = 0; b < buffer.length; b++) {
                    out.writeByte(buffer[b]);
                    buffer[b] = 0;
                }
            }
            // read.close();
            // out.close();
        }

        newSize = out.length();

        read.close();
        out.close();
    }

    /**
     * Converte 8 bits para 12 bits
    */
    public String to12bit(int i) {
        String str = Integer.toBinaryString(i);
        while (str.length() < 12) {
            str = "0" + str;
        }
        return str;
    }

    /* 
     * Classe recebe o nome do arquivo (sem a sua extensao pois ja e adicionado automaticamente)
     * e faz todo o processo de compactacao gerando um novo arquivo 
     * com o mesmo nome e extensao (.lzw)
    */
    public static void init(String fileName) throws IOException {
        try {

            System.out.println("\n\t*** Compressao LZW ***");

            LZWCompression lzw = new LZWCompression();

            String str = fileName;

            File file = new File(str);

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNext()) {
                System.out.println("\nIniciando a compressao de dados do arquivo ("+ fileName +") \n");
            }
            lzw.compress(str);
            System.out.println("\nCompressao completa!");
            System.out.println("Esta compressao gerou um novo arquivo de nome: " + str.replace(".db", ".lzw"));
            System.out.println("|Tamanho do arquivo original: " + oldSize + "|Tamanho do arquivo comprimido: " + newSize + "|\n");

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("\n\nErro! Arquivo nao encontrado!\n");
        }
    }
}