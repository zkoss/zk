import { test, expect } from '@playwright/test';

// Guard for avatar.css `.z-avatar i` (ZK-6112, zkdemo B7 Z2): an icon avatar sizes its glyph at 60%
// of the avatar, not at the initials font size (14px in a 40px avatar). Covers the three sizes and
// the avatargroup size cascade.
// Fixture: web/pv/avatar-icon-guard.zul

const FIXTURE = '/pv/avatar-icon-guard.zul';
const RATIO = 0.6;
const TOLERANCE_PX = 0.5;

for (const [name, avatarPx] of [['small', 32], ['medium', 40], ['large', 56], ['group', 56]] as const) {
  test(`${name} icon avatar draws the icon at 60% of ${avatarPx}px`, async ({ page }) => {
    const resp = await page.goto(FIXTURE, { waitUntil: 'networkidle' });
    expect(resp?.status()).toBe(200);
    const { avatar, icon } = await page.locator(`.icon-${name}`).evaluate((el) => ({
      // clientWidth: the avatargroup child carries a 2px content-box border outside its size.
      avatar: el.clientWidth,
      icon: parseFloat(getComputedStyle(el.querySelector('i')!).fontSize),
    }));
    expect(avatar).toBe(avatarPx);
    expect(Math.abs(icon - avatarPx * RATIO), `avatar ${avatar}px, icon font-size ${icon}px`)
      .toBeLessThanOrEqual(TOLERANCE_PX);
  });
}
