-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Waktu pembuatan: 10 Jan 2023 pada 10.47
-- Versi server: 10.5.16-MariaDB
-- Versi PHP: 7.3.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `id20107267_apex`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `products`
--

CREATE TABLE `products` (
  `productId` int(11) NOT NULL,
  `productName` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `productType` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `productImage` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `productPrice` double(50,2) NOT NULL,
  `productCondition` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `productManufacter` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `productWeight` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `productColor` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `productStock` int(11) NOT NULL,
  `productDescription` longtext COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data untuk tabel `products`
--

INSERT INTO `products` (`productId`, `productName`, `productType`, `productImage`, `productPrice`, `productCondition`, `productManufacter`, `productWeight`, `productColor`, `productStock`, `productDescription`) VALUES
(13, 'Arctis Nova 7', 'Headsets', 'arctis_nova_7p.png', 3600000.00, 'Used', 'MetalSeries', '750', 'Blue', 999, 'Sistem Akustik Nova menggabungkan perangkat keras dan perangkat lunak untuk Audio Mahakuasa dan pencelupan murni, dengan fitur nirkabel yang kuat untuk pengalaman luar biasa. Nova Acoustic System menampilkan Driver Fidelity Tinggi untuk kualitas audio superior dengan Audio Spasial 360° yang imersif dan EQ Parametrik pertama dalam game. Nirkabel Simultan (2.4GHz dan Bluetooth) memungkinkan pencampuran audio game dan seluler. Masa pakai baterai 38 jam dengan pengisian cepat USB-C, menghasilkan penggunaan selama 6 jam setelah 15 menit. Mikrofon peredam bising bertenaga AI menggunakan algoritme Sonar ClearCast AI untuk membungkam suara sekitar atau kebisingan rekan satu tim, dengan headset Sidetone Dial.'),
(14, 'Arctis Nova 3', 'Headsets', 'arctis_nova_3.png', 1260000.00, 'New', 'SteelSeries', '750', 'Orange', 99, 'Nova Acoustic System menghadirkan High Fidelity Drivers yang dirancang khusus untuk kualitas audio superior. Headset ringan dengan 4 titik penyesuaian dalam Sistem ComfortMAX untuk kesesuaian yang sempurna. Mikrofon peredam bising ClearCast Gen 2 menggunakan algoritme AI untuk mengurangi suara latar belakang untuk komunikasi yang jelas. Pencahayaan RGB zona ganda yang dinamis dan dapat disesuaikan dalam 16,8 juta warna yang cemerlang. Kompatibel dengan PC, Mac, PlayStation, Switch, dan perangkat seluler melalui USB-C (termasuk adaptor USB-A). Tentukan lokasi musuh Anda jauh sebelum Anda melihatnya dengan Sonar terobosan dalam suara game.'),
(17, 'Arctis Nova 1', 'Headsets', 'arctis_nova_1.png', 880000.00, 'New', 'SteelSeries', '750', 'Black', 99, 'Nova Acoustic System menghadirkan High Fidelity Drivers yang dirancang khusus untuk kualitas audio superior. Headset ultra ringan dengan 4 titik penyesuaian dalam Sistem ComfortMAX untuk kesesuaian yang sempurna. Mikrofon peredam bising ClearCast Gen 2 mengurangi suara latar belakang untuk komunikasi yang jelas. Cocok untuk platform PC dan konsol apa pun dengan jack 3,5 mm, bagus untuk perangkat seluler saat bepergian. Kontrol onboard yang nyaman dengan tombol volume dan tombol bisu suara pada headset. Tentukan lokasi musuh Anda jauh sebelum Anda melihatnya dengan Sonar terobosan dalam suara game.'),
(18, 'Apex Pro Mini', 'Keyboards', 'apex_pro_mini.png', 2890000.00, 'New', 'SteelSeries', '750', 'RGB', 99, 'Sakelar yang dapat disesuaikan OmniPoint 2.0 tercepat di dunia dengan respons 11x lebih cepat dan aktuasi 10x lebih cepat. Sesuaikan sensitivitas setiap tombol dari 0,2mm yang cepat hingga 3,8mm yang disengaja. Programkan dua tindakan berbeda ke tombol yang sama untuk pintasan game yang andal. Faktor bentuk 60% ringkas dengan fungsi cetak samping untuk fungsionalitas keyboard ukuran penuh.'),
(20, 'Aerox 5', 'Mice', 'aerox_5_black.png', 1400000.00, 'New', 'SteelSeries', '750', 'Orange', 99, 'Direkayasa 40% lebih ringan dari mouse multi-genre standar dengan desain 66g ultra-ringan. Tata letak ergonomis 9 tombol yang dapat diprogram dengan 5 tombol samping tindakan cepat, termasuk sakelar jentikan naik/turun kami yang unik. Teknologi AquaBarrier™ diberi peringkat IP54 untuk ketahanan air dan perlindungan dari debu, kotoran, dan lainnya. Serbaguna dan sangat ringan, mouse yang sempurna untuk semua Battle Royale, FPS, MOBA, dan game berkecepatan tinggi lainnya. Sensor optik presisi TrueMove Air kustom dengan pelacakan 1-ke-1 sejati, 18.000 CPI, 400 IPS, akselerasi 40G, dan pelacakan kemiringan. Sakelar Golden Micro IP54 generasi berikutnya dengan daya tahan lebih dari 80 juta klik.');

-- --------------------------------------------------------

--
-- Struktur dari tabel `transactions`
--

CREATE TABLE `transactions` (
  `transactionId` int(11) NOT NULL,
  `invoiceNumber` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `transactionStatus` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `transactionAmount` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `transactionAddress` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `invoiceDate` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `paymentDate` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `products` longtext COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data untuk tabel `transactions`
--

INSERT INTO `transactions` (`transactionId`, `invoiceNumber`, `transactionStatus`, `transactionAmount`, `transactionAddress`, `invoiceDate`, `paymentDate`, `userId`, `products`) VALUES
(3, 'INV/230107-0009-000', 'Pending', '22220100', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":1},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":2}]'),
(4, 'INV/230107-0009-000', 'Pending', '22220100', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":1},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":2}]'),
(5, 'INV/230107-0009-005', 'Pending', '22220100', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":1},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":2}]'),
(6, 'INV/230107-0009-006', 'Pending', '22220100', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":1},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":2}]'),
(7, 'INV/230107-0009-007', 'Pending', '24612000', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":2},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":3}]'),
(8, 'INV/230107-0009-008', 'Pending', '21583800', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"14\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":2},{\"productId\":\"20\",\"productQuantity\":3}]'),
(9, 'INV/230107-0009-009', 'Pending', '17610600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":2},{\"productId\":\"20\",\"productQuantity\":3}]'),
(10, 'INV/230107-0009-010', 'Pending', '9615900', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":1},{\"productId\":\"14\",\"productQuantity\":1},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":1}]'),
(11, 'INV/230108-0009-011', 'Pending', '1333500', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '08 Januari 23', '-', 9, '[{\"productId\":\"14\",\"productQuantity\":1}]'),
(12, 'INV/230108-0009-012', 'Pending', '7568400', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '08 Januari 2023', '-', 9, '[{\"productId\":\"13\",\"productQuantity\":2}]'),
(13, 'INV/002', 'Success', '20000000', 'Blora, Jawa Tengah', '24 Desember 2022', '11 Januari 2023', 1, '{\n    \"products\": [\n        {\n            \"productId\": 1,\n            \"productName\": \"SteelSeries\",\n            \"productQuantity\": 2\n        },\n        {\n            \"productId\": 2,\n            \"productName\": \"MetalSeries\",\n            \"productQuantity\": 3\n        }\n    ]\n}'),
(14, 'INV/230108-0002-001', 'Pending', '5115600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '08 Januari 2023', '-', 2, '[{\"productId\":\"13\",\"productQuantity\":1},{\"productId\":\"14\",\"productQuantity\":1}]'),
(15, 'INV/230107-0009-000', 'Success', '20000000', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '15 Februari 24', 1, 'this'),
(16, 'INV/230109-0002-002', 'Pending', '3792600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '09 Januari 2023', '-', 2, '[{\"productId\":\"23\",\"productQuantity\":1}]'),
(17, 'INV/230109-0004-001', 'Success', '8893500', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '09 Januari 2023', '10 Januari 2023', 4, '[{\"productId\":\"13\",\"productQuantity\":2},{\"productId\":\"14\",\"productQuantity\":1}]'),
(18, 'INV/230109-0004-002', 'Success', '1482600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '09 Januari 2023', '10 Januari 2023', 4, '[{\"productId\":\"20\",\"productQuantity\":1}]'),
(19, 'INV/230107-0009-420', 'Success', '690000', 'myAddress', '24 Januri 2021', '12 Februari 2020', 2, '[{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":2}]'),
(20, 'INV/230110-0004-003', 'Pending', '27543600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '-', 4, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":3},{\"productId\":\"18\",\"productQuantity\":2},{\"productId\":\"20\",\"productQuantity\":5}]'),
(21, 'INV/230107-0001-999', 'Success', '20000000', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '07 Januari 23', '15 Februari 24', 1, 'this'),
(22, 'INV/230110-0004-004', 'Success', '27543600', 'Jalan Gedong Songo V No. 5, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '10 Januari 2023', 4, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":3},{\"productId\":\"18\",\"productQuantity\":2},{\"productId\":\"20\",\"productQuantity\":5}]'),
(23, 'INV/230110-0004-005', 'Pending', '27543600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '-', 4, '[{\"productId\":\"13\",\"productQuantity\":3},{\"productId\":\"17\",\"productQuantity\":3},{\"productId\":\"18\",\"productQuantity\":2},{\"productId\":\"20\",\"productQuantity\":5}]'),
(24, 'INV/230110-0004-006', 'Pending', '10132500', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '-', 4, '[{\"productId\":\"17\",\"productQuantity\":3},{\"productId\":\"20\",\"productQuantity\":5}]'),
(25, 'INV/230110-0010-001', 'Pending', '11541600', 'Jalan Gedong Songo III No. No. 14, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '-', 10, '[{\"productId\":\"13\",\"productQuantity\":2},{\"productId\":\"14\",\"productQuantity\":3}]'),
(26, 'INV/230110-0011-001', 'Success', '8610000', 'null No. 292P+J28, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '10 Januari 2023', 11, '[{\"productId\":\"14\",\"productQuantity\":1},{\"productId\":\"17\",\"productQuantity\":3},{\"productId\":\"18\",\"productQuantity\":1},{\"productId\":\"20\",\"productQuantity\":1}]'),
(27, 'INV/230110-0011-002', 'Success', '2785650', 'null No. 292P+J28, Kecamatan Semarang Barat, Kota Semarang, Jawa Tengah, Indonesia, 50147', '10 Januari 2023', '10 Januari 2023', 11, '[{\"productId\":\"17\",\"productQuantity\":3}]'),
(28, 'INV/230110-0011-003', 'Success', '20876100', 'null No. 2C85+WF5, Kecamatan Semarang Tengah, Kota Semarang, Jawa Tengah, Indonesia, 50131', '10 Januari 2023', '12 Januari 2023', 11, '[{\"productId\":\"13\",\"productQuantity\":1}]');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` bigint(255) NOT NULL,
  `profilePicture` varchar(255) COLLATE utf8_unicode_ci DEFAULT '',
  `username` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `password` longtext COLLATE utf8_unicode_ci NOT NULL,
  `phoneNumber` varchar(50) COLLATE utf8_unicode_ci DEFAULT '',
  `gender` varchar(20) COLLATE utf8_unicode_ci DEFAULT '',
  `birthDate` varchar(60) COLLATE utf8_unicode_ci DEFAULT '',
  `category` varchar(20) COLLATE utf8_unicode_ci DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `profilePicture`, `username`, `email`, `password`, `phoneNumber`, `gender`, `birthDate`, `category`) VALUES
(2, '5a704af5d18d510311c9bfef65ada813.jpg', 'admin', 'admin@gmail.com', 'admin1234', '', '', '', 'admin'),
(4, '1673319466571.png', 'test', 'test@gmail.com', 'd41d8cd98f00b204e9800998ecf8427e', '8123456789', 'Male', '01 Januari 2000', 'user'),
(8, '1673320263546.png', 'My Self', 'myself@gmail.com', 'd41d8cd98f00b204e9800998ecf8427e', '', 'Male', '10 Januari 2023', 'user'),
(9, '844f0b62656456fbc7472928c44c076f.jpg', 'testing', 'testing@gmail.com', 'f4251ce516b8b2efe3ea49ac4f787489', '236985471', 'Male', '09 Januari 2023', 'user'),
(10, '1673323300666.png', 'what', 'what@gmail.com', '8b8752751972948a9734303f4e008134', '5886665889', 'Male', '10 Januari 2023', 'user'),
(11, '1673344446052.png', 'teat123', 'test123@gmail.com', 'cc03e747a6afbbcbf8be7668acfebee5', '5588888', 'Male', '01 Januari 2023', 'user');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`productId`);

--
-- Indeks untuk tabel `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transactionId`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `products`
--
ALTER TABLE `products`
  MODIFY `productId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT untuk tabel `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transactionId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
