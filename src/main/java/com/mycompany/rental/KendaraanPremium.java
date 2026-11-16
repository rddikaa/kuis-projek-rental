/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.rental;

/**
 *
 * @author masdika
 */
public class KendaraanPremium extends Kendaraan {
    private static final long serialVersionUID = 1L;

    public KendaraanPremium(String kode, String nama, int kuota) {
        super(kode, nama, kuota);
    }

    @Override
    public String getKategori() {
        return "Premium";
    }
}