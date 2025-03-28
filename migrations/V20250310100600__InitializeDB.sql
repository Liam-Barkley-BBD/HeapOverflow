CREATE TABLE "users" (
  "user_google_id" varchar(30) PRIMARY KEY,
  "user_email" varchar(255) UNIQUE NOT NULL,
  "user_name" varchar(255) NOT NULL
);

CREATE TABLE "threads" (
  "thread_id" serial PRIMARY KEY,
  "thread_title" varchar(50) NOT NULL,
  "thread_description" text NOT NULL,
  "user_google_id" varchar(30) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT now(),
  "closed_at" timestamp DEFAULT null,
  CONSTRAINT fk_threads_user FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id") ON DELETE CASCADE
);

CREATE TABLE "comments" (
  "comment_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "thread_id" int NOT NULL,
  "content" text NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT now(),
  CONSTRAINT fk_comments_user FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id") ON DELETE CASCADE,
  CONSTRAINT fk_comments_thread FOREIGN KEY ("thread_id") REFERENCES "threads" ("thread_id") ON DELETE CASCADE
);

CREATE TABLE "replies" (
  "reply_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "comment_id" int NOT NULL,
  "content" text NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT now(),
  CONSTRAINT fk_replies_user FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id") ON DELETE CASCADE,
  CONSTRAINT fk_replies_comment FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id") ON DELETE CASCADE
);

CREATE TABLE "comment_upvotes" (
  "comment_upvote_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "comment_id" int NOT NULL,
  CONSTRAINT fk_comment_upvotes_user FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id") ON DELETE CASCADE,
  CONSTRAINT fk_comment_upvotes_comment FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id") ON DELETE CASCADE
);

CREATE TABLE "thread_upvotes" (
  "thread_upvote_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "thread_id" int NOT NULL,
  CONSTRAINT fk_thread_upvotes_user FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id") ON DELETE CASCADE,
  CONSTRAINT fk_thread_upvotes_thread FOREIGN KEY ("thread_id") REFERENCES "threads" ("thread_id") ON DELETE CASCADE
);