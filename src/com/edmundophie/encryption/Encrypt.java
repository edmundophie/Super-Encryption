/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edmundophie.encryption;

import java.util.Random;

/**
 *
 * @author edmundophie
 */
public class Encrypt {
    private String key;
    private String plaintext;
    private String ciphertext;
    private final int blockLength = 8;

    public Encrypt(String plaintext,String key) {
        setPlaintext(plaintext);
        setKey(key);
        doEncrypt();
    }
    
    public void setKey(String key) {
        this.key = key;
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
    
    public void doEncrypt() {
        ciphertext = "";
        int leftBlock = 0, rightBlock = 0;
        for(int len=0;len<plaintext.length();len+=2) {
            if(len==0) { // First block
                leftBlock = (int) plaintext.charAt(len);
                rightBlock = (int) plaintext.charAt(len+1);
            }
            else { // xor with previous block (CBC)
                leftBlock = leftBlock ^ (int) plaintext.charAt(len);
                if(len==plaintext.length()-1) // handle padding block
                    rightBlock = rightBlock ^ 0;
                else
                    rightBlock = rightBlock ^ (int) plaintext.charAt(len+1);
            }
            int seed = key.charAt(len/2);
            int oldLeftBlock = 0, oldRightBlock = 0;
            for(int i=0;i<4;++i) {
                if(i>0) { // xor kan dengan blok sebelumnya
                    leftBlock = leftBlock ^ oldLeftBlock;
                    rightBlock = rightBlock ^ oldRightBlock;
                }
                oldLeftBlock = leftBlock;
                oldRightBlock = rightBlock;
                for(int j=0;j<3;++j) {
                    // Feistel network
                    int temp = rightBlock;
                    rightBlock = mapBySBox(leftBlock, seed);
                    leftBlock = temp;
                    // 1x shift-left seed (wrapping bit)
                    int msb = seed>>7; 
                    seed = (seed << 1) | msb;
                }
            }
            
            ciphertext += (char)leftBlock;
            ciphertext += (char)rightBlock;
        }
    }
    
    private int mapBySBox(int block, int seed) {
        Random rand = new Random(seed);
        Integer SBox[][] = new Integer[16][16];
        
        // Inisialisasi isi SBox dari 0-255
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
        
        return SBox[block>>4][block&15];
    }
    
    public static void main(String args[]) {
//        int d = mapBySBox(97, 5);
        String aa = "aa";
        byte a = new Byte("1");
        int c = a>>4 & 15;
        int intA = 'a';
        aa += 'a';
        Random rand = new Random();
        System.out.println("inta : " + rand.nextInt(1));
    }
}
