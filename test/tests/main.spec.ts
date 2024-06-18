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

        await expect(page.getByTestId("league-name-0")).toBeVisible();

        const gameMode = page.getByTestId('league-gamemode-0');
        const challengeDuration = page.getByTestId('league-challenge-duration-0');
        await expect(gameMode).toHaveText("Gamemode: Picture Ingredients");
        await expect(challengeDuration).toHaveText("Challenge duration: 7 days");
    });
})
