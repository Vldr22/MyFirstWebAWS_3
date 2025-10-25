--
-- PostgreSQL database dump
--

\restrict 33nx2aG5QS8IGp9a3QfGB3C7wkulzT9DreYeMY1SC1m40BlYQGUBdTMaB8aB57C

-- Dumped from database version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.files (id, size, name, type, file_path) FROM stdin;
5	2829970	ubuntu.png	image/png	https://first-aws-bucket.storage.yandexcloud.net/ubuntu.png
6	5459555	road.png	image/png	https://first-aws-bucket.storage.yandexcloud.net/road.png
7	1079463	java dark.png	image/png	https://first-aws-bucket.storage.yandexcloud.net/java%20dark.png
8	5206944	java black.png	image/png	https://first-aws-bucket.storage.yandexcloud.net/java%20black.png
9	105097	Снимок экрана от 2025-09-18 08-44-53.png	image/png	https://first-aws-bucket.storage.yandexcloud.net/%D0%A1%D0%BD%D0%B8%D0%BC%D0%BE%D0%BA%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0%20%D0%BE%D1%82%202025-09-18%2008-44-53.png
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (id, name) FROM stdin;
1	ROLE_USER
2	ROLE_ADMIN
3	ROLE_USER_ADDED
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, password, username) FROM stdin;
1	$2a$10$Vnq6dgah9re0qsfbFFQo8.ef1v3HdGTy36.uHsNCqctyoD1NCTGWS	Vlad
2	$2a$10$gdWYvQJDWRp108Q5oJabaeqA3nkrc80hNOq1rmkXpLes.FRqIrNM2	Conan
3	$2a$10$H9nQT/G3AWW7smWt4gJijuzIF8.GcDUAj97cmZX9tGFw.ej6g/hK2	Sara
4	$2a$10$tjtLWQ2a.KqIr2z7d8aljuVJ6DSR4vWLGMIT..ODOz9qY8J9xbJPy	Carl
\.


--
-- Data for Name: users_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users_roles (roles_id, user_id) FROM stdin;
2	1
1	2
3	3
3	4
\.


--
-- Name: files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.files_id_seq', 9, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 4, true);


--
-- PostgreSQL database dump complete
--

\unrestrict 33nx2aG5QS8IGp9a3QfGB3C7wkulzT9DreYeMY1SC1m40BlYQGUBdTMaB8aB57C

