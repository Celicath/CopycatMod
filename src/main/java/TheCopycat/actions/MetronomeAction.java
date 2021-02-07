package TheCopycat.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import java.util.ArrayList;

public class MetronomeAction extends AbstractGameAction {
	private boolean retrieveCard = false;
	private boolean upgraded;

	public MetronomeAction(boolean upgraded) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		this.upgraded = upgraded;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			AbstractDungeon.cardRewardScreen.customCombatOpen(generateCardChoices(), CardRewardScreen.TEXT[1], false);
			this.tickDuration();
		} else {
			if (!this.retrieveCard) {
				if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
					AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();

					AbstractDungeon.player.limbo.addToBottom(disCard);
					// disCard.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
					// disCard.target_y = Settings.HEIGHT / 2.0F;

					AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
					if (m != null) {
						disCard.calculateCardDamage(m);
					}
					disCard.purgeOnUse = true;
					AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(disCard, m, disCard.energyOnUse, true, true), true);

					AbstractDungeon.cardRewardScreen.discoveryCard = null;
				}

				this.retrieveCard = true;
			}

			this.tickDuration();
		}
	}

	private ArrayList<AbstractCard> generateCardChoices() {
		ArrayList<AbstractCard> result = new ArrayList<>();

		while (result.size() != 3) {
			int roll = AbstractDungeon.cardRandomRng.random(99);
			AbstractCard.CardRarity cardRarity;
			if (roll < 55) {
				cardRarity = AbstractCard.CardRarity.COMMON;
			} else if (roll < 85) {
				cardRarity = AbstractCard.CardRarity.UNCOMMON;
			} else {
				cardRarity = AbstractCard.CardRarity.RARE;
			}

			AbstractCard tmp = CardLibrary.getAnyColorCard(cardRarity);
			if (result.stream().noneMatch(c -> c.cardID.equals(tmp.cardID))) {
				if (upgraded) tmp.upgrade();
				result.add(tmp.makeCopy());
			}
		}

		return result;
	}
}
