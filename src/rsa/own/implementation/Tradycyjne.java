package rsa.own.implementation;

import java.math.BigInteger;
import java.util.Random;
import java.io.*;

/**
 * Created by piotr on 08.01.17.
 */

public class Tradycyjne
{
    private BigInteger n, d, e;
    private int bitlength = 1024;

    private FileOutputStream out;
    private File file;

    public Tradycyjne(int bits)
    {
        bitlength = bits;
        Random r = new Random();

        BigInteger p = BigInteger.probablePrime(bitlength, r);
        BigInteger q = BigInteger.probablePrime(bitlength, r);

        n = p.multiply(q); // tzw. modul
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // tzw. funkcja Eulera

        e = BigInteger.probablePrime(bitlength/2, r);

        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0 ) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    public String bytesToString(byte[] encrypted)
    {
        String outputString = "";
        for (byte b : encrypted) {
            outputString += Byte.toString(b);
        }
        return outputString;
    }

    //Encrypt message
    public byte[] encrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(e, n).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(d, n).toByteArray();
    }

    public void savekeys() throws IOException
    {
        file = new File("/home/piotr/IdeaProjects/RSA/public.key");
        out = new FileOutputStream(file);

        BigInteger tE = e;
        BigInteger tN = n;
        String temp = "e:"+tE+"n:"+tN;
        byte[] pubkkeycontent = temp.getBytes();
        out.write(pubkkeycontent);
        out.flush();
        out.close();

        file = new File("/home/piotr/IdeaProjects/RSA/private.key");
        out = new FileOutputStream(file);
        BigInteger tD = d;
        temp = "d:"+tD+"n:"+tN;
        pubkkeycontent = temp.getBytes();
        out.write(pubkkeycontent);
        out.flush();
        out.close();
    }

    public static void main (String[] args) throws IOException
    {
        DataInputStream in = new DataInputStream(System.in);

        Tradycyjne rsa = new Tradycyjne(1024);
        rsa.savekeys();

        String inputString ;
        System.out.println("Enter the plain text:");
        inputString = in.readLine();
        System.out.println("Encrypting String: " + inputString);
        System.out.println("String in Bytes: " + rsa.bytesToString(inputString.getBytes()));

        // encrypt
        byte[] encrypted = rsa.encrypt(inputString.getBytes());
        System.out.println("Encrypted String in Bytes: " + rsa.bytesToString(encrypted));

        // decrypt
        byte[] decrypted = rsa.decrypt(encrypted);
        System.out.println("Decrypted String in Bytes: " +  rsa.bytesToString(decrypted));

        System.out.println("Decrypted String: " + new String(decrypted));
    }
}