-- V4__Insert_Upvotes.sql
ALTER SEQUENCE public.thread_upvotes_thread_upvote_id_seq RESTART WITH 1;
-- Insert mock thread upvotes
-- These represent users upvoting various threads
INSERT INTO thread_upvotes (user_google_id, thread_id) VALUES
-- JavaScript Promise chaining best practices (thread_id 1)
('g103', 1),  -- User g103 upvotes thread 1
('g105', 1),  -- User g105 upvotes thread 1
('g107', 1),  -- User g107 upvotes thread 1
('g111', 1),  -- User g111 upvotes thread 1
('g115', 1),  -- User g115 upvotes thread 1
('g119', 1),  -- User g119 upvotes thread 1
('g123', 1),  -- User g123 upvotes thread 1
('g127', 1),  -- User g127 upvotes thread 1

-- Python decorator confusion with arguments (thread_id 2)
('g101', 2),  -- User g101 upvotes thread 2
('g102', 2),  -- User g102 upvotes thread 2
('g107', 2),  -- User g107 upvotes thread 2
('g109', 2),  -- User g109 upvotes thread 2
('g113', 2),  -- User g113 upvotes thread 2
('g121', 2),  -- User g121 upvotes thread 2

-- React useEffect dependency array explained (thread_id 3)
('g101', 3),  -- User g101 upvotes thread 3
('g104', 3),  -- User g104 upvotes thread 3
('g107', 3),  -- User g107 upvotes thread 3
('g113', 3),  -- User g113 upvotes thread 3
('g123', 3),  -- User g123 upvotes thread 3
('g129', 3),  -- User g129 upvotes thread 3

-- SQL query optimization for large datasets (thread_id 4)
('g101', 4),  -- User g101 upvotes thread 4
('g102', 4),  -- User g102 upvotes thread 4
('g103', 4),  -- User g103 upvotes thread 4
('g105', 4),  -- User g105 upvotes thread 4
('g111', 4),  -- User g111 upvotes thread 4
('g119', 4),  -- User g119 upvotes thread 4
('g123', 4),  -- User g123 upvotes thread 4
('g127', 4),  -- User g127 upvotes thread 4

-- Docker container networking issues (thread_id 5)
('g101', 5),  -- User g101 upvotes thread 5
('g103', 5),  -- User g103 upvotes thread 5
('g105', 5),  -- User g105 upvotes thread 5
('g107', 5),  -- User g107 upvotes thread 5
('g111', 5),  -- User g111 upvotes thread 5
('g121', 5),  -- User g121 upvotes thread 5

-- Understanding Big O notation practically (thread_id 6)
('g103', 6),  -- User g103 upvotes thread 6
('g105', 6),  -- User g105 upvotes thread 6
('g107', 6),  -- User g107 upvotes thread 6
('g109', 6),  -- User g109 upvotes thread 6
('g113', 6),  -- User g113 upvotes thread 6
('g127', 6),  -- User g127 upvotes thread 6

-- Go concurrency patterns explained (thread_id 7)
('g101', 7),  -- User g101 upvotes thread 7
('g105', 7),  -- User g105 upvotes thread 7
('g109', 7),  -- User g109 upvotes thread 7
('g113', 7),  -- User g113 upvotes thread 7
('g123', 7),  -- User g123 upvotes thread 7

-- CSS Grid vs Flexbox decision making (thread_id 8)
('g103', 8),  -- User g103 upvotes thread 8
('g107', 8),  -- User g107 upvotes thread 8
('g111', 8),  -- User g111 upvotes thread 8
('g119', 8),  -- User g119 upvotes thread 8
('g125', 8),  -- User g125 upvotes thread 8
('g129', 8),  -- User g129 upvotes thread 8

-- Git workflow for small teams (thread_id 9)
('g101', 9),  -- User g101 upvotes thread 9
('g107', 9),  -- User g107 upvotes thread 9
('g109', 9),  -- User g109 upvotes thread 9
('g115', 9),  -- User g115 upvotes thread 9
('g119', 9),  -- User g119 upvotes thread 9
('g123', 9),  -- User g123 upvotes thread 9
('g129', 9),  -- User g129 upvotes thread 9

-- Memory management in C++ modern applications (thread_id 10)
('g103', 10), -- User g103 upvotes thread 10
('g105', 10), -- User g105 upvotes thread 10
('g111', 10), -- User g111 upvotes thread 10
('g113', 10), -- User g113 upvotes thread 10
('g117', 10), -- User g117 upvotes thread 10
('g127', 10), -- User g127 upvotes thread 10

