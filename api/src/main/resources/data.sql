INSERT INTO designation (DESIGNATION_ID, NAME, LEVEL)
VALUES (1, 'Director', 1),
       (2, 'Manager', 2),
       (3, 'Lead', 3),
       (4, 'Developer', 4),
       (5, 'DevOps', 4),
       (6, 'QA', 4),
       (7, 'Intern', 5);

INSERT INTO employee (ID, NAME, DESIGNATION_ID, MANAGER_ID)
VALUES (1, 'Thor', 1, -1),
       (2, 'Iron Man', 2, 1),
       (3, 'Hulk', 3, 1),
       (4, 'CaptainAmerica', 2, 1),
       (5, 'War Machine', 6, 2),
       (6, 'Vison', 5, 2),
       (7, 'Falcon', 4, 4),
       (8, 'Ant Man', 3, 4),
       (9, 'SpiderMan', 7, 2),
       (10, 'BlackWidow', 4, 3);
