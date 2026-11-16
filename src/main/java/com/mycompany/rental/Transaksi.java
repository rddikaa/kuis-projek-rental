/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.rental;

/**
 *
 * @author masdika
 */
import java.io.Serializable;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private Penyewa penyewa;
    private Kendaraan kendaraan;
    private int jumlah;

    public Transaksi(Penyewa penyewa, Kendaraan kendaraan, int jumlah) {
        this.penyewa = penyewa;
        this.kendaraan = kendaraan;
        this.jumlah = jumlah;
    }

    public Penyewa getPenyewa() {
        return penyewa;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public int getJumlah() {
        return jumlah;
    }
}