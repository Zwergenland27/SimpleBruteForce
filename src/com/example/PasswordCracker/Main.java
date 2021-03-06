package com.example.PasswordCracker;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.StandardOpenOption;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Main {

    final static char[] supportedChar = new char[]{   //chars that the password can contain
            '!', '"', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', ']', '^', '_', '{', '|', '}', '~',
            'A', 'B', 'C' , 'D' ,  'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    static int maxSteps = 10000; //maximum rows in one array
    static String lookPassword = "asdwas";      //password to check
    static long time;
    public static void main(String[] args) {
        for(char c: supportedChar){
            File dir = new File("FILEPATH" + (int) c);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File[] files = dir.listFiles();
            for(File f: files){
                //f.delete();
            }
        }
        time = System.currentTimeMillis();
        crackPassword(1);
    }

    public static void crackPassword(int length){
        System.out.println("trying with length = " + length);
        String rcv = getCombinations2(length);
        if(rcv == null){
            crackPassword(length + 1);
        }else {
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(time);

            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(System.currentTimeMillis());
            long hours = ChronoUnit.HOURS.between(c1.toInstant(), c2.toInstant());
            long minutes = ChronoUnit.MINUTES.between(c1.toInstant(), c2.toInstant());
            long seconds = ChronoUnit.SECONDS.between(c1.toInstant(), c2.toInstant());
            long milliseconds = ChronoUnit.MILLIS.between(c1.toInstant(), c2.toInstant());
            System.out.println(hours + ":" + minutes + ":" + seconds + ":" + (milliseconds - seconds * 1000) + " " + rcv);
        }
    }

    public static String getCombinations(int length){
        int chars = supportedChar.length;
        int steps = (int) Math.pow(chars, length);

        int[] nowChar = new int[length];
        for(int i= 0; i < nowChar.length; i++){
            nowChar[i] = -1;
        }

        int[] jTotal = new int[length];
        for(int jt = 0; jt < jTotal.length; jt++){
            jTotal[jt] = 0;
        }

        for(int s = 0; s < (int) Math.ceil((float) steps / (float) maxSteps); s++) {

            int localMaxSteps = maxSteps;
            if(localMaxSteps >= steps - s * maxSteps){
                localMaxSteps = steps - s * maxSteps;
            }
            char[][] charTable = new char[localMaxSteps][length];   //table with all possible combinations (rows = number of combinations, columns = length of password)
            for (int i = length - 1; i >= 0; i--) {   //runs through columns backwards
                for (int j = 0; j < localMaxSteps; j++) {     //runs through rows
                    int fillSame = (int) Math.pow(chars, i); //how many chars have to be filled in with same char
                    if (jTotal[i] % fillSame == 0 || jTotal[i] == 0) {    //selects next character when all fillSame characters are filled with same character
                        nowChar[i]++;
                        if (nowChar[i] >= chars) {   //if all characters are already used, start with the first one
                            nowChar[i] = 0;
                        }
                    }
                    try{
                        charTable[j][i] = supportedChar[nowChar[i]];   //set character in Table#
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    jTotal[i]++;
                }
            }
            String[] combinations = new String[localMaxSteps];
            for (int i = 0; i < localMaxSteps; i++){
                combinations[i] = "";
                for(int j = length - 1; j >= 0; j--){
                    combinations[i] += charTable[i][j];
                }
            }
            for(String password: combinations){
                if(length > 1 && 1 == 2){
                    File writeFile = new File("C:/Users/koenigsf/IdeaProjects/SimpleBruteForce/passwords/" + (int) password.charAt(0) + "/" + length + ".txt");
                    try {
                        writeFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        FileWriter writer = new FileWriter(writeFile.getAbsolutePath(), true);
                        writer.write(password + "\r\n");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(password.equals(lookPassword)){
                    return password;
                }
            }
        }
        return null;
    }

    public static String getCombinations2(int length){
        for(char c: supportedChar){
            File file = new File("C:/Users/koenigsf/IdeaProjects/SimpleBruteForce/passwords/" + (int) c + "/" + length + ".txt");
            boolean lengthDone = lengthFullDone(length);
            try {
                FileWriter writer = null;
                if(!lengthDone){
                    file.createNewFile();
                    writer = new FileWriter(file, true);
                }
                if(length == 1){
                    if(!lengthDone){
                        writer.write(c + "\r\n");
                    }
                    if(lookPassword.equals(String.valueOf(c))){
                        return String.valueOf(c);
                    }
                }else{
                    for(char cInner: supportedChar){
                        System.out.println(c  + ", " + cInner);
                        BufferedReader reader = new BufferedReader(new FileReader("C:/Users/koenigsf/IdeaProjects/SimpleBruteForce/passwords/" + (int) cInner + "/" + (length - 1) + ".txt"));
                        String line;
                        while((line = reader.readLine()) != null){
                            String password = c + line;
                            if(!lengthDone){
                                writer.write(password + "\r\n");
                            }
                            if(password.equals(lookPassword)){
                                return password;
                            }
                        }
                        reader.close();
                    }
                }
                if(!lengthDone){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static boolean lengthFullDone(int length){
        File file = new File("C:/Users/koenigsf/IdeaProjects/SimpleBruteForce/passwords/" + (int) supportedChar[0] + "/" + (length + 1) + ".txt");
        if(file.exists()){
            return true;
        }
        return false;
    }
}

