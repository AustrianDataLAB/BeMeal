/*      Create users        */
-- Admin accounts
-- Passwords are encrypted matriculation IDs
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (1, 'benjamin.probst@bemeal.at', 'Bini', '$2a$12$nsiO.0j8gKPbbLssvXxR5.F6NuIX1BAb5Ll14LH.6MJHjxKuVvI/O', true);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (2, 'dave.pfliegler@bemeal.at', 'Dave', '$2a$12$37Oc6dzwqWXR2Jn1mkCs.uLOPkzpBUdj8M0bHtwYybBwSG.7Gj/OG', true);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (3, 'dennis.toth@bemeal.at', 'Dennis', '$2a$10$DFYTkpABc.pTpJWCYMBvW.Wijxwl3lr67G6I5r5Pzcr.5/p2V4xwm', true);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (4, 'manuel.waibel@bemeal.at', 'Manu', '$2a$10$TOPbeItKkxyC2SAIw6DXN.y725xGXG5Rxs8ZG6JaaZU..gDCU.DWy', true);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (5, 'matteo.kofler@bemeal.at', 'Matteo', '$2a$10$p61cLtBQ3h3vwXy2GB9c.OJPX9ae3qp8SLNNmCxt0mZXPd.rWOicG', true);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (6, 'richard.stoeckl@bemeal.at', 'Richi', '$2a$10$diB3.0ugwJk.7wdeAY0PBOU90Ukw4MQtOAvbMxnO.tsE7mtRsLv5C', true);
-- Show user accounts
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (7, 'test1@gmail.com', 'test1', '$2a$10$qlE9fTpRBEsw1nBpFXvcPOPafSnlKKIfSNHMceKizTGdckSU1qbJi', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (8, 'test2@gmail.com', 'test2', '$2a$10$2CUt/N6l99GmxWmfBBXnDOTMueJDFOhKUdQDUyk7NQpxX4gmDVPpO', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (9, 'test3@gmail.com', 'test3', '$2a$10$sx/9lMzt/9s9QG.NvtvHf.JbTdAqmRahMEjuNcAqOp47nO6uH.v0G', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (10, 'test4@gmail.com', 'test4', '$2a$10$3QSmui3.ul8jAWpXRzKw4eqSrO7822dM5qIecTFOYG4GHwXfHSNuO', false);

/*      Create show user Participants     */
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (7, 1100, 1, '2021-01-22 16:38:54', 66);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (8, 6890, 6, '2021-08-21 18:42:41', 28);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (9, 2100, 4, '2021-07-02 21:32:05', 75);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (10, 5020, 6, '2020-05-20 23:14:37', 89);


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
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (1, 2, 0, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (2, 2, 1, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (3, 2, 2, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (4, 2, 3, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (5, 2, 4, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (6, 2, 5, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (7, 2, 6, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (8, 2, 7, 7);
INSERT INTO League (id, game_mode, region, challenge_duration) VALUES (9, 2, 8, 7);

/*      Create show user league entries     */
INSERT INTO League_Participants (league_id, participants_participant_id) VALUES (1, 7);
INSERT INTO League_Participants (league_id, participants_participant_id) VALUES (9, 8);
INSERT INTO League_Participants (league_id, participants_participant_id) VALUES (2, 9);
INSERT INTO League_Participants (league_id, participants_participant_id) VALUES (6, 10);

/*      Create show Challenges      */
-- ToDo: Beware of placeholder UUID
INSERT INTO Challenge (id, description, start_date, end_date, recipe, league_id) VALUES (1, 'Weekly Challenge for Vienna', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '2cac1d9a-c513-4daa-94a7-c11d6731ac51', 1);
INSERT INTO League_Challenges (league_id, challenges_id) VALUES (1, 1);

/*      Create Submission      */
-- ToDo: Beware of placeholder UUID
INSERT INTO Submission (id, date, picture, challenge_id, participant_participant_id) VALUES (1, DATEADD(day, 2, CURRENT_TIMESTAMP), '9908fe2f-3b78-4c0c-a8e0-34abff36e95a', 1, 7);

/*      Create Upvotes      */
INSERT INTO Submission_up_votes (votes_id, up_votes_participant_id) VALUES (1, 8);
--INSERT INTO Submission_up_votes (votes_id, up_votes_participant_id) VALUES (1, 25);
--INSERT INTO Submission_up_votes (votes_id, up_votes_participant_id) VALUES (1, 34);

-- ToDo: PlatformUser-Leagues vs. League-Participants
