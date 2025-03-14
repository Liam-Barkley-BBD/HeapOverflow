-- V4__Insert_Comment_Upvotes.sql
ALTER SEQUENCE public.comment_upvotes_comment_upvote_id_seq RESTART WITH 1;
INSERT INTO comment_upvotes (user_google_id, comment_id) VALUES
-- JavaScript Promise chaining thread (thread_id = 1)
('g105', 1),  -- User g105 upvoted g102's comment
('g107', 1),  -- User g107 upvoted g102's comment
('g109', 1),  -- User g109 upvoted g102's comment
('g103', 1),  -- User g103 upvoted g102's comment
('g101', 2),  -- User g101 upvoted g104's comment
('g103', 2),  -- User g103 upvoted g104's comment
('g105', 2),  -- User g105 upvoted g104's comment

-- Python decorator thread (thread_id = 2)
('g101', 3),  -- User g101 upvoted g105's comment
('g107', 3),  -- User g107 upvoted g105's comment
('g109', 3),  -- User g109 upvoted g105's comment
('g111', 3),  -- User g111 upvoted g105's comment
('g103', 4),  -- User g103 upvoted g107's comment
('g105', 4),  -- User g105 upvoted g107's comment
('g109', 4),  -- User g109 upvoted g107's comment

-- React useEffect thread (thread_id = 3)
('g103', 5),  -- User g103 upvoted g109's comment
('g105', 5),  -- User g105 upvoted g109's comment
('g107', 5),  -- User g107 upvoted g109's comment
('g113', 5),  -- User g113 upvoted g109's comment
('g115', 5),  -- User g115 upvoted g109's comment
('g101', 6),  -- User g101 upvoted g111's comment
('g103', 6),  -- User g103 upvoted g111's comment
('g105', 6),  -- User g105 upvoted g111's comment

-- SQL optimization thread (thread_id = 4)
('g101', 7),  -- User g101 upvoted g113's comment
('g105', 7),  -- User g105 upvoted g113's comment
('g111', 7),  -- User g111 upvoted g113's comment
('g117', 7),  -- User g117 upvoted g113's comment
('g103', 8),  -- User g103 upvoted g115's comment
('g107', 8),  -- User g107 upvoted g115's comment
('g113', 8),  -- User g113 upvoted g115's comment

-- Docker container networking thread (thread_id = 5)
('g101', 9),  -- User g101 upvoted g117's comment
('g103', 9),  -- User g103 upvoted g117's comment
('g113', 9),  -- User g113 upvoted g117's comment
('g121', 9),  -- User g121 upvoted g117's comment
('g105', 10), -- User g105 upvoted g119's comment
('g111', 10), -- User g111 upvoted g119's comment
('g117', 10), -- User g117 upvoted g119's comment

-- Big O notation thread (thread_id = 6)
('g101', 11), -- User g101 upvoted g121's comment
('g105', 11), -- User g105 upvoted g121's comment
('g109', 11), -- User g109 upvoted g121's comment
('g117', 11), -- User g117 upvoted g121's comment
('g125', 11), -- User g125 upvoted g121's comment
('g103', 12), -- User g103 upvoted g123's comment
('g111', 12), -- User g111 upvoted g123's comment
('g121', 12), -- User g121 upvoted g123's comment

-- Go concurrency thread (thread_id = 7)
('g101', 13), -- User g101 upvoted g125's comment
('g107', 13), -- User g107 upvoted g125's comment
('g115', 13), -- User g115 upvoted g125's comment
('g123', 13), -- User g123 upvoted g125's comment
('g103', 14), -- User g103 upvoted g127's comment
('g109', 14), -- User g109 upvoted g127's comment
('g125', 14), -- User g125 upvoted g127's comment

-- CSS Grid vs Flexbox thread (thread_id = 8)
('g103', 15), -- User g103 upvoted g129's comment
('g107', 15), -- User g107 upvoted g129's comment
('g111', 15), -- User g111 upvoted g129's comment
('g119', 15), -- User g119 upvoted g129's comment
('g127', 15), -- User g127 upvoted g129's comment
('g105', 16), -- User g105 upvoted g101's comment
('g109', 16), -- User g109 upvoted g101's comment
('g129', 16), -- User g129 upvoted g101's comment

-- Git workflow thread (thread_id = 9)
('g101', 17), -- User g101 upvoted g103's comment
('g107', 17), -- User g107 upvoted g103's comment
('g115', 17), -- User g115 upvoted g103's comment
('g123', 17), -- User g123 upvoted g103's comment
('g129', 17), -- User g129 upvoted g103's comment
('g103', 18), -- User g103 upvoted g105's comment
('g109', 18), -- User g109 upvoted g105's comment
('g121', 18), -- User g121 upvoted g105's comment

-- Memory management in C++ thread (thread_id = 10)
('g101', 19), -- User g101 upvoted g107's comment
('g103', 19), -- User g103 upvoted g107's comment
('g111', 19), -- User g111 upvoted g107's comment
('g117', 19), -- User g117 upvoted g107's comment
('g125', 19), -- User g125 upvoted g107's comment
('g105', 20), -- User g105 upvoted g109's comment
('g107', 20), -- User g107 upvoted g109's comment
('g121', 20), -- User g121 upvoted g109's comment
('g123', 20), -- User g123 upvoted g109's comment

