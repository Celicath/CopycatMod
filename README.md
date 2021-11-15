# CopycatMod

This mod adds The Copycat to Slay the Spire. It's a gimmicky character mod, like [Poker Player](https://steamcommunity.com/sharedfiles/filedetails/?id=1710999325).
* Requires [FriendlyMinions](https://steamcommunity.com/workshop/filedetails/?id=1612426481) to boot.

## The Copycat

Inspired by Copycat or Blue Mage archetype in some games, the Copycat's theme is "Copying others". Mainly, she copies enemy moves and uses them herself.

Copycat has about 40 class cards. It's much less than other characters (~75 cards), but Copycat has another type of card: Monster Cards.

### Monster Card

**Monster Cards** are cards containing enemies' moves. After an enemy takes a turn, all actions during that move are recorded and become the effects of a card. For example, when Gremlin Nob uses Skull Bash (deal 6 damage and apply 2 Vulnerable), it becomes a card with the text `Deal 6 damage. Apply 2 Vulnerable.`

You can get one Monster Card from each card reward, but some Copycat's class cards let you copy or use enemy's last move during combat. For example, 2 copies of the following card are in the starting deck.

* Mimic (Basic, Skill, Cost 0)
    * Add a Monster Card containing enemy's last move into your hand. Exhaust.

#### Incompatible Effects

Damage, Block, and most buffs/debuffs work on either the player or monsters.
However, Dexterity or Frail does nothing on enemies, and some enemies add Status cards to your deck, which isn't good when copied.
Therefore, I've made the following changes to Monster Cards:
- Dexterity loss -> Vulnerable
- Frail -> 1 ~ 2 Poison
- Dazed -> Draw
- Wound -> Discard
- Slimed -> Exhaust
- Burn -> 2 Poison
- Void -> Calm

There are unique debuffs that don't work on enemies as well, such as Hex or Confusion. I've made special Monster Cards for these moves, so be sure to check them out.

#### Cost of Monster Cards

Enemy's moves vary in power, so the cost of each Monster Card should be different.
In this mod, a Monster Card's cost is automatically calculated based on how strong it is.
For example, a Monster Card that deals 8-12 damage becomes a 1-cost card.
If a Monster Card has other effects, they are first converted to damage and added up before determining the cost.
It may also get `Exhaust` if it is too strong.
Because the cost is determined in range, each Monster Card can be weaker or stronger than average.
This is one characteristic of Monster Cards.

### Friendly Minions
Some enemies summon other monsters. What will happen if you copy these moves? You will summon monsters that fight for you!

This mod uses [FriendlyMinions](https://steamcommunity.com/workshop/filedetails/?id=1612426481) to summon monsters.
The Copycat can summon up to 4 minions, each of them having 25% chance to be targeted by each attacking enemy.
If you have 4 minions summoned, you won't be attacked! ...until one of them dies.

Copycat's minions move at the end of your turn. Some of them are copies of enemies, and they will use the move of the original enemy.

There are some stuff interact with Minions:
* Protective (Stance)
    * While in this Stance, all enemies target you. When you exit this Stance, all attacking enemies targeting you change their target.
* Beat Up (Common, Attack, Cost 1)
    * ALL allies attack the enemy for 7 damage.
* Spotlight (Rare, Skill, Cost 1)
    * Choose an ally to gain 7 Block. ALL attacking enemies change their target to the chosen ally.

You can protect your Minions for a big Beat Up attack, or sacrifice one of your Minion if you are in danger.
The targeting system is luck-based, which makes it not the greatest system, but hey, at least there are some tools to control it.

#### "Better Friendly Minions"
FriendlyMinions is a nice library that enables minions, but there are many problems as well.
* When an enemy attacks your minion, the damage animation will be displayed on the player. There's also weird delay between the animation and minion taking damage.
* If an enemy intends to attack your minion, damage from Thorns is redirected as well.
* When enemies apply a debuff to your minion, it is applied to the player instead.
* If a minion dies during enemy turn, all enemies targeting that minion will still attack it, wasting their attack.
* Enemy choosing which minion to attack is not seeded.
* Custom attack icon is sometimes not displayed.
* Minions are invisible in event combats.
* Enemies fail to damage anyone in the following scenario.
  * Enemy is using `setMove` instead of `RollMoveAction` to set their next move
  * Enemy is attacking 2 turns in a row
  * Of those 2 turns, enemy attacks your minion on the 1st turn and the player on the 2nd turn

I decided to fix these problems for this mod. The fix is codenamed "Better Friendly Minions", and I might convert this into a standalone mod or as an update to FriendlyMinions if there's enough demand.

Here are videos showcasing the fixes.
* Friendly Minions: https://streamable.com/cba8ux
* "Better Friendly Minions": https://streamable.com/73h6td

(Videos do not show all fixed problems)

### Class Cards
Copycat has about 40 class cards, which do not include the aforementioned Monster Cards.
Copycat's cards are wacky and complex compared to my other character mods.

#### Base Game Archetypes With A Twist
Although the theme of Copycat is copying others, simply making similar cards to base game ones is very common and not worth mentioning.
Hopefully, giving a twist to base game characters' archetypes is a cool way to implement this theme.

* Strength
    * Threshold-based. "If you have 6 or more Strength, do X."
* Poison
    * Poison cards naturally synergize with each other, but a [few](https://user-images.githubusercontent.com/1008668/128121698-94a31087-3e27-47c0-bff7-7f1f1f37491c.png) [cards](https://user-images.githubusercontent.com/1008668/128121778-dd5b51db-7152-4645-a4ef-35abd2dd492c.png) will make your deck different.
* Stance
    * Unlike Watcher's Stance, where you are supposed to switch a lot and avoid being in Wrath at the end of your turn, Copycat's Protection Stance emphasizes more on finding perfect timing to switch.

#### Others
There are [hybrid cards](https://user-images.githubusercontent.com/1008668/128122014-4b1bf1f8-0295-4447-87f9-41e7ee9dc149.png), ["steal" cards](https://user-images.githubusercontent.com/1008668/128122062-e14d2d82-270c-4ae0-9b36-784516b1e777.png), or cards that care about [enemy's intention.](https://user-images.githubusercontent.com/1008668/128122125-3c1f5a22-f12a-4223-bb87-181d2662c0fc.png)

There are also cards that you may think of as a meme. You will either laugh at them or feel smart by using them adequately.

## Notice
* If you enable this mod, it will save monster card images, even if you are not playing with the Copycat.
    * It will take about 15MB of disk space for all base game acts, and more if you have modded acts. If you want to check those files, they are in `%LOCALAPPDATA%\ModTheSpire\CopycatMod` on Windows.
    * You might encounter lag when the game tries to save these images. It happens once for each unique enemy move.
