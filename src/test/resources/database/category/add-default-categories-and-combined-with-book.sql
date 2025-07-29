INSERT INTO categories (id, name, description, is_deleted)
VALUES (2, 'test1', 'test', false), (3, 'test2', 'test', false);

INSERT INTO books_categories (book_id, category_id)
VALUES (2, 2);