-- Unit testing best practices thread (thread_id = 11)
('g101', 21), -- User g101 upvoted g111's comment
('g105', 21), -- User g105 upvoted g111's comment
('g117', 21), -- User g117 upvoted g111's comment
('g125', 21), -- User g125 upvoted g111's comment
('g129', 21), -- User g129 upvoted g111's comment
('g103', 22), -- User g103 upvoted g113's comment
('g111', 22), -- User g111 upvoted g113's comment
('g119', 22), -- User g119 upvoted g113's comment
('g127', 22), -- User g127 upvoted g113's comment

-- Kubernetes resource allocation thread (thread_id = 12)
('g103', 23), -- User g103 upvoted g115's comment
('g107', 23), -- User g107 upvoted g115's comment
('g113', 23), -- User g113 upvoted g115's comment
('g119', 23), -- User g119 upvoted g115's comment
('g127', 23), -- User g127 upvoted g115's comment
('g101', 24), -- User g101 upvoted g117's comment
('g109', 24), -- User g109 upvoted g117's comment
('g115', 24), -- User g115 upvoted g117's comment
('g123', 24), -- User g123 upvoted g117's comment

-- Authentication strategies thread (thread_id = 13)
('g101', 25), -- User g101 upvoted g119's comment
('g105', 25), -- User g105 upvoted g119's comment
('g113', 25), -- User g113 upvoted g119's comment
('g121', 25), -- User g121 upvoted g119's comment
('g129', 25), -- User g129 upvoted g119's comment
('g103', 26), -- User g103 upvoted g121's comment
('g111', 26), -- User g111 upvoted g121's comment
('g119', 26), -- User g119 upvoted g121's comment
('g127', 26), -- User g127 upvoted g121's comment

-- Flutter state management thread (thread_id = 14)
('g101', 27), -- User g101 upvoted g123's comment
('g107', 27), -- User g107 upvoted g123's comment
('g115', 27), -- User g115 upvoted g123's comment
('g121', 27), -- User g121 upvoted g123's comment
('g129', 27), -- User g129 upvoted g123's comment
('g103', 28), -- User g103 upvoted g125's comment
('g109', 28), -- User g109 upvoted g125's comment
('g117', 28), -- User g117 upvoted g125's comment
('g123', 28), -- User g123 upvoted g125's comment

-- GraphQL vs REST thread (thread_id = 15)
('g101', 29), -- User g101 upvoted g127's comment
('g105', 29), -- User g105 upvoted g127's comment
('g117', 29), -- User g117 upvoted g127's comment
('g123', 29), -- User g123 upvoted g127's comment
('g103', 30), -- User g103 upvoted g129's comment
('g109', 30), -- User g109 upvoted g129's comment
('g121', 30), -- User g121 upvoted g129's comment
('g127', 30), -- User g127 upvoted g129's comment

-- Machine learning model deployment thread (thread_id = 16)
('g103', 31), -- User g103 upvoted g101's comment
('g107', 31), -- User g107 upvoted g101's comment
('g115', 31), -- User g115 upvoted g101's comment
('g123', 31), -- User g123 upvoted g101's comment
('g129', 31), -- User g129 upvoted g101's comment
('g101', 32), -- User g101 upvoted g103's comment
('g111', 32), -- User g111 upvoted g103's comment
('g119', 32), -- User g119 upvoted g103's comment
('g127', 32), -- User g127 upvoted g103's comment

-- Ruby on Rails N+1 query thread (thread_id = 17)
('g101', 33), -- User g101 upvoted g105's comment
('g111', 33), -- User g111 upvoted g105's comment
('g119', 33), -- User g119 upvoted g105's comment
('g127', 33), -- User g127 upvoted g105's comment
('g105', 34), -- User g105 upvoted g107's comment
('g113', 34), -- User g113 upvoted g107's comment
('g121', 34), -- User g121 upvoted g107's comment
('g129', 34), -- User g129 upvoted g107's comment

-- Serverless architecture limitations thread (thread_id = 18)
('g101', 35), -- User g101 upvoted g109's comment
('g105', 35), -- User g105 upvoted g109's comment
('g117', 35), -- User g117 upvoted g109's comment
('g125', 35), -- User g125 upvoted g109's comment
('g103', 36), -- User g103 upvoted g111's comment
('g109', 36), -- User g109 upvoted g111's comment
('g119', 36), -- User g119 upvoted g111's comment
('g127', 36), -- User g127 upvoted g111's comment

-- Logging strategies thread (thread_id = 19)
('g101', 37), -- User g101 upvoted g113's comment
('g107', 37), -- User g107 upvoted g113's comment
('g119', 37), -- User g119 upvoted g113's comment
('g127', 37), -- User g127 upvoted g113's comment
('g105', 38), -- User g105 upvoted g115's comment
('g113', 38), -- User g113 upvoted g115's comment
('g121', 38), -- User g121 upvoted g115's comment
('g129', 38), -- User g129 upvoted g115's comment

-- TypeScript type system thread (thread_id = 20)
('g101', 39), -- User g101 upvoted g117's comment
('g105', 39), -- User g105 upvoted g117's comment
('g113', 39), -- User g113 upvoted g117's comment
('g125', 39), -- User g125 upvoted g117's comment
('g129', 39), -- User g129 upvoted g117's comment
('g103', 40), -- User g103 upvoted g119's comment
('g111', 40), -- User g111 upvoted g119's comment
('g117', 40), -- User g117 upvoted g119's comment
('g123', 40), -- User g123 upvoted g119's comment
('g127', 40); -- User g127 upvoted g119's comment
