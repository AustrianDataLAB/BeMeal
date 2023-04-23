DELETE FROM League_Participants;
DELETE FROM Participant;
DELETE FROM Platform_User;
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (555, 'John@gmail.com', 'John', '243261243130244537706a65396c31666d496e39516f4e57336f6f4d65687178754d78595a352e4343324339327866725554495246414b2e47654a75', false);
INSERT INTO Participant (participant_id, postal_code, region, registered, wins) VALUES (555, '1010', 8, '2022-05-26 05:04:49', 52);
