package TheCopycat.actions;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class LectureAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("LectureAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	private AbstractPlayer p;
	private AbstractMonster m;
	private ArrayList<AbstractCard> nonMonsterCards = new ArrayList<>();

	public LectureAction(AbstractMonster m) {
		actionType = ActionType.CARD_MANIPULATION;
		p = AbstractDungeon.player;
		duration = Settings.ACTION_DUR_FAST;
		this.m = m;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			ArrayList<AbstractCard> monsterCards = new ArrayList<>();
			for (AbstractCard c : p.hand.group) {
				if (c instanceof AbstractMonsterCard) {
					monsterCards.add(c);
				} else {
					nonMonsterCards.add(c);
				}
			}

			if (monsterCards.isEmpty()) {
				isDone = true;
				return;
			} else if (monsterCards.size() == 1) {
				doAction(monsterCards.get(0));
				isDone = true;
				return;
			}

			p.hand.group = monsterCards;
			AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false, false, false);
			tickDuration();
			return;
		}

		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
				p.hand.addToTop(c);
				doAction(c);
				c.applyPowers();
			}

			returnCards();
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
			isDone = true;
		}

		tickDuration();
	}

	private void doAction(AbstractCard c) {
		AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(c, m, c.energyOnUse, true, true), true);
		if (c instanceof AbstractMonsterCard) {
			addToTop(new ManipulateIntentAction(m, (AbstractMonsterCard) c, false));
		}
	}

	private void returnCards() {
		p.hand.group.addAll(nonMonsterCards);
		p.hand.refreshHandLayout();
	}
}
