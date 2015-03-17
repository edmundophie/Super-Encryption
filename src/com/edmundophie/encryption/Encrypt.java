/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edmundophie.encryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author edmundophie
 */
public class Encrypt {
    private String key;
    private String plaintext;
    private String ciphertext;
    private List<int[][]> arrSBox;
    private int initialSeed;
    private String mode; 
    private char ivBlock[];
    
    public Encrypt(String plaintext,String key, String mode) {
        arrSBox = new ArrayList<>();
        setMode(mode);    
        setPlaintext(plaintext);
        setKey(key);
        doEncrypt();
    }
    
    public void setKey(String key) {
        this.key = key;
        
        // generate seed
        for(int i=0; i<8;++i)
            initialSeed += (int)key.charAt(i);
    }
    
    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }
    
    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getPlaintext() {
        return  plaintext;
    }
    
    public String getCiphertext() {
        return ciphertext;
    }
    
    public void generateSBox() {
        // Generate seed from key
        int seed = initialSeed;
        Random rand = new Random(seed);
        
        // Create 3 SBox'es
        for(int i=0;i<3;++i) {
            int msb = seed >> 31;
            seed = (seed<<1) | msb;
            
            // Inisialisasi isi SBox dari 0-255
            int SBox[][] = new int[16][16];
            int counter = 0;
            for(int row=0;row<16;++row)
                for(int col=0;col<16;++col) {
                    SBox[row][col] = counter;
                    ++counter;
                }

            // Create SBox using random seed (Knuth-Fisher-Yates algorithm)
            int maxRow = 15;
            for(int row=0;row<16;++row) {
                int maxCol = 15;
                for(int col=0;col<16;++col) {
                    int r = rand.nextInt(maxRow+1);
                    int c = rand.nextInt(maxCol+1);

                    // Swap
                    int temp = SBox[maxRow][maxCol]; 
                    SBox[maxRow][maxCol] = SBox[r][c];
                    SBox[r][c] = temp;
                    maxCol--;
                }
                maxRow--;
            }
            arrSBox.add(SBox);
        }
    }
    
    public void doEncrypt() {
        generateSBox();
        ciphertext = "";
        char leftBlock[] = new char[4];
        char rightBlock[] = new char[4];
        char processedBlock[] = new char[8];
        for(int block=0;block<plaintext.length();block+=8) {
            if(mode=="CBC") {
                if(block==0) { // xor kan dengan initialization vector
                    // Pecah blok menjadi left dan right
                    for(int i=0,j=4;i<4&&j<8;++i,++j) {
                        leftBlock[i] = (char) (ivBlock[i] ^ plaintext.charAt(block+i));
                        rightBlock[i] = (char) (ivBlock[j] ^ plaintext.charAt(block+j));
                    }
                }
                else { // xor kan dengan processedBlock sebelumnya
                    // Pecah blok menjadi left dan right
                    for(int i=0,j=4;i<4&&j<8;++i,++j) {
                        leftBlock[i] = (char) (processedBlock[i] ^ plaintext.charAt(block+i));
                        rightBlock[i] = (char) (processedBlock[j] ^ plaintext.charAt(block+j));
                    }
                }
            }
            else {
                // Pecah blok menjadi left dan right
                for(int i=0,j=4;i<4&&j<8;++i,++j) {
                    leftBlock[i] = plaintext.charAt(block+i);
                    rightBlock[i] = plaintext.charAt(block+j);
                }
            }
            
            int seed = initialSeed;
            for(int i=0;i<4;++i){
                for(int j=0;j<3;++j) {
                    char tempBlock[] = new char[4];
                    tempBlock = rightBlock;
                    rightBlock = mapBySBox(leftBlock, j); // Petakan leftBlock dg SBox[j]
                    leftBlock = tempBlock;
                    processedBlock = combineArrayOfChar(leftBlock, rightBlock);
                    int msb = seed >> 31;
                    seed = (seed<<1) | msb;
                    shuffleBlock(processedBlock, seed);    // Acak blok sesuai seed << 1
                }
            }
            System.out.println("cipher length : " + processedBlock.length);
            // tambahkan processedBlock ke ciphertext
            for(int i=0;i<processedBlock.length;++i)
                ciphertext += processedBlock[i];
        }
    }
    
    private char[] combineArrayOfChar(char block1[], char block2[]) {
        char resultBlock[] = new char[8];
        for(int i=0,j=4;i<4&&j<8;++i,++j) {
            resultBlock[i] = block1[i];
            resultBlock[j] = block2[i];
        }
        return resultBlock;
    }
    
    private void shuffleBlock(char block[],int seed) {
        Random rand = new Random(seed);
        for(int i=0;i<8;++i) {
            int randIdx = rand.nextInt(8);
            char temp = block[i];
            block[i] = block[randIdx];
            block[randIdx] = temp;
        }
    }
    
    private char[] mapBySBox(char block[], int sboxIdx) {
        int SBox[][] = arrSBox.get(sboxIdx);
        char resultBlock[] = new char[4];
        for(int i=0;i<block.length;++i) {
            int idxRow = block[i]>>4;
            int idxCol = block[i]&15;
            resultBlock[i] = (char) SBox[idxRow][idxCol];
        }
        
        return resultBlock;
    }
    
    public static void main(String args[]) {
    }

    private void setMode(String mode) {
        this.mode = mode;
        if(mode == "CBC" || mode=="CFB")
            initializeVector();
    }
    
    private void initializeVector() {
        ivBlock = new char[8];
        Random rand = new Random(initialSeed);
        for(int i=0;i<8;++i) {
            ivBlock[i] = (char) rand.nextInt(256);
        }
    }
}
