-- Platform users
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (1, 'maria@gmail.com', 'Maria', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (2, 'maria2@gmail.com', 'Maria2', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (3, 'maria3@gmail.com', 'Maria3', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (4, 'jose@gmail.com', 'Jose', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (5, 'jose2@gmail.com', 'Jose2', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (6, 'jose3@gmail.com', 'Jose3', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (7, 'arnold@gmail.com', 'Arnold', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (27, 'legend27@gmail.com', 'TheLegend27', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (555, 'John@gmail.com', 'John', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (666, 'John2@gmail.com', 'John2', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (777, 'John3@gmail.com', 'John3', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);

-- Participants
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (1, '1010', 1, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (2, '1010', 1, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (3, '1010', 2, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (4, '1010', 2, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (5, '1010', 3, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (6, '1010', 3, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (7, '1010', 4, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (27, '1010', 5, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (555, '1010', 8, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (666, '1010', 8, '2022-05-26 05:04:49', 40);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (777, '1010', 8, '2022-05-26 05:04:49', 52);

-- Leagues
INSERT INTO League (id, game_mode, region, challenge_duration, hidden_identifier, name) VALUES (1, 2, 8, 7, 'ef2133ad-f5e9-4aac-bc1f-46dcf62f495e', 'League1');
INSERT INTO League (id, game_mode, region, challenge_duration, hidden_identifier, name) VALUES (2, 1, 5, 14, 'ef2133ad-f5e9-4aac-bc1f-46dcf62f495e', 'BigLeague');
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 555);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 666);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 777);

INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 1);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 2);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 3);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 4);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 5);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 6);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 7);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 27);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 555);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 666);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (2, 777);
