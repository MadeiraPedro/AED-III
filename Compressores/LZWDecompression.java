package Compressores;

import java.io.*;
import java.util.*;

public class LZWDecompression {

    public HashMap<Integer, String> dictionary = new HashMap<>();
    public String[] Array_char;
    public int dictSize = 256;
    public int currword;
    public int priorword;
    public byte[] buffer = new byte[3];
    public boolean onleft = true;

    public void LZW_Decompress(String input) throws IOException {
        Array_char = new String[4096];

        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Character.toString((char) i));
            Array_char[i] = Character.toString((char) i);
        }

        RandomAccessFile in = new RandomAccessFile(input, "r");
        RandomAccessFile out = new RandomAccessFile(input.replace(".lzw", ".db"), "rw");

        try {
            buffer[0] = in.readByte();
            buffer[1] = in.readByte();
            priorword = getvalue(buffer[0], buffer[1], onleft);
            onleft = !onleft;
            out.writeBytes(Array_char[priorword]);

            while (true) {
                if (onleft) {
                    buffer[0] = in.readByte();
                    buffer[1] = in.readByte();
                    currword = getvalue(buffer[0], buffer[1], onleft);
                } else {
                    buffer[2] = in.readByte();
                    currword = getvalue(buffer[1], buffer[2], onleft);
                }
                onleft = !onleft;

                if (currword >= dictSize) {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword]
                                + Array_char[priorword].charAt(0);
                    }
                    dictSize++;
                    out.writeBytes(Array_char[priorword]
                            + Array_char[priorword].charAt(0));
                } else {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword]
                                + Array_char[currword].charAt(0);
                    }
                    dictSize++;
                    out.writeBytes(Array_char[currword]);
                }
                priorword = currword;
            }
        } catch (EOFException e) {
            in.close();
            out.close();
        }
    }

    public int getvalue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);

        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }
    }

    public static void init(String fileName) throws IOException {
        try {
            System.out.println("\n\t*** Descompressao LZW ***");

            LZWDecompression lzw = new LZWDecompression();

            String str = fileName;

            File file = new File(str);

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNext()) {
                System.out.println("\nIniciando a descompressao de dados do arquivo (" + fileName + ") \n");
            }
            lzw.LZW_Decompress(str);
            System.out.println("\nDecompressao completa!");
            System.out.println("\nEsta descompressao gerou um novo arquivo de nome: "+ str.replace(".lzw", ".db"));

            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("\n\nErro! Arquivo nao encontrado!\n");
        }
    }
}