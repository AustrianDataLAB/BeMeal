-- Platform users
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (555, 'John@gmail.com', 'John', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (666, 'John2@gmail.com', 'John2', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (777, 'John3@gmail.com', 'John3', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);

-- Participants
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (555, '1010', 8, '2022-05-26 05:04:49', 52);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (666, '1010', 8, '2022-05-26 05:04:49', 40);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (777, '1010', 8, '2022-05-26 05:04:49', 52);

-- Leagues
INSERT INTO League (id, game_mode, region, challenge_duration, hidden_identifier, name) VALUES (1, 2, 8, 7, 'ef2133ad-f5e9-4aac-bc1f-46dcf62f495e', 'League1');
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 555);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 666);
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (1, 777);
