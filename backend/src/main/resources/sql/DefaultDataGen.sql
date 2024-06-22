/*      Create users        */
-- Admin accounts
-- Show user accounts
INSERT INTO Platform_User (id, email, username, password, is_admin) VALUES (7, 'test@test.com', 'test', '$2y$10$H5NoXd0UoOFZPEVeqFC8Ku3qfXFSD2l6zx8JPJ5AGKCkEHwIgBOru', false); /*testtest1!*/

/*      Create show user Participants     */

INSERT INTO Participant (participant_id, postal_code, region, registered) VALUES (7, 1100, 1, '2021-01-22 16:38:54');

-- game mode for regional leagues always picture_ingredients:
INSERT INTO League (id, game_mode, region, challenge_duration, hidden_identifier, name) VALUES (10, 2, 0, 7, 'ef2133ad-f5e9-4aac-bc1f-46dcf62f495e', 'Uni friends');
-- team member users participate in their regional leagues

/*      Create show user league entries     */
INSERT INTO League_Participants (leagues_id, participants_participant_id) VALUES (10, 7);

/*      Create show Challenges      */
INSERT INTO Challenge (description, start_date, end_date, recipe, league_id) VALUES ('Weekly Challenge for Uni friends', CURRENT_TIMESTAMP, DATEADD(day, 7, CURRENT_TIMESTAMP), '102452', 10);

