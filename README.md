# DermaFace: An App for Your Facial Skin and Care 

![Bangkit Academy Logo](https://github.com/wahyuardiantito/DermaFace-An-App-For-Your-Facial-Skin-and-Care/assets/90948812/d041b0d2-430e-43b9-9ae6-9c374c2ecf26)

**DermaFace** adalah aplikasi mobile yang menggunakan teknik pembelajaran mesin untuk mendeteksi penyakit kulit wajah dan memberikan saran perawatan kulit. Aplikasi ini bertujuan untuk membantu pengguna, terutama remaja, dalam menilai kondisi kulit wajah mereka dan memberikan tips perawatan kulit yang relevan.  

## Table of Contents

- [Fitur Utama](#fitur-utama)
- [Langkah Pengembangan](#langkah-pengembangan)
- [Instalasi](#instalasi)
- [Penggunaan](#penggunaan)
- [Kontribusi](#kontribusi)
- [Kontak](#kontak)
- [Anggota Kelompok](#anggota-kelompok)

## Fitur Utama

- **Deteksi Penyakit Kulit Wajah**: Menggunakan model pembelajaran mesin untuk menganalisis dan mendeteksi masalah kulit wajah.
- **Tips Perawatan Kulit**: Menyediakan saran perawatan kulit berdasarkan hasil deteksi.
- **Penyimpanan Data**: Menyimpan data pengguna dan hasil deteksi di Firebase.
- **Antarmuka Pengguna**: Desain UI yang ramah dan mudah digunakan.

## Langkah Pengembangan

1. **Desain UI di Figma**
   - Mendesain antarmuka pengguna yang menarik dan mudah digunakan.
   - [Desain Figma](https://www.figma.com/design/41kR9ZXvR5QTEj7eNnguAW/DermaFace?node-id=0-1&t=m9Aa9tCVLZDglX04-1) <!-- Ganti link dengan yang sesuai -->

2. **Konversi Desain ke Layout XML di Android Studio**
   - Mengimplementasikan desain Figma ke dalam layout XML untuk Android.

3. **Integrasi Autentikasi Google Firebase**
   - Menyediakan opsi login menggunakan Google untuk kemudahan pengguna.
   - [Dokumentasi Firebase Authentication](https://firebase.google.com/docs/auth)

4. **Pengembangan Rest API untuk Artikel**
   - Membuat Rest API untuk artikel dengan menggunakan Firebase Realtime Database.
   - Mengintegrasikan API untuk menampilkan artikel kesehatan wajah secara dinamis dalam aplikasi.
   - [Dokumentasi Firebase Realtime Database](https://firebase.google.com/docs/database)

5. **Integrasi Model Pembelajaran Mesin dengan Firebase ML**
   - Menggunakan model Machine Learning untuk deteksi penyakit kulit wajah.
   - Model disimpan di Firebase ML dan diunduh oleh aplikasi untuk menganalisis gambar yang diunggah pengguna.
   - - Nama model: `Face_Detection`
   - [Dokumentasi Firebase ML](https://firebase.google.com/docs/ml)   
   - [Link Model](https://drive.google.com/file/d/1xJ7R9ANRQRVOhv2Jb7go1CHKI0MP4pjS/view?usp=sharing)

6. **Penyimpanan Hasil Analisis di Firestore Firebase**
   - Menyimpan hasil deteksi penyakit kulit dan tips perawatan di Firestore.
   - [Dokumentasi Firebase Firestore](https://firebase.google.com/docs/firestore)

7. **Pengelolaan Source Code dengan Git**
   - Menggunakan Git untuk kontrol versi dan mendeploy source code ke GitHub.
   - [GitHub Repository](https://github.com/wahyuardiantito/DermaFace-An-App-For-Your-Facial-Skin-and-Care)

## Instalasi

Ikuti langkah-langkah berikut untuk menginstal dan menjalankan aplikasi ini:

### Prasyarat

- Android Studio terbaru
- Akun Firebase dengan project yang sudah dikonfigurasi

### Langkah-langkah

1. **Clone repositori**
    ```bash
    git clone https://github.com/wahyuardiantito/DermaFace-An-App-For-Your-Facial-Skin-and-Care.git
    ```

2. **Buka proyek di Android Studio**
    - Buka Android Studio dan pilih "Open an existing Android Studio project".
    - Arahkan ke direktori tempat kamu meng-clone repositori ini.

3. **Konfigurasi Firebase**
    - Tambahkan file `google-services.json` ke direktori `app`.
    - Upload model `modelquantized.tflite` ke firebase ML dengan nama `Face_Detection`
    - Konfigurasi Firebase dalam aplikasi dengan mengikuti petunjuk di [Firebase Documentation](https://firebase.google.com/docs).

4. **Jalankan aplikasi**
    - Jalankan aplikasi di emulator atau perangkat fisik.

## Penggunaan

1. Buka aplikasi DermaFace di perangkat Anda.
2. Login menggunakan autentikasi Google.
3. Unggah atau ambil gambar wajah Anda.
4. Dapatkan hasil deteksi dan tips perawatan kulit.

## Kontribusi

Kami menyambut kontribusi dari siapa pun. Untuk berkontribusi, silakan lakukan langkah-langkah berikut:

1. Fork repositori ini.
2. Buat branch fitur:
    ```bash
    git checkout -b feature/AmazingFeature
    ```
3. Commit perubahan Anda:
    ```bash
    git commit -m 'Add some AmazingFeature'
    ```
4. Push ke branch:
    ```bash
    git push origin feature/AmazingFeature
    ```
5. Buat Pull Request.

## Kontak

Untuk pertanyaan lebih lanjut, hubungi [mukhtarijal6902@gmail.com](mukhtarijal6902@gmail.com).

## Anggota Kelompok

### Machine Learning (ML)
- **M281D4KY1503** – Wahyu Ardiantito. S – Universitas Negeri Medan
- **M734D4KX1910** – Juliani Jakin – Institut Sains dan Teknologi Nasional
- **M281D4KX3359** – Rabiahtul Adawiah Hasyani – Universitas Negeri Medan

### Mobile Development (MD)
- **A282D4KX3580** – Hayatun Nupus – Universitas Negeri Padang
- **A282D4KY3619** – Mukhtarijal – Universitas Negeri Padang

### Cloud Computing (CC)
- **C382D4KY0695** – Riski Dwi Prakoso – Universitas Merdeka Malang
- **C732D4KX1260** – Nilam Darma Taksiah – Universitas Bung Hatta
