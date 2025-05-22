-- boards.sql
CREATE TABLE IF NOT EXISTS boards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE, -- e.g., "g"
    full_name TEXT NOT NULL,   -- e.g., "/g/ - Tecnología"
    description TEXT
);

CREATE TABLE IF NOT EXISTS threads (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    board_id INTEGER NOT NULL,
    subject TEXT,
    name TEXT DEFAULT 'Anónimo',
    email TEXT,
    comment TEXT NOT NULL,
    image_path TEXT, -- Relative path to stored image
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES boards(id)
);

CREATE TABLE IF NOT EXISTS posts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    thread_id INTEGER NOT NULL,
    name TEXT DEFAULT 'Anónimo',
    email TEXT,
    comment TEXT NOT NULL,
    image_path TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (thread_id) REFERENCES threads(id)
);

INSERT OR IGNORE INTO boards (name, full_name, description) VALUES
('b', '/b/ - Random', 'Todo tipo de contenido aleatorio y sin filtrar.'),
('g', '/g/ - Tecnología', 'Discusiones sobre hardware, software y tecnología en general.'),
('v', '/v/ - Videojuegos', 'Noticias, discusiones y memes de videojuegos.'),
('a', '/a/ - Anime & Manga', 'Discusiones sobre anime y manga.');