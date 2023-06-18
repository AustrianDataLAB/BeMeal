/*      Create users        */
-- Admin accounts
-- Passwords are encrypted matriculation IDs
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('benjamin.probst@bemeal.at', 'Bini', '$2a$12$nsiO.0j8gKPbbLssvXxR5.F6NuIX1BAb5Ll14LH.6MJHjxKuVvI/O', true);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('dave.pfliegler@bemeal.at', 'Dave', '$2a$12$37Oc6dzwqWXR2Jn1mkCs.uLOPkzpBUdj8M0bHtwYybBwSG.7Gj/OG', true);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('dennis.toth@bemeal.at', 'Dennis', '$2a$10$DFYTkpABc.pTpJWCYMBvW.Wijxwl3lr67G6I5r5Pzcr.5/p2V4xwm', true);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('manuel.waibel@bemeal.at', 'Manu', '$2a$10$TOPbeItKkxyC2SAIw6DXN.y725xGXG5Rxs8ZG6JaaZU..gDCU.DWy', true);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('matteo.kofler@bemeal.at', 'Matteo', '$2a$10$p61cLtBQ3h3vwXy2GB9c.OJPX9ae3qp8SLNNmCxt0mZXPd.rWOicG', true);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('richard.stoeckl@bemeal.at', 'Richi', '$2a$10$diB3.0ugwJk.7wdeAY0PBOU90Ukw4MQtOAvbMxnO.tsE7mtRsLv5C', true);
-- Show user accounts
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('test1@gmail.com', 'test1', '$2a$10$qlE9fTpRBEsw1nBpFXvcPOPafSnlKKIfSNHMceKizTGdckSU1qbJi', false);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('test2@gmail.com', 'test2', '$2a$10$2CUt/N6l99GmxWmfBBXnDOTMueJDFOhKUdQDUyk7NQpxX4gmDVPpO', false);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('test3@gmail.com', 'test3', '$2a$10$sx/9lMzt/9s9QG.NvtvHf.JbTdAqmRahMEjuNcAqOp47nO6uH.v0G', false);
INSERT INTO Platform_User (email, username, password, is_admin) VALUES ('test4@gmail.com', 'test4', '$2a$10$3QSmui3.ul8jAWpXRzKw4eqSrO7822dM5qIecTFOYG4GHwXfHSNuO', false);

/*      Create show user Participants     */
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (7, 1100, 1, '2021-01-22 16:38:54', 66);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (8, 6890, 6, '2021-08-21 18:42:41', 28);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (9, 2100, 4, '2021-07-02 21:32:05', 75);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (10, 5020, 6, '2020-05-20 23:14:37', 89);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (1, 2301, 1, '2020-05-20 23:14:37', 5);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (2, 2401, 1, '2020-05-20 23:14:37', 6);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (3, 3804, 1, '2020-05-20 23:14:37', 7);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (4, 6890, 8, '2020-05-20 23:14:37', 8);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (5, 6850, 8, '2020-05-20 23:14:37', 9);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (6, 1010, 1, '2020-05-20 23:14:37', 10);


/*      Create default Leagues      */
/*
Gamemodes according to enum ordinal:
PICTURE                 0
INGREDIENTS             1
PICTURE_INGREDIENTS     2

Regions according to enum ordinal:
VIENNA          0
LOWER_AUSTRIA   1
UPPER_AUSTRIA   2
STYRIA          3
TYROL           4
SALZBURG        5
CARINTHIA       6
BURGENLAND      7
VORARLBERG      8

*/
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 0, 7, 'ef2133ad-f5e9-4aac-bc1f-46dcf62f495e', 'Uni friends');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 1, 7, 'df3fe84b-6143-4d41-a0b9-a452c8c91b18', 'Work friends');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 2, 7, 'd964c968-8078-40c7-b528-bb305ec7290a', 'The legends');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 3, 7, '8b7edb62-a5db-4c65-9adb-c3a7d2fc9952', 'Cooking Experts');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 4, 7, '300fdfc3-ebd1-4a5a-bb56-fbad47ae91ce', 'Broccoli lovers');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 5, 7, '8fe94524-e052-49f8-a55d-3f4af99317bb', 'Heast, oida' );
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 6, 7, '645379dd-3b61-4635-817d-1ee9808e20b0', 'Foodies');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 7, 7, '64188f31-9fc5-478f-9342-4ccd61cc0920', 'True legends');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 8, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Turtles');

-- game mode for regional leagues always picture_ingredients:
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 0, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Vienna League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 1, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Lower Austria League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 2, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Upper Austria League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 3, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Styria League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 4, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Tyrol League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 5, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Salzburg League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 6, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Carinthia League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 7, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Burgenland League');
INSERT INTO League (game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 8, 7, 'd9e7c7c7-0eab-42f6-a09b-475a2bf08f66', 'Vorarlberg League');

-- team member users participate in their regional leagues
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (11, 1);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (11, 2);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (11, 3);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (18, 4);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (18, 5);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (11, 6);

/*      Create show user league entries     */
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 7);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (9, 8);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 9);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (6, 10);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 1);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 2);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 3);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 4);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 5);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 6);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (3, 1);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (3, 2);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (3, 3);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (4, 4);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (4, 5);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (4, 6);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (5, 1);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (5, 2);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (5, 3);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (6, 4);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (6, 5);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (6, 6);

/*      Create show Challenges      */
-- ToDo: Beware of placeholder UUID
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Uni friends', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '4138721', 1);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Work friends', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5834601', 2);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for The legends', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5766986', 3);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Cooking Experts', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5728361', 4);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Broccoli lovers', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '7834996', 5);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Heast, oida', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5854161', 6);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Foodies', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '6363046', 7);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for True legends', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '101879', 8);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Turtles', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '95471', 9);

INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Vienna League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '4138721', 10);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Lower Austria League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5834601', 11);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Upper Austria League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5766986', 12);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Styria League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5728361', 13);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Tyrol League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '7834996', 14);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Salzburg League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '5854161', 15);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Carinthia League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '6363046', 16);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Burgenland League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '101879', 17);
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Vorarlberg League', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '95471', 18);
--INSERT INTO League_Challenges (league_id, challenges_id) VALUES (1, 1);



/*      Create Submission      */
-- ToDo: Beware of placeholder UUID
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), '518cf81f-0ff6-4999-b160-756e0dfeac0a', 1, 1);
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), '717c37e6-94e9-49a1-adf2-35617b91e0a6', 1, 2);
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), 'f7828b1f-9b3c-4ded-918a-c2cd8f20bfab', 1, 3);
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), '518cf81f-0ff6-4999-b160-756e0dfeac0a', 2, 4);
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), '717c37e6-94e9-49a1-adf2-35617b91e0a6', 2, 5);
INSERT INTO Submission (date, picture, challenge_id, participant_participant_id) VALUES ( DATEADD(day, 2, CURRENT_TIMESTAMP), 'f7828b1f-9b3c-4ded-918a-c2cd8f20bfab', 2, 6);
