package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.stances.ProtectiveStance;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Grab extends CustomCard {
	private static final String RAW_ID = "Grab";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 0;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	public Grab() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

		baseDamage = POWER;
	}

	@Override
	public void triggerOnGlowCheck() {
		if (CopycatModMain.strengthThreshold()) {
			glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
		} else {
			glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		}
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				if (CopycatModMain.strengthThreshold()) {
					addToTop(new ChangeStanceAction(new ProtectiveStance()));
				}
				isDone = true;
			}
		});
	}

	@Override
	public AbstractCard makeCopy() {
		return new Grab();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}
}
