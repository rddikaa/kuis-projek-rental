/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.rental;

/**
 *
 * @author masdika
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemRental implements Serializable {
    private static final long serialVersionUID = 1L;
    private String namaRental;
    private Map<String, Penyewa> daftarPenyewa;
    private Map<String, Kendaraan> daftarKendaraan;
    private List<Transaksi> daftarTransaksi;

    public SistemRental(String namaRental) {
        this.namaRental = namaRental;
        this.daftarPenyewa = new HashMap<>();
        this.daftarKendaraan = new HashMap<>();
        this.daftarTransaksi = new ArrayList<>();
    }

    public void tambahPenyewa(String id, String nama) {
        daftarPenyewa.put(id, new Penyewa(id, nama));
    }

    public void tambahKendaraan(String kode, String nama, int kuota) {
        Kendaraan kendaraan;
        if (nama.toLowerCase().contains("premium")) {
            kendaraan = new KendaraanPremium(kode, nama, kuota);
        } else {
            kendaraan = new KendaraanReguler(kode, nama, kuota);
        }
        daftarKendaraan.put(kode, kendaraan);
    }

    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        File file = new File(namaFile);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + namaFile + " tidak ditemukan.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            int nomorBaris = 0;

            while ((baris = br.readLine()) != null) {
                nomorBaris++;
                baris = baris.trim();
                if (baris.isEmpty()) continue;

                try {
                    prosesBarisTransaksi(baris);
                } catch (TransaksiException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("[GAGAL BARIS " + nomorBaris + "] " + e.getMessage());
                }
            }
        }
    }

    private void prosesBarisTransaksi(String baris) throws TransaksiException {
        // Format digit 4: ID=ACT-01, KD=OBJ-A, JML=3,
        String[] bagian = baris.split(",");
        String idPenyewa = null;
        String kodeKendaraan = null;
        int jumlah = 0;

        for (String item : bagian) {
            item = item.trim();
            if (item.isEmpty()) continue;

            String[] kv = item.split("=");
            if (kv.length != 2) {
                throw new TransaksiException("Format pasangan key-value tidak valid: " + item);
            }

            String key = kv[0].trim();
            String val = kv[1].trim();

            switch (key) {
                case "ID":
                    idPenyewa = val;
                    break;
                case "KD":
                    kodeKendaraan = val;
                    break;
                case "JML":
                    jumlah = Integer.parseInt(val);
                    break;
                default:
                    throw new TransaksiException("Atribut tidak dikenal: " + key);
            }
        }

        if (idPenyewa == null || kodeKendaraan == null) {
            throw new TransaksiException("Data transaksi tidak lengkap pada baris: " + baris);
        }

        Penyewa penyewa = daftarPenyewa.get(idPenyewa);
        if (penyewa == null) {
            throw new TransaksiException("ID Penyewa tidak terdaftar: " + idPenyewa);
        }

        Kendaraan kendaraan = daftarKendaraan.get(kodeKendaraan);
        if (kendaraan == null) {
            throw new TransaksiException("Kode Kendaraan tidak ditemukan: " + kodeKendaraan);
        }

        kendaraan.kurangiKuota(jumlah);
        Transaksi trx = new Transaksi(penyewa, kendaraan, jumlah);
        daftarTransaksi.add(trx);

        System.out.println("[BERHASIL] " + penyewa.getNama() + " (" + idPenyewa + ") menyewa "
                + kendaraan.getNama() + " [" + kendaraan.getKategori() + "] sebanyak " + jumlah + " unit.");
    }

    public void simpanLaporanAkhir() {
        // 1. Object Serialization ke state.bak (Digit Ketiga: 9)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("\n[INFO] Serialisasi state sistem berhasil disimpan ke state.bak");
        } catch (IOException e) {
            System.out.println("[ERROR SERIALIZATION] Gagal menyimpan state.bak: " + e.getMessage());
        }

        // 2. Penulisan File I/O Teks Format Kolom ke laporan.txt (Digit Ketiga: 9)
        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println("====================================================================================");
            pw.println("LAPORAN AKHIR TRANSAKSI - " + namaRental.toUpperCase());
            pw.println("====================================================================================");
            pw.println(String.format("%-4s | %-8s | %-12s | %-8s | %-22s | %-16s | %-5s",
                    "NO", "ID AKTOR", "NAMA PENYEWA", "KODE", "NAMA KENDARAAN", "KATEGORI", "QTY"));
            pw.println("------------------------------------------------------------------------------------");

            int no = 1;
            for (Transaksi trx : daftarTransaksi) {
                pw.println(String.format("%-4d | %-8s | %-12s | %-8s | %-22s | %-16s | %-5d",
                        no++,
                        trx.getPenyewa().getId(),
                        trx.getPenyewa().getNama(),
                        trx.getKendaraan().getKode(),
                        trx.getKendaraan().getNama(),
                        trx.getKendaraan().getKategori(),
                        trx.getJumlah()));
            }

            pw.println("====================================================================================");
            pw.println("\nSISA KUOTA KENDARAAN:");
            pw.println(String.format("%-8s | %-22s | %-16s | %-10s", "KODE", "NAMA KENDARAAN", "KATEGORI", "SISA STOK"));
            pw.println("------------------------------------------------------------------");
            for (Kendaraan k : daftarKendaraan.values()) {
                pw.println(String.format("%-8s | %-22s | %-16s | %-10d",
                        k.getKode(), k.getNama(), k.getKategori(), k.getKuota()));
            }
            pw.println("==================================================================");

            System.out.println("[INFO] Laporan format kolom berhasil ditulis ke laporan.txt");
        } catch (IOException e) {
            System.out.println("[ERROR FILE I/O] Gagal menulis laporan.txt: " + e.getMessage());
        }
    }
}