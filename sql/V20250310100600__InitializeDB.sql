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
  "created_at" timestamp NOT NULL DEFAULT (now()),
  "closed_at" timestamp DEFAULT null
);

CREATE TABLE "comments" (
  "comment_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "thread_id" int NOT NULL,
  "content" text NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "replies" (
  "reply_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "comment_id" int NOT NULL,
  "content" text NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "comment_upvotes" (
  "comment_upvote_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "comment_id" int NOT NULL
);

CREATE TABLE "thread_upvotes" (
  "thread_upvote_id" serial PRIMARY KEY,
  "user_google_id" varchar(30) NOT NULL,
  "thread_id" int NOT NULL
);

ALTER TABLE "threads" ADD FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id");

ALTER TABLE "comments" ADD FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id");

ALTER TABLE "comments" ADD FOREIGN KEY ("thread_id") REFERENCES "threads" ("thread_id");

ALTER TABLE "replies" ADD FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id");

ALTER TABLE "replies" ADD FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id");

ALTER TABLE "comment_upvotes" ADD FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id");

ALTER TABLE "comment_upvotes" ADD FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id");

ALTER TABLE "thread_upvotes" ADD FOREIGN KEY ("user_google_id") REFERENCES "users" ("user_google_id");

ALTER TABLE "thread_upvotes" ADD FOREIGN KEY ("thread_id") REFERENCES "threads" ("thread_id");
