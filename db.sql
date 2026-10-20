-- Необязательный файл: БД создаётся автоматически при запуске.
-- Тестовые данные можно добавить после первого запуска приложения.
CREATE DATABASE IF NOT EXISTS autopark CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE autopark;

INSERT INTO cars (brand, release_year, registration_date, owner_name) VALUES
('Лада', 2018, CURDATE() - INTERVAL 2 DAY, 'Рогов Андрей Петрович'),
('Тойота', 2020, CURDATE() - INTERVAL 2 DAY, 'Иванов Сергей Олегович'),
('Киа', 2020, CURDATE() - INTERVAL 5 DAY, 'Смирнова Анна Игоревна'),
('Рено', 2015, CURDATE() - INTERVAL 9 DAY, 'Петров Дмитрий Андреевич'),
('Хендай', 2018, CURDATE() - INTERVAL 14 DAY, 'Рогов Андрей Петрович'),
('Форд', 2012, CURDATE() - INTERVAL 20 DAY, 'Козлова Мария Сергеевна'),
('Лада', 2021, CURDATE() - INTERVAL 27 DAY, 'Николаев Павел Викторович');
