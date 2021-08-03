package TheCopycat.actions;

import TheCopycat.CopycatModMain;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class UnstablePickupAction extends AbstractGameAction {
	public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("UnstablePickupAction")).TEXT;
	AbstractPlayer p;

	public UnstablePickupAction() {
		actionType = ActionType.CARD_MANIPULATION;
		duration = startDuration = Settings.ACTION_DUR_FAST;
		p = AbstractDungeon.player;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			if (p.discardPile.size() <= 2) {
				ArrayList<AbstractCard> cardsToMove = new ArrayList<>(p.discardPile.group);

				doAction(cardsToMove, false);
				isDone = true;
			} else {
				AbstractDungeon.gridSelectScreen.open(p.discardPile, 2, TEXT[0], false);

				tickDuration();
			}
		} else {
			if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
				doAction(AbstractDungeon.gridSelectScreen.selectedCards, true);

				for (AbstractCard c : p.discardPile.group) {
					c.target_y = 0.0F;
					c.unhover();
					c.target_x = (float) CardGroup.DISCARD_PILE_X;
				}

				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				AbstractDungeon.player.hand.refreshHandLayout();
			}

			tickDuration();
			if (isDone) {
				for (AbstractCard c : p.hand.group) {
					c.applyPowers();
				}
			}
		}
	}

	void doAction(ArrayList<AbstractCard> list, boolean manaulSelect) {
		boolean topdeck = list.size() >= 2 && AbstractDungeon.cardRandomRng.randomBoolean();

		for (AbstractCard c : list) {
			if (topdeck) {
				p.discardPile.removeCard(c);
				p.discardPile.moveToDeck(c, false);
			} else {
				if (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
					p.hand.addToHand(c);

					p.discardPile.removeCard(c);
				}

				c.lighten(false);
				if (manaulSelect) {
					c.unhover();
				}
				c.applyPowers();
			}
			topdeck = !topdeck;
		}
	}
}
