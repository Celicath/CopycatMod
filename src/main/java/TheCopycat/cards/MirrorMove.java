package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MirrorMove extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "MirrorMove";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;
	private static final int NEW_COST = 0;

	public MirrorMove() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		exhaust = true;

		tags.add(CharacterEnum.CustomTags.COPYCAT_MIMIC);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractCard origCard = CopycatModMain.getEnemyLastMoveCard(m);
		AbstractCard c = origCard.makeStatEquivalentCopy();
		c.calculateCardDamage(m);
		c.current_x = origCard.current_x;
		c.current_y = origCard.current_y;
		c.target_x = (float) Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
		c.target_y = (float) Settings.HEIGHT / 2.0F;
		c.targetAngle = 0.0F;
		c.drawScale = 0.75F;
		c.targetDrawScale = 0.75F;
		p.limbo.addToBottom(c);
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				c.purgeOnUse = true;

				addToTop(new NewQueueCardAction(c, m, false, true));
				addToTop(new UnlimboAction(c));
				addToTop(new WaitAction(Settings.FAST_MODE ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_MED));
				isDone = true;
			}
		});
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		CopycatModMain.getEnemyLastMoveCard(m).calculateCardDamage(m);
	}

	@Override
	public AbstractCard makeCopy() {
		return new MirrorMove();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeBaseCost(NEW_COST);
		}
	}
}
