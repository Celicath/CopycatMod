package TheCopycat.actions;

import TheCopycat.patches.CharacterEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.colorless.Apotheosis;
import com.megacrit.cardcrawl.cards.colorless.HandOfGreed;
import com.megacrit.cardcrawl.cards.green.Nightmare;
import com.megacrit.cardcrawl.cards.green.Reflex;
import com.megacrit.cardcrawl.cards.green.Tactician;
import com.megacrit.cardcrawl.cards.red.Armaments;
import com.megacrit.cardcrawl.cards.red.DualWield;
import com.megacrit.cardcrawl.cards.red.Exhume;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetronomeAction extends AbstractGameAction {
	private boolean retrieveCard = false;
	private boolean upgraded;
	public static HashMap<AbstractCard.CardRarity, CardGroup> metronomeCardGroupMap = null;

	public MetronomeAction(boolean upgraded) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		this.upgraded = upgraded;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			AbstractDungeon.cardRewardScreen.customCombatOpen(generateCardChoices(), CardRewardScreen.TEXT[1], false);
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
					AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(disCard, m, EnergyPanel.getCurrentEnergy(), true, true), true);

					AbstractDungeon.cardRewardScreen.discoveryCard = null;
				}

				retrieveCard = true;
			}

		}
		tickDuration();
	}

	private ArrayList<AbstractCard> generateCardChoices() {
		if (metronomeCardGroupMap == null) {
			metronomeCardGroupMap = new HashMap<>();
			CardGroup commonGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			CardGroup uncommonGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
			CardGroup rareGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

			for (Map.Entry<String, AbstractCard> entry : CardLibrary.cards.entrySet()) {
				AbstractCard c = entry.getValue();
				if (c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS &&
						(!UnlockTracker.isCardLocked(entry.getKey()) || Settings.treatEverythingAsUnlocked()) &&
						(c.color == AbstractCard.CardColor.RED ||
								c.color == AbstractCard.CardColor.GREEN ||
								c.color == AbstractCard.CardColor.BLUE ||
								c.color == AbstractCard.CardColor.PURPLE ||
								c.color == AbstractCard.CardColor.COLORLESS ||
								c.color == CharacterEnum.CardColorEnum.COPYCAT_BLUE) &&
						!c.hasTag(AbstractCard.CardTags.HEALING) &&
						!(c instanceof Armaments ||
								c instanceof DualWield ||
								c instanceof Exhume ||
								c instanceof Nightmare ||
								c instanceof HandOfGreed ||
								c instanceof Apotheosis ||
								c instanceof Reflex ||
								c instanceof Tactician)
				) {
					switch (c.rarity) {
						case COMMON:
							commonGroup.addToBottom(c);
							break;
						case UNCOMMON:
							uncommonGroup.addToBottom(c);
							break;
						case RARE:
							rareGroup.addToBottom(c);
							break;
					}
				}
			}
			metronomeCardGroupMap.put(AbstractCard.CardRarity.COMMON, commonGroup);
			metronomeCardGroupMap.put(AbstractCard.CardRarity.UNCOMMON, uncommonGroup);
			metronomeCardGroupMap.put(AbstractCard.CardRarity.RARE, rareGroup);
		}

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
			CardGroup group = metronomeCardGroupMap.get(cardRarity);
			AbstractCard tmp = group.group.get(AbstractDungeon.cardRandomRng.random(group.size() - 1)).makeCopy();

			if (result.stream().noneMatch(c -> c.cardID.equals(tmp.cardID))) {
				if (upgraded) tmp.upgrade();
				result.add(tmp);
			}
		}

		return result;
	}
}
