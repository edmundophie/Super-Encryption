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
        int leftBlock = 0, rightBlock = 0;
        for(int len=0;len<plaintext.length();len+=2) {
            if(len==0) { // First block
                leftBlock = (int) plaintext.charAt(len);
                rightBlock = (int) plaintext.charAt(len+1);
            }
            else { // xor with previous block
                leftBlock = leftBlock ^ (int) plaintext.charAt(len);
                if(len==plaintext.length()-1) // handle padding block
                    rightBlock = rightBlock ^ 0;
                else
                    rightBlock = rightBlock ^ (int) plaintext.charAt(len+1);
            }
            int seed = key.charAt(len);
            for(int i=0;i<4;++i) {
                for(int j=0;j<3;++j) {
                    // Feistel network
                    int temp = rightBlock;
//                    rightBlock = mapBySBox(leftBlock, seed);
                    leftBlock = temp;
                    
                    // 1x shift-left seed (wrapping bit)
                    int msb = (seed&128)>>7; // ek unsigned atau signed int
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
        for(int row=0;row<16;++row)
            for(int col=0;col<16;++col) {
                SBox[row][col] = rand.nextInt()%256;
            }
        
        return SBox[block>>4][block&15];
    }
    
    public static void main(String args[]) {
        String aa = "aa";
        byte a = new Byte("1");
        int c = a>>4 & 15;
        int intA = 'a';
        aa += 'a';
        System.out.println("inta : " + aa);
        System.out.println("c : " + c);
    }
}
