import {expect, test} from '@playwright/test';

test.describe.serial('Leagues page test', () => {
    const url = 'http://frontend:8080';
    test('home test', async ({page}) => {
        await page.goto(url);
        await page.getByText("Get Started").click();
        await expect(page).toHaveTitle('beMeal');
    });
    test('league details test', async ({page}) => {
        await page.goto(`${url}/login`);

        await page.getByTestId('username').fill('test');
        await page.getByTestId('password').fill('testtest1!');
        await page.getByTestId("login-button").click()

        await expect(page).toHaveURL(`${url}/leagues`);

        const leagueExists = await page.$('[data-testid="league-name-0"]') !== null;
        // the code  here got bitten and might be written cleaner
        if (leagueExists) {
            const gameMode = await page.textContent('[data-testid="league-gamemode-0"]');
            const challengeDuration = await page.textContent('[data-testid="league-challenge-duration-0"]');

            if (gameMode !== 'Gamemode: Picture Ingredients' || challengeDuration !== 'Challenge duration: 7 days') {
                throw new Error('League details are incorrect. Please check the values.');
            }
        } else {
            throw new Error('The league "Uni friends" does not exist. Please check the league name.');
        }
    });
})
