package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.DualImageCard;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.patches.DualImageCardPatch;
import TheCopycat.utils.GameLogicUtils;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.SearingBlowEffect;

public class SeesawBlow extends CustomCard implements DualImageCard {
	private static final String RAW_ID = "SeesawBlow";
	private static final String RAW_ID2 = "SeesawBlow2";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	public static final String IMG_UPGRADE = CopycatModMain.GetCardPath(RAW_ID2);
	public static final String IMG_UPGRADE_PORTRAIT = CopycatModMain.GetCardPath(RAW_ID2 + "_p");
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 6;
	private static final int UPGRADE_BONUS = 4;
	private static final int UPGRADE_BONUS2 = 3;
	private static final int DEBUFF = 2;
	private static final int UPGRADE_DEBUFF = 1;

	public SeesawBlow() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseDamage = POWER;
		baseMagicNumber = magicNumber = DEBUFF;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (m != null) {
			addToBot(new VFXAction(new SearingBlowEffect(m.hb.cX, m.hb.cY, timesUpgraded), 0.15F));
		}
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
		if (m != null && GameLogicUtils.checkIntent(m, 1)) {
			if (timesUpgraded % 2 == 0) {
				addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false)));
			} else {
				addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false)));
			}
		}
	}

	public void triggerOnGlowCheck() {
		glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDeadOrEscaped() && GameLogicUtils.checkIntent(m, 1)) {
				glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
				break;
			}
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new SeesawBlow();
	}

	public boolean canUpgrade() {
		return true;
	}

	void updateText() {
		if (timesUpgraded % 2 == 0) {
			rawDescription = DESCRIPTION;
			textureImg = IMG;
		} else {
			rawDescription = cardStrings.UPGRADE_DESCRIPTION;
			textureImg = IMG_UPGRADE;
		}
		initializeDescription();
		loadCardImage(textureImg);
	}

	@Override
	public void upgrade() {
		++timesUpgraded;
		upgraded = true;
		name = cardStrings.NAME + "+" + timesUpgraded;
		initializeTitle();
		if (timesUpgraded % 2 == 0) {
			upgradeMagicNumber(UPGRADE_DEBUFF);
			upgradeDamage(UPGRADE_BONUS2);
		} else {
			upgradeDamage(UPGRADE_BONUS);
		}
		updateText();
	}

	@Override
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard c = super.makeStatEquivalentCopy();
		if (c instanceof SeesawBlow) {
			((SeesawBlow) c).updateText();
			c.upgradedMagicNumber = false;
		}
		return c;
	}

	@Override
	protected Texture getPortraitImage() {
		if (viewUpgradedImage()) {
			DualImageCardPatch.portraitUpgraded = true;
			try {
				return ImageMaster.loadImage(IMG_UPGRADE_PORTRAIT);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			DualImageCardPatch.portraitUpgraded = false;
			return super.getPortraitImage();
		}
	}

	@Override
	public boolean viewUpgradedImage() {
		return timesUpgraded % 2 == 1;
	}
}
