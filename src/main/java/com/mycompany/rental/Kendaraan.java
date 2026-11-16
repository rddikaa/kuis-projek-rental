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

public abstract class Kendaraan implements Serializable {
    private static final long serialVersionUID = 1L;
    private String kode;
    private String nama;
    private int kuota;

    public Kendaraan(String kode, String nama, int kuota) {
        this.kode = kode;
        this.nama = nama;
        this.kuota = kuota;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getKuota() {
        return kuota;
    }

    public void kurangiKuota(int jumlah) throws TransaksiException {
        if (jumlah <= 0) {
            throw new TransaksiException("Jumlah sewa harus lebih dari 0.");
        }
        if (this.kuota < jumlah) {
            throw new TransaksiException("Kuota unit " + nama + " (" + kode + ") tidak mencukupi. Tersisa: " + kuota + ", Diminta: " + jumlah);
        }
        this.kuota -= jumlah;
    }

    public abstract String getKategori();
}