-- Unit testing best practices for Node.js (thread_id 11)
('g101', 11), -- User g101 upvotes thread 11
('g103', 11), -- User g103 upvotes thread 11
('g105', 11), -- User g105 upvotes thread 11
('g115', 11), -- User g115 upvotes thread 11
('g117', 11), -- User g117 upvotes thread 11
('g123', 11), -- User g123 upvotes thread 11
('g125', 11), -- User g125 upvotes thread 11
('g129', 11), -- User g129 upvotes thread 11

-- Kubernetes resource allocation strategy (thread_id 12)
('g101', 12), -- User g101 upvotes thread 12
('g105', 12), -- User g105 upvotes thread 12
('g109', 12), -- User g109 upvotes thread 12
('g113', 12), -- User g113 upvotes thread 12
('g119', 12), -- User g119 upvotes thread 12
('g127', 12), -- User g127 upvotes thread 12

-- Authentication strategies for microservices (thread_id 13)
('g101', 13), -- User g101 upvotes thread 13
('g103', 13), -- User g103 upvotes thread 13
('g107', 13), -- User g107 upvotes thread 13
('g109', 13), -- User g109 upvotes thread 13
('g111', 13), -- User g111 upvotes thread 13
('g123', 13), -- User g123 upvotes thread 13
('g125', 13), -- User g125 upvotes thread 13

-- Flutter state management comparison (thread_id 14)
('g103', 14), -- User g103 upvotes thread 14
('g105', 14), -- User g105 upvotes thread 14
('g107', 14), -- User g107 upvotes thread 14
('g109', 14), -- User g109 upvotes thread 14
('g111', 14), -- User g111 upvotes thread 14
('g115', 14), -- User g115 upvotes thread 14
('g121', 14), -- User g121 upvotes thread 14
('g127', 14), -- User g127 upvotes thread 14

-- GraphQL vs REST API design decisions (thread_id 15)
('g103', 15), -- User g103 upvotes thread 15
('g107', 15), -- User g107 upvotes thread 15
('g109', 15), -- User g109 upvotes thread 15
('g111', 15), -- User g111 upvotes thread 15
('g113', 15), -- User g113 upvotes thread 15
('g117', 15), -- User g117 upvotes thread 15
('g121', 15), -- User g121 upvotes thread 15
('g123', 15), -- User g123 upvotes thread 15

-- Machine learning model deployment strategies (thread_id 16)
('g101', 16), -- User g101 upvotes thread 16
('g105', 16), -- User g105 upvotes thread 16
('g111', 16), -- User g111 upvotes thread 16
('g113', 16), -- User g113 upvotes thread 16
('g115', 16), -- User g115 upvotes thread 16
('g119', 16), -- User g119 upvotes thread 16
('g125', 16), -- User g125 upvotes thread 16
('g129', 16), -- User g129 upvotes thread 16

-- Ruby on Rails N+1 query problems (thread_id 17)
('g101', 17), -- User g101 upvotes thread 17
('g103', 17), -- User g103 upvotes thread 17
('g105', 17), -- User g105 upvotes thread 17
('g111', 17), -- User g111 upvotes thread 17
('g117', 17), -- User g117 upvotes thread 17
('g123', 17), -- User g123 upvotes thread 17

-- Serverless architecture limitations (thread_id 18)
('g101', 18), -- User g101 upvotes thread 18
('g105', 18), -- User g105 upvotes thread 18
('g107', 18), -- User g107 upvotes thread 18
('g115', 18), -- User g115 upvotes thread 18
('g117', 18), -- User g117 upvotes thread 18
('g121', 18), -- User g121 upvotes thread 18
('g123', 18), -- User g123 upvotes thread 18
('g125', 18), -- User g125 upvotes thread 18

-- Effective logging strategies for distributed systems (thread_id 19)
('g101', 19), -- User g101 upvotes thread 19
('g107', 19), -- User g107 upvotes thread 19
('g109', 19), -- User g109 upvotes thread 19
('g113', 19), -- User g113 upvotes thread 19
('g117', 19), -- User g117 upvotes thread 19
('g121', 19), -- User g121 upvotes thread 19
('g123', 19), -- User g123 upvotes thread 19
('g129', 19), -- User g129 upvotes thread 19

-- TypeScript type system advanced features (thread_id 20)
('g101', 20), -- User g101 upvotes thread 20
('g103', 20), -- User g103 upvotes thread 20
('g107', 20), -- User g107 upvotes thread 20
('g109', 20), -- User g109 upvotes thread 20
('g113', 20), -- User g113 upvotes thread 20
('g121', 20), -- User g121 upvotes thread 20
('g125', 20), -- User g125 upvotes thread 20
('g127', 20); -- User g127 upvotes thread 